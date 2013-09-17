package org.xmlcml.svg2xml.figure;

import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.paths.ComplexLine.LineOrientation;

public class AxisAnalyzerTest {

	private static final int XMIN = 0;
	private static final int XMAX = 500;
	private static final int XDELTA = 50;
	private static final int NXTICKS = (XMAX - XMIN)/XDELTA + 1  ;
	private static final int XTICKLENGTH = 20;
	private static final int YMIN = 0;
	private static final int YMAX = 480;
	private static final int YDELTA = 40;
	private static final int NYTICKS = (YMAX - YMIN)/YDELTA + 1  ;
	private static final int YTICKLENGTH = 10;
	
	private static final Real2 XSTART = new Real2((double)XMIN, (double)YMIN);
	private static final Real2 XEND = new Real2((double)XMAX, (double)YMIN);
	private static final Real2 YSTART = new Real2((double)YMIN, (double)XMIN);
	private static final Real2 YEND = new Real2((double)YMAX, (double)XMIN);

	@Test
	public void testSimpleAxis() {
		SVGSVG svg = new SVGSVG();
		SimpleAxis horizontalAxis = new SimpleAxis(LineOrientation.HORIZONTAL);
		horizontalAxis.setAxisMin(100.);
		horizontalAxis.setNsteps(10);
		horizontalAxis.setDelta(30.);
		horizontalAxis.setLabelFontSize(20.);
		horizontalAxis.setLabel("Horizontal Axis");
		svg.appendChild(horizontalAxis.createAxis());
		SVGUtil.debug(svg, "target/xaxis.svg", 1);
		
		svg = new SVGSVG();
		SimpleAxis verticalAxis = new SimpleAxis(LineOrientation.VERTICAL);
		verticalAxis.setAxisMin(200.);
		verticalAxis.setDelta(30);
		verticalAxis.setAxisMax(500.);
		verticalAxis.setTickLen(5.);
		verticalAxis.setTickLabelMin(5.);
		verticalAxis.setTickLabelDelta(1.);
		verticalAxis.setAxisConstant(200.);
		verticalAxis.setLabelFontSize(20.);
		verticalAxis.setLabel("Vertical Axis");
		verticalAxis.setRotateLabel(true);
		verticalAxis.setRotateVerticalCharacters(true);
		svg.appendChild(verticalAxis.createAxis());
		SVGUtil.debug(svg, "target/yaxis.svg", 1);
		
		svg = new SVGSVG();
		svg.appendChild(verticalAxis.createAxis());
		svg.appendChild(horizontalAxis.createAxis());
		SVGUtil.debug(svg, "target/xyaxis.svg", 1);
	}

}
