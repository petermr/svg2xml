package org.xmlcml.svgplus.document;

import java.util.ArrayList;
import java.util.List;


import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.command.AbstractAnalyzer;
import org.xmlcml.svgplus.core.DocumentAnalyzer;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.core.SemanticDocumentElement;

public abstract class DocumentAction extends AbstractAction {

	public DocumentAction(AbstractActionElement documentActionElement) {
		super(documentActionElement);
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

}
