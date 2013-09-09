package org.xmlcml.svg2xml.figure;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.PathAnalyzer;
import org.xmlcml.svg2xml.paths.Path2SVGInterpreter;

public class Path2SVGInterpreterTest {

	@Test
	public void nopathTest() {
		SVGElement svgElement = SVGElement.readAndCreateSVG(Fixtures.FIGURE_NOPATH_SVG);
		PathAnalyzer pathAnalyzer = new PathAnalyzer(svgElement, (PageAnalyzer)null);
		pathAnalyzer.interpretAsSVG();
		Path2SVGInterpreter path2svgInterpreter = pathAnalyzer.getPath2SVGInterpreter();
		SVGElement newElement = path2svgInterpreter.getNewSVGElement();
		Assert.assertNull("no paths", newElement);
		Assert.assertEquals("rect", 
				"<svg xmlns=\"http://www.w3.org/2000/svg\">\n" +
				"  <text stroke=\"none\" font-size=\"1.0\" x=\"10.\" y=\"20.\">no paths</text>\n" +
				"</svg>" +
				"",
				svgElement.toXML());
	}
	
	@Test
	public void rectangularLineTest() {
		SVGElement svgElement = SVGElement.readAndCreateSVG(Fixtures.FIGURE_RECT_LINE_SVG);
		PathAnalyzer pathAnalyzer = new PathAnalyzer(svgElement, (PageAnalyzer)null);
		pathAnalyzer.interpretAsSVG();
		Path2SVGInterpreter path2svgInterpreter = pathAnalyzer.getPath2SVGInterpreter();
		SVGElement newElement = path2svgInterpreter.getNewSVGElement();
		Assert.assertEquals("rect", 
				"<rect stroke=\"black\" stroke-width=\"1.0\" fill=\"none\" x=\"42.52\" y=\"144.433\" height=\"3.005\" width=\"520.044\" id=\"rect0\" />", 
				newElement.toXML());
	}
	
	@Test
	public void rectangularLineTextTest() {
		SVGElement svgElement = SVGElement.readAndCreateSVG(Fixtures.FIGURE_TEXT_LINE_SVG);
		PathAnalyzer pathAnalyzer = new PathAnalyzer(svgElement, (PageAnalyzer)null);
		pathAnalyzer.interpretAsSVG();
		Path2SVGInterpreter path2svgInterpreter = pathAnalyzer.getPath2SVGInterpreter();
		SVGElement newElement = path2svgInterpreter.getNewSVGElement();
		Assert.assertEquals("rect", 
				"<rect stroke=\"black\" stroke-width=\"1.0\" fill=\"none\" x=\"42.52\" y=\"144.433\" height=\"3.005\" width=\"520.044\" id=\"rect0\" />", 
				newElement.toXML());
		Assert.assertEquals("rect", 
				"<svg xmlns=\"http://www.w3.org/2000/svg\">\n" +
				"   <text stroke=\"none\" font-size=\"1.0\" x=\"10.\" y=\"20.\">text</text>\n" +
				"   <rect stroke=\"black\" stroke-width=\"1.0\" fill=\"none\" x=\"42.52\" y=\"144.433\" height=\"3.005\" width=\"520.044\" id=\"rect0\" />\n" + 
				"</svg>" +
				"",
				svgElement.toXML());
	}
	
}
