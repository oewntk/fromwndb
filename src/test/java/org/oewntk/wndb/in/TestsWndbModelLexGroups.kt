/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.LibTestModelLexGroups.testCICounts
import org.oewntk.model.LibTestModelLexGroups.testCICountsFromMap
import org.oewntk.model.LibTestModelLexGroups.testCIHypermap
import org.oewntk.model.LibTestModelLexGroups.testCILemmas
import org.oewntk.model.LibTestModelLexGroups.testCILexesFor
import org.oewntk.model.LibTestModelLexGroups.testCILexesFor3
import org.oewntk.model.LibTestModelLexGroups.testCIMultipleAll
import org.oewntk.wndb.`in`.LibTestsWndbCommon.model
import org.oewntk.wndb.`in`.LibTestsWndbCommon.ps

class TestsWndbModelLexGroups {

    @Test
    fun testCIMultipleAll() {
        testCIMultipleAll(model!!, ps)
    }

    @Test
    fun testCILemmas() {
        testCILemmas(model!!, "battle of verdun", ps)
    }

    @Test
    fun testCICounts() {
        testCICounts(model!!, "battle of verdun", ps)
    }

    @Test
    fun testCICountsFromMap() {
        testCICountsFromMap(model!!, "battle of verdun", ps)
    }

    @Test
    fun testCIHypermapWest() {
        testCIHypermap(model!!, "west", ps)
    }

    @Test
    fun testCIHypermapBaroque() {
        testCIHypermap(model!!, "baroque", ps)
    }

    @Test
    fun testCIAi() {
        testCILexesFor(model!!, "ai", ps)
    }

    @Test
    fun testCIBaroque() {
        testCILexesFor(model!!, "baroque", ps)
    }

    @Test
    fun testCIWest3() {
        testCILexesFor3(model!!, "West", ps)
    }

    @Test
    fun testCIBaroque3() {
        testCILexesFor3(model!!, "Baroque", ps)
    }

    @Test
    fun testCIAi3() {
        testCILexesFor3(model!!, "Ai", ps)
    }

    @Test
    fun testCIAbsolute3() {
        testCILexesFor3(model!!, "Absolute", ps)
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
