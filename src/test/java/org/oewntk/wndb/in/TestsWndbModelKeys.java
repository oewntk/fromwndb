/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.CoreModel;
import org.oewntk.model.LibTestModelKeys;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.oewntk.pojos.ParsePojoException;

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
	public static void init() throws IOException, ParsePojoException
	{
		File inDir = new File(source);
		// File inDir2 = new File(source2);

		model = Factory.makeCoreModel(inDir);
		System.err.println(model.info());
		System.err.println(model.counts());
	}

	@Test
	public void testEarth()
	{
		LibTestModelKeys.testEarth(model, ps);
	}

	@Test
	public void testBaroque()
	{
		LibTestModelKeys.testBaroque(model, ps);
	}

	@Test
	public void testMobile()
	{
		LibTestModelKeys.testMobile(model, ps);
	}

	@Test
	public void testBass()
	{
		LibTestModelKeys.testBass(model, ps);
	}

	@Test
	public void testRow()
	{
		LibTestModelKeys.testRow(model, ps);
	}

	@Test
	public void testCritical()
	{
		LibTestModelKeys.testCritical(model, ps);
	}
}
