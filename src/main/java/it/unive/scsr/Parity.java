package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.ModuloOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.RemainderOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity implements BaseNonRelationalValueDomain<Parity> {

	public static final Parity TOP = new Parity(1);
	public static final Parity BOTTOM = new Parity(-1);
	public static final Parity EVEN = new Parity(2);
	public static final Parity ODD = new Parity(3);

	private final int parity;

	public Parity() {
		this(0);
	}

	public Parity(int parity) {
		this.parity = parity;
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
	public Parity top() {
		return TOP;
	}

	@Override
	public Parity bottom() {
		return BOTTOM;
	}

	@Override
	public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		Parity res = TOP;
		if (operator == NumericNegation.INSTANCE) {
			res = arg;
		}
		return res;
	}

	@Override
	public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return TOP;
	}

	@Override
	public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		Parity res = TOP;
		if (constant.getValue() instanceof Integer) {
			int k = (int) constant.getValue();
			if (k % 2 == 0) {
				res = EVEN;
			} else {
				res = ODD;
			}
		}
		return res;
	}

	public boolean isEven() {
		return this == EVEN;
	}

	public boolean isOdd() {
		return this == ODD;
	}

	@Override
	public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {
		if (left.isTop() || right.isTop()) {
			return TOP;
		}
		if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
			if (right.equals(left)) {
				return EVEN;
			} else {
				return ODD;
			}
		}
		if (operator instanceof MultiplicationOperator) {
			if (left.isEven() || right.isEven()) {
				return EVEN;
			} else {
				return ODD;
			}
		}
		if (operator instanceof DivisionOperator) {
			if (left.isOdd()) {
				if (right.isOdd()) {
					return ODD;
				} else {
					return EVEN;
				}
			} else {
				if (right.isOdd()) {
					return EVEN;
				} else {
					return TOP;
				}
			}
		}
		if (operator instanceof ModuloOperator || operator instanceof RemainderOperator) {
			return TOP;
		}
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

}