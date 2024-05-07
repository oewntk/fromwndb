/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.ModelInfo
import org.oewntk.wndb.`in`.LibTestsWndbCommon.model
import org.oewntk.wndb.`in`.LibTestsWndbCommon.ps

class TestsWndbModel {

    @Test
    fun testModelInfo() {
        val info = model!!.info()
        val counts = ModelInfo.counts(model!!)
        ps.println("$info\n$counts")
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun init() {
            LibTestsWndbCommon.init()
            checkNotNull(model)
        }
    }
}
