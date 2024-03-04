package it.unive.scsr;

import java.util.Collection;
import java.util.stream.Collectors;
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
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp  implements 

	DataflowElement<
		DefiniteDataflowDomain<CProp>,
			CProp>{

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

	public CProp(){
		this.id = null;
		this.constant = null;
	}

	public CProp(
		Identifier id,
		Integer constant){
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
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}


	public Integer expEvaluation(
		SymbolicExpression expression, 
		DefiniteDataflowDomain<CProp> domain){

		if (expression instanceof Constant)
			return ((Constant) expression).getValue() instanceof Integer ? (Integer) (((Constant) expression).getValue()) : null;
			
		if (expression instanceof Identifier) return domain.getDataflowElements().stream()
				.filter(ex -> ex.id.equals(expression))
				.map(ex -> ex.constant)
				.findFirst().orElse(null);
		
		if (expression instanceof UnaryExpression) {
			Integer r = expEvaluation(((UnaryExpression) expression).getExpression(),domain);
			return r.equals(null) ? null : (((UnaryExpression) expression).getOperator() instanceof NumericNegation ? -r : null);
		}

		if (expression instanceof BinaryExpression) { 
			BinaryExpression expr = (BinaryExpression) expression;
			Integer rl = expEvaluation(expr.getLeft(), domain);
			Integer rr = expEvaluation(expr.getRight(), domain);
			if (rl.equals(null) || rr.equals(null)) return null;
			if (expr.getOperator() instanceof AdditionOperator) return rl + rr;
			if (expr.getOperator() instanceof SubtractionOperator) return rl - rr;
			if (expr.getOperator() instanceof MultiplicationOperator) return rl * rr;
			if (expr.getOperator() instanceof DivisionOperator) return rr != 0 ?  rl / rr : null;
		}
		return null;
	}


	@Override
	public Collection<CProp> gen(
		Identifier id, 
		ValueExpression expression, 
		ProgramPoint pp,
		DefiniteDataflowDomain<CProp> domain){
			Integer constant = expEvaluation(expression, domain);
			Collection<CProp> cpset = new HashSet<>();
			if (!constant.equals(null)) cpset.add(new CProp(id,constant));
			return cpset;
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

	@Override
	public Collection<CProp> gen(
		ValueExpression expression, 
		ProgramPoint pp, 
		DefiniteDataflowDomain<CProp> domain){
		return new HashSet<CProp>();
	}

	@Override
	public Collection<CProp> kill(
		Identifier id, 
		ValueExpression expression, 
		ProgramPoint pp,
		DefiniteDataflowDomain<CProp> domain){
			return domain.getDataflowElements().stream()
				.filter(cp -> cp.id.equals(expression))
				.collect(Collectors.toSet());
	}

	@Override
	public Collection<CProp> kill(
		ValueExpression expression, 
		ProgramPoint pp, 
		DefiniteDataflowDomain<CProp> domain){
			return new HashSet<CProp>();
	}
}
