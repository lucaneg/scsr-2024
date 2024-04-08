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

	public static final DefiniteTaint BOTTOM = new DefiniteTaint(0);
	public static final DefiniteTaint TOP = new DefiniteTaint(4);
	public static final DefiniteTaint CLEAN = new DefiniteTaint(1);
	public static final DefiniteTaint TAINTED = new DefiniteTaint(2);

	private final int taint_v;

	public DefiniteTaint() {
		this(DefiniteTaint.TOP.taint_v);
	}

	public DefiniteTaint(int r_taint) {
		this.taint_v = r_taint;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		
		if (other == null)
			return false;
		
		if (this.getClass() != other.getClass())
			return false;

		DefiniteTaint otherTaint = (DefiniteTaint) other;

		return this.taint_v == otherTaint.taint_v;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		return prime * result + taint_v;
	}

	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		return DefiniteTaint.TOP;
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		return false;
	}

	@Override
	public DefiniteTaint top() {
		return DefiniteTaint.TOP;
	}

	@Override
	public DefiniteTaint bottom() {
		return DefiniteTaint.BOTTOM;
	}

	@Override
	protected DefiniteTaint tainted() {
		return DefiniteTaint.TAINTED;
	}

	@Override
	protected DefiniteTaint clean() {
		return DefiniteTaint.CLEAN;
	}

	@Override
	public boolean isAlwaysTainted() {
		return this == DefiniteTaint.TAINTED;
	}

	@Override
	public boolean isPossiblyTainted() {
		return this == DefiniteTaint.TOP;
	}
	
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			ThreeLevelsTaint left,
			ThreeLevelsTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {

		if (left.isAlwaysTainted() || right.isAlwaysTainted())
			return DefiniteTaint.TAINTED;

		if (left.isPossiblyTainted() || right.isPossiblyTainted())
			return DefiniteTaint.TOP;

		return DefiniteTaint.CLEAN;
	}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		return DefiniteTaint.TOP;
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
	}
}
