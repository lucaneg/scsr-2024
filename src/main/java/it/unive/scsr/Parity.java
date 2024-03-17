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
    // Constants for identifying top, bottom, even and odd elements
    private static final Parity TOP = new Parity(10);
    private static final Parity BOTTOM = new Parity(-10);
    private static final Parity EVEN = new Parity(0);
    private static final Parity ODD = new Parity(1);

    // This is just needed to distinguish the elements
    private final int parity;

    public Parity() {
        this(10);
    }

    private Parity(int parity) {
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

    // Logic for evaluating expressions below

    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (constant.getValue() instanceof Integer) {
            int v = (Integer) constant.getValue();
            if (v % 2 == 0)
                return EVEN;
            else
                return ODD;
        }
        return top();
    }
    
    @Override
    public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        // Null does not have a parity, so we return TOP to represent an unknown value
        return top();
    }


    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (operator instanceof NumericNegation)
            // negation does not change the parity
            return arg;

        return TOP;
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
            // for addition and subtraction, if both operands have the same parity, the result is EVEN, else ODD
            return left == right ? EVEN : ODD;
        } else if (operator instanceof MultiplicationOperator) {
            // for multiplication, if either operand is EVEN, the result is EVEN, else ODD
            return left == EVEN || right == EVEN ? EVEN : ODD;
        } else if (operator instanceof DivisionOperator) {
            // for division, if the divisor is EVEN, throw an exception, else return the parity of the dividend
            if (right == EVEN) {
                throw new SemanticException("Division by an even number is undefined in this domain.");
            } else {
                return left;
            }
        }

        return TOP;
    }
}
