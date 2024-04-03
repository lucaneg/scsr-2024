package it.unive.scsr;

import org.junit.Test;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.DefaultConfiguration;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.scsr.checkers.TaintChecker;

public class TaintTest {
	
	
	
	// we define the signatures for matching sources, sanitizers, and sinks
	String[] sources = new String[] {"source1", "source2"};
	String[] sanitizers = new String[] {"sanitizer1", "sanitizer2"};
	String[] sinks = new String[] {"sink1", "sinks"};
	

	@Test
	public void testTaint() throws ParsingException, AnalysisException {
		// we parse the program to get the CFG representation of the code in it
		Program program = IMPFrontend.processFile("inputs/taint.imp");

		// we load annotation for identify sources, sanitizer, and sinks during the analysis and checker execution
		loadAnnotations(program);
		
		// we build a new configuration for the analysis
		LiSAConfiguration conf = new DefaultConfiguration();

		// we specify where we want files to be generated
		conf.workdir = "outputs/taint";

		// we specify the visual format of the analysis results
		conf.analysisGraphs = GraphType.HTML;
		
		// we specify the create a json file containing warnings triggered by the analysis
		conf.jsonOutput= true;

		// we specify the analysis that we want to execute
		
		 conf.abstractState = DefaultConfiguration.simpleState(
				DefaultConfiguration.defaultHeapDomain(),
				new ValueEnvironment<>(new Taint()),
				DefaultConfiguration.defaultTypeDomain());
		 
		 // we specify to perform an interprocedural analysis (require to recognize calls to sources, sanitizers, and sinks)
		 conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		 
		 // the TaintChecker is executed after the Taint analysis and it checks if a tainted value is flowed in a sink
		 conf.semanticChecks.add(new TaintChecker());
		 
		// we instantiate LiSA with our configuration
		LiSA lisa = new LiSA(conf);
		

		// finally, we tell LiSA to analyze the program
		lisa.run(program);
	}


	private void loadAnnotations(Program program) {
		
		for(Unit unit : program.getUnits()) {
			if(unit instanceof ClassUnit) {
				ClassUnit cunit = (ClassUnit) unit;
				for(CodeMember cm : cunit.getInstanceCodeMembers(false)) {
					if(isSource(cm))
						cm.getDescriptor().getAnnotations().addAnnotation(Taint.TAINTED_ANNOTATION);
					else if(isSanitizer(cm)) 
						cm.getDescriptor().getAnnotations().addAnnotation(Taint.CLEAN_ANNOTATION);
					else if(isSink(cm))
						for(Parameter param : cm.getDescriptor().getFormals()) {
							param.addAnnotation(TaintChecker.SINK_ANNOTATION);
						}		
				}	
			}
		}
		
	}


	private boolean isSource(CodeMember cm) {
		for(String signatureName : sources) {
			if(cm.getDescriptor().getName().equals(signatureName))
				return true;
		}
		return false;
	}
	

	private boolean isSanitizer(CodeMember cm) {
		for(String signatureName : sanitizers) {
			if(cm.getDescriptor().getName().equals(signatureName))
				return true;
		}
		return false;
	}
	

	private boolean isSink(CodeMember cm) {
		for(String signatureName : sinks) {
			if(cm.getDescriptor().getName().equals(signatureName))
				return true;
		}
		return false;
	}
	
}
