package org.xmlcml.svg2xml.indexer;

import java.util.regex.Pattern;

import org.xmlcml.svg2xml.page.TableAnalyzer;
import org.xmlcml.svg2xml.pdf.PDFIndex;

public class TableIndexer extends AbstractIndexer {

	public static final Pattern PATTERN = TableAnalyzer.PATTERN;
	public static final String TITLE = "TABLE";


	public TableIndexer(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	protected Pattern getPattern() {
		return PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	public String getTitle() {
		return TITLE;
	}


}
