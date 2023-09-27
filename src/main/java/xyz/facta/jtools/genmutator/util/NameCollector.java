package xyz.facta.jtools.genmutator.util;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;

import java.util.HashSet;
import java.util.Set;

public class NameCollector extends AbstractProcessor<CtElement> {
    private final Set<String> classes = new HashSet<>();
    private final Set<String> methods = new HashSet<>();


    private final Set<String> invokedMethods = new HashSet<>();
    private final Set<String> variables = new HashSet<>();

    @Override
    public void process(CtElement element) {
        if (element != null) {
            if (element instanceof CtClass) {
                CtClass<?> ctClass = (CtClass<?>) element;
                classes.add(ctClass.getSimpleName());
            } else if (element instanceof CtMethod) {
                CtMethod<?> ctMethod = (CtMethod<?>) element;
                methods.add(ctMethod.getSimpleName());
            } else if (element instanceof CtVariable) {
                CtVariable<?> ctVariable = (CtVariable<?>) element;
                variables.add(ctVariable.getSimpleName());
            } else if (element instanceof CtInvocation) {
                CtInvocation<?> ctInvocation = (CtInvocation<?>) element;
                invokedMethods.add(ctInvocation.getExecutable().getSimpleName());
            }
        }
    }

    public Set<String> getClasses() {
        return classes;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public Set<String> getInvokedMethods() {
        return invokedMethods;
    }
}
