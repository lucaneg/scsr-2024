package it.unive.scsr;


import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;


public class DefiniteTaintSolution extends BaseTaint<DefiniteTaintSolution>  {

	// lattice elements
	private static final DefiniteTaintSolution TOP = new DefiniteTaintSolution((byte) 3);
	private static final DefiniteTaintSolution TAINTED = new DefiniteTaintSolution((byte) 2);
	private static final DefiniteTaintSolution CLEAN = new DefiniteTaintSolution((byte) 1);
	private static final DefiniteTaintSolution BOTTOM = new DefiniteTaintSolution((byte) 0);
	
	private final byte taint;

	/**
	 * Builds a new instance of taint.
	 */
	public DefiniteTaintSolution() {
		this((byte) 3);
	}

	private DefiniteTaintSolution(
			byte v) {
		this.taint = v;
	}
	
	@Override
	public DefiniteTaintSolution lubAux(
			DefiniteTaintSolution other)
			throws SemanticException {
		// only happens with clean and tainted, that are not comparable
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(
			DefiniteTaintSolution other)
			throws SemanticException {
		// only happens with clean and tainted, that are not comparable
		return false;
	}

	@Override
	public DefiniteTaintSolution top() {
		return TOP;
	}

	@Override
	public DefiniteTaintSolution bottom() {
		return BOTTOM;
	}

	@Override
	protected DefiniteTaintSolution tainted() {
		return TAINTED;
	}

	@Override
	protected DefiniteTaintSolution clean() {
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
	public DefiniteTaintSolution evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaintSolution left,
			DefiniteTaintSolution right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		
		// both elements are tainted
		if (left == TAINTED && right == TAINTED)
			return TAINTED;
		
		// only one element is tainted
		if (left == TAINTED || right == TAINTED)
			return TOP;

		// at least an element is top
		if (left == TOP || right == TOP)
			return TOP;

		// both elements are clean
		return CLEAN;
	}
	
	@Override
	public DefiniteTaintSolution evalTernaryExpression(
			TernaryOperator operator,
			DefiniteTaintSolution left,
			DefiniteTaintSolution middle,
			DefiniteTaintSolution right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		
		if (left == TAINTED && right == TAINTED && middle == TAINTED)
			return TAINTED;
		
		if (left == TAINTED || right == TAINTED || middle == TAINTED)
			return TOP;

		if (left == TOP || right == TOP || middle == TOP)
			return TOP;

		return CLEAN;
	}
	
	@Override
	public DefiniteTaintSolution wideningAux(
			DefiniteTaintSolution other)
			throws SemanticException {
		// only happens with clean and tainted, that are not comparable
		return TOP;
	}

		@Override
	public StructuredRepresentation representation() {
		return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
		
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
		DefiniteTaintSolution other = (DefiniteTaintSolution) obj;
		if (taint != other.taint)
			return false;
		return true;
	}	
		
	@Override
	public String toString() {
		return representation().toString();
	}	
}
