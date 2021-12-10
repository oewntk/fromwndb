/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SenseToVerbTemplatesParser
{
	private final File inDir;

	public SenseToVerbTemplatesParser(final File inDir)
	{
		this.inDir = inDir;
	}

	public Map<String, int[]> parse() throws IOException
	{
		Map<String, int[]> result = new HashMap<>();
		parseVerbTemplates(new File(inDir, "sentidx.vrb"), result);
		return Collections.unmodifiableMap(result);
	}

	public static void parseVerbTemplates(File file, Map<String, int[]> map) throws IOException
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
					String[] fields = line.split("[\\s,]+");
					String sensekey = fields[0];
					int[] templateIds = new int[fields.length - 1];
					for (int i = 1; i < fields.length; i++)
					{
						templateIds[i - 1] = Integer.parseInt(fields[i]);
					}
					map.put(sensekey, templateIds);
					valueCount++;
				}
				catch (final RuntimeException e)
				{
					System.err.println("[E] verb templates at line " + lineCount + " " + e);
				}
			}
		}
	}
}
