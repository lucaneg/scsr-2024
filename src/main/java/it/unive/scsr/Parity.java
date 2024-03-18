package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.ValueExpression;
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


    private enum Type {
        EVEN(10),
        ODD(20),
        TOP(30),
        BOTTOM(40);

        private final int value;
        private Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    private final Type type;
    private static final Parity TOP = new Parity(Type.TOP);
    private static final Parity ODD = new Parity(Type.ODD);
    private static final Parity EVEN = new Parity(Type.EVEN);
    private static final Parity BOTTOM = new Parity(Type.BOTTOM);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime*result + this.type.getValue();
        return result;
    }

    public Parity() {
        this(Type.TOP);
    }

    public Parity(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == this) return true;
        if (rhs == null) return false;
        if (getClass() != rhs.getClass()) return false;
        Parity other = (Parity) rhs;
        if (type.getValue() != other.type.getValue()) return false;
        return true;
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
    public Parity lubAux(Parity other) throws SemanticException {
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity other) throws SemanticException {
        System.out.println("SONO QUAAAAAAAAA MINORE O UGUALEEEEE " + other.type + " " + other.type.getValue());
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

//    @Override
//    public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
//        return TOP;
//    }

    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) {
        if (constant.getValue() instanceof Integer) {
            Integer i = (Integer) constant.getValue();
            if (i % 2 == 0) return EVEN;
            else return ODD;
        }
        return TOP;
    }

    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {

        if (operator instanceof NumericNegation) {
            return arg;
        }
        return TOP;
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {

        if (operator instanceof AdditionOperator) return evaluateAddition(left, right);
        if (operator instanceof SubtractionOperator) return evaluateSubstracton(left, right);
        if (operator instanceof MultiplicationOperator) return evaluateMoltiplication(left, right);
        if (operator instanceof DivisionOperator) return evaluateDivision(left, right);
        return TOP;

    }

    private static Parity evaluateAddition(Parity left, Parity right) {
        if (left == EVEN && right == EVEN) return EVEN;
        if (left == ODD && right == ODD) return EVEN;
        if (left == EVEN && right == ODD) return ODD;
        if (left == ODD && right == EVEN) return ODD;
        return TOP;
    }

    private static Parity evaluateSubstracton(Parity left, Parity right) {
        return evaluateAddition(left, right);
    }

    private static Parity evaluateMoltiplication(Parity left, Parity right) {
        if (left == EVEN || right == EVEN) return EVEN;
        if (left == ODD && right == ODD) return ODD;
        return TOP;
    }

    private static Parity evaluateDivision(Parity left, Parity right) {
        return TOP;
    }
}
