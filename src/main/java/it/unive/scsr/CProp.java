package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
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
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;



public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>,CProp>{

	// IMPLEMENTATION NOTE:
	// the code below is outside of the scope of the course. You can uncomment
	// it to get your code to compile. Be aware that the code is written
	// expecting that a field named "id" and a field named "constant" exist
	// in this class: if you name them differently, change also the code below
	// to make it work by just using the name of your choice instead of
	// "id"/"constant". If you don't have these fields in your
	// solution, then you should make sure that what you are doing is correct :)

	
	private  Identifier id;
	private  Integer constant;
	
	
	public CProp(){
	
		id= null;
		constant= null;
	}
	
	
	public CProp(Identifier id, Integer constant){
		this.id=id;
		this.constant=constant;
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
	public boolean equals(
			Object obj) {
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
	public StructuredRepresentation representation() {
		return new ListRepresentation(
				new StringRepresentation(id), 
				new StringRepresentation(constant));
	}

	@Override
	public CProp pushScope(ScopeToken scope)
			throws SemanticException {
	return this;
	}

	@Override
	public CProp popScope(ScopeToken scope)
	   throws SemanticException {
		return this;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		
		return null;
	}
	
	
	public static Integer evaluateExp(SymbolicExpression exp, DefiniteDataflowDomain<CProp> domain) {
        if (exp instanceof Constant) {
            Constant c = (Constant) exp;
            if (c.getValue() instanceof Integer)
                return (Integer) c.getValue();
            return null;
        }

        if (exp instanceof Identifier) {
            Identifier id = (Identifier) exp;
            Optional<CProp> variable = domain.getDataflowElements().stream().filter(cp -> cp.id.equals(id)).findFirst();
            return variable.map(cProp -> cProp.constant).orElse(null);
        }

        if (exp instanceof UnaryExpression) {
            UnaryExpression ue = (UnaryExpression) exp;
            Integer i = evaluateExp(ue, domain);
            if (i == null)
                return null;
            return ue.getOperator() == NumericNegation.INSTANCE ? Integer.valueOf(-i) : i;
        }

        if (exp instanceof BinaryExpression) {
            BinaryExpression be = (BinaryExpression) exp;
            Integer left = evaluateExp(be.getLeft(), domain);
            Integer right = evaluateExp(be.getRight(), domain);
            BinaryOperator operator = be.getOperator();

            if (left == null || right == null)
                return null;
            if (operator instanceof AdditionOperator)
                return left + right;
            if (operator instanceof SubtractionOperator)
                return left - right;
            if (operator instanceof MultiplicationOperator)
                return left * right;
            if (operator instanceof DivisionOperator)
                return left / right;
        }
        return null;
    }


	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		
		Integer value = evaluateExp(expression, domain);
		return value != null ? Collections.singleton(new CProp(id, value)) : Collections.emptySet();
		
	}

	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return new HashSet<>();
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		
		return new HashSet<>();
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return new HashSet<>();
	}
}
