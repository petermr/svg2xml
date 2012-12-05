package org.xmlcml.svgplus.action;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGSVG;

public class PageIteratorActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(PageIteratorActionX.class);

	public static final String PAGE_COUNT = SVGPlusConstantsX.D_DOT+"pageCount";
	public static final String PAGE_NUMBER = SVGPlusConstantsX.P_DOT+"page";
	private List<SVGSVG> svgPageList;
	boolean convertPages = true; // settable by attributes later

	public PageIteratorActionX(AbstractActionX actionElement) {
		super(actionElement);
	}

	
	public final static String TAG ="pageIterator";		

	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(PageActionX.PAGE_RANGE);
		ATTNAMES.add(PageActionX.MAX_MBYTE);
		ATTNAMES.add(AbstractActionX.TIMEOUT);
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
		SVGPlusConverterX svgPlusConverterX = semanticDocumentActionX.ensureSVGPlusConverter();
		if (infile != null) {
			svgPageList = svgPlusConverterX.createSVGPageList(infile);
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
	

}
