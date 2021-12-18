/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModelKeys;

public class TestsWndbModelKeys
{
	@BeforeClass
	public static void init()
	{
		TestsWndbCommon.init();
	}

	@Test
	public void testEarthMulti()
	{
		LibTestModelKeys.testEarthMulti(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEarthMono()
	{
		LibTestModelKeys.testEarthMono(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testBaroqueMulti()
	{
		LibTestModelKeys.testBaroqueMulti(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBaroqueMono()
	{
		LibTestModelKeys.testBaroqueMono(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testMobile()
	{
		LibTestModelKeys.testMobile(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBassDeep()
	{
		LibTestModelKeys.testBassDeep(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBassShallow()
	{
		LibTestModelKeys.testBassShallow(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowDeep()
	{
		LibTestModelKeys.testRowDeep(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowShallow()
	{
		LibTestModelKeys.testRowShallow(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCriticalDeep()
	{
		LibTestModelKeys.testCriticalDeep(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCriticalPos()
	{
		LibTestModelKeys.testCriticalPos(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testCriticalPWN()
	{
		LibTestModelKeys.testCriticalPWN(TestsWndbCommon.model, TestsWndbCommon.ps);
	}
}
