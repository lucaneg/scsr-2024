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

	/*
	 * From top to bottom we have the following representation	
	 * TOP - (EVEN | ODD) - BOTTOM
	 * 
	 * Higher element is TOP which comprehends the sub-levels EVEN and ODD
	 * The lower level is BOTTOM which comprehends the empty element or if an element has an error
	 * 
	 * EVEN = { x | x is EVEN }    ex: {-5}{-3}{-1}{1}{3}{5}...
	 * ODD = { x | x is ODD }    ex: {-4}{-2}{0}{2}{4}...
	 */
	
	/*
	 * I defined four Parity elements:
	 * - BOTTOM which comprehends the empty element or if an element has an error
	 * - EVEN which is associated to the even numbers
	 * - ODD which is associated to the odd numbers
	 * - TOP which comprehends the sub-levels EVEN and ODD
	 */
	private static final Parity BOTTOM = new Parity(-10);
	private static final Parity EVEN = new Parity(-1);
	private static final Parity ODD = new Parity(0);
	private static final Parity TOP = new Parity(10);
	
	/*
	 * This is just needed to distinguish the elements
	 */
	private final int parity;
	
	/*
	 * Constructors
	 */
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

	/* 
	 * This method evaluates if two objects are equal so if they represent the same Parity value
	 * @input: an Object obj to compare with the Object from which the function is called
 	 * @return: boolean value representing if the two objects are equal
 	*/
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
	
	/* 
	 * In this function I find the least upper bound of the two Parity elements, the one which call the function and the one passed as parameter
	 * I considered four main cases:
	 * 1. if both elements are EVEN the least upper bound is EVEN
	 * 2. if both elements are ODD the least upper bound is ODD
	 * 3. if both elements are BOTTOM the least upper bound is BOTTOM
	 * 4. otherwise, if the elements are different (so one EVEN and the other ODD, or vice versa, or one BOTTOM and the other EVEN/ODD, or vice versa) the least upper bound is TOP
	 * @input: a Parity element to compare with the element from which the function is called to determine the least upper bound
	 * @return: a Parity element representing the least upper bound of the two elements in input
	*/
	@Override
	public Parity lubAux(Parity other) throws SemanticException {
		if (this == EVEN && other == EVEN)
			return EVEN;
		else if (this == ODD && other == ODD)
			return ODD;
		else if (this == BOTTOM && other == BOTTOM)
			return BOTTOM;
		else return TOP;
	}
	
	/* 
	 * This function evaluates if, given two elements (the one which call the function and the one passed as parameter)
	 * if the first, so this is less or equal than other
	 * I considered seven cases:
	 * 1. if both elements are TOP the function returns true
	 * 2. if both elements are BOTTOM the function returns true
	 * 3. if one element (this) equals TOP while the other isn't equal to TOP nor BOTTOM the function returns false because the TOP element is greater than the others
	 * 4. if one element (other) equals TOP while the other isn't equal to TOP nor BOTTOM the function returns true because the TOP element is greater than the others
	 * 5. if one element (this) equals BOTTOM while the other isn't equal to TOP nor BOTTOM the function returns false because the TOP element is greater than the others
	 * 6. if one element (other) equals BOTTOM while the other isn't equal to TOP nor BOTTOM the function returns true because the TOP element is greater than the others
	 * 7. if no one element equals TOP the function pass the evaluation to the lessOrEqual function
	 * @input: a Parity element to compare with the element from which the function is called to determine the less or equal relation
	 * @return: a boolean element that says if the first element is less or equal than the second
	*/
	@Override
	public boolean lessOrEqualAux(Parity other) throws SemanticException {
	    if (this == TOP && other == TOP)
	        return true;
	    else if (this == BOTTOM && other == BOTTOM)
	    	return true;
	    else if (this == TOP && other != TOP && other != BOTTOM)
	    	return false;
	    else if (this != TOP && this != BOTTOM && other == TOP)
	    	return true;
	    else if (this == BOTTOM && other != TOP && other != BOTTOM)
	    	return false;
	    else if (this != TOP && this != BOTTOM && other == BOTTOM)
	    	return true;
	    else
	        return this.lessOrEqual(other);
	}

	/*
	 * This functions returns TOP and BOTTOM unconditionally
	 */
	@Override
	public Parity top() {
		return TOP;
	}

	@Override
	public Parity bottom() {
		return BOTTOM;
	}
	
	/*
	 * This function evaluates constants considering two cases
	 * It just check if the constant is divisible by two and if it isn't
	 * In the positive case the number is EVEN 
	 * (in this case I also check if the number equals zero because zero is even but dividing it by zero it could return error so it would become BOTTOM)
	 * In the other case the number is ODD
	 * @input: a constant, variable whose value cannot be changed after it has been initialized, a program point, specific location in a program where the execution can reach, a semantic oracle, a tool or a component used in program analysis to provide information about the semantics of a program.
	 * @return: a Parity element representing the evaluation of the constant
	 */
	@Override
	public Parity evalNonNullConstant(
			Constant constant,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if (constant.getValue() instanceof Integer) {
			int v = (Integer) constant.getValue();
			if (v % 2 == 0 || v == 0)
				return EVEN;
			else
				return ODD;
		}
		return top();
	}
	
	/*
	 * This function evaluates the negation of the number, so it changes the sign but it doesn't change the Parity value
	 * @return: the negation of the element from which the function is called
	 */
	private Parity negate() {
		if (this == EVEN)
			return EVEN;
		else if (this == ODD)
			return ODD;
		else
			return this;
	}
	
	/*
	 * This function evaluates the unary expressions (so the negation operator)
	 * @input: a constant, variable whose value cannot be changed after it has been initialized, a program point, specific location in a program where the execution can reach, a semantic oracle, a tool or a component used in program analysis to provide information about the semantics of a program.
	 * @return: the evaluation of the element from which the function is called
	 */
	public Parity evalUnaryExpression(
			UnaryOperator operator,
			Parity arg,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if (arg == TOP) return TOP;
		else if (arg == BOTTOM) return BOTTOM;
		else if (operator instanceof NumericNegation)
			if (arg == TOP)
				return TOP;
			else
				return this.negate();
		return TOP;
	}

	/*
	 * This function evaluates the binary expressions such as addition, subtraction and multiplication
	 * First I check if the elements are TOP or BOTTOM
	 * If not I evaluate the operator:
	 * I evaluated mainly three operations: addition, subtraction and multiplication (division is not required, but in case of division the function returns TOP)
	 * @input: a constant, variable whose value cannot be changed after it has been initialized, a program point, specific location in a program where the execution can reach, a semantic oracle, a tool or a component used in program analysis to provide information about the semantics of a program.
	 * @return: the evaluation of the result of the binary expression
	 */
	public Parity evalBinaryExpression(
			BinaryOperator operator,
			Parity left,
			Parity right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if (left == TOP || right == TOP) return TOP;
		else if (left == BOTTOM || right == BOTTOM) return BOTTOM;
		else if (operator instanceof AdditionOperator) {
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
}

public Parity evalBranches (
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