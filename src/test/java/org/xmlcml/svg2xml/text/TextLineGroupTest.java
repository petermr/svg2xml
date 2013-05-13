package org.xmlcml.svg2xml.text;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.euclid.IntArray;

public class TextLineGroupTest {

	private final static Logger LOG = Logger.getLogger(TextLineGroupTest.class);
	
	@Test
	public void testTextLineContainerGroupWeight() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLineGroup> textLineGroupList = textLineContainer
				.getSeparatedTextLineGroupList();
		Assert.assertTrue("header", textLineGroupList.get(0).isBold());
		Assert.assertFalse("header", textLineGroupList.get(1).isBold());
		Assert.assertFalse("header", textLineGroupList.get(15).isBold());
		for (TextLineGroup textLineGroup : textLineGroupList) {
			LOG.trace(textLineGroup);
		}
	}

	@Test
	public void testTextLineContainerLineIsCommonestFontSize() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		Assert.assertEquals("commonest size", 9.76, textLineContainer
				.getCommonestFontSize().getDouble(), 0.001);
		TextLineGroup textLineGroup0 = textLineContainer.getSeparatedTextLineGroupList().get(0);
		Assert.assertFalse("not commonest size",
				textLineContainer.isCommonestFontSize(textLineGroup0));
		TextLineGroup textLineGroup1 = textLineContainer.getSeparatedTextLineGroupList().get(1);
		LOG.trace("textLine1 " + textLineGroup1);
		Assert.assertTrue("commonest Font Size",
				textLineContainer.isCommonestFontSize(textLineGroup1));
		;
	}

	@Test
	public void testTextLineContainerLineIsDifferentSize() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		Assert.assertEquals("commonest size", 9.76, textLineContainer
				.getCommonestFontSize().getDouble(), 0.001);
		TextLine textLine0 = textLineContainer.getTextLineList().get(0);
		Assert.assertFalse("not commonestFontSize",
				textLineContainer.isCommonestFontSize(textLine0));
		Assert.assertTrue("line 0 different size",
				textLineContainer.lineIsLargerThanCommonestFontSize(textLine0));
		TextLine textLine1 = textLineContainer.getTextLineList().get(1);
		Assert.assertTrue("isCommonestFontSize",
				textLineContainer.isCommonestFontSize(textLine1));
		Assert.assertFalse("line 1 not different size",
				textLineContainer.lineIsLargerThanCommonestFontSize(textLine1));
	}

	@Test
	public void testTextLineContainerSplitLineSizesNone() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_8_SVG);
		IntArray intArray = textLineContainer.splitGroupBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}

	@Test
	public void testTextLineContainerSplitLineSizesOne() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		IntArray intArray = textLineContainer.splitBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = { 0 };
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}

	@Test
	public void testTextLineContainerSplitLineSizesNoneButFont() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_4_3_SVG);
		IntArray intArray = textLineContainer.splitBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}

	@Test
	public void testTextLineContainerSplitLineGroupSizesNoneButFont() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_4_3_SVG);
		IntArray intArray = textLineContainer.splitBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}

	@Test
	public void testTextLineContainerSplit() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		int[] ref = {0};
		IntArray intArray = new IntArray(ref);
		List <TextLineContainer> textLineContainerList =textLineContainer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 2, textLineContainerList.size());
		LOG.trace("E0 "+textLineContainerList.get(0));
		LOG.trace("E1 "+textLineContainerList.get(1));
	}

	@Test
	public void testTextLineContainerSplit6_4_0_1() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {0,1};
		IntArray intArray = new IntArray(ref);
		List <TextLineContainer> textLineContainerList =textLineContainer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 3, textLineContainerList.size());
		Assert.assertEquals("T0 ", "TextLineContainer: 1chars: 10 Y: 489.92 fontSize: 10.261 physicalStyle: null >>Discussion\n", 
				textLineContainerList.get(0).toString());
		Assert.assertEquals("T1 ", "TextLineContainer: 1chars: 47 Y: 501.882 fontSize: 9.763 physicalStyle: null >>Thecurrentmodelofholinholeformationhypothesizes\n",
				textLineContainerList.get(1).toString());
		Assert.assertEquals("T2 ", "TextLineContainer: 1chars: 45 Y: 513.842 fontSize: 9.763 physicalStyle: null >>thatλphagelysistimingismainlydeterminedbywhen\n",
				textLineContainerList.get(2).toString());
	}


	@Test
	public void testTextLineContainerSplit6_4_0() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {0};
		IntArray intArray = new IntArray(ref);
		List <TextLineContainer> textLineContainerList =textLineContainer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 2, textLineContainerList.size());
		Assert.assertEquals("T0 ", "TextLineContainer: 1chars: 10 Y: 489.92 fontSize: 10.261 physicalStyle: null >>Discussion\n", 
				textLineContainerList.get(0).toString());
		Assert.assertEquals("T1 ", 
				"TextLineContainer: 2chars: 47 Y: 501.882 fontSize: 9.763 physicalStyle: null >>Thecurrentmodelofholinholeformationhypothesizes\n" +
				"chars: 45 Y: 513.842 fontSize: 9.763 physicalStyle: null >>thatλphagelysistimingismainlydeterminedbywhen\n", 
				textLineContainerList.get(1).toString());
	}

	@Test
	public void testTextLineContainerSplit6_4_1() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {1};
		IntArray intArray = new IntArray(ref);
		List <TextLineContainer> textLineContainerList =textLineContainer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 2, textLineContainerList.size());
		Assert.assertEquals("T0 ", "TextLineContainer: 2chars: 10 Y: 489.92 fontSize: 10.261 physicalStyle: null >>Discussion\n" +
				"chars: 47 Y: 501.882 fontSize: 9.763 physicalStyle: null >>Thecurrentmodelofholinholeformationhypothesizes\n",
				textLineContainerList.get(0).toString());
		Assert.assertEquals("T1 ", 
				"TextLineContainer: 1chars: 45 Y: 513.842 fontSize: 9.763 physicalStyle: null >>thatλphagelysistimingismainlydeterminedbywhen\n",
				textLineContainerList.get(1).toString());
	}

	@Test
	public void testTextLineContainerSplit6_4_2() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {2};
		IntArray intArray = new IntArray(ref);
		List <TextLineContainer> textLineContainerList =textLineContainer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 2, textLineContainerList.size());
		Assert.assertEquals("T0 ", "TextLineContainer: 3chars: 10 Y: 489.92 fontSize: 10.261 physicalStyle: null >>Discussion\nchars: 47 Y: 501.882 fontSize: 9.763 physicalStyle: null >>Thecurrentmodelofholinholeformationhypothesizes\nchars: 45 Y: 513.842 fontSize: 9.763 physicalStyle: null >>thatλphagelysistimingismainlydeterminedbywhen\n",
				textLineContainerList.get(0).toString());
		Assert.assertEquals("T1 ", 
				"null",
				textLineContainerList.get(1).toString());
	}

	@Test
	public void testTextLineContainerSplit6_4_3() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {3};
		IntArray intArray = new IntArray(ref);
		try {
			textLineContainer.splitLineGroupsAfter(intArray);
			Assert.fail("should fail");
		} catch (RuntimeException e) {
			
		}
	}


	@Test
	public void testTextLineContainerSplit5_2_subAndSuper() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_5_2_SVG);
		int[] ref = {1,2,4,5};
		IntArray intArray = new IntArray(ref);
		List <TextLineContainer> textLineContainerList =textLineContainer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 5, textLineContainerList.size());
		Assert.assertEquals("L0_1 ", 
				"TextLineContainer: 2chars: 41 Y: 95.394 fontSize: 9.763 physicalStyle: null >>betweenMLTandanothercommonlyusedmeasureof\n" +
				"chars: 52 Y: 107.354 fontSize: 9.763 physicalStyle: null >>stochasticity,thecoefficientofvariation(CV,definedas\n",
				textLineContainerList.get(0).toString());
		Assert.assertEquals("L2 ", 
				"TextLineContainer: 2chars: 44 Y: 119.314 fontSize: 9.763 physicalStyle: null >>SD/MLT;[15,25,48,49])(F=1.50,p=0.2445),indi-\n" +
				"chars: 6 Y: 120.962 fontSize: 6.903 physicalStyle: null >>[1,12]\n",
				textLineContainerList.get(1).toString());
		Assert.assertEquals("L3_4 ", 
				"TextLineContainer: 2chars: 45 Y: 131.278 fontSize: 9.763 physicalStyle: null >>catingaproportionalincreaseoftheSDwiththeMLT.\n" +
				"chars: 51 Y: 143.238 fontSize: 9.763 physicalStyle: null >>Figure3Aalsorevealsarelativelyscatteredrelationship\n",
				textLineContainerList.get(2).toString());
		Assert.assertEquals("L5 ", 
				"TextLineContainer: 2chars: 1 Y: 150.839 fontSize: 6.903 physicalStyle: null >>2\n" +
				"chars: 41 Y: 155.199 fontSize: 9.763 physicalStyle: null >>betweentheMLTsandtheSDs(adjustedR=0.363),\n",
				textLineContainerList.get(3).toString());
		Assert.assertEquals("L6_end ", 
				"TextLineContainer: 9chars: 45 Y: 167.106 fontSize: 9.763 physicalStyle: null >>withseveralinstancesinwhichstrainswithsimilar\n" +
				"chars: 45 Y: 179.067 fontSize: 9.763 physicalStyle: null >>MLTsareaccompaniedbyverydifferentSDs.Forexam-\n" +
				"chars: 43 Y: 191.027 fontSize: 9.763 physicalStyle: null >>ple,themeanlysistimesforIN56andIN71were65.1\n" +
				"chars: 44 Y: 202.987 fontSize: 9.763 physicalStyle: null >>and68.8min,buttheSDswere3.2and7.7min,respec-\n" +
				"chars: 50 Y: 214.947 fontSize: 9.763 physicalStyle: null >>tively.Apparentlytheobservedpositiverelationshipis\n" +
				"chars: 47 Y: 226.907 fontSize: 9.763 physicalStyle: null >>onlyageneraltrend,notanabsolute.Thescatteringof\n" +
				"chars: 49 Y: 238.867 fontSize: 9.763 physicalStyle: null >>theplotalsosuggeststhatdifferentmissensemutations\n" +
				"chars: 43 Y: 250.827 fontSize: 9.763 physicalStyle: null >>intheholinsequencecaninfluenceMLTandSDsome-\n" +
				"chars: 18 Y: 262.729 fontSize: 9.763 physicalStyle: null >>whatindependently.\n",
				textLineContainerList.get(4).toString());
	}

	
	@Test
	public void testTextLineContainerSplitBoldHeader_1_6() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLineContainer> splitList = textLineContainer.splitBoldHeader();
		Assert.assertEquals("split", 2, splitList.size());
		Assert.assertEquals("split0", 
				"TextLineContainer: 1chars: 10 Y: 464.578 fontSize: 10.261 physicalStyle: null >>Background\n", 
				splitList.get(0).toString());
		TextLineContainer split1 = splitList.get(1);
		List<TextLineGroup> split1GroupList = split1.getSeparatedTextLineGroupList();
		Assert.assertEquals("split1", 15, split1GroupList.size());
		Assert.assertEquals("split1.0", 
				"chars: 45 Y: 476.483 fontSize: 9.763 physicalStyle: null >>Somephenotypicvariationarisesfromrandomnessin\n----\n", 
				split1GroupList.get(0).toString());
	}

	@Test
	/** this contains bold headers of two sizes
	 * 
	 */
	public void testTextLineContainerSplitBoldHeader_9_3() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_9_3_SVG);
		List<TextLineContainer> splitList = textLineContainer.splitBoldHeader();
		Assert.assertEquals("split", 2, splitList.size());
		List<TextLineGroup> split0GroupList = splitList.get(0).getSeparatedTextLineGroupList();
		Assert.assertEquals("split0.0", 
				"chars: 7 Y: 489.92 fontSize: 10.261 physicalStyle: null >>Methods\n----\n", 
				split0GroupList.get(0).toString());
		Assert.assertEquals("split0.1", 
				"chars: 16 Y: 501.882 fontSize: 9.165 physicalStyle: null >>Bacterialstrains\n----\n", 
				split0GroupList.get(1).toString());
		TextLineContainer split1 = splitList.get(1);
		List<TextLineGroup> split1GroupList = split1.getSeparatedTextLineGroupList();
		Assert.assertEquals("split1", 3, split1GroupList.size());
		Assert.assertEquals("split1.0", 
				"chars: 50 Y: 513.844 fontSize: 9.763 physicalStyle: null >>Allbacteriaandphagestrainsusedinthisstudyarelisted\n" +
				"----\n",
				split1GroupList.get(0).toString());
		Assert.assertEquals("split1.1", 
				"chars: 41 Y: 525.804 fontSize: 9.763 physicalStyle: null >>inTable3.Thecopynumberofλgenomewaschecked\n" +
				"----\n",
				split1GroupList.get(1).toString());
		Assert.assertEquals("split1.2", 
				"chars: 41 Y: 537.764 fontSize: 9.763 physicalStyle: null >>byPCRfollowingthemethodofPowelletal.[64].\n" +
				"----\n",
				split1GroupList.get(2).toString());
	}

	@Test
	/** this contains bold headers of two sizes
	 * 
	 */
	public void testTextLineContainerSplitBoldHeader_9_3_fontSplitter() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_9_3_SVG);
		List<TextLineContainer> splitList = textLineContainer.splitBoldHeader();
		TextLineContainer header = splitList.get(0);
		LOG.trace(header);
		IntArray splitter = header.getSplitArrayForFontSizeChange();
		Assert.assertEquals("splitter", "(0)", splitter.toString());
	}

	@Test
	/** this contains bold headers of two sizes
	 * 
	 */
	public void testTextLineContainerSplitBoldHeader_9_3_fontSize() {
		TextLineContainer textLineContainer = TextLineContainer
				.createTextLineContainerWithSortedLines(TextFixtures.BMC_174_9_3_SVG);
		List<TextLineContainer> splitList = textLineContainer.splitBoldHeader();
		TextLineContainer header = splitList.get(0);
		LOG.trace(header);
		List<TextLineContainer> headers = header.splitOnFontSizeChange();
		Assert.assertEquals("headers", 2, headers.size());
		List<TextLineGroup> header0 = headers.get(0).getSeparatedTextLineGroupList();
		Assert.assertEquals("header0", 
				"[chars: 7 Y: 489.92 fontSize: 10.261 physicalStyle: null >>Methods\n----\n]", 
				header0.toString());
		List<TextLineGroup> header1 = headers.get(1).getSeparatedTextLineGroupList();
		Assert.assertEquals("header1", 
				"[chars: 16 Y: 501.882 fontSize: 9.165 physicalStyle: null >>Bacterialstrains\n----\n]", 
				header1.toString());
	}

}
