package org.xmlcml.svgplus.control.page;

import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.svgplus.paths.Axis;

public class PlotBox {


	private Axis horizontalAxis;
	private Axis verticalAxis;
	private RealRange horizontalRange;
	private RealRange verticalRange;
	private Real2Range boxRange;


	public PlotBox(Axis horizontalAxis, Axis verticalAxis) {
		setHorizontalAxis(horizontalAxis);
		setVerticalAxis(verticalAxis);
	}
	
	public Axis getHorizontalAxis() {
		return horizontalAxis;
	}

	public void setHorizontalAxis(Axis horizontalAxis) {
		this.horizontalAxis = horizontalAxis;
		this.horizontalRange = (horizontalAxis == null) ? null : horizontalAxis.getAxisRangeInPixels();
	}

	public Axis getVerticalAxis() {
		return verticalAxis;
	}

	public void setVerticalAxis(Axis verticalAxis) {
		this.verticalAxis = verticalAxis;
		this.verticalRange = (verticalAxis == null) ? null : verticalAxis.getAxisRangeInPixels();
	}

	SVGRect createRect() {
		SVGRect boxRect = null; 
		if (horizontalRange != null && verticalRange != null) {
			boxRange = new Real2Range(horizontalRange, verticalRange);
			boxRect = new SVGRect(boxRange);
		}
		return boxRect;
	}

	public Real2Range getBoxRange() {
		return boxRange;
	}
}
