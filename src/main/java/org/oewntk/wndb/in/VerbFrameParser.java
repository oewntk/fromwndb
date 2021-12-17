/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.VerbFrame;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class VerbFrameParser
{
	private final File inDir;

	public VerbFrameParser(final File inDir)
	{
		this.inDir = inDir;
	}

	public Collection<VerbFrame> parse() throws IOException
	{
		Collection<VerbFrame> result = new ArrayList<>();
		parseVerbFrames(new File(inDir, "verbFrames.txt"), result);
		return result;
	}

	private void parseVerbFrames(final File file, final Collection<VerbFrame> verbFrames) throws IOException
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
					verbFrames.add(new VerbFrame(field1, field2));
				}
				catch (final RuntimeException e)
				{
					Tracing.psErr.println("[E] verb frame at line " + lineCount + " " + e);
				}
			}
		}
	}
}
