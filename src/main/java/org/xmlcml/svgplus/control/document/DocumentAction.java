package org.xmlcml.svgplus.control.document;

import java.util.ArrayList;

import java.util.List;

import org.xmlcml.svgplus.control.AbstractAction;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.control.SemanticDocumentAction;
import org.xmlcml.svgplus.control.SemanticDocumentElement;
import org.xmlcml.svgplus.core.AbstractAnalyzer;
import org.xmlcml.svgplus.core.DocumentAnalyzer;
import org.xmlcml.graphics.svg.SVGSVG;

public abstract class DocumentAction extends AbstractAction {

	protected AbstractActionElement documentActionElement;
	protected DocumentAnalyzer documentAnalyzer;
	protected SemanticDocumentAction semanticDocumentAction;
	private DocumentIteratorAction documentIteratorAction;
	
	public DocumentAction(AbstractActionElement documentActionElement) {
		super(documentActionElement);
		this.documentActionElement = documentActionElement;
	}

	/** execute the command
	 * 
	 */
	public abstract void run();

	/**
	 * get the commandElement that generated the action
	 * @return
	 */
	public AbstractActionElement getAbstractActionElement() {
		return this.documentActionElement;
	}
	
	public List<SVGSVG> getPageList() {
		List<SVGSVG> svgPageList = new ArrayList<SVGSVG>();
		return svgPageList;
	}

	public DocumentAnalyzer getDocumentAnalyzer() {
		if (documentAnalyzer == null) {
			if (!(this instanceof DocumentIteratorAction)) {
				documentAnalyzer = getDocumentIteratorAction().getDocumentAnalyzer();
			} else {
				documentAnalyzer = ((DocumentIteratorAction)this).ensureDocumentAnalyzer();
				
			}
		}
		return documentAnalyzer;
	}

	public void setDocumentAnalyzer(DocumentAnalyzer documentAnalyzer) {
		this.documentAnalyzer = documentAnalyzer;
	}
	
	protected AbstractAnalyzer getAnalyzer() {
		return documentAnalyzer;
	}
	
	protected DocumentIteratorAction getDocumentIteratorAction() {
		documentIteratorAction = null;
		if (this instanceof DocumentIteratorAction) {
			documentIteratorAction = (DocumentIteratorAction) this;
		} else {
			DocumentIteratorElement documentIteratorElement = documentActionElement.getAncestorDocumentIteratorElement();
			documentIteratorAction = documentIteratorElement.getDocumentIteratorAction();
		}
		return documentIteratorAction;
	}
	
	protected SemanticDocumentAction getSemanticDocumentAction() {
		SemanticDocumentElement semanticDocumentElement = documentActionElement.getAncestorSemanticDocumentElement();
		semanticDocumentAction = semanticDocumentElement.getSemanticDocumentAction();
		return semanticDocumentAction;
	}
	
	
}
