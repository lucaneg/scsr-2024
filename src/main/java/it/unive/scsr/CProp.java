package it.unive.scsr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
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

public class CProp implements DataflowElement<DefiniteDataflowDomain<CProp>, CProp>  {

	// IMPLEMENTATION NOTE:
	// the code below is outside of the scope of the course. You can uncomment
	// it to get your code to compile. Be aware that the code is written
	// expecting that a field named "id" and a field named "constant" exist
	// in this class: if you name them differently, change also the code below
	// to make it work by just using the name of your choice instead of
	// "id"/"constant". If you don't have these fields in your
	// solution, then you should make sure that what you are doing is correct :)

	private Identifier id;
	private Constant constant;
	
	public CProp()
	{
		
		id=null;
		constant=null;
	}
	
	public CProp(Identifier id,Constant constant)
	{
		this.id=id;
		this.constant=constant;
		
	}
	
		@Override
	public int hashCode() {
		return Objects.hash(constant, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CProp other = (CProp) obj;
		return Objects.equals(constant, other.constant) && Objects.equals(id, other.id);
	}


	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		
		Set<Identifier> result = new HashSet<>();
		result.add(id);
		return result;
	}

	@Override
	public Collection<CProp> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		
		Set<CProp> constantEvaluations= new HashSet<>();
		
		if(expression.getClass()== Constant.class) {
			
			CProp c=new CProp(id,(Constant)expression);
			constantEvaluations.add(c);
		}
		else
		{
			if(expression.getClass()== Identifier.class)
			{
				Identifier ide=(Identifier) expression;
				for (CProp cp: domain.getDataflowElements())
				{
					if(cp.id.equals(ide) && cp.getInvolvedIdentifiers().contains(ide))
					{
						
						constantEvaluations.add(cp);
					}
					
				}
				
			}
			else
			{
				if(expression.getClass()==UnaryExpression.class)
				{
					
					UnaryExpression u=(UnaryExpression) expression;
					Integer i=null/*evaluateUnary(u,domain)*/;
					
					if(i!=null)
					{
						Constant c;
						
					
						if(u.getOperator()==NumericNegation.INSTANCE==true)
						{	
							c=new Constant(expression.getStaticType(),Integer.valueOf(-i),expression.getCodeLocation());
							
						}
						else
							c=new Constant(expression.getStaticType(),i,expression.getCodeLocation());
						
						
						constantEvaluations.add(new CProp(id,c));
										
					}
					
			
			}
			else
			{
				if(expression.getClass()==BinaryExpression.class)
				{
					BinaryExpression b=(BinaryExpression) expression;
					
					Integer left=null/*evaluateBinary(b.getLeft(),domain)*/;
					Integer right=null/*evaluateBinary(b.getLeft(),domain)*/;
					
					BinaryOperator op=b.getOperator();
					
					if(left!=null && right!=null) {
						
						Constant c;
						
						if(op instanceof AdditionOperator)
						{
							c=new Constant(expression.getStaticType(),left+right,expression.getCodeLocation());
							constantEvaluations.add(new CProp(id,c));
							
						}
						else
							if(op instanceof SubtractionOperator)
							{
								c=new Constant(expression.getStaticType(),left-right,expression.getCodeLocation());
								constantEvaluations.add(new CProp(id,c));
								
							}
							else
								if(op instanceof MultiplicationOperator)
								{
									c=new Constant(expression.getStaticType(),left*right,expression.getCodeLocation());
									constantEvaluations.add(new CProp(id,c));
									
								}
								else
									if(op instanceof DivisionOperator)
									{
										c=new Constant(expression.getStaticType(),left/right,expression.getCodeLocation());
										constantEvaluations.add(new CProp(id,c));
										
									}
					}
				}
				
			}
				
				
			}
			
		}
		
		
		for (CProp cp: domain.getDataflowElements())
		{
			constantEvaluations.add(cp);
			System.out.println("Expression: "+ expression.getStaticType());
		}
		return constantEvaluations;
	}

	@Override
	public Collection<CProp> gen(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		
		return new HashSet<>(); /*empty set*/
	}

	@Override
	public Collection<CProp> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<CProp> domain) throws SemanticException {
		
		Set<CProp> killed=new HashSet<>();
		
		for (CProp cp: domain.getDataflowElements())
		{	
			if(cp.getInvolvedIdentifiers().contains(id))
			killed.add(cp);
			
		}
		
		return killed;
	}

	@Override
	public Collection<CProp> kill(ValueExpression expression, ProgramPoint pp, DefiniteDataflowDomain<CProp> domain)
			throws SemanticException {
		
		return new HashSet<>(); /*empty set*/
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
