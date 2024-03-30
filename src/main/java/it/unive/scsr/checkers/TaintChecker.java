package it.unive.scsr.checkers;

import java.util.Collection;
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
import it.unive.scsr.Taint;

public class TaintChecker implements
SemanticCheck<
		SimpleAbstractState<PointBasedHeap, ValueEnvironment<Taint>, TypeEnvironment<InferredTypes>>> {
	
	/**
	 * Sink annotation.
	 */
	public static final Annotation SINK_ANNOTATION = new Annotation("lisa.taint.Sink");

	/**
	 * Sink matcher.
	 */
	public static final AnnotationMatcher SINK_MATCHER = new BasicAnnotationMatcher(SINK_ANNOTATION);

	// This method is called for each statement of CFG
	// The idea is to detect calls with formal parameter annotated as sink and check if the value is tainted
	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Taint>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		
		if (!(node instanceof UnresolvedCall))
			return true; // if it is a statement that it is not a call, then we don't care because cannot have paramenters annaotated as sinks

		UnresolvedCall call = (UnresolvedCall) node;
		try {
			// we get the taint analysis results mapped on the CFG containg the call that we want investigate
			for (AnalyzedCFG<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Taint>,
							TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
				
				// we resolve the call, i.e. we ensure that call has been correctly processed by the analysis
				Call resolved = tool.getResolvedVersion(call, result);
				if (resolved == null)
					System.err.println("Error");

				if (resolved instanceof CFGCall) {
					CFGCall cfg = (CFGCall) resolved;
					for (CodeMember n : cfg.getTargets()) {
						// we check if the call parameters are annotated as sinks
						Parameter[] parameters = n.getDescriptor().getFormals();
						for (int i = 0; i < parameters.length; i++)
							if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {
								//we check if the parameter annotated as sink may be tainted
								AnalysisState<
										SimpleAbstractState<PointBasedHeap, ValueEnvironment<Taint>,
												TypeEnvironment<InferredTypes>>> state = result
														.getAnalysisStateAfter(call.getParameters()[i]);
								Set<SymbolicExpression> reachableIds = new HashSet<>();
								for (SymbolicExpression e : state.getComputedExpressions())
									reachableIds
											.addAll(state.getState().reachableFrom(e, node, state.getState()).elements);

								for (SymbolicExpression s : reachableIds) {
									ValueEnvironment<Taint> valueState = state.getState().getValueState();

									if (valueState.eval((ValueExpression) s, node, state.getState())
											.isPossiblyTainted())
										// in the sink flows a possible tainted data, then we report an warning in the LiSA report result 
										tool.warnOn(call, "The value passed for the " + StringUtilities.ordinal(i + 1)
												+ " parameter of this call may be tainted, and it reaches the sink at parameter '"
												+ parameters[i].getName() + "' of " + resolved.getFullTargetName());
								}
							}

					}
				} 
				// ... case of NativeCall
			}
		} catch (SemanticException e) {
			System.err.println("Cannot check " + node);
			e.printStackTrace(System.err);
		}

		return true;
	}

	
		
	

}