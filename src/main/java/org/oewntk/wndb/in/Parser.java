/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.oewntk.model.CoreModel;
import org.oewntk.model.Lex;
import org.oewntk.parse.DataParser;
import org.oewntk.parse.IndexParser;
import org.oewntk.parse.SenseParser;
import org.oewntk.parse.Utils;
import org.oewntk.pojos.AdjLemma;
import org.oewntk.pojos.Index;
import org.oewntk.pojos.Lemma;
import org.oewntk.pojos.LemmaCS;
import org.oewntk.pojos.LemmaRef;
import org.oewntk.pojos.LexRelation;
import org.oewntk.pojos.ParsePojoException;
import org.oewntk.pojos.Relation;
import org.oewntk.pojos.Sense;
import org.oewntk.pojos.Synset;
import org.oewntk.pojos.SynsetId;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Parser
{
	static final boolean FAIL_DUPLICATES = false;

	// PRINTSTREAMS

	/**
	 * Null print stream
	 */
	private static final PrintStream psnull = Utils.nullPrintStream();

	/**
	 * Info print stream
	 */
	private static final PrintStream psi = !System.getProperties().containsKey("SILENT") ? System.out : psnull;

	/**
	 * Error print stream
	 */
	private static final PrintStream pse = !System.getProperties().containsKey("SILENT") ? System.err : psnull;

	/**
	 * Key which is to represent sense
	 */
	static class Key
	{
		public final String lemma;
		public final char pos;
		public final int index;

		public Key(final String lemma, final char pos, final int index)
		{
			this.lemma = lemma;
			this.pos = pos;
			this.index = index;
		}

		@Override
		public String toString()
		{
			return "Key{" + "lemma='" + lemma + '\'' + ", pos=" + pos + ", index=" + index + '}';
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
			return pos == key.pos && index == key.index && lemma.equals(key.lemma);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(lemma, pos, index);
		}
	}

	/**
	 * Collected data
	 */
	static class Triplet<T, U, V>
	{
		public final T first;
		public final U second;
		public final V third;

		public Triplet(final T first, final U second, final V third)
		{
			this.first = first;
			this.second = second;
			this.third = third;
		}

		@Override
		public String toString()
		{
			return "Triplet{" + "first=" + first + ", second=" + second + ", third=" + third + '}';
		}
	}

	// MAPS

	// final results

	/**
	 * Lexical items mapped by lemma written form.
	 * A multimap: each value is an array of lexes for the lemma.
	 */
	private final Map<String, List<Lex>> lexesByLemma = new TreeMap<>();

	/**
	 * Senses mapped by id (sensekey)
	 */
	private final Map<String, org.oewntk.model.Sense> sensesById = new TreeMap<>();

	/**
	 * Synsets mapped by id (synset id)
	 */
	private final Map<String, org.oewntk.model.Synset> synsetsById = new TreeMap<>();

	// intermediate pojos

	/**
	 * Pojo Synsets by pojo SynsetId
	 */
	private final Map<SynsetId, Synset> pojoSynsetsById = new HashMap<>();

	// by key

	/**
	 * Sensekey by key
	 */
	private final Map<Key, String> sensekeyByKey = new HashMap<>();

	/**
	 * Sense relations by key
	 */
	private final Map<Key, Relation[]> senseRelationsByKey = new HashMap<>();

	/**
	 * Triplet (synsetId, senseIndex, tagCnt) by key
	 */
	private final Map<Key, Triplet<Long, Integer, Integer>> dataByKey = new HashMap<>();

	// C O N S U M E   S Y N S E T   P O J O S
	// from data.(noun|verb|adj|adv)

	private final Consumer<Synset> synsetConsumer = synset -> {

		String source = synset.domain.getDomain();
		String synsetId = synset.synsetId.toString();
		char type = synset.type.toChar();
		var members = Arrays.stream(synset.lemmas).map(LemmaCS::toString).toArray(String[]::new);
		String[] definitions = new String[] { synset.gloss.getDefinition() };
		String[] examples = synset.gloss.getSamples();

		Map<String, List<String>> relations = buildSynsetRelations(synset.relations);

		org.oewntk.model.Synset newS = new org.oewntk.model.Synset(source, synsetId, type, members, definitions, examples, null, relations);
		synsetsById.put(synsetId, newS);

		pojoSynsetsById.put(synset.synsetId, synset);
	};

	private static Map<String, List<String>> buildSynsetRelations(final Relation[] relations)
	{
		if (relations == null && relations.length > 0)
			return null;
		return Arrays.stream(relations) //
				.filter(r -> !(r instanceof LexRelation)) //
				.map(relation -> new SimpleEntry<>(relation.type.getName(), relation.toSynsetId.toString())) // (type, synsetid)
				.collect(groupingBy(SimpleEntry::getKey, mapping(SimpleEntry::getValue, toList()))); // type: synsetids
	}

	private Map<String, List<String>> buildSenseRelations(final Relation[] relations)
	{
		if (relations != null && relations.length > 0)
		{
			return Arrays.stream(relations) //
					.filter(r -> (r instanceof LexRelation)) //
					.map(relation -> new SimpleEntry<>(relation.type.getName(), toSensekey((LexRelation) relation))) // (type: sensekey)
					.collect(groupingBy(SimpleEntry::getKey, mapping(SimpleEntry::getValue, toList()))); // type: sensekeys
		}
		return null;
	}

	private String toSensekey(final LexRelation lr)
	{
		SynsetId toSynsetId = lr.getToSynsetId();
		Synset toSynset = pojoSynsetsById.get(toSynsetId);
		LemmaRef toWordRef = lr.getToWord();
		String toWord = toWordRef.resolve(toSynset).toString();
		Key k = new Key(toWord, toSynsetId.getPos().toChar(), toWordRef.getWordNum());
		return sensekeyByKey.get(k);
	}

	// C O N S U M E   S E N S E   P O J O S
	// from index.sense

	private final Consumer<Sense> senseConsumer = sense -> {

		// sensekey
		String sensekey = sense.sensekey.toString();

		// key
		String lemma = sense.lemma.toString();
		char pos = sense.synsetId.getPos().toChar();
		int posIndex = sense.sensePosIndex;
		Key k = new Key(lemma, pos, posIndex);

		// store sensekey by key
		sensekeyByKey.put(k, sensekey);

		// store synsetid, index, tagcnt by key
		long synsetId = sense.synsetId.getOffset();
		int senseIndex = sense.sensePosIndex;
		int tagCnt = sense.tagCnt.getTagCount();
		var triplet = new Triplet<>(synsetId, senseIndex, tagCnt);
		if (FAIL_DUPLICATES)
		{
			assert !dataByKey.containsKey(k) : k + " contains " + dataByKey.get(k) + ", new " + triplet;
			dataByKey.put(k, triplet);
		}
		else
		{
			if (dataByKey.containsKey(k))
			{
				pse.println(k + " contained " + dataByKey.get(k) + ", skipped " + triplet);
			}
			else
			{
				dataByKey.put(k, triplet);
			}
		}

		// store relations by key
		Synset synset = pojoSynsetsById.get(sense.synsetId);
		senseRelationsByKey.put(k, synset.relations);
	};

	// C O N S U M E   I N D E X   P O J O S
	// from index.(noun|verb|adj|adv)

	public static final Map<Integer, String> VERBFRAMENID2IDS = Stream.of(org.oewntk.model.VerbFrame.VALUES)
			.collect(toMap(data -> (Integer) data[2], data -> (String) data[0]));

	//private static final Consumer<Index> indexConsumer = System.out::println;
	private final Consumer<Index> indexConsumer = idx -> {

		String lemma = idx.lemma.toString();
		char pos = idx.pos.toChar();
		String source = null;
		//psi.println(lemma);

		// senses
		final int[] i = { 1 };
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
								Key k = new Key(lemma, pos, i[0]);

								// retrieve data
								var data = dataByKey.get(k);
								if (FAIL_DUPLICATES)
								{
									assert i[0] == data.second : k + " " + data.second;
									assert synsetId.getOffset() == data.first : k + " " + data.first;
								}

								// retrieve relations
								Map<String, List<String>> relations = buildSenseRelations(senseRelationsByKey.get(k));

								// retrieve sensekey
								String sensekey = sensekeyByKey.get(k);

								// type
								String type = Character.toString(pos);

								// lex with case-sensitive lemma
								Lex lex = new org.oewntk.model.Lex(null, memberLemma, type);
								lexesByLemma.computeIfAbsent(memberLemma, l -> new ArrayList<>()).add(lex);

								String[] verbFrames = pos != 'v' ? null : buildVerbFrames(synset, memberLemma);
								String adjPosition =
										pos != 'a' ? null : (member.lemma instanceof AdjLemma ? ((AdjLemma) member.lemma).getPosition().getId() : null);

								// lex senses
								org.oewntk.model.Sense modelSense = new org.oewntk.model.Sense(sensekey, lex, pos, i[0], sense.synsetId.toString(), verbFrames,
										adjPosition, relations);
								sensesById.put(sensekey, modelSense);
								lex.addSense(modelSense);
							});
					i[0]++;
				});
	};

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
				.map(vfr -> VERBFRAMENID2IDS.get(vfr.frameId)) //
				.toArray(String[]::new);
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

	public CoreModel parseAll(final Consumer<Synset> synsetConsumer, final Consumer<Sense> senseConsumer, final Consumer<Index> indexConsumer)
			throws IOException, ParsePojoException
	{
		DataParser.parseAllSynsets(dir, synsetConsumer);
		SenseParser.parseSenses(dir, senseConsumer);
		IndexParser.parseAllIndexes(dir, indexConsumer);
		psi.println();
		psi.printf("%-50s %d%n", "synsets by id", synsetsById.size());
		psi.printf("%-50s %d%n", "senses by id", sensesById.size());
		psi.printf("%-50s %d%n", "lexes by lemma", lexesByLemma.size());
		return new CoreModel(Collections.unmodifiableMap(lexesByLemma), Collections.unmodifiableMap(sensesById), Collections.unmodifiableMap(synsetsById));
	}

	public CoreModel parseAll() throws IOException, ParsePojoException
	{
		return parseAll(synsetConsumer, senseConsumer, indexConsumer);
	}

	// MAIN

	public static void main(String[] args) throws IOException, ParsePojoException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Input
		File inDir = new File(args[0]);
		new Parser(inDir).parseAll();

		// Timing
		final long endTime = System.currentTimeMillis();
		psi.println("Total execution time: " + (endTime - startTime) / 1000 + "s");
	}
}
