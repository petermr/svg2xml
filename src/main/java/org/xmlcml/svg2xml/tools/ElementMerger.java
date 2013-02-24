package org.xmlcml.svg2xml.tools;

import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.svgplus.paths.LineMerger;

/** merges two or more SVGElements
 * 
 * @author pm286
 *
 */
public abstract class ElementMerger {

	protected SVGElement elem0;
	protected double eps;

	/** this could be messy
	 * it is recommended to use a vistor pattern and reflection
	 * this is a quick lashup
	 * 
	 * @param elem to merge
	 * @return new element (null if none created)
	 */
	public static ElementMerger createElementMerger(SVGElement elem, double eps) {
		ElementMerger elementMerger = null;
		if (elem != null && elem instanceof SVGLine) {
			elementMerger = new LineMerger((SVGLine)elem, eps);
		}
		return elementMerger;
	}
	
	public ElementMerger(SVGElement elem, double eps) {
		this.elem0 = elem;
		this.eps = eps;
	}
	
	/** this could be messy
	 * it is recommended to use a vistor pattern and reflection
	 * this is a quick lashup
	 * 
	 * @param elem to merge
	 * @return new element (null if none created)
	 */
	public abstract SVGElement createNewElement(SVGElement elem);

}
