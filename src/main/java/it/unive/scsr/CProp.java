package it.unive.scsr;

import java.util.Collection;
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
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

/**
 * A constant propagation dataflow element, representing the constant value
 */

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>,CProp>{
	private final Identifier id;
	private final Integer constant;

	/**
	 * Builds a new instance of constant propagation
	 */
	public CProp() {
		this.constant = null;
		this.id = null;
	}

	/**
	 * Builds a new instance of constant propagation
	 * @param id       the identifier
	 * @param constant the constant value
	 */
	public CProp(Identifier id, Integer constant) {
		this.id = id;
		this.constant = constant;
	}

	/**
	 * Returns the identifier
	 * @return the identifier
	 */
	public Identifier getId() {
		return id;
	}

	/**
	 * Returns the constant value
	 * @return the constant value
	 */
	public Integer getConstant() {
		return constant;
	}

	/**
	 * Returns a string representation of the constant propagation
	 * @return a string representation of the constant propagation
	 */
	@Override
	public String toString() {
		return representation().toString();
	}

	/**
	 * Returns the hash code of the constant propagation
	 * @return the hash code of the constant propagation
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((constant == null) ? 0 : constant.hashCode());
		return result;
	}

	/**
	 * Checks if the constant propagation is equal to another object
	 * @param obj the object to compare
	 * @return true if the constant propagation is equal to the object, false otherwise
	 */
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

	/**
	 * Gets the involved identifiers
	 * @return the involved identifiers
	 */
	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}

	/**
	 * Evaluates the given expression
	 * @param e      the expression to evaluate
	 * @param domain the domain
	 * @return the result of the evaluation
	 */
	private static Integer evaluate(SymbolicExpression e, DefiniteDataflowDomain<CProp> domain) {
		if (e instanceof Constant) {
			Constant c = (Constant) e;
			return c.getValue() instanceof Integer ? (Integer) c.getValue() : null;
		}
	
		if (e instanceof Identifier) {
			for (CProp cp : domain.getDataflowElements()) {
				if (cp.id.equals(e)) {
					return cp.constant;
				}
			}
			return null;
		}
	
		if (e instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) e;
			Integer i = evaluate(unary.getExpression(), domain);
			if (i == null) {
				return null;
			}
			if (unary.getOperator() == NumericNegation.INSTANCE) {
				return -i;
			}
		}
	
		if (e instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) e;
			Integer right = evaluate(binary.getRight(), domain);
			Integer left = evaluate(binary.getLeft(), domain);
	
			if (right == null || left == null) {
				return null;
			}
	
			switch (binary.getOperator().getClass().getSimpleName()) {
				case "AdditionOperator":
					return left + right;
				case "DivisionOperator":
					return right == 0 ? null : (int) left / right;
				case "ModuloOperator":
					return right == 0 ? null : left % right;
				case "MultiplicationOperator":
					return left * right;
				case "SubtractionOperator":
					return left - right;
				default:
					return null;
			}
		}
	
		return null;
	}
	
	/**
	 * Generates the constant propagation
	 * @param id         the identifier
	 * @param expression the expression
	 * @param pp         the program point
	 * @param domain     the domain
	 * @return the generated constant propagation
	 * @throws SemanticException if something goes wrong during the generation
	 */
	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Integer value = evaluate(expression, domain);

		Collection<CProp> gen_set = new HashSet<>();
		if (value == null)
			return new HashSet<>();
		else {
			CProp cp = new CProp(id, value);
			gen_set.add(cp);
			return gen_set;
		}
	}

	/**
	 * Generates the constant propagation
	 * @param expression the expression
	 * @param pp         the program point
	 * @param domain     the domain
	 * @return the generated constant propagation
	 * @throws SemanticException if something goes wrong during the generation
	 */
	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		return new HashSet<>();
	}

	/**
	 * Kills the constant propagation
	 * @param id         the identifier
	 * @param expression the expression
	 * @param pp         the program point
	 * @param domain     the domain
	 * @return the killed constant propagation
	 * @throws SemanticException if something goes wrong during the killing
	 */
	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		
		Collection<CProp> genSet = new HashSet<>();
		for (CProp cp : domain.getDataflowElements())
		if (cp.id.equals(id) && !(expression instanceof Constant))
				genSet.add(cp);
		return genSet;
	}

	/**
	 * Kills the constant propagation
	 * @param expression the expression
	 * @param pp         the program point
	 * @param domain     the domain
	 * @return the killed constant propagation
	 * @throws SemanticException if something goes wrong during the killing
	 */
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
}
