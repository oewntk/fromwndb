/*
 * Copyright (c) 2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.LibTestModelDuplicates.testDuplicatesForKeyIC
import org.oewntk.model.LibTestModelDuplicates.testDuplicatesForKeyOEWN
import org.oewntk.model.LibTestModelDuplicates.testDuplicatesForKeyPWN
import org.oewntk.model.LibTestModelDuplicates.testDuplicatesForKeyPos
import org.oewntk.wndb.`in`.LibTestsWndbCommon.model
import org.oewntk.wndb.`in`.LibTestsWndbCommon.ps

class TestsWndbModelDuplicates {

    @Test
    fun testKeyOEWN() {
        testDuplicatesForKeyOEWN(model!!, ps)
    }

    @Test
    fun testKeyPos() {
        testDuplicatesForKeyPos(model!!, ps)
    }

    @Test
    fun testKeyIC() {
        testDuplicatesForKeyIC(model!!, ps)
    }

    @Test
    fun testKeyPWN() {
        testDuplicatesForKeyPWN(model!!, ps)
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
