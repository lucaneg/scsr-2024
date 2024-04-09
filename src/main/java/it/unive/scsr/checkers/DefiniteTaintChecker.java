package it.unive.scsr.checkers;

import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.StringUtilities;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.scsr.DefiniteTaint;

public class DefiniteTaintChecker implements
SemanticCheck<
		SimpleAbstractState<PointBasedHeap, ValueEnvironment<DefiniteTaint>, TypeEnvironment<InferredTypes>>> {
	
	/**
	 * Sink annotation.
	 */
	public static final Annotation SINK_ANNOTATION = new Annotation("lisa.taint.Sink");

	/**
	 * Sink matcher.
	 */
	public static final AnnotationMatcher SINK_MATCHER = new BasicAnnotationMatcher(SINK_ANNOTATION);

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DefiniteTaint>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		
		if (!(node instanceof UnresolvedCall))
			return true; 
		UnresolvedCall call = (UnresolvedCall) node;
		try {
			for (AnalyzedCFG<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<DefiniteTaint>,
							TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
				
				Call resolved = tool.getResolvedVersion(call, result);
				if (resolved == null)
					System.err.println("Error");

				if (resolved instanceof CFGCall) {
					CFGCall cfg = (CFGCall) resolved;
					for (CodeMember n : cfg.getTargets()) {
						Parameter[] parameters = n.getDescriptor().getFormals();
						for (int i = 0; i < parameters.length; i++)
							if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {
								AnalysisState<
										SimpleAbstractState<PointBasedHeap, ValueEnvironment<DefiniteTaint>,
												TypeEnvironment<InferredTypes>>> state = result
														.getAnalysisStateAfter(call.getParameters()[i]);
								Set<SymbolicExpression> reachableIds = new HashSet<>();
								for (SymbolicExpression e : state.getComputedExpressions())
									reachableIds
											.addAll(state.getState().reachableFrom(e, node, state.getState()).elements);

								for (SymbolicExpression s : reachableIds) {
									ValueEnvironment<DefiniteTaint> valueState = state.getState().getValueState();

									if(valueState.eval((ValueExpression) s, node, state.getState()).isAlwaysTainted())
										tool.warnOn(call, "[DEFINITE] The value passed for the " + StringUtilities.ordinal(i + 1)
										+ " parameter of this call is always tainted, and it reaches the sink at parameter '"
										+ parameters[i].getName() + "' of " + resolved.getFullTargetName());
									else if (valueState.eval((ValueExpression) s, node, state.getState())
											.isPossiblyTainted())
										tool.warnOn(call, "[POSSIBLE] The value passed for the " + StringUtilities.ordinal(i + 1)
												+ " parameter of this call may be tainted, and it reaches the sink at parameter '"
												+ parameters[i].getName() + "' of " + resolved.getFullTargetName());
								}
							}

					}
				} 
			}
		} catch (SemanticException e) {
			System.err.println("Cannot check " + node);
			e.printStackTrace(System.err);
		}

		return true;
	}

	
		
	

}