package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.*;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.*;
public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

	private final Identifier id;
	private final Integer constant;

	public CProp() {
		this(null, null);
	}

	public CProp(Identifier id, Integer constant) {
		this.id = id;
		this.constant = constant;
	}

	private Integer calculateValue(SymbolicExpression exp, DefiniteDataflowDomain<CProp> domain) {
		Integer result = null;

		// If exp is a Constant, then the result is it's value
		if (exp instanceof Constant) {
			Constant c = (Constant) exp;

			if (c.getValue() instanceof Integer) {
				result = (Integer) c.getValue();
			}
		// If exp is an Indentifier, then i have to search for the result
		} else if (exp instanceof Identifier) {
			Identifier id = (Identifier) exp;

			for (CProp cp : domain.getDataflowElements()) {
				if (cp.id.equals(id)) {
					result = cp.constant;
					break;
				}
			}
		// If exp is a Unary Expression, then I have to extract expression and domain
		} else if (exp instanceof UnaryExpression) {
			UnaryExpression ue = (UnaryExpression) exp;
			result = calculateValue(ue.getExpression(), domain);
		// If exp is a BinaryExpression, I have to split it into its main parts, left and right Integer and the BinaryOperator
		} else if (exp instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression) exp;
			Integer left = calculateValue(be.getLeft(), domain);
			BinaryOperator bp = be.getOperator();
			Integer right = calculateValue(be.getRight(), domain);

			if (left == null || right == null)
				result = null;
			else if (bp instanceof AdditionOperator)
				result = left + right;
			else if (bp instanceof SubtractionOperator)
				result = left - right;
			else if (bp instanceof MultiplicationOperator)
				result = left * right;
			else if (bp instanceof ModuloOperator)
				result = left % right;
			else if (bp instanceof DivisionOperator)
				result = right == 0 ? null : left / right;
		}

		return result;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression exp, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Collection<CProp> cp;
		if(calculateValue(exp, domain) != null)
			cp = Collections.singleton(new CProp(id, constant));
		else
			cp = Collections.emptySet();

		return  cp;
	}

	@Override
	public Collection<CProp> gen(ValueExpression exp, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression exp, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Set<CProp> elements = domain.getDataflowElements();
		Optional<CProp> cp = elements.stream().filter(x -> x.id.equals(id)).findFirst();
		return cp.map(Collections::singleton).orElse(Collections.emptySet());
	}

	@Override
	public Collection<CProp> kill(ValueExpression exp, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		CProp other = (CProp) obj;

		return (!Objects.equals(this.id, other.id) || !Objects.equals(this.constant, other.constant));
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