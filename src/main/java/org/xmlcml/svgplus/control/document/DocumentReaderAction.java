package org.xmlcml.svgplus.control.document;

import java.io.File;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;

public class DocumentReaderAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentReaderAction.class);
	
	public DocumentReaderAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
	}
	
	@Override
	public void run() {
		LOG.trace("executing: \n"+getActionCommandElement().getString());
		String filename = getFilename();
		LOG.trace("reading file: "+filename);
		LOG.trace(getActionCommandElement().getString());
		File file = new File(filename);
		String skip = getSkip();
		if (!file.exists() || !file.isDirectory()) {
			throw new RuntimeException("file does not exist or is not a directory: "+file.getAbsolutePath());
		}
		
//		List<SVGSVG> svgPageList = PDF2SVGReader.createPageList(file);
//		LOG.trace("pages read: "+svgPageList.size());
//		documentAnalyzer.setPageList(svgPageList);
	}

}
