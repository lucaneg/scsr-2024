package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;


public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

	// IMPLEMENTATION NOTE:
	// the code below is outside of the scope of the course. You can uncomment
	// it to get your code to compile. Be aware that the code is written
	// expecting that a field named "id" and a field named "constant" exist
	// in this class: if you name them differently, change also the code below
	// to make it work by just using the name of your choice instead of
	// "id"/"constant". If you don't have these fields in your
	// solution, then you should make sure that what you are doing is correct :)

	private final Identifier id;
	private final Integer constant;


	public CProp() {
		id = null;
		constant = null;
	}

	public CProp(Identifier id, int constant) {
		this.id = id;
		this.constant = constant;
	}

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

	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return new HashSet<>();
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {

		HashSet<CProp> set = new HashSet<>();
		Integer value = evaluate(expression,domain);
		if(value != null) set.add(new CProp(id, value));

		return set;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {

		HashSet<CProp> set = new HashSet<>();

		if(expression instanceof Constant) return set;

		for (CProp i : domain.getDataflowElements())
			if(i.id.equals(id))	
				set.add(i);

		return set;
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return new HashSet<>();
	}

	
	public Integer evaluate(SymbolicExpression exp,DefiniteDataflowDomain<CProp>domain){

		if(exp instanceof UnaryExpression){
			// the only unary expression implemented is the negation
			Integer value = evaluate(((UnaryExpression)exp).getExpression(), domain);
			if(value == null) 
				return null;
			if(((UnaryExpression)exp).getOperator() instanceof NumericNegation) 
				return -value;
		}
		else if (exp instanceof BinaryExpression){
			// first i have to recurse on the left and write expression
			Integer left=evaluate(((BinaryExpression)exp).getLeft(), domain);
			Integer right=evaluate(((BinaryExpression)exp).getRight(), domain);

			// than i can evalueate the expression based on its operator 
			BinaryOperator op = ((BinaryExpression)exp).getOperator();

			if(op instanceof AdditionOperator)        return left + right;
			if(op instanceof SubtractionOperator)     return left - right;
			if(op instanceof MultiplicationOperator)  return left * right;
			if(op instanceof DivisionOperator)        return left / right;

		}
		else if(exp instanceof TernaryExpression){
			// first i have to recurse on the left and write expression
			Integer left=evaluate(((TernaryExpression)exp).getLeft(), domain);
			Integer middle=evaluate(((TernaryExpression)exp).getMiddle(), domain);
			Integer right=evaluate(((TernaryExpression)exp).getRight(), domain);

			// than i can evalueate the expression based on its operator 
			BinaryOperator op = ((BinaryExpression)exp).getOperator();

			// i haven't found an implementation of the ternary boolean operator a ? b : c 
			//if(op instanceof ???)        return left ? middle : right;
			
			return null;
		}
		else if(exp instanceof Constant ){
			if(((Constant)exp).getValue() instanceof Integer)
				return (Integer)((Constant)exp).getValue();
		}
		else if(exp instanceof Identifier ){
			for (CProp i : domain.getDataflowElements())
				if (i.id.equals(exp))
					return i.constant;
		}

		return null;
	}

	@Override
	public int hashCode() {
	    int result = 123 + ((constant == null) ? 0 : constant.hashCode());
	    result = 123 * result + ((id == null) ? 0 : id.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null || getClass() != obj.getClass())
		return false;
	    CProp other = (CProp) obj;
	    return constant == other.constant && id.equals(other.id);
	}
	
}
