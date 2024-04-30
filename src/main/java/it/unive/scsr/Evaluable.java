package it.unive.scsr;

import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;

public interface Evaluable<T extends BaseNonRelationalValueDomain<T>> {
    T eval(SymbolicExpression expression, ProgramPoint pp);
}