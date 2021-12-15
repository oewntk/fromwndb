/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModelLexGroups;
import org.oewntk.model.Model;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

public class TestsWndbModelLexGroups
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
	public static void init()
	{
		File inDir = new File(source);
		File inDir2 = new File(source2);

		model = new Factory(inDir, inDir2).get();
		System.err.println(model.info());
		System.err.println(model.counts());
	}

	@Test
	public void testCIMultipleAll()
	{
		LibTestModelLexGroups.testCIMultipleAll(model, ps);
	}

	@Test
	public void testCILemmas()
	{
		LibTestModelLexGroups.testCILemmas(model, "battle of verdun", ps);
	}

	@Test
	public void testCICounts()
	{
		LibTestModelLexGroups.testCICounts(model, "battle of verdun", ps);
	}

	@Test
	public void testCICountsFromMap()
	{
		LibTestModelLexGroups.testCICountsFromMap(model, "battle of verdun", ps);
	}

	@Test
	public void testCIHypermapWest()
	{
		LibTestModelLexGroups.testCIHypermap(model, "west", ps);
	}

	@Test
	public void testCIHypermapBaroque()
	{
		LibTestModelLexGroups.testCIHypermap(model, "baroque", ps);
	}

	@Test
	public void testCIAi()
	{
		LibTestModelLexGroups.testCILexesFor(model, "ai", ps);
	}

	@Test
	public void testCIBaroque()
	{
		LibTestModelLexGroups.testCILexesFor(model, "baroque", ps);
	}

	@Test
	public void testCIWest3()
	{
		LibTestModelLexGroups.testCILexesFor3(model, "West", ps);
	}

	@Test
	public void testCIBaroque3()
	{
		LibTestModelLexGroups.testCILexesFor3(model, "Baroque", ps);
	}

	@Test
	public void testCIAi3()
	{
		LibTestModelLexGroups.testCILexesFor3(model, "Ai", ps);
	}

	@Test
	public void testCIAbsolute3()
	{
		LibTestModelLexGroups.testCILexesFor3(model, "Absolute", ps);
	}
}
