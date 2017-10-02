package org.xmlcml.svg2xml.text;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.svg2xml.SVG2XMLFixtures;
import org.xmlcml.svg2xml.container.ScriptContainerTest;

public class ScriptLineTest {

	private final static Logger LOG = Logger.getLogger(ScriptLineTest.class);
	@Test
	public void testFixtures() {
		TextStructurerOLD textContainer = null;
		textContainer = TextStructurerOLD.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_0SA_SVG);
		textContainer = TextStructurerOLD.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		textContainer = TextStructurerOLD.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_3SA_SVG);
		textContainer = TextStructurerOLD.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_4SA_SVG);
		textContainer = TextStructurerOLD.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_4SB_SVG);
		
	}
	
	@Test
	public void testTextContentWithoutSpaces() {
		List<StyleSpansOLD> styleSpansList = ScriptContainerTest.getStyleSpansList(TextFixtures.BMC_312_6_0SA_SVG);
		String[] expected = {
				"Hiwatashi et al. BMC Evolutionary Biology 2011, 11:312",
				"http://www.biomedcentral.com/1471-2148/11/312",
		};
		int i = 0;
		for (StyleSpansOLD styleSpans : styleSpansList) {
			Assert.assertEquals("s"+(i), expected[i], styleSpans.getTextContentWithSpaces());
			i++;
		}
	}
	
	@Test
	public void testMissingFontSize() {
		List<StyleSpansOLD> styleSpansList = ScriptContainerTest.getStyleSpansList(new File(SVG2XMLFixtures.TEXT_DIR,  "bmc312.chunk6.0Samini.svg"));
		String[] expected = {
//				"Hiwatashi et al. BMC Evolutionary Biology 2011, 11:312",
//				"http://www.biomedcentral.com/1471-2148/11/312",
            "H .",
		};
		int i = 0;
		for (StyleSpansOLD styleSpans : styleSpansList) {
			Assert.assertEquals("s"+(i), expected[i], styleSpans.getTextContentWithSpaces());
			i++;
		}
	}
	
	@Test
	public void testAddStyleSpans() {
		List<StyleSpansOLD> styleSpansList = ScriptContainerTest.getStyleSpansList(TextFixtures.BMC_312_6_0SA_SVG);
		StyleSpansOLD testSpans = new StyleSpansOLD();
		for (StyleSpansOLD styleSpans : styleSpansList) {
			testSpans.addStyleSpans(styleSpans, true);
		}
		Assert.assertEquals("s", 
				"Hiwatashi et al. BMC Evolutionary Biology 2011, 11:312 http://www.biomedcentral.com/1471-2148/11/312", 
				testSpans.getTextContentWithSpaces());
	}
	
	
}
