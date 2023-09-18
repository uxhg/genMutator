package xyz.facta.jtools.genmutator.mut;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;

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
            String invokedFnClassName = invocation.getExecutable().getDeclaringType().getQualifiedName();
            String newName = FnNameMutator.renameFn(oldName, invokedFnClassName);
            // Replace the invoked method's name with the new name
            logger.debug("Rename invoked function: {}:{} -> {}", invokedFnClassName, oldName, newName);
            invocation.getExecutable().setSimpleName(newName);
        }
    }


}
