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

    private enum Type {
        TOP(2),
        TAINT(1),
        CLEAN(0),
        BOTTOM(-1);

        private final int value;
        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;

        }

    }


    public final static DefiniteTaint TOP = new DefiniteTaint(Type.TOP);
    public final static DefiniteTaint BOTTOM = new DefiniteTaint(Type.BOTTOM);
    public final static DefiniteTaint TAINT = new DefiniteTaint(Type.TAINT);
    public final static DefiniteTaint CLEAN = new DefiniteTaint(Type.CLEAN);

    private final Type type;

    public DefiniteTaint() {
        this(Type.TOP);
    }

    public DefiniteTaint(Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return 31 + this.type.getValue();
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) return true;
        if (rhs == null) return false;
        if (this.getClass() != rhs.getClass()) return false;
        DefiniteTaint other = (DefiniteTaint) rhs;
        return other.type.getValue() == this.type.getValue();
    }

    @Override
    public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
        // TODO: to implement
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
        return TAINT;
    }

    @Override
    protected DefiniteTaint clean() {
        return CLEAN;
    }

    @Override
    public boolean isAlwaysTainted() {
        return this == TAINT;
    }

    @Override
    public boolean isPossiblyTainted() {
        return this == TOP;
    }

    @Override
    public DefiniteTaint evalBinaryExpression(
            BinaryOperator operator,
            DefiniteTaint left,
            DefiniteTaint right,
            ProgramPoint pp,
            SemanticOracle oracle)
            throws SemanticException {


        if (left == TAINT || right == TAINT) return TAINT;
        if (left == TOP || right == TOP) return TOP;

        return CLEAN;
    }

    @Override
    public DefiniteTaint wideningAux(
            DefiniteTaint other)
            throws SemanticException {

        return TOP;
    }


    @Override
    public StructuredRepresentation representation() {
        // return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
        switch(this.type) {
            case BOTTOM:
                return Lattice.bottomRepresentation();
            case TOP:
                return Lattice.topRepresentation();
            case CLEAN:
                return new StringRepresentation("_");
            case TAINT:
                return new StringRepresentation("#");
        }
        return null;
    }



}
