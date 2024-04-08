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
	public static final DefiniteTaint TOP = new DefiniteTaint(0);
	public static final DefiniteTaint BOTTOM = new DefiniteTaint(1);
	public static final DefiniteTaint CLEAN = new DefiniteTaint(2);
	public static final DefiniteTaint TAINTED = new DefiniteTaint(3);

	
	int taint;

	public DefiniteTaint(int i) {
		this.taint = i;
	}

	public DefiniteTaint() {
		this(0);
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
		return this==TAINTED;
	}

	@Override
	public boolean isPossiblyTainted() {
		return this==TOP;
	}
	
	public DefiniteTaint evalBinaryExpression(BinaryOperator operator,DefiniteTaint left,DefiniteTaint right,ProgramPoint pp,SemanticOracle oracle)throws SemanticException {
		if (left == TAINTED || right == TAINTED){
			return tainted();
		}

		if (left == TOP || right == TOP){
			return top();
		}
		return clean();
	}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
			return this.lub(other);
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
		return 
		this == BOTTOM ? Lattice.bottomRepresentation() : 
		this == TOP ? Lattice.topRepresentation() : 
		this == CLEAN ? new StringRepresentation("_") : 
			new StringRepresentation("#");
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
		if (taint != other.taint)
			return false;
		return true;
	}
	
		
		
	
}
