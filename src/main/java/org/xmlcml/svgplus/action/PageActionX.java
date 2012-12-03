package org.xmlcml.svgplus.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.command.PageActionElement;
import org.xmlcml.svgplus.command.PageEditor;
import org.xmlcml.svgplus.command.PageIteratorAction;
import org.xmlcml.svgplus.tools.PageSelector;
import org.xmlcml.svgplus.util.GraphUtil;


public abstract class PageActionX extends AbstractActionX {
	
	final static Logger LOG = Logger.getLogger(PageActionX.class);

	public static final String DOCUMENT = "document";
	public static final String PAGE = "page";
	public static final String EXIT = "exit";

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
	public static final String VARIABLES = "variables";
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
		return getInteger(PageActionElement.BOX_COUNT);
	}
	
	public Integer getDecimalPlaces() {
		return getInteger(PageActionElement.FORMAT_DECIMAL_PLACES);
	}
	
	public Integer getDepth() {
		return getInteger(PageActionElement.DEPTH);
	}
	
	public String getDeleteXPaths() {
		return getAndExpand(PageActionElement.DELETE_XPATHS);
	}
	
	public String getFail() {
		return getAndExpand(PageActionElement.FAIL);
	}
	
	public String getFill() {
		return getAndExpand(PageActionElement.FILL);
	}
	
	public Double getMarginX() {
		return getDouble(PageActionElement.MARGIN_X);
	}
	
	public Double getMarginY() {
		return getDouble(PageActionElement.MARGIN_Y);
	}
	
	public Double getOpacity() {
		return getDouble(PageActionElement.OPACITY);
	}
	
	public String getPageRange() {
		return getAndExpand(PageActionElement.PAGE_RANGE);
	}
	
	public String getStroke() {
		return getAndExpand(PageActionElement.STROKE);
	}
	
	public Double getStrokeWidth() {
		return getDouble(PageActionElement.STROKE_WIDTH);
	}
	
	public List<String> getVariables() {
		String s = this.getAttributeValue(PageActionElement.VARIABLES);
		String[] ss = (s == null) ? null : s.split(CMLConstants.S_WHITEREGEX);
		return (ss == null) ? null : Arrays.asList(ss);
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
			pageCount = (Integer) semanticDocumentActionX.getVariable(PageIteratorAction.PAGE_COUNT);
			pageSelector = (pageRange == null) ? null : new PageSelector(pageCount);
		}
		return pageSelector;
	}

	protected void fail(String string) {
		String fail = getFail();
		if (EXIT.equalsIgnoreCase(fail)) {
			throw new RuntimeException(string+" ... "+this.toXML());
		} else {
			LOG.error("******** FAIL: "+string+" *************");
		}
	}

	protected void warn(String string) {
		LOG.error("******** WARN: "+string+" *************");
	}

	protected void info(String string) {
		LOG.error("******** INFO: "+string+" *************");
	}

	protected void log(String string) {
		info(string);
	}

	protected void debugFile(String filename) {
		SVGSVG svg = new SVGSVG(getSVGPage());
		List<SVGElement> defs = SVGUtil.getQuerySVGElements(svg, ".//svg:defs");
		for (SVGElement def : defs) def.detach();
		CMLUtil.outputQuietly(svg, new File(filename), 1);
	}

	public PageEditorX getPageEditor() {
		return this.semanticDocumentActionX.getPageEditor();
	}
}
