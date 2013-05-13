package org.xmlcml.svg2xml.text;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.euclid.IntArray;

public class TextLineContainerTest {

	private final static Logger LOG = Logger.getLogger(TextLineContainerTest.class);

	@Test
	public void testTextLineContainerRead() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLine> textLineList = textLineContainer.getTextLineList();
		Assert.assertEquals("textLineCount", 16, textLineList.size());
	}
	

	@Test
	public void testTextLineContainerFonts() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLine> textLineList = textLineContainer.getTextLineList();
		Assert.assertEquals("header", "Helvetica", textLineList.get(0).getFontFamily());
		Assert.assertEquals("header", "TimesNewRoman", textLineList.get(1).getFontFamily());
		Assert.assertEquals("header", "TimesNewRoman", textLineList.get(15).getFontFamily());
	}
	
	@Test
	public void testTextLineContainerSizes() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLine> textLineList = textLineContainer.getTextLineList();
		Assert.assertEquals("header", 10.261, textLineList.get(0).getFontSize(), 0.001);
		Assert.assertEquals("header", 9.76, textLineList.get(1).getFontSize(), 0.01);
		Assert.assertEquals("header", 9.76, textLineList.get(15).getFontSize(), 0.01);
		for (TextLine textLine: textLineList) {
			LOG.trace(textLine);
		}
	}
	
	@Test
	public void testTextLineContainerWeight() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLine> textLineList = textLineContainer.getTextLineList();
		Assert.assertTrue("header", textLineList.get(0).isBold());
		Assert.assertFalse("header", textLineList.get(1).isBold());
		Assert.assertFalse("header", textLineList.get(15).isBold());
		for (TextLine textLine: textLineList) {
			LOG.trace(textLine);
		}
	}
	
	@Test
	public void testTextLineContainerLineIsCommonestFontSize() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		Assert.assertEquals("commonest size", 9.76, textLineContainer.getCommonestFontSize().getDouble(), 0.001);
		TextLine textLine0 = textLineContainer.getTextLineList().get(0);
		Assert.assertFalse("not commonest size", textLineContainer.isCommonestFontSize(textLine0));
		TextLine textLine1 = textLineContainer.getTextLineList().get(1);
		LOG.trace("textLine1 "+textLine1);
		Assert.assertTrue("commonest Font Size", textLineContainer.isCommonestFontSize(textLine1));;
	}
	
	@Test
	public void testTextLineContainerLineIsDifferentSize() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		Assert.assertEquals("commonest size", 9.76, textLineContainer.getCommonestFontSize().getDouble(), 0.001);
		TextLine textLine0 = textLineContainer.getTextLineList().get(0);
		Assert.assertFalse("not commonestFontSize", textLineContainer.isCommonestFontSize(textLine0));
		Assert.assertTrue("line 0 different size", textLineContainer.lineIsLargerThanCommonestFontSize(textLine0));
		TextLine textLine1 = textLineContainer.getTextLineList().get(1);
		Assert.assertTrue("isCommonestFontSize", textLineContainer.isCommonestFontSize(textLine1));
		Assert.assertFalse("line 1 not different size", textLineContainer.lineIsLargerThanCommonestFontSize(textLine1));
	}
	
	@Test
	public void testTextLineContainerLineIsBold() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		TextLine textLine0 = textLineContainer.getTextLineList().get(0);
		Assert.assertTrue("line 0 bold", textLine0.isBold());
		TextLine textLine1 = textLineContainer.getTextLineList().get(1);
		Assert.assertFalse("line 1 bold", textLine1.isBold());
		
		// this should be bold but didn't trigger threshold in PDF2SVG
		textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_4_3_SVG);
		textLine0 = textLineContainer.getTextLineList().get(0);
		Assert.assertFalse("line 0 bold", textLine0.isBold());
		textLine1 = textLineContainer.getTextLineList().get(1);
		Assert.assertFalse("line 1 bold", textLine1.isBold());
	}

	@Test
	public void testTextLineContainerLineGetFontFamily() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		TextLine textLine0 = textLineContainer.getTextLineList().get(0);
		Assert.assertEquals("line 0", "Helvetica", textLine0.getFontFamily());
		TextLine textLine1 = textLineContainer.getTextLineList().get(1);
		Assert.assertEquals("line 1", "TimesNewRoman", textLine1.getFontFamily());
		
	}

	@Test //this is 
	public void testTextLineContainerSplitLineSizesNone() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_8_SVG);
		IntArray intArray = textLineContainer.splitGroupBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}
	
	@Test
		public void testTextLineContainerSplitLineSizesOne() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		IntArray intArray = textLineContainer.splitGroupBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {0};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}
	
	@Test
	public void testTextLineContainerSplitLineSizesNoneButFont() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_4_3_SVG);
		IntArray intArray = textLineContainer.splitGroupBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}
	
	
}
