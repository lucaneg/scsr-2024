package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class DefiniteTaint extends BaseTaint<DefiniteTaint>  {

    // Constants for the four states
    private static final DefiniteTaint TAINTED = new DefiniteTaint(1);
    private static final DefiniteTaint CLEAN = new DefiniteTaint(0);
    private static final DefiniteTaint BOTTOM = new DefiniteTaint(-1);
    private static final DefiniteTaint TOP = new DefiniteTaint(10);

    // Field to store the state
    private final int state;

    public DefiniteTaint(int state) {
        this.state = state;
    }

    @Override
    public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
        // The least upper bound of two elements is the "more uncertain" one
        if (this.state == other.state)
            return this;
        else
            return TOP;
    }

    @Override
    public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
        // An element is less or equal to another one if it is "more certain"
        return this.state >= other.state;
    }

    @Override
    public DefiniteTaint top() {
        // The top element is the "most uncertain" one
        return TOP;
    }

    @Override
    public DefiniteTaint bottom() {
        // The bottom element is the "most certain" one
        return CLEAN;
    }

    @Override
    protected DefiniteTaint tainted() {
        // Represents a definitely tainted value
        return TAINTED;
    }

    @Override
    protected DefiniteTaint clean() {
        // Represents a definitely clean value
        return CLEAN;
    }

    @Override
    public boolean isAlwaysTainted() {
        // A value is always tainted if it is definitely tainted
        return this == TAINTED;
    }

    @Override
    public boolean isPossiblyTainted() {
        // A value is possibly tainted if it is not definitely clean
        return this != CLEAN;
    }

    @Override
    public DefiniteTaint evalBinaryExpression(
            BinaryOperator operator,
            DefiniteTaint left,
            DefiniteTaint right,
            ProgramPoint pp,
            SemanticOracle oracle)
            throws SemanticException {
        // If either of the operands is definitely tainted, the result is definitely tainted
        if (left == TAINTED || right == TAINTED)
            return TAINTED;
        // If either of the operands is bottom, the result is bottom
        else if (left == BOTTOM || right == BOTTOM)
            return BOTTOM;
        // If either of the operands is top, the result is top
        else if (left == TOP || right == TOP)
            return TOP;
        // Otherwise, the result is definitely clean
        else
            return CLEAN;
    }

    @Override
    public DefiniteTaint wideningAux(
            DefiniteTaint other)
            throws SemanticException {
        // The widening of two elements is their least upper bound
        return this.lubAux(other);
    }

    @Override
    public StructuredRepresentation representation() {
        if (this == TOP)
            return Lattice.topRepresentation();
        else if (this == BOTTOM)
            return Lattice.bottomRepresentation();
        else if (this == CLEAN)
            return new StringRepresentation("_");
        else
            return new StringRepresentation("#");
    }
}

