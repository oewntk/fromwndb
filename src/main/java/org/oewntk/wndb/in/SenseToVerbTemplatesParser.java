/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import kotlin.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Sense-to-verb_templates parser
 */
public class SenseToVerbTemplatesParser
{
	private final File inDir;

	/**
	 * Constructor
	 *
	 * @param inDir extra WNDB dir
	 */
	public SenseToVerbTemplatesParser(final File inDir)
	{
		this.inDir = inDir;
	}

	/**
	 * Parse verb templates per sense
	 *
	 * @return collection of sensekey-verb_templates_ids pairs
	 * @throws IOException io exception
	 */
	public Collection<Pair<String, Integer[]>> parse() throws IOException
	{
		Collection<Pair<String, Integer[]>> result = new ArrayList<>();
		parseVerbTemplates(new File(inDir, "sentidx.vrb"), result);
		return result;
	}

	/**
	 * Parse verb templates per sense
	 *
	 * @param file    file
	 * @param entries accumulator of sensekey-verb_templates_ids pairs
	 * @throws IOException io exception
	 */
	private static void parseVerbTemplates(File file, Collection<Pair<String, Integer[]>> entries) throws IOException
	{
		// iterate on lines
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)))
		{
			int lineCount = 0;
			String line;
			while ((line = reader.readLine()) != null)
			{
				lineCount++;
				if (line.isEmpty() || line.charAt(0) == ' ')
				{
					continue;
				}

				try
				{
					String[] fields = line.split("[\\s,]+");
					String sensekey = fields[0];
					Integer[] templateIds = new Integer[fields.length - 1];
					for (int i = 1; i < fields.length; i++)
					{
						templateIds[i - 1] = Integer.parseInt(fields[i]);
					}
					entries.add(new Pair<>(sensekey, templateIds));
				}
				catch (final RuntimeException e)
				{
					Tracing.psErr.println("[E] verb templates at line " + lineCount + " " + e);
				}
			}
		}
	}
}
