package org.xmlcml.svg2xml.misc;

import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;


public class LAPDFDocument extends LAPDFElement {

	public final static String TAG = "Document";
	
	public LAPDFDocument() {
		super(TAG);
	}
	
	protected SVGElement createSVGElement() {
		SVGElement svg = new SVGSVG();
		return svg;
	}

}
