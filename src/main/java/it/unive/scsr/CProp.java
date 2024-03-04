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
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

    private final Identifier id;
    private final Integer constant;

    public CProp() {
        this.id = null;
        this.constant = null;
    }

    public CProp(Identifier id, Integer constant) {
        this.id = id;
        this.constant = constant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, constant);
    }

    @Override
    public boolean equals(
            Object object) {
        if (this == object)
            return true;
        if (object == null || this == null)
            return false;
        if (this.getClass() != object.getClass())
            return false;
        CProp other = (CProp) object;
        if (!this.id.equals(other.id) || !this.constant.equals(other.constant))
            return false;
        return true;
    }

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        return Collections.singleton(this.id);
    }

    private Integer evaluateExpConstant(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain) {
        if (expression instanceof Identifier) {
            return getIdentifierConstant((Identifier) expression, domain);
        } else if (expression instanceof Constant) {
            return getConstantValue((Constant) expression);
        } else if (expression instanceof UnaryExpression) {
            return evaluateUnaryExpression((UnaryExpression) expression, domain);
        } else if (expression instanceof BinaryExpression) {
            return evaluateBinaryExpression((BinaryExpression) expression, domain);
        }
        return null;
    }

    private Integer getIdentifierConstant(Identifier identifier, DefiniteDataflowDomain<CProp> domain) {
        return domain.getDataflowElements().stream()
                .filter(cprop -> cprop.id.equals(identifier))
                .map(cprop -> cprop.constant)
                .findFirst()
                .orElse(null);
    }

    private Integer getConstantValue(Constant constant) {
        return constant.getValue() instanceof Integer ? (Integer) constant.getValue() : null;
    }

    private Integer evaluateUnaryExpression(UnaryExpression unaryExpression, DefiniteDataflowDomain<CProp> domain) {
        Integer value = evaluateExpConstant(unaryExpression.getExpression(), domain);
        return (unaryExpression.getOperator() instanceof NumericNegation && value != null) ? -value : null;
    }

    private Integer evaluateBinaryExpression(BinaryExpression binaryExpression, DefiniteDataflowDomain<CProp> domain) {
        Integer left = evaluateExpConstant(binaryExpression.getLeft(), domain);
        Integer right = evaluateExpConstant(binaryExpression.getRight(), domain);

        if (left == null || right == null) return null;

        if (binaryExpression.getOperator() instanceof AdditionOperator) return left + right;
        if (binaryExpression.getOperator() instanceof SubtractionOperator) return left - right;
        if (binaryExpression.getOperator() instanceof MultiplicationOperator) return left * right;
        if (binaryExpression.getOperator() instanceof DivisionOperator) return left / right;

        return null;
    }

    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
                                 DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Integer value = evaluateExpConstant(expression, domain);

        Collection<CProp> result = new HashSet<>();
        if (value == null)
            return new HashSet<>();
        else {
            CProp cp = new CProp(id, value);
            result.add(cp);
            return result;
        }
    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
            throws SemanticException {
        return new HashSet<>();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
                                  DefiniteDataflowDomain<CProp> domain) throws SemanticException {

        Collection<CProp> result = new HashSet<>();
        for (CProp cp : domain.getDataflowElements())
            if (cp.id.equals(id) && !(expression instanceof Constant))
                result.add(cp);
        return result;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
            throws SemanticException {
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
