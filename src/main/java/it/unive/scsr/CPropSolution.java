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
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CPropSolution implements DataflowElement<DefiniteDataflowDomain<CPropSolution>, CPropSolution> {

	private final Identifier id;

	private final Integer constant;

	public CPropSolution(
			Identifier id,
			Integer constant) {
		super();
		this.id = id;
		this.constant = constant;
	}

	public CPropSolution() {
		this(null, null);
	}

	@Override
	public int hashCode() {
		return Objects.hash(constant, id);
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
		CPropSolution other = (CPropSolution) obj;
		return Objects.equals(constant, other.constant) && Objects.equals(id, other.id);
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}

	private static Integer getValueOf(
			Identifier id,
			DefiniteDataflowDomain<CPropSolution> domain) {
		for (CPropSolution cp : domain.getDataflowElements())
			if (cp.id.equals(id))
				return cp.constant;
		return null;
	}

	private static Integer eval(
			ValueExpression expression,
			DefiniteDataflowDomain<CPropSolution> domain) {
		if (expression == null)
			return null;

		if (expression instanceof Constant) {
			Object value = ((Constant) expression).getValue();
			if (value instanceof Integer)
				return (Integer) value;
		}

		if (expression instanceof Identifier)
			return getValueOf((Identifier) expression, domain);

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;
			UnaryOperator operator = unary.getOperator();
			ValueExpression arg = (ValueExpression) unary.getExpression();

			Integer value = eval(arg, domain);
			if (value == null)
				return null;
			if (operator instanceof NumericNegation)
				return -value;
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;
			BinaryOperator operator = binary.getOperator();
			ValueExpression left = (ValueExpression) binary.getLeft();
			ValueExpression right = (ValueExpression) binary.getRight();

			Integer lvalue = eval(left, domain);
			Integer rvalue = eval(right, domain);
			if (lvalue == null || rvalue == null)
				return null;
			if (operator instanceof AdditionOperator)
				return lvalue + rvalue;
			if (operator instanceof SubtractionOperator)
				return lvalue - rvalue;
			if (operator instanceof MultiplicationOperator)
				return lvalue * rvalue;
			if (operator instanceof DivisionOperator)
				return lvalue / rvalue;
		}

		return null;
	}

	@Override
	public Collection<CPropSolution> gen(
			Identifier id,
			ValueExpression expression,
			ProgramPoint pp,
			DefiniteDataflowDomain<CPropSolution> domain)
			throws SemanticException {
		Integer value = eval(expression, domain);
		if (value != null)
			return Collections.singleton(new CPropSolution(id, value));
		return Collections.emptySet();
	}

	@Override
	public Collection<CPropSolution> gen(
			ValueExpression expression,
			ProgramPoint pp,
			DefiniteDataflowDomain<CPropSolution> domain)
			throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public Collection<CPropSolution> kill(
			Identifier id,
			ValueExpression expression,
			ProgramPoint pp,
			DefiniteDataflowDomain<CPropSolution> domain)
			throws SemanticException {
		Collection<CPropSolution> result = new HashSet<>();

		for (CPropSolution cp : domain.getDataflowElements())
			if (cp.id.equals(id))
				result.add(cp);

		return result;
	}

	@Override
	public Collection<CPropSolution> kill(
			ValueExpression expression,
			ProgramPoint pp,
			DefiniteDataflowDomain<CPropSolution> domain)
			throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public StructuredRepresentation representation() {
		return new ListRepresentation(
				new StringRepresentation(id),
				new StringRepresentation(constant));
	}

	@Override
	public CPropSolution pushScope(
			ScopeToken scope)
			throws SemanticException {
		return this;
	}

	@Override
	public CPropSolution popScope(
			ScopeToken scope)
			throws SemanticException {
		return this;
	}
}
