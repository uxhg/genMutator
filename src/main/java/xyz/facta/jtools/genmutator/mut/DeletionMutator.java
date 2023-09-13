package xyz.facta.jtools.genmutator.mut;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtCompilationUnit;

import java.util.Random;
import java.util.Set;

public class DeletionMutator extends AbstractProcessor<CtStatement> {
    private static final Logger logger = LogManager.getLogger(DeletionMutator.class);
    private final Random random = new Random();
    private final Set<Class<? extends CtStatement>> stmtTypesNotToTouch;
    private final Set<Integer> linesNotToTouch;
    private final int minNumOfLinesToDelete;
    private final int maxNumOfLinesToDelete;
    private final int totalNumOfLinesToDelete;
    private int numOfLinesDeleted = 0;
    private final Distribution distribution;

    public DeletionMutator(
        Set<Class<? extends CtStatement>> stmtTypesNotToTouch,
        Set<Integer> linesNotToTouch,
        int minNumOfLinesToDelete,
        int maxNumOfLinesToDelete,
        Distribution distribution) {
        this.stmtTypesNotToTouch = stmtTypesNotToTouch;
        this.linesNotToTouch = linesNotToTouch;
        this.minNumOfLinesToDelete = minNumOfLinesToDelete;
        this.maxNumOfLinesToDelete = maxNumOfLinesToDelete;
        this.distribution = distribution;
        this.totalNumOfLinesToDelete = minNumOfLinesToDelete +
            random.nextInt(maxNumOfLinesToDelete - minNumOfLinesToDelete + 1);
        logger.debug("Random generated #num(lines to delete) is {}", totalNumOfLinesToDelete);
    }

    @Override
    public void process(CtStatement statement) {
        logger.debug(statement.toString());
        int cuStartLine, cuEndLine;
        int stmtEndLine, stmtStartLine;
        CtCompilationUnit cu = statement.getPosition().getCompilationUnit();
        if (cu == null || cu.getPosition() == null) {
            logger.debug("cu is null or no source position.");
            return;
        } else {
            //logger.debug(cu.getPosition().getClass());
            cuStartLine = cu.getPosition().getLine();
            cuEndLine = cu.getPosition().getEndLine();
        }
        logger.debug("Cu Start/End: {}/{}\n", cuStartLine, cuEndLine);

        try {
            stmtStartLine = statement.getPosition().getLine();
            stmtEndLine = statement.getPosition().getEndLine();
        } catch (UnsupportedOperationException e) {
            logger.debug("Cannot get line number.");
            return;
        }
        logger.debug("Stmt: {}/{}\n", stmtStartLine, stmtEndLine);
        if (shouldDelete(statement, cuEndLine, cuStartLine, stmtStartLine, stmtEndLine)) {
            statement.delete();
            this.numOfLinesDeleted += (stmtEndLine - stmtStartLine + 1);
        }
    }

    private boolean shouldDelete(CtStatement stat, int cuEndLine, int cuStartLine, int stmtStartLine, int stmtEndLine) {
        // Deny deletion if: (1)num of deleted lines will exceed the limit
        // (2)statement type is in the list of types to delete
        // (3)line number is in the list of lines to delete
        // (4)will delete everything
        int totalLines = cuEndLine - cuStartLine + 1;
        int linesDeleteThisTime = stmtEndLine - stmtStartLine + 1;
        if (numOfLinesDeleted + linesDeleteThisTime > totalNumOfLinesToDelete
            || stmtTypesNotToTouch.contains(stat.getClass())
            || linesNotToTouch.contains(stat.getPosition().getLine())
            || numOfLinesDeleted + linesDeleteThisTime >= totalLines
        ) {
            return false;
        }

        if (distribution == Distribution.EVEN) {
            return true;
        } else if (distribution == Distribution.ALL_AT_ONCE) {
            throw new UnsupportedOperationException("Deletion with distribution 'ALL_AT_ONCE' is not supported now.");
            //return cu.getElements(new LineFilter())
            //    .stream()
            //    .anyMatch(line -> !linesNotToTouch.contains(line.getPosition().getLine()));
        } else {
            // Implement other distribution strategies as needed
            return false;
        }
    }


    public enum Distribution {
        EVEN,
        ALL_AT_ONCE,
        // Add more distribution strategies as needed
    }


}

