package it.unive.scsr;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.ConstantPropagation;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.analysis.dataflow.PossibleDataflowDomain;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.numeric.Subtraction;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.*;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import javassist.bytecode.analysis.Type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CProp
    implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp> {
    /*
    The domain should track:
    • Assignments to constants (store the constant-variable pair)
    • Assignments to constant expressions (evaluate expressions containing constants and variables,
        and store the new constant-variable pair if the result is constant
        - support x+y, x-y, x*y, x/y, -x)
    ===> above two will be combined into constant and id.
    • When a variable is assigned to a non-constant value, kill it
    */

    // instances of this class are dataflow elements such that:
    // - their state (fields) hold the information contained into a single
    // element
    // - they provide gen and kill functions that are specific to the
    // analysis that we are executing
    // Every cloud has a silver lining.
    /**
     * The variable being defined
     */
    private final Identifier id;

    /**
     * The number being evaluated as constant value
     */
    private final Integer constant;

	public CProp() {
        this(null, null);
    }

	public CProp(
            Identifier id,
            Integer constant) {
        this.id = id;
        this.constant = constant;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((constant == null) ? 0 : constant.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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

    @Override
    public Collection<Identifier> getInvolvedIdentifiers() {
        Set<Identifier> result = new HashSet<>();
        result.add(id);
        return result;
    }

    private static Integer evaluateExpression(
            ValueExpression expression,
            DefiniteDataflowDomain<CProp> domain) {

        Integer resultConstant = null;

        if (expression == null) {
            return null;
        }
        else if (expression instanceof Constant) {
            resultConstant = ((Integer) ((Constant) expression).getValue());
        }
        else if (expression instanceof Identifier) {
            for (CProp cp : domain.getDataflowElements()) {
                if (cp.id.equals(expression))
                    resultConstant = cp.constant;
            }
        }
        else if (expression instanceof UnaryExpression unary) {
            // support -x (possibly constant - therefore, recursive)
            resultConstant = (evaluateExpression((ValueExpression) unary.getExpression(), domain));

            if(resultConstant == null) {
                return null;
            }
            else if(unary.getOperator() instanceof SubtractionOperator) {
                resultConstant *= -1;
            }
        }
        else if (expression instanceof BinaryExpression binary) {
            // support x+y, x-y, x*y, x/y
            Integer leftResult = evaluateExpression((ValueExpression) binary.getLeft(), domain);
            Integer rightResult = evaluateExpression((ValueExpression) binary.getRight(), domain);

            if (leftResult == null || rightResult == null) {
                return null;
            }
            else if (binary.getOperator() instanceof AdditionOperator) {
                resultConstant = leftResult + rightResult;
            }
            else if (binary.getOperator() instanceof SubtractionOperator) {
                resultConstant = leftResult - rightResult;
            }
            else if (binary.getOperator() instanceof MultiplicationOperator) {
                resultConstant = leftResult * rightResult;
            }
            else if (binary.getOperator() instanceof DivisionOperator) {
                if (rightResult == 0) {
                    return null;
                }
                resultConstant = leftResult / rightResult;
            }
        }

        return resultConstant;
    }

    @Override
    public Collection<CProp> gen(
            Identifier id,
            ValueExpression expression,
            ProgramPoint pp,
            DefiniteDataflowDomain<CProp> domain)
        throws SemanticException {
        // we generate a new element tracking this definition
        Set<CProp> result = new HashSet<>();
        Integer genConstant = evaluateExpression(expression, domain);
        if(genConstant != null){
            CProp cprop = new CProp(id, evaluateExpression(expression, domain));
            result.add(cprop);
        }
        return result;
    }

    @Override
    public Collection<CProp> gen(
            ValueExpression expression,
            ProgramPoint pp,
            DefiniteDataflowDomain<CProp> domain)
        throws SemanticException {
        // if no assignment is performed, no element is generated!
        return new HashSet<>();
    }

    @Override
    public Collection<CProp> kill(
            Identifier id,
            ValueExpression expression,
            ProgramPoint pp,
            DefiniteDataflowDomain<CProp> domain)
        throws SemanticException {
        // we kill all of the elements that refer to the variable being
        // assigned, as we are redefining the variable
        Set<CProp> killed = new HashSet<>();
        for (CProp cp : domain.getDataflowElements())
            // refer to one variable at a time
            if (cp.getInvolvedIdentifiers().contains(id)) {
                killed.add(cp);
            }

        return killed;
    }

    @Override
    public Collection<CProp> kill(
            ValueExpression expression,
            ProgramPoint pp,
            DefiniteDataflowDomain<CProp> domain)
        throws SemanticException {
        // if no assignment is performed, no element is killed!
        return new HashSet<>();
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
