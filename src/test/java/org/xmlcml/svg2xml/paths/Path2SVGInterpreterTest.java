package org.xmlcml.svg2xml.paths;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.PathAnalyzer;

public class Path2SVGInterpreterTest {

	@Test
	public void nopathTest() {
		SVGElement svgElement = SVGElement.readAndCreateSVG(Fixtures.PATHS_NOPATH_SVG);
		PathAnalyzer pathAnalyzer = new PathAnalyzer(svgElement, (PageAnalyzer)null);
		pathAnalyzer.convertPathsToSVG();
		List<SVGElement> elementList = pathAnalyzer.getConvertedPathList();
		Assert.assertEquals("converted", 0, elementList.size());
	}
	
	@Test
	public void rectTest() {
		SVGElement svgElement = SVGElement.readAndCreateSVG(Fixtures.PATHS_TEXT_LINE_SVG);
		PathAnalyzer pathAnalyzer = new PathAnalyzer(svgElement, (PageAnalyzer)null);
		pathAnalyzer.convertPathsToSVG();
		List<SVGElement> elementList = pathAnalyzer.getConvertedPathList();
		Assert.assertEquals("converted", 1, elementList.size());
		Assert.assertEquals("rect", 
				"<rect xmlns=\"http://www.w3.org/2000/svg\" fill=\"#000000\" stroke=\"black\" stroke-width=\"0.0\" x=\"42.52\" y=\"144.433\" height=\"3.005\" width=\"520.044\" id=\"rect.0\" />", 
				elementList.get(0).toXML());
	}
	
	
	@Test
	/** The BMC Evolutionary Biology logo as paths.
	 * The small 'l' should be interpreted as a rect
	 * The small 'o' should be a circle
	 * The 'M' should be a polyline
	 * The 'E' should ultimately break into two polylines
	 * The 'u', 'm', etc have curves and so won't be changed
	 */
	public void bmcLogoTest() {
		SVGElement svgElement = SVGElement.readAndCreateSVG(Fixtures.PATHS_BMCLOGO_SVG);
		PathAnalyzer pathAnalyzer = new PathAnalyzer(svgElement, (PageAnalyzer)null);
		pathAnalyzer.convertPathsToSVG();
		List<SVGElement> elementList = pathAnalyzer.getPathContainer().getConvertedPathList();
		Assert.assertEquals("converted", 23, elementList.size());
		Assert.assertEquals("E", 
				"<polygon xmlns=\"http://www.w3.org/2000/svg\" fill=\"magenta\" stroke=\"green\" stroke-width=\"0.5\"" +
				" points=\"435.968 61.082 440.167 61.082 440.167 62.027 437.042 62.027 437.042 64.218 439.888 64.218 439.888 65.163" +
				" 437.042 65.163 437.042 67.633 440.167 67.633 440.167 68.578 435.968 68.578\" id=\"polygon.0\" />",
				elementList.get(0).toXML());
		// "v" omitted
		Assert.assertEquals("o", 
				"<circle xmlns=\"http://www.w3.org/2000/svg\" fill=\"magenta\" stroke=\"green\" stroke-width=\"0.5\"" +
				" cx=\"449.58\" cy=\"65.84\" r=\"2.665\" id=\"circle.2\" />",
				elementList.get(2).toXML());
		Assert.assertEquals("lower case l", 
				"<rect xmlns=\"http://www.w3.org/2000/svg\" fill=\"magenta\" stroke=\"green\" stroke-width=\"0.5\"" +
				" x=\"453.849\" y=\"60.523\" height=\"8.055\" width=\"1.009\" id=\"rect.3\" />",
				elementList.get(3).toXML());
		Assert.assertEquals("unconverted u", 
				"<path xmlns=\"http://www.w3.org/2000/svg\" clip-path=\"url(#clipPath1)\" fill=\"magenta\" stroke=\"green\"" +
				" stroke-width=\"0.5\" d=\"M461.548 68.578 L460.571 68.578 L460.571 67.708 L460.55 67.708" +
				" C460.249 68.331 459.519 68.707 458.756 68.707 C457.338 68.707 456.705 67.827 456.705 66.355" +
				" L456.705 63.101 L457.714 63.101 L457.714 65.936 C457.714 67.214 457.994 67.837 458.874 67.891" +
				" C460.023 67.891 460.539 66.967 460.539 65.636 L460.539 63.101 L461.548 63.101 L461.548 68.578 \" id=\"path.4\" />",
				elementList.get(4).toXML());
		Assert.assertEquals("half moon", 
				"<path xmlns=\"http://www.w3.org/2000/svg\" clip-path=\"url(#clipPath3)\" fill=\"pink\" stroke=\"purple\"" +
				" stroke-width=\"0.5\" d=\"M428.911 60.844 C425.543 55.314 425.357 48.129 429.037 42.27" +
				" C434.222 34.019 444.971 31.662 453.224 36.848 C456.212 38.725 458.352 42.501 458.352 42.501" +
				" C457.189 40.651 455.601 39.024 453.626 37.784 C446.595 33.367 437.315 35.487 432.898 42.518" +
				" C429.731 47.56 429.925 53.76 432.882 58.492 L428.911 60.844 \" id=\"path.22\" />",
				elementList.get(22).toXML());
	}
	
	@Test
	public void bmcLogoTestInSitu() throws IOException {
		SVGElement svgElement = SVGElement.readAndCreateSVG(Fixtures.PATHS_BMCLOGO_SVG);
		PathAnalyzer pathAnalyzer = new PathAnalyzer(svgElement, (PageAnalyzer)null);
		pathAnalyzer.convertPaths2SVG();
		pathAnalyzer.getSVGChunk();
		CMLUtil.debug(svgElement, new FileOutputStream("target/converted"+System.currentTimeMillis()+".svg"), 1);
		List<SVGElement> svgElements = SVGUtil.getQuerySVGElements(svgElement, "/*/*/svg:*");
		Assert.assertEquals("converted", 23, svgElements.size());
		Assert.assertTrue("0", svgElements.get(0) instanceof SVGPolygon);
		Assert.assertTrue("1", svgElements.get(1) instanceof SVGPolygon);
		Assert.assertTrue("2", svgElements.get(2) instanceof SVGCircle);
		Assert.assertTrue("3", svgElements.get(3) instanceof SVGRect);
		Assert.assertTrue("4", svgElements.get(4) instanceof SVGPath);
	}
	
}
