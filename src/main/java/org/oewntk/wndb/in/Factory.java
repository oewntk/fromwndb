/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.*;
import org.oewntk.pojos.ParsePojoException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Supplier;

public class Factory implements Supplier<Model>
{
	private final File inDir;

	private final File inDir2;

	public Factory(final File inDir, final File inDir2)
	{
		this.inDir = inDir;
		this.inDir2 = inDir2;
	}

	@Override
	public Model get()
	{
		CoreModel coreModel = new CoreFactory(inDir).get();
		if (coreModel == null)
		{
			return null;
		}

		try
		{
			// verb frames and templates
			Collection<VerbFrame> verbFramesById = new VerbFrameParser(inDir2).parse();
			Collection<VerbTemplate> verbTemplatesById = new VerbTemplateParser(inDir2).parse();
			Collection<Entry<String, int[]>> senseToVerbTemplates = new SenseToVerbTemplatesParser(inDir).parse();

			// tag counts
			Collection<Entry<String, TagCount>> senseToTagCounts = new SenseToTagCountsParser(inDir).parse();

			return new Model(coreModel, verbFramesById, verbTemplatesById, senseToVerbTemplates, senseToTagCounts).setSources(inDir, inDir2);
		}
		catch (IOException e)
		{
			e.printStackTrace(Tracing.psErr);
			return null;
		}
	}

	static public Model makeModel(String[] args) throws IOException, ParsePojoException
	{
		File inDir = new File(args[0]);
		File inDir2 = new File(args[1]);
		return new Factory(inDir, inDir2).get();
	}

	static public void main(String[] args) throws IOException, ParsePojoException
	{
		Model model = makeModel(args);
		Tracing.psInfo.printf("[Model] %s\n%s\n%s%n", Arrays.toString(model.getSources()), model.info(), model.counts());
	}
}