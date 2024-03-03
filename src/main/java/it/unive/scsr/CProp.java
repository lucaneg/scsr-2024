package it.unive.scsr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

//IMPLEMENTATION NOTE:
	// the code below is outside of the scope of the course. You can uncomment
	// it to get your code to compile. Be aware that the code is written
	// expecting that a field named "id" and a field named "constant" exist
	// in this class: if you name them differently, change also the code below
	// to make it work by just using the name of your choice instead of
	// "id"/"constant". If you don't have these fields in your
	// solution, then you should make sure that what you are doing is correct :)


public class CProp
		// instances of this class are dataflow elements such that:
		// - their state (fields) hold the information contained into a single
		// element
		// - they provide gen and kill functions that are specific to the
		// analysis that we are executing
		implements
		DataflowElement<
				// the type of dataflow domain that we want to use with this analysis
				DefiniteDataflowDomain<
						// java requires this type parameter to have this class as type in fields/methods
						CProp>,
				// java requires this type parameter to have this class as type in fields/methods
				CProp> {

	
	// the variable being defined
	private final Identifier id;
	// the constant being defined
	private final Constant constant;

	
	public CProp() {
		this(null, null);
	}

	public CProp(
			Identifier id,
			Constant constant) {
		this.id = id;
		this.constant = constant;
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
    public boolean equals(
            Object obj) {
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
            return other.constant == null;
        } else return constant.equals(other.constant);
    }


	
	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		// TODO Auto-generated method stub
        Set<Identifier> result = new HashSet<>();
        result.add(id);
        return result;
	}

	@Override
	public Collection<CProp> gen(
			Identifier id, 
			ValueExpression expression, 
			ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		// TODO Auto-generated method stub
		Set<CProp> result = new HashSet<>();

        // Check if the expression is a constant, if yes, generate a new element
        if (expression instanceof Constant) {
            Constant constant = (Constant) expression;
            CProp newcp = new CProp(id, constant);
            result.add(newcp);
        }
		
		return result;
	}

	@Override
	public Collection<CProp> gen(
			ValueExpression expression, 
			ProgramPoint pp, 
			DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		// if no assignment is performed, no element needs to be generated
		return new HashSet<>();
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {		
        // kill all elements referring to the variable being re-assigned
        Set<CProp> result = new HashSet<>();
        for (CProp cp : domain.getDataflowElements())
            if (cp.getInvolvedIdentifiers().contains(id))
                result.add(cp);
        return result;		
		
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		// if no assignment is performed, no element needs to be killed
		return new HashSet<>();
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

