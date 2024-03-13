package it.unive.scsr;

import java.util.Objects;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity implements BaseNonRelationalValueDomain<Parity> {

    private int value;

    private static final Parity TOP     = new Parity(2);
	private static final Parity ODD     = new Parity(1);
    private static final Parity EVEN    = new Parity(0);
    private static final Parity BOTTOM  = new Parity(-1);

    public Parity(int value) {
        this.value = value;
    }

    public Parity() {
        this(TOP.value);
    }

    private Parity evalAdditionOperation(Parity left, Parity right) {
		Parity result = TOP;

        if ((left == EVEN && right == EVEN) || (left == ODD && right == ODD)) {
			result = EVEN;

		} else if ((left == ODD && right == EVEN) || (left == EVEN && right == ODD)) {
			result = ODD;
		}

		return result;
    }

	private Parity evalMultiplicationOperation(Parity left, Parity right) {
		Parity result = TOP;

        if (left == EVEN || right == EVEN) {
			result = EVEN;
		} else if (left == ODD && right == ODD) {
			result = ODD;
		}

		return result;
    }

    @Override
    public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        return (operator instanceof NumericNegation) ? arg : TOP;
    }

    @Override
    public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        
        if (constant.getValue() instanceof Integer) {
            Integer x = (Integer) constant.getValue();
            return (x % 2 == 0) ? EVEN : ODD;
        } else {
            return TOP;
        }
    }

    @Override
    public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
        Parity result = TOP;
        
        if (operator instanceof AdditionOperator) {
            result = evalAdditionOperation(left, right);

        } else if (operator instanceof SubtractionOperator) {
            result = evalAdditionOperation(left, right);	// It's the same for subtraction

        } else if (operator instanceof MultiplicationOperator) {
            result = evalMultiplicationOperation(left, right);
		}

        return result;
    }

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;	
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		Parity other = (Parity) obj;

		return Objects.equals(this.value, other.value);
	}

    @Override
    public Parity lubAux(Parity other) throws SemanticException {
        return TOP;
    }

    @Override
    public boolean lessOrEqualAux(Parity other) throws SemanticException {
        return false;
    }

    @Override
    public Parity top() {
        return TOP;
    }

    @Override
    public Parity bottom() {
        return BOTTOM;
    }

    @Override
    public StructuredRepresentation representation() {
        StructuredRepresentation repr = null;

        if (this == BOTTOM) {
            repr = Lattice.bottomRepresentation();
        } else if (this == TOP) {
            repr = Lattice.topRepresentation();
        } else if (this == EVEN) {
            repr = new StringRepresentation("EVEN");
        } else if (this == ODD) {
            repr = new StringRepresentation("ODD");
        }

        return repr;
    }
    
}