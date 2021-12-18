/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModelQueries;

public class TestsWndbModelQueries
{
	@BeforeClass
	public static void init()
	{
		TestsWndbCommon.init();
	}

	@Test
	public void testRowByType()
	{
		LibTestModelQueries.testWordByType(TestsWndbCommon.model, "row", TestsWndbCommon.ps);
	}

	@Test
	public void testRowByPos()
	{
		LibTestModelQueries.testWordByType(TestsWndbCommon.model, "row", TestsWndbCommon.ps);
	}

	@Test
	public void testRowByTypeAndPronunciation()
	{
		LibTestModelQueries.testWordByTypeAndPronunciation(TestsWndbCommon.model, "row", TestsWndbCommon.ps);
	}

	@Test
	public void testRowByPosAndPronunciation()
	{
		LibTestModelQueries.testWordByTypeAndPronunciation(TestsWndbCommon.model, "row", TestsWndbCommon.ps);
	}


	@Test
	public void testCriticalByType()
	{
		LibTestModelQueries.testWordByType(TestsWndbCommon.model, "critical", TestsWndbCommon.ps);
	}

	@Test
	public void testCriticalByPos()
	{
		LibTestModelQueries.testWordByType(TestsWndbCommon.model, "critical", TestsWndbCommon.ps);
	}

	@Test
	public void testBassByPos()
	{
		LibTestModelQueries.testWordByType(TestsWndbCommon.model, "bass", TestsWndbCommon.ps);
	}

	@Test
	public void testBaroqueByPos()
	{
		LibTestModelQueries.testWordByType(TestsWndbCommon.model, "baroque", TestsWndbCommon.ps);
	}

	@Test
	public void testBaroqueCSByPos()
	{
		LibTestModelQueries.testWordByType(TestsWndbCommon.model, "Baroque", TestsWndbCommon.ps);
	}

	@Test
	public void testGaloreByPos()
	{
		LibTestModelQueries.testWordByType(TestsWndbCommon.model, "galore", TestsWndbCommon.ps);
	}
}
