package it.unive.scsr;

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

import java.util.*;

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

    private Integer evaluateExpression(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain){
        if (expression instanceof Identifier) {
            Identifier identifier = (Identifier) expression;
            for (CProp cprop : domain.getDataflowElements())
                if (cprop.id.equals(identifier))
                    return cprop.constant;
            return null;
        }
        if (expression instanceof Constant) {
            Constant constant = (Constant) expression;
            if (constant.getValue() instanceof Integer)
                return (Integer) constant.getValue();
            return null;
        }
        if (expression instanceof UnaryExpression) {
            UnaryExpression unaryExpression = (UnaryExpression) expression;
            Integer partialExpression = this.evaluateExpression(unaryExpression.getExpression(), domain);
            if(unaryExpression.getOperator()!= null && unaryExpression.getOperator() instanceof NumericNegation)
                return -partialExpression;
            return null;
        }
        if (expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            Integer rightOperand = evaluateExpression(binaryExpression.getRight(), domain);
            Integer leftOperand = evaluateExpression(binaryExpression.getLeft(), domain);

            if (rightOperand == null || leftOperand == null)
                return null;
            if (binaryExpression.getOperator() instanceof AdditionOperator)
                return leftOperand + rightOperand;
            if (binaryExpression.getOperator() instanceof SubtractionOperator)
                return leftOperand - rightOperand;
            if (binaryExpression.getOperator() instanceof MultiplicationOperator)
                return leftOperand * rightOperand;
            if (binaryExpression.getOperator() instanceof DivisionOperator)
                return leftOperand / rightOperand;
        }
        return null;
    }

    @Override
    public Collection<CProp> gen(Identifier identifier, ValueExpression valueExpression, ProgramPoint programPoint, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Collection<CProp> hash = new HashSet<>();
        Integer integer = evaluateExpression(valueExpression, domain);
        if (integer != null)
            hash.add(new CProp(identifier, integer));
        return hash;
    }

    @Override
    public Collection<CProp> gen(ValueExpression valueExpression, ProgramPoint programPoint, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return Collections.<CProp>emptySet();
    }

    @Override
    public Collection<CProp> kill(Identifier identifier, ValueExpression valueExpression, ProgramPoint programPoint, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Collection<CProp> killed = new HashSet<>();
        for (CProp cprop : domain.getDataflowElements()) {
            if (cprop.id.equals(identifier))
                killed.add(cprop);
        }
        return killed;
    }

    @Override
    public Collection<CProp> kill(ValueExpression valueExpression, ProgramPoint programPoint, DefiniteDataflowDomain<CProp> cPropDefiniteDataflowDomain) throws SemanticException {
        return Collections.<CProp>emptySet();
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
