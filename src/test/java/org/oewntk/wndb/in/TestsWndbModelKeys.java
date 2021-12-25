/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModelKeys;

import static org.junit.Assert.assertEquals;

public class TestsWndbModelKeys
{
	@BeforeClass
	public static void init()
	{
		TestsWndbCommon.init();
	}

	@Test
	public void testEarthMultiNoPronunciation()
	{
		LibTestModelKeys.testEarthMultiNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testEarthMonoNoPronunciation()
	{
		LibTestModelKeys.testEarthMonoNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testBaroqueMultiNoPronunciation()
	{
		int[] r = LibTestModelKeys.testBaroqueMultiNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
		assertEquals(r[0], 3);
		assertEquals(r[1], 1);
		assertEquals(r[2], 3);
	}

	@Test
	public void testBaroqueMonoNoPronunciation()
	{
		int[] r = LibTestModelKeys.testBaroqueMonoNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
		assertEquals(r[0], 1);
		assertEquals(r[1], 1);
		assertEquals(r[2], 0);
		assertEquals(r[3], 1);
	}

	@Test
	public void testMobileNoPronunciation()
	{
		LibTestModelKeys.testMobileNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testBassMonoNoPronunciation()
	{
		LibTestModelKeys.testBassMonoNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testBassMultiNoPronunciation()
	{
		LibTestModelKeys.testBassMultiNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testRowMonoNoPronunciation()
	{
		LibTestModelKeys.testBassMonoNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testRowMultiNoPronunciation()
	{
		LibTestModelKeys.testBassMultiNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}


	@Test
	public void testCriticalDeepNoPronunciation()
	{
		LibTestModelKeys.testCriticalDeepNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testCriticalPosNoPronunciation()
	{
		LibTestModelKeys.testCriticalPosNoPronunciation(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testCriticalPWN()
	{
		LibTestModelKeys.testCriticalPWN(TestsWndbCommon.model, TestsWndbCommon.ps);
	}
}
