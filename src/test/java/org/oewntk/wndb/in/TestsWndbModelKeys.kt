/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import junit.framework.TestCase.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.LibTestModelKeys.testBaroqueMono
import org.oewntk.model.LibTestModelKeys.testBaroqueMulti
import org.oewntk.model.LibTestModelKeys.testBassNoPronunciationDeep
import org.oewntk.model.LibTestModelKeys.testBassNoPronunciationShallow
import org.oewntk.model.LibTestModelKeys.testCriticalMono
import org.oewntk.model.LibTestModelKeys.testCriticalMulti
import org.oewntk.model.LibTestModelKeys.testEarthMono
import org.oewntk.model.LibTestModelKeys.testEarthMulti
import org.oewntk.model.LibTestModelKeys.testMobileNoPronunciation
import org.oewntk.model.LibTestModelKeys.testRowNoPronunciationDeep
import org.oewntk.model.LibTestModelKeys.testRowNoPronunciationShallow
import org.oewntk.wndb.`in`.LibTestsWndbCommon.model
import org.oewntk.wndb.`in`.LibTestsWndbCommon.ps

class TestsWndbModelKeys {

    @Test
    fun testMobile() {
        val r = testMobileNoPronunciation(model!!, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r[1].toLong())
        assertEquals(2, r[2].toLong())
        assertEquals(2, r[3].toLong())
        assertEquals(4, r.size.toLong())
    }

    @Test
    fun testEarthMulti() {
        val r = testEarthMulti(model!!, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r[1].toLong())
        assertEquals(2, r[2].toLong())
        assertEquals(2, r[3].toLong())
        assertEquals(4, r.size.toLong())
    }

    @Test
    fun testEarthMono() {
        val r = testEarthMono(model!!, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r[1].toLong())
        assertEquals(1, r[2].toLong())
        assertEquals(1, r[3].toLong())
        assertEquals(4, r.size.toLong())
    }

    @Test
    fun testBaroqueMulti() {
        val r = testBaroqueMulti(model!!, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r[1].toLong())
        assertEquals(2, r[2].toLong())
        assertEquals(2, r[3].toLong())
        assertEquals(1, r[4].toLong())
        assertEquals(2, r[5].toLong())
        assertEquals(3, r[6].toLong())
        assertEquals(3, r[7].toLong())
        assertEquals(0, r[8].toLong())
        assertEquals(1, r[9].toLong())
        assertEquals(1, r[10].toLong())
        assertEquals(1, r[11].toLong())
        assertEquals(0, r[12].toLong())
        assertEquals(0, r[13].toLong())
        assertEquals(0, r[14].toLong())
        assertEquals(0, r[14].toLong())
        assertEquals(16, r.size.toLong())
    }

    @Test
    fun testBaroqueMono() {
        val r = testBaroqueMono(model!!, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r[1].toLong())
        assertEquals(1, r[2].toLong())
        assertEquals(1, r[3].toLong())
        assertEquals(1, r[4].toLong())
        assertEquals(1, r[5].toLong())
        assertEquals(1, r[6].toLong())
        assertEquals(1, r[7].toLong())
        assertEquals(0, r[8].toLong())
        assertEquals(1, r[9].toLong())
        assertEquals(1, r[10].toLong())
        assertEquals(1, r[11].toLong())
        assertEquals(0, r[12].toLong())
        assertEquals(0, r[13].toLong())
        assertEquals(0, r[14].toLong())
        assertEquals(0, r[14].toLong())
        assertEquals(16, r.size.toLong())
    }

    @Test
    fun testCriticalMulti() {
        val r = testCriticalMulti(model!!, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(2, r[1].toLong())
        assertEquals(1, r[2].toLong())
        assertEquals(0, r[3].toLong())
        assertEquals(4, r.size.toLong())
    }

    @Test
    fun testCriticalMono() {
        val r = testCriticalMono(model!!, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r[1].toLong())
        assertEquals(1, r[2].toLong())
        assertEquals(0, r[3].toLong())
        assertEquals(4, r.size.toLong())
    }

    @Test
    fun testBassDeep() {
        val r = testBassNoPronunciationDeep(model, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r.size.toLong())
    }

    @Test
    fun testBassShallow() {
        val r = testBassNoPronunciationShallow(model, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r.size.toLong())
    }

    @Test
    fun testRowDeep() {
        val r = testRowNoPronunciationDeep(model, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r.size.toLong())
    }

    @Test
    fun testRowShallow() {
        val r = testRowNoPronunciationShallow(model, ps)
        assertEquals(1, r[0].toLong())
        assertEquals(1, r.size.toLong())
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
