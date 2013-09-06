package org.xmlcml.svg2xml.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlUl;
import org.xmlcml.svg2xml.dead.HtmlEditorDead;
import org.xmlcml.svg2xml.dead.HtmlVisitorDead;

/**
 * Not really the same as the other analyzers, but helps to tidey code
 * @author pm286
 *
 */
public class SpeciesIndexer /*extends HtmlVisitor*/ {
	private static final Logger LOG = Logger.getLogger(SpeciesIndexer.class);
	private static final String BINOMIAL_REGEX_S = "[A-Z][a-z]*\\.?\\s+[a-z][a-z]+(\\s+[a-z]+)*";
	private final static Pattern PATTERN = Pattern.compile(BINOMIAL_REGEX_S);
	private static final String ITALIC_XPATH_S = ".//*[local-name()='i']";

	
	private final static String TITLE = "species";
		
	public SpeciesIndexer() {
	}
	
//	@Override
//	public void visit(HtmlEditorOld htmlEditor) {
//		HtmlUl speciesList = htmlEditor.searchHtml(ITALIC_XPATH_S, PATTERN);
//	}

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

	public String getFileName() {
		return "species.html";
	}


}
