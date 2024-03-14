package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.*;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity implements BaseNonRelationalValueDomain<Parity>
{
    private static final Parity BOTTOM = new Parity(-10);
    private static final Parity EVEN = new Parity(2);
    private static final Parity ODD = new Parity(1);
    private static final Parity TOP = new Parity(10);

    private final int parity;

	public Parity() {
        this(10);
    }

	public Parity(int parity) {
        this.parity = parity;
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
        return parity == other.parity;
    }

    @Override
    public Parity top() {
        return TOP;
    }

    @Override
    public Parity bottom() {
        return BOTTOM;
    }

    @Override
    public Parity lubAux(Parity other) throws SemanticException {
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity other) throws SemanticException {
        return false;
    }

    @Override
    public StructuredRepresentation representation() {
        if (this == TOP)
            return Lattice.topRepresentation();
        if (this == BOTTOM)
            return Lattice.bottomRepresentation();
        if (this == EVEN)
            return new StringRepresentation("EVEN");
        return new StringRepresentation("ODD");
    }

    @Override
    public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) {
        return top();
    }

    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (constant.getValue() instanceof Integer) {
            int v = (Integer) constant.getValue();
            return (v%2 == 0) ? EVEN : ODD;
        }
        return top();
    }

    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (operator instanceof NumericNegation)
            return arg;
        return top();
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (left.isTop() || right.isTop())
            return top();
        if (left.isBottom() || right.isBottom())
            return bottom();
        if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
            if (left.equals(right))
                return EVEN;
            else
                return ODD;
        } else if (operator instanceof MultiplicationOperator) {
            if (left == EVEN || right == EVEN)
                return EVEN;
            else
                return ODD;
        } else if (operator instanceof DivisionOperator)
            return TOP;
        return TOP;
    }
}