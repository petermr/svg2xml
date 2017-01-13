package org.xmlcml.svg2xml.figure;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Test;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.linestuff.ComplexLine.LineOrientation;

public class SimpleAxisTest {

	/** creates axes and writes them.
	 * 
	 * mainly for test/education.
	 */
	@Test
	public void testSimpleAxis() throws Exception {
		SVGSVG svg = new SVGSVG();
		SimpleAxis horizontalAxis = new SimpleAxis(LineOrientation.HORIZONTAL);
		horizontalAxis.setAxisMin(100.);
		horizontalAxis.setNsteps(10);
		horizontalAxis.setDelta(30.);
		horizontalAxis.setLabelFontSize(20.);
		horizontalAxis.setLabel("Horizontal Axis");
		svg.appendChild(horizontalAxis.createAxis());
		File axes = new File("target/axes/");
		axes.mkdirs();
		SVGUtil.debug(svg, new FileOutputStream(new File(axes, "xaxis.svg")), 1);
		
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
		SVGUtil.debug(svg, "target/axes/yaxis.svg", 1);
		
		svg = new SVGSVG();
		svg.appendChild(verticalAxis.createAxis());
		svg.appendChild(horizontalAxis.createAxis());
		SVGUtil.debug(svg, "target/axes/xyaxis.svg", 1);
	}

}
