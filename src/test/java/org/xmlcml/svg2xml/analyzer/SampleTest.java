package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.page.PageAnalyzerTest;

public class SampleTest {

	private final static Logger LOG = Logger.getLogger(SampleTest.class);

	public final static File AJCINDIR = new File(Fixtures.EXT_PDFTOP, "ajc");
	public final static File AJCSVGDIR = new File(Fixtures.TARGET, "ajc");
	public final static File AJCOUTDIR = new File(Fixtures.TARGET, "ajc");
	
	public final static String MATHS = "maths-1471-2148-11-311";
	public final static String MULTIPLE = "multiple-1471-2148-11-312";
	public final static String TREE = "tree-1471-2148-11-313";

	public final static String AJC1 = "CH01182";
	
	public void createSVGFixtures() {
//		PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, MATHS);
//		PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, MULTIPLE);
//		PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, TREE);

//		createSVG(AJCINDIR, AJCOUTDIR, AJC1);    // uncomment for AJC

	}
	
	@Test
	public void testSetup() {
		
	}
	
	@Test
	@Ignore
	public void testAnalyzePDFSInBMCDirectory() {
		PageAnalyzerTest.testDirectory(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, Fixtures.BMCOUTDIR, false);
	}

	@Test
	@Ignore
	public void testAnalyzePDFSInElifeDirectory() {
		PageAnalyzerTest.testDirectory(Fixtures.ELIFEINDIR, Fixtures.ELIFESVGDIR, Fixtures.ELIFEOUTDIR, false);
	}
	
	@Test
	@Ignore
	public void testAnalyzePDFSInPeerJDirectory() {
		PageAnalyzerTest.testDirectory(Fixtures.PEERJINDIR, Fixtures.PEERJSVGDIR, Fixtures.PEERJOUTDIR, false);
	}
	
	@Test
	@Ignore
	public void testAny() {
		PageAnalyzerTest.testDirectory(Fixtures.ANYINDIR, Fixtures.ANYSVGDIR, Fixtures.ANYOUTDIR);
	}

	//====================================================================

}
