package org.xmlcml.svgplus.command;


import org.apache.log4j.Logger;

public class NodeDeleterAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(NodeDeleterAction.class);
	
	public NodeDeleterAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		deleteNodes(getXPath());
	}

}
