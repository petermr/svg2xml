package org.xmlcml.svg2xml.paths;

import org.junit.Test;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.axisold.SVGLineAnalyzer;

public class SVGLineAnalyzerTest {

	@Test
	public void testFindAxes() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		SVGLineAnalyzer lineAnalyzer = new SVGLineAnalyzer();
		lineAnalyzer.analyzeLinesAsAxesAndWhatever(g);
		lineAnalyzer.debug();
		lineAnalyzer.getLineAngleMap();
		lineAnalyzer.getPlotBox();
	}
}
