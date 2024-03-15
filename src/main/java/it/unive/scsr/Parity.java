package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity implements BaseNonRelationalValueDomain<Parity> {

    private static final int EVEN_VALUE = 0;
    private static final int ODD_VALUE = 1;
    private static final int TOP_VALUE = 2;
    private static final int BOTTOM_VALUE = 3;

    public final int parity;
    private static final Parity TOP = new Parity(TOP_VALUE);
    private static final Parity BOTTOM = new Parity(BOTTOM_VALUE);
    private static final Parity EVEN = new Parity(EVEN_VALUE);
    private static final Parity ODD = new Parity(ODD_VALUE);

    public Parity() {
        this(TOP_VALUE);
    }

    public Parity(int parity){
        this.parity = parity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.parity;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        Parity other = (Parity) obj;
        return this.parity == other.parity;
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
    public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) {
        return TOP;
    }

    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) {
        if (constant.getValue() instanceof Integer) {
            Integer integer = (Integer) constant.getValue();
            return integer % 2 == 0 ? EVEN : ODD;
        }
        return TOP;
    }

    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity argument, ProgramPoint pp, SemanticOracle oracle)
            throws SemanticException {
        if (operator instanceof NumericNegation) {
            return argument;
        }
        return TOP;
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp,
                                       SemanticOracle oracle)
            throws SemanticException {
        if (left == TOP || right == TOP) return TOP;
        if (operator instanceof  AdditionOperator || operator instanceof SubtractionOperator) {
            return evalAdditionSubtraction(left, right);
        }
        if (operator instanceof MultiplicationOperator) {
            return evalMultiplication(left, right);
        }
        // if (operator instanceof DivisionOperator) {
        //     return evalDivision(left, right);
        // }
        return TOP;
    }

    private Parity evalAdditionSubtraction(Parity left, Parity right) {
        if (left == EVEN && right == EVEN || left == ODD && right == ODD) return EVEN;
        else return ODD;
    }

    private Parity evalMultiplication(Parity left, Parity right) {
        if (left == EVEN || right == EVEN) return EVEN;
        else return ODD;
    }

    // private Parity evalDivision(Parity left, Parity right) {
    //     return null;
    // }

    // IMPLEMENTATION NOTE:
    // the code below is outside of the scope of the course. You can uncomment
    // it to get your code to compile. Be aware that the code is written
    // expecting that you have constants for identifying top, bottom, even and
    // odd elements as we saw for the sign domain: if you name them differently,
    // change also the code below to make it work by just using the name of your
    // choice. If you use methods instead of constants, change == with the
    // invocation of the corresponding method

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
}
