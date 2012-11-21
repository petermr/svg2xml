package org.xmlcml.svgplus.command;

import org.apache.log4j.Logger;

public class BreakAction extends AbstractAction {

	private final static Logger LOG = Logger.getLogger(BreakAction.class);
	
	public BreakAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		throw new RuntimeException("BREAK");
	}


}
