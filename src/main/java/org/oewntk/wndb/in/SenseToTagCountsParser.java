/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.TagCount;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SenseToTagCountsParser
{
	private final File inDir;

	public SenseToTagCountsParser(final File inDir)
	{
		this.inDir = inDir;
	}

	public Map<String, TagCount> parse() throws IOException
	{
		Map<String, TagCount> result = new HashMap<>();
		parseTagCounts(new File(inDir, "cntlist.rev"), result);
		return Collections.unmodifiableMap(result);
	}

	public static void parseTagCounts(File file, Map<String, TagCount> map) throws IOException
	{
		// iterate on lines
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)))
		{
			long valueCount = 0;
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

					map.put(sensekey, new TagCount(senseNum, tagCnt));
					valueCount++;
				}
				catch (final RuntimeException e)
				{
					System.err.println("[E] at line " + lineCount + " " + e);
				}
			}
		}
	}
}
