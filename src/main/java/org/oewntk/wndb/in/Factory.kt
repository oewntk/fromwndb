/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import kotlin.Pair;
import org.oewntk.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * Model factory
 */
public class Factory implements Supplier<Model>
{
    private final File inDir;

    private final File inDir2;

    /**
     * Constructor
     *
     * @param inDir  WNDB dir
     * @param inDir2 extra WNDB dir
     */
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
            Collection<Pair<String, Integer[]>> senseToVerbTemplates = new SenseToVerbTemplatesParser(inDir).parse();

            // tag counts
            Collection<Pair<String, TagCount>> senseToTagCounts = new SenseToTagCountsParser(inDir).parse();

            return new Model(coreModel, verbFramesById, verbTemplatesById, senseToVerbTemplates, senseToTagCounts).setSources(inDir, inDir2);
        }
        catch (IOException e)
        {
            e.printStackTrace(Tracing.psErr);
            return null;
        }
    }

    /**
     * Make model
     *
     * @param inDir  WNDB dir
     * @param inDir2 extra WNDB dir
     * @return model
     */
    static public Model makeModel(File inDir, File inDir2)
    {
        return new Factory(inDir, inDir2).get();
    }

    /**
     * Make model
     *
     * @param dirPath1 WNDB dir path
     * @param dirPath2 extra WNDB dir path
     * @return core model
     */
    static public Model makeModel(final String dirPath1, final String dirPath2)
    {
        File inDir = new File(dirPath1);
        File inDir2 = new File(dirPath2);
        return new Factory(inDir, inDir2).get();
    }

    /**
     * Make model
     *
     * @param args command-line arguments
     * @return core model
     */
    static public Model makeModel(String[] args)
    {
        File inDir = new File(args[0]);
        File inDir2 = new File(args[1]);
        return makeModel(inDir, inDir2);
    }

    /**
     * Main
     *
     * @param args command-line arguments
     */
    static public void main(String[] args)
    {
        String dirPath2 = args[args.length - 1]; // last
        for (int i = 0; i < args.length - 1; i++)
        {
            Model model = makeModel(args[i], dirPath2);
            Tracing.psInfo.printf("[Model] %s%n%s%n%s%n", Arrays.toString(model.getSources()), model.info(), model.counts());
        }
    }
}