/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.Key
import org.oewntk.model.Lex
import org.oewntk.model.LibTestModel.makeIndexMap
import org.oewntk.model.LibTestModel.makeSortedIndexMap
import org.oewntk.model.LibTestModel.testScanLexesForTestWords
import org.oewntk.model.LibTestModel.testWord
import org.oewntk.model.LibTestModel.testWords
import org.oewntk.wndb.`in`.LibTestsWndbCommon.model
import org.oewntk.wndb.`in`.LibTestsWndbCommon.ps

class TestsWndbModelWords {

    @Test
    fun testScanLexesForTestWords() {
        testScanLexesForTestWords(model!!, { lex: Lex -> Key.KeyLCP.of_t(lex) }, { seq: Sequence<Key> -> makeIndexMap(seq) }, testWords, PRINT_TEST_WORDS, ps)
    }

    @Test
    fun testScanLexesForTestWordsSorted() {
        testScanLexesForTestWords(model!!, { lex: Lex -> Key.KeyLCP.of_t(lex) }, { seq: Sequence<Key> -> makeSortedIndexMap(seq) }, testWords, PRINT_TEST_WORDS, ps)
    }

    @Test
    fun testBass() {
        testWord("bass", model!!, ps)
    }

    @Test
    fun testRow() {
        testWord("row", model!!, ps)
    }

    @Test
    fun testBaroque() {
        testWords(model!!, ps, "baroque", "Baroque")
    }

    @Test
    fun testEarth() {
        testWords(model!!, ps, "earth", "Earth")
    }

    @Test
    fun testCritical() {
        testWord("critical", 'a', model!!, ps)
    }

    @Test
    fun testHollywood() {
        testWord("Hollywood", 'a', model!!, ps)
    }

    @Test
    fun testVictorian() {
        testWord("Victorian", 'a', model!!, ps)
    }

    @Test
    fun testAllied() {
        testWord("allied", 'a', model!!, ps)
    }

    @Test
    fun testAlliedUpper() {
        testWord("Allied", 'a', model!!, ps)
    }

    @Test
    fun testAbsent() {
        testWord("absent", 'a', model!!, ps)
    }

    @Test
    fun testApocryphal() {
        testWord("apocryphal", 'a', model!!, ps)
    }

    @Test
    fun testUsed() {
        testWord("used", 'a', model!!, ps)
    }

    companion object {

        private const val PRINT_TEST_WORDS = false

        private val testWords = setOf("baroque", "Baroque", "bass", "row")

        @JvmStatic
        @BeforeClass
        fun init() {
            LibTestsWndbCommon.init()
            checkNotNull(model)
        }
    }
}
