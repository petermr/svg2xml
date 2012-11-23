package org.xmlcml.svgplus.command;

import java.io.File;

import org.apache.log4j.Logger;

public class DocumentReaderAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentReaderAction.class);
	
	public DocumentReaderAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		LOG.trace("executing: \n"+getActionElement().getString());
		String filename = getFilename();
		LOG.trace("reading file: "+filename);
		LOG.trace(getActionElement().getString());
		File file = new File(filename);
		String skip = getSkip();
		if (!file.exists() || !file.isDirectory()) {
			throw new RuntimeException("file does not exist or is not a directory: "+file.getAbsolutePath());
		}
		
	}

}
