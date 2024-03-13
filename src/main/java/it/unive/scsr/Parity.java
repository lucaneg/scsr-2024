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

import java.util.Objects;

public class Parity implements BaseNonRelationalValueDomain<Parity> {

    private static final Parity TOP = new Parity(10);
    private static final Parity BOTTOM = new Parity(-10);
    private static final Parity ODD = new Parity(1);
    private static final Parity EVEN = new Parity(2);

    private final int sign;

	public Parity(){
		this.sign = 10;
	}

	public Parity(int sign){
		this.sign = sign;
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
	public Parity lubAux(Parity other) throws SemanticException {
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(Parity other) throws SemanticException {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Parity parity = (Parity) o;
		return sign == parity.sign;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sign);
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
	public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (constant.getValue() instanceof Integer v)
			return (v % 2 == 0) ? EVEN : ODD;
		else
			return TOP;
	}

	@Override
	public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (operator instanceof NumericNegation)
			return arg;
		else
			return TOP;
	}

	@Override
	public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
			if (left == EVEN && right == EVEN || left == ODD && right == ODD) return EVEN;
			else if (left == EVEN && right == ODD || left == ODD && right == EVEN) return ODD;
		}
		else if (operator instanceof MultiplicationOperator) {
			if (left == EVEN || right == EVEN) return EVEN;
			else if (left == ODD && right == ODD) return ODD;
		}
		return TOP;
	}
}