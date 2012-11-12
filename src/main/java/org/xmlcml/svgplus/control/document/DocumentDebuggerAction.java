package org.xmlcml.svgplus.control.document;


import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;

public class DocumentDebuggerAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentDebuggerAction.class);
	
	public DocumentDebuggerAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
	}
	
	@Override
	public void run() {
		LOG.debug(documentAnalyzer.debugString("DOCUMENT DEBUG"));
	}

}
