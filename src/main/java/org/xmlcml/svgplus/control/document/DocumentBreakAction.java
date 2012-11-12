package org.xmlcml.svgplus.control.document;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;

public class DocumentBreakAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentBreakAction.class);
	
	public DocumentBreakAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
	}
	
	@Override
	public void run() {
		throw new RuntimeException("BREAK");
	}


}
