package org.xmlcml.svgplus.page;


import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractActionElement;

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
