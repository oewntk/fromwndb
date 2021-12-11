/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oewntk.model.LibTestModelQueries;
import org.oewntk.model.Model;
import org.oewntk.pojos.ParsePojoException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class TestsWndbModelQueries
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
	public void testRowByType()
	{
		LibTestModelQueries.testWordByType(model, "row", ps);
	}

	@Test
	public void testRowByPos()
	{
		LibTestModelQueries.testWordByType(model, "row", ps);
	}

	@Test
	public void testRowByTypeAndPronunciation()
	{
		LibTestModelQueries.testWordByTypeAndPronunciation(model, "row", ps);
	}

	@Test
	public void testRowByPosAndPronunciation()
	{
		LibTestModelQueries.testWordByTypeAndPronunciation(model, "row", ps);
	}


	@Test
	public void testCriticalByType()
	{
		LibTestModelQueries.testWordByType(model, "critical", ps);
	}

	@Test
	public void testCriticalByPos()
	{
		LibTestModelQueries.testWordByType(model, "critical", ps);
	}

	@Test
	public void testBassByPos()
	{
		LibTestModelQueries.testWordByType(model, "bass", ps);
	}

	@Test
	public void testBaroqueByPos()
	{
		LibTestModelQueries.testWordByType(model, "baroque", ps);
	}

	@Test
	public void testBaroqueCSByPos()
	{
		LibTestModelQueries.testWordByType(model, "Baroque", ps);
	}

	@Test
	public void testGaloreByPos()
	{
		LibTestModelQueries.testWordByType(model, "galore", ps);
	}
}
