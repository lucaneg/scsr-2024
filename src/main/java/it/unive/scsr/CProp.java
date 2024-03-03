package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.PossibleDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.*;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.*;

public class CProp implements DataflowElement<PossibleDataflowDomain<CProp>,CProp> {
    private final Identifier id;
    private final Integer constant;

    public CProp(Identifier id, Integer constant) {
        this.id = id;
        this.constant = constant;
    }

    public CProp() {
        this(null, null);
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
            if (other.constant != null)
                return false;
        } else if (!constant.equals(other.constant))
            return false;
        return true;
    }

    private static Integer calculateExpression(
            SymbolicExpression expression, PossibleDataflowDomain<CProp> domain) {
        Collection<Identifier> result = new HashSet<>();

        if (expression == null)
            return null;

        if (expression instanceof Constant) {
            Constant c = (Constant) expression;
            return c.getValue() instanceof Integer ? (Integer) c.getValue() : null;
        }

        if (expression instanceof Identifier){
            for (CProp cp : domain.getDataflowElements())
                if (cp.id.equals(expression)){
                    return cp.constant;
                }


        }


        if (expression instanceof UnaryExpression){
            UnaryExpression unary = (UnaryExpression) expression;
            Integer value = calculateExpression(unary.getExpression(), domain);
            if (value == null)
                return value;
            else if(unary.getOperator() == NumericNegation.INSTANCE)
                return -value;

        }

        if (expression instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) expression;
            Integer right = calculateExpression(binary.getRight(), domain);
            Integer left = calculateExpression(binary.getLeft(), domain);

            if (right == null || left == null)
                return null;

            if (binary.getOperator() instanceof AdditionOperator)
                return left + right;
            if (binary.getOperator() instanceof DivisionOperator)
                return left == 0 ? null : (int) left / right;
            if (binary.getOperator() instanceof ModuloOperator)
                return right == 0 ? null : left % right;
            if (binary.getOperator() instanceof MultiplicationOperator)
                return left * right;
            if (binary.getOperator() instanceof SubtractionOperator)
                return left - right;
        }

        return null;
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
    public Collection<Identifier> getInvolvedIdentifiers() {
        Set<Identifier> result = new HashSet<>();

        result.add(id);
        return result;
    }

    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, PossibleDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> res = new HashSet<>();
        Integer value = calculateExpression(expression, domain);
        if(value != null)
            res.add(new CProp(id, value));
        return res;
    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, PossibleDataflowDomain<CProp> domain) throws SemanticException {
        return new HashSet<>();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, PossibleDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> killed = new HashSet<>();
        for (CProp cp : domain.getDataflowElements())
            if (cp.getInvolvedIdentifiers().contains(id))
                killed.add(cp);
        return killed;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, PossibleDataflowDomain<CProp> domain) throws SemanticException {
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
