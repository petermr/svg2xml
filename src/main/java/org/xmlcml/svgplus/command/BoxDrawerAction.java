package org.xmlcml.svgplus.command;

import java.util.List;

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
public class BoxDrawerAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(BoxDrawerAction.class);
	
	public static final String ANNOTATION_BOX = "annotationBox";

	private String title;
	
	public BoxDrawerAction(AbstractActionElement actionElement) {
		super(actionElement);
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
        		PageNormalizerAction.removeCSSStyleAndExpandAsSeparateAttributes(box);
    		}
    	}
	}

}
