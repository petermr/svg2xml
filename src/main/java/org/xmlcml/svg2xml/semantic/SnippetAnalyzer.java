package org.xmlcml.svg2xml.semantic;

import java.util.Set;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.analyzer.AbstractAnalyzer;
import org.xmlcml.svg2xml.analyzer.ChunkId;
import org.xmlcml.svg2xml.analyzer.PDFIndex;

/**
 * @author pm286
 *
 */
public class SnippetAnalyzer extends AbstractAnalyzer {
	private static final Logger LOG = Logger.getLogger(SnippetAnalyzer.class);
	public static final Pattern PATTERN = Pattern.compile("^(" +
			"([Aa][Cc][Kk][Nn][Oo][Ww][Ll][Ee][Dd][Gg][Ee]?[Mm][Ee][Nn][Tt][Ss]?)|" +   // acknowledgments
			"([Ff][Uu][Nn][Dd][Ii][Nn][Gg])|" +   // funding
			"([Gg][Rr][Aa][Nn][Tt]\\s*[Dd][Ii][Ss][Cc][Ll][Oo][Ss][Uu][Rr][Ee])|" +   // grant disclosure
			"([Cc][Oo][Rr][Rr][Ee][Ss][Pp][Oo][Nn][Dd][Ee][Nn][Cc][Ee])|" +   // correspondence
			"([Aa][Uu][Tt][Hh][Oo][Rr]\\s*[Dd][Ee][Tt][Aa][Ii][Ll][Ss])|" +    // author details
			"(.*[Cc][Oo][Rr][Rr][Ee][Ss][Pp][Oo][Nn][Dd][Ii][Nn][Gg]\\s*[Aa][Uu][Tt][Hh][Oo][Rr])|" +    // corresponding author
			"([Au][Uu][Tt][Hh][Oo][Rr].?[Ss]?.?\\s*[Cc][Oo][Nn][Tt][Rr][Ii][Bb][Uu][Tt][Ii][Oo][Nn][Ss])|" +    // contributions
			"([Cc][Oo][Mm][Pp][Ee][Tt][Ii][Nn][Gg]\\s*[Ii][Nn][Tt][Ee][Rr][Ee][Ss][Tt][Ss])|" +    // competing
			"([Kk][Ee][Yy][Ww][Oo][Rr][Dd])|" +    // keyword
			"([Aa][Cc][Aa][Dd][Ee][Mm][Ii][Cc]\\s*[Ee][Dd][Ii][Tt][Oo][Rr])" +    // academic editor
			"([Rr][Ee][Cc][Ee][Ii][Vv][Ee][Dd])" +    // received
			"([Ss][Uu][Bb][Mm][Ii][Tt][Tt][Ee][Dd])" +    // submitted
			").*", Pattern.DOTALL);
	public final static String TITLE = "SNIPPET";
	
	public SnippetAnalyzer(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	public void analyze() {
	}
	
	@Override
	public SVGG oldAnnotateChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
	public Integer indexAndLabelChunk(String content, ChunkId id) {
		Integer serial = super.indexAndLabelChunk(content, id);
		if (serial != null) {
			LOG.trace("SNIPP "+content);
		}
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
