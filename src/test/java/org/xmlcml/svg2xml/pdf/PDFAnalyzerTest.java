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
	//@Ignore
	public void testPDFAnalyzerPDFWithSVG() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(Fixtures.MULTIPLE312_PDF);
	}


	public static void analyzePDF(String filename) {
		File file = new File(filename);
		if (file.exists() && !file.isDirectory()) {
			new PDFAnalyzer().analyzePDFFile(file);
		} else {
			throw new RuntimeException("File must exist and not be directory: "+filename);
		}
	}
	
}
