package xyz.facta.jtools.genmutator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.code.*;
import xyz.facta.jtools.genmutator.mut.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);
    private static final Random RANDOM = new Random();
    //private static final double PROCESSOR_APPLY_PROBABILITY = 0.9;

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

        Option startNum = new Option( "startNumber", true, "start number in output file names");
        startNum.setRequired(false);
        startNum.setType(Number.class);
        options.addOption(startNum);

        Option cfgFile = new Option("c", "config", true, "path to the configuration file");
        cycles.setRequired(true);
        options.addOption(cfgFile);

        //Option binOpMutOption = new Option("mbo", "mut-bin-op-expr", false, "Enable binary operator mutator");
        //options.addOption(binOpMutOption);

        //Option renameVariableOption = new Option("mrv", "mut-rename-variable", false, "Enable variable renaming mutator");
        //options.addOption(renameVariableOption);

        //Option addIfStructOption = new Option("mai", "mut-add-if", false, "Enable adding if structure mutator");
        //options.addOption(addIfStructOption);

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


        //List<Processor<?>> processors = getProcessors(cmd);
        Map<String, AbstractProcessor<?>> processors;
        try {
            processors = readConfig(cmd.getOptionValue("config"));
        } catch (IOException e) {
            logger.error("Cannot read configuration file @{}", cmd.getOptionValue("cfg"));
            throw new RuntimeException(e);
        }
        if (processors.isEmpty()) {
            logger.error("No processors added, please enable at least one processor, see help for details");
            return;
        }

        for (int i = 0; i < numberOfCycles; i++) {
            int subDirNumber = i + Integer.parseInt(cmd.getOptionValue("startNumber", "1"));
            String currentOutputPath = baseOutputDirectoryPath + File.separator + subDirNumber;
            processSourceCodeDir(inputResourcePath, currentOutputPath, processors);
        }
    }


    /**
     * Load and parse the configuration JSON
     */
    private static Map<String, AbstractProcessor<?>> readConfig(String configFileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(configFileName));

        // Initialize processors based on configuration
        return initializeProcessors(rootNode);
    }

    private static List<Processor<?>> getProcessors(CommandLine cmd) {
        List<Processor<?>> processors = new ArrayList<>();
        if (cmd.hasOption("mbo")) {
            processors.add(new BinOpExprMutator());
        }
        if (cmd.hasOption("mrv")) {
            processors.add(new VarNameMutator());
        }

        if (cmd.hasOption("mai")) {
            processors.add(new AddIfMutator());
        }
        return processors;
    }

    private static void processSourceCodeDir(String inPath, String outPath, Map<String, AbstractProcessor<?>> processors) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(inPath);

        //launcher.addProcessor(new EncapsulateClassProcessor());
        // Add processors with prob
        for (Processor<?> processor : processors.values()) {
            //if (RANDOM.nextDouble() <= PROCESSOR_APPLY_PROBABILITY) {
            launcher.addProcessor(processor);
            //}
        }

        launcher.getEnvironment().setSourceOutputDirectory(new File(outPath));
        CtModel model = launcher.buildModel();

        //Map<CtStatement, Integer> originalLineNumbers = new HashMap<>();
        // Iterate through classes and capture line numbers
        //for (CtClass clazz : model.getRootPackage().getElements(new TypeFilter<>(CtClass.class))) {
        //    List<CtStatement> statements = clazz.getElements(new LineFilter());

        //    for (CtStatement statement : statements) {
        //        // Store the original line number of each statement
        //        originalLineNumbers.put(statement, statement.getPosition().getLine());
        //    }
        //}
        launcher.process();
        launcher.prettyprint();
    }


    private static Map<String, AbstractProcessor<?>> initializeProcessors(JsonNode configNode) {
        Map<String, AbstractProcessor<?>> processors = new HashMap<>();

        // Initialize the VarNameMutator if enabled
        if (configNode.has("VarRename") && configNode.get("VarRename").has("enabled") &&
            configNode.get("VarRename").get("enabled").asBoolean()) {
            double probability = configNode.get("VarRename").get("prob").asDouble();
            VarNameMutator varRenameProcessor = new VarNameMutator(probability);
            processors.put("VarRename", varRenameProcessor);
        }

        // Initialize the BinOpExprMutator if enabled
        if (configNode.has("BinOp") && configNode.get("BinOp").has("enabled") &&
            configNode.get("BinOp").get("enabled").asBoolean()) {
            double probability = configNode.get("BinOp").get("prob").asDouble();
            JsonNode binOpCategoryNode = configNode.get("BinOp").get("category");
            Set<BinOpExprMutator.BinOpCategory> categories = new HashSet<>();
            if (binOpCategoryNode.has("logical") && binOpCategoryNode.get("logical").asBoolean()) {
                categories.add(BinOpExprMutator.BinOpCategory.LOGICAL);
            }
            if (binOpCategoryNode.has("comparison") && binOpCategoryNode.get("comparison").asBoolean()) {
                categories.add(BinOpExprMutator.BinOpCategory.COMPARISON);
            }
            if (binOpCategoryNode.has("arithmetic") && binOpCategoryNode.get("arithmetic").asBoolean()) {
                categories.add(BinOpExprMutator.BinOpCategory.ARITHMETIC);
            }
            BinOpExprMutator binOpProcessor = new BinOpExprMutator(probability, categories);
            processors.put("BinOp", binOpProcessor);
        }

        // Initialize the AddIfProcessor if enabled
        if (configNode.has("AddIf") && configNode.get("AddIf").has("enabled") &&
            configNode.get("AddIf").get("enabled").asBoolean()) {
            double probability = configNode.get("AddIf").get("prob").asDouble();
            AddIfMutator addIfProcessor = new AddIfMutator(probability);
            processors.put("AddIf", addIfProcessor);
        }

        // Initialize the RandomDeletionProcessor if enabled
        if (configNode.has("Deletion") && configNode.get("Deletion").has("enabled") &&
            configNode.get("Deletion").get("enabled").asBoolean()) {
            JsonNode deletionGlobalConfigNode = configNode.get("Deletion").get("global");
            //double probability = deletionGlobalConfigNode.get("prob").asDouble();
            DeletionMutator.Distribution distribution = DeletionMutator.Distribution.valueOf(
                deletionGlobalConfigNode.get("distribution").asText().toUpperCase()
            );
            Set<Integer> linesNotToTouch = extractLinesNotToTouch(deletionGlobalConfigNode);
            Set<Class<? extends CtStatement>> stmtTypesNotToTouch = extractStatementTypes(deletionGlobalConfigNode);
            int minNumOfLinesToDelete = deletionGlobalConfigNode.get("minNumOfLinesToDelete").asInt();
            int maxNumOfLinesToDelete = deletionGlobalConfigNode.get("maxNumOfLinesToDelete").asInt();

            if (configNode.get("Deletion").get("enabled").asBoolean()) {
                processors.put("Deletion", new DeletionMutator(stmtTypesNotToTouch, linesNotToTouch, minNumOfLinesToDelete, maxNumOfLinesToDelete, distribution));
            }
        }

        // Initialize the FnNameMutator if enabled
        if (configNode.has("FnRename") && configNode.get("FnRename").has("enabled") &&
            configNode.get("FnRename").get("enabled").asBoolean()) {
            double probability = configNode.get("FnRename").get("prob").asDouble();
            Pattern pattern = Pattern.compile(configNode.get("FnRename").get("pattern").asText());
            FnNameMutator fnNameMutator = new FnNameMutator(pattern, probability);
            processors.put("FnRename", fnNameMutator);
        }
        // Initialize the InvocationMutator if enabled
        if (configNode.has("InvocationRename") && configNode.get("InvocationRename").has("enabled") &&
            configNode.get("InvocationRename").get("enabled").asBoolean()) {
            double probability = configNode.get("InvocationRename").get("prob").asDouble();
            InvocationMutator invocationMutator = new InvocationMutator(probability);
            processors.put("InvocationRename", invocationMutator);
        }

        // Initialize the TypeRefMutator if enabled
        if (configNode.has("TypeRefMutate") && configNode.get("TypeRefMutate").has("enabled") &&
            configNode.get("TypeRefMutate").get("enabled").asBoolean()) {
            double probability = configNode.get("TypeRefMutate").get("prob").asDouble();
            TypeRefMutator typeRefMutator = new TypeRefMutator(probability);
            processors.put("TypeRefMutate", typeRefMutator);
        }
        return processors;
    }


    public static Set<Integer> extractLinesNotToTouch(JsonNode configNode) {
        Set<Integer> linesNotToTouch = new HashSet<>();
        JsonNode linesNode = configNode.get("linesNotToTouch");
        if (linesNode != null && linesNode.isArray()) {
            for (JsonNode line : linesNode) {
                linesNotToTouch.add(line.asInt());
            }
        }
        return linesNotToTouch;
    }

    public static Set<Class<? extends CtStatement>> extractStatementTypes(JsonNode configNode) {
        Set<Class<? extends CtStatement>> statementTypes = new HashSet<>();
        JsonNode typesNode = configNode.get("typesNotToTouch");
        if (typesNode != null && typesNode.isArray()) {
            for (JsonNode type : typesNode) {
                String typeName = type.asText();
                switch (typeName) {
                    case "invocation":
                        statementTypes.add(CtInvocation.class);
                        break;
                    case "return":
                        statementTypes.add(CtReturn.class);
                        break;
                    case "assignment":
                        statementTypes.add(CtAssignment.class);
                        break;
                    case "if":
                        statementTypes.add(CtIf.class);
                }
            }
        }
        return statementTypes;
    }

}
