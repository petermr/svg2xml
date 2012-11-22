package org.xmlcml.svgplus.document;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.core.SVGPlusConverter;

public class PageIteratorAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(PageIteratorAction.class);
	
	public PageIteratorAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		LOG.warn("PageIterator NYI");
	}

	public void convertFileToSVGList(File infile) {
		SVGPlusConverter converter = semanticDocumentAction.getSVGPlusConverter();
		List<SVGSVG> svgPageList = converter.createSVGPageList(infile);
		LOG.debug("pages "+svgPageList.size());
	}


}
