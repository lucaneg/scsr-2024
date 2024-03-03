package it.unive.scsr;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {
    public final Identifier id;
    public final Integer constant;

    public CProp() {
        this.id = null;
        this.constant = null;
    }

    public CProp(Identifier id, Integer constant) {
        this.id = id;
        this.constant = constant;
    }

    public Integer evaluate(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain) {
        if (expression instanceof Identifier) {
            for (CProp cp : domain.getDataflowElements()) {
                if (cp.id.equals(expression)) {
                    return cp.constant;
                }
            }
            return null;
        }

        if (expression instanceof Constant) {
            Object value = ((Constant) expression).getValue();
            return (value instanceof Integer) ? (Integer) value : null;
        }

        if (expression instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) expression;
            Integer value = evaluate(unary.getExpression(), domain);
            return (value == null) ? null : (unary.getOperator() instanceof NumericNegation) ? -value : null;
        }

        if (expression instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) expression;
            Integer l = evaluate(binary.getLeft(), domain);
            Integer r = evaluate(binary.getRight(), domain);

            if (l == null || r == null) {
                return null;
            }

            BinaryOperator operator = binary.getOperator();
            if (operator instanceof AdditionOperator) {
                return l + r;
            } else if (operator instanceof DivisionOperator) {
                return (r == 0) ? null : l / r;
            } else if (operator instanceof MultiplicationOperator) {
                return l * r;
            } else if (operator instanceof SubtractionOperator) {
                return l - r;
            }
        }

        return null;
    }

    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
                                 DefiniteDataflowDomain<CProp> domain) {
        Collection<CProp> generated = new HashSet<>();
        Integer result = evaluate(expression, domain);
        if (result != null) {
            generated.add(new CProp(id, result));
        }
        return generated;
    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        return Collections.emptySet();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
                                  DefiniteDataflowDomain<CProp> domain) {
        Collection<CProp> killed = new HashSet<>();
        if (!(expression instanceof Constant)) {
            for (CProp cp : domain.getDataflowElements()) {
                if (cp.id.equals(id)) {
                    killed.add(cp);
                }
            }
        }
        return killed;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        return Collections.emptySet();
    }

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        return Collections.singleton(id);
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
