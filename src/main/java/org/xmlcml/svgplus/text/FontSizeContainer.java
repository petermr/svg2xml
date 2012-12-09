package org.xmlcml.svgplus.text;

import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Util;

public class FontSizeContainer {

	/** significant decimal places in font-size (appears to be set by PDF2SVG) */
	public final static int NDECIMAL = 2;
	/** comparing factor */
	public final static double EPS = 1/(Math.pow(10.0, (double) NDECIMAL)); 
	private Double size;

	public FontSizeContainer(Double size) {
		this.size = Util.format(size, NDECIMAL);
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj instanceof FontSizeContainer) {
			FontSizeContainer fs = (FontSizeContainer)obj;
			equals = Real.isEqual(this.size, fs.size, EPS);
		}
		return equals;
	}
	
	@Override
	public int hashCode() {
		return size.hashCode();
	}

	public Double getSize() {
		return size;
	}
}
