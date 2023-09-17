package xyz.facta.jtools.genmutator.mut;

import spoon.processing.AbstractProcessor;
import spoon.refactoring.CtRenameGenericVariableRefactoring;
import spoon.reflect.declaration.CtVariable;
import xyz.facta.jtools.genmutator.util.NameGenerator;

import java.util.Random;

public class VarNameMutator extends AbstractProcessor<CtVariable> {

    private final double MUTATION_PROBABILITY;
    private final Random random = new Random();

    public VarNameMutator(double mutationProbability) {
        MUTATION_PROBABILITY = mutationProbability;
    }

    public VarNameMutator() {
        this(0.5);
    }

    @Override
    public void process(CtVariable ctVar) {
        if (shouldMutate()) {
            String newName = NameGenerator.generateName(0.4, 0.6);
            new CtRenameGenericVariableRefactoring().setTarget(ctVar).setNewName(newName).refactor();
        }
    }

    private boolean shouldMutate() {
        return random.nextDouble() <= MUTATION_PROBABILITY;
    }

}
