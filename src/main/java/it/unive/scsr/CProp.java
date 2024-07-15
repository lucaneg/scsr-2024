package it.unive.scsr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
import it.unive.lisa.symbolic.value.Operator;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.ModuloOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

// CProp class implementing the DataflowElement interface for constant propagation analysis
public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {
    private final Identifier id; // Identifier associated with the constant
    private final Integer constant; // Constant value associated with the identifier

    // Constructor initializing the identifier and constant
    public CProp(Identifier id, Integer constant) {
        this.id = id;
        this.constant = constant;
    }

    // Default constructor setting identifier and constant to null
    public CProp() {
        this.id = null;
        this.constant = null;
    }

    // Returns a collection of identifiers involved in this CProp instance
    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        Set<Identifier> set = new HashSet<>();
        set.add(id);
        return set;
    }

    // Evaluates a symbolic expression within the given domain to a constant value, if possible
    private static Integer evaluate(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain) {
        if (expression instanceof Constant) {
            return evaluateConstant((Constant) expression);
        } else if (expression instanceof Identifier) {
            return evaluateIdentifier((Identifier) expression, domain);
        } else if (expression instanceof UnaryExpression) {
            return evaluateUnaryExpression((UnaryExpression) expression, domain);
        } else if (expression instanceof BinaryExpression) {
            return evaluateBinaryExpression((BinaryExpression) expression, domain);
        }
        return null;
    }

    private static Integer evaluateConstant(Constant constant) {
        return constant.getValue() instanceof Integer ? (Integer) constant.getValue() : null;
    }

    private static Integer evaluateIdentifier(Identifier identifier, DefiniteDataflowDomain<CProp> domain) {
        for (CProp constantPropagation : domain.getDataflowElements()) {
            if (constantPropagation.id.equals(identifier)) {
                return constantPropagation.constant;
            }
        }
        return null;
    }

    private static Integer evaluateUnaryExpression(UnaryExpression unaryExpression, DefiniteDataflowDomain<CProp> domain) {
        Integer value = evaluate(unaryExpression.getExpression(), domain);
        if (unaryExpression.getOperator() == NumericNegation.INSTANCE && value != null) {
            return -value;
        }
        return value;
    }

    private static Integer evaluateBinaryExpression(BinaryExpression binaryExpression, DefiniteDataflowDomain<CProp> domain) {
        Integer leftValue = evaluate(binaryExpression.getLeft(), domain);
        Integer rightValue = evaluate(binaryExpression.getRight(), domain);

        if (leftValue == null || rightValue == null) {
            return null;
        }

        Operator operator = binaryExpression.getOperator();
        if (operator instanceof AdditionOperator) {
            return leftValue + rightValue;
        } else if (operator instanceof DivisionOperator) {
            return rightValue == 0 ? null : leftValue / rightValue;
        } else if (operator instanceof ModuloOperator) {
            return rightValue == 0 ? null : leftValue % rightValue;
        } else if (operator instanceof MultiplicationOperator) {
            return leftValue * rightValue;
        } else if (operator instanceof SubtractionOperator) {
            return leftValue - rightValue;
        }

        return null;
    }

    // Generates new CProp elements based on the given identifier and expression
    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
                                 DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> gen = new HashSet<>();
        Integer evaluation = evaluate(expression, domain);
        if (evaluation != null) {
            gen.add(new CProp(id, evaluation));
        }
        return gen;
    }

    // Generates an empty set of CProp elements based on the given expression
    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
            throws SemanticException {
        return new HashSet<CProp>();
    }

    // Kills the CProp elements associated with the given identifier and expression
    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
                                  DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> set = new HashSet<>();
        for (CProp constantPropagation : domain.getDataflowElements()) {
            if (constantPropagation.id.equals(id)) {
                set.add(constantPropagation);
            }
        }
        return set;
    }

    // Generates an empty set of CProp elements to be killed based on the given expression
    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
            throws SemanticException {
        return new HashSet<CProp>();
    }

    // Generates a hash code for this CProp instance
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + constant;
        return result;
    }

    // Compares this CProp instance with another object for equality
    @Override
    public boolean equals(Object obj) {
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
        if (constant != other.constant)
            return false;
        return true;
    }

    // Returns a structured representation of this CProp instance
    @Override
    public StructuredRepresentation representation() {
        return new ListRepresentation(
                new StringRepresentation(id),
                new StringRepresentation(constant));
    }

    // Pushes a scope, but in this case, it returns this instance without any modification
    @Override
    public CProp pushScope(
            ScopeToken scope)
            throws SemanticException {
        return this;
    }

    // Pops a scope, but in this case, it returns this instance without any modification
    @Override
    public CProp popScope(
            ScopeToken scope)
            throws SemanticException {
        return this;
    }
}
