package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

	private final Identifier id;
	private final Integer constant;
	//private final Constant constant;

	public CProp() {
		this(null, null);
	}
	public CProp(Identifier id, Integer constant){
		this.id = id;
		//this.constant = 0;
		this.constant = constant;
	}

	public Integer calculateValue(SymbolicExpression exp, DefiniteDataflowDomain<CProp> domain) {
		Integer result = null;

		switch(exp){
			case Constant c:
				Object cTemp = c.getValue();
				if (cTemp instanceof Integer)
					result = (Integer) cTemp;
				break;
			case Identifier i:
				break;
			case UnaryExpression ue:
				result = calculateValue(ue.getExpression(), domain);
				break;
			case BinaryExpression be:
				break;
            default:
                result = null;
				break;
        }
		return result;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CProp cProp = (CProp) o;
		return Objects.equals(id, cProp.id) && Objects.equals(constant, cProp.constant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, constant);
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		Set<Identifier> result = new HashSet<>();
		result.add(id);
		return result;
	}

	@Override
	public Collection<CProp> gen(Identifier identifier, ValueExpression valueExpression, ProgramPoint pp, DefiniteDataflowDomain<CProp> cPropPossibleDataflowDomain) throws SemanticException {
		// we generate a new element tracking this definition
		Set<CProp> result = new HashSet<>();
		CProp rd = new CProp(id,constant);
		result.add(rd);
		return result;
	}

	@Override
	public Collection<CProp> gen(ValueExpression valueExpression, ProgramPoint programPoint, DefiniteDataflowDomain<CProp> cPropPossibleDataflowDomain) throws SemanticException {
		return null;
	}

	@Override
	public Collection<CProp> kill(Identifier identifier, ValueExpression valueExpression, ProgramPoint programPoint, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		Set<CProp> killed = new HashSet<>();
		for (CProp cp : domain.getDataflowElements())
			// we could use `rd.variable.equals(id)` as elements of this class
			// refer to one variable at a time
			if (cp.getInvolvedIdentifiers().contains(id))
				killed.add(cp);
		return killed;
	}

	@Override
	public Collection<CProp> kill(ValueExpression valueExpression, ProgramPoint programPoint, DefiniteDataflowDomain<CProp> cPropPossibleDataflowDomain) throws SemanticException {
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
