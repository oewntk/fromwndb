/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.Assert
import org.oewntk.model.CoreModel
import org.oewntk.model.ModelInfo
import java.io.File
import java.io.PrintStream

object LibTestsWndbCommon {

    private val source: String? = System.getProperty("SOURCE")

    val ps: PrintStream = if (!System.getProperties().containsKey("SILENT")) Tracing.psInfo else Tracing.psNull

    var model: CoreModel? = null

    fun init() {
        if (model == null) {
            if (source == null) {
                Tracing.psErr.println("Define WNDB source dir with -DSOURCE=path")
                Tracing.psErr.println("When running Maven tests, define the wndb directory as child to the project directory.")
                Assert.fail()
            }
            val inDir = File(source!!)
            Tracing.psInfo.printf("source=%s%n", inDir.absolutePath)
            if (!inDir.exists()) {
                Tracing.psErr.println("Define WNDB source dir that exists")
                Assert.fail()
            }
            model = CoreFactory(inDir).get()
        }
        checkNotNull(model)
        ps.println(model!!.info())
        ps.println(ModelInfo.counts(model!!))
    }
}
