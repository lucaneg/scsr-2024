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

    // Constant elements representing the different states of the parity lattice
    private static final Parity TOP = new Parity(10);
    private static final Parity EVEN = new Parity(1);
    private static final Parity ODD = new Parity(-1);
    private static final Parity BOTTOM = new Parity(-10);

    // Field to store the parity state
    private final int parity;

    // Default constructor setting the element to TOP
    public Parity() {
        this(10);
    }

    // Constructor for creating a Parity element with a specific parity value
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
        // Returns the top element of the lattice
        return TOP;
    }

    @Override
    public Parity bottom() {
        // Returns the bottom element of the lattice
        return BOTTOM;
    }

    @Override
    public Parity lubAux(Parity other) throws SemanticException {
        // Computes the least upper bound of two incomparable elements
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity other) throws SemanticException {
        // Checks if this element is less than or equal to another element
        return false;
    }

    @Override
    public StructuredRepresentation representation() {
        // Returns a string representation of the parity state
        if (this == TOP)
            return Lattice.topRepresentation();
        if (this == BOTTOM)
            return Lattice.bottomRepresentation();
        if (this == EVEN)
            return new StringRepresentation("EVEN");
        return new StringRepresentation("ODD");
    }

    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        // Evaluates the parity of a non-null constant
        if (constant.getValue() instanceof Integer) {
            int value = (Integer) constant.getValue();
            return (value % 2) == 0 ? EVEN : ODD;
        }
        return TOP;
    }

    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        // Evaluates the parity of a unary expression
        if (operator instanceof NumericNegation)
            return arg;
        return TOP;
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        // Evaluates the parity of a binary expression
        if (left == TOP || right == TOP) {
            return TOP;
        } else if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
            return (left == right) ? EVEN : ODD;
        } else if (operator instanceof MultiplicationOperator) {
            return (left == right) ? (left == EVEN ? EVEN : ODD) : EVEN;
        } else if (operator instanceof DivisionOperator) {
            return (left == right) ? (left == EVEN ? TOP : ODD) : EVEN;
        }
        return TOP;
    }
}
