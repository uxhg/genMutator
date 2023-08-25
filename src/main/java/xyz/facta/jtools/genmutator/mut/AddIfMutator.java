package xyz.facta.jtools.genmutator.mut;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class AddIfMutator extends AbstractProcessor<CtInvocation<?>> {

    private final Random random = new Random();

    private boolean shouldInsertIfStatement() {
        // For this example, we're using a 50% chance to insert the if statement
        return random.nextBoolean();
    }

    private CtIf createIfStatementForInvocation(CtInvocation<?> invocation) {
        CtIf ifStmt = invocation.getFactory().Core().createIf();
        CtExpression<Boolean> condition = generateConditionBasedOnContext(invocation);

        if (condition != null) {
            ifStmt.setCondition(condition);
            ifStmt.setThenStatement(invocation.clone());
            return ifStmt;
        }
        return null;
    }

    @Override
    public void process(CtInvocation<?> invocation) {
        //System.out.println("Apply AddIfMutator to file: " + invocation.getPosition());
        if (shouldInsertIfStatement()) {
            CtIf newIfStatement = createIfStatementForInvocation(invocation);

            if (newIfStatement != null) {
                CtElement parent = invocation.getParent();

                if (parent instanceof CtBlock<?>) {
                    // If the parent is a block, simply replace the invocation with the new if statement
                    invocation.replace(newIfStatement);
                } else {
                    // Create a new block and add the new if statement to it
                    CtBlock<?> block = invocation.getFactory().Core().createBlock();
                    block.addStatement(newIfStatement);

                    // Find the parent block and insert the new block at the appropriate position
                    CtBlock<?> parentBlock = invocation.getParent(CtBlock.class);
                    if (parentBlock != null) {
                        int position = parentBlock.getStatements().indexOf(invocation);
                        if (position < 0) {
                            System.out.printf("invocation pos of parent block is %s\n", position);
                            System.out.printf("invocation is: %s\n", invocation);
                            position = 0;
                        }
                        parentBlock.getStatements().add(position, block);

                        // Now, remove the original invocation
                        invocation.delete();
                    }
                }
            }
        }
    }

    private CtExpression<Boolean> generateConditionBasedOnContext(CtInvocation<?> invocation) {
        Factory factory = invocation.getFactory();

        // Gather available local variables from the context
        List<CtVariableAccess<?>> availableVariables = gatherAvailableVariables(invocation);
        List<CtExpression<Boolean>> booleanVariables = availableVariables.stream().filter(Objects::nonNull)
            .filter(var -> var.getType() != null && (var.getType().toString().equals("boolean") || var.getType().toString().equals("java.lang.Boolean")))
            .map(var -> (CtExpression<Boolean>) var) // Explicit casting here
            .collect(Collectors.toList());

        CtExpression<Boolean> condition = null;

        if (!booleanVariables.isEmpty() && random.nextInt(100) < 50) { // 50% chance to use boolean variable
            CtExpression<Boolean> randomBoolVar = booleanVariables.get(random.nextInt(booleanVariables.size()));
            if (random.nextBoolean()) { // 50% chance to negate the boolean
                // condition = factory.Code().createBinaryOperator(condition, UnaryOperatorKind.NOT)  createUnaryOperator(condition, UnaryOperatorKind.NOT);
                CtUnaryOperator<Boolean> unaryOperator = factory.Core().createUnaryOperator();
                unaryOperator.setKind(UnaryOperatorKind.NOT);
                unaryOperator.setOperand(randomBoolVar);
                condition = unaryOperator;
            }
        } else if (!availableVariables.isEmpty()) {
            // Pick a random variable from the list
            CtVariableAccess<?> randomVariable = availableVariables.get(random.nextInt(availableVariables.size()));
            // Creating a sample condition: "randomVariable != null"
            condition = factory.Code().createCodeSnippetExpression(randomVariable + " != null");
        } else {
            // do nothing, return null
        }
        return condition;
    }

    private List<CtVariableAccess<?>> gatherAvailableVariables(CtElement element) {
        // Get the parent block or method
        CtBlock<?> parentBlock = element.getParent(CtBlock.class);

        if (parentBlock != null) {
            // Collect all local variable references in the block or method
            return parentBlock.getElements(new TypeFilter<>(CtVariableAccess.class));
        } else { // return empty list
            return List.of();
        }
    }

    private CtStatementList createIfBody(Factory factory) {
        CtStatementList body = factory.Core().createStatementList();

        // Example operation: incrementing a variable (you can add more real operations)
        CtStatement statement = factory.Code().createCodeSnippetStatement("someCounter++");
        body.addStatement(statement);

        // More realistic operations can be added based on context, e.g., method calls, assignments, etc.
        return body;
    }
}