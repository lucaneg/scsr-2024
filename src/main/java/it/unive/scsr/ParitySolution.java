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

public class ParitySolution implements BaseNonRelationalValueDomain<ParitySolution> {

	private static final ParitySolution EVEN = new ParitySolution((byte) 3);
	private static final ParitySolution ODD = new ParitySolution((byte) 2);
	private static final ParitySolution TOP = new ParitySolution((byte) 0);
	private static final ParitySolution BOTTOM = new ParitySolution((byte) 1);

	private final int parity;

	public ParitySolution() {
		this(0);
	}

	public ParitySolution(
			int parity) {
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
	public boolean equals(
			Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParitySolution other = (ParitySolution) obj;
		if (parity != other.parity)
			return false;
		return true;
	}

	@Override
	public ParitySolution lubAux(
			ParitySolution other)
			throws SemanticException {
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(
			ParitySolution other)
			throws SemanticException {
		return false;
	}

	@Override
	public ParitySolution top() {
		return TOP;
	}

	@Override
	public ParitySolution bottom() {
		return BOTTOM;
	}

	@Override
	public ParitySolution evalNonNullConstant(
			Constant constant,
			ProgramPoint pp,
			SemanticOracle oracle) {
		if (constant.getValue() instanceof Integer) {
			Integer i = (Integer) constant.getValue();
			return i % 2 == 0 ? EVEN : ODD;
		}

		return top();
	}

	@Override
	public ParitySolution evalUnaryExpression(
			UnaryOperator operator,
			ParitySolution arg,
			ProgramPoint pp,
			SemanticOracle oracle) {
		if (operator == NumericNegation.INSTANCE)
			return arg;
		return top();
	}

	@Override
	public ParitySolution evalBinaryExpression(
			BinaryOperator operator,
			ParitySolution left,
			ParitySolution right,
			ProgramPoint pp,
			SemanticOracle oracle) {
		if (left.isTop() || right.isTop())
			return top();

		if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator)
			if (right.equals(left))
				return EVEN;
			else
				return ODD;
		else if (operator instanceof MultiplicationOperator)
			if (left == EVEN || right == EVEN)
				return EVEN;
			else
				return ODD;

		return TOP;
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
}
