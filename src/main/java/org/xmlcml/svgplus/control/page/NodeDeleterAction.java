package org.xmlcml.svgplus.control.page;


import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;

public class NodeDeleterAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(NodeDeleterAction.class);
	
	public NodeDeleterAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	@Override
	public void run() {
		deleteNodes(getXPath());
	}

}
