package org.xmlcml.svg2xml.text;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.svg2xml.container.ScriptContainerTest;

public class ScriptLineTest {

	private final static Logger LOG = Logger.getLogger(ScriptLineTest.class);
	@Test
	public void testFixtures() {
		TextStructurer textContainer = null;
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_0SA_SVG);
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_3SA_SVG);
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_4SA_SVG);
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_4SB_SVG);
		
	}
	
	@Test
	public void testTextContentWithoutSpaces() {
		List<StyleSpans> styleSpansList = ScriptContainerTest.getStyleSpansList(TextFixtures.BMC_312_6_0SA_SVG);
		String[] expected = {
				"Hiwatashi et al. BMC Evolutionary Biology 2011, 11:312",
				"http://www.biomedcentral.com/1471-2148/11/312",
		};
		int i = 0;
		for (StyleSpans styleSpans : styleSpansList) {
			Assert.assertEquals("s"+(i), expected[i], styleSpans.getTextContentWithSpaces());
			i++;
		}
	}
	
	@Test
	public void testAddStyleSpans() {
		List<StyleSpans> styleSpansList = ScriptContainerTest.getStyleSpansList(TextFixtures.BMC_312_6_0SA_SVG);
		StyleSpans testSpans = new StyleSpans();
		for (StyleSpans styleSpans : styleSpansList) {
			testSpans.addStyleSpans(styleSpans, true);
		}
		Assert.assertEquals("s", 
				"Hiwatashi et al. BMC Evolutionary Biology 2011, 11:312 http://www.biomedcentral.com/1471-2148/11/312", 
				testSpans.getTextContentWithSpaces());
	}
	
	
}
