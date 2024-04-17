/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.CoreModel;
import org.oewntk.pojos.ParsePojoException;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * Core model factory
 */
public class CoreFactory implements Supplier<CoreModel>
{
	private final File inDir;

	/**
	 * Constructor
	 *
	 * @param inDir WNDB dir
	 */
	public CoreFactory(final File inDir)
	{
		this.inDir = inDir;
	}

	@Override
	public CoreModel get()
	{
		try
		{
			return new Parser(inDir) //
					.parseCoreModel() //
					.generateInverseRelations() //
					.setModelSource(inDir);
		}
		catch (IOException | ParsePojoException e)
		{
			e.printStackTrace(Tracing.psErr);
			return null;
		}
	}

	/**
	 * Make core model
	 *
	 * @param dirPath WNDB dir path
	 * @return core model
	 */
	static public CoreModel makeCoreModel(String dirPath)
	{
		File inDir = new File(dirPath);
		return new CoreFactory(inDir).get();
	}

	/**
	 * Main
	 *
	 * @param args command-line arguments
	 */
	static public void main(String[] args)
	{
		for (String arg : args)
		{
			CoreModel model = makeCoreModel(arg);
			Tracing.psInfo.printf("[CoreModel] %s%n%s%n%s%n", model.getSource(), model.info(), model.counts());
		}
	}
}