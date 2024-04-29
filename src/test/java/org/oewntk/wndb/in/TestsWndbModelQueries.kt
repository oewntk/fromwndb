/*
 * Copyright (c) 2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.LibTestModelQueries.testWordByType
import org.oewntk.model.LibTestModelQueries.testWordByTypeAndPronunciation
import org.oewntk.wndb.`in`.LibTestsWndbCommon.model
import org.oewntk.wndb.`in`.LibTestsWndbCommon.ps

class TestsWndbModelQueries {

    @Test
    fun testRowByType() {
        testWordByType(model!!, "row", ps)
    }

    @Test
    fun testRowByPos() {
        testWordByType(model!!, "row", ps)
    }

    @Test
    fun testRowByTypeAndPronunciation() {
        testWordByTypeAndPronunciation(model!!, "row", ps)
    }

    @Test
    fun testRowByPosAndPronunciation() {
        testWordByTypeAndPronunciation(model!!, "row", ps)
    }

    @Test
    fun testCriticalByType() {
        testWordByType(model!!, "critical", ps)
    }

    @Test
    fun testCriticalByPos() {
        testWordByType(model!!, "critical", ps)
    }

    @Test
    fun testBassByPos() {
        testWordByType(model!!, "bass", ps)
    }

    @Test
    fun testBaroqueByPos() {
        testWordByType(model!!, "baroque", ps)
    }

    @Test
    fun testBaroqueCSByPos() {
        testWordByType(model!!, "Baroque", ps)
    }

    @Test
    fun testGaloreByPos() {
        testWordByType(model!!, "galore", ps)
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
