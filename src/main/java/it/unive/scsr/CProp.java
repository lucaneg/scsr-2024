package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>,CProp> {

	private  final Identifier id ;
    private  final Integer  constant ;

    public CProp(Identifier id , Integer constant ){
        super();
        this.id=id;
        this.constant = constant ;
    }
    public CProp() {
        this(null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CProp cProp = (CProp) o;
        return Objects.equals(id, cProp.id) && Objects.equals(constant, cProp.constant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, constant);
    }


    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        return Collections.singleton(id);
    }

    private Integer evaluateExpression(ValueExpression expression, DefiniteDataflowDomain<CProp> domain) {
        if (expression instanceof Constant) {
            Constant constant = (Constant) expression;
            if (constant.getValue() instanceof Integer) {
                return (Integer) constant.getValue();
            }
        } else if (expression instanceof Identifier) {
            for (CProp cProp : domain.getDataflowElements()) {
                if (cProp.id.equals(expression)) {
                    return cProp.constant;
                }
            }
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) expression;
            ValueExpression left = (ValueExpression) binary.getLeft();
            ValueExpression right = (ValueExpression) binary.getRight();

            Integer leftValue = evaluateExpression(left, domain);
            Integer rightValue = evaluateExpression(right, domain);

            if (leftValue == null || rightValue == null) {
                return null;
            }

            switch (binary.getOperator().toString()) {
                case "+":
                    return leftValue + rightValue;
                case "-":
                    return leftValue - rightValue;
                case "*":
                    return leftValue * rightValue;
                case "/":
                    if (rightValue != 0) {
                        return leftValue / rightValue;
                    }
                    return null;
                default:
                    return null;
            }
        }
        return null;
    }


    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Integer evaluatedValue = evaluateExpression(expression, domain);

        if (evaluatedValue != null) {
            CProp constantMapping = new CProp(id, evaluatedValue);
            return Collections.singleton(constantMapping);
        }

        return Collections.emptySet();
    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return Collections.emptySet();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Collection<CProp> toBeKilled = new ArrayList<>();
        for (CProp cprop :  domain.getDataflowElements()) {
            if (cprop.id.equals(id)) {
                toBeKilled.add(cprop);
            }
        }

        return toBeKilled;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return Collections.emptySet();
    }
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
