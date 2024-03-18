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

public class Parity implements BaseNonRelationalValueDomain<Parity> {

    // Constants representing different parities
    public static final Parity TOP = new Parity(1);
    public static final Parity BOTTOM = new Parity(-1);
    public static final Parity EVEN = new Parity(2);
    public static final Parity ODD = new Parity(5);

    // Instance variable representing the parity value
    private final int value;

    // Constructor
    private Parity(int value) {
        this.value = value;
    }

    // Abstract domain operations

    @Override
    public Parity lubAux(Parity other) throws SemanticException {
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity other) throws SemanticException {
        return this == BOTTOM || other == TOP;
    }

    @Override
    public Parity top() {
        return TOP;
    }

    @Override
    public Parity bottom() {
        return BOTTOM;
    }

    // Evaluation of constant expressions

    @Override
    public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) {
        return TOP;
    }

    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
            throws SemanticException {
        Parity result = TOP;
        if (constant.getValue() instanceof Integer) {
            int value = (Integer) constant.getValue();
            result = (value % 2 == 0) ? EVEN : ODD;
        }
        return result;
    }

    // Evaluation of binary expressions

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp,
                                       SemanticOracle oracle) throws SemanticException {
        if (left == TOP || right == TOP)
            return TOP;

        if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
            if (left == right)
                return EVEN;
            else
                return ODD;
        }

        if (operator instanceof MultiplicationOperator) {
            if (left == EVEN || right == EVEN)
                return EVEN;
            else
                return ODD;
        }

        if (operator instanceof DivisionOperator) {
            if (right == TOP)
                return TOP;
            if (left == TOP)
                return TOP;
            if (right == EVEN || right == ODD)
                return left;
        }

        return TOP;
    }

    // Evaluation of unary expressions

    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle)
            throws SemanticException {
        if (operator instanceof NumericNegation)
            return arg;

        return TOP;
    }

    // Arithmetic operations

    public Parity add(Parity other) {
        if (this == TOP || other == TOP)
            return TOP;
        if (this == BOTTOM)
            return other;
        if (other == BOTTOM)
            return this;
        if ((this == EVEN && other == EVEN) || (this == ODD && other == ODD))
            return EVEN;
        return ODD;
    }

    public Parity sub(Parity other) {
        return add(other);
    }

    public Parity mul(Parity other) {
        if (this == TOP || other == TOP)
            return TOP;
        if (this == BOTTOM || other == BOTTOM)
            return BOTTOM;
        if ((this == EVEN && other == EVEN) || (this == ODD && other == ODD))
            return EVEN;
        return ODD;
    }

    public Parity div(Parity other) {
        return mul(other);
    }

    public Parity neg() {
        if (this == TOP)
            return TOP;
        return this == EVEN ? ODD : EVEN;
    }

    // Representation of the abstract domain

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


    // Custom toString() method for debugging purposes

    @Override

    public String toString() {
        if (this == TOP) {
            return "#TOP#";
        } else if (this == BOTTOM) {
            return "#BOTTOM#";
        } else if (value % 2 == 0) {
            return "EVEN";
        } else {
            return "ODD";
        }
    }


    // Main method for testing purposes

    public static void main(String[] args) {
        Parity p1 = TOP;
        Parity p2 = EVEN;
        Parity p3 = ODD;

        System.out.println("Before operations:");
        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
        System.out.println("p3: " + p3);

        System.out.println("\nAfter operations:");
        System.out.println("p1 + p2: " + p1.add(p2));
        System.out.println("p2 - p3: " + p2.sub(p3));
        System.out.println("p2 * p3: " + p2.mul(p3));
        System.out.println("p3 / p2: " + p3.div(p2));
        System.out.println("-p2: " + p2.neg());
    }
}
