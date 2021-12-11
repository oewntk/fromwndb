/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModelGroups;
import org.oewntk.model.Model;
import org.oewntk.pojos.ParsePojoException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class TestsWndbModelGroups
{
	private static final String source = System.getProperty("SOURCE");

	private static final String source2 = System.getProperty("SOURCE2");

	private static final PrintStream ps = !System.getProperties().containsKey("SILENT") ? System.out : new PrintStream(new OutputStream()
	{
		public void write(int b)
		{
			//DO NOTHING
		}
	});

	private static Model model;

	@BeforeClass
	public static void init() throws IOException, ParsePojoException
	{
		File inDir = new File(source);
		File inDir2 = new File(source2);

		model = Factory.makeModel(inDir, inDir2);
		System.err.println(model.info());
		System.err.println(model.counts());
	}

	@Test
	public void testCIMultipleAll()
	{
		LibTestModelGroups.testCIMultipleAll(model, ps);
	}

	@Test
	public void testCILemmas()
	{
		LibTestModelGroups.testCILemmas(model, "battle of verdun", ps);
	}

	@Test
	public void testCICounts()
	{
		LibTestModelGroups.testCICounts(model, "battle of verdun", ps);
	}

	@Test
	public void testCICountsFromMap()
	{
		LibTestModelGroups.testCICountsFromMap(model, "battle of verdun", ps);
	}

	@Test
	public void testCIWest()
	{
		LibTestModelGroups.testCI(model, "west", ps);
	}

	@Test
	public void testCIBaroque()
	{
		LibTestModelGroups.testCI(model, "baroque", ps);
	}
}
