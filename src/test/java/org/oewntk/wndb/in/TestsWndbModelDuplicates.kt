/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModelDuplicates;

public class TestsWndbModelDuplicates
{
	@BeforeClass
	public static void init()
	{
		TestsWndbCommon.init();
	}

	@Test
	public void testKeyOEWN()
	{
		LibTestModelDuplicates.testDuplicatesForKeyOEWN(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test(expected = AssertionError.class)
	public void testKeyPos()
	{
		LibTestModelDuplicates.testDuplicatesForKeyPos(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testKeyIC()
	{
		LibTestModelDuplicates.testDuplicatesForKeyIC(TestsWndbCommon.model, TestsWndbCommon.ps);
	}

	@Test
	public void testKeyPWN()
	{
		LibTestModelDuplicates.testDuplicatesForKeyPWN(TestsWndbCommon.model, TestsWndbCommon.ps);
	}
}
