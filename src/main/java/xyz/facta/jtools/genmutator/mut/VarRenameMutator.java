package xyz.facta.jtools.genmutator.mut;

import spoon.processing.AbstractProcessor;
import spoon.refactoring.CtRenameGenericVariableRefactoring;
import spoon.reflect.declaration.CtVariable;
import xyz.facta.jtools.genmutator.util.VarNameGenerator;

public class VarRenameMutator extends AbstractProcessor<CtVariable> {


    @Override
    public void process(CtVariable ctVar) {
        String newName = VarNameGenerator.generateVariableName();
        new CtRenameGenericVariableRefactoring().setTarget(ctVar).setNewName(newName).refactor();
    }
}
