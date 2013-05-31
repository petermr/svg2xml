package org.xmlcml.svg2xml.misc;

import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;


public class LAPDFChunk extends LAPDFElement {

	public final static String TAG = "Chunk";
	
	public LAPDFChunk() {
		super(TAG);
	}
	
	protected SVGElement createSVGElement() {
		SVGElement svg = new SVGG();
		return svg;
	}

}
