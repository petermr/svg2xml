package org.xmlcml.svg2xml.text;

import java.util.List;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.euclid.IntArray;

public class TextLineGroupTest {

	private static final String TERM = ScriptLine.TERM;
	private final static Logger LOG = Logger.getLogger(TextLineGroupTest.class);
	
	@Test
	public void testTextStructurerGroupWeight() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<ScriptLine> textLineGroupList = textStructurer.getScriptedLineList();
		Assert.assertTrue("header", textLineGroupList.get(0).isBold());
		Assert.assertFalse("header", textLineGroupList.get(1).isBold());
		Assert.assertFalse("header", textLineGroupList.get(15).isBold());
		for (ScriptLine textLineGroup : textLineGroupList) {
			LOG.trace(textLineGroup);
		}
	}

	@Test
	public void testTextStructurerLineIsCommonestFontSize() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		Assert.assertEquals("commonest size", 9.76, textStructurer
				.getCommonestFontSize().getDouble(), 0.001);
		ScriptLine textLineGroup0 = textStructurer.getScriptedLineList().get(0);
		Assert.assertFalse("not commonest size",
				textStructurer.isCommonestFontSize(textLineGroup0));
		ScriptLine textLineGroup1 = textStructurer.getScriptedLineList().get(1);
		LOG.trace("textLine1 " + textLineGroup1);
		Assert.assertTrue("commonest Font Size",
				textStructurer.isCommonestFontSize(textLineGroup1));
		;
	}

	@Test
	public void testTextStructurerLineIsDifferentSize() {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		Assert.assertEquals("commonest size", 9.76, textStructurer
				.getCommonestFontSize().getDouble(), 0.001);
		TextLine textLine0 = textStructurer.getTextLineList().get(0);
		Assert.assertFalse("not commonestFontSize",
				textStructurer.isCommonestFontSize(textLine0));
		Assert.assertTrue("line 0 different size",
				textStructurer.lineIsLargerThanCommonestFontSize(textLine0));
		TextLine textLine1 = textStructurer.getTextLineList().get(1);
		Assert.assertTrue("isCommonestFontSize",
				textStructurer.isCommonestFontSize(textLine1));
		Assert.assertFalse("line 1 not different size",
				textStructurer.lineIsLargerThanCommonestFontSize(textLine1));
	}

	@Test
	public void testTextStructurerSplitLineSizesNone() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_8_SVG);
		IntArray intArray = textStructurer.splitGroupBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}

	@Test
	public void testTextStructurerSplitLineSizesOne() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		IntArray intArray = textStructurer.splitBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = { 0 };
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}

	@Test
	public void testTextStructurerSplitLineSizesNoneButFont() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_4_3_SVG);
		IntArray intArray = textStructurer.splitBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}

	@Test
	public void testTextStructurerSplitLineGroupSizesNoneButFont() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_4_3_SVG);
		IntArray intArray = textStructurer.splitBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}

	@Test
	public void testTextStructurerSplit() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		int[] ref = {0};
		IntArray intArray = new IntArray(ref);
		List <TextStructurer> textStructurerList = textStructurer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 2, textStructurerList.size());
		LOG.trace("E0 "+textStructurerList.get(0));
		LOG.trace("E1 "+textStructurerList.get(1));
	}

	@Test
	public void testTextStructurerSplit6_4_0_1() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {0,1};
		IntArray intArray = new IntArray(ref);
		List <TextStructurer> textStructurerList = textStructurer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 3, textStructurerList.size());
		Assert.assertEquals("T0 ", "TextStructurer: 1chars: 10 Y: 489.92 fontSize: 10.261 >>Discussion\n", 
				textStructurerList.get(0).toString());
		Assert.assertEquals("T1 ", "TextStructurer: 1chars: 47 Y: 501.882 fontSize: 9.763 >>Thecurrentmodelofholinholeformationhypothesizes\n",
				textStructurerList.get(1).toString());
		Assert.assertEquals("T2 ", "TextStructurer: 1chars: 45 Y: 513.842 fontSize: 9.763 >>thatλphagelysistimingismainlydeterminedbywhen\n",
				textStructurerList.get(2).toString());
	}


	@Test
	public void testTextStructurerSplit6_4_0() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {0};
		IntArray intArray = new IntArray(ref);
		List <TextStructurer> TextStructurerList = textStructurer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 2, TextStructurerList.size());
		Assert.assertEquals("T0 ", "TextStructurer: 1chars: 10 Y: 489.92 fontSize: 10.261 >>Discussion\n", 
				TextStructurerList.get(0).toString());
		Assert.assertEquals("T1 ", 
				"TextStructurer: 2chars: 47 Y: 501.882 fontSize: 9.763 >>Thecurrentmodelofholinholeformationhypothesizes\n" +
				"chars: 45 Y: 513.842 fontSize: 9.763 >>thatλphagelysistimingismainlydeterminedbywhen\n", 
				TextStructurerList.get(1).toString());
	}

	@Test
	public void testTextStructurerSplit6_4_1() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {1};
		IntArray intArray = new IntArray(ref);
		List <TextStructurer> TextStructurerList = textStructurer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 2, TextStructurerList.size());
		Assert.assertEquals("T0 ", "TextStructurer: 2chars: 10 Y: 489.92 fontSize: 10.261 >>Discussion\n" +
				"chars: 47 Y: 501.882 fontSize: 9.763 >>Thecurrentmodelofholinholeformationhypothesizes\n",
				TextStructurerList.get(0).toString());
		Assert.assertEquals("T1 ", 
				"TextStructurer: 1chars: 45 Y: 513.842 fontSize: 9.763 >>thatλphagelysistimingismainlydeterminedbywhen\n",
				TextStructurerList.get(1).toString());
	}

	@Test
	public void testTextStructurerSplit6_4_2() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {2};
		IntArray intArray = new IntArray(ref);
		List <TextStructurer> TextStructurerList = textStructurer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 2, TextStructurerList.size());
		Assert.assertEquals("T0 ", "TextStructurer: 3chars: 10 Y: 489.92 fontSize: 10.261 >>Discussion\nchars: 47 Y: 501.882 fontSize: 9.763 >>Thecurrentmodelofholinholeformationhypothesizes\nchars: 45 Y: 513.842 fontSize: 9.763 >>thatλphagelysistimingismainlydeterminedbywhen\n",
				TextStructurerList.get(0).toString());
		Assert.assertEquals("T1 ", 
				"null",
				TextStructurerList.get(1).toString());
	}

	@Test
	public void testTextStructurerSplit6_4_3() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_6_4_SVG);
		int[] ref = {3};
		IntArray intArray = new IntArray(ref);
		try {
			textStructurer.splitLineGroupsAfter(intArray);
			Assert.fail("should fail");
		} catch (RuntimeException e) {
			
		}
	}


	@Test
	public void testTextStructurerSplit5_2_subAndSuper() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_5_2_SVG);
		int[] ref = {1,2,4,5};
		IntArray intArray = new IntArray(ref);
		List <TextStructurer> textStructurerList = textStructurer.splitLineGroupsAfter(intArray);
		Assert.assertEquals("split after 0", 5, textStructurerList.size());
		Assert.assertEquals("L0_1 ", 
				"TextStructurer: 2chars: 41 Y: 95.394 fontSize: 9.763 >>betweenMLTandanothercommonlyusedmeasureof\n" +
				"chars: 52 Y: 107.354 fontSize: 9.763 >>stochasticity,thecoefficientofvariation(CV,definedas\n",
				textStructurerList.get(0).toString());
		Assert.assertEquals("L2 ", 
				"TextStructurer: 2chars: 44 Y: 119.314 fontSize: 9.763 >>SD/MLT;[15,25,48,49])(F=1.50,p=0.2445),indi-\n" +
				"chars: 6 Y: 120.962 fontSize: 6.903 >>[1,12]\n",
				textStructurerList.get(1).toString());
		Assert.assertEquals("L3_4 ", 
				"TextStructurer: 2chars: 45 Y: 131.278 fontSize: 9.763 >>catingaproportionalincreaseoftheSDwiththeMLT.\n" +
				"chars: 51 Y: 143.238 fontSize: 9.763 >>Figure3Aalsorevealsarelativelyscatteredrelationship\n",
				textStructurerList.get(2).toString());
		Assert.assertEquals("L5 ", 
				"TextStructurer: 2chars: 1 Y: 150.839 fontSize: 6.903 >>2\n" +
				"chars: 41 Y: 155.199 fontSize: 9.763 >>betweentheMLTsandtheSDs(adjustedR=0.363),\n",
				textStructurerList.get(3).toString());
		Assert.assertEquals("L6_end ", 
				"TextStructurer: 9chars: 45 Y: 167.106 fontSize: 9.763 >>withseveralinstancesinwhichstrainswithsimilar\n" +
				"chars: 45 Y: 179.067 fontSize: 9.763 >>MLTsareaccompaniedbyverydifferentSDs.Forexam-\n" +
				"chars: 43 Y: 191.027 fontSize: 9.763 >>ple,themeanlysistimesforIN56andIN71were65.1\n" +
				"chars: 44 Y: 202.987 fontSize: 9.763 >>and68.8min,buttheSDswere3.2and7.7min,respec-\n" +
				"chars: 50 Y: 214.947 fontSize: 9.763 >>tively.Apparentlytheobservedpositiverelationshipis\n" +
				"chars: 47 Y: 226.907 fontSize: 9.763 >>onlyageneraltrend,notanabsolute.Thescatteringof\n" +
				"chars: 49 Y: 238.867 fontSize: 9.763 >>theplotalsosuggeststhatdifferentmissensemutations\n" +
				"chars: 43 Y: 250.827 fontSize: 9.763 >>intheholinsequencecaninfluenceMLTandSDsome-\n" +
				"chars: 18 Y: 262.729 fontSize: 9.763 >>whatindependently.\n",
				textStructurerList.get(4).toString());
	}

	
	@Test
	public void testTextStructurerSplitBoldHeader_1_6() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextStructurer> splitList = textStructurer.splitOnFontBoldChange(0);
		Assert.assertEquals("split", 2, splitList.size());
		Assert.assertEquals("split0", 
				"TextStructurer: 1chars: 10 Y: 464.578 fontSize: 10.261 >>Background\n", 
				splitList.get(0).toString());
		TextStructurer split1 = splitList.get(1);
		List<ScriptLine> split1GroupList = split1.getScriptedLineList();
		Assert.assertEquals("split1", 15, split1GroupList.size());
		Assert.assertEquals("split1.0", 
				"Somephenotypicvariationarisesfromrandomnessin  %%%%\n", 
				split1GroupList.get(0).toString());
	}

	@Test
	/** this contains bold headers of two sizes
	 * 
	 */
	public void testTextStructurerSplitBoldHeader_9_3() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_9_3_SVG);
		List<TextStructurer> splitList = textStructurer.splitOnFontBoldChange(0);
		Assert.assertEquals("split", 2, splitList.size());
		List<ScriptLine> split0GroupList = splitList.get(0).getScriptedLineList();
		Assert.assertEquals("split0.0", 
				"Methods  %%%%\n", 
				split0GroupList.get(0).toString());
		Assert.assertEquals("split0.1", 
				"Bacterialstrains  %%%%\n", 
				split0GroupList.get(1).toString());
		TextStructurer split1 = splitList.get(1);
		List<ScriptLine> split1GroupList = split1.getScriptedLineList();
		Assert.assertEquals("split1", 3, split1GroupList.size());
		Assert.assertEquals("split1.0", 
				"Allbacteriaandphagestrainsusedinthisstudyarelisted  %%%%\n",
				split1GroupList.get(0).toString());
		Assert.assertEquals("split1.1", 
				"inTable3.Thecopynumberofλgenomewaschecked  %%%%\n",
				split1GroupList.get(1).toString());
		Assert.assertEquals("split1.2", 
				"byPCRfollowingthemethodofPowelletal.[64].  %%%%\n",
				split1GroupList.get(2).toString());
	}

	@Test
	/** this contains bold headers of two sizes
	 * 
	 */
	public void testTextStructurerSplitBoldHeader_9_3_fontSplitter() {
		TextStructurer textStructurer = TextStructurer
				.createTextStructurerWithSortedLines(TextFixtures.BMC_174_9_3_SVG);
		List<TextStructurer> splitList = textStructurer.splitOnFontBoldChange(0);
		TextStructurer header = splitList.get(0);
		LOG.trace(header);
		IntArray splitter = header.getSplitArrayForFontSizeChange(0);
		Assert.assertEquals("splitter", "(0)", splitter.toString());
	}

	@Test
	/** this contains bold headers of two sizes
	 * 
	 */
	public void testTextStructurerSplitBoldHeader_9_3_fontSize() {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_9_3_SVG);
		List<TextStructurer> splitList = textStructurer.splitOnFontBoldChange(0);
		TextStructurer header = splitList.get(0);
		LOG.trace(header);
		List<TextStructurer> headers = header.splitOnFontSizeChange(0);
		Assert.assertEquals("headers", 2, headers.size());
		List<ScriptLine> header0 = headers.get(0).getScriptedLineList();
		Assert.assertEquals("header0", 
				"[Methods"+TERM+"]", 
				header0.toString());
		List<ScriptLine> header1 = headers.get(1).getScriptedLineList();
		Assert.assertEquals("header1", 
				"[Bacterialstrains  %%%%\n]", 
				header1.toString());
	}

}
