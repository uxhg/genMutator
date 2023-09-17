package xyz.facta.jtools.genmutator.mut;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import xyz.facta.jtools.genmutator.util.NameGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InvocationMutator extends AbstractProcessor<CtInvocation<?>> {
    private static final Logger logger = LogManager.getLogger(InvocationMutator.class);
    private final Random random = new Random();
    //private final Pattern patternToMatch;
    private final double MUTATION_PROBABILITY;
    //private final NameGenerator nameGenerator;

    public InvocationMutator(double prob) {
        this.MUTATION_PROBABILITY = prob;
    }

    @Override
    public void process(CtInvocation<?> invocation) {
        // Check if the invoked method is a method, and optionally apply a random mutation
        if (invocation.getExecutable() != null && random.nextDouble() < MUTATION_PROBABILITY) {
            String oldName = invocation.getExecutable().getSimpleName();
            // Generate a new name using your VarNameGenerator
            String newName = renameFn(oldName);
            // Replace the invoked method's name with the new name
            logger.debug("Rename {} to {}", oldName, newName);
            invocation.getExecutable().setSimpleName(newName);
        }
    }

    private static String renameFn(String fnName) {
        List<String> optionalKeywords = Arrays.asList("isPresent", "hasValue", "isNotEmpty", "hasData", "isSet", "exist", "exists", "isNotBlank");
        if (optionalKeywords.contains(fnName)) {
            int randomIndex = (int) (Math.random() * optionalKeywords.size());
            return optionalKeywords.get(randomIndex);
        }

        List<String> optionalNotKeywords = Arrays.asList("isNone", "notExist", "isEmpty", "isNull", "isMissing", "isBlank");
        if (optionalNotKeywords.contains(fnName)) {
            int randomIndex = (int) (Math.random() * optionalNotKeywords.size());
            return optionalNotKeywords.get(randomIndex);
        }

        String newName = NameGenerator.generateName(-1, 0.5);
        String[] fetchKeywords = {"find", "get", "search", "query", "select", "lookUp", "fetch", "retrieve"};

        String selectedKeyword = fetchKeywords[(int) (Math.random() * fetchKeywords.length)];
        // Randomly decide whether to add "By" or not
        if (Math.random() < 0.5) {
            selectedKeyword = selectedKeyword + "By";
        }

        // Iterate through the list of keywords and check if the function name starts with any of them
        for (String keyword : fetchKeywords) {
            if (fnName.startsWith(keyword)) {
                newName = selectedKeyword + NameGenerator.capitalize(newName);
                break; // Exit the loop if a match is found
            }
        }
        return newName;
    }


}
