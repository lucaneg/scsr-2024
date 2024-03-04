package it.unive.scsr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import java.math.BigDecimal;

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

import java.util.Collections;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {	
	//The Identifier and Constant being defined
	private final Identifier id;
	private final Constant constant;
	// A Map is defined to map the couples identifier-constant
	private Map<Identifier, Constant> constantVariableMap;
	// Constructors
	// Here I defined an empty constructor
    public CProp() {
        this.constantVariableMap = new HashMap<>();
		this.id = null;
		this.constant = null;
    }
	// Here I defined a constructor just with constant
	public CProp(Constant constant) {
        this.constantVariableMap = new HashMap<>();
		this.id = null;
		this.constant = constant;
	}
	// Here I defined a constructor just with id
	public CProp(Identifier id) {
        this.constantVariableMap = new HashMap<>();
		this.id = id;
		this.constant = null;
	}
	// Here I defined a constructor with both id and constant
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

	//Point 1: Assignments to constants (store the constant-variable pair)
	/*
     * @explain: with this method I map the couples <identier,constant>
     */
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
	// store the new constant-variable pair if the result is constant - support x+y, x-y, x*y, x/y, -x)
	
	// 2+5*2 -> 12
	// -3+4*1 -> 1
	/*
     * @explain: function that evaluates an expression given as a string
     */
	public BigDecimal eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            BigDecimal parse() {
                nextChar();
                BigDecimal x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            BigDecimal parseExpression() {
                BigDecimal x = parseTerm();
                for (;;) {
                    if (eat('+')) x = x.add(parseTerm()); // addition
                    else if (eat('-')) x = x.subtract(parseTerm()); // subtraction
                    else return x;
                }
            }

            BigDecimal parseTerm() {
                BigDecimal x = parseFactor();
                for (;;) {
                    if (eat('*')) x = x.multiply(parseFactor()); // multiplication
                    else if (eat('/')) x = x.divide(parseFactor()); // division
                    else return x;
                }
            }

            BigDecimal parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return parseFactor().negate(); // unary minus

                BigDecimal x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = new BigDecimal(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }
    
    //Point 3: When a variable is assigned to a non-constant value, kill it
	/*
	 * @explain: function kill that takes in input an identifier, an expression, a programpoint and a domain
	 * 			 and kill the variables that have been modified from their initialization
	 */
    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
                                  DefiniteDataflowDomain<CProp> domain) {
        Collection<CProp> var_killed = new HashSet<>();
        if (!(expression instanceof Constant)) {
            for (CProp cp : domain.getDataflowElements()) {
                if (cp.id.equals(id)) {
                    var_killed.add(cp);
                }
            }
        }
        return var_killed;
    }
    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        return Collections.emptySet();
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
	@Override
    public Collection<Identifier> getInvolvedIdentifiers() {
		Set<Identifier> result = new HashSet<>();
		result.add(id);
		return result;
	}
	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}
}
