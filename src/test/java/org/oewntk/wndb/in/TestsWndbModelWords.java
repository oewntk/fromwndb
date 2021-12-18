/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModel;

import java.util.Set;

public class TestsWndbModelWords
{
	private static final boolean peekTestWords = false;

	private static final Set<String> testWords = Set.of("baroque", "Baroque", "bass", "row");

	@BeforeClass
	public static void init()
	{
		TestsWndbCommon.init();
	}

	@Test
	public void testScanLexesForTestWords()
	{
		LibTestModel.testScanLexesForTestWords(TestsWndbCommon.model, LibTestModel::makeIndexMap, testWords, peekTestWords, TestsWndbCommon.ps);
	}

	@Test
	public void testScanLexesForTestWordsSorted()
	{
		LibTestModel.testScanLexesForTestWords(TestsWndbCommon.model, LibTestModel::makeSortedIndexMap, testWords, peekTestWords, TestsWndbCommon.ps);
	}

	@Test
	public void testBass()
	{
		LibTestModel.testWord("bass", TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testRow()
	{
		LibTestModel.testWord("row", TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testBaroque()
	{
		LibTestModel.testWords(TestsWndbCommon.model, TestsWndbCommon.ps, "baroque", "Baroque");
	}

	@Test
	public void testEarth()
	{
		LibTestModel.testWords(TestsWndbCommon.model, TestsWndbCommon.ps, "earth", "Earth");
	}

	@Test
	public void testCritical()
	{
		LibTestModel.testWord("critical", 'a', TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testHollywood()
	{
		LibTestModel.testWord("Hollywood", 'a', TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testVictorian()
	{
		LibTestModel.testWord("Victorian", 'a', TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testAllied()
	{
		LibTestModel.testWord("allied", 'a', TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testAlliedUpper()
	{
		LibTestModel.testWord("Allied", 'a', TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testAbsent()
	{
		LibTestModel.testWord("absent", 'a', TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testApocryphal()
	{
		LibTestModel.testWord("apocryphal", 'a', TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testUsed()
	{
		LibTestModel.testWord("used", 'a', TestsWndbCommon.model, TestsWndbCommon.ps);
	}
}
