package org.xmlcml.svg2xml.container;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.ScriptWord;
import org.xmlcml.svg2xml.text.TextFixtures;
import org.xmlcml.svg2xml.text.TextStructurer;

public class ScriptContainerTest {

	@Test
	public void test3WordContainer() {
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		Assert.assertEquals("1a", 
				"TextStructurer: 1chars: 9 Y: 39.615 fontSize: 7.97 physicalStyle: null >>Page6of14\n",
				textContainer.toString());
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(TextFixtures.BMC_312_6_1SA_SVG);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		Assert.assertEquals("1a", "Page6of14", sc.getRawValue());
	}
	
	
	@Test
	public void test4WordContainerScriptList() {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(TextFixtures.BMC_312_6_1SA_SVG);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<ScriptLine> scriptList = sc.getScriptLineList();
		Assert.assertEquals("scriptLines", 1, scriptList.size());
		Assert.assertEquals("line0", "Page6of14\n----\n", scriptList.get(0).toString());
	}

	@Test
	public void testGet4Words() {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(TextFixtures.BMC_312_6_1SA_SVG);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<ScriptLine> scriptLineList = sc.getScriptLineList();
		ScriptLine scriptLine = scriptLineList.get(0);
		List<ScriptWord> scriptWords = scriptLine.getWords();
		Assert.assertEquals("line0", 4, scriptWords.size());
		String[] value ={"Page", "6", "of", "14"};
		for (int i = 0; i < scriptWords.size(); i++) {
			Assert.assertEquals(""+i, value[i], scriptWords.get(i).getRawValue());
		}
	}

	@Test
	public void testGetTitle() {
		testScript(TextFixtures.BMC_312_6_0SA_SVG, new String[][] {
				{"Hiwatashi", "et", "al.", "BMC", "Evolutionary", "Biology", "2011,", "11:312"},
				{"http://www.biomedcentral.com/1471-2148/11/312", },
				});
	}

	/** this is not right - shouldn't split after slash */
	@Test
	public void testBadSlash() {
		testScript(TextFixtures.BMC_312_6_0SA1_SVG, new String[][] {
				{"http://www.biomedcentral.com/1471-2148/11/312" }
				});
	}

	@Test
	public void testGetShortPara() {
		testScript(TextFixtures.BMC_312_6_3SA_SVG, new String[][] {
				{"genes", "in", "the", "exons", "and", "introns", "in", "these", "individuals", "was"},
				{"essentially", "the", "same", "as", "the", "pattern", "shown", "in", "Figure", "1."}
		});
	}

	@Test
	public void testGetShortHeading0() {
		testScript(TextFixtures.BMC_312_6_4SA_SVG, new String[][] {
				{"Nucleotide", "diversity", "of", "L", "and", "M", "opsin", "genes", "within"},
				{"species"}
		}
		);
	}

	@Test
	/** {} means skip checking that line
	 * 
	 */
	public void testGetLargePara() {
		testScript(TextFixtures.BMC_312_6_4SB_SVG, new String[][] {
				{"Figure", "2", "summarizes", "the", "nucleotide", "diversity", "of", "the", "L"},
				{"and", "M", "opsin", "exons", "and", "introns", "and", "of", "the", "neutral", "refer-"},
				{"ences", "(see", "Additional", "file", "1,", "Tables", "S4", "and", "S5", "for", "the"},
				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
		}
		);
	}

	@Test
	// wrong
	public void testGetLargePara3() {
		testScript(TextFixtures.BMC_312_6_4SB3_SVG, new String[][] {
				{"ences", "(see", "Additional", "file", "1,", "Tables", "S4", "and", "S5", "for", "the"},
		}
		);
	}

	private void testScript(File svgFile, String[][] words) {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(svgFile);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<ScriptLine> scriptLineList = sc.getScriptLineList();
		Assert.assertEquals("scriptLines", words.length, scriptLineList.size());
		for(int i = 0; i < words.length; i++) {
			ScriptLine scriptLine = scriptLineList.get(i);
			List<ScriptWord> scriptWords = scriptLine.getWords();
			if (words[i].length > 0) {
				Assert.assertEquals("line"+i, words[i].length, scriptWords.size());
				for (int j = 0; j < scriptWords.size(); j++) {
					Assert.assertEquals(""+j, words[i][j], scriptWords.get(j).getRawValue());
				}
			}
		}
	}
}
