package org.xmlcml.svg2xml.analyzer;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

public class PDFAnalyzerTest {

	@Test
	public void Dummy() {
	}

	@Test
	@Ignore
	public void testPDFAnalyzerPDF() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.MULTIPLE312_PDF);
	}

	/** commented out for maven
	@Test
	public void testPDFAnalyzerPDFGEO310() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.GEO310_PDF);
	}
	
	@Test
	public void testPDFAnalyzerPDFMATH311() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.MATH311_PDF);
	}

	@Test
	public void testPDFAnalyzerPDFTREE313() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.TREE313_PDF);
	}
	
	
	
	@Test
	public void testPDFAnalyzerROBERTS() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.ROBERTS_PDF);
	}

	@Test
	public void testPDFAnalyzerELS() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.ELS_1917_PDF);
	}

	@Test
	public void testPDFAnalyzerNATURE() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.NATURE_12352_PDF);
	}

	@Test
	public void testPDFAnalyzerPEERJ() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.PEERJ_50_PDF);
	}

	@Test
	public void testPDFAnalyzerPLOS() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.PLOS_0049149_PDF);
	}


	@Test
	public void testPDFAnalyzerSVG() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.setRawSvgDirectory(Fixtures.SVG_MULTIPLE312_DIR);
		analyzer.analyzeRawSVGPagesWithPageAnalyzers();
	}

*/
	@Test
	@Ignore
	public void testPDFAnalyzerDIR() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFs(Fixtures.PDFS_BMC_DIR.toString());
	}	
	
	@Test
	@Ignore 
	public void testKevinACS() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(new File("../pdfs/acs/nn400656n.pdf"));
	}
	
}
