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

	private final int taint;

	public static final DefiniteTaint BOTTOM = new DefiniteTaint(0);
	public static final DefiniteTaint TOP = new DefiniteTaint(1);
	public static final DefiniteTaint CLEAN = new DefiniteTaint(2);
	public static final DefiniteTaint TAINTED = new DefiniteTaint(3);

	public DefiniteTaint() {
		this.taint = TOP.taint;
	}

	public DefiniteTaint(int taint) {
		this.taint = taint;
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
		// if this is tainted we return true
		return this == TAINTED;
	}

	@Override
	public boolean isPossiblyTainted() {
		// if we are TOP there is a possibility that we are tainted
		return this == TOP;
	}
	
	public DefiniteTaint evalBinaryExpression( BinaryOperator operator, ThreeLevelsTaint left, ThreeLevelsTaint right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
	
		// if at least one component of the expression is tainted the result will be too
		if (left.isAlwaysTainted() || right.isAlwaysTainted())
			return DefiniteTaint.TAINTED;

		// if we are not sure of the taint state of the components the result will be TOP
		if (left.isPossiblyTainted() || right.isPossiblyTainted())
			return DefiniteTaint.TOP;

		return DefiniteTaint.CLEAN;
	}
	
	@Override
	public DefiniteTaint wideningAux(DefiniteTaint other) throws SemanticException {
		return TOP;
	}

	@Override
	public StructuredRepresentation representation() {
		return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
	}

}
