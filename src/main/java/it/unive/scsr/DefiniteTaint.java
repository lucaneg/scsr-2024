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


	private final String TOP_s = "TOP";
	private final String TAINT_s = "TAINT";
	private final String CLEAN_s = "CLEAN";
	private final String BOTTOM_s = "BOTTOM";

	private final DefiniteTaint TOP = new DefiniteTaint(TOP_s);
	private final DefiniteTaint TAINT = new DefiniteTaint(TAINT_s);
	private final DefiniteTaint CLEAN = new DefiniteTaint(CLEAN_s);
	private final DefiniteTaint BOTTOM = new DefiniteTaint(BOTTOM_s);

	private final String type;

	public DefiniteTaint() {
		this("TOP");
	}

	public DefiniteTaint(String type) {
		this.type = type;
	}

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
		return TAINT;
	}

	@Override
	protected DefiniteTaint clean() {
		return CLEAN;
	}

	@Override
	public boolean isAlwaysTainted() {
		return this == TAINT;
	}

	@Override
	public boolean isPossiblyTainted() {
		return this == TOP;
	}

	@Override
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,
			DefiniteTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		String result = CLEAN_s;
		if (left.isAlwaysTainted() || right.isAlwaysTainted()) {
			result = TAINT_s;
		} else if (left.isPossiblyTainted() || right.isPossiblyTainted()) {
			result = TOP_s;
		}
		return new DefiniteTaint(result);
	}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		return TOP;
	}

	@Override
	public StructuredRepresentation representation() {
		// return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
		switch(this.type) {
			case BOTTOM_s:
				return Lattice.bottomRepresentation();
			case TOP_s:
				return Lattice.topRepresentation();
			case CLEAN_s:
				return new StringRepresentation("_");
			case TAINT_s:
				return new StringRepresentation("#");
		}
		return null;
	}
		
	   @Override
    public int hashCode() {
        return 31 + this.type.hashCode();
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) return true;
        if (rhs == null) return false;
        if (this.getClass() != rhs.getClass()) return false;
        DefiniteTaint other = (DefiniteTaint) rhs;
        return other.type.hashCode() == this.type.hashCode();
    }
}
