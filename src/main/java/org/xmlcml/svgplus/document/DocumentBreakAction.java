package org.xmlcml.svgplus.document;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.core.AbstractActionElement;

public class DocumentBreakAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentBreakAction.class);
	
	public DocumentBreakAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		throw new RuntimeException("BREAK");
	}


}
