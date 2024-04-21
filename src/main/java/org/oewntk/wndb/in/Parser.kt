/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.CoreModel;
import org.oewntk.model.Lex;
import org.oewntk.parse.DataParser;
import org.oewntk.parse.IndexParser;
import org.oewntk.parse.MorphParser;
import org.oewntk.parse.SenseParser;
import org.oewntk.pojos.*;
import org.oewntk.utils.Tracing;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * WNDB parser
 */
public class Parser
{
    private static final boolean LOG_TAGCOUNT_MERGE = false;

    // PRINT STREAMS

    /**
     * Info print stream
     */
    private static final PrintStream psi = !System.getProperties().containsKey("SILENT") ? Tracing.psInfo : Tracing.psNull;

    /**
     * Error print stream
     */
    private static final PrintStream pse = !System.getProperties().containsKey("SILENT") ? Tracing.psErr : Tracing.psNull;

    /**
     * Key which is to represent sense
     */
    static class Key
    {
        public final String lemma;
        public final char pos;
        public final long offset;

        public Key(final String lemma, final char pos, final long offset)
        {
            this.lemma = lemma;
            this.pos = pos;
            this.offset = offset;
        }

        @Override
        public String toString()
        {
            return "K{" + "lemma='" + lemma + '\'' + ", pos=" + pos + ", offset=" + offset + '}';
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            Key key = (Key) o;
            return pos == key.pos && offset == key.offset && lemma.equals(key.lemma);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(lemma, pos, offset);
        }
    }

    // MAPS

    // final results

    /**
     * Lexical units
     */
    private final Map<org.oewntk.model.Key.W_P, Lex> lexesByKey = new TreeMap<>();

    /**
     * Senses
     */
    private final Collection<org.oewntk.model.Sense> senses = new ArrayList<>();

    /**
     * Synsets
     */
    private final Collection<org.oewntk.model.Synset> synsets = new ArrayList<>();

    // intermediate pojos

    /**
     * Pojo Synsets by pojo SynsetId
     */
    private final Map<SynsetId, Synset> pojoSynsetsById = new HashMap<>();

    // by key

    /**
     * Sensekey by key
     */
    private final Map<? extends Key, String> sensekeyByKey = new HashMap<>();

    /**
     * Sense relations by key
     */
    private final Map<? extends Key, Relation[]> relationsByKey = new HashMap<>();

    /**
     * TagCnt by key representing sense
     */
    private final Map<Key, TagCnt> tagCntByKey = new HashMap<>();

    // 1 - C O N S U M E   S Y N S E T   P O J O S
    // from data.(noun|verb|adj|adv)

    /**
     * Synset consumer
     */
    private final Consumer<Synset> synsetConsumer = synset -> {

        String synsetId = synset.synsetId.toString();
        char type = synset.type.toChar();
        String domain = synset.domain.getName();
        var members = Arrays.stream(synset.lemmas).map(LemmaCS::toString).toArray(String[]::new);
        String[] definitions = new String[]{synset.gloss.getDefinition()};
        String[] examples = synset.gloss.getSamples();

        Map<String, Set<String>> relations = buildSynsetRelations(synset.relations);

        org.oewntk.model.Synset modelSynset = new org.oewntk.model.Synset(synsetId, type, domain, members, definitions, examples, null, relations);
        synsets.add(modelSynset);

        pojoSynsetsById.put(synset.synsetId, synset);
    };

    /**
     * Build synset relations
     *
     * @param relations relations
     * @return map type to set of target synset ids
     */
    private static Map<String, Set<String>> buildSynsetRelations(final Relation[] relations)
    {
        if (relations != null && relations.length > 0)
        {
            var map = Arrays.stream(relations) //
                    .filter(r -> !(r instanceof LexRelation)) //
                    .map(relation -> new SimpleEntry<>(relation.type.getName(), relation.toSynsetId.toString())) // (type, synsetid)
                    .collect(groupingBy(SimpleEntry::getKey, mapping(SimpleEntry::getValue, toSet()))); // type: synsetids
            return map.size() == 0 ? null : map;
        }
        return null;
    }

    // 2 - C O N S U M E   S E N S E   P O J O S
    // from index.sense

    /**
     * Sense consumer
     */
    private final Consumer<Sense> senseConsumer = sense -> {

        // sensekey
        String sensekey = sense.sensekey.toString();

        // key
        String lemma = sense.lemma.toString();
        char pos = sense.synsetId.getPos().toChar();
        Key key = new Key(lemma, pos, sense.synsetId.getOffset());

        // store sensekey by key
        sensekeyByKey.put(key, sensekey);

        // store tagcnt by key
        TagCnt tagCnt = sense.tagCnt;
        var existingTagCnt = tagCntByKey.put(key, tagCnt);
        if (existingTagCnt != null && !existingTagCnt.equals(tagCnt))
        {
            // merge
            var tagCnt2 = new TagCnt(Math.min(tagCnt.senseNum, existingTagCnt.senseNum), Math.max(tagCnt.tagCount, existingTagCnt.tagCount));
            tagCntByKey.put(key, tagCnt2);
            if (LOG_TAGCOUNT_MERGE)
            {
                psi.printf("Tag count for %s contained %s, merged to %s%n", key, existingTagCnt, tagCnt2);
            }
        }

        // store relations by key
        Synset synset = pojoSynsetsById.get(sense.synsetId);
        /* var r =*/
        relationsByKey.put(key, synset.relations);
        //assert r2 != null : r2;
    };

    // 3 - C O N S U M E   I N D E X   P O J O S
    // from index.(noun|verb|adj|adv)

    /**
     * Index consumer
     */
    private final Consumer<Index> indexConsumer = idx -> {

        String lemma = idx.lemma.toString();
        char pos = idx.pos.toChar();

        // senses
        final int[] i = {1};
        Arrays.stream(idx.senses) //
                .forEach(sense -> {

                    //psi.println("\t" + sense);

                    // pos and index
                    assert pos == sense.synsetId.getPos().toChar() : sense;
                    //assert i[0] == sense.sensePosIndex : sense;

                    // case-sensitive lemma
                    SynsetId synsetId = sense.synsetId;
                    Synset synset = pojoSynsetsById.get(synsetId);
                    LemmaCS[] members = synset.lemmas;
                    Arrays.stream(members) //
                            .filter(member -> member.toString().equalsIgnoreCase(lemma)) //
                            .forEach(member -> {

                                String memberLemma = member.toString();

                                // key
                                Key key = new Key(lemma, pos, sense.synsetId.getOffset());

                                // retrieve tag count
                                var tagCnt = tagCntByKey.get(key);

                                // retrieve relations, build sense relations
                                var relations = relationsByKey.get(key);
                                Map<String, Set<String>> senseRelations = buildSenseRelations(member.toString(), relations);

                                // retrieve sensekey
                                String sensekey = sensekeyByKey.get(key);
                                assert sensekey != null : "no sensekey for " + key;

                                // type
                                char type = pos != 'a' ? pos : (sensekey.split("%")[1].startsWith("5") ? 's' : 'a');

                                // ver frames and adj positions
                                String[] verbFrames = pos != 'v' ? null : buildVerbFrames(synset, memberLemma);
                                String adjPosition = pos != 'a' ? null : (member.lemma instanceof AdjLemma ? ((AdjLemma) member.lemma).getPosition().getId() : null);

                                // collect lex
                                org.oewntk.model.Key.W_P wpKey = org.oewntk.model.Key.W_P.from(memberLemma, type);
                                Lex lex = lexesByKey.computeIfAbsent(wpKey, k -> new org.oewntk.model.Lex(memberLemma, Character.toString(type), null));

                                // senses
                                org.oewntk.model.Sense modelSense = new org.oewntk.model.Sense(sensekey, lex, pos, i[0], sense.synsetId.toString(), null, verbFrames, adjPosition, senseRelations);
                                if (tagCnt.tagCount != 0)
                                {
                                    modelSense.setTagCount(new org.oewntk.model.TagCount(tagCnt.senseNum, tagCnt.tagCount));
                                }

                                // collect sense in lex
                                lex.addSense(modelSense);

                                // collect in senses
                                senses.add(modelSense);
                            });
                    i[0]++;
                });
    };

    /**
     * Build sense relations
     *
     * @param relations relations including synset relations
     * @return sense relations
     */
    private Map<String, Set<String>> buildSenseRelations(final String member, final Relation[] relations)
    {
        if (member.contains("_ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
        {
            throw new IllegalArgumentException(member);
        }

        if (relations != null && relations.length > 0)
        {
            var map = Arrays.stream(relations) //
                    .filter(r -> (r instanceof LexRelation)) // discard non-lexical
                    .filter(lr -> member.equalsIgnoreCase(((LexRelation) lr).getFromWord().lemma.toString())) // discard relations whose from word is not target member
                    //.peek(lr -> System.err.println(resolveToWord((LexRelation) lr)))
                    .map(relation -> new SimpleEntry<>(relation.type.getName(), toSensekey((LexRelation) relation))) // (type: sensekey)
                    .collect(groupingBy(SimpleEntry::getKey, mapping(SimpleEntry::getValue, toSet()))); // type: sensekeys
            return map.size() == 0 ? null : map;
        }
        return null;
    }

    /**
     * Resolve target sensekey of a lex relation
     *
     * @param lr lexical relation
     * @return target sensekey
     */
    private String toSensekey(final LexRelation lr)
    {
        SynsetId toSynsetId = lr.getToSynsetId();
        String toWord = resolveToWord(lr);
        Key key = new Key(toWord, toSynsetId.getPos().toChar(), toSynsetId.getOffset());
        String sensekey = sensekeyByKey.get(key);
        assert sensekey != null : "no sensekey for " + key;
        return sensekey;
    }

    /**
     * Resolve target word of a lex relation
     *
     * @param lr lexical relation
     * @return target word
     */
    private String resolveToWord(final LexRelation lr)
    {
        SynsetId toSynsetId = lr.getToSynsetId();
        Synset toSynset = pojoSynsetsById.get(toSynsetId);
        LemmaRef toWordRef = lr.getToWord();
        return toWordRef.resolve(toSynset).toString();
    }

    // name, frame, frameid
    private static final Object[][] VERBFRAME_VALUES = new Object[][]{ //
            {"vii", 1}, //
            {"via", 2}, //
            {"nonreferential", 3}, //
            {"vii-pp", 4}, //
            {"vtii-adj", 5}, //
            {"vii-adj", 6}, //
            {"via-adj", 7}, //
            {"vtai", 8}, //
            {"vtaa", 9}, //
            {"vtia", 10}, //
            {"vtii", 11}, //
            {"vii-to", 12}, //
            {"via-on-inanim", 13}, //
            {"ditransitive", 14}, //
            {"vtai-to", 15}, //
            {"vtai-from", 16}, //
            {"vtaa-with", 17}, //
            {"vtaa-of", 18}, //
            {"vtai-on", 19}, //
            {"vtaa-pp", 20}, //
            {"vtai-pp", 21}, //
            {"via-pp", 22}, //
            {"vibody", 23}, //
            {"vtaa-to-inf", 24}, //
            {"vtaa-inf", 25}, //
            {"via-that", 26}, //
            {"via-to", 27}, //
            {"via-to-inf", 28}, //
            {"via-whether-inf", 29}, //
            {"vtaa-into-ger", 30}, //
            {"vtai-with", 31}, //
            {"via-inf", 32}, //
            {"via-ger", 33}, //
            {"nonreferential-sent", 34}, //
            {"vii-inf", 35}, //
            {"via-at", 36}, //
            {"via-for", 37}, //
            {"via-on-anim", 38}, //
            {"via-out-of", 39}, //
    };

    /**
     * Map frame numeric id to id (via, ...)
     */
    private static final Map<Integer, String> VERB_FRAME_NID_TO_IDS = Stream.of(VERBFRAME_VALUES).collect(toMap(data -> (Integer) data[1], data -> (String) data[0]));

    /**
     * Build verb frame ids
     *
     * @param synset synset
     * @param lemma  lemma
     * @return array of verb frame ids
     */
    static String[] buildVerbFrames(Synset synset, String lemma)
    {
        var verbFrames = synset.getVerbFrames();
        if (verbFrames == null)
        {
            return null;
        }
        return Arrays.stream(verbFrames) //
                .filter(vbf -> Arrays.stream(vbf.lemmas) //
                        .map(Lemma::toString) //
                        .anyMatch(vfm -> vfm.equals(lemma))) //
                .map(vfr -> VERB_FRAME_NID_TO_IDS.get(vfr.frameId)) //
                .toArray(String[]::new);
    }

    // 4 - C O N S U M E   M O R P H M A P P I N G   P O J O S
    // from (noun|verb|adj|adv).exc

    /**
     * Lemma to set of morphs
     */
    private final Map<String, Map<Character, TreeSet<String>>> lemmaToMorphs = new HashMap<>();

    /**
     * Morph consumer
     */
    private final Consumer<MorphMapping> morphConsumer = mapping -> {

        char pos = mapping.pos.toChar();
        String morph = mapping.morph.toString();
        Collection<Lemma> lemmas = mapping.lemmas;
        lemmas.forEach(lemma ->  //
                lemmaToMorphs //
                        .computeIfAbsent(lemma.toString(), l -> new HashMap<>()) //
                        .computeIfAbsent(pos, p -> new TreeSet<>()) //
                        .add(morph));
    };

    /**
     * Inject morphs into model
     *
     * @param model         model
     * @param lemmaToMorphs lemma to morphs map
     */
    private void setMorphs(final CoreModel model, final Map<String, Map<Character, TreeSet<String>>> lemmaToMorphs)
    {
        var lexByLemma = model.getLexesByLemma();
        lemmaToMorphs.forEach((lemma, map2) -> //
                map2.forEach((pos, morphs) -> {

                    var lexes = lexByLemma.get(lemma);
                    if (lexes != null)
                    {
                        lexes.stream().filter(lex -> lex.getPartOfSpeech() == pos).forEach(lex -> {
                            var morphs2 = morphs.toArray(String[]::new);
                            lex.setForms(morphs2);
                        });
                    }
                }));
    }

    /**
     * WN dict directory
     */
    final File dir;

    /**
     * Constructor
     *
     * @param dir WN home dict directory
     */
    public Parser(final File dir)
    {
        this.dir = dir;
    }

    /**
     * Parse core model
     *
     * @param synsetConsumer synset consumer
     * @param senseConsumer  sense consumer
     * @param indexConsumer  index consumer
     * @param morphConsumer  morph consumer
     * @return core model
     * @throws IOException        io exception
     * @throws ParsePojoException parse pojo exception
     */
    public CoreModel parseCoreModel(final Consumer<Synset> synsetConsumer, final Consumer<Sense> senseConsumer, final Consumer<Index> indexConsumer, final Consumer<MorphMapping> morphConsumer) throws IOException, ParsePojoException
    {
        DataParser.parseAllSynsets(dir, synsetConsumer);
        SenseParser.parseSenses(dir, senseConsumer);
        IndexParser.parseAllIndexes(dir, indexConsumer);
        MorphParser.parseAllMorphs(dir, morphConsumer);

        Collection<Lex> lexes = new ArrayList<>(lexesByKey.values()); // TreeMap.Values are not serializable
        CoreModel model = new CoreModel(lexes, senses, synsets);
        setMorphs(model, lemmaToMorphs);
        return model;
    }

    /**
     * Parse core model
     *
     * @return core model
     * @throws IOException        io exception
     * @throws ParsePojoException parse pojo exception
     */
    public CoreModel parseCoreModel() throws IOException, ParsePojoException
    {
        return parseCoreModel(synsetConsumer, senseConsumer, indexConsumer, morphConsumer);
    }
}
