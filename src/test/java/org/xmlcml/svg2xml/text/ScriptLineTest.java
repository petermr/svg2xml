package org.xmlcml.svg2xml.text;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.container.ScriptContainer;

public class ScriptLineTest {

	@Test
	public void testFixtures() {
		TextStructurer textContainer = null;
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_0SA_SVG);
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_3SA_SVG);
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_4SA_SVG);
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_4SB_SVG);
		
	}
	
}
