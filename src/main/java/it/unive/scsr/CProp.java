package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.numeric.Subtraction;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

	public Identifier id;
	public Integer constant;

	
	public CProp(){
		this.id = null;
		this.constant = null;
	}

	public CProp(Identifier id, Integer constant) {
		this.id = id;
		this.constant = constant;
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null){
			return false;
		}
		if(getClass() != o.getClass()){
			return false;
		}
		CProp other = (CProp) o;
		if(id == null){
			if(other.id != null){
				return false;
			}
		}else if(!id.equals(other.id)){
			return false;
		}
		if(constant == null){
			if(other.constant != null){
				return false;
			}
		}else if(!constant.equals(other.constant)){
			return false;
		}
		return true;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result + ((id == null) ? 0 : id.hashCode());
		result = prime*result + ((constant == null) ? 0 : constant.hashCode());
		return result;
	}
	

	public Integer evaluate(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain) {
		if(expression instanceof Constant){
			if(((Constant) expression).getValue() instanceof Integer){
				return (Integer)((Constant) expression).getValue();
			}
		}
		else if(expression instanceof Identifier){
			for(CProp elem : domain.getDataflowElements()){
				if(elem.id.equals(expression)){
					return elem.constant;
				}
			}
		}
		else if(expression instanceof UnaryExpression){
			Integer value = evaluate(((UnaryExpression) expression).getExpression(), domain);
			if(value == null){
				return null;
			}
			if(((UnaryExpression) expression).getOperator() instanceof NumericNegation){
				return -value;
			}
		}
		else if(expression instanceof BinaryExpression){
			Integer l = evaluate(((BinaryExpression) expression).getLeft(), domain);
			Integer r = evaluate(((BinaryExpression) expression).getRight(), domain);

			if(r == null || l == null){
				return null;
			}

			BinaryOperator operator = ((BinaryExpression)expression).getOperator();

			if(operator instanceof AdditionOperator){
				return l+r;
			}
			if(operator instanceof Subtraction){
				return l-r;
			}
			if(operator instanceof MultiplicationOperator){
				return l*r;
			}
			if(operator instanceof DivisionOperator){
				if(r == 0){
					return null;
				}
				return l/r;
			}
		}
		return null;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}


	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Collection<CProp> gen = new HashSet<>();
		Integer val = evaluate(expression, domain);
		if(val != null){
			gen.add(new CProp(id, val));
		}
		return gen;
	}


	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return new HashSet<>();
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Collection<CProp> gen = new HashSet<>();
		if(expression instanceof Constant){
			return gen;
		}
		for(CProp elem : domain.getDataflowElements()){
			if(elem.id.equals(id)){
				gen.add(elem);
			}
		}
		return gen;
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return new HashSet<>();
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
				new StringRepresentation(id), 
				new StringRepresentation(constant));
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