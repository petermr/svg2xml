package org.xmlcml.svg2xml.indexer;

import java.util.regex.Pattern;

import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.pdf.PDFIndex;

public class FigureIndexer extends AbstractIndexer {

	public static final Pattern CAPTION_PATTERN = FigureAnalyzer.CAPTION_PATTERN;
	public static final String TITLE = "FIGURE";


	public FigureIndexer(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	protected Pattern getPattern() {
		return CAPTION_PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	public String getTitle() {
		return TITLE;
	}


}
