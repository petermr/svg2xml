package org.xmlcml.svgplus.command;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.core.SVGPlusConstants;
import org.xmlcml.svgplus.core.SVGPlusConverter;

public class PageIteratorAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(PageIteratorAction.class);

	public static final String PAGE_COUNT = SVGPlusConstants.D_DOT+"pageCount";
	public static final String PAGE_NUMBER = SVGPlusConstants.P_DOT+"page";
	private SVGPlusConverter svgPlusConverter;
	private List<SVGSVG> svgPageList;
	boolean convertPages = true; // settable by attributes later

	public PageIteratorAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		getOrCreateSVGPageList();
	}

	private void getOrCreateSVGPageList() {
		File infile = (File) semanticDocumentAction.getVariable(DocumentIteratorAction.INPUT_FILE);
		if (infile != null) {
			ensureSVGPlusConverter();
			svgPageList = svgPlusConverter.createSVGPageList(infile);
			semanticDocumentAction.setVariable(PAGE_COUNT, svgPageList.size());
			LOG.debug("pages "+svgPageList.size());
			if (convertPages) {
				for (int i = 0; i < svgPageList.size(); i++) {
					semanticDocumentAction.setVariable(PAGE_NUMBER, i+1);
					this.getPageEditor().setSVGPage(svgPageList.get(i));
					runChildActionList();
				}
			}
		}
	}
	
	private void ensureSVGPlusConverter() {
		if (svgPlusConverter == null) {
			svgPlusConverter = semanticDocumentAction.getSVGPlusConverter();
		}
	}


}
