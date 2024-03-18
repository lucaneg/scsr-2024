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

//	@Override
//	public StructuredRepresentation representation() {
//		if (this == TOP)
//			return Lattice.topRepresentation();
//		if (this == BOTTOM)
//			return Lattice.bottomRepresentation();
//		if (this == EVEN)
//			return new StringRepresentation("EVEN");
//		return new StringRepresentation("ODD");
//	}
	
	
	

		// as this is a finite lattice, we can optimize by having constant elements
		// for each of them
		private static final Parity BOTTOM = new Parity(-10);
		private static final Parity EVEN = new Parity(-1);
		private static final Parity ODD = new Parity(1);
		private static final Parity TOP = new Parity(10);

		// this is just needed to distinguish the elements
		private final int Parity;

		public Parity() {
			this(10);
		}

		public Parity(int Parity) {
			this.Parity = Parity;
		}

		
		@Override
		public int hashCode() {
			return Objects.hash(Parity);
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
			return Parity == other.Parity;
		}

		public Parity top() {
			// the top element of the lattice
			// if this method does not return a constant value,
			// you must override the isTop() method!
			return TOP;
		}

		public Parity bottom() {
			// the bottom element of the lattice
			// if this method does not return a constant value,
			// you must override the isBottom() method!
			return BOTTOM;
		}

		public Parity lubAux(
				Parity other)
				throws SemanticException {
			// this and other are always incomparable when we reach here
			return TOP;
		}

		public boolean lessOrEqualAux(
				Parity other)
				throws SemanticException {
			// this and other are always incomparable when we reach here
			return false;
		}
		public StructuredRepresentation representation() {
			if (this == TOP)
			return Lattice.topRepresentation();
		if (this == BOTTOM)
			return Lattice.bottomRepresentation();
			if (this == EVEN)
				return new StringRepresentation("EVEN");
			return new StringRepresentation("ODD");
		}
		

		// logic for evaluating expressions below

		public Parity evalNonNullConstant(
				Constant constant,
				ProgramPoint pp,
				SemanticOracle oracle)
				throws SemanticException {
			if (constant.getValue() instanceof Integer) {
				int v = (Integer) constant.getValue();
				if (v%2==0)
					return EVEN;
				else
					return ODD;
			}
			return top();
		}

		private Parity negate() {
			if (this == EVEN)
				return EVEN;
			else if (this == ODD)
				return ODD;
			else
				return this;
		}

		public Parity evalUnaryExpression(
				UnaryOperator operator,
				Parity arg,
				ProgramPoint pp,
				SemanticOracle oracle)
				throws SemanticException {
			if(arg ==TOP)
				return TOP;
			else if(arg== BOTTOM)
				return BOTTOM;
			else if  (operator instanceof NumericNegation)
				return arg.negate();

			return TOP;
		}

		
		public Parity evalBinaryExpression(
				BinaryOperator operator,
				Parity left,
				Parity right,
				ProgramPoint pp,
				SemanticOracle oracle)
				throws SemanticException {
			if(left==TOP || right==TOP)
				return TOP;
			else if(left==BOTTOM || right==BOTTOM)
				return BOTTOM;
			else if  (operator instanceof AdditionOperator) {
				if (left == EVEN && right== EVEN) 
					return EVEN;
				if (left == ODD && right == ODD)
						return EVEN;
				if ((left == ODD && right == EVEN)||( right == ODD && left == EVEN))
					return ODD;
				else
					return TOP;
			} 
			else if (operator instanceof SubtractionOperator) {
				
					if (left == EVEN && right== EVEN) 
						return EVEN;
					if (left == ODD && right == ODD)
							return EVEN;
					if ((left == ODD && right == EVEN)||( right == ODD && left == EVEN))
						return ODD;
					else
					return TOP;
			} 
			else if (operator instanceof MultiplicationOperator) {
				if (left == EVEN && right== EVEN) 
					return EVEN;
				if (left == ODD && right == ODD)
						return ODD;
				if ((left == ODD && right == EVEN)||( right == ODD && left == EVEN))
					return EVEN;
				else
				return TOP;
			} 
			else if (operator instanceof DivisionOperator) {
				
				return TOP;
			}

			return TOP;
		}
	
	
}