package it.unive.scsr;


import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;


public class DefiniteTaint extends BaseTaint<DefiniteTaint>  {

	private final TaintState state;

	private DefiniteTaint(TaintState state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (state == null ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		DefiniteTaint other = (DefiniteTaint) obj;
		if (state == null) {
			return other.state == null;
		} else {
			return state.equals(other.state);
		}
	}

	private enum TaintState {
		TAINTED, CLEAN, TOP, BOTTOM
	}

	private static final DefiniteTaint TAINTED = new DefiniteTaint(TaintState.TAINTED);
	private static final DefiniteTaint CLEAN = new DefiniteTaint(TaintState.CLEAN);
	private static final DefiniteTaint TOP = new DefiniteTaint(TaintState.TOP);
	private static final DefiniteTaint BOTTOM = new DefiniteTaint(TaintState.BOTTOM);

	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
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
		return this.state == TaintState.TAINTED;
	}

	@Override
	public boolean isPossiblyTainted() {
		return this.state == TaintState.TAINTED || this.state == TaintState.TOP;
	}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		return TOP;
	}

	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,
			DefiniteTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {

		if (left.isAlwaysTainted() || right.isAlwaysTainted()) {
			return TAINTED;
		}
		if (left.isPossiblyTainted() || right.isPossiblyTainted()) {
			return TOP;
		}
		return CLEAN;
	}
	
		@Override
	public StructuredRepresentation representation() {
		 return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
	}
}
