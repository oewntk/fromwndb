/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.oewntk.model.Model
import org.oewntk.model.TagCount
import java.io.File
import java.io.IOException
import java.util.function.Supplier

/**
 * Model factory
 *
 * @property inDir  WNDB dir
 * @property inDir2 extra WNDB dir
 */
class Factory(
	private val inDir: File,
	private val inDir2: File
) : Supplier<Model?> {

	override fun get(): Model? {
		val coreModel = CoreFactory(inDir).get() ?: return null

		try {
			// verb frames and templates
			val verbFramesById = VerbFrameParser(inDir2).parse()
			val verbTemplatesById = VerbTemplateParser(inDir2).parse()
			val senseToVerbTemplates = SenseToVerbTemplatesParser(inDir).parse()

			// tag counts
			val senseToTagCounts: Collection<Pair<String, TagCount>> = SenseToTagCountsParser(inDir).parse()

			return Model(coreModel, verbFramesById, verbTemplatesById, senseToVerbTemplates, senseToTagCounts).setSources(inDir, inDir2)
		} catch (e: IOException) {
			e.printStackTrace(Tracing.psErr)
			return null
		}
	}

	companion object {

		/**
		 * Make model
		 *
		 * @param inDir  WNDB dir
		 * @param inDir2 extra WNDB dir
		 * @return model
		 */
		private fun makeModel(inDir: File, inDir2: File): Model? {
			return Factory(inDir, inDir2).get()
		}

		/**
		 * Make model
		 *
		 * @param dirPath1 WNDB dir path
		 * @param dirPath2 extra WNDB dir path
		 * @return core model
		 */
		private fun makeModel(dirPath1: String, dirPath2: String): Model? {
			val inDir = File(dirPath1)
			val inDir2 = File(dirPath2)
			return Factory(inDir, inDir2).get()
		}

		/**
		 * Make model
		 *
		 * @param args command-line arguments
		 * @return core model
		 */
		fun makeModel(args: Array<String>): Model? {
			val inDir = File(args[0])
			val inDir2 = File(args[1])
			return makeModel(inDir, inDir2)
		}

		/**
		 * Main
		 *
		 * @param args command-line arguments
		 */
		@JvmStatic
		fun main(args: Array<String>) {
			val dirPath2 = args[args.size - 1] // last
			for (i in 0 until args.size - 1) {
				val model = makeModel(args[i], dirPath2)
				Tracing.psInfo.printf("[Model] %s%n%s%n%s%n", model!!.sources.contentToString(), model.info(), model.counts())
			}
		}
	}
}