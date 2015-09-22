package org.xmlcml.svg2xml;

import java.io.File;

import org.xmlcml.svg2xml.pdf.PDFAnalyzerTest;

public class Prototypes {

	public static void main(String[] args) {
		carnosic();
	}
	
	private static void carnosic() {
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/phytochem", "src/test/resources/elsevier/carnosic.pdf"
//		new SVG2XMLConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/phytochem", "src/test/resources/elsevier/carnosic.pdf"
//		PDFAnalyzerTest.analyzePDF("src/test/resources/pdfs/els/1-s2.0-S1055790313001917-main.pdf");
		PDFAnalyzerTest.analyzePDF("src/test/resources/pdfs/els/carnosic.pdf");
	
	}

}
