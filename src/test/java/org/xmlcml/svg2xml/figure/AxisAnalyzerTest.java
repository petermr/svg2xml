package org.xmlcml.svg2xml.figure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.axisold.Axis;
import org.xmlcml.svg2xml.axisold.AxisAnalyzer;
import org.xmlcml.svg2xml.axisold.GraphPlotBox;
import org.xmlcml.svg2xml.text.TextStructurer;

public class AxisAnalyzerTest {

	private static final Logger LOG = Logger.getLogger(AxisAnalyzerTest.class);

	@Test
	public void testAxes() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.5);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		List<Axis> horizontalAxisList = axisAnalyzer.getHorizontalAxisList();
		Assert.assertEquals(1, horizontalAxisList.size());
		List<Axis> verticalAxisList = axisAnalyzer.getVerticalAxisList();
		Assert.assertEquals(1, verticalAxisList.size());
	}
	
	@Test
	/** the horizontal axis is not quite horizontal.
	 * 
	 */
	public void testAxesSmallEpsilon() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.01);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		List<Axis> horizontalAxisList = axisAnalyzer.getHorizontalAxisList();
		Assert.assertEquals(0, horizontalAxisList.size());
		List<Axis> verticalAxisList = axisAnalyzer.getVerticalAxisList();
		Assert.assertEquals(1, verticalAxisList.size());
	}
	
	@Test
	public void testPlotBoxLinePlots() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.5);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		GraphPlotBox plotBox = axisAnalyzer.getPlotBox();
		Assert.assertNotNull(plotBox);
		LOG.debug("plotBox "+plotBox);
	}
	
	@Test
	public void testPlotScatterplot() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOT_7_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.1);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		GraphPlotBox plotBox = axisAnalyzer.getPlotBox();
		Assert.assertNotNull(plotBox);
		LOG.debug("plotBox "+plotBox);
	}
	
	
	@Test
	public void testPlotScatterplotFive() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOT_FIVE_7_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.1);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		List<Axis> horizontalAxisList = axisAnalyzer.getHorizontalAxisList();
		Assert.assertEquals(5, horizontalAxisList.size());
		List<Axis> verticalAxisList = axisAnalyzer.getVerticalAxisList();
		Assert.assertEquals(5, verticalAxisList.size());
		List<GraphPlotBox> plotBoxList = axisAnalyzer.getPlotBoxList();
		Assert.assertNotNull(plotBoxList);
		Assert.assertEquals("plotBoxList ", 5, plotBoxList.size());
		
		SVGG gg = new SVGG();
		for (GraphPlotBox plotBox : plotBoxList) {
			gg.appendChild(plotBox.drawBox());
		}
		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/plotBoxe5.svg"));
	}
	
	@Test
	public void testScatterplotHorizontalText() throws FileNotFoundException {
		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOT_7_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.1);
		Axis horizontalAxis = axisAnalyzer.getHorizontalAxis();
		Assert.assertNotNull(horizontalAxis);
		List<SVGText> textList = horizontalAxis.extractText(g);
		Assert.assertEquals("characters", 48, textList.size());
		TextStructurer textStructurer = new TextStructurer(textList);
		HtmlElement htmlElement = textStructurer.createHtmlElement();
		SVGUtil.debug(htmlElement, new FileOutputStream("target/horizontalAxis.html"), 1);
		Assert.assertEquals("horizontal", "<div xmlns=\"http://www.w3.org/1999/xhtml\">0 .00   0.01   0.02   0.03   0.04   0.05   <p />" +
				"<b>dN of </b> <b><i>EF-1 </i></b> Δ  null </div>", htmlElement.toXML());
	}
	
	@Test
	public void testScatterplotVerticalText() throws FileNotFoundException {
		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOT_7_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.1);
		Axis verticalAxis = axisAnalyzer.getVerticalAxis();
		Assert.assertNotNull(verticalAxis);
		List<SVGText> textList = verticalAxis.extractText(g);
		Assert.assertEquals("characters", 42, textList.size());
		TextStructurer textStructurer = new TextStructurer(textList);
		HtmlElement htmlElement = textStructurer.createHtmlElement();
		SVGUtil.debug(htmlElement, new FileOutputStream("target/verticalAxis.html"), 1);
		// this is the vertical text
		Assert.assertEquals("horizontal", "<div xmlns=\"http://www.w3.org/1999/xhtml\">0 .04   <sup>0.03  </sup> <p /><b><i> </i></b> <b><i>b</i></b> <b>" +
				"<i>u</i></b> <b><i>t</i></b> <b><i>-</i></b>Ε <p /><b> </b> <b>f</b> <sup>0.02  </sup> <p />" +
				"<b>o</b> <b> </b> <b>N</b> <b>d</b> 0.01   0.00   </div>", htmlElement.toXML());
	}
	
	@Test
	@Ignore // needs reanalysing
	public void testLinePlotsHorizontalText() throws FileNotFoundException {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.1);
		Axis horizontalAxis = axisAnalyzer.getHorizontalAxis();
		Assert.assertNotNull(horizontalAxis);
		List<SVGText> textList = horizontalAxis.extractText(g);
		Assert.assertEquals("characters", 48, textList.size());
		TextStructurer textStructurer = new TextStructurer(textList);
		HtmlElement htmlElement = textStructurer.createHtmlElement();
		SVGUtil.debug(htmlElement, new FileOutputStream("target/horizontalAxis.html"), 1);
		Assert.assertEquals("horizontal", "<div xmlns=\"http://www.w3.org/1999/xhtml\">0 .00   0.01   0.02   0.03   0.04   0.05   <p />" +
				"<b>dN of </b> <b><i>EF-1 </i></b> Δ  null </div>", htmlElement.toXML());
	}
	
	@Test
	public void testLinePlotsVerticalText() throws FileNotFoundException {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.1);
		Axis verticalAxis = axisAnalyzer.getVerticalAxis();
		Assert.assertNotNull(verticalAxis);
		List<SVGText> textList = verticalAxis.extractText(g);
		Assert.assertEquals("characters", 44, textList.size());
		TextStructurer textStructurer = new TextStructurer(textList);
		HtmlElement htmlElement = textStructurer.createHtmlElement();
		SVGUtil.debug(htmlElement, new FileOutputStream("target/verticalAxis.html"), 1);
		// this is the vertical text
		Assert.assertEquals("horizontal", "<div xmlns=\"http://www.w3.org/1999/xhtml\">6 0 50 d e s i l i t r 40 e f   s g g e 30  f o   r e b 20 m u n   l a t 10 o T 0 </div>", htmlElement.toXML());
	}
	
}
