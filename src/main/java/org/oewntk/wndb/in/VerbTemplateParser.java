/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.VerbTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VerbTemplateParser
{
	private final File inDir;

	public VerbTemplateParser(final File inDir)
	{
		this.inDir = inDir;
	}

	public Map<Integer, VerbTemplate> parse() throws IOException
	{
		Map<Integer, VerbTemplate> result = new HashMap<>();
		parseVerbTemplates(new File(inDir, "verbTemplates.txt"), result);
		return Collections.unmodifiableMap(result);
	}

	private static void parseVerbTemplates(File file, Map<Integer, VerbTemplate> map) throws IOException
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
					String[] fields = line.split(":");
					String field1 = fields[0];
					String field2 = fields[1].trim();
					int id = Integer.parseInt(field1);
					map.put(id, new VerbTemplate(id, field2));
				}
				catch (final RuntimeException e)
				{
					System.err.println("[E] verb templates at line " + lineCount + " " + e);
				}
			}
		}
	}
}
