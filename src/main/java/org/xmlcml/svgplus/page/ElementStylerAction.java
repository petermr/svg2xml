package org.xmlcml.svgplus.page;

import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.StyleBundle;
import org.xmlcml.svgplus.command.AbstractActionElement;

/**
	<pageAction xpath="//svg:g[@LEAF='3']/svg:g" action="drawBoxes" 
	stroke="blue" strokeWidth="3" fill="cyan" opacity="0.3" />
 * @author pm286
 *
 */
public class ElementStylerAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(ElementStylerAction.class);
	
	public static final String ANNOTATION_BOX = "annotationBox";
	
	public ElementStylerAction(AbstractActionElement actionElement) {
		super(actionElement);
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
			PageNormalizerAction.removeCSSStyleAndExpandAsSeparateAttributes(element);
		}
	}

}
