package it.unive.scsr;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.DefaultConfiguration;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.outputs.compare.JsonReportComparer;
import it.unive.lisa.outputs.json.JsonReport;
import it.unive.lisa.program.Program;
import it.unive.lisa.util.file.FileManager;

public class ParityTaskEvaluation {

	@Test
	public void testParity() throws ParsingException, AnalysisException {
		Program program = IMPFrontend.processFile("inputs/parity-eval.imp");
		LiSAConfiguration conf = new DefaultConfiguration();
		conf.workdir = "outputs/parity-eval";
		conf.serializeResults = true;
		conf.jsonOutput = true;
		conf.analysisGraphs = GraphType.HTML;
		conf.abstractState = DefaultConfiguration.simpleState(
				DefaultConfiguration.defaultHeapDomain(),
				new ValueEnvironment<>(new ParitySolution()),
				DefaultConfiguration.defaultTypeDomain());

		try {
			FileManager.forceDeleteFolder(conf.workdir);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			fail("Cannot delete working directory '" + conf.workdir + "': " + e.getMessage());
		}

		LiSA lisa = new LiSA(conf);
		lisa.run(program);

		Path expectedPath = Paths.get("expected", "parity-eval");
		Path actualPath = Paths.get("outputs", "parity-eval");

		File expFile = Paths.get(expectedPath.toString(), "report.json").toFile();
		File actFile = Paths.get(actualPath.toString(), "report.json").toFile();
		try {
			JsonReport expected = JsonReport.read(new FileReader(expFile));
			JsonReport actual = JsonReport.read(new FileReader(actFile));
			assertTrue("Results are different",
					JsonReportComparer.compare(expected, actual, expectedPath.toFile(), actualPath.toFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			fail("Unable to find report file");
		} catch (IOException e) {
			e.printStackTrace(System.err);
			fail("Unable to compare reports");
		}
	}
}
