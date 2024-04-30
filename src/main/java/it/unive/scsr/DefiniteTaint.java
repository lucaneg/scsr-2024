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

	@Override
	public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
		// TODO: to implement
		return null;
	}

	@Override
	public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
		// TODO: to implement
		return false;
	}

	@Override
	public DefiniteTaint top() {
		// TODO: to implement
		return null;
	}

	@Override
	public DefiniteTaint bottom() {
		// TODO: to implement
		return null;
	}

	@Override
	protected DefiniteTaint tainted() {
		// TODO: to implement
		return null;
	}

	@Override
	protected DefiniteTaint clean() {
		// TODO: to implement
		return null;
	}

	@Override
	public boolean isAlwaysTainted() {
		// TODO: to implement
		return false;
	}

	@Override
	public boolean isPossiblyTainted() {
		// TODO: to implement
		return false;
	}
	
	public DefiniteTaint evalBinaryExpression(
			BinaryOperator operator,
			DefiniteTaint left,
			DefiniteTaint right,
			ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		// TODO: to implement
		return null;
	}
	
	@Override
	public DefiniteTaint wideningAux(
			DefiniteTaint other)
			throws SemanticException {
		// TODO: to implement
		return null;
	}


public class DefiniteTaint extends BaseTaint<DefiniteTaint> {

    private static final DefiniteTaint TOP = new DefiniteTaint(3);
    private static final DefiniteTaint TAINTED = new DefiniteTaint(2);
    private static final DefiniteTaint CLEAN = new DefiniteTaint(1);
    private static final DefiniteTaint BOTTOM = new DefiniteTaint(0);

    int taint;

    public DefiniteTaint() {
        this(3);
    }

    public DefiniteTaint(int taint) {
        this.taint = taint;
    }

    @Override
    public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
        return TOP; // this function is called when one element is tainted and the other clean, lub(taint, clean) is top
    }

    @Override
    public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
        return false; // the two elements are not in relation one with the other
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
        return this == TAINTED; // the element is always tainted only if it is definitely tainted
    }

    @Override
    public boolean isPossiblyTainted() {
        return this == TOP; // return the representation of possibilty tainted
    }

    @Override
    public DefiniteTaint evalBinaryExpression(
            BinaryOperator operator,
            DefiniteTaint left,
            DefiniteTaint right,
            ProgramPoint pp,
            SemanticOracle oracle)
            throws SemanticException {
        if (left == TAINTED || right == TAINTED)
            return TAINTED;
        else if (left == TOP || right == TOP)
            return TOP;
        return CLEAN;
    }

    @Override
    public DefiniteTaint wideningAux(
            DefiniteTaint other)
            throws SemanticException {
        return TOP; // since it is called with clean and tainted only, that are not compatible
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefiniteTaint that = (DefiniteTaint) o;
        return taint == that.taint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taint);
    }

}
