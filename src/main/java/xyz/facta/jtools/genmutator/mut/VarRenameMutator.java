package xyz.facta.jtools.genmutator.mut;

import spoon.processing.AbstractProcessor;
import spoon.refactoring.CtRenameGenericVariableRefactoring;
import spoon.reflect.declaration.CtVariable;
import xyz.facta.jtools.genmutator.util.VarNameGenerator;

import java.util.Random;

public class VarRenameMutator extends AbstractProcessor<CtVariable> {

    private static final float MUTATION_PROBABILITY = 0.5f;  // 50% chance
    private final Random random = new Random();

    @Override
    public void process(CtVariable ctVar) {
        if (shouldMutate()) {
            String newName = VarNameGenerator.generateVariableName();
            new CtRenameGenericVariableRefactoring().setTarget(ctVar).setNewName(newName).refactor();
        }
    }

    private boolean shouldMutate() {
        return random.nextFloat() <= MUTATION_PROBABILITY;
    }

}
