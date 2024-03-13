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
	private static final Parity BOTTOM = new Parity();
	private static final Parity TOP = new Parity();
	private static final Parity EVEN = new Parity();
	private static final Parity ODD = new Parity();

	@Override
	public StructuredRepresentation representation() {
		if (this == BOTTOM)
			return Lattice.bottomRepresentation();
		if (this == ODD)
			return new StringRepresentation("ODD");
		if (this == EVEN)
			return new StringRepresentation("EVEN");
		return Lattice.topRepresentation();
	}

	@Override
	public Parity lubAux(Parity other) throws SemanticException {
		return top();
	}

	@Override
	public boolean lessOrEqualAux(Parity other) throws SemanticException {
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

	@Override
	public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (constant.getValue() instanceof Integer) {
			int value = (Integer) constant.getValue();
			if (value % 2 == 0)
				return EVEN;
			return ODD;
		}
		return top();
	}

	@Override
	public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {
		if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
			if (left == TOP || right == TOP)
				return TOP;
			if (left == right)
				return EVEN;
			else
				return ODD;
		}

		if (operator instanceof MultiplicationOperator) {
			if (left == EVEN || right == EVEN)
				return EVEN;
			if (left == ODD && right == ODD)
				return ODD;
		}

		return top();
	}

	@Override
	public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (operator instanceof NumericNegation)
			return arg;

		return top();
	}

}