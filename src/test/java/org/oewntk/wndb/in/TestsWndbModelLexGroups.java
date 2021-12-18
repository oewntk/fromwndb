/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModelLexGroups;

public class TestsWndbModelLexGroups
{
	@BeforeClass
	public static void init()
	{
		TestsWndbCommon.init();
	}

	@Test
	public void testCIMultipleAll()
	{
		LibTestModelLexGroups.testCIMultipleAll(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testCILemmas()
	{
		LibTestModelLexGroups.testCILemmas(TestsWndbCommon.model, "battle of verdun", TestsWndbCommon.ps);
	}

	@Test
	public void testCICounts()
	{
		LibTestModelLexGroups.testCICounts(TestsWndbCommon.model, "battle of verdun", TestsWndbCommon.ps);
	}

	@Test
	public void testCICountsFromMap()
	{
		LibTestModelLexGroups.testCICountsFromMap(TestsWndbCommon.model, "battle of verdun", TestsWndbCommon.ps);
	}

	@Test
	public void testCIHypermapWest()
	{
		LibTestModelLexGroups.testCIHypermap(TestsWndbCommon.model, "west", TestsWndbCommon.ps);
	}

	@Test
	public void testCIHypermapBaroque()
	{
		LibTestModelLexGroups.testCIHypermap(TestsWndbCommon.model, "baroque", TestsWndbCommon.ps);
	}

	@Test
	public void testCIAi()
	{
		LibTestModelLexGroups.testCILexesFor(TestsWndbCommon.model, "ai", TestsWndbCommon.ps);
	}

	@Test
	public void testCIBaroque()
	{
		LibTestModelLexGroups.testCILexesFor(TestsWndbCommon.model, "baroque", TestsWndbCommon.ps);
	}

	@Test
	public void testCIWest3()
	{
		LibTestModelLexGroups.testCILexesFor3(TestsWndbCommon.model, "West", TestsWndbCommon.ps);
	}

	@Test
	public void testCIBaroque3()
	{
		LibTestModelLexGroups.testCILexesFor3(TestsWndbCommon.model, "Baroque", TestsWndbCommon.ps);
	}

	@Test
	public void testCIAi3()
	{
		LibTestModelLexGroups.testCILexesFor3(TestsWndbCommon.model, "Ai", TestsWndbCommon.ps);
	}

	@Test
	public void testCIAbsolute3()
	{
		LibTestModelLexGroups.testCILexesFor3(TestsWndbCommon.model, "Absolute", TestsWndbCommon.ps);
	}
}
