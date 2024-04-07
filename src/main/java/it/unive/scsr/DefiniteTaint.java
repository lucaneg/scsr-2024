package it.unive.scsr;


import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.analysis.taint.ThreeLevelsTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;


public class DefiniteTaint extends BaseTaint<DefiniteTaint>  {

	public static final DefiniteTaint TOP=new DefiniteTaint(true);
	public static final DefiniteTaint BOTTOM=new DefiniteTaint(null);
	public static final DefiniteTaint TAINT=new DefiniteTaint(true);
	public static final DefiniteTaint CLEAN=new DefiniteTaint(false);
	
	private Boolean tainted;
	
	public DefiniteTaint()
	{
		tainted=false;
	}
	
	public DefiniteTaint(Boolean tainted)
	{
		this.tainted=tainted;		
	}
	
	
	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		/*if(this.isAlwaysTainted().isAlwaysClean())*/
		
		
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		// TODO: to implement
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
		
		return this==TAINT;
	}

	@Override
	public boolean isPossiblyTainted() {
		return isTop();
	}
	
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			ThreeLevelsTaint left,
			ThreeLevelsTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		
		if(left.isPossiblyTainted() || right.isPossiblyTainted())
			return top();
		
		if(left.isBottom() || right.isBottom())
			return bottom();
		
		
		if(operator instanceof AdditionOperator || operator instanceof SubtractionOperator || operator instanceof MultiplicationOperator)
		{
			
			
			if(left.isAlwaysTainted() || right.isAlwaysTainted())
				return tainted();
			
			if(left.isAlwaysClean() && right.isAlwaysClean())
				return clean();
		}
		return TOP;
	}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		
		return lubAux(other);
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
