package it.unive.scsr;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class DefiniteTaint extends BaseTaint<DefiniteTaint> {

	private static final DefiniteTaint TOP = new DefiniteTaint(3);
	private static final DefiniteTaint TAINTED = new DefiniteTaint(2);
	private static final DefiniteTaint CLEAN = new DefiniteTaint(1);
	private static final DefiniteTaint BOTTOM = new DefiniteTaint(0);

	private final int taint;

	public DefiniteTaint(int taint) {
		this.taint = taint;
	}

	public DefiniteTaint() {
		this(TOP.taint);
	}

	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		if (this == TOP || other == TOP) return TOP;
		if (this == TAINTED || other == TAINTED) return TAINTED;
		if (this == CLEAN && other == CLEAN) return CLEAN;
		return BOTTOM;
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		if (this == BOTTOM || other == TOP) return true;
		if (this == other) return true;
		if (this == CLEAN && other == TAINTED) return true;
		return false;
	}

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
		return this == TOP;
	}

	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,
			DefiniteTaint right,
			ProgramPoint pp) throws SemanticException {
		if (left == TAINTED || right == TAINTED) {
			return TAINTED;
		} else if (left == TOP || right == TOP) {
			return TOP;
		}
		return CLEAN;
	}

	@Override
	public DefiniteTaint wideningAux(DefiniteTaint other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	public StructuredRepresentation representation() {
		return this == BOTTOM ? Lattice.bottomRepresentation()
				: this == TOP ? Lattice.topRepresentation()
				: this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + taint;
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
		DefiniteTaint other = (DefiniteTaint) obj;
		return taint == other.taint;
	}
}
