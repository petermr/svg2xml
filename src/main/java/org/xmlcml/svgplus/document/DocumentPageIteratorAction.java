package org.xmlcml.svgplus.document;

import org.apache.log4j.Logger;

import org.xmlcml.svgplus.command.AbstractActionElement;

public class DocumentPageIteratorAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentPageIteratorAction.class);
	
	public DocumentPageIteratorAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		throw new RuntimeException("DocumemntPageIterator NYI");
	}


}
