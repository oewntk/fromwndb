/*
 * Copyright (c) 2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.oewntk.model.*
import org.oewntk.model.Key.W_P
import org.oewntk.model.Key.W_P.Companion.from
import org.oewntk.parse.DataParser
import org.oewntk.parse.IndexParser
import org.oewntk.parse.MorphParser
import org.oewntk.parse.SenseParser
import org.oewntk.pojos.*
import org.oewntk.pojos.Sense
import org.oewntk.pojos.Synset
import org.oewntk.pojos.SynsetId
import org.oewntk.utils.Tracing
import java.io.File
import java.io.IOException
import java.io.PrintStream
import java.util.*
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.min

/**
 * WNDB parser
 *
 * @property dir WN home dict directory
 */
class Parser(
	val dir: File
) {

	/**
	 * Key which is to represent sense
	 */
	internal class Key(
		private val lemma: String,
		private val pos: Char,
		private val offset: Long
	) {
		override fun toString(): String {
			return "K{lemma='$lemma', pos=$pos, offset=$offset}"
		}

		override fun equals(other: Any?): Boolean {
			if (this === other) {
				return true
			}
			if (other == null || javaClass != other.javaClass) {
				return false
			}
			val key = other as Key
			return pos == key.pos && offset == key.offset && lemma == key.lemma
		}

		override fun hashCode(): Int {
			return Objects.hash(lemma, pos, offset)
		}
	}

	// MAPS
	// final results

	/**
	 * Lexical units
	 */
	private val lexesByKey: MutableMap<W_P, Lex> = TreeMap()

	/**
	 * Senses
	 */
	private val senses: MutableCollection<org.oewntk.model.Sense> = ArrayList()

	/**
	 * Synsets
	 */
	private val synsets: MutableCollection<org.oewntk.model.Synset> = ArrayList()

	// intermediate pojos
	/**
	 * Pojo Synsets by pojo SynsetId
	 */
	private val pojoSynsetsById: MutableMap<SynsetId, Synset> = HashMap()

	// by key
	/**
	 * Sensekey by key
	 */
	private val sensekeyByKey: MutableMap<Key, String> = HashMap()

	/**
	 * Sense relations by key
	 */
	private val relationsByKey: MutableMap<Key, Array<Relation>> = HashMap()

	/**
	 * TagCnt by key representing sense
	 */
	private val tagCntByKey: MutableMap<Key, TagCnt> = HashMap()

	// 1 - C O N S U M E   S Y N S E T   P O J O S
	// from data.(noun|verb|adj|adv)

	/**
	 * Synset consumer
	 */
	private val synsetConsumer = Consumer<Synset> { synset: Synset ->
		val synsetId = synset.id.toString()
		val type = synset.type.toChar()
		val domain = synset.domain.name
		val members = synset.cSLemmas
			.map { it.toString() }
			.toTypedArray()
		val definitions = arrayOf(synset.gloss.definition)
		val examples = synset.gloss.samples

		val relations = buildSynsetRelations(synset.relations)

		val modelSynset = org.oewntk.model.Synset(synsetId, type, domain, members, definitions, examples, null, relations)
		synsets.add(modelSynset)
		pojoSynsetsById[synset.id] = synset
	}

	// 2 - C O N S U M E   S E N S E   P O J O S
	// from index.sense

	/**
	 * Sense consumer
	 */
	private val senseConsumer = Consumer { sense: Sense ->

		// sensekey
		val sensekey = sense.sensekey.toString()

		// key
		val lemma = sense.lemma.toString()
		val pos = sense.synsetId.pos.toChar()
		val key = Key(lemma, pos, sense.synsetId.offset)

		// store sensekey by key
		sensekeyByKey[key] = sensekey

		// store tagcnt by key
		val tagCnt = sense.tagCnt
		val existingTagCnt = tagCntByKey.put(key, tagCnt)
		if (existingTagCnt != null && existingTagCnt != tagCnt) {
			// merge
			val tagCnt2 = TagCnt(min(tagCnt.senseNum.toDouble(), existingTagCnt.senseNum.toDouble()).toInt(), max(tagCnt.tagCount.toDouble(), existingTagCnt.tagCount.toDouble()).toInt())
			tagCntByKey[key] = tagCnt2
			if (LOG_TAGCOUNT_MERGE) {
				psi.printf("Tag count for %s contained %s, merged to %s%n", key, existingTagCnt, tagCnt2)
			}
		}

		// store relations by key
		val synset = pojoSynsetsById[sense.synsetId]
		synset?.relations.let {
			relationsByKey[key] = it!!
		}
	}

	// 3 - C O N S U M E   I N D E X   P O J O S

	// from index.(noun|verb|adj|adv)
	/**
	 * Index consumer
	 */
	private val indexConsumer = Consumer { idx: Index ->
		val lemma = idx.lemma.toString()
		val pos = idx.pos.toChar()

		// senses
		val i = intArrayOf(1)
		Arrays.stream(idx.senses) //
			.forEach { sense: BaseSense ->

				//psi.println("\t" + sense);
				// pos and index
				assert(pos == sense.synsetId.pos.toChar()) { sense }

				//assert i[0] == sense.sensePosIndex : sense;

				// case-sensitive lemma
				val synsetId = sense.synsetId
				val synset = pojoSynsetsById[synsetId]
				val members = synset!!.cSLemmas
				Arrays.stream(members) //
					.filter { member: LemmaCS -> member.toString().equals(lemma, ignoreCase = true) } //
					.forEach { member: LemmaCS ->
						val memberLemma = member.toString()
						// key
						val key = Key(lemma, pos, sense.synsetId.offset)

						// retrieve tag count
						val tagCnt = tagCntByKey[key]

						// retrieve relations, build sense relations
						val relations = relationsByKey[key]
						val senseRelations = buildSenseRelations(member.toString(), relations)

						// retrieve sensekey
						val sensekey = checkNotNull(sensekeyByKey[key]) { "no sensekey for $key" }
						// type
						val type = if (pos != 'a') pos else (if (sensekey.split("%".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].startsWith("5")) 's' else 'a')

						// ver frames and adj positions
						val verbFrames = if (pos != 'v') null else buildVerbFrames(synset, memberLemma)
						val adjPosition = if (pos != 'a') null else (if (member.lemma is AdjLemma) (member.lemma as AdjLemma).position.id else null)

						// collect lex
						val wpKey = from(memberLemma, type)
						val lex = lexesByKey.computeIfAbsent(wpKey) { Lex(memberLemma, type.toString(), null) }

						// senses
						val modelSense = org.oewntk.model.Sense(sensekey, lex, pos, i[0], sense.synsetId.toString(), null, verbFrames, adjPosition, senseRelations)
						if (tagCnt!!.tagCount != 0) {
							modelSense.tagCount = TagCount(tagCnt.senseNum, tagCnt.tagCount)
						}

						// collect sense in lex
						lex.addSense(modelSense)

						// collect in senses
						senses.add(modelSense)
					}
				i[0]++
			}
	}

	/**
	 * Build sense relations
	 *
	 * @param relations relations including synset relations
	 * @return sense relations
	 */
	private fun buildSenseRelations(member: String, relations: Array<Relation>?): MutableMap<String, MutableSet<String>>? {
		require(!member.contains("_ABCDEFGHIJKLMNOPQRSTUVWXYZ")) { member }

		if (!relations.isNullOrEmpty()) {
			val map = relations
				.filterIsInstance<LexRelation>() // discard non-lexical
				.filter { member.equals((it).fromWord.lemma.toString(), ignoreCase = true) } // discard relations whose from word is not target member
				.map { it.type.name to toSensekey(it) } // (type: sensekey)
				.groupBy { it.first }
				.mapValues { it.value.map { it2 -> it2.second }.toMutableSet() } // type: sensekeys
				.toMutableMap()
			return map.ifEmpty { null }
		}
		return null
	}

	/**
	 * Resolve target sensekey of a lex relation
	 *
	 * @param lr lexical relation
	 * @return target sensekey
	 */
	private fun toSensekey(lr: LexRelation): String {
		val toSynsetId = lr.toSynsetId
		val toWord = resolveToWord(lr)
		val key = Key(toWord, toSynsetId.pos.toChar(), toSynsetId.offset)
		val sensekey = checkNotNull(sensekeyByKey[key]) { "no sensekey for $key" }
		return sensekey
	}

	/**
	 * Resolve target word of a lex relation
	 *
	 * @param lr lexical relation
	 * @return target word
	 */
	private fun resolveToWord(lr: LexRelation): String {
		val toSynsetId = lr.toSynsetId
		val toSynset = pojoSynsetsById[toSynsetId]!!
		val toWordRef = lr.toWord
		return toWordRef.resolve(toSynset).toString()
	}

	// 4 - C O N S U M E   M O R P H M A P P I N G   P O J O S

	// from (noun|verb|adj|adv).exc
	/**
	 * Lemma to set of morphs
	 */
	private val lemmaToMorphs: MutableMap<String, MutableMap<Char, TreeSet<String>>> = HashMap()

	/**
	 * Morph consumer
	 */
	private val morphConsumer = Consumer { mapping: MorphMapping ->
		val pos = mapping.pos.toChar()
		val morph = mapping.morph.toString()
		val lemmas = mapping.lemmas
		lemmas.forEach(Consumer { lemma: Lemma ->  //
			lemmaToMorphs //
				.computeIfAbsent(lemma.toString()) { HashMap() } //
				.computeIfAbsent(pos) { TreeSet() } //
				.add(morph)
		})
	}

	/**
	 * Inject morphs into model
	 *
	 * @param model         model
	 * @param lemmaToMorphs lemma to morphs map
	 */
	private fun setMorphs(model: CoreModel, lemmaToMorphs: Map<String, Map<Char, TreeSet<String>>>) {
		val lexByLemma = model.lexesByLemma!!
		lemmaToMorphs.forEach { (lemma, map2) ->
			map2.forEach { (pos, morphs) ->
				val lexes = lexByLemma[lemma]
				lexes?.let { lexes2 ->
					lexes2
						.filter { it.partOfSpeech == pos }
						.forEach {
							val morphs2 = morphs.toTypedArray()
							it.setForms(*morphs2)
						}
				}
			}
		}
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
	@JvmOverloads
	@Throws(IOException::class, ParsePojoException::class)
	fun parseCoreModel(
		synsetConsumer: Consumer<Synset> = this.synsetConsumer,
		senseConsumer: Consumer<Sense> = this.senseConsumer,
		indexConsumer: Consumer<Index> = this.indexConsumer,
		morphConsumer: Consumer<MorphMapping> = this.morphConsumer
	): CoreModel {
		DataParser.parseAllSynsets(dir, synsetConsumer)
		SenseParser.parseSenses(dir, senseConsumer)
		IndexParser.parseAllIndexes(dir, indexConsumer)
		MorphParser.parseAllMorphs(dir, morphConsumer)

		val lexes: Collection<Lex> = ArrayList(lexesByKey.values) // TreeMap.Values are not serializable
		val model = CoreModel(lexes, senses, synsets)
		setMorphs(model, lemmaToMorphs)
		return model
	}

	companion object {
		private const val LOG_TAGCOUNT_MERGE = false

		// PRINT STREAMS

		/**
		 * Info print stream
		 */
		private val psi: PrintStream = if (!System.getProperties().containsKey("SILENT")) Tracing.psInfo else Tracing.psNull

		// /**
		//  * Error print stream
		//  */
		// private val pse: PrintStream = if (!System.getProperties().containsKey("SILENT")) Tracing.psErr else Tracing.psNull

		/**
		 * Build synset relations
		 *
		 * @param relations relations
		 * @return map type to set of target synset ids
		 */
		private fun buildSynsetRelations(relations: Array<Relation>?): MutableMap<String, MutableSet<String>>? {
			if (!relations.isNullOrEmpty()) {
				val map = relations
					.filter { it !is LexRelation }
					.map { it.type.name to it.toSynsetId.toString() } // (type, synsetid)
					.groupBy { it.first }
					.mapValues { it.value.map { it2 -> it2.second }.toMutableSet() } // type: synsetids
					.toMutableMap()
				return map.ifEmpty { null }
			}
			return null
		}

		// name, frame, frameid
		private val VERBFRAME_VALUES = arrayOf(
			"vii" to 1,
			"via" to 2,
			"nonreferential" to 3,
			"vii-pp" to 4,
			"vtii-adj" to 5,
			"vii-adj" to 6,
			"via-adj" to 7,
			"vtai" to 8,
			"vtaa" to 9,
			"vtia" to 10,
			"vtii" to 11,
			"vii-to" to 12,
			"via-on-inanim" to 13,
			"ditransitive" to 14,
			"vtai-to" to 15,
			"vtai-from" to 16,
			"vtaa-with" to 17,
			"vtaa-of" to 18,
			"vtai-on" to 19,
			"vtaa-pp" to 20,
			"vtai-pp" to 21,
			"via-pp" to 22,
			"vibody" to 23,
			"vtaa-to-inf" to 24,
			"vtaa-inf" to 25,
			"via-that" to 26,
			"via-to" to 27,
			"via-to-inf" to 28,
			"via-whether-inf" to 29,
			"vtaa-into-ger" to 30,
			"vtai-with" to 31,
			"via-inf" to 32,
			"via-ger" to 33,
			"nonreferential-sent" to 34,
			"vii-inf" to 35,
			"via-at" to 36,
			"via-for" to 37,
			"via-on-anim" to 38,
			"via-out-of" to 39,
		)

		/**
		 * Map frame numeric id to id (via, ...)
		 */
		private val VERB_FRAME_NID_TO_IDS = VERBFRAME_VALUES.associate { it.second to it.first }

		/**
		 * Build verb frame ids
		 *
		 * @param synset synset
		 * @param lemma  lemma
		 * @return array of verb frame ids
		 */
		fun buildVerbFrames(synset: Synset, lemma: String): Array<String>? {
			val verbFrames = synset.verbFrames ?: return null
			return verbFrames
				.filter { it2 ->
					it2.lemmas
						.map { it.toString() }
						.any { it == lemma }
				}
				.map { VERB_FRAME_NID_TO_IDS[it.frameId]!! }
				.toTypedArray()
		}
	}
}
