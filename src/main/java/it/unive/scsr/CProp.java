package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.analysis.dataflow.PossibleDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.*;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.*;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp>{

	private final Identifier id;
	private final Integer constant;

	public CProp() {
		this(null, null);
	}

	public CProp(Identifier id, Integer constant) {
		this.id = id;
		this.constant = constant;
	}

	private static Integer evaluateExp(SymbolicExpression exp, DefiniteDataflowDomain<CProp> domain){
		if(exp instanceof Constant c){
			if(c.getValue() instanceof Integer) return (Integer) c.getValue();
			return null;
		}

		if(exp instanceof Identifier id){
			Optional<CProp> variable = domain.getDataflowElements().stream().filter(cp -> cp.id.equals(id)).findFirst();
			return variable.map(cProp -> cProp.constant).orElse(null);
		}

		if(exp instanceof UnaryExpression ue){
			Integer i = evaluateExp(ue, domain);
			if(i == null) return null;
			return ue.getOperator() == NumericNegation.INSTANCE ? i : Integer.valueOf(-i);
		}

		if(exp instanceof BinaryExpression be){
			Integer left = evaluateExp(be.getLeft(), domain);
			Integer right = evaluateExp(be.getRight(), domain);
			Operator operator = be.getOperator();

			if(left == null || right == null) return null;
			if(operator instanceof AdditionOperator) return left + right;
			if(operator instanceof SubtractionOperator) return left - right;
			if(operator instanceof ModuloOperator) return left % right;
			if(operator instanceof MultiplicationOperator) return left * right;
			if(operator instanceof DivisionOperator) return left / right;
		}
		return null;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return evaluateExp(expression, domain) != null ? Collections.singleton(new CProp(id, constant)) : Collections.emptySet();
	}

	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Optional<CProp> cp = domain.getDataflowElements().stream().filter(x -> x.id.equals(id)).findFirst();
		return cp.map(Collections::singleton).orElse(Collections.emptySet());
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CProp cProp)) return false;
		return Objects.equals(id, cProp.id) && Objects.equals(constant, cProp.constant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, constant);
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
}
