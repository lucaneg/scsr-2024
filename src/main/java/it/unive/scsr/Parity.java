package it.unive.scsr;

import java.util.Objects;

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

	private static final Parity BOTTOM = new Parity(-10);
	private static final Parity EVEN = new Parity(-1);
	private static final Parity ODD = new Parity(0);
	private static final Parity TOP = new Parity(10);
	
	// this is just needed to distinguish the elements
	private final int parity;
	
	public Parity() {
		this(10);
	}

	public Parity(
			int parity) {
		this.parity = parity;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(parity);
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
	public Parity lubAux(Parity other) throws SemanticException {
		if (this == EVEN && other == EVEN)
			return EVEN;
		else if (this == ODD && other == ODD)
			return ODD;
		else return TOP;
	}

	@Override
	public boolean lessOrEqualAux(Parity other) throws SemanticException {
	    if (this == TOP && other == TOP)
	        return true;
	    else if (this == TOP && other != TOP)
	    	return false;
	    else if (this != TOP && other == TOP)
	    	return true;
	    else
	        return this.lessOrEqual(other);
	}
	
	/*@Override
	public boolean lessOrEqualAux(Parity other) throws SemanticException {
	    // If 'this' is TOP, it is always less than or equal to any other value.
	    if (this == TOP) {
	        return true;
	    }
	    // If 'other' is TOP, 'this' can only be less than or equal to TOP.
	    else if (other == TOP) {
	        return this == TOP;
	    }
	    // If both are EVEN or both are ODD, they are equal, and thus 'this' is less than or equal to 'other'.
	    else if ((this == EVEN && other == EVEN) || (this == ODD && other == ODD)) {
	        return true;
	    }
	    // If 'this' is EVEN and 'other' is ODD, 'this' is not less than or equal to 'other'.
	    else if (this == EVEN && other == ODD) {
	        return false;
	    }
	    // If 'this' is ODD and 'other' is EVEN, 'this' is not less than or equal to 'other'.
	    else if (this == ODD && other == EVEN) {
	        return false;
	    }
		return false;
	}*/

	@Override
	public Parity top() {
		return TOP;
	}

	@Override
	public Parity bottom() {
		return BOTTOM;
	}
	
	@Override
	public Parity evalNonNullConstant(
			Constant constant,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if (constant.getValue() instanceof Integer) {
			int v = (Integer) constant.getValue();
			if (v % 2 == 0)
				return EVEN;
			else
				return ODD;
		}
		return top();
	}
	
	private Parity negate() {
		if (this == EVEN)
			return EVEN;
		else if (this == ODD)
			return ODD;
		else
			return this;
	}
	
	public Parity evalUnaryExpression(
			UnaryOperator operator,
			Parity arg,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if (operator instanceof NumericNegation)
			if (arg == TOP)
				return TOP;
			else
				return this.negate();
		return TOP;
	}

	public Parity evalBinaryExpression(
			BinaryOperator operator,
			Parity left,
			Parity right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if (operator instanceof AdditionOperator) {
			if (left == EVEN && right == EVEN) {
				return EVEN;
			} else if (left == ODD && right == ODD) {
				return ODD;
			} else if ((left == EVEN && right == ODD) || (left == ODD && right == EVEN)) {
				return ODD;
			}
		}
		else if (operator instanceof SubtractionOperator) {
			if (left == EVEN && right == EVEN) {
				return EVEN;
			} else if (left == ODD && right == ODD) {
				return EVEN;
			} else if ((left == EVEN && right == ODD) || (left == ODD && right == EVEN)) {
				return ODD;
			}
		}
		else if (operator instanceof MultiplicationOperator) {
			if (left == EVEN && right == EVEN) {
				return EVEN;
			} else if (left == ODD && right == ODD) {
				return ODD;
			} else if ((left == EVEN && right == ODD) || (left == ODD  && right == EVEN)) {
				return EVEN;
			}
		}
		else if (operator instanceof DivisionOperator) {
			return TOP;
		}
		return TOP;
	}
	
	/*public Parity evalBranches (
			BinaryOperator operator,
			Parity left,
			Parity right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		return left.lubAux(right);
	}
	
	public Parity evalLoops(
			BinaryOperator operator,
			Parity left,
			Parity right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		return left.lubAux(right);
	}*/
			
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