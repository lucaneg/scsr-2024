package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class DefiniteTaint extends BaseTaint<DefiniteTaint> {

    public static final DefiniteTaint BOTTOM = new DefiniteTaint(0);
    public static final DefiniteTaint CLEAN = new DefiniteTaint(1);
    public static final DefiniteTaint TAINTED = new DefiniteTaint(2);
    public static final DefiniteTaint TOP = new DefiniteTaint(3);

    private final int taintLevel;

    public DefiniteTaint(int taintLevel) {
        this.taintLevel = taintLevel;
    }

    public DefiniteTaint() {
        this(TOP.taintLevel);
    }

    @Override
    public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
        if (this == TOP  other == TOP) return TOP;
        if (this == TAINTED  other == TAINTED) return TAINTED;
        if (this == CLEAN && other == CLEAN) return CLEAN;
        return BOTTOM;
    }

    @Override
    public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
        if (this == BOTTOM  other == TOP) return true;
        if (this == other) return true;
        if (this == CLEAN && other == TAINTED) return true;
        return false;
    }

    @Override
    public DefiniteTaint top() {
        return TOP;
    }

    @Override
    public DefiniteTaint bottom() {
        return BOTTOM;
    }

    @Override
    protected DefiniteTaint tainted() {
        return TAINTED;
    }

    @Override
    protected DefiniteTaint clean() {
        return CLEAN;
    }

    @Override
    public boolean isAlwaysTainted() {
        return this == TAINTED;
    }

    @Override
    public boolean isPossiblyTainted() {
        return this == TOP;
    }

    public DefiniteTaint evalBinaryExpression(
            BinaryOperator operator,
            DefiniteTaint left,
            DefiniteTaint right,
            ProgramPoint pp,
            SemanticOracle oracle) throws SemanticException {
        if (left.isAlwaysTainted()  right.isAlwaysTainted())
            return TAINTED;

        if (left.isPossiblyTainted() || right.isPossiblyTainted())
            return TOP;

        return CLEAN;
    }

    @Override
    public DefiniteTaint wideningAux(DefiniteTaint other) throws SemanticException {
        return lubAux(other);
    }

    @Override
    public StructuredRepresentation representation() {
        return this == BOTTOM ? Lattice.bottomRepresentation()
                : this == TOP ? Lattice.topRepresentation()
                : this == CLEAN ? new StringRepresentation("CLEAN") : new StringRepresentation("TAINTED");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + taintLevel;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefiniteTaint other = (DefiniteTaint) obj;
        return taintLevel == other.taintLevel;
    }
}