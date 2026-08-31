/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.Lemma
import org.oewntk.model.LibTestModelQueries.testWordByType
import org.oewntk.model.LibTestModelQueries.testWordByTypeAndPronunciation
import org.oewntk.wndb.`in`.LibTestsWndbCommon.model
import org.oewntk.wndb.`in`.LibTestsWndbCommon.ps

class TestsWndbModelQueries {

    @Test
    fun testRowByType() {
        testWordByType(model, Lemma("row"), ps)
    }

    @Test
    fun testRowByPos() {
        testWordByType(model, Lemma("row"), ps)
    }

    @Test
    fun testRowByTypeAndPronunciation() {
        testWordByTypeAndPronunciation(model, Lemma("row"), ps)
    }

    @Test
    fun testRowByPosAndPronunciation() {
        testWordByTypeAndPronunciation(model, Lemma("row"), ps)
    }

    @Test
    fun testCriticalByType() {
        testWordByType(model, Lemma("critical"), ps)
    }

    @Test
    fun testCriticalByPos() {
        testWordByType(model, Lemma("critical"), ps)
    }

    @Test
    fun testBassByPos() {
        testWordByType(model, Lemma("bass"), ps)
    }

    @Test
    fun testBaroqueByPos() {
        testWordByType(model, Lemma("baroque"), ps)
    }

    @Test
    fun testBaroqueCSByPos() {
        testWordByType(model, Lemma("Baroque"), ps)
    }

    @Test
    fun testGaloreByPos() {
        testWordByType(model, Lemma("galore"), ps)
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun init() {
            model
        }
    }
}
