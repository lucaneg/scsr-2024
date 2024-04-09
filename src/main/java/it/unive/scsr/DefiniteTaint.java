package it.unive.scsr;


import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.taint.BaseTaint;
import it.unive.lisa.analysis.taint.ThreeLevelsTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;


public class DefiniteTaint extends BaseTaint<DefiniteTaint>  {
  
  private static final DefiniteTaint TOP = new DefiniteTaint(true);
  private static final DefiniteTaint TAINT = new DefiniteTaint(true);
  private static final DefiniteTaint CLEAN = new DefiniteTaint(false);
  private static final DefiniteTaint BOTTOM = new DefiniteTaint(null);
  
  
  Boolean taint;
  
  public DefiniteTaint() {
    this(true);
  }

  public DefiniteTaint(Boolean taint) {
    this.taint = taint;
  }
  

  @Override
  public DefiniteTaint lubAux(DefiniteTaint other) throws SemanticException {
    if(this== TAINT  other== TAINT)
      return TAINT;
    else if(this==CLEAN && other== CLEAN)
      return CLEAN;
    else
      return TOP;
  }

  @Override
  public boolean lessOrEqualAux(DefiniteTaint other) throws SemanticException {
    if(this== BOTTOM)
      return true;
    else if((this == CLEAN  this == TAINT) && other==TOP)
      return true;
    else if((this == CLEAN  this == TAINT) && other==BOTTOM)
      return false;
    else if(this==TOP && other== TOP)
      return true;
    else 
      return this.lessOrEqual(other);
  }

  @Override
  public DefiniteTaint top() {
    return TOP;
  }

  @Override
  public DefiniteTaint bottom() {
    return BOTTOM;
  }

  @Override
  protected DefiniteTaint tainted() {
  
    return TAINT;
  }

  @Override
  protected DefiniteTaint clean() {
    return CLEAN;
  }

  @Override
  public boolean isAlwaysTainted() {
    if( this== TAINT)
      return true;
    else
      return false;
  }

  @Override
  public boolean isPossiblyTainted() {
    if( this== TOP)
      return true;
    else
      return false;
  }
  
  public DefiniteTaint evalBinaryExpression(
      BinaryOperator operator,
      ThreeLevelsTaint left,
      ThreeLevelsTaint right,
      ProgramPoint pp,
      SemanticOracle oracle)
      throws SemanticException {
    if(left.isTop()  right.isTop())
      return TOP;
    else if(left.isBottom() right.isBottom())
      return BOTTOM;
    else if  (operator instanceof AdditionOperator  operator instanceof SubtractionOperator  operator instanceof MultiplicationOperator  operator instanceof DivisionOperator) {
      if (left.isAlwaysTainted() && right.isAlwaysTainted()) 
        return TAINT;
      else if (left.isAlwaysClean() && right.isAlwaysClean())
        return CLEAN;
      else if ((left.equals(right)))
        return TAINT;
    }
    
    return TOP;
  }
  
  @Override
  public DefiniteTaint wideningAux(
      DefiniteTaint other)
      throws SemanticException {
    return lubAux(other);
  }
// IMPLEMENTATION NOTE:
  // the code below is outside of the scope of the course. You can uncomment
  // it to get your code to compile. Be aware that the code is written
  // expecting that you have constants for identifying top, bottom, even and
  // odd elements as we saw for the sign domain: if you name them differently,
  // change also the code below to make it work by just using the name of your
  // choice. If you use methods instead of constants, change == with the
  // invocation of the corresponding method
  
    @Override
  public StructuredRepresentation representation() {
    return this == BOTTOM ? Lattice.bottomRepresentation() : this == TOP ? Lattice.topRepresentation() : this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
  }
    
     
}
