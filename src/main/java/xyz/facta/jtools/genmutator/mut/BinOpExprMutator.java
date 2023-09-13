package xyz.facta.jtools.genmutator.mut;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;

public class BinOpExprMutator extends AbstractProcessor<CtBinaryOperator<Boolean>> {

    private final HashSet<CtElement> hostSpots = new HashSet<>();
    private static final Random rand = new Random();
    private final double probability;
    private final Set<BinOpCategory> categories;
    private static final EnumSet<BinaryOperatorKind> LOGICAL_OPERATORS = EnumSet
        .of(BinaryOperatorKind.AND, BinaryOperatorKind.OR);
    private static final EnumSet<BinaryOperatorKind> COMPARISON_OPERATORS = EnumSet
        .of(BinaryOperatorKind.EQ, BinaryOperatorKind.GE,
            BinaryOperatorKind.GT, BinaryOperatorKind.LE,
            BinaryOperatorKind.LT, BinaryOperatorKind.NE);
    private static final EnumSet<BinaryOperatorKind> REDUCED_COMPARISON_OPERATORS = EnumSet
        .of(BinaryOperatorKind.EQ, BinaryOperatorKind.NE);

    private static final EnumSet<BinaryOperatorKind> ARITHMETIC_OPERATORS = EnumSet
        .of(BinaryOperatorKind.PLUS, BinaryOperatorKind.MINUS, BinaryOperatorKind.DIV, BinaryOperatorKind.MUL);

    public BinOpExprMutator(double prob, Set<BinOpCategory> categories) {
        this.probability = prob;
        this.categories = categories;
    }

    public BinOpExprMutator() {
        this(0.5, EnumSet.allOf(BinOpCategory.class));
    }

    @Override
    public void process(CtBinaryOperator<Boolean> binaryOperator) {
        if (rand.nextDouble() > probability)
            return;
        BinaryOperatorKind kind = binaryOperator.getKind();
        if (LOGICAL_OPERATORS.contains(kind)) {
            if (categories.contains(BinOpCategory.LOGICAL)) { // check configuration: logical op mutation enabled
                mutateOperator(binaryOperator, LOGICAL_OPERATORS);
            }
        } else if (ARITHMETIC_OPERATORS.contains(kind)) {
            if (categories.contains(BinOpCategory.ARITHMETIC)
                && isNumber(binaryOperator.getLeftHandOperand())
                && isNumber(binaryOperator.getRightHandOperand())) {
                mutateOperator(binaryOperator, ARITHMETIC_OPERATORS);
            }
        } else if (COMPARISON_OPERATORS.contains(kind)) {
            if (categories.contains(BinOpCategory.COMPARISON)) {
                if (isNumber(binaryOperator.getLeftHandOperand())
                    && isNumber(binaryOperator.getRightHandOperand())) {
                    mutateOperator(binaryOperator, COMPARISON_OPERATORS);
                } else {
                    EnumSet<BinaryOperatorKind> clone = REDUCED_COMPARISON_OPERATORS.clone();
                    clone.add(kind);
                    mutateOperator(binaryOperator, clone);
                }
            }
        }
    }

    public static <T extends Enum<T>> T getRandomElementExcept(EnumSet<T> set, T exclude) {
        // Copy the elements to a list and remove the excluded one
        List<T> list = new ArrayList<T>(set);
        list.remove(exclude);

        // Return a random element from the list
        return list.get(rand.nextInt(list.size()));
    }

    private void mutateOperator(final CtBinaryOperator<Boolean> expression, EnumSet<BinaryOperatorKind> operators) {

        if (!operators.contains(expression.getKind())) {
            throw new IllegalArgumentException("not consistent ");
        }

        if (alreadyInHotsSpot(expression) || expression.toString().contains(".is(\"")) {
            System.out
                .printf("Expression '%s' ignored because it is included in previous hot spot%n",
                    expression);
            return;
        }

        //int thisIndex = ++index;

        BinaryOperatorKind originalKind = expression.getKind();
        BinaryOperatorKind newBinOpKind = getRandomElementExcept(operators, originalKind);


        CtBinaryOperator<Boolean> newExprOp = getFactory().Code().createBinaryOperator(expression.getLeftHandOperand(), expression.getRightHandOperand(), newBinOpKind);
        //String newExpression = newExprOp.toString();
        ////String newExpression = expression.getLeftHandOperand() + newBinOpKind + expression.getRightHandOperand();

        //CtCodeSnippetExpression<Boolean> codeSnippet = getFactory().Core()
        //    .createCodeSnippetExpression();
        //codeSnippet.setValue(newExpression);

        expression.replace(newExprOp);
        expression.replace(expression);
        //Selector.generateSelector(expression, originalKind, thisIndex, operators, PREFIX);

        hostSpots.add(expression);

    }

    private boolean isNumber(CtExpression<?> operand) {

        if (operand.getType() == null || operand.getType().toString().equals(CtTypeReference.NULL_TYPE_NAME))
            return false;

        if (operand.toString().contains(".class"))
            return false;

        return operand.getType().getSimpleName().equals("int")
            || operand.getType().getSimpleName().equals("long")
            || operand.getType().getSimpleName().equals("byte")
            || operand.getType().getSimpleName().equals("char")
            || operand.getType().getSimpleName().equals("float")
            || operand.getType().getSimpleName().equals("double")
            || operand.getType().isSubtypeOf(getFactory().Type().createReference(Number.class));
    }

    private boolean alreadyInHotsSpot(CtElement element) {
        CtElement parent = element.getParent();
        while (!isTopLevel(parent) && parent != null) {
            if (hostSpots.contains(parent))
                return true;

            parent = parent.getParent();
        }

        return false;

    }

    private boolean isTopLevel(CtElement parent) {
        return parent instanceof CtClass && ((CtClass<?>) parent).isTopLevel();
    }

    public enum BinOpCategory {
        LOGICAL,
        COMPARISON,
        ARITHMETIC
    }
}
