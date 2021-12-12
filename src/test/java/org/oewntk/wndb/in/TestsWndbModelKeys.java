/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.CoreModel;
import org.oewntk.model.LibTestModelKeys;
import org.oewntk.pojos.ParsePojoException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class TestsWndbModelKeys
{
	private static final String source = System.getProperty("SOURCE");

	// private static final String source2 = System.getProperty("SOURCE2");

	private static final PrintStream ps = !System.getProperties().containsKey("SILENT") ? System.out : new PrintStream(new OutputStream()
	{
		public void write(int b)
		{
			//DO NOTHING
		}
	});

	private static CoreModel model;

	@BeforeClass
	public static void init()
	{
		File inDir = new File(source);
		// File inDir2 = new File(source2);

		model = new CoreFactory(inDir).get();
		System.err.println(model.info());
		System.err.println(model.counts());
	}

	@Test
	public void testEarthMulti()
	{
		LibTestModelKeys.testEarthMulti(model, ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEarthMono()
	{
		LibTestModelKeys.testEarthMono(model, ps);
	}

	@Test
	public void testBaroqueMulti()
	{
		LibTestModelKeys.testBaroqueMulti(model, ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBaroqueMono()
	{
		LibTestModelKeys.testBaroqueMono(model, ps);
	}

	@Test
	public void testMobile()
	{
		LibTestModelKeys.testMobile(model, ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBassDeep()
	{
		LibTestModelKeys.testBassDeep(model, ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBassShallow()
	{
		LibTestModelKeys.testBassShallow(model, ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowDeep()
	{
		LibTestModelKeys.testRowDeep(model, ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRowShallow()
	{
		LibTestModelKeys.testRowShallow(model, ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCriticalDeep()
	{
		LibTestModelKeys.testCriticalDeep(model, ps);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCriticalPos()
	{
		LibTestModelKeys.testCriticalPos(model, ps);
	}

	@Test
	public void testCriticalPWN()
	{
		LibTestModelKeys.testCriticalPWN(model, ps);
	}
}
