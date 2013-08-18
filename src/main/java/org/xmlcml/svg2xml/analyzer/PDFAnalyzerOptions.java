package org.xmlcml.svg2xml.analyzer;

/** options for processing in PDFAnalyzer
 * 
 * @author pm286
 *
 */
public class PDFAnalyzerOptions {

	boolean summarize = false;
	PDFAnalyzer pdfAnalyzer;
	boolean outputChunks = true;
	boolean outputHtmlChunks = true;
	boolean outputFigures = true;
	boolean outputFooters = false;
	boolean outputHeaders = false;
	boolean outputTables = true;
	boolean outputRunningText = true;
	public boolean outputHtml = true;

	public PDFAnalyzerOptions(PDFAnalyzer pdfAnalyzer) {
		this.pdfAnalyzer = pdfAnalyzer;
	}

}
