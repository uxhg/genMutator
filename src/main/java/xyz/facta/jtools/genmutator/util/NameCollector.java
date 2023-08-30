package xyz.facta.jtools.genmutator.util;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;

import java.util.HashSet;
import java.util.Set;

public class NameCollector extends AbstractProcessor<CtElement> {
    private final Set<String> classNames = new HashSet<>();
    private final Set<String> methodNames = new HashSet<>();
    private final Set<String> variableNames = new HashSet<>();

    @Override
    public void process(CtElement element) {
        if (element != null) {
            if (element instanceof CtClass) {
                CtClass<?> ctClass = (CtClass<?>) element;
                classNames.add(ctClass.getSimpleName());
            } else if (element instanceof CtMethod) {
                CtMethod<?> ctMethod = (CtMethod<?>) element;
                methodNames.add(ctMethod.getSimpleName());
            } else if (element instanceof CtVariable) {
                CtVariable<?> ctVariable = (CtVariable<?>) element;
                variableNames.add(ctVariable.getSimpleName());
            }
        }
    }

    public Set<String> getClassNames() {
        return classNames;
    }

    public Set<String> getMethodNames() {
        return methodNames;
    }

    public Set<String> getVariableNames() {
        return variableNames;
    }
}
