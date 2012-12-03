package org.xmlcml.svgplus.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.command.PageActionElement;
import org.xmlcml.svgplus.command.PageIteratorElement;
import org.xmlcml.svgplus.core.SVGPlusConstants;
import org.xmlcml.svgplus.core.SVGPlusConverter;

public class PageIteratorActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(PageIteratorActionX.class);

	public static final String PAGE_COUNT = SVGPlusConstants.D_DOT+"pageCount";
	public static final String PAGE_NUMBER = SVGPlusConstants.P_DOT+"page";
	private SVGPlusConverter svgPlusConverter;
	private List<SVGSVG> svgPageList;
	boolean convertPages = true; // settable by attributes later

	public PageIteratorActionX(AbstractActionX actionElement) {
		super(actionElement);
	}

	
	public final static String TAG ="pageIterator";		

	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(PageActionElement.PAGE_RANGE);
		ATTNAMES.add(PageActionElement.MAX_MBYTE);
		ATTNAMES.add(PageActionElement.TIMEOUT);
	}
	
	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	/** constructor
	 */
	public PageIteratorActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageIteratorActionX(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}

	@Override
	public void run() {
		getOrCreateSVGPageList();
	}

	private void getOrCreateSVGPageList() {
		File infile = (File) semanticDocumentActionX.getVariable(DocumentIteratorActionX.INPUT_FILE);
		if (infile != null) {
			ensureSVGPlusConverter();
			svgPageList = svgPlusConverter.createSVGPageList(infile);
			semanticDocumentActionX.setVariable(PAGE_COUNT, svgPageList.size());
			LOG.debug("pages "+svgPageList.size());
			if (convertPages) {
				for (int i = 0; i < svgPageList.size(); i++) {
					semanticDocumentActionX.setVariable(PAGE_NUMBER, i+1);
					this.getPageEditor().setSVGPage(svgPageList.get(i));
					runChildActionList();
				}
			}
		}
	}
	
	private void ensureSVGPlusConverter() {
		if (svgPlusConverter == null) {
			svgPlusConverter = semanticDocumentActionX.getSVGPlusConverter();
		}
	}


}
