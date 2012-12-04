package org.xmlcml.svgplus.action;

import org.apache.log4j.Logger;

import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;

public abstract class AbstractPageAnalyzerX {
	
	private final static Logger LOG = Logger.getLogger(AbstractPageAnalyzerX.class);

	protected SVGG svgg; // current svg:gelement
	protected SemanticDocumentActionX semanticDocumentActionX;
	protected PageEditorX pageEditorX;
	
	protected AbstractPageAnalyzerX() {
	}

	protected AbstractPageAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		this();
		this.semanticDocumentActionX = semanticDocumentActionX;
		this.pageEditorX = getPageEditor();
	}
	
	public PageEditorX getPageEditor() {
		return semanticDocumentActionX.getPageEditor();
	}
	
	public SVGSVG getSVGPage() {
		return getPageEditor().getSVGPage();
	}

}
