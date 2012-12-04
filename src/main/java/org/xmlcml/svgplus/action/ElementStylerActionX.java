package org.xmlcml.svgplus.action;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.StyleBundle;

/**
	<pageAction xpath="//svg:g[@LEAF='3']/svg:g" action="drawBoxes" 
	stroke="blue" strokeWidth="3" fill="cyan" opacity="0.3" />
 * @author pm286
 *
 */
public class ElementStylerActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(ElementStylerActionX.class);
	
	public static final String ANNOTATION_BOX = "annotationBox";
	
	public ElementStylerActionX(AbstractActionX actionElement) {
		super(actionElement);
	}


	public final static String TAG ="styler";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionX.ACTION);
		ATTNAMES.add(PageActionX.FILL);
		ATTNAMES.add(PageActionX.OPACITY);
		ATTNAMES.add(PageActionX.STROKE_WIDTH);
		ATTNAMES.add(PageActionX.STROKE);
		ATTNAMES.add(AbstractActionX.TITLE);
		ATTNAMES.add(AbstractActionX.XPATH);
	}

	/** constructor
	 */
	public ElementStylerActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new ElementStylerActionX(this);
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
		return Arrays.asList(new String[]{
				AbstractActionX.XPATH,
		});
	}

	@Override
	public void run() {
		String xpath = getXPath();
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), xpath);
		for (SVGElement element : elements) {
			if (getStroke() != null) {
				element.addAttribute(new Attribute(StyleBundle.STROKE, ""+getStroke()));
			}
			if (getStrokeWidth() != null) {
				element.addAttribute(new Attribute(StyleBundle.STROKE_WIDTH, ""+getStrokeWidth()));
			}
			if (getFill() != null) {
				element.addAttribute(new Attribute(StyleBundle.FILL, ""+getFill()));
			}
			if (getOpacity() != null) {
				element.addAttribute(new Attribute(StyleBundle.OPACITY, ""+getOpacity()));
			}
//			PageNormalizerAction.removeCSSStyleAndExpandAsSeparateAttributes(element);
		}
	}

}
