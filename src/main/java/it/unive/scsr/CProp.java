package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.*;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {

    // IMPLEMENTATION NOTE:
    // the code below is outside of the scope of the course. You can uncomment
    // it to get your code to compile. Be aware that the code is written
    // expecting that a field named "id" and a field named "constant" exist
    // in this class: if you name them differently, change also the code below
    // to make it work by just using the name of your choice instead of
    // "id"/"constant". If you don't have these fields in your
    // solution, then you should make sure that what you are doing is correct :)

    private final Identifier id;
    private final Integer constant;

    public CProp() {
        this(null, null);
    }

    public CProp(Identifier id, Integer constant) {
        this.id = id;
        this.constant = constant;
    }

    @Override
    public StructuredRepresentation representation() {
        return new ListRepresentation(new StringRepresentation(id), new StringRepresentation(constant));
    }

    @Override
    public CProp pushScope(ScopeToken scope) throws SemanticException {
        return this;
    }

    @Override
    public CProp popScope(ScopeToken scope) throws SemanticException {
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

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        Set<Identifier> result = new HashSet<>();
        result.add(id);
        return result;
    }

    private Integer evaluate(SymbolicExpression expr, DefiniteDataflowDomain<CProp> domain) {
        // The behaviour of the evaluate function depends on the type of expr, that can be an instance of: Constant, Identifier, BinaryExpression, UnaryExpression (more info here: https://lisa-analyzer.github.io/structure/analysis-infrastructure.html)
        // if it is a constant, return its value
        if (expr instanceof Constant c && c.getValue() instanceof Integer) {
            return (Integer) c.getValue();
        }
        // if it is an identifier, retrieve its value (if defined)
        else if (expr instanceof Identifier i) {
            for (CProp cp : domain.getDataflowElements()) {
                if (cp.id.equals(i) && cp.constant != null)
                    return cp.constant;
            }
        }
        // if it is a binary expression, return the result of the application of the proper operator (operators  are the 4 basic operations and the modulo)
        else if (expr instanceof BinaryExpression be) {
            // first of all, evaluate the left and the right part of the expression; if both are not null then cast to Integer
            Integer lv = evaluate(be.getLeft(), domain);
            Integer rv = evaluate(be.getRight(), domain);
            if (lv != null && rv != null) {
                BinaryOperator op = be.getOperator();

                if (op instanceof AdditionOperator) {
                    return lv + rv;
                } else if (op instanceof SubtractionOperator) {
                    return lv - rv;
                } else if (op instanceof MultiplicationOperator) {
                    return lv * rv;
                } else if (op instanceof DivisionOperator) {
                    return lv / rv;
                } else if (op instanceof ModuloOperator) {
                    return lv % rv;
                }
            }
        } else if (expr instanceof UnaryExpression ue) {
            Integer v = evaluate(ue.getExpression(), domain);
            if (v != null)
                return ue.getOperator() == NumericNegation.INSTANCE ? -v : v;
        }
        // for everything else that is not recognized
        return null;
    }


    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> set = new HashSet<>();
        Integer res = evaluate(expression, domain);
        if (res != null) {
            set.add(new CProp(id, res));
        }
        return set;

    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return new HashSet<>();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> set = new HashSet<>();
        for (CProp cp : domain.getDataflowElements()) {
            if (cp.id.equals(id))
                set.add(cp);
        }
        return set;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return new HashSet<>();
    }
}
