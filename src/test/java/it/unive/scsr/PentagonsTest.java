package it.unive.scsr;

import org.junit.Test;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.DefaultConfiguration;
import it.unive.lisa.LiSA;

import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.program.Program;

public class PentagonsTest {

	@Test
	public void testPentagons() throws ParsingException, AnalysisException {
		// we parse the program to get the CFG representation of the code in it
		Program program = IMPFrontend.processFile("inputs/pentagons.imp");

		// we build a new configuration for the analysis
		LiSAConfiguration conf = new DefaultConfiguration();

		// we specify where we want files to be generated
		conf.workdir = "outputs/penta";

		// we specify the visual format of the analysis results
		conf.analysisGraphs = GraphType.HTML;

		// we specify the analysis that we want to execute
		
		 conf.abstractState = DefaultConfiguration.simpleState(
				DefaultConfiguration.defaultHeapDomain(),
				new Pentagons(),
				DefaultConfiguration.defaultTypeDomain());
		
		// we instantiate LiSA with our configuration
		LiSA lisa = new LiSA(conf);

		// finally, we tell LiSA to analyze the program
		lisa.run(program);
	}
	
}
