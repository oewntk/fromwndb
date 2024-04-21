/*
 * Copyright (c) 2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.VerbTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Verb templates parser
 */
public class VerbTemplateParser
{
	private final File inDir;

	/**
	 * Constructor
	 *
	 * @param inDir extra WNDB dir
	 */
	public VerbTemplateParser(final File inDir)
	{
		this.inDir = inDir;
	}

	/**
	 * Parse verb templates
	 *
	 * @return collection of verb templates
	 * @throws IOException io exception
	 */
	public Collection<VerbTemplate> parse() throws IOException
	{
		Collection<VerbTemplate> result = new ArrayList<>();
		parseVerbTemplates(new File(inDir, "verbTemplates.txt"), result);
		return result;
	}

	/**
	 * Parse verb templates
	 *
	 * @param file          file
	 * @param verbTemplates accumulator of verb templates
	 * @throws IOException io exception
	 */
	private static void parseVerbTemplates(final File file, final Collection<VerbTemplate> verbTemplates) throws IOException
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
					verbTemplates.add(new VerbTemplate(id, field2));
				}
				catch (final RuntimeException e)
				{
					Tracing.psErr.println("[E] verb templates at line " + lineCount + " " + e);
				}
			}
		}
	}
}
