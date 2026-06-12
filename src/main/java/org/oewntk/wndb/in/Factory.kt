/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.oewntk.model.Model
import org.oewntk.model.ModelInfo
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
    private val inDir2: File?,
    private val verbose: Boolean = false,
) : Supplier<Model?> {

    override fun get(): Model? {
        val coreModel = CoreFactory(inDir, verbose = verbose).get() ?: return null

        try {
            // verb frames and templates
            val verbFramesById = VerbFrameParser(inDir2 ?: inDir).parse() // orig:verbFrames.txt gen:verbFrames.txt
            val verbTemplatesById = VerbTemplateParser(inDir2 ?: inDir).parse() // templates.txt

            // sense to verb templates
            val senseToVerbTemplates = SenseToVerbTemplatesParser(inDir).parse() // sentidx.vrb

            // tag counts
            val senseToTagCounts: Collection<Pair<String, TagCount>> = SenseToTagCountsParser(inDir).parse() // cntlist.rev

            return Model(coreModel, verbFramesById, verbTemplatesById, senseToVerbTemplates, senseToTagCounts)
                .apply {
                    source = inDir.absolutePath
                    source2 = inDir2?.absolutePath
                }

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
        private fun makeModel(inDir: File, inDir2: File?, verbose: Boolean = false): Model? {
            return Factory(inDir, inDir2, verbose = verbose).get()
        }

        /**
         * Make model
         *
         * @param dirPath1 WNDB dir path
         * @param dirPath2 extra WNDB dir path
         * @return core model
         */
        private fun makeModel(dirPath1: String, dirPath2: String?, verbose: Boolean = false): Model? {
            val inDir = File(dirPath1)
            val inDir2 = if (dirPath2 == null) null else File(dirPath2)
            return Factory(inDir, inDir2, verbose = verbose).get()
        }

        /**
         * Make model
         *
         * @param args command-line arguments
         * @return core model
         */
        fun makeModel(args: Array<String>): Model? {
            var iArg = 0
            var verbose = false
            if (args[iArg] == "--verbose") {
                verbose = true
                iArg++
            }
            val inDir = File(args[iArg])
            iArg++
            val inDir2 = if (iArg < args.size) File(args[iArg]) else null
            return makeModel(inDir, inDir2, verbose = verbose)
        }

        /**
         * Main
         *
         * @param args command-line arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val model = makeModel(args)
            Tracing.psInfo.printf("[Model] %s%n%s%n%s%n", model!!, model.info(), ModelInfo.counts(model))
        }
    }
}