package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

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

    private Integer evaluateExpression(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain) {
        if (expression instanceof Constant) {
            Constant constant = (Constant) expression;
            if (constant.getValue() instanceof Integer) {
                return (Integer) constant.getValue();
            }
        } else if (expression instanceof Identifier) {
            Identifier id = (Identifier) expression;
            for (CProp cProp : domain.getDataflowElements()) {
                if (cProp.id.equals(id)) {
                    return cProp.constant;
                }
            }
        } else if (expression instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) expression;
            Integer value = evaluateExpression(unary.getExpression(), domain);
            if (value != null && unary.getOperator() instanceof NumericNegation) {
                return -value;
            }
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) expression;
            BinaryOperator operator = binary.getOperator();
            Integer left = evaluateExpression(binary.getLeft(), domain);
            Integer right = evaluateExpression(binary.getRight(), domain);
            if (left != null && right != null) {
                if (operator instanceof AdditionOperator) {
                    return left + right;
                } else if (operator instanceof SubtractionOperator) {
                    return left - right;
                } else if (operator instanceof MultiplicationOperator) {
                    return left * right;
                } else if (operator instanceof DivisionOperator) {
                    return right != 0 ? left / right : null;
                }
            }
        }
        return null;
    }

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        return Collections.singleton(id);
    }

    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Integer value = evaluateExpression(expression, domain);
        if (value != null) {
            return Collections.singleton(new CProp(id, value));
        }
        return Collections.emptySet();
    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return Collections.emptySet();
    }

@Override
public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
    Collection<CProp> result = new HashSet<>();
    for (CProp cp : domain.getDataflowElements()) {
        if (cp.id.equals(id)) {
            result.add(cp);
        }
    }
    return result;
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
    if (obj == null || getClass() != obj.getClass()) return false;
    CProp other = (CProp) obj;
    return Objects.equals(id, other.id) && Objects.equals(constant, other.constant);
}

@Override
public StructuredRepresentation representation() {
    return new ListRepresentation(
            new StringRepresentation(id),
            new StringRepresentation(constant));
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