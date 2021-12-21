/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import org.oewntk.model.CoreModel;
import org.oewntk.pojos.ParsePojoException;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class CoreFactory implements Supplier<CoreModel>
{
	private final File inDir;

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
					.setSource(inDir);
		}
		catch (IOException | ParsePojoException e)
		{
			e.printStackTrace(Tracing.psErr);
			return null;
		}
	}

	static public CoreModel makeCoreModel(String[] args) throws IOException, ParsePojoException
	{
		File inDir = new File(args[0]);
		return new CoreFactory(inDir).get();
	}

	static public void main(String[] args) throws IOException, ParsePojoException
	{
		CoreModel model = makeCoreModel(args);
		Tracing.psInfo.printf("[CoreModel] %s\n%s\n%s%n", model.getSource(), model.info(), model.counts());
	}
}