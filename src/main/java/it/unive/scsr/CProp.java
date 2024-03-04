package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;

import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>,CProp> {
	public final Identifier id;
	public final Integer constant;

	
	public CProp() {
		this.id = null;
		this.constant = null;
	}

	public CProp(Identifier id, Integer constant) {
		this.id = id;
		this.constant = constant;
	}

	


	

	@Override
	public String toString() {
		return "CProp [id=" + id + ", constant=" + constant + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((constant == null) ? 0 : constant.hashCode());
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
		CProp other = (CProp) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (constant == null) {
			if (other.constant != null)
				return false;
		} else if (!constant.equals(other.constant))
			return false;
		return true;
	}

	public Integer evaluate(SymbolicExpression expression, DefiniteDataflowDomain<CProp>domain){
		
		// using a variable to recall the constant, find its id and return its constant
		if (expression instanceof Identifier){
			for (CProp cp: domain.getDataflowElements())
				if (cp.id.equals(expression))
					return cp.constant;
			return null;
		}
		// if constant return its value, if not, return null
		if (expression instanceof Constant){
			Constant converted = (Constant) expression;
			Object convertedvalue=converted.getValue();
			if (convertedvalue instanceof Integer){
				return (Integer) convertedvalue;
			}else{
				return null;
			}
		}
		// if Unary return its value, if not, return null
		if (expression instanceof UnaryExpression){
			UnaryExpression converted = (UnaryExpression) expression;
			//recursive call
			Integer value=evaluate(converted.getExpression(), domain);

			if (value==null){
				return null;
			}
			if (converted.getOperator() instanceof NumericNegation){
				return -value;
			}
		}
		// if Binary expression return its calculated value, if not, return null
		if (expression instanceof BinaryExpression){
			BinaryExpression converted =(BinaryExpression) expression;
			BinaryOperator convertedoperator= converted.getOperator();
			//recursive call
			
			Integer left=evaluate(converted.getLeft(), domain);
			Integer right=evaluate(converted.getRight(), domain);
			

			if (left==null||right==null){
				return null;
			}
			
			if (convertedoperator instanceof AdditionOperator){
				return left+right;
			}
			if (convertedoperator instanceof DivisionOperator){
				if (right==0){
					return null;
				}
				return left/right;
			}
			if (convertedoperator instanceof MultiplicationOperator){
				return left*right;
			}
			if (convertedoperator instanceof SubtractionOperator){
				return left-right;
			}

		}


		return null;
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
	DefiniteDataflowDomain<CProp> domain){
				Collection<CProp> generated=new HashSet<>();
				Integer constant=evaluate(expression,domain);
				if(constant !=null){
					generated.add(new CProp(id,constant));
				}
				return generated;
	}

	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			 {
				Collection<CProp> generated=new HashSet<>();
				return generated;
		
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
	DefiniteDataflowDomain<CProp> domain)  {
				Collection<CProp> generated=new HashSet<>();
				//if its not a constant, then kill it
				boolean quit = expression instanceof Constant;
				for (CProp cp:domain.getDataflowElements())
					if(cp.id.equals(id) && !quit){
						generated.add(cp);
					}
				return generated;

	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
				Collection<CProp> generated=new HashSet<>();
				return generated;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}


	// IMPLEMENTATION NOTE:
	// the code below is outside of the scope of the course. You can uncomment
	// it to get your code to compile. Be aware that the code is written
	// expecting that a field named "id" and a field named "constant" exist
	// in this class: if you name them differently, change also the code below
	// to make it work by just using the name of your choice instead of
	// "id"/"constant". If you don't have these fields in your
	// solution, then you should make sure that what you are doing is correct :)

	@Override
	public StructuredRepresentation representation() {
		return new ListRepresentation(
				new StringRepresentation(this.id),
				new StringRepresentation(this.constant));
	}

	@Override
	public CProp pushScope(
			ScopeToken scope)
			throws SemanticException {
		return this;
	}

	@Override
	public CProp popScope(
			ScopeToken scope)
			throws SemanticException {
		return this;
	}

}
