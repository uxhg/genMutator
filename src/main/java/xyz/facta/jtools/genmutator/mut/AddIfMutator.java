package xyz.facta.jtools.genmutator.mut;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class AddIfMutator extends AbstractProcessor<CtAssignment<?, ?>> {
    private static final Logger logger = LogManager.getLogger(AddIfMutator.class);
    private final Random random = new Random();

    private final double probability;

    public AddIfMutator(double prob) {
        probability = prob;
    }

    public AddIfMutator() {
        this(0.5);
    }

    private boolean shouldInsertIfStatement() {
        return random.nextDouble() < probability;
    }

    private CtIf createIfStatementForStat(CtStatement stat) {
        CtIf ifStmt = stat.getFactory().Core().createIf();
        CtExpression<Boolean> condition = generateConditionBasedOnContext(stat);

        if (condition != null) {
            ifStmt.setCondition(condition);
            ifStmt.setThenStatement(stat.clone());
            return ifStmt;
        }
        return null;
    }

    @Override
    public void process(CtAssignment<?, ?> assignment) {
        if (shouldInsertIfStatement()) {
            CtIf newIfStatement = createIfStatementForStat(assignment);

            if (newIfStatement != null) {
                CtElement parent = assignment.getParent();
                logger.debug("parent is: {}, type is: {}\n", parent.prettyprint(), parent.getClass().getName());
                if (parent instanceof CtBinaryOperator) {
                    logger.info("skip, parent {} is also binary operation\n", parent);
                    return;
                }
                if (parent instanceof CtBlock<?>) {
                    // If the parent is a block, simply replace the assignment with the new if statement
                    assignment.replace(newIfStatement);
                } else {
                    // Create a new block and add the new if statement to it
                    CtBlock<?> block = assignment.getFactory().Core().createBlock();
                    block.addStatement(newIfStatement);

                    // Find the parent block and insert the new block at the appropriate position
                    CtBlock<?> parentBlock = assignment.getParent(CtBlock.class);
                    if (parentBlock != null) {
                        int position = parentBlock.getStatements().indexOf(assignment);
                        if (position < 0) {
                            logger.debug("assignment pos of parent block is {}\n", position);
                            logger.debug("assignment is: {}\n", assignment);
                            position = 0;
                        }
                        parentBlock.getStatements().add(position, block);

                        // Now, remove the original assignment
                        assignment.delete();
                    }
                }
            }
        }
    }


    private CtExpression<Boolean> generateConditionBasedOnContext(CtStatement stat) {
        Factory factory = stat.getFactory();

        // Gather available local variables from the context
        List<CtVariableAccess<?>> availableVariables = gatherAvailableVariables(stat);
        List<CtExpression<Boolean>> booleanVariables = availableVariables.stream().filter(Objects::nonNull)
            .filter(var -> var.getType() != null && (var.getType().toString().equals("boolean") || var.getType().toString().equals("java.lang.Boolean")))
            .map(var -> (CtExpression<Boolean>) var) // Explicit casting here
            .collect(Collectors.toList());

        CtExpression<Boolean> condition = null;

        if (!booleanVariables.isEmpty() ) { // if there is boolean, use boolean variable
            CtExpression<Boolean> randomBoolVar = booleanVariables.get(random.nextInt(booleanVariables.size()));
            if (random.nextBoolean()) { // 50% chance to negate the boolean
                // condition = factory.Code().createBinaryOperator(condition, UnaryOperatorKind.NOT)  createUnaryOperator(condition, UnaryOperatorKind.NOT);
                CtUnaryOperator<Boolean> unaryOperator = factory.Core().createUnaryOperator();
                unaryOperator.setKind(UnaryOperatorKind.NOT);
                unaryOperator.setOperand(randomBoolVar);
                condition = unaryOperator;
            } else { // 50% chance to use the boolean as is
                condition = randomBoolVar;
            }
        } else if (!availableVariables.isEmpty()) {
            // Pick a random variable from the list
            CtVariableAccess<?> randomVariable = availableVariables.get(random.nextInt(availableVariables.size()));
            // Creating a sample condition: "randomVariable != null"
            condition = factory.Code().createCodeSnippetExpression(randomVariable + " != null");
        }  // else: do nothing, will return null

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