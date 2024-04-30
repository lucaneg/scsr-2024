package it.unive.scsr;


import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;


public class Taint extends BaseTaint<Taint> {

	
	private static final Taint TAINT = new Taint(true);
	private static final Taint CLEAN = new Taint(false);
	private static final Taint BOTTOM = new Taint(null);
	
	
	Boolean taint;
	
	public Taint() {
		this(true);
	}
	
	public Taint(Boolean taint) {
		this.taint = taint;
		
	}
	
	@Override
	public Taint lubAux(Taint other) throws SemanticException {
		return TAINT;
	}

	@Override
	public boolean lessOrEqualAux(Taint other) throws SemanticException {
		return false;
	}

	@Override
	public Taint top() {
		
		return TAINT;
	}

	@Override
	public Taint bottom() {
		return BOTTOM;
	}

	@Override
	public StructuredRepresentation representation() {
		return this == BOTTOM ? Lattice.bottomRepresentation()
					: this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");

	}

	
	@Override
	public Taint wideningAux(Taint other) throws SemanticException {
		
		return TAINT;
	}

	@Override
	protected Taint tainted() {
		return TAINT;
	}

	@Override
	protected Taint clean() {
		return CLEAN;
	}

	@Override
	public boolean isAlwaysTainted() {
		return false;
	}

	@Override
	public boolean isPossiblyTainted() {
		
		return this == TAINT;
	}

}
