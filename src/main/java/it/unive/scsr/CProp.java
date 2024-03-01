package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.type.Float32Type;
import it.unive.lisa.program.type.Int32Type;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingDiv;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingMul;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingSub;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

	private final Identifier id;
	private final Constant constant;

	public CProp(Identifier id, Constant constant) {
		this.id = id;
		this.constant = constant;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}

	private static Optional<Object> trySolveUnaryExpression(UnaryOperator op, Constant c) {
		if (c.getStaticType() == Float32Type.INSTANCE) {
			float value = (Float) c.getValue();
			if (op == NumericNegation.INSTANCE)
				return Optional.of(-value);
		}
		if (c.getStaticType() == Int32Type.INSTANCE) {
			int value = (Integer) c.getValue();
			if (op == NumericNegation.INSTANCE)
				return Optional.of(Math.negateExact(value));
		}
		return Optional.empty();
	}

	private static Optional<Object> trySolveBinaryExpression(BinaryOperator op, Constant a, Constant b) {
		if (a.getStaticType() == Float32Type.INSTANCE && b.getStaticType() == Float32Type.INSTANCE) {
			float valueA = (Float) a.getValue();
			float valueB = (Float) b.getValue();
			if (op == NumericNonOverflowingAdd.INSTANCE)
				return Optional.of(valueA + valueB);
			if (op == NumericNonOverflowingSub.INSTANCE)
				return Optional.of(valueA - valueB);
			if (op == NumericNonOverflowingMul.INSTANCE)
				return Optional.of(valueA * valueB);
			if (op == NumericNonOverflowingDiv.INSTANCE)
				return Optional.of(valueA / valueB);
		}
		if (a.getStaticType() == Int32Type.INSTANCE && b.getStaticType() == Int32Type.INSTANCE) {
			int valueA = (Integer) a.getValue();
			int valueB = (Integer) b.getValue();
			if (op == NumericNonOverflowingAdd.INSTANCE)
				return Optional.of(Math.addExact(valueA, valueB));
			if (op == NumericNonOverflowingSub.INSTANCE)
				return Optional.of(Math.subtractExact(valueA, valueB));
			if (op == NumericNonOverflowingMul.INSTANCE)
				return Optional.of(Math.multiplyExact(valueA, valueB));
			if (op == NumericNonOverflowingDiv.INSTANCE)
				return Optional.of(valueA / valueB);
		}
		return Optional.empty();
	}

	private static Optional<Constant> tryComputeConstant(SymbolicExpression expression,
			DefiniteDataflowDomain<CProp> domain) {
		if (expression instanceof Constant)
			return Optional.of((Constant) expression);

		if (expression instanceof Identifier) {
			Identifier id = (Identifier) expression;
			return domain.getDataflowElements().stream()
					.filter(e -> e.id.equals(id))
					.map(e -> e.constant)
					.findFirst();
		}

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;
			return tryComputeConstant(unary.getExpression(), domain)
					.flatMap(c -> trySolveUnaryExpression(unary.getOperator(), c)
							.map(res -> new Constant(c.getStaticType(), res, expression.getCodeLocation())));
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;
			return tryComputeConstant(binary.getLeft(), domain)
					.flatMap(l -> tryComputeConstant(binary.getRight(), domain)
							.flatMap(r -> trySolveBinaryExpression(binary.getOperator(), l, r)
									.map(res -> new Constant(l.getStaticType(), res, expression.getCodeLocation()))));
		}

		return Optional.empty();
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		try {
			return tryComputeConstant(expression, domain)
					.map(c -> Collections.singleton(new CProp(id, c)))
					.orElse(Collections.emptySet());
		} catch (ArithmeticException e) {
			return Collections.emptySet();
		}
	}

	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return Collections.emptySet();
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		return domain.getDataflowElements().stream()
				.filter(e -> e.id.equals(id))
				.collect(Collectors.toSet());
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
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
}
