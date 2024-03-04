package it.unive.scsr;

import java.util.*;

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
import it.unive.lisa.util.representation.StructuredRepresentation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;


public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

	private final Identifier id;
	private final Integer constant;

	public CProp(Identifier id, Integer constant) {
		this.id = id;
		this.constant = constant;
	}

	public CProp() {
		this(null, null);
	}

	private Integer getValue(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain) {
		Integer value = null;

		if (expression instanceof Constant) {
			Constant c = (Constant) expression;
			
			if (c.getValue() instanceof Integer) {
				value = (Integer) c.getValue();
			}

		} else if (expression instanceof Identifier) {
			Identifier id = (Identifier) expression;

			for (CProp cp : domain.getDataflowElements()) {
				if (cp.id.equals(id)) {
					value = cp.constant;
					break;
				}
			}

		} else if (expression instanceof UnaryExpression) {
			UnaryExpression ue = (UnaryExpression) expression;
			value = getValue(ue.getExpression(), domain);

			if (ue.getOperator() instanceof NumericNegation) value *= -1;

		} else if (expression instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression) expression;
			BinaryOperator op = be.getOperator();
			Integer left = getValue(be.getLeft(), domain);
			Integer right = getValue(be.getRight(), domain);

			if (left == null || right == null) {
				value = null;

			} else if (op instanceof AdditionOperator) {
				value = left + right;

			} else if (op instanceof SubtractionOperator) {
				value = left - right;

			} else if (op instanceof MultiplicationOperator) {
				value = left * right;

			} else if (op instanceof DivisionOperator) {
				try {
					value = left / right;
				} catch (ArithmeticException ae) {}		// value is still null
			}
		}

		return value;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}
	
	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Collection<CProp> result = new HashSet<>();
    
		for (CProp cp : domain.getDataflowElements()) {
			if (cp.id.equals(id)) result.add(cp);
		}
		
		return result;
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Integer value = getValue(expression, domain);

		if (value != null) {
			return new HashSet<>(Collections.singleton(new CProp(id, value)));
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, constant);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;	
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		CProp other = (CProp) obj;

		return (Objects.equals(this.id, other.id) && Objects.equals(this.constant, other.constant));
	}

	@Override
	public StructuredRepresentation representation() {
		return new ListRepresentation(new StringRepresentation(id), new StringRepresentation(constant));
	}

	@Override
	public CProp pushScope(ScopeToken scope) throws SemanticException {
		return this;
	}

	@Override
	public CProp popScope(ScopeToken scope) throws SemanticException {
		return this;
	}
}
