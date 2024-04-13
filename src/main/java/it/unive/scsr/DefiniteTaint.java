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

	// Just same as ThreeLevelsTaint
	private static final DefiniteTaint TOP = new DefiniteTaint((byte)3);
	private static final DefiniteTaint TAINT = new DefiniteTaint((byte)(2));
	private static final DefiniteTaint CLEAN = new DefiniteTaint((byte)(1));
	private static final DefiniteTaint BOTTOM = new DefiniteTaint((byte)(0));

	private byte taint;
	
	public DefiniteTaint() {
		this((byte)3);
	}
	
	public DefiniteTaint(byte taint) {
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
	
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,		// ThreeLevelsTaint -> DefiniteTaint
			DefiniteTaint right,	// ThreeLevelsTaint -> DefiniteTaint
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if(left == TAINT || right == TAINT)
				return TAINT;
		else if(left == TOP || right == TOP)
				return TOP;	// either of them is TAINT but TOP
		return CLEAN;	// no TAINT, no TOP
	}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		return TOP;
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
		return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
		// return null;
	}
		
		
	
}
