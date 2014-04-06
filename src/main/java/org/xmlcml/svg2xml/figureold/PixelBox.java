package org.xmlcml.svg2xml.figureold;

import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;

public class PixelBox extends Real2Range {

	public final static int UNMARKED = -2;
	public final static int UNPROCESSED = -1;
	private int clusterNumber;
	
	public PixelBox(Real2Range r2r) {
		this(r2r.getXRange(), r2r.getYRange());
	}
	
	public PixelBox(RealRange xRange, RealRange yRange) {
		this.setXRange(xRange);
		this.setYRange(yRange);
	}
	
	public int getClusterNumber() {
		return clusterNumber;
	}

	public void setClusterNumber(int number) {
		this.clusterNumber = number;
	}

}
