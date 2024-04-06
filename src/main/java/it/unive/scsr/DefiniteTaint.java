package it.unive.scsr;


import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.analysis.taint.ThreeLevelsTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;


public class DefiniteTaint extends BaseTaint<DefiniteTaint>  {
	private int value;

    private static final DefiniteTaint TOP     = new DefiniteTaint(2);
    private static final DefiniteTaint CLEAN   = new DefiniteTaint(1);
    private static final DefiniteTaint TAINTED = new DefiniteTaint(0);
    private static final DefiniteTaint BOTTOM  = new DefiniteTaint(-1);

	public DefiniteTaint(int value) {
        this.value = value;
    }

    public DefiniteTaint() {
        this(TOP.value);
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

	@Override
	public DefiniteTaint evalBinaryExpression(BinaryOperator operator, DefiniteTaint left, DefiniteTaint right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		DefiniteTaint result;
		
		if (left == TAINTED || right == TAINTED) {
			result = TAINTED;
		} else if (left == TOP || right == TOP) {
			result = TOP;
		} else {
			result = CLEAN;
		}

		return result;
	}
	
	@Override
	public DefiniteTaint wideningAux(DefiniteTaint other) throws SemanticException {
		return TOP;
	}
	
	@Override
	public StructuredRepresentation representation() {
		if (this == TOP) {
			return Lattice.topRepresentation();

		} else if (this == CLEAN) {
			return new StringRepresentation("_");

		} else if (this == TAINTED) {
			return new StringRepresentation("#");

		} else {
			return Lattice.bottomRepresentation();
		}
	}
}
