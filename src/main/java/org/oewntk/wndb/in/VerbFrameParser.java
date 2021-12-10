/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.VerbFrame;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VerbFrameParser
{
	private final File inDir;

	public VerbFrameParser(final File inDir)
	{
		this.inDir = inDir;
	}

	public Map<String, VerbFrame> parse() throws IOException
	{
		Map<String, VerbFrame> result = new HashMap<>();
		parseVerbFrames(new File(inDir, "verbFrames.txt"), result);
		return Collections.unmodifiableMap(result);
	}

	private void parseVerbFrames(final File file, final Map<String, VerbFrame> map) throws IOException
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
					map.put(field1, new VerbFrame(field1, field2));
				}
				catch (final RuntimeException e)
				{
					System.err.println("[E] verb frame at line " + lineCount + " " + e);
				}
			}
		}
	}
}
