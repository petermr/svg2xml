package org.xmlcml.svg2xml.analyzer;

/** options for processing in PDFAnalyzer
 * 
 * @author pm286
 *
 */
public class PDFAnalyzerOptions {

	boolean summarize = false;
	boolean outputChunks = true;
	PDFAnalyzer pdfAnalyzer;
	boolean outputHtmlChunks;

	public PDFAnalyzerOptions(PDFAnalyzer pdfAnalyzer) {
		this.pdfAnalyzer = pdfAnalyzer;
	}

}
