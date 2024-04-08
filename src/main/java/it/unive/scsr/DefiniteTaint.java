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


	private final String TOP = "TOP";
	private final String TAINT = "TAINT";
	private final String CLEAN = "CLEAN";
	private final String BOTTOM = "BOTTOM";

	private final String type;

	public DefiniteTaint() {
		this("TAINT");
	}

	public DefiniteTaint(String type) {
		this.type = type;
	}

	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		return new DefiniteTaint(TOP);
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		return false;
	}

	@Override
	public DefiniteTaint top() {
		return new DefiniteTaint(TOP);
	}

	@Override
	public DefiniteTaint bottom() {
		return new DefiniteTaint(BOTTOM);
	}

	@Override
	protected DefiniteTaint tainted() {
		return new DefiniteTaint(TAINT);
	}

	@Override
	protected DefiniteTaint clean() {
		return new DefiniteTaint(CLEAN);
	}

	@Override
	public boolean isAlwaysTainted() {
		return type.equals(TAINT);
	}

	@Override
	public boolean isPossiblyTainted() {
		return type.equals(TOP);
	}

	@Override
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,
			DefiniteTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		String result = CLEAN;
		if (left.isAlwaysTainted() || right.isAlwaysTainted()) {
			result = TAINT;
		} else if (left.isPossiblyTainted() || right.isPossiblyTainted()) {
			result = TOP;
		}
		return new DefiniteTaint(result);
	}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		return new DefiniteTaint(TOP);
	}

	@Override
	public StructuredRepresentation representation() {
		// return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
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
