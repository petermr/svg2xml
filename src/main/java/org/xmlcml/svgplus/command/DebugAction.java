package org.xmlcml.svgplus.command;


import org.apache.log4j.Logger;
import org.xmlcml.svgplus.document.DocumentAction;

public class DebugAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DebugAction.class);
	
	public DebugAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
	}
	
	@Override
	public void run() {
//		LOG.debug(debugString("DOCUMENT DEBUG"));
		semanticDocumentAction.debug();
	}

}
