/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.Assert
import org.oewntk.model.CoreModel
import java.io.File
import java.io.PrintStream

object LibTestsWndbCommon {

    private val source: String? = System.getProperty("SOURCE")

    val ps: PrintStream = if (!System.getProperties().containsKey("SILENT")) Tracing.psInfo else Tracing.psNull

    val model: CoreModel by lazy {
        if (source == null) {
            Tracing.psErr.println("Define serialized source file dir with -DSOURCE=path")
            throw AssertionError("SOURCE not defined")
        }
        val inDir = File(source)
        Tracing.psInfo.printf("source=%s%n", inDir.absolutePath)
        if (!inDir.exists()) {
            Tracing.psErr.println("Define YAML source dir that exists")
            Assert.fail()
        }
        CoreFactory(inDir).get()!!
    }
}
