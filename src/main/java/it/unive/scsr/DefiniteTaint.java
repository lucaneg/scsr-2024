package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class DefiniteTaint extends BaseTaint<DefiniteTaint> {

	// Enumeration representing the different states of taint
	private enum Type {
		TOP(2),
		TAINT(1),
		CLEAN(0),
		BOTTOM(-1);

		private final int value;

		Type(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// Constant elements representing the different states of the taint lattice
	public static final DefiniteTaint TOP = new DefiniteTaint(Type.TOP);
	public static final DefiniteTaint BOTTOM = new DefiniteTaint(Type.BOTTOM);
	public static final DefiniteTaint TAINT = new DefiniteTaint(Type.TAINT);
	public static final DefiniteTaint CLEAN = new DefiniteTaint(Type.CLEAN);

	private final Type type;

	// Default constructor setting the element to TOP
	public DefiniteTaint() {
		this(Type.TOP);
	}

	// Constructor for creating a DefiniteTaint element with a specific type
	public DefiniteTaint(Type type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		return 31 + this.type.getValue();
	}

	@Override
	public boolean equals(Object rhs) {
		if (this == rhs) return true;
		if (rhs == null) return false;
		if (this.getClass() != rhs.getClass()) return false;
		DefiniteTaint other = (DefiniteTaint) rhs;
		return other.type.getValue() == this.type.getValue();
	}

	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		// Computes the least upper bound of two elements
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		// Checks if this element is less than or equal to another element
		return false;
	}

	@Override
	public DefiniteTaint top() {
		// Returns the top element of the lattice
		return TOP;
	}

	@Override
	public DefiniteTaint bottom() {
		// Returns the bottom element of the lattice
		return BOTTOM;
	}

	@Override
	protected DefiniteTaint tainted() {
		// Returns the tainted element
		return TAINT;
	}

	@Override
	protected DefiniteTaint clean() {
		// Returns the clean element
		return CLEAN;
	}

	@Override
	public boolean isAlwaysTainted() {
		// Checks if this element is always tainted
		return this == TAINT;
	}

	@Override
	public boolean isPossiblyTainted() {
		// Checks if this element is possibly tainted
		return this == TOP;
	}

	@Override
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,
			DefiniteTaint right,
			ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {
		// Evaluates the taint of a binary expression
		if (left == TAINT || right == TAINT) return TAINT;
		if (left == TOP || right == TOP) return TOP;
		return CLEAN;
	}

	@Override
	public DefiniteTaint wideningAux(DefiniteTaint other) throws SemanticException {
		// Performs the widening operation
		return TOP;
	}

	@Override
	public StructuredRepresentation representation() {
		// Returns a string representation of the taint state
		switch(this.type) {
			case BOTTOM:
				return Lattice.bottomRepresentation();
			case TOP:
				return Lattice.topRepresentation();
			case CLEAN:
				return new StringRepresentation("_");
			case TAINT:
				return new StringRepresentation("#");
		}
		return null;
	}
}
