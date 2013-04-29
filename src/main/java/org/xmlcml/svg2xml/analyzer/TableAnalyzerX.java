package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.table.Table;

/**
 * @author pm286
 *
 */
public class TableAnalyzerX extends AbstractPageAnalyzerX {
	private static final Logger LOG = Logger.getLogger(TableAnalyzerX.class);
	
	public static final Pattern PATTERN = Pattern.compile("^[Tt][Aa][Bb][Ll]?[Ee]?\\s*\\.?\\s*(\\d+).*", Pattern.DOTALL);
	public static final String TITLE = "TABLE";
	
	public TableAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}

	public TableAnalyzerX(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	public void analyze() {
		LOG.error("Table NYI");
	}

	@Override
	public SVGG labelChunk() {
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
