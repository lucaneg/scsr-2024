package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator; // Assuming this exists
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity implements BaseNonRelationalValueDomain<Parity> {
    private static final Parity ODD = new Parity(1);
    private static final Parity EVEN = new Parity(0);
    private static final Parity TOP = new Parity(10);
    private static final Parity BOTTOM = new Parity(-10);

    private final int parity;

    public Parity() {
        this(10);
    }

    public Parity(int parity) {
        this.parity = parity;
    }

    @Override
    public int hashCode() {
        return 31 * parity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return parity == ((Parity) obj).parity;
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
    public Parity top() {
        return TOP;
    }

    @Override
    public Parity bottom() {
        return BOTTOM;
    }

    @Override
    public StructuredRepresentation representation() {
        if (this == TOP) return Lattice.topRepresentation();
        if (this == BOTTOM) return Lattice.bottomRepresentation();
        return new StringRepresentation(parity == 0 ? "EVEN" : "ODD");
    }

    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (constant.getValue() instanceof Integer) {
            return ((Integer) constant.getValue()) % 2 == 0 ? EVEN : ODD;
        }
        return top();
    }

    @Override
    public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        return EVEN;
    }

    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (operator instanceof NumericNegation) {
            // For unary negation, the parity remains the same.
            return arg;
        }
        // For other unary operators, if any, we return TOP as the parity is not known.
        return top();
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
            return left == EVEN ? right : (left == ODD && right == ODD ? EVEN : TOP);
        } else if (operator instanceof MultiplicationOperator) {
            return left == EVEN ? left : (left == ODD ? right : TOP);
        } else if (operator instanceof DivisionOperator) {
            // Assuming the result's parity is the same as the dividend's parity
            return left;
        }
        // For other binary operators, if any, we return TOP as the parity is not known.
        return top();
    }
}
