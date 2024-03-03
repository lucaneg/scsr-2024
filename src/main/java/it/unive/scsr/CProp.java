package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {
    private final Identifier id;
    private final Integer constant;

    public CProp(){
        this(null, null);
    }

    public CProp(Identifier id, Integer constant){
        this.id = id;
        this.constant = constant;
    }

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        return Collections.singleton(id);
    }

    private static Integer eval(SymbolicExpression e, DefiniteDataflowDomain<CProp> domain){
        Integer res = null;

        if (e instanceof Constant c)
            res = c.getValue() instanceof Integer ? (Integer) c.getValue() : null;
        else if (e instanceof Identifier) {
            for (CProp cp : domain.getDataflowElements())
                if (cp.id.equals(e)) res = cp.constant;
        }
        else if (e instanceof UnaryExpression unary) {
            Integer i = eval(unary.getExpression(), domain);
            if (i != null && unary.getOperator() == NumericNegation.INSTANCE) res = -i;
        }
        else if (e instanceof BinaryExpression binary) {
            Integer right = eval(binary.getRight(), domain);
            Integer left = eval(binary.getLeft(), domain);
            BinaryOperator op = binary.getOperator();

            if (right == null || left == null) return null;
            else if (op instanceof AdditionOperator) res = left + right;
            else if (op instanceof SubtractionOperator) res = left - right;
            else if (op instanceof DivisionOperator) res = left == 0 ? null : (int) left / right;
            else if (op instanceof MultiplicationOperator) res = left * right;
        }

        return res;
    }
    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> set = new HashSet<>();
        Integer constant = eval(expression, domain);
        if (constant != null) set.add(new CProp(id, constant));
        return set;
    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return new HashSet<>();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> killed = new HashSet<>();
        for (CProp cProp : domain.getDataflowElements())
            if (cProp.getInvolvedIdentifiers().contains(id))
                killed.add(cProp);
        return killed;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return new HashSet<>();
    }

    @Override
    public StructuredRepresentation representation() {
        return new ListRepresentation(
                new StringRepresentation(id),
                new StringRepresentation(constant)
        );
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
        if (constant == null) {
            if (other.constant != null)
                return false;
        } else if (!constant.equals(other.constant))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
