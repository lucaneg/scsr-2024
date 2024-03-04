package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp>{

	// IMPLEMENTATION NOTE:
	// the code below is outside of the scope of the course. You can uncomment
	// it to get your code to compile. Be aware that the code is written
	// expecting that a field named "id" and a field named "constant" exist
	// in this class: if you name them differently, change also the code below
	// to make it work by just using the name of your choice instead of
	// "id"/"constant". If you don't have these fields in your
	// solution, then you should make sure that what you are doing is correct :)
		
		public final Identifier id;
		public final Integer constant;
		
		public CProp () {
			this.id = null;
			this.constant = null;
		}

		public CProp (Identifier id, Integer constant) {
			this.id = id;
			this.constant = constant;
		}
		
		public Integer evaluate(SymbolicExpression exp, DefiniteDataflowDomain<CProp>domain) {
			if(exp instanceof Identifier) {
				for (CProp cp: domain.getDataflowElements())
					if (cp.id.equals(exp))
						return cp.constant;
				return null;
			}
			if(exp instanceof Constant) {
				Constant temp=(Constant)exp;
				if(temp.getValue() instanceof Integer) {
					return (Integer) temp.getValue();
				}
				else {
					return null;
				}
			}
			if(exp instanceof UnaryExpression) {
				UnaryExpression temp=(UnaryExpression)exp;
				Integer result=evaluate(temp.getExpression(), domain);
				if(result==null) {
					return result;
				}
				if(temp.getOperator() instanceof NumericNegation) {
					return -result;
				}
			}
			if(exp instanceof BinaryExpression) {
				BinaryExpression temp=(BinaryExpression)exp;
				BinaryOperator op=temp.getOperator();
				Integer left=evaluate(temp.getLeft(), domain);
				Integer right=evaluate(temp.getRight(), domain);
				
				if(left==null||right==null)return null;
				if(op instanceof AdditionOperator)return left+right;
				if(op instanceof SubtractionOperator)return left-right;
				if(op instanceof MultiplicationOperator)return left*right;
				if(op instanceof DivisionOperator)return left/right;
			}
			
		return null;	
		}
		
		@Override
		public Collection<Identifier> getInvolvedIdentifiers() {
			return Collections.singleton(id);
		}

		@Override
		public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
				DefiniteDataflowDomain<CProp> domain) throws SemanticException {
			HashSet<CProp> result = new HashSet<>();
			Integer constant = evaluate(expression, domain);
			if (constant != null)
					result.add(new CProp(id, constant));
			return result;
		}

		@Override
		public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
				throws SemanticException {
			return new HashSet<>();
		}

		@Override
		public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
				DefiniteDataflowDomain<CProp> domain) throws SemanticException {
			HashSet<CProp> result = new HashSet<>();
			if (expression instanceof Constant)
				return result;
			for (CProp element : domain.getDataflowElements()) {
				if(element.id.equals(id))
					result.add(element);
				}
			return result;
		}

		@Override
		public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
				throws SemanticException {
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
