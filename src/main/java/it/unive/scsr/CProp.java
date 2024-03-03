package it.unive.scsr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
import it.unive.lisa.symbolic.value.Operator;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.ModuloOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {
	private final Identifier id;
	private final Integer constant;

	public CProp(Identifier id, Integer constant) {
		this.id = id;
		this.constant = constant;
	}

	public CProp() {
		this.id = null;
		this.constant = null;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		Set<Identifier> set = new HashSet<>();
		set.add(id);
		return set;
	}

	private static int evaluate(SymbolicExpression e, DefiniteDataflowDomain<CProp> domain) {
		int res = -1;
		if (e instanceof Constant) {
			Constant c = (Constant) e;
			res = c.getValue() instanceof Integer ? (Integer) c.getValue() : res;
		} else if (e instanceof Identifier) {
			for (CProp constantPropagation : domain.getDataflowElements()) {
				if (constantPropagation.id.equals(e)) {
					res = constantPropagation.constant;
				}
			}
		} else if (e instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) e;
			res = evaluate(unary.getExpression(), domain);
			if (unary.getOperator() == NumericNegation.INSTANCE) {
				res = -res;
			}
		} else if (e instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) e;
			int left = evaluate(binary.getLeft(), domain);
			int right = evaluate(binary.getRight(), domain);
			if (right != -1 && left != -1) {
				Operator operator = binary.getOperator();
				if (operator instanceof AdditionOperator) {
					res = left + right;
				} else if (operator instanceof DivisionOperator) {
					res = left == 0 ? res : left / right;
				} else if (operator instanceof ModuloOperator) {
					res = right == 0 ? res : left % right;
				} else if (operator instanceof MultiplicationOperator) {
					res = left * right;
				} else if (operator instanceof SubtractionOperator) {
					res = left - right;
				}
			}
		}
		return res;
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Set<CProp> gen = new HashSet<>();
		int evaluation = evaluate(expression, domain);
		if (evaluation != -1) {
			gen.add(new CProp(id, evaluation));
		}
		return gen;
	}

	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return new HashSet<CProp>();
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Set<CProp> set = new HashSet<>();
		for (CProp constantPropagation : domain.getDataflowElements()) {
			if (constantPropagation.id.equals(id)) {
				set.add(constantPropagation);
			}
		}
		return set;
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return new HashSet<CProp>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + constant;
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
		if (constant != other.constant)
			return false;
		return true;
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
