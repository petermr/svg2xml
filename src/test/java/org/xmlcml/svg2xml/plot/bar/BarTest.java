package org.xmlcml.svg2xml.plot.bar;

import java.io.File;

import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.plot.SVGMediaBox;
import org.xmlcml.svg2xml.SVG2XMLFixtures;

import junit.framework.Assert;

/** test bar plot
 * 
 * @author pm286
 *
 */
public class BarTest {

	@Test
	public void testBar() {
		String fileroot = "nature-page3.crop2";
		File inputFile = new File(SVG2XMLFixtures.BAR_DIR, fileroot + ".svg");
		Assert.assertTrue(""+inputFile, inputFile.exists());
		SVGElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		SVGMediaBox mediaBox = new SVGMediaBox();
		mediaBox.readAndCreateBarPlot(svgElement);
	}
}
