/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.Key2
import org.oewntk.model.Lemma
import org.oewntk.model.Lex
import org.oewntk.model.Lex.Groups.groupByLCLemmaThenByKey2
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
        testCIMultipleAll(model, ps)
    }

    @Test
    fun testCILemmas() {
        testCILemmas(model, Lemma("battle of verdun"), ps)
    }

    @Test
    fun testCICounts() {
        testCICounts(model, Lemma("battle of verdun"), ps)
    }

    @Test
    fun testCICountsFromMap() {
        testCICountsFromMap(model, Lemma("battle of verdun"), ps)
    }

    @Test
    fun testCIAi() {
        testCILexesFor(model, Lemma("ai"), ps)
    }

    @Test
    fun testCIBaroque() {
        testCILexesFor(model, Lemma("baroque"), ps)
    }

    @Test
    fun testCIWest3() {
        testCILexesFor3(model, Lemma("West"), ps)
    }

    @Test
    fun testCIBaroque3() {
        testCILexesFor3(model, Lemma("Baroque"), ps)
    }

    @Test
    fun testCIAi3() {
        testCILexesFor3(model, Lemma("Ai"), ps)
    }

    @Test
    fun testCIAbsolute3() {
        testCILexesFor3(model, Lemma("Absolute"), ps)
    }

    private val lexLCHyperMap: Map<Lemma, Map<Key2, Collection<Lex>>> by lazy { model.lexes.asSequence().groupByLCLemmaThenByKey2() }

    @Test
    fun testCIHypermapWest() {
        testCIHypermap(lexLCHyperMap, Lemma("west"), ps)
    }

    @Test
    fun testCIHypermapBaroque() {
        testCIHypermap(lexLCHyperMap, Lemma("baroque"), ps)
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun init() {
            model
        }
    }
}
