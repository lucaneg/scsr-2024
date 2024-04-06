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

    private static final DefiniteTaint TOP = new DefiniteTaint(1);
    private static final DefiniteTaint CLEAN = new DefiniteTaint(2);
    private static final DefiniteTaint TAINT = new DefiniteTaint(3);
    private static final DefiniteTaint BOTTOM = new DefiniteTaint(0);

    int value;


    public DefiniteTaint() {
        this.value = 1; // TOP
    }

    public DefiniteTaint(int value) {
        this.value = value;
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

    @Override
    public DefiniteTaint evalBinaryExpression(
            BinaryOperator operator,
            DefiniteTaint left,
            DefiniteTaint right,
            ProgramPoint pp,
            SemanticOracle oracle)
            throws SemanticException {
			
            if(left == TAINT || right == TAINT)
                return TAINT;
            if(left == TOP || right == TOP)
                return TOP;
            return CLEAN;
    }

    @Override
    public DefiniteTaint wideningAux(
            DefiniteTaint other)
            throws SemanticException {

        return TOP;
    }

    @Override
    public StructuredRepresentation representation() {
        return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
    }



}