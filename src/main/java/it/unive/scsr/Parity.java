package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity {

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
		if (this == ODD)
			return new StringRepresentation("ODD");
	}
	@Override
	public Signs evalBinaryExpression(
			BinaryOperator operator,
			Signs left,
			Signs right)
			throws SemanticException {
		if (operator instanceof AdditionOperator) {
			if (left == ODD) {
				if (right == TOP)
					return TOP;
				else if (right == ODD)
					return EVEN;
				else if (right == EVEN)
					return ODD;
				else
					return BOTTOM;
			} else if (left == EVEN) { // even
				if (right == TOP)
					return TOP;
				else if (right == ODD)
					return ODD;
				else if (right == EVEN)
					return EVEN;
				else
					return BOTTOM;
			} else if (left == TOP) {	// top
				if (right== TOP || right == EVEN || right == ODD)
					return TOP;
				else
					return BOTTOM;
			} else
				return BOTTOM;	// bottom
		} else if (operator instanceof SubtractionOperator) {
			if (left == ODD) {
				if (right == TOP)
					return TOP;
				else if (right == ODD)
					return EVEN;
				else if (right == EVEN)
					return ODD;
				else
					return BOTTOM;
			} else if (left == EVEN) { // even
				if (right == TOP)
					return TOP;
				else if (right == ODD)
					return ODD;
				else if (right == EVEN)
					return EVEN;
				else
					return BOTTOM;
			} else if (left == TOP) {	// top
				if (right== TOP || right == EVEN || right == ODD)
					return TOP;
				else
					return BOTTOM;
			} else
				return BOTTOM;	// bottom
		} else if (operator instanceof MultiplicationOperator) {
			if (left == ODD) {
				if (right == TOP)
					return TOP;
				else if (right == ODD)
					return ODD;
				else if (right == EVEN)
					return EVEN;
				else
					return BOTTOM;
			} else if (left == EVEN) { // even
				if (right == TOP)
					return TOP;
				else if (right == ODD)
					return EVEN;
				else if (right == EVEN)
					return EVEN;
				else
					return BOTTOM;
			} else if (left == TOP) {	// top
				if (right== TOP || right == EVEN || right == ODD)
					return TOP;
				else
					return BOTTOM;
			} else
				return BOTTOM;	// bottom
		} else if (operator instanceof NegativeOperator) {
			if (left == ODD)
				return ODD;
			else if (left == EVEN)
				return EVEN;
			else if (left == TOP)
				return TOP;
			else
				return BOTTOM;
	}
}
}