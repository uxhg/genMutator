package xyz.facta.jtools.genmutator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.Launcher;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import xyz.facta.jtools.genmutator.mut.AddIfMutator;
import xyz.facta.jtools.genmutator.mut.BinOpExprMutator;
import xyz.facta.jtools.genmutator.mut.VarRenameMutator;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);
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


        Option binOpMutOption = new Option("mbo", "mut-bin-op-expr", false, "Enable binary operator mutator");
        options.addOption(binOpMutOption);

        Option renameVariableOption = new Option("mrv", "mut-rename-variable", false, "Enable variable renaming mutator");
        options.addOption(renameVariableOption);

        Option addIfStructOption = new Option("mai", "mut-add-if", false, "Enable adding if structure mutator");
        options.addOption(addIfStructOption);

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


        List<Processor<?>> processors =  getProcessors(cmd);
        if (processors.isEmpty()){
            logger.error("No processors added, please enable at least one processor, see help for details");
            return;
        }

        for (int i = 1; i <= numberOfCycles; i++) {
            String currentOutputPath = baseOutputDirectoryPath + File.separator + i;
            processSourceCodeDir(inputResourcePath, currentOutputPath, processors);
        }
    }

    private static List<Processor<?>> getProcessors(CommandLine cmd) {
        List<Processor<?>> processors = new ArrayList<>();
        if (cmd.hasOption("mbo")) {
            processors.add(new BinOpExprMutator());
        }
        if (cmd.hasOption("mrv")) {
            processors.add(new VarRenameMutator());
        }

        if (cmd.hasOption("mai")) {
            processors.add(new AddIfMutator());
        }
        return processors;
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
