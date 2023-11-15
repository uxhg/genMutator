package xyz.facta.jtools.genmutator.mut;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import xyz.facta.jtools.genmutator.util.NameGenerator;

import java.util.*;
import java.util.regex.Pattern;

public class FnNameMutator extends GMAbstractMutator<CtMethod<?>> {
    //AbstractProcessor<CtMethod<?>> {
    private static final Logger logger = LogManager.getLogger(FnNameMutator.class);
    private final Random random = new Random();
    private final Pattern patternToMatch;
    private final double MUTATION_PROBABILITY;
    //private final NameGenerator nameGenerator;
    private static final HashMap<String, String> changedNames = new HashMap<>();

    private Set<String> namesChosenToMutate = new HashSet<>();

    public FnNameMutator(Pattern patternToMatch, double prob) {
        this.patternToMatch = patternToMatch;
        this.MUTATION_PROBABILITY = prob;
        // this.nameGenerator = nameGenerator;
    }

    public void setNamesChosenToMutate(Set<String> namesChosenToMutate) {
        this.namesChosenToMutate = namesChosenToMutate;
    }

    @Override
    public void reset() {
        changedNames.clear();
    }

    public static String renameFn(String fnName, String className) {
        //String oldNameAsKey = className + ":" + fnName;
        String oldNameAsKey = fnName;
        logger.debug("[to rename function] {}", oldNameAsKey);
        if (changedNames.containsKey(oldNameAsKey)) {
            return changedNames.get(oldNameAsKey);
        }
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

        String newName = NameGenerator.generateName(-1, 0.5, NameGenerator.NameCategory.FuncName);
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
        changedNames.put(oldNameAsKey, newName);
        return newName;
    }


    @Override
    public void process(CtMethod<?> method) {
        logger.debug("known function names: {}", namesChosenToMutate);
        // Check if the method name matches the specified pattern
        if (shouldMutate() && patternToMatch.matcher(method.getSimpleName()).matches()) {
            // Generate a new name using the shared NameGenerator
            String newName = renameFn(method.getSimpleName(), method.getDeclaringType().getQualifiedName());
            logger.debug("[rename function] {} -> {}", method.getSimpleName(), newName);
            //String newName = NameGenerator.generateName(-1, 0.5);

            // Create a new method by copying the original method
            CtMethod<?> mutatedMethod = getFactory().Method().create(
                method.getDeclaringType(),
                method,
                true // redirect references to the target type
            );
            mutatedMethod.setSimpleName(newName);

            // Remove the original method from the declaring type
            method.getDeclaringType().removeMethod(method);

            // Add the mutated method to the declaring type
            method.getDeclaringType().addMethod(mutatedMethod);
        }
    }

    private boolean shouldMutate() {
        // return random.nextDouble() <= MUTATION_PROBABILITY;
        return true;
    }


}
