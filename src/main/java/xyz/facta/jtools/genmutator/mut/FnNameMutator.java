package xyz.facta.jtools.genmutator.mut;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import xyz.facta.jtools.genmutator.util.NameGenerator;

import java.util.Random;
import java.util.regex.Pattern;

public class FnNameMutator extends AbstractProcessor<CtMethod<?>> {
    private final Random random = new Random();
    private final Pattern patternToMatch;
    private final double MUTATION_PROBABILITY;
    //private final NameGenerator nameGenerator;

    public FnNameMutator(Pattern patternToMatch, double prob) {
        this.patternToMatch = patternToMatch;
        this.MUTATION_PROBABILITY = prob;
        // this.nameGenerator = nameGenerator;
    }


    @Override
    public void process(CtMethod<?> method) {
        // Check if the method name matches the specified pattern
        if (shouldMutate() && patternToMatch.matcher(method.getSimpleName()).matches()) {
            // Generate a new name using the shared NameGenerator
            String newName = NameGenerator.generateName(false, true);

            // Create a new method by copying the original method
            CtMethod<?> mutatedMethod = getFactory().Method().create(
                method.getDeclaringType(),
                method,
                true // redirect references to the target type
            );

            // Rename the newly created method
            mutatedMethod.setSimpleName(newName);

            // Remove the original method from the declaring type
            method.getDeclaringType().removeMethod(method);

            // Add the mutated method to the declaring type
            method.getDeclaringType().addMethod(mutatedMethod);
        }
    }

    private boolean shouldMutate() {
        return random.nextDouble() <= MUTATION_PROBABILITY;
    }


}
