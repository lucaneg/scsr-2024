package it.unive.scsr;

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
import it.unive.scsr.checkers.DefiniteTaintChecker;

import org.junit.Test;

public class TaintTest {
    
    // Define the signatures for matching sources, sanitizers, and sinks
    String[] sources = new String[] {"source1", "source2"};
    String[] sanitizers = new String[] {"sanitizer1", "sanitizer2"};
    String[] sinks = new String[] {"sink1", "sinks"};

    @Test
    public void testDefiniteTaint() throws ParsingException, AnalysisException {
        // Parse the program to get the CFG representation of the code in it
        Program program = IMPFrontend.processFile("inputs/taint.imp");

        // Load annotations to identify sources, sanitizer, and sinks during the analysis and checker execution
        loadAnnotations(program);

        // Build a new configuration for the analysis
        LiSAConfiguration conf = new DefaultConfiguration();

        // Specify where files should be generated
        conf.workdir = "outputs/taint";

        // Specify the visual format of the analysis results
        conf.analysisGraphs = GraphType.HTML;

        // Specify to create a JSON file containing warnings triggered by the analysis
        conf.jsonOutput = true;

        // Specify the analysis to execute
        conf.abstractState = DefaultConfiguration.simpleState(
                DefaultConfiguration.defaultHeapDomain(),
                new ValueEnvironment<>(new DefiniteTaint()),
                DefaultConfiguration.defaultTypeDomain());

        // Specify to perform an interprocedural analysis (required to recognize calls to sources, sanitizers, and sinks)
        conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());

        // The DefiniteTaintChecker is executed after the DefiniteTaint analysis and checks if a tainted value is flowed to a sink
        conf.semanticChecks.add(new DefiniteTaintChecker());

        // Instantiate LiSA with the provided configuration
        LiSA lisa = new LiSA(conf);

        // Finally, tell LiSA to analyze the program
        lisa.run(program);
    }

    private void loadAnnotations(Program program) {
        for (Unit unit : program.getUnits()) {
            if (unit instanceof ClassUnit) {
                ClassUnit cunit = (ClassUnit) unit;
                for (CodeMember cm : cunit.getInstanceCodeMembers(false)) {
                    if (isSource(cm))
                        cm.getDescriptor().getAnnotations().addAnnotation(Taint.TAINTED_ANNOTATION);
                    else if (isSanitizer(cm))
                        cm.getDescriptor().getAnnotations().addAnnotation(Taint.CLEAN_ANNOTATION);
                    else if (isSink(cm))
                        for (Parameter param : cm.getDescriptor().getFormals()) {
                            param.addAnnotation(Taint
