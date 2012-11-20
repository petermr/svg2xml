package org.xmlcml.svgplus.document;


import org.apache.log4j.Logger;
import org.xmlcml.svgplus.core.AbstractActionElement;

public class DocumentDebuggerAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentDebuggerAction.class);
	
	public DocumentDebuggerAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
	}
	
	@Override
	public void run() {
//		LOG.debug(debugString("DOCUMENT DEBUG"));
		semanticDocumentAction.debug();
	}

}
