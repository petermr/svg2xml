package org.xmlcml.svg2xml.pdf;

/** options for processing in PDFAnalyzer
 * 
 * @author pm286
 *
 */
public class PDFAnalyzerOptions {

	public static final int PLACES = 6;
	
	boolean summarize = false;
	PDFAnalyzer pdfAnalyzer;
	boolean outputChunks = false;
	boolean outputHtmlChunks = false;
	boolean outputRawFigureHtml = false;
	boolean outputFooters = false;
	boolean outputHeaders = false;
	boolean outputImages = true;
	boolean outputRawTableHtml = false;
	boolean outputRunningText = true;
	public boolean outputHtml = true;

	public PDFAnalyzerOptions(PDFAnalyzer pdfAnalyzer) {
		this.pdfAnalyzer = pdfAnalyzer;
	}

}
