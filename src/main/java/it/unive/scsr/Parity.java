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

import java.util.Objects;

public class Parity implements BaseNonRelationalValueDomain<Parity> {

    /*Let's define static values*/
    private static final int iEVEN = 0;
    private static final int iODD = 1;
    private static final int iTOP = 2;
    private static final int iBOTTOM = 3;

    /*Let's define static classes with the values above*/
    private static final Parity TOP = new Parity(iTOP);
    private static final Parity BOTTOM = new Parity(iBOTTOM);
    private static final Parity EVEN = new Parity(iEVEN);
    private static final Parity ODD = new Parity(iODD);

    public final int iVal;
    public Parity()
    {
        this(iTOP);
    }

    public Parity(int iVal){
        this.iVal = iVal;
    }

    @Override
    public Parity lubAux(Parity parity) throws SemanticException {
        // this and other are always incomparable when we reach here
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity parity) throws SemanticException {
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

    // logic for evaluating expressions below
    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)  throws SemanticException {

        if (constant.getValue() instanceof Integer) {
            Integer v = (Integer) constant.getValue();
            if (v % 2 == 0)
                return EVEN;
            else
                return ODD;
        }
        return top();
    }

    @Override
    public Parity evalUnaryExpression(
            UnaryOperator operator,
            Parity arg,
            ProgramPoint pp,
            SemanticOracle oracle)
            throws SemanticException {
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

        Parity pRes = TOP;

        if (left == TOP || right == TOP)
            return TOP;

        if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
            if(left == ODD && right == ODD || left == EVEN && right == EVEN)
                pRes = ODD;
            else if (left == ODD && right == EVEN || left == EVEN && right == ODD)
                pRes = EVEN;
        } else if (operator instanceof MultiplicationOperator) {
            if(left == ODD && right == ODD)
                pRes = ODD;
            else if (right == EVEN || left == EVEN)
                pRes = EVEN;
        } else if (operator instanceof DivisionOperator) {
            if (left == ODD)
                return right == ODD ? ODD : EVEN;
            else
                return right == ODD ? EVEN : TOP;
        }
        else if (operator instanceof ModuloOperator || operator instanceof RemainderOperator) {
            pRes = TOP;
        }

        return pRes;
    }


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parity parity = (Parity) o;
        return iVal == parity.iVal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(iVal);
    }
}