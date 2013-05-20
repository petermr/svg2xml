package org.xmlcml.svg2xml.analyzer;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;

/**
 * @author pm286
 *
 */
public class SchemeAnalyzer extends AbstractAnalyzer {
	private static final Logger LOG = Logger.getLogger(SchemeAnalyzer.class);
	public static final Pattern PATTERN = Pattern.compile("^[Ss][Cc][Hh][Ee][Mm][Ee]\\s*\\.?\\s*(\\d+).*", Pattern.DOTALL);
	public final static String TITLE = "SCHEME";
	
	public SchemeAnalyzer(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}
	
	public SchemeAnalyzer(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	public void analyze() {
	}
	
	@Override
	public SVGG annotateChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
	public Integer indexAndLabelChunk(String content, ChunkId id) {
		Integer serial = super.indexAndLabelChunk(content, id);
		// index...
		return serial;
	}
	
	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

}
