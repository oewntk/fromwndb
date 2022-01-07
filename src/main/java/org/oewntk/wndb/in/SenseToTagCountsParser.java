/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.TagCount;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

/**
 * Sense-to-tag_count parser
 */
public class SenseToTagCountsParser
{
	private final File inDir;

	/**
	 * Constructor
	 *
	 * @param inDir extra WNDB dir
	 */
	public SenseToTagCountsParser(final File inDir)
	{
		this.inDir = inDir;
	}

	/**
	 * Parse tag counts per sense
	 *
	 * @return collection of sensekey-tag_count pairs
	 * @throws IOException io exception
	 */
	public Collection<Entry<String, TagCount>> parse() throws IOException
	{
		Collection<Entry<String, TagCount>> result = new ArrayList<>();
		parseTagCounts(new File(inDir, "cntlist.rev"), result);
		return result;
	}

	/**
	 * Parse tag counts per sense
	 *
	 * @param file    file
	 * @param entries sensekey-tag_count pairs accumulator
	 * @throws IOException io exception
	 */
	private static void parseTagCounts(File file, Collection<Entry<String, TagCount>> entries) throws IOException
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
					String[] fields = line.split("\\s+");
					String sensekey = fields[0];
					// int sensenum = Integer.parseInt(fields[1]);
					int senseNum = Integer.parseInt(fields[1]);
					int tagCnt = Integer.parseInt(fields[2]);

					entries.add(new SimpleEntry<>(sensekey, new TagCount(senseNum, tagCnt)));
				}
				catch (final RuntimeException e)
				{
					Tracing.psErr.println("[E] at line " + lineCount + " " + e);
				}
			}
		}
	}
}
