package org.xmlcml.svg2xml.pdf;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;

public class PDFAnalyzerTest {

	@Test
	public void Dummy() {
	}

	@Test
//	@Ignore
	public void testPDFAnalyzerPDF() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.MULTIPLE312_PDF);
	}

	@Test
//	@Ignore
	public void multipleTest() {
//		new PDFAnalyzer().analyzePDFFile(Fixtures.GEO310_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.MATH311_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.TREE313_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.BMC174_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.ROBERTS_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.CELL_8994_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.MDPI_02982_PDF);
		new PDFAnalyzer().analyzePDFFile(Fixtures.ELS_1917_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.NATURE_12352_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.PEERJ_50_PDF);
//		new PDFAnalyzer().analyzePDFFile(Fixtures.PLOS_0049149_PDF);
//		new PDFAnalyzer().analyzePDFFile(new File("../pdfs/acs/nn400656n.pdf"));
	}

	@Test
	@Ignore
	public void testPDFAnalyzerUBIQ() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		// this fails to separate 2-column text
//		analyzer.analyzePDFFile(new File("src/test/resources/pdfs/ubiquity/1-4-4-PB.pdf"));
		// this fails with OOME heap space. Why??
		analyzer.analyzePDFFile(new File("src/test/resources/pdfs/ubiquity/63-684-1-PB-1.pdf"));
	}



	@Test
	public void testPDFAnalyzerSVG() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.setRawSvgDirectory(Fixtures.SVG_MULTIPLE312_DIR);
		analyzer.analyzeRawSVGPagesWithPageAnalyzers();
	}

	@Test
	//@Ignore
	public void testPDFAnalyzerDIR() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFs(Fixtures.PDFS_BMC_DIR.toString());
	}	
	
}
