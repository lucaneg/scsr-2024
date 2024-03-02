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
import it.unive.lisa.util.representation.StructuredRepresentation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;


// IMPLEMENTATION NOTE:
// the code below is outside of the scope of the course. You can uncomment
// it to get your code to compile. Be aware that the code is written
// expecting that a field named "id" and a field named "constant" exist
// in this class: if you name them differently, change also the code below
// to make it work by just using the name of your choice instead of
// "id"/"constant". If you don't have these fields in your
// solution, then you should make sure that what you are doing is correct :)
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

	


	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return Collections.singleton(id);
	}
	
	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		// TODO
		return null;
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		// TODO
		return null;
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
	public int hashCode() {
		return Objects.hash(id, constant);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;	
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		CProp other = (CProp) obj;

		if (!Objects.equals(this.id, other.id) || !Objects.equals(this.constant, other.constant)) return false;
		return true;
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
