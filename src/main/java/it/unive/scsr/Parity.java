package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.ModuloOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.RemainderOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity implements BaseNonRelationalValueDomain<Parity> {

    public static final Parity TOP = new Parity(0);
    public static final Parity BOTTOM = new Parity(1);
    public static final Parity EVEN = new Parity(2);
    public static final Parity ODD = new Parity(3);

    private final int parity;

    public Parity() {
        this(0);
    }

    public Parity(int parity) {
        this.parity = parity;
    }

    @Override
    public Parity lubAux(Parity other) throws SemanticException {
        if (this.isTop() ||  other.isTop()) {
            return TOP;
        }
        if (this.equals(other)) {
            return this;
        }
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity other) throws SemanticException {
        return this.equals(other) || other.isTop();
    }

    @Override
    public Parity top() {
        return TOP;
    }

    @Override
    public Parity bottom() {
        return BOTTOM;
    }

    public boolean isEven() {
        return this == EVEN;
    }

    public boolean isOdd() {
        return this == ODD;
    }

    public boolean isTop() {
        return this == TOP;
    }

    public boolean isBottom() {
        return this == BOTTOM;
    }

    @Override
    public StructuredRepresentation representation() {
        if (this.isTop()) {
            return Lattice.topRepresentation();
        }
        if (this.isBottom()) {
            return Lattice.bottomRepresentation();
        }
        if (this.isEven()) {
            return new StringRepresentation("EVEN");
        }
        return new StringRepresentation("ODD");
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp,
                                       SemanticOracle oracle) throws SemanticException {
        if (left.isTop() || right.isTop()) {
            return TOP;
        }

        if (operator instanceof AdditionOperator ||  operator instanceof SubtractionOperator) {
            if ((left.isEven() && right.isEven()) || (left.isOdd() && right.isOdd())) {
                return EVEN;
            } else {
                return ODD;
            }
        } else if (operator instanceof MultiplicationOperator) {
            if (left.isEven() || right.isEven()) {
                return EVEN;
            }
            return ODD;
        } else if (operator instanceof DivisionOperator) {
            if (right.isEven()) {
                return TOP;
            }
            return left;
        } else if (operator instanceof ModuloOperator || operator instanceof RemainderOperator) {
            return left;
        }

        return TOP;
    }

    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle)
            throws SemanticException {
        if (operator == NumericNegation.INSTANCE) {
            return arg;
        }
        return TOP;
    }
    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
            throws SemanticException {
        if (constant.getValue() instanceof Integer) {
            int value = (Integer) constant.getValue();
            if (value == 0) {
                return EVEN;
            } else if (value % 2 == 0) {
                return EVEN;
            } else {
                return ODD;
            }
        }
        return TOP;
    }

    @Override
    public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        return TOP;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + parity;
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
        Parity other = (Parity) obj;
        if (parity != other.parity)
            return false;
        return true;
    }
}