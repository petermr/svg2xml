package org.xmlcml.svgplus.command;


import org.apache.log4j.Logger;

public class DebugAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DebugAction.class);
	
	public DebugAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		LOG.debug(semanticDocumentAction.debug());
	}

}
