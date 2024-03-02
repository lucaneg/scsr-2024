package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.ConstantPropagation;
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
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import org.antlr.v4.codegen.target.CppTarget;

import java.sql.SQLOutput;
import java.util.*;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {
    //differenza, definite fa intersezione dei branch, possible fa unione dei branch

    /*
    if (true) {
    x=15
    } else {
    x=15
    }

    intersezione uguale a prescindere quindi alla fine x compare nelle definizioni
     */
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
    public boolean equals(Object rhs) {
        if (this == rhs) return true;
        if (this == null || rhs == null) return false;
        if (rhs.getClass() != CProp.class) return false;
        CProp crhs = (CProp) rhs;
        return Objects.equals(this.id, crhs.id) && Objects.equals(this.constant, crhs.constant);
    }


    //if the expression does not contain a constant it returns null
    private static Integer getValues(SymbolicExpression expression, DefiniteDataflowDomain<CProp> domain) {
        Integer result = null;

        try {
            if (expression instanceof Constant) {
                Constant c = (Constant) expression;
                if (c.getValue() instanceof Integer) {
                    result = (Integer) c.getValue();
                }
            }

            if (expression instanceof Identifier) {
                Identifier id = (Identifier) expression;
                for (CProp cp : domain.getDataflowElements()) {
                    if (cp.id.equals(id)) result = cp.constant;
                }
            }

            if (expression instanceof UnaryExpression) {
                UnaryExpression u = (UnaryExpression) expression;
                result = getValues(u.getExpression(), domain);
                UnaryOperator uop = u.getOperator();
                if (uop instanceof NumericNegation) {
                    result = -result;
                }

            }

            if (expression instanceof BinaryExpression) {
                BinaryExpression bi = (BinaryExpression) expression;
                Integer n1 = (Integer) getValues(bi.getLeft(), domain);
                Integer n2 = (Integer) getValues(bi.getRight(), domain);
                BinaryOperator biOp = bi.getOperator();

                if (biOp instanceof AdditionOperator) result = n1 + n2;
                if (biOp instanceof SubtractionOperator) result = n1 - n2;
                if (biOp instanceof MultiplicationOperator) result = n1 * n2;
                if (biOp instanceof DivisionOperator) result = n1 / n2;
            }
        } catch (Exception e) {
            return null;
        }

        return result;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, constant);
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

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        return Collections.singleton(this.id);
    }

    @Override
    public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Set<CProp> result = new HashSet<>();
        Integer c = getValues(expression, domain);
        if (c != null) result.add(new CProp(id, c));
        if (id.toString().equals("xy")) System.out.println(id.toString() + " " + c);
        //x = y + z
        return result;

    }

    //since there is no assignment, no constant is created
    @Override
    public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return new HashSet<>();
    }

    @Override
    public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        Integer c = getValues(expression, domain);
        //how to kill a identifier if it is assigned to a non constant value if we don't know it's value? bho
        Set<CProp> killed = new HashSet<>();
        // x = 8
        // x = 10
        if (id.toString().equals("xy")) System.out.println("trying to delete " + id.toString());
        for (CProp cp : domain.getDataflowElements()) {
            if (cp.getInvolvedIdentifiers().contains(id)) {
                System.out.println(id.toString() + " killato, con valore " + cp.constant);
                killed.add(cp);
            }
        }


        return killed;
    }

    //since there is no assignment, no variable is gonna be killed
    @Override
    public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain) throws SemanticException {
        return new HashSet<>();
    }

}
