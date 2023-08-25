package xyz.facta.jtools.genmutator;

import spoon.Launcher;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import xyz.facta.jtools.genmutator.mut.AddIfMutator;
import xyz.facta.jtools.genmutator.mut.BinOpExprMutator;
import xyz.facta.jtools.genmutator.mut.VarRenameMutator;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.List;
import java.util.Random;

public class App {

    private static final Random RANDOM = new Random();
    private static final double PROCESSOR_APPLY_PROBABILITY = 0.9;

    public static void main(String[] args) {

        Options options = new Options();

        Option input = new Option("i", "input", true, "path of input source files");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output directory path");
        output.setRequired(true);
        options.addOption(output);

        Option cycles = new Option("n", "cycles", true, "number of output copies");
        cycles.setRequired(false);
        cycles.setType(Number.class);
        options.addOption(cycles);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("genMutator", options);
            System.exit(1);
            return;
        }

        String inputResourcePath = cmd.getOptionValue("input");
        String baseOutputDirectoryPath = cmd.getOptionValue("output");
        int numberOfCycles = 1;
        if (cmd.hasOption("cycles")) {
            numberOfCycles = Integer.parseInt(cmd.getOptionValue("cycles"));
        }

        List<Processor<?>> processors = List.of(new BinOpExprMutator(), new VarRenameMutator(), new AddIfMutator());
        for (int i = 1; i <= numberOfCycles; i++) {
            String currentOutputPath = baseOutputDirectoryPath + File.separator + i;
            processSourceCodeDir(inputResourcePath, currentOutputPath, processors);
        }
    }

    private static void processSourceCodeDir(String inPath, String outPath, List<Processor<?>> processors) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(inPath);

        // Add processors with prob
        for (Processor<?> processor : processors) {
            if (RANDOM.nextDouble() <= PROCESSOR_APPLY_PROBABILITY) {
                launcher.addProcessor(processor);
            }
        }

        launcher.getEnvironment().setSourceOutputDirectory(new File(outPath));
        CtModel model = launcher.buildModel();
        launcher.process();
        launcher.prettyprint();
    }
}
