package org.xmlcml.svgplus.document;

import java.util.ArrayList;
import java.util.List;


import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.core.AbstractAction;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.core.AbstractAnalyzer;
import org.xmlcml.svgplus.core.DocumentAnalyzer;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.core.SemanticDocumentElement;

public abstract class DocumentAction extends AbstractAction {

	public DocumentAction(AbstractActionElement documentActionElement) {
		super(documentActionElement);
		this.actionElement = documentActionElement;
	}

	protected DocumentAction() {
		super();
	}

	/** execute the command
	 * 
	 */
	public abstract void run();

	public List<SVGSVG> getPageList() {
		List<SVGSVG> svgPageList = new ArrayList<SVGSVG>();
		return svgPageList;
	}

//	public DocumentAnalyzer getDocumentAnalyzer() {
//		if (documentAnalyzer == null) {
//			if (!(this instanceof DocumentIteratorAction)) {
//				documentAnalyzer = getDocumentIteratorAction().getDocumentAnalyzer();
//			} else {
//				documentAnalyzer = ((DocumentIteratorAction)this).ensureDocumentAnalyzer();
//				
//			}
//		}
//		return documentAnalyzer;
//	}

//	protected DocumentIteratorAction getDocumentIteratorAction() {
//		documentIteratorAction = null;
//		if (this instanceof DocumentIteratorAction) {
//			documentIteratorAction = (DocumentIteratorAction) this;
//		} else {
//			DocumentIteratorElement documentIteratorElement = documentActionElement.getAncestorDocumentIteratorElement();
//			documentIteratorAction = documentIteratorElement.getDocumentIteratorAction();
//		}
//		return documentIteratorAction;
//	}
	
}
