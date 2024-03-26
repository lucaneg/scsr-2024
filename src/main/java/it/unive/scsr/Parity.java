package it.unive.scsr;

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

	private static final Parity TOP = new Parity((byte) 3);
	private static final Parity BOTTOM = new Parity();
	private static final Parity EVEN = new Parity((byte) 2);
	private static final Parity ODD = new Parity((byte) 1);

	private final byte parity;

	public Parity(byte r_parity) {
		this.parity = r_parity;
	}

	public Parity() {
		this((byte) 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)				// Reference to the same object
			return true;
		
		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Parity arg = (Parity) obj;

		if (this.parity != arg.parity)
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		int prime = 43;
		int result = 1;

		return prime * result + this.parity;
	}

	boolean isOdd() {
		return this.equals(Parity.ODD);
	}

	boolean isEven() {
		return this.equals(Parity.EVEN);
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
		if (this == TOP)
			return Lattice.topRepresentation();
		if (this == BOTTOM)
			return Lattice.bottomRepresentation();
		if (this == EVEN)
			return new StringRepresentation("EVEN");
		return new StringRepresentation("ODD");
	}

	@Override
	public Parity lubAux(Parity other) throws SemanticException {
		return Parity.TOP;
	}

	@Override
	public boolean lessOrEqualAux(Parity other) throws SemanticException {
		return false;	
	}

	@Override
	public Parity top() {
		return Parity.TOP;
	}

	@Override
	public Parity bottom() {
		return Parity.BOTTOM;
	}
	
	@Override
	public Parity evalUnaryExpression(UnaryOperator operator, Parity arg, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		
		return (operator instanceof NumericNegation) ? arg : Parity.TOP;
	}

	@Override
	public Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right, ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {

		if (left.isBottom() || right.isBottom())
				return Parity.BOTTOM;

		if(left.isTop() || right.isTop())
				return Parity.TOP;
		
		if (operator instanceof AdditionOperator || operator instanceof SubtractionOperator) {
			if (left.equals(right))
				return Parity.EVEN;
			else 
				return Parity.ODD;
			
		} else if (operator instanceof MultiplicationOperator) {
			if (left.isEven() || right.isEven()) 
				return Parity.EVEN;
			else
				return Parity.ODD;
		} /* else if (operator instanceof DivisionOperator) {
			
		}*/

		return Parity.TOP; 
	}

	@Override
	public Parity evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return Parity.TOP;
	}

	@Override
	public Parity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		
		if (constant.getValue() instanceof Integer) {
			Integer value = (Integer) constant.getValue();

			if (value % 2 == 0) 
				return Parity.EVEN;
			else
				return Parity.ODD;
		}

		return Parity.TOP;
	}
}
