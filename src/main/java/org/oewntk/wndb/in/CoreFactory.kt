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
) : Supplier<CoreModel?> {

    override fun get(): CoreModel? {
        try {
            return Parser(inDir) 
                .parseCoreModel() 
                .generateInverseRelations() 
                .apply { source = inDir }
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
         * @param dirPath WNDB dir path
         * @return core model
         */
        private fun makeCoreModel(dirPath: String): CoreModel? {
            val inDir = File(dirPath)
            return CoreFactory(inDir).get()
        }

        /**
         * Main
         *
         * @param args command-line arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            for (arg in args) {
                val model = makeCoreModel(arg)
                Tracing.psInfo.printf("[CoreModel] %s%n%s%n%s%n", model!!.source, model.info(), ModelInfo.counts(model))
            }
        }
    }
}