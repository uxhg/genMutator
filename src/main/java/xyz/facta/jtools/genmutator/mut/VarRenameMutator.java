package xyz.facta.jtools.genmutator.mut;

import spoon.processing.AbstractProcessor;
import spoon.refactoring.CtRenameGenericVariableRefactoring;
import spoon.reflect.declaration.CtVariable;
import xyz.facta.jtools.genmutator.util.VarNameGenerator;

import java.util.Random;

public class VarRenameMutator extends AbstractProcessor<CtVariable> {

    private final double MUTATION_PROBABILITY;
    private final Random random = new Random();

    public VarRenameMutator(double mutationProbability) {
        MUTATION_PROBABILITY = mutationProbability;
    }

    public VarRenameMutator() {
        this(0.5);
    }

    @Override
    public void process(CtVariable ctVar) {
        if (shouldMutate()) {
            String newName = VarNameGenerator.generateVariableName();
            new CtRenameGenericVariableRefactoring().setTarget(ctVar).setNewName(newName).refactor();
        }
    }

    private boolean shouldMutate() {
        return random.nextDouble() <= MUTATION_PROBABILITY;
    }

}
