/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.oewntk.model.CoreModel
import org.oewntk.model.ModelInfo
import org.oewntk.pojos.ParsePojoException
import java.io.File
import java.io.IOException
import java.util.function.Supplier

/**
 * Core model factory
 *
 * @property inDir WNDB dir
 */
class CoreFactory(
    private val inDir: File,
    private val inverses: Boolean = false,
    private val verbose: Boolean = false
) : Supplier<CoreModel?> {

    override fun get(): CoreModel? {
        try {
            return Parser(inDir, verbose = verbose)
                .parseCoreModel()
                .apply { if (inverses) generateInverseRelations() }
                .apply { source = inDir.absolutePath }
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
            return null
        } catch (e: ParsePojoException) {
            e.printStackTrace(Tracing.psErr)
            return null
        }
    }

    companion object {

        /**
         * Make core model
         *
         * @param inDir WNDB dir
         * @return core model
         */
        private fun makeCoreModel(inDir: File, inverses: Boolean = false, verbose: Boolean = false): CoreModel? {
            return CoreFactory(inDir, inverses = inverses, verbose = verbose).get()
        }

        /**
         * Make core model from YAML files
         *
         * @param args command-line arguments
         * @return core model
         */
        private fun makeCoreModel(args: Array<String>): CoreModel? {
            var iArg = 0
            var inverses = false
            var verbose = false
            if ("--verbose" == args[iArg]) {
                verbose = true
                iArg++
            }
            if ("--inverses" == args[iArg]) {
                inverses = true
                iArg++
            }
            val inDir = File(args[iArg])
            return makeCoreModel(inDir, inverses = inverses, verbose = verbose)
        }

        /**
         * Main
         *
         * @param args command-line arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val model = makeCoreModel(args)
            Tracing.psInfo.printf("[CoreModel] %s%n%s%n%s%n", model!!.source, model.info(), ModelInfo.counts(model))
        }
    }
}