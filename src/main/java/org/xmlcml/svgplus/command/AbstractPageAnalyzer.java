package org.xmlcml.svgplus.command;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.core.SemanticDocumentAction;

public abstract class AbstractPageAnalyzer {
	
	private final static Logger LOG = Logger.getLogger(AbstractPageAnalyzer.class);

	protected SVGG svgg; // current svg:gelement
	protected SemanticDocumentAction semanticDocumentAction;
	protected PageEditor pageEditor;
	
	protected AbstractPageAnalyzer() {
	}

	protected AbstractPageAnalyzer(SemanticDocumentAction semanticDocumentAction) {
		this();
		this.semanticDocumentAction = semanticDocumentAction;
		this.pageEditor = getPageEditor();
	}
	
	public PageEditor getPageEditor() {
		return semanticDocumentAction.getPageEditor();
	}
	
	public SVGSVG getSVGPage() {
		return getPageEditor().getSVGPage();
	}

}
