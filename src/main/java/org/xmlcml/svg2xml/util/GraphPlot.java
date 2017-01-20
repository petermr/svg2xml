package org.xmlcml.svg2xml.util;

import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGRect;

/** utilities for plotting
 * 
 * @author pm286
 *
 */
public class GraphPlot {

	public static SVGRect plotBox(Real2Range bbox, String fill, double opacity) {
		SVGRect plotRect = SVGRect.createFromReal2Range(bbox);
		if (plotRect == null) {
			plotRect = new SVGRect(new Real2(0.0, 0.0), new Real2(200., 30.));
		}
		plotRect.setFill(fill);
		plotRect.setOpacity(opacity);
		return plotRect;
	}


}
