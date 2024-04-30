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

	public static final DefiniteTaint BOTTOM = new DefiniteTaint(0);

	public static final DefiniteTaint CLEAN = new DefiniteTaint(1);

	public static final DefiniteTaint TAINTED = new DefiniteTaint(2);

    public static final DefiniteTaint TOP = new DefiniteTaint(4);


	private final int taint;

	public DefiniteTaint() {
		this(DefiniteTaint.TOP.taint);
	}

	public DefiniteTaint(int tainted) {
		this.taint = tainted;
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

		return this.taint == otherTaint.taint;
	}

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(taint);
        return result;
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
			DefiniteTaint left,
			DefiniteTaint right,
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

    @Override
	public StructuredRepresentation representation() {
		return this == BOTTOM ?
		    Lattice.bottomRepresentation()
		        :
            this == TOP ?
                Lattice.topRepresentation()
                    :
                this == CLEAN ?
                    new StringRepresentation("_")
                        :
                    new StringRepresentation("#");
	}

}
