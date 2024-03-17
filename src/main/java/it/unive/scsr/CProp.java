package it.unive.scsr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>,CProp> {
	private final Identifier id;
    private final Integer constant;

	public CProp(Identifier id, Integer constant){
        this.id = id;
        this.constant = constant;
    }


	public CProp(){
		this.id = null;
        this.constant = null;
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




	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		// we only contain information about one identifier, so we return it
		Set<Identifier> result = new HashSet<>();
		if (id != null)
			result.add(id);
		return result;
	}


	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Collection<CProp> result = new HashSet<>();
		Integer constant = eval(expression,domain);
		result.add(new CProp(id, constant));
		return result;
	}


	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Collection<CProp> result = new HashSet<>();
		return result;
	}


	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		// if the expression is not a constant, we kill the corresponding CProp
		Collection<CProp> result = new HashSet<>();
		if (!(expression instanceof Constant))
			result.add(new CProp(id, null));
		return result;
	}


	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		// we return a singleton set containing the killed CProp
		Collection<CProp> result = new HashSet<>();
		return result;
	}

	public Integer eval(SymbolicExpression symbolicExpression, DefiniteDataflowDomain<CProp>domain){
		/**
		 * Evaluates the given symbolic expression using the given domain of CProp.
		 * If the expression is a constant, returns the constant value, otherwise returns null.
		 * return the value of the given symbolic expression, or null if it is not a constant
		 */
		if(symbolicExpression instanceof Constant){
			return (Integer) ((Constant) symbolicExpression).getValue();
		}

		if(symbolicExpression instanceof Identifier){
			Identifier id = (Identifier) symbolicExpression;
			for (CProp cprop : domain.getDataflowElements()){
				if(cprop.id.equals(id)){
					return cprop.constant;
				}
			}
		}
		//evaluates the given symbolic expression using the given domain of CProp
		//if the expression is a constant, returns the constant value, otherwise returns null
		//this function is not used in the current implementation, but it might be useful in the future
		//so we keep it here as a comment
		
		if (symbolicExpression instanceof UnaryExpression){
			UnaryExpression unary = (UnaryExpression) symbolicExpression;
			if(unary.getOperator() instanceof NumericNegation){
				return -eval(unary.getExpression(), domain);
			}
		}
		// Evaluates a binary expression using the given domain of CProp,
		// returns the result of the computation, or null if it cannot be computed.
		if(symbolicExpression instanceof BinaryExpression){
			BinaryExpression binary = (BinaryExpression) symbolicExpression;
			Integer rightOperand = eval(binary.getRight(), domain);
            Integer leftOperand = eval(binary.getLeft(), domain);
			if(rightOperand == null || leftOperand == null) return null;
			if(binary.getOperator() instanceof AdditionOperator)	return leftOperand + rightOperand;
			if(binary.getOperator() instanceof SubtractionOperator)	return leftOperand - rightOperand;
			if(binary.getOperator() instanceof MultiplicationOperator)	return leftOperand * rightOperand;
			if(binary.getOperator() instanceof DivisionOperator)	return leftOperand / rightOperand;	
		}
		return null;
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