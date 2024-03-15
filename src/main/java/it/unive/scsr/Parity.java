package it.unive.scsr;

import java.util.Objects;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class Parity implements BaseNonRelationalValueDomain<Parity> {

	// IMPLEMENTATION NOTE:
	// the code below is outside of the scope of the course. You can uncomment
	// it to get your code to compile. Be aware that the code is written
	// expecting that you have constants for identifying top, bottom, even and
	// odd elements as we saw for the sign domain: if you name them differently,
	// change also the code below to make it work by just using the name of your
	// choice. If you use methods instead of constants, change == with the
	// invocation of the corresponding method

	private static final Parity EVEN=new Parity(0);
	private static final Parity ODD=new Parity(1);
	private static final Parity TOP=new Parity(2);
	private static final Parity BOTTOM=new Parity(-1);
	
	private int parityValue;


	public Parity()
	{
		parityValue=-1; /*default value is top*/
	}
	
	public Parity(int value)
	{
		parityValue=value;
	}
	
	
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
	public int hashCode() {
		return Objects.hash(parityValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parity other = (Parity) obj;
		return parityValue == other.parityValue;
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
	
	public Parity evalNonNullConstant(Constant constant,ProgramPoint pp,SemanticOracle oracle) throws SemanticException
	{
		Object value =constant.getValue();
		
		if(value instanceof Integer)
		{
			Integer i =(Integer) value;
			
			if(i%2==0)
				return EVEN;
			else
				return ODD;
		}
		return TOP;
	}
	
	public Parity evalUnaryExpression(UnaryOperator operator,Parity argument,ProgramPoint pp,SemanticOracle oracle) throws SemanticException
	{
		if(operator instanceof NumericNegation)
		{
			if(argument==TOP)
				return TOP;
			
			else if(argument==EVEN)
				return EVEN;
			
			else
				return ODD;
		
		}
		return TOP;
	}
	
	public Parity evalBinaryExpression(BinaryOperator operator,Parity lhs,Parity rhs,ProgramPoint pp,SemanticOracle oracle) throws SemanticException
	{
		if(operator instanceof AdditionOperator || operator instanceof SubtractionOperator )
		{
			if(lhs==EVEN && rhs==EVEN)
				return EVEN;
			
			else if((lhs==ODD && rhs ==EVEN)|| (lhs==EVEN ||rhs==ODD))
			{
				return ODD;
			}
			
			else
				return EVEN;
			
		}
		else if(operator instanceof MultiplicationOperator)
		{
			
			
		}
		else if(operator instanceof DivisionOperator)
		{
			return TOP;
			
		}
		
		return TOP;
	}
}