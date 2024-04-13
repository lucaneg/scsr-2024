package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity implements BaseNonRelationalValueDomain<Parity> {

    private enum State {
        EVEN, ODD, ZERO, TOP, BOTTOM
    }

    private final State parity;

    private static final Parity EVEN = new Parity(State.EVEN);
    private static final Parity ODD = new Parity(State.ODD);
    private static final Parity ZERO = new Parity(State.ZERO);
    private static final Parity TOP = new Parity(State.TOP);
    private static final Parity BOTTOM = new Parity(State.BOTTOM);

    public Parity() {
        this.parity = State.TOP;
    }

    private Parity(State state) {
        this.parity = state;
    }

    public static Parity fromInteger(int value) {
        if (value == 0) {
            return ZERO;
        } else if (value % 2 == 0) {
            return EVEN;
        } else {
            return ODD;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (parity == null ? 0 : parity.hashCode());
        return result;
    }

    @Override
    public boolean equals(
            Object obj) {
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

    @Override
    public Parity top() {
        return TOP;
    }

    @Override
    public Parity bottom() {
        return BOTTOM;
    }

    @Override
    public boolean isTop() {
        return this.parity == State.TOP;
    }

    @Override
    public boolean isBottom() {
        return this.parity == State.BOTTOM;
    }

    @Override
    public Parity lubAux(Parity other) throws SemanticException {
        if (this.parity == other.parity) {
            return this;
        }
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity other) throws SemanticException {
        if (this.parity == other.parity || this.parity == State.BOTTOM) {
            return true;
        }
        if (this.parity == State.TOP) {
            return other.parity == State.TOP;
        }
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
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
            throws SemanticException {
        if (constant.getValue() instanceof Integer) {
            int v = (Integer) constant.getValue();
            return v % 2 == 0 ? EVEN : ODD;
        }
        return TOP;
    }

    @Override
    public Parity evalUnaryExpression(
            UnaryOperator operator,
            Parity arg,
            ProgramPoint pp,
            SemanticOracle oracle) throws SemanticException {
        if (operator instanceof NumericNegation)
            return arg;
        return TOP;
    }

    @Override
    public Parity evalBinaryExpression(
            BinaryOperator operator,
            Parity left,
            Parity right,
            ProgramPoint pp,
            SemanticOracle oracle)
            throws SemanticException {
        if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
            if (left == BOTTOM || right == BOTTOM)
                return BOTTOM;
            else if (left == TOP || right == TOP)
                return TOP;
            else if (left == EVEN && right == EVEN)
                return EVEN;
            else
                return ODD;
        } else if (operator instanceof MultiplicationOperator) {
            if (left == BOTTOM || right == BOTTOM)
                return BOTTOM;
            else if (left == ZERO || right == ZERO)
                return EVEN; // Assuming ZERO is even for multiplication
            else if (left == TOP || right == TOP)
                return TOP;
            else if (left == ODD && right == ODD)
                return ODD;
            else
                return EVEN;
        } else if (operator instanceof DivisionOperator) {
            if (right == ZERO)
                throw new SemanticException("Division by zero");
            if (left == BOTTOM || right == BOTTOM)
                return BOTTOM;
            else if (left == TOP || right == TOP)
                return TOP;
            else if (left == ZERO)
                return EVEN;
            else
                return TOP;
        }
        return TOP;
    }


}