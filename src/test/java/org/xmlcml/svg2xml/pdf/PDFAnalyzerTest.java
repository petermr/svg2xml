package org.xmlcml.svg2xml.pdf;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

public class PDFAnalyzerTest {

	@Test
	public void Dummy() {
	}

	@Test
//	@Ignore
	public void testPDFAnalyzerPDFWithSVG() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.MULTIPLE312_PDF);
	}

	
	@Test
	@Ignore
	public void multipleTest() {
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/misc/Tz.Vol2.2013-2014.pdf"));
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/misc/Tz.Vol3.2013-2014.pdf"));
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/misc/Tz.Vol4.2013-2014.pdf"));
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/ipcc/FinalDraft_All.pdf"));
//		new PDFAnalyzer().analyzePDFFile(Fixtures.GEO310_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.MATH311_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.TREE313_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.GRAPHIC_TEXT_315_PDF);
//		new PDFAnalyzer().analyzePDFFile(new File("src/test/resources/pdfs/royalsoc/120109.full.pdf"));
//		new PDFAnalyzer().analyzePDFFile(Fixtures.LINEPLOTS_327_PDF);
		new PDFAnalyzer().analyzePDFFile(Fixtures.SCATTERPLOTS_322_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.BMC174_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.ROBERTS_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.CELL_8994_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.MDPI_02982_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.ELS_1917_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.NATURE_12352_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.PEERJ_50_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.PLOS_0049149_PDF);
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/acs/nn400656n.pdf"));
	}

	@Test
//	@Ignore
	public void testPDFAnalyzerUBIQ() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		// this fails to separate 2-column text
//		analyzer.analyzePDFFile(new File("src/test/resources/pdfs/ubiquity/1-4-4-PB.pdf"));
		analyzer.analyzePDFFile(new File("src/test/resources/pdfs/ubiquity/63-684-1-PB-1.pdf"));
	}



	@Test
	@Ignore
	public void testPDFAnalyzerSVG() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.setRawSvgDirectory(Fixtures.SVG_MULTIPLE312_DIR);
		analyzer.analyzeRawSVGPagesWithPageAnalyzers();
	}

	@Test
	@Ignore
	public void testPDFAnalyzerDIR() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFs(Fixtures.PDFS_BMC_DIR.toString());
	}	
	
	@Test
	@Ignore
	public void testSVGBug() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
//		analyzer.analyzePDFs("../pdfs/dmd/Shukla.pdf");
		analyzer.analyzePDFs("../pdfs/mdpi/metabolites-02-00100.pdf");
	}	
	
	@Test
	// uncomment to run tests
	@Ignore
	public void testAllPDFAnalyzerDIR() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
//		analyzer.analyzePDFs("../pdfs/cell");
//		analyzer.analyzePDFs("../pdfs/acs/biochem");
		analyzer.analyzePDFs("../pdfs/mdpi");
//		analyzer.analyzePDFs("../pdfs/bmc/misc");
//		analyzer.analyzePDFs("../pdfs/dmd"); // problem with svg??
	}	
	
	@Test
	@Ignore // may have cured memory limit?
	public void testCHBudget() {
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/misc/2013-06-28-asp-2014-bericht-de.pdf")); // OK		
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/misc/Banedanmark.pdf")); // OK
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/misc/IYR2011DRC.pdf")); // OK
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/misc/2010FINAL.pdf"));
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/misc/ByLawTraffic2011.pdf")); // OK		
	}
	
	@Test
	//@Ignore
	public void testMDPI() {
		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/mdpi/metabolites-02-00039.pdf")); // has bitmapped chemical elements :-)
	}
	
}
