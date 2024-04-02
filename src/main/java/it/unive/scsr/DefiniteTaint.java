package it.unive.scsr;

/**
 * @author Patrick Fabbiani - 869936
 * @university Ca' Foscari University - Venice (Italy)
 * @version 2.0.1
 * This is a simulation of an abstract domain representing the complete Taint using LiSA Analyzer library
 */

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.analysis.taint.ThreeLevelsTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StructuredRepresentation;

/**
 * Taint analysis is a process used in information security to identify the flow of user input through a system to understand the 
 * security implications of the system design.
 * @implNote BaseTaint: BaseTaint serves as a fundamental component in taint analysis, providing a structured 
 * approach to tracking and analyzing the taint of data as it flows through a program.
 * ValueDomain" refers to the set of possible values that a variable, data structure, or dataset can hold,
 * defining the range of valid or meaningful values within a specific context.
 */

/**
 * From top to bottom we have the following representation:
 * TOP - {TAINT, CLEAN} - BOTTOM
 * 
 * Higher element is TOP which comprehends the sub-levels TAINT and CLEAN
 * The lower level is BOTTOM which comprehends the empty element or if an element has an error
 */

public class DefiniteTaint extends BaseTaint<DefiniteTaint>  {
	
	/**
	 * I defined four DefiniteTaint elements:
	 * - BOTTOM which comprehends the empty element or if an element has an error;
	 * - TAINT represents data that is potentially unsafe or untrusted, often due to its origin or the operations performed on it. 
	 *   Tainted data requires special handling to prevent security vulnerabilities;
	 * - CLEAN represents data that is considered safe and trusted, typically because it has been validated or sanitized against potential threats. 
	 *   Clean data can be safely used in operations without the risk of introducing security vulnerabilities;
	 * - TOP which comprehends the sub-levels TAINT and CLEAN
	 */
	
	private static final DefiniteTaint TOP = new DefiniteTaint(true);
	private static final DefiniteTaint TAINT = new DefiniteTaint(true);
	private static final DefiniteTaint CLEAN = new DefiniteTaint(false);
	private static final DefiniteTaint BOTTOM = new DefiniteTaint(null);

	/**
	 * This is just needed to distinguish the elements
	 */
	Boolean taint;
	
	/**
	 * Constructors
	 */
	public DefiniteTaint() {
		this(true);
	}
	
	public DefiniteTaint(Boolean taint) {
		this.taint = taint;
	}
	
	/** 
	 * In this function I find the least upper bound of the two DefineTaint elements, the one which call the function and the one passed as parameter
	 * I considered four main cases:
	 * 1. if at least one element is TAINT the least upper bound is TAINT;
	 * 2. if both elements are CLEAN the least upper bound is CLEAN;
	 * 3. if both elements are BOTTOM the least upper bound is BOTTOM;
	 * 4. otherwise, if the two elements are TOP, the least upper bound is TOP
	 * @param a DefiniteTaint element to compare with the element from which the function is called to determine the least upper bound
	 * @return a DefiniteTaint element representing the least upper bound of the two elements in input
	 * @throws SemanticException: type of exception that occurs when there is a semantic error in a program
	*/
	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		if (this == TAINT || other == TAINT) {
            return TAINT;
        } else if (this == CLEAN && other == CLEAN) {
            return CLEAN;
        } else if (this == BOTTOM && other == BOTTOM)
			return BOTTOM;
		else return TOP;	
	}

	/**
	 * This function evaluates if, given two elements (the one which call the function and the one passed as parameter) if the first, so this, is less or equal than other
	 * I considered four cases:
	 * 1. if this is BOTTOM the function returns true,
	 * 2. if this is CLEAN the function returns true if other equals CLEAN or if other equals BOTTOM, otherwise it returns false,
	 * 3. if this is TAINT the function returns true only if other equals CLEAN or if other equals BOTTOM, otherwise it returns false,
	 * 3. otherwise it returns the result of the equality operator applied on the element other and the value TAINT
	 * @param a Taint element to compare with the element from which the function is called to determine the less or equal relation
	 * @return a boolean element that says if the first element is less or equal than the second
	 * @throws SemanticException: type of exception that occurs when there is a semantic error in a program
	*/
	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		if (this == BOTTOM) {
            return true;
        } else if ((this == CLEAN || this == TAINT) && other == TOP) {
            return true;
        }  else if (this == TOP) {
            return false;
        } else {
        	return this.lessOrEqual(other);
        }
	}
	
	/**
	 * This functions returns TOP, BOTTOM, TAINT and CLEAN unconditionally
	 */
	@Override
	public DefiniteTaint top() {
		return TOP;
	}
	
	@Override
	public DefiniteTaint bottom() {
		return BOTTOM;
	}
	
	@Override
	protected DefiniteTaint tainted() {
		return TAINT;
	}

	@Override
	protected DefiniteTaint clean() {
		return CLEAN;
	}
	
	/**
	 * This function checks if the element is Taint
	 */
	@Override
	public boolean isAlwaysTainted() {
		return this == TAINT;
	}
	
	/**
	 * This function checks if the element is TOP, so it would be possibly tainted
	 */
	@Override
	public boolean isPossiblyTainted() {
		return this == TOP;
	}
	
	/**
	 * This function evaluates the binary expressions such as addition, subtraction and multiplication
	 * First I check if the elements are TOP or BOTTOM
	 * If not I evaluate the operator evaluating three operations: addition, subtraction and multiplication (division is not required, but in case of division the function returns TOP)
	 * @param a constant, variable whose value cannot be changed after it has been initialized, a program point, specific location in a program where the execution can reach, a semantic oracle, a tool or a component used in program analysis to provide information about the semantics of a program.
	 * @return the evaluation of the result of the binary expression
	 * @exception SemanticException: type of exception that occurs when there is a semantic error in a program
	 */
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			ThreeLevelsTaint left,
			ThreeLevelsTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
			if (left.isTop() || right.isTop()) return TOP;
			else if (left.isBottom() || right.isBottom()) return BOTTOM;
			else if (operator instanceof AdditionOperator) {
				if (left.isAlwaysTainted() && right.isAlwaysTainted()) {
					return TAINT;
				} else if (left.isAlwaysClean() && right.isAlwaysClean()) {
					return CLEAN;
				} else if (!(left.equals(right))) {
					return TAINT;
				}
			}
			else if (operator instanceof SubtractionOperator) {
				if (left.isAlwaysTainted() && right.isAlwaysTainted()) {
					return TAINT;
				} else if (left.isAlwaysClean() && right.isAlwaysClean()) {
					return CLEAN;
				} else if (!(left.equals(right))) {
					return TAINT;
				}
			}
			else if (operator instanceof MultiplicationOperator) {
				if (left.isAlwaysTainted() && right.isAlwaysTainted()) {
					return TAINT;
				} else if (left.isAlwaysClean() && right.isAlwaysClean()) {
					return CLEAN;
				} else if (!(left.equals(right))) {
					return TAINT;
				}
			}
			else if (operator instanceof DivisionOperator) {
				return TOP;
			}
			return TOP;
	}
	
	/**
	 * The wideningAux method in the context of taint analysis is used to approximate the behavior of a program over time, 
	 * especially in static analysis. The concept of "widening" is a technique used to refine the analysis as it progresses, 
	 * allowing the analysis to converge to a more precise result. This is particularly useful in scenarios where the analysis 
	 * might not be able to precisely determine the taint state of data at every point in the program due to the complexity or 
	 * the dynamic nature of the code being analyzed.
	 * The wideningAux method is designed to be overridden by subclasses to provide specific behavior for widening operations. 
	 * In the provided code snippet, the wideningAux method is implemented to simply call the lubAux method.
	 * @param a Taint element from which execute the function
	 * @return the least upper bound of the two states being compared
	 * @throws SemanticException: type of exception that occurs when there is a semantic error in a program
	 */
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		return lubAux(other);
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
		// return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
		return null;
	}
		
		
	
}
