package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.*;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.*;


public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>,CProp>{
    private final Identifier id;
    private final Integer constant;
    public CProp(){
        this(null,null);
    }
    public CProp(Identifier id, Integer constant){
        this.constant=constant;
        this.id=id;
    }
    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        return Collections.singleton(id);
    }

    private static Integer evaluation(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain) {

        if (expression instanceof Constant) {
            Constant x = (Constant) expression;
            return x.getValue() instanceof Integer ? (Integer) x.getValue() : null;
        }

        if (expression instanceof Identifier) {
            for (CProp x : domain.getDataflowElements())
                if (x.id.equals(expression))
                    return x.constant;

            return null;
        }

        if (expression instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) expression;
            Integer x = evaluation(unary.getExpression(), domain);

            if (x == null)
                return x;

            if (unary.getOperator() == NumericNegation.INSTANCE)
                return -x;
        }

        if (expression instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) expression;
            Integer right = evaluation(binary.getRight(), domain);
            Integer left = evaluation(binary.getLeft(), domain);

            if (right == null || left == null)
                return null;

            if (binary.getOperator() instanceof AdditionOperator)
                return left + right;
            if (binary.getOperator() instanceof DivisionOperator)
                return left == 0 ? null : left / right;
            if (binary.getOperator() instanceof MultiplicationOperator)
                return left * right;
            if (binary.getOperator() instanceof SubtractionOperator)
                return left - right;
        }

        return null;
    }
    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        Set<CProp> set = new HashSet<>();

        Integer x = evaluation(expression, domain);
        if (x != null)
            set.add(new CProp(id, x));

        return set;
    }

    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        return new HashSet<>();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        Set<CProp> set = new HashSet<>();

        for (CProp x : domain.getDataflowElements())
            if (x.id.equals(id))
                set.add(x);

        return set;
    }

    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) {
        return new HashSet<>();
    }

   @Override
    public boolean canProcess(SymbolicExpression expression, ProgramPoint pp, SemanticOracle oracle) {
        return DataflowElement.super.canProcess(expression, pp, oracle);
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
