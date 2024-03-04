package it.unive.scsr;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

    private final Identifier id;
    private final Integer constant;

    public CProp(String x, int constant) {
        this(null, null);
    }

    public CProp(Identifier id, Integer constant) {
        this.id = id;
        this.constant = constant;
    }

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        Set<Identifier> result = new HashSet<>();
        result.add(id);
        return result;
    }

    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        Set<CProp> result = new HashSet<>();
        Integer constantValue = evaluateExpression(expression, domain);
        if (constantValue != null) {
            result.add(new CProp(id, constantValue));
        }
        return result;
    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        // If no assignment is performed, no element is generated
        return new HashSet<>();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        Set<CProp> killed = new HashSet<>();
        for (CProp cProp : domain.getDataflowElements()) {
            if (cProp.id.equals(id)) {
                killed.add(cProp);
            }
        }
        return killed;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        // If no assignment is performed, no element is killed
        return new HashSet<>();
    }

    private Integer evaluateExpression(ValueExpression expression, DefiniteDataflowDomain<CProp> domain) {
        if (expression instanceof Identifier) {
            // If the expression is an identifier, check if it has a constant value in the domain
            for (CProp cProp : domain.getDataflowElements()) {
                if (cProp.id.equals(expression)) {
                    return cProp.constant;
                }
            }
        } else if (expression instanceof UnaryExpression unaryExpression) {
            // If the expression is a unary expression, evaluate its operand and apply the operator
            Integer operandValue = evaluateExpression((ValueExpression) unaryExpression.getExpression(), domain);
            if (operandValue != null) {
                if (unaryExpression.getOperator().getClass().getSimpleName().equals("NumericNegation")) {
                    return -operandValue;
                }
            }
        } else if (expression instanceof BinaryExpression binaryExpression) {
            // If the expression is a binary expression, evaluate its operands and apply the operator
            Integer leftValue = evaluateExpression((ValueExpression) binaryExpression.getLeft(), domain);
            Integer rightValue = evaluateExpression((ValueExpression) binaryExpression.getRight(), domain);
            if (leftValue != null && rightValue != null) {
                switch (binaryExpression.getOperator().getClass().getSimpleName()) {
                    case "AdditionOperator":
                        return leftValue + rightValue;
                    case "SubtractionOperator":
                        return leftValue - rightValue;
                    case "MultiplicationOperator":
                        return leftValue * rightValue;
                    case "DivisionOperator":
                        return rightValue != 0 ? leftValue / rightValue : null;
                }
            }
        }
        // If the expression cannot be evaluated to a constant, return null
        return null;
    }

    @Override
    public StructuredRepresentation representation() {
        return new ListRepresentation(
                new StringRepresentation(id.toString()),
                new StringRepresentation(String.valueOf(constant)));
    }

    @Override
    public CProp pushScope(ScopeToken scope) throws SemanticException {
        return this;
    }

    @Override
    public CProp popScope(ScopeToken scope) throws SemanticException {
        return this;
    }

    public static void main(String[] args) {
        // Example usage of CProp class
        CProp cProp = new CProp("x", 42); // Create a new instance with identifier "x" and constant value 42
        System.out.println("Identifier: " + cProp.id + ", Constant: " + cProp.constant);
    }
}
