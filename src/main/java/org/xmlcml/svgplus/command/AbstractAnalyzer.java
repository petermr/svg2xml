package org.xmlcml.svgplus.command;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.page.PageAnalyzer;

public abstract class AbstractAnalyzer {
	
	private final static Logger LOG = Logger.getLogger(AbstractAnalyzer.class);

	protected SemanticDocumentAction semanticDocumentAction;
	protected PageAnalyzer pageAnalyzer;  // because almost all analyzers work on pages
	protected SVGSVG svgPage;             // because almost all analyzers work on <svg:svg>s
	protected SVGG svgg;                  // because almost all analyzers work on <svg:g>s

	protected AbstractAnalyzer() {
	}

	protected AbstractAnalyzer(PageAnalyzer pageAnalyzer) {
		this();
		this.pageAnalyzer = pageAnalyzer;
	}
	
	public SVGSVG getSVGPage() {
		return svgPage;
	}

	public void setSVGPage(SVGSVG svgPage) {
		this.svgPage = svgPage;
	}

	protected AbstractAction getSemanticDocumentAction() {
		return semanticDocumentAction;
	}

}
