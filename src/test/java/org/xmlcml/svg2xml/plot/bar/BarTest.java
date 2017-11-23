package org.xmlcml.svg2xml.plot.bar;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.plot.YPlotBox;
import org.xmlcml.svg2xml.SVG2XMLFixtures;

/** test bar plot
 * 
 * @author pm286
 *
 */
public class BarTest {

	@Test
	public void testBar() {
		String fileroot = "figure";
		File inputDir = new File(SVG2XMLFixtures.BAR_DIR, "nature/p3.a");
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue(""+inputFile, inputFile.exists());
		SVGElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		YPlotBox mediaBox = new YPlotBox();
		mediaBox.readAndCreateBarPlot(svgElement);
	}
}
