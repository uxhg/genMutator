package xyz.facta.jtools.genmutator.mut;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class InvocationMutator extends GMAbstractMutator<CtInvocation<?>> {
    private static final Logger logger = LogManager.getLogger(InvocationMutator.class);
    private final Random random = new Random();
    //private final Pattern patternToMatch;
    private final double MUTATION_PROBABILITY;
    //private final NameGenerator nameGenerator;

    private Set<String> namesChosenToMutate = new HashSet<>();

    public InvocationMutator(double prob) {
        this.MUTATION_PROBABILITY = prob;
    }

    public void setNamesChosenToMutate(Set<String> namesChosenToMutate) {
        this.namesChosenToMutate = namesChosenToMutate;
    }

    @Override
    public void process(CtInvocation<?> invocation) {
        // Check if the invoked method is a method, and optionally apply a random mutation
        //if (invocation.getExecutable() != null && random.nextDouble() < MUTATION_PROBABILITY) {
        if (invocation.getExecutable() != null) {
            String oldName = invocation.getExecutable().getSimpleName();
            // Generate a new name using your VarNameGenerator
            logger.debug("[invocation] {}, {}", invocation.getExecutable(), invocation.getExecutable().getSimpleName());
            String invokedFnClassName = getInvocationClassName(invocation);
            String newName = FnNameMutator.renameFn(oldName, invokedFnClassName);
            // Replace the invoked method's name with the new name
            logger.debug("[rename invoked function] {}:{} -> {}", invokedFnClassName, oldName, newName);
            invocation.getExecutable().setSimpleName(newName);
        }
    }

    private String getInvocationClassName(CtInvocation<?> invocation) {
        CtTypeReference<?> declaringType = invocation.getExecutable().getDeclaringType();
        if (declaringType != null) {
            return declaringType.getQualifiedName();
        } else {
            return "";
        }
    }


    @Override
    public void reset() {
    }
}
