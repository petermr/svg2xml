package org.xmlcml.svg2xml.builder;

import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.builder.SimpleGeometryBuilder;

/** adds text functionality to Geometry Building.
 * 
 * @author pm286
 *
 */
public class GeometryBuilder extends SimpleGeometryBuilder {

	public GeometryBuilder() {
		super();
	}

	public GeometryBuilder(SVGElement svgElement) {
		super(svgElement);
	}
	
}
