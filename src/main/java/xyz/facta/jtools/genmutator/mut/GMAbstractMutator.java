package xyz.facta.jtools.genmutator.mut;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;


public abstract class GMAbstractMutator<T extends CtElement> extends AbstractProcessor<T> {
    // Implement the reset method in the subclasses
    public abstract void reset();
}