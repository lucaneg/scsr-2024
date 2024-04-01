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

	private static final DefiniteTaint BOTTOM = new DefiniteTaint();
	private static final DefiniteTaint MAYBE = new DefiniteTaint();
	private static final DefiniteTaint TAINTED = new DefiniteTaint();
	private static final DefiniteTaint CLEAN = new DefiniteTaint();

	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		return MAYBE;
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		return false;
	}

	@Override
	public DefiniteTaint top() {
		return MAYBE;
	}

	@Override
	public DefiniteTaint bottom() {
		return BOTTOM;
	}

	@Override
	protected DefiniteTaint tainted() {
		return TAINTED;
	}

	@Override
	protected DefiniteTaint clean() {
		return CLEAN;
	}

	@Override
	public boolean isAlwaysTainted() {
		return this == TAINTED;
	}

	@Override
	public boolean isPossiblyTainted() {
		return this == MAYBE;
	}

	@Override
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,
			DefiniteTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if (left == TAINTED || right == TAINTED)
			return TAINTED;
		if (left == MAYBE || right == MAYBE)
			return MAYBE;

		return CLEAN;
	}

	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		return MAYBE;
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
		return this == BOTTOM ? Lattice.bottomRepresentation()
				: this == MAYBE ? Lattice.topRepresentation()
						: this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
	}

}
