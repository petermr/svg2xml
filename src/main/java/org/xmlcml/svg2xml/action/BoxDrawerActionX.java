package org.xmlcml.svg2xml.action;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGUtil;

/**
	<pageAction xpath="//svg:g[@LEAF='3']/svg:g" action="drawBoxes" 
	stroke="blue" strokeWidth="3" fill="cyan" opacity="0.3" />
 * @author pm286
 *
 */
public class BoxDrawerActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(BoxDrawerActionX.class);
	
	public static final String ANNOTATION_BOX = "annotationBox";

	private String title;

	
	public final static String TAG ="boxDrawer";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	/** attribute names
	 * 
	 */

	static {
		ATTNAMES.add(PageActionX.FILL);
		ATTNAMES.add(PageActionX.OPACITY);
		ATTNAMES.add(PageActionX.STROKE_WIDTH);
		ATTNAMES.add(PageActionX.STROKE);
		ATTNAMES.add(AbstractActionX.TITLE);
		ATTNAMES.add(AbstractActionX.XPATH);
	}

	/** constructor
	 */
	public BoxDrawerActionX() {
		super(TAG);
	}
	
	/** constructor
	 */
	public BoxDrawerActionX(AbstractActionX element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new BoxDrawerActionX(this);
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
		title = getTitle();
		drawBoxes();
	}

	private void drawBoxes() {
    	List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), getXPath());
    	for (SVGElement element : elements) {
    		SVGRect box = element.drawBox(this.getStroke(), this.getFill(), this.getStrokeWidth(), this.getOpacity());
    		if (box != null) {
        		if (title != null) {
        			box.setTitle(title);
        		}
//        		PageNormalizerAction.removeCSSStyleAndExpandAsSeparateAttributes(box);
    		}
    	}
	}

}
