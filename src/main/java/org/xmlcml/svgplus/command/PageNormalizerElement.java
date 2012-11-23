package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;


public class PageNormalizerElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(PageNormalizerElement.class);

	public final static String TAG ="pageNormalizer";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.APPLY_AND_REMOVE_CUMULATIVE_TRANSFORMS);
		ATTNAMES.add(PageActionElement.CAN_ROTATE_LANDSCAPE);
		ATTNAMES.add(PageActionElement.CLEAN_SVG_STYLES);
		ATTNAMES.add(PageActionElement.DENORMALIZE_FONT_SIZES);
		ATTNAMES.add(PageActionElement.FORMAT_DECIMAL_PLACES);
		ATTNAMES.add(PageActionElement.NORMALIZE_HIGH_CODE_POINTS);
		ATTNAMES.add(PageActionElement.REMOVE_IMAGE_DATA);
		ATTNAMES.add(PageActionElement.REMOVE_UNIT_TRANSFORMS);
		ATTNAMES.add(PageActionElement.REMOVE_UNWANTED_ATTRIBUTES);
	}

	/** constructor
	 */
	public PageNormalizerElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public PageNormalizerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageNormalizerElement(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return null;
	}

	@Override
	protected AbstractAction createAction() {
		return new PageNormalizerAction(this);
	}

}
