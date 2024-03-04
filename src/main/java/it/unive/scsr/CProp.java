package it.unive.scsr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.PossibleDataflowDomain;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<PossibleDataflowDomain<CProp>, CProp> {

    private final Identifier id; // Represents the variable being defined
    private final ValueExpression constant; // Represents the constant value assigned to the variable

    public CProp(Identifier id, ValueExpression constant) {
        this.id = id;
        this.constant = constant;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CProp other = (CProp) obj;
        return id.equals(other.id);
    }

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        Set<Identifier> result = new HashSet<>();
        result.add(id); // The variable involved in this expression
        return result;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp,
            PossibleDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> killed = new HashSet<>();
        for (CProp cprop : domain.getDataflowElements()) {
            if (cprop.id.equals(id))
                killed.add(cprop); // Kill elements that refer to the same variable being assigned
        }
        return killed;
    }

 // Evaluate the expression and return a constant if possible
    private String evaluateExpression(String expression) {
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) + Double.parseDouble(getConstant(y));
            return String.valueOf(result);
        } else if (expression.contains("-")) {
            String[] parts = expression.split("-");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) - Double.parseDouble(getConstant(y));
            return String.valueOf(result);
        } else if (expression.contains("*")) {
            String[] parts = expression.split("\\*");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) * Double.parseDouble(getConstant(y));
            return String.valueOf(result);
        } else if (expression.contains("/")) {
            String[] parts = expression.split("/");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) / Double.parseDouble(getConstant(y));
            return String.valueOf(result);
        } else {
            // Expression doesn't involve arithmetic operations (e.g., just a constant or variable)
            return expression;
        }
    }

    // Check if the given expression is a constant
    private boolean isConstant(String expression) {
        try {
            Double.parseDouble(expression);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Get the constant value from the given expression
    private String getConstant(String expression) {
        if (isConstant(expression)) {
            return expression;
        } else {
            return null;
        }
    }
 
 
 
 
 
 @Override
 public StructuredRepresentation representation() {
     return new ListRepresentation(new StringRepresentation(id.getName()),
             new StringRepresentation(constant.toString()));
 }

 @Override
 public CProp pushScope(ScopeToken token) throws SemanticException {
     return this;
 }

 @Override
 public CProp popScope(ScopeToken token) throws SemanticException {
     return this;
 }

@Override
public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
		PossibleDataflowDomain<CProp> domain) throws SemanticException {
	// TODO Auto-generated method stub
	return null;
}



@Override
public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
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


 

}
