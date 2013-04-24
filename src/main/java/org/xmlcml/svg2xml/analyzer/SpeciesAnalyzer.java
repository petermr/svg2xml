package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlUl;

/**
 * Not really the same as the other analyzers, but helps to tidey code
 * @author pm286
 *
 */
public class SpeciesAnalyzer extends AbstractPageAnalyzerX {
	private static final Logger LOG = Logger.getLogger(SpeciesAnalyzer.class);
	private static final String BINOMIAL_REGEX_S = "[A-Z][a-z]*\\.?\\s+[a-z][a-z]+(\\s+[a-z]+)*";
	private final static Pattern PATTERN = Pattern.compile(BINOMIAL_REGEX_S);
	private static final String ITALIC_XPATH_S = ".//*[local-name()='i']";

	
	private final static String TITLE = "species";
		
	public SpeciesAnalyzer(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	public void analyze() {
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

	public HtmlUl extractEntities(List<File> htmlFiles) {
		HtmlUl speciesList = pdfIndex.searchHtml(htmlFiles, ITALIC_XPATH_S, PATTERN);
		return speciesList;
	}

	public String getFileName() {
		return "species.html";
	}


}
