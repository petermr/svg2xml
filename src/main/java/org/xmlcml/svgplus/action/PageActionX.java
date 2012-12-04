package org.xmlcml.svgplus.action;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.tools.PageSelector;
import org.xmlcml.svgplus.util.GraphUtil;


public abstract class PageActionX extends AbstractActionX {
	
	final static Logger LOG = Logger.getLogger(PageActionX.class);

	public static final String DOCUMENT = "document";
	public static final String PAGE = "page";
	/** attribute names
	 * 
	 */
	public static final String APPLY_AND_REMOVE_CUMULATIVE_TRANSFORMS = "applyAndRemoveCumulativeTransforms";
	public static final String BOX_COUNT = "boxCount";
	public static final String CAN_ROTATE_LANDSCAPE = "canRotateLandscape";
	public static final String CLEAN_SVG_STYLES = "cleanSVGStyles";
	public static final String DELETE_XPATHS = "deleteXPaths";
	public static final String DENORMALIZE_FONT_SIZES = "denormalizeFontSizes";
	public static final String DEPTH = "depth";
	public static final String FORMAT_DECIMAL_PLACES = "formatDecimalPlaces";
	public static final String FAIL = "fail";
	public static final String FILL = "fill";
	public static final String MARGIN_X = "marginX";
	public static final String MARGIN_Y = "marginY";
	public static final String NORMALIZE_HIGH_CODE_POINTS = "normalizeHighCodePoints";
	public static final String OPACITY = "opacity";
	public static final String PAGE_RANGE = "pageRange";
	public static final String REMOVE_DEFS = "removeDefs";
	public static final String REMOVE_IMAGE_DATA = "removeImageData";
	public static final String REMOVE_UNIT_TRANSFORMS = "removeUnitTransforms";
	public static final String REMOVE_UNWANTED_ATTRIBUTES = "removeUnwantedAttributes";
	public static final String STROKE = "stroke";
	public static final String STROKE_WIDTH = "strokeWidth";
	public static final String TRANSLATE_CLIP_PATHS_TO_PHYSICAL_STYLES = "translateClipPathsToPhysicalStyles";
	public static final String VALUE = "value";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(ACTION);
		ATTNAMES.add(APPLY_AND_REMOVE_CUMULATIVE_TRANSFORMS);
		ATTNAMES.add(BOX_COUNT);
		ATTNAMES.add(CAN_ROTATE_LANDSCAPE);
		ATTNAMES.add(CLEAN_SVG_STYLES);
		ATTNAMES.add(COUNT);
		ATTNAMES.add(DEBUG);
		ATTNAMES.add(DELETE_XPATHS);
		ATTNAMES.add(DENORMALIZE_FONT_SIZES);
		ATTNAMES.add(DEPTH);
		ATTNAMES.add(FAIL);
		ATTNAMES.add(FILENAME);
		ATTNAMES.add(FILL);
		ATTNAMES.add(FORMAT_DECIMAL_PLACES);
		ATTNAMES.add(MARGIN_X);
		ATTNAMES.add(MARGIN_Y);
		ATTNAMES.add(MESSAGE);
		ATTNAMES.add(NAME);
		ATTNAMES.add(NORMALIZE_HIGH_CODE_POINTS);
		ATTNAMES.add(OPACITY);
		ATTNAMES.add(PAGE_RANGE);
		ATTNAMES.add(REGEX);
		ATTNAMES.add(REMOVE_DEFS);
		ATTNAMES.add(REMOVE_IMAGE_DATA);
		ATTNAMES.add(REMOVE_UNIT_TRANSFORMS);
		ATTNAMES.add(REMOVE_UNWANTED_ATTRIBUTES);
		ATTNAMES.add(STROKE);
		ATTNAMES.add(STROKE_WIDTH);
		ATTNAMES.add(TITLE);
		ATTNAMES.add(XPATH);
		ATTNAMES.add(VALUE);
		ATTNAMES.add(VARIABLES);
	}

	public static final String MAX_MBYTE = "maxMbyte";
	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	public final static String TAG = "pageAction";

	// private so it can't be modified - have to copy it - is this a good idea?
	private PageSelector pageSelector;
	private int pageCount;

	public PageActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	
	/** constructor 
	 */
	public PageActionX(String tag) {
		super(tag);
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

	
	public Integer getBoxCount() {
		return getInteger(PageActionX.BOX_COUNT);
	}
	
	public Integer getDecimalPlaces() {
		return getInteger(PageActionX.FORMAT_DECIMAL_PLACES);
	}
	
	public Integer getDepth() {
		return getInteger(PageActionX.DEPTH);
	}
	
	public String getDeleteXPaths() {
		return getAndExpand(PageActionX.DELETE_XPATHS);
	}
	
	public String getFill() {
		return getAndExpand(PageActionX.FILL);
	}
	
	public Double getMarginX() {
		return getDouble(PageActionX.MARGIN_X);
	}
	
	public Double getMarginY() {
		return getDouble(PageActionX.MARGIN_Y);
	}
	
	public Double getOpacity() {
		return getDouble(PageActionX.OPACITY);
	}
	
	public String getPageRange() {
		return getAndExpand(PageActionX.PAGE_RANGE);
	}
	
	public String getStroke() {
		return getAndExpand(PageActionX.STROKE);
	}
	
	public Double getStrokeWidth() {
		return getDouble(PageActionX.STROKE_WIDTH);
	}
	
	protected void deleteNodes(String xpath) {
		if (xpath != null) {
			Nodes nodes = GraphUtil.query(getSVGPage(), xpath);
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		}
	}

	public PageSelector getPageSelector() {
		if (pageSelector == null) {
			String pageRange = getPageRange();
			pageCount = (Integer) semanticDocumentActionX.getVariable(PageIteratorActionX.PAGE_COUNT);
			pageSelector = (pageRange == null) ? null : new PageSelector(pageCount);
		}
		return pageSelector;
	}

	public PageEditorX getPageEditor() {
		return this.semanticDocumentActionX.getPageEditor();
	}
}
