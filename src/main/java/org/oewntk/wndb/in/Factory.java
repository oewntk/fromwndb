/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.oewntk.pojos.ParsePojoException;

public class Factory
{
	static public CoreModel makeCoreModel(File inDir) throws IOException, ParsePojoException
	{
		Parser parser = new Parser(inDir);
		CoreModel model = parser.parseAll();
		System.err.println("[Model] synsets");

		return model.setSource(inDir);
	}

	static public Model makeModel(File inDir, File inDir2) throws IOException, ParsePojoException
	{
		CoreModel coreModel = makeCoreModel(inDir).setSource(inDir);

		// verb frames and templates
		Map<String, VerbFrame> verbFramesById = new VerbFrameParser(inDir2).parse();
		Map<Integer, VerbTemplate> verbTemplatesById = new VerbTemplateParser(inDir2).parse();
		Map<String, int[]> senseToVerbTemplates = new SenseToVerbTemplatesParser(inDir).parse();
		System.err.println("[Model] verb frames and templates");

		// tag counts
		Map<String, TagCount> senseToTagCounts = new SenseToTagCountsParser(inDir).parse();
		System.err.println("[Model] tag counts");

		return new Model(coreModel, verbFramesById, verbTemplatesById, senseToVerbTemplates, senseToTagCounts).setSources(inDir, inDir2);
	}

	static public CoreModel makeCoreModel(String[] args) throws IOException, ParsePojoException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Heap
		boolean traceHeap = true;
		String traceHeapEnv = System.getenv("TRACEHEAP");
		if (traceHeapEnv != null)
		{
			traceHeap = Boolean.parseBoolean(traceHeapEnv);
		}
		if (traceHeap)
		{
			System.err.println(Memory.heapInfo("before maps,", Memory.Unit.M));
		}

		// Args
		File inDir = new File(args[0]);

		// Make
		CoreModel model = makeCoreModel(inDir);

		// Heap
		if (traceHeap)
		{
			System.gc();
			System.err.println(Memory.heapInfo("after maps,", Memory.Unit.M));
		}

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("[Time] " + (endTime - startTime) / 1000 + "s");

		return model;
	}

	static public Model makeModel(String[] args) throws IOException, ParsePojoException
	{
		// Timing
		final long startTime = System.currentTimeMillis();

		// Heap
		boolean traceHeap = true;
		String traceHeapEnv = System.getenv("TRACEHEAP");
		if (traceHeapEnv != null)
		{
			traceHeap = Boolean.parseBoolean(traceHeapEnv);
		}
		if (traceHeap)
		{
			System.err.println(Memory.heapInfo("before maps,", Memory.Unit.M));
		}

		// Args
		File inDir = new File(args[0]);
		File inDir2 = new File(args[1]);

		// Make
		Model model = makeModel(inDir, inDir2);

		// Heap
		if (traceHeap)
		{
			System.gc();
			System.err.println(Memory.heapInfo("after maps,", Memory.Unit.M));
		}

		// Timing
		final long endTime = System.currentTimeMillis();
		System.err.println("[Time] " + (endTime - startTime) / 1000 + "s");

		return model;
	}

	static public void main(String[] args) throws IOException, ParsePojoException
	{
		Model model = makeModel(args);
		System.err.printf("model %s\n%s\n%s%n", Arrays.toString(model.getSources()), model.info(), model.counts());
	}
}