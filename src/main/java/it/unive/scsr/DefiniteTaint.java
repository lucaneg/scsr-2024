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

	
	private static final DefiniteTaint MAYBETAINTED = new DefiniteTaint(10);
	private static final DefiniteTaint TAINTED = new DefiniteTaint(1);
	private static final DefiniteTaint CLEAN = new DefiniteTaint(0);
	private static final DefiniteTaint BOTTOM = new DefiniteTaint(-10);

 	private final Integer value;

	public DefiniteTaint() {
		this(10);
	}

	public DefiniteTaint(Integer value) {
		this.value = value;
	}

	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		return top();
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		return false;
	}

	@Override
	public DefiniteTaint top() {
		return MAYBETAINTED;
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
		return this == tainted();
	}

	@Override
	public boolean isPossiblyTainted() {
		return this == top();
	}
	
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,
			DefiniteTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
				if (operator instanceof BinaryOperator) {
						if (left == TAINTED || right == TAINTED) {
							return tainted();
						} else if (left == MAYBETAINTED || right == MAYBETAINTED) {
							return top();
						} else {
							return clean();
						}
					}
				return top();
			}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		return top();
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
				this == MAYBETAINTED ? Lattice.topRepresentation() : 
				this == CLEAN ? new StringRepresentation("_") : 
					new StringRepresentation("#");
	}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		
		
	
}
