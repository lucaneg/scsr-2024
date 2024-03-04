package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;

import it.unive.lisa.program.cfg.ProgramPoint;

import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
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

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

	private final Identifier id;
	private final Integer constant;

	public CProp () {
		this.id = null;
		this.constant = null;
	}

	public CProp (Identifier r_id, Integer r_constant) {
		this.id = r_id;
		this.constant = r_constant;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof CProp))
			return false;

		CProp o = (CProp) obj;

		return this.constant == o.constant && id.equals(o.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, constant);
	}

	public Integer evaluate(SymbolicExpression sym_expr, DefiniteDataflowDomain<CProp> def_domain) {

		if (sym_expr instanceof UnaryExpression) {
			// -x
			UnaryExpression u = (UnaryExpression) sym_expr;
			Integer value = evaluate(u.getExpression(), def_domain);

			if (value == null)
				return null;
			
			if (u.getOperator() instanceof NumericNegation)
				return -value;

		} else if (sym_expr instanceof BinaryExpression) {
			
			BinaryExpression b = (BinaryExpression) sym_expr;
			
			Integer l = evaluate(b.getLeft(), def_domain);
			Integer r = evaluate(b.getRight(), def_domain);

			BinaryOperator operator = b.getOperator();

			if (l == null || r == null)
				return null;

			if (operator instanceof AdditionOperator)
				return l + r;
			else if (operator instanceof SubtractionOperator)
				return l - r;
			else if (operator instanceof MultiplicationOperator)
				return l * r;
			else if (operator instanceof DivisionOperator && r != 0) 
				return l / r;

		} else if (sym_expr instanceof Constant) {
			
			Constant c = (Constant) sym_expr;

			if (c.getValue() instanceof Integer)
				return (Integer) c.getValue();
			
		} else if (sym_expr instanceof Identifier) {
			
			Identifier i = (Identifier) sym_expr;

			for (CProp elem : def_domain.getDataflowElements()) {
				if (elem.id.equals(i))
					return elem.constant;
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
		
		HashSet<CProp> result = new HashSet<>();
		Integer r_constant = evaluate(expression, domain);

		if (r_constant != null)
				result.add(new CProp(id, r_constant));

		return result;
	}
	
	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, 
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
			
		return new HashSet<>();
	}
	
	
	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
	DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		
		HashSet<CProp> result = new HashSet<>();

		if (expression instanceof Constant)
			return result;

		for (CProp d_elem : domain.getDataflowElements()) {
			
			if(d_elem.id.equals(id))
				result.add(d_elem);
		
			}
			
		return result;
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
	// ----------------------------------------------------------
}
