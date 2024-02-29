package it.unive.scsr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.PossibleDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<PossibleDataflowDomain<CProp>, CProp> {	
	//The id and constant being defined
	private final Identifier id;
	private final Constant constant;
	// A Map is defined to map the couples identifier-constant
	private Map<Identifier, Constant> constantVariableMap;
	// Constructors
    public CProp() {
        this.constantVariableMap = new HashMap<>();
		this.id = null;
		this.constant = null;
    }
	public CProp(Constant constant) {
        this.constantVariableMap = new HashMap<>();
		this.id = null;
		this.constant = constant;
	}
	public CProp(Identifier id) {
        this.constantVariableMap = new HashMap<>();
		this.id = id;
		this.constant = null;
	}
	public CProp(Identifier id, Constant constant) {
        this.constantVariableMap = new HashMap<>();
		this.id = id;
		this.constant = constant;
	}
	// Getters
	public Identifier getId() {
		return id;
	}
	public Constant getConstant() {
		return constant;
	}
	// HashCode method 
	@Override
	public int hashCode() {
		return Objects.hash(constant, id);
	}
	// Equals method
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CProp other = (CProp) obj;
		return constant == other.constant && Objects.equals(id, other.id);
	}

	//Point 1: Assignments to constants (store the constant-variable pair) [OK]
	public void assignConstant(Identifier id, Constant constant) {
		constantVariableMap.put(id, constant);
	}
    /*
     * @explain: with this method I get the values assigned to the variables
     */
	public Collection<Constant> getVariablesAssignedToConstants() {
		return constantVariableMap.values();
	}
	
    //Point 2: Assignments to constant expressions (evaluate expressions containing constants and variables, and
	// store the new constant-variable pair if the result is constant - support x+y, x-y, x*y, x/y, -x) [OK]
	// ASSUMPTION: There are just 2 numbers and 1 operator on each expression
	public void assignConstant(String expression) {
		int result = evaluateExpression(expression);
		if (result != Integer.MIN_VALUE) {
			System.out.println("OK" + evaluateExpression(expression));
		}else System.out.println("Not OK");
	}
    private int evaluateExpression(String expression) {
    	boolean trovato = false;
    	int i = 0;
    	int result = 0;
        try {
        	while(!trovato && i<expression.length()) {
        		if(i != 0 && (expression.charAt(i)=='+' || expression.charAt(i)=='-' || expression.charAt(i)=='*' || expression.charAt(i)=='/')) {
        			int n1 = Integer.parseInt(expression.substring(0,i));
        			int n2 = Integer.parseInt(expression.substring(i+1,expression.length()));
        			if(expression.charAt(i)=='+') result = n1+n2;
        			else if(expression.charAt(i)=='-') result = n1-n2;
        			else if(expression.charAt(i)=='*') result  = n1*n2;
        			else if(expression.charAt(i)=='/') result = n1/n2;
        			trovato = true;
        			return result;
        		} else if(i == 0 && expression.charAt(i)=='-') {
        			int n1 = Integer.parseInt(expression.substring(i+1,expression.length()));
        			result = -n1;
        			trovato = true;
        			return result;
        		}
        		i = i+1;
        	}
        	return Integer.parseInt(expression);
        } catch (NumberFormatException e) {
            // If expression cannot be parsed directly as integer, it's not a constant
            return Integer.MIN_VALUE;
        }
    }
    
    //Point 3: When a variable is assigned to a non-constant value, kill it
    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
		Set<Identifier> result = new HashSet<>();
		result.add(id);
		return result;
	}
    @Override
    public Collection<CProp> kill(
			Identifier id,
			ValueExpression expression,
			ProgramPoint pp,
			PossibleDataflowDomain<CProp> domain)
			throws SemanticException {
		// we kill all of the elements that refer to the variable being
		// assigned, as we are redefining the variable
		Set<CProp> killed = new HashSet<>();
		for (CProp rd : domain.getDataflowElements())
			// we could use `rd.variable.equals(id)` as elements of this class
			// refer to one variable at a time
			if (rd.getInvolvedIdentifiers().contains(id))
				killed.add(rd);
		return killed;
	}
	public Collection<CProp> kill(
			ValueExpression expression,
			ProgramPoint pp,
			PossibleDataflowDomain<CProp> domain)
			throws SemanticException {
		// if no assignment is performed, no element is killed!
		return new HashSet<>();
	}
	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			PossibleDataflowDomain<CProp> domain) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, PossibleDataflowDomain<CProp> domain)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	// IMPLEMENTATION NOTE:
	// the code below is outside of the scope of the course. You can uncomment
	// it to get your code to compile. Be aware that the code is written
	// expecting that a field named "id" and a field named "constant" exist
	// in this class: if you name them differently, change also the code below
	// to make it work by just using the name of your choice instead of
	// "id"/"constant". If you don't have these fields in your
	// solution, then you should make sure that what you are doing is correct :)
	
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
