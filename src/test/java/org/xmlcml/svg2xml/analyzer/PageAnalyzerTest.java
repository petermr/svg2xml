package org.xmlcml.svg2xml.analyzer;

import java.io.File;


import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.svg2xml.Fixtures;


public class PageAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(PageAnalyzerTest.class);
	
	public final static String BMC_GEOTABLE = "geotable-1471-2148-11-310";

	public final static String AJC1 = "CH01182";
	
	@Before
	public void createSVGFixtures() {
//		PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, BMC_GEOTABLE);
	}
	
	@Test
	public void testSetup() {
		
	}
	
//	@Test
//	public void testGeoTablePage() {
//		int page = 2;
//		PDFAnalyzer.analyzeChunkInSVGPage(Fixtures.BMCSVGDIR, BMC_GEOTABLE, page, Fixtures.BMCOUTDIR);
//	}
//	
//	@Test
//	public void testGeoTablePages() {
//		File[] files = new File(Fixtures.BMCSVGDIR, BMC_GEOTABLE).listFiles();
//		PDFAnalyzer.analyzeChunksInPagesInFiles(files, Fixtures.BMCSVGDIR, BMC_GEOTABLE, Fixtures.BMCOUTDIR);
//	}
//
//	@Test
//	public void testBoldResults() {
//		SVGElement svgElement = SVGElement.readAndCreateSVG(new File("src/test/resources/org/xmlcml/svg2xml/svg/bmc/tree-page-2-results.svg"));
//		PDFAnalyzer.analyzeChunkInSVGPage((SVGElement) svgElement.getChildElements().get(0), "chunk", Fixtures.BMCOUTDIR, "results");
//	}
	
	//================================================================
	
	//====================================================================
	
	public static void testDirectory(File inDir, File svgDir, File outDir) {
		testDirectory(inDir, svgDir, outDir, true);
	}

	public static void testDirectory(File inDir, File svgDir, File outDir, boolean skipFile) {
		LOG.debug("inputTopDir: "+inDir+"; svgDir "+svgDir+"; outDir "+outDir);
		File[] files = inDir.listFiles();
		if (files != null) {
			for (File file : files) {
				String path = file.getName().toLowerCase();
				LOG.debug("path: "+path);
				if (path.endsWith(".pdf")) {
					PDFAnalyzer analyzer = new PDFAnalyzer();
					analyzer.setSVGTopDir(svgDir);
					analyzer.setOutputTopDir(outDir);
//					analyzer.setSkipFile(skipFile);
					analyzer.analyzePDFFile(file);
				}
			}
		}
	}

}
