package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.Optional;

public class Parity implements Evaluable<Parity>, BaseNonRelationalValueDomain<Parity> {
    public static byte top;
    public static byte bottom;

    private static final Parity EVEN = new Parity(3);
    private static final Parity ODD = new Parity(2);
    private static final Parity TOP = new Parity(0);
    private static final Parity BOTTOM = new Parity(1);

    private final int parity;
    public Parity() {
        this(0);  // Initializes to TOP by default
    }
    public Parity(int parity) {
        this.parity = parity;
    }

    @Override
    public boolean isTop() {
        return this == TOP;
    }

    @Override
    public boolean isBottom() {
        return this == BOTTOM;
    }

    @Override
    public Parity lubAux(Parity other) {
        if (this.parity == other.parity) {
            return this;
        }
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity other) {
        return this.isBottom() || other.isTop() || this.parity == other.parity;
    }

    @Override
    public Parity wideningAux(Parity other) {
        return lubAux(other);
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
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) {
        return Optional.ofNullable(constant.getValue())
                .filter(v -> v instanceof Integer)
                .map(v -> ((Integer) v) % 2 == 0 ? EVEN : ODD)
                .orElse(top());
    }




    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle) {
        if (NumericNegation.INSTANCE.equals(operator)) {
            return arg; // Negation does not change parity
        }
        return top(); // Default to TOP for unhandled unary operators
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp, SemanticOracle oracle) {
        if (left.isTop() || right.isTop())
            return TOP;

        if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
            if (right == left)
                return EVEN;
            else
                return ODD;
        } else if (operator instanceof MultiplicationOperator) {
            if (left == EVEN || right == EVEN)
                return EVEN;
            else
                return ODD;
        }

        return TOP; // Default to TOP if the operation is not handled
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Parity other = (Parity) obj;
        return parity == other.parity;
    }

    @Override
    public int hashCode() {
        return parity;
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
    public Parity eval(SymbolicExpression expression, ProgramPoint pp) {
        return null;
    }
}