package it.unive.scsr;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonGe;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonGt;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonLe;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonLt;
import it.unive.lisa.util.representation.SetRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class UpperBounds
		// instances of this class are lattice elements such that:
		// - their state (fields) hold the information contained into a single
		// variable
		// - they provide logic for the evaluation of expressions
		implements
		BaseNonRelationalValueDomain<
				// java requires this type parameter to have this class
				// as type in fields/methods
				UpperBounds>, Iterable<Identifier> {

	
	/**
	 * The flag to set abstract top state.
	 */
	private final boolean isTop;

	/**
	 * The set containing the bounds.
	 */
	private final Set<Identifier> bounds;

	
	/**
	 * The abstract top element.
	 */
	private static final UpperBounds TOP = new UpperBounds(true);

	/**
	 * The abstract bottom element.
	 */
	private static final UpperBounds BOTTOM = new UpperBounds(new TreeSet<>());

	/**
	 * Builds the upper bounds.
	 */
	public UpperBounds() {
		this(true);
	}

	/**
	 * Builds the upper bounds.
	 * 
	 * @param isTop {@code true} if the abstract domain is top; otherwise
	 *                  {@code false}.
	 */
	public UpperBounds(
			boolean isTop) {
		this.bounds = null;
		this.isTop = isTop;
	}

	/**
	 * Builds the upper bounds.
	 * 
	 * @param bounds the bounds to set
	 */
	public UpperBounds(
			Set<Identifier> bounds) {
		this.bounds = bounds;
		this.isTop = false;
	}

	
	@Override
	public UpperBounds glbAux(UpperBounds other) throws SemanticException {
		Set<Identifier> result = new HashSet<Identifier>(bounds);
		result.addAll(other.bounds);
		return new UpperBounds(result);
	}

	@Override
	public UpperBounds lubAux(UpperBounds other) throws SemanticException {
		Set<Identifier> result = new HashSet<Identifier>(bounds);
		result.retainAll(other.bounds);
		return new UpperBounds(result);
	}

	@Override
	public boolean lessOrEqualAux(UpperBounds other) throws SemanticException {
		return bounds.containsAll(other.bounds);
	}

	@Override
	public UpperBounds top() {
		
		return TOP;
	}
	
	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public UpperBounds bottom() {
		return BOTTOM;
	}
	
	@Override
	public boolean isBottom() {
		return !isTop && bounds != null && bounds.isEmpty();
	}

	@Override
	public StructuredRepresentation representation() {
		if(isTop())
			return new StringRepresentation("{}");
		if(isBottom())
			return Lattice.bottomRepresentation();
		
		return new SetRepresentation(bounds, StringRepresentation::new);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bounds, isTop);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpperBounds other = (UpperBounds) obj;
		return Objects.equals(bounds, other.bounds) && isTop == other.isTop;
	}

	@Override
	public Iterator<Identifier> iterator() {
		if(bounds == null)
			return Collections.emptyIterator();
		
		return bounds.iterator();
	}

	@Override
	public ValueEnvironment<UpperBounds> assumeBinaryExpression(ValueEnvironment<UpperBounds> environment,
			BinaryOperator operator, ValueExpression left, ValueExpression right, ProgramPoint src, ProgramPoint dest,
			SemanticOracle oracle) throws SemanticException {
		
		if(!(left instanceof Identifier && right instanceof Identifier))
			return environment;
		
		Identifier leftId = (Identifier) left;
		Identifier rightId = (Identifier) right;
		
		if(operator instanceof ComparisonLt) {
			UpperBounds set = environment.getState(leftId).glb(environment.getState(rightId))
					.glb(new UpperBounds(Collections.singleton(rightId)));
			return environment.putState(leftId, set);
		} else if(operator instanceof ComparisonEq) {
			
			UpperBounds set = environment.getState(leftId).glb(environment.getState(rightId));
			return environment.putState(leftId, set).putState(rightId, set);
			
		} else if(operator instanceof ComparisonGt) {
			// x > y --> y < x
			
			UpperBounds set = environment.getState(rightId).glb(environment.getState(leftId))
					.glb(new UpperBounds(Collections.singleton(leftId)));
			return environment.putState(rightId, set);
		} else if(operator instanceof ComparisonLe) {
			// x <= y 
			UpperBounds set = environment.getState(leftId).glb(environment.getState(rightId));
			
			return environment.putState(leftId, set);
		} else if(operator instanceof ComparisonGe) {
			// x >= y --> y <= x
			UpperBounds set = environment.getState(rightId).glb(environment.getState(leftId));
			
			return environment.putState(rightId, set);
		} 
		
			
		return environment;
	}
	
	@Override
	public UpperBounds wideningAux(
			UpperBounds other)
			throws SemanticException {
		return other.bounds.containsAll(bounds) ? other : TOP;
	}

	/**
	 * Checks if this bounds contains a specified identifier of a program
	 * variable.
	 * 
	 * @param id the identifier to check
	 * 
	 * @return {@code true} if this bounds contains the specified identifier;
	 *             otherwise, {@code false}.
	 */
	public boolean contains(
			Identifier id) {
		return bounds != null && bounds.contains(id);
	}

	/**
	 * Adds the specified identifier of a program variable in the bounds.
	 * 
	 * @param id the identifier to add in the bounds.
	 * 
	 * @return the updated bounds.
	 */
	public UpperBounds add(
			Identifier id) {
		Set<Identifier> res = new HashSet<>();
		if (!isTop() && !isBottom())
			res.addAll(bounds);
		res.add(id);
		return new UpperBounds(res);
	}
}