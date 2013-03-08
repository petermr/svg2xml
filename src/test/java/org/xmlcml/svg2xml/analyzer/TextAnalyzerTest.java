package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.text.SvgPlusCoordinate;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextLineSet;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class TextAnalyzerTest {

	private static final File TEXT_ANALYZER_DIR = new File("src/test/resources/org/xmlcml/svg2xml/analyzer/");
	/** a 4 line chunk (paragraph) with no suscripts */
	private static final File PARA1_SVG = new File(TEXT_ANALYZER_DIR, "1parachunk.svg");
	// 3 paragraphs
	public static final File PARA_SUSCRIPT_SVG = new File(TEXT_ANALYZER_DIR, "parasWithSuscripts.svg");
	
	private static final File LINE1_SVG = new File(TEXT_ANALYZER_DIR, "singleLine.svg");

	/** checks there are 4 lines in para
	 * 
	 */
	@Test
	public void analyze1ParaTest() {
		List<TextLine> textLineList = createTextLineList(PARA1_SVG);
		Assert.assertEquals("lines ", 4, textLineList.size());
	}

	/** checks 52 characters in first line */
	@Test
	public void analyzeLine() {
		List<TextLine> textLineList = createTextLineList(PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		List<SVGText> characters = textLine0.getSVGTextCharacters();
		Assert.assertEquals("textLine0", 52, characters.size());
	}

	@Test
	/**checks content of line 0 - note no spaces
	 * 
	 */
	public void getTextLineStringTest() {
		List<TextLine> textLineList = createTextLineList(PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		String lineContent = textLine0.getLineString();
		Assert.assertEquals("text line", "dependentonreactiontimet,whichisafeatureofzero-order", lineContent);
	}

	/** inserts spaces into line 0 */
	@Test
	public void insertSpacesInTextLineStringTest() {
		List<TextLine> textLineList = createTextLineList(PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		textLine0.insertSpaces();
		String lineContent = textLine0.getLineString();
		Assert.assertEquals("text line", "dependent on reaction time t, which is a feature of zero-order", lineContent);
	}

	/** finds mean font sizes in each line (normally only one size per line)
	 */
	@Test
	public void getMeanFontSizeArrayTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA1_SVG);
		 RealArray meanFontSizeArray = analyzerX.getMeanFontSizeArray();
		 Assert.assertNotNull(meanFontSizeArray);
		 Assert.assertTrue(meanFontSizeArray.equals(new RealArray(new double[] {9.465,9.465,9.465,9.465}), 0.001));
	}
	
	@Test
	/** gets all the lines in a suscripted para.
	 * does not detect spaces
	 * 
	 */
	public void getTextLinesParaSuscriptTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.getLinesInIncreasingY();
		List<String> textLineContentList = analyzerX.getTextLineContentList();
		StringTestBase.assertEquals("unspaced strings", 
		    new String[]{""+(char)8722+"1"+(char)8722+"1",
			"Therateconstantis0.61795mgLh.",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"bytheArrheniusequationk=kexp("+(char)8722+"E/RT),whereEisthe",
			"0aa",
			"activationenergy.Takingthenaturallogarithmofthisequa-",
			"tionandcombiningthekvaluesobtainedforthereactionat",
			""+(char)9702,
			"130and200CyieldstheresultsofkandEathighertem-",
			"0a",
			"peratures.Therefore,thecalculatedactivationenergy(E)is",
			"a",
			"5"+(char)8722+"1",
			"1.11×10Jmol.",
			"Theaboveanalysisseemstoindicatethatindifferentreac-",
			"tiontemperaturerangesthesolvothermalreactioninthereverse",
			"micellesolutioniscontrolledbydifferentfactors.Thereactionat"
			},
			textLineContentList.toArray(new String[0]));
	}

	@Test
	@Ignore // until we have the spaces sorted
    /**
	 * It's still not quite right
	 * 
	 */
	public void getTextLinesParaSuscriptWithSpacesTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.getLinesInIncreasingY();
		analyzerX.insertSpaces();
		List<String> textLineContentList = analyzerX.getTextLineContentList();
//		for (String s : textLineContentList) {
//			System.out.println(s);
//		}
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{""+(char)8722+" "+"1"+" "+(char)8722+" "+"1",
			"The rate constant is 0.61795mgL h .",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"by theArrhenius equation k =k exp(− E /RT), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			""+(char)9702,
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+(char)8722+" "+"1",
			"1.11×10 Jmol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micellesolutioniscontrolledbydifferentfactors.Thereactionat"
			},
			textLineContentList.toArray(new String[0]));
	}

	@Test
	@Ignore // not sure why now
	/**
	 * It's still not quite right
	 * 
	 */
	public void defaultSpaceTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.getLinesInIncreasingY();
		analyzerX.insertSpaces();
		List<String> textLineContentList = analyzerX.getTextLineContentList();
//		for (String s : textLineContentList) {
//			System.out.println(s);
//		}
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{""+(char)8722+" "+"1"+" "+(char)8722+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"by theArrhenius equation k =k exp(− E /RT), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			""+(char)9702,
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+(char)8722+" "+"1",
			"1.11×10 Jmol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micellesolutioniscontrolledbydifferentfactors.Thereactionat"
			},
		textLineContentList.toArray(new String[0]));

//		for (String s : textLineContentList) {
//			System.out.println(s);
//		}
	}

	@Test
	/**
	 * It's still not quite right
	 * 
	 */
	public void minSpaceFactorTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.getLinesInIncreasingY();
		analyzerX.insertSpaces(0.05); // this seems to be minimum
//		analyzerX.insertSpaces(0.12); // this seems to be maximum
		List<String> textLineContentList = analyzerX.getTextLineContentList();
//		for (String s : textLineContentList) {
//			System.out.println(s);
//		}
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{""+(char)8722+" "+"1"+" "+(char)8722+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"The temperature dependence of the rate constants is described",
			"by the Arrhenius equation k = k exp(− E /RT ), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			""+(char)9702,
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+(char)8722+" "+"1",
			"1.11 × 10 J mol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micelle solution is controlled by different factors. The reaction at"
			},
		textLineContentList.toArray(new String[0]));

//		for (String s : textLineContentList) {
//			System.out.println(s);
//		}
	}

	@Test
	/**
	 * It's still not quite right
	 * 
	 */
	public void maxSpaceFactorTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.getLinesInIncreasingY();
//		analyzerX.insertSpaces(0.05); // this seems to be minimum
		analyzerX.insertSpaces(0.12); // this seems to be maximum
		                              // but very critically balanced
		List<String> textLineContentList = analyzerX.getTextLineContentList();
//		for (String s : textLineContentList) {
//			System.out.println(s);
//		}
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{""+(char)8722+" "+"1"+" "+(char)8722+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"The temperature dependence of the rate constants is described",
			"by the Arrhenius equation k = k exp(− E /RT ), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			""+(char)9702,
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+(char)8722+" "+"1",
			"1.11 × 10 J mol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micelle solution is controlled by different factors. The reaction at"
			},
		textLineContentList.toArray(new String[0]));

//		for (String s : textLineContentList) {
//			System.out.println(s);
//		}
	}

	
	@Test
	public void getMeanSpaceSeparationArrayParaSuscriptTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.insertSpaces();
		List<TextLine> textLineList = analyzerX.getLinesInIncreasingY();
//		for (TextLine textLine : textLineList) {
//			RealArray ra = textLine.getActualWidthsOfSpaceCharacters();
//			System.out.println(ra);
//		}
		List<String> textLineContentList = analyzerX.getTextLineContentList();
//		for (String s : textLineContentList) {
//			System.out.println(s);
//		}
		List<Double> meanSpaceWidthList = analyzerX.getActualWidthsOfSpaceCharactersList();
		Assert.assertNotNull(meanSpaceWidthList);
//		for (Double dd : meanSpaceWidthList) {
//			System.out.println("> "+dd);
//		}
	}
	
	@Test
	public void getMeanFontSizeArrayParaSuscriptTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		RealArray meanFontSizeArray = analyzerX.getMeanFontSizeArray();
		Assert.assertNotNull(meanFontSizeArray);
		Assert.assertTrue("fontSizes "+meanFontSizeArray, meanFontSizeArray.equals(
			 new RealArray(new double[] 
        {
					 7.074, // super0
					 9.465,	// l0
					 9.465,	// l1
					 9.465,	//l2
					 7.074, // sub2
					 9.465, // l3
					 9.465, // l4
					 7.074, // sup5
					 9.465, // l5
					 7.074, // sub5
					 9.465, // l6
					 7.074, // sub6
					 7.074,	// sup7
					 9.465, // l7
					 9.465, // l8
					 9.465, // l9
					 9.465  // l10
					 }), 0.001));
	}

	@Test
	@Ignore // FIXME
	/** not sure what this does
	 * 
	 */
	public void getModalExcessWidthArrayTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.insertSpaces();
		RealArray modalExcessWidthArray = analyzerX.getModalExcessWidthArray();
		Assert.assertNotNull(modalExcessWidthArray);
		Assert.assertTrue("fontSizes "+modalExcessWidthArray, modalExcessWidthArray.equals(
			 new RealArray(new double[] 
        {7.074,9.465,9.465,9.465,7.074,9.465,9.465,7.074,9.465,7.074,9.465,7.074,7.074,9.465,9.465,9.465,9.465}), 0.001));
	}
	
	@Test
	/** test contains normal and superscripts so two fontsizes
	 * 
	 */
	public void getFontSizeSetTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		Set<SvgPlusCoordinate> fontSizeSet = analyzerX.getFontSizeSet();
		Assert.assertEquals("font sizes", 2, fontSizeSet.size());
		Assert.assertTrue("font large", fontSizeSet.contains(new SvgPlusCoordinate(9.465)));
		Assert.assertTrue("font small", fontSizeSet.contains(new SvgPlusCoordinate(7.07)));
	}

	@Test
	/** 
	 * indexes lines by font sizes
	 */
	public void getTextLinesByFontSizeTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		Multimap<SvgPlusCoordinate, TextLine> textLineListByFontSize = analyzerX.getTextLineListByFontSize();
		Assert.assertEquals("font sizes", 17, textLineListByFontSize.size());
		List<TextLine> largeLines = (List<TextLine>) textLineListByFontSize.get(new SvgPlusCoordinate(9.465));
		Assert.assertEquals("font large", 11, largeLines.size());
		Assert.assertEquals("font small", 6, ((List<TextLine>) textLineListByFontSize.get(new SvgPlusCoordinate(7.07))).size());
	}

	@Test
	/** 
	 * retrieve by font size
	 */
	public void getTextLineSetByFontSizeTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		TextLineSet textLineSetByFontSize = analyzerX.getTextLineSetByFontSize(9.465);
		Assert.assertEquals("textLineSet", 11, textLineSetByFontSize.size());
	}

	@Test
	/** 
	 * get Mainlines
	 */
	public void getLargestFontTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		SvgPlusCoordinate maxSize = analyzerX.getLargestFont();
		Assert.assertEquals("largest font", 9.47, maxSize.getDouble(), 0.001);
	}

	@Test
	/** 
	 * get Mainlines
	 */
	public void getLinesWithLargestFontTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = analyzerX.getLinesWithLargestFont();
		Assert.assertEquals("largest", 11, largestLineList.size());
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts0() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = analyzerX.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(0);
		TextLine superscript = largeLine.getSuperscript();
		Assert.assertNotNull(superscript);
		Assert.assertEquals("sup", "−1−1", superscript.getLineString());
		TextLine subscript = largeLine.getSubscript();
		Assert.assertNull(subscript);
	}


	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts1() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = analyzerX.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(1);
		TextLine superscript = largeLine.getSuperscript();
		Assert.assertNull(superscript);
		TextLine subscript = largeLine.getSubscript();
		Assert.assertNull(subscript);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts2() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = analyzerX.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(2);
		TextLine superscript = largeLine.getSuperscript();
		Assert.assertNull(superscript);
		TextLine subscript = largeLine.getSubscript();
		Assert.assertNotNull(subscript);
		Assert.assertEquals("sub", "0aa", subscript.getLineString());
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts5() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = analyzerX.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(5);
		TextLine superscript = largeLine.getSuperscript();
		Assert.assertNotNull(superscript);
		String s = superscript.getLineString();
		// this is a WHITE BULLET (should be a degree sign)
		Assert.assertEquals("sup"+(int)s.charAt(0), ""+(char)9702, superscript.getLineString());
		TextLine subscript = largeLine.getSubscript();
		Assert.assertNotNull(subscript);
		Assert.assertEquals("sub", "0a", subscript.getLineString());
	}

	@Test
	/** 
	 * suscripts - mainly debugging routine
	 */
	public void testCreateSuscriptLine0() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		TextLine largeLine = analyzerX.getLinesWithLargestFont().get(0);
		List<SVGText> largeLineSVG = largeLine.createSuscriptString();
		printLine(largeLineSVG);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines0() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		TextLine largeLine = analyzerX.getLinesWithLargestFont().get(0);
		List<TextLine> suscriptLines = largeLine.createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}
	
	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines1() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> suscriptLines = analyzerX.getLinesWithLargestFont().get(1).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines2() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> suscriptLines = analyzerX.getLinesWithLargestFont().get(2).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines5() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> suscriptLines = analyzerX.getLinesWithLargestFont().get(5).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines7() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> suscriptLines = analyzerX.getLinesWithLargestFont().get(7).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}
	
	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptWordTextLines0() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		TextLine largeLine = analyzerX.getLinesWithLargestFont().get(0);
		List<TextLine> suscriptLines = largeLine.createSuscriptTextLineList();
		for (TextLine textLine : suscriptLines) {
			textLine.insertSpaces();
//			System.out.println(textLine.getSpacedLineString());
		}
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTML0() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		TextLine largeLine = analyzerX.getLinesWithLargestFont().get(0);
		HtmlElement p = largeLine.createHtmlLine();
		Assert.assertEquals("p", 
				"<p xmlns=\"http://www.w3.org/1999/xhtml\"><span>The rate constant is 0.61795 mg L</span><sup>− 1</sup><span>h</span><sup>− 1</sup><span>.</span></p>",
				p.toXML());
	}
	

	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTML1() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		HtmlElement p = analyzerX.getLinesWithLargestFont().get(1).createHtmlLine();
		Assert.assertEquals("p", 
				"<p xmlns=\"http://www.w3.org/1999/xhtml\"><span>The temperature dependence of the rate constants is described</span></p>",
				p.toXML());
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTML2() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		HtmlElement p = analyzerX.getLinesWithLargestFont().get(2).createHtmlLine();
		Assert.assertEquals("p", 
				"<p xmlns=\"http://www.w3.org/1999/xhtml\"><span>by the Arrhenius equation k = k</span>" +
				"<sub>0</sub><span>exp("+(char)8722+" E</span><sub>a</sub><span>/RT ), where E</span>" +
				"<sub>a</sub><span>is the</span></p>",
				p.toXML());
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTML5() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		HtmlElement p = analyzerX.getLinesWithLargestFont().get(5).createHtmlLine();
		Assert.assertEquals("p", 
				"<p xmlns=\"http://www.w3.org/1999/xhtml\"><span>130 and 200</span><sup>"+(char)9702+"</sup>" +
						"<span>C yields the results of k</span><sub>0</sub><span>and E</span><sub>a</sub>" +
						"<span>at higher tem-</span></p>",
				p.toXML());
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTMLRawDiv() throws Exception {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		HtmlElement div = analyzerX.createHtmlRawDiv();
		CMLUtil.debug(div, new FileOutputStream("target/div.html"), 1);
	}
	
	@Test
	/** 
	 * superscripts
	 */
	public void testCreateHTMLDivWithParas() throws Exception {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		HtmlElement div = analyzerX.createHtmlDivWithParas();
		CMLUtil.debug(div, new FileOutputStream("target/divParas.html"), 1);
	}
	

	private void printTextLines(List<TextLine> suscriptLines) {
		for (TextLine textLine : suscriptLines){
			System.out.print(""+textLine.getSuscript()+" ");
			printLine(textLine.getSVGTextCharacters());
		}
		System.out.println();
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptLine4() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = analyzerX.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(4);
		List<SVGText> largeLineSVG = largeLine.createSuscriptString();
		printLine(largeLineSVG);
		System.out.println();
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptLine5() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = analyzerX.getLinesWithLargestFont();
		TextLine largeLine = largestLineList.get(5);
		List<SVGText> largeLineSVG = largeLine.createSuscriptString();
		printLine(largeLineSVG);
		System.out.println();
	}

	private void printLine(List<SVGText> largeLineSVG) {
		for (SVGText large : largeLineSVG) {
			System.out.print(" "+large.getValue());
		}
	}


	@Test
	/** 
	 * get serial of text
	 */
	public void testgetSerial() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		List<TextLine> largestLineList = analyzerX.getLinesWithLargestFont();
		Assert.assertEquals("super", 1, (int) analyzerX.getSerialNumber(largestLineList.get(0)));
	}



	@Test
	/** 
	 * get Interline separation
	 */
	public void getInterTextLineSeparationSetTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		Multiset<Double> separationSet = analyzerX.createSeparationSet(2);
		Assert.assertEquals("separationSet", 16, separationSet.size());
		Assert.assertEquals("separationSet", 6, separationSet.entrySet().size());
	}

	@Test
	/** 
	 * get Interline separation
	 */
	public void getMainInterTextLineSeparationTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		Double sep = analyzerX.getMainInterTextLineSeparation(2);
		Assert.assertEquals("sep ", 10.96, sep, 0.001);
	}

	@Test
	/** 
	 * get Interline separation
	 */
	public void getInterTextLineSeparation() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		RealArray interTextLineSeparationArray = analyzerX.getInterTextLineSeparationArray();
		Assert.assertNotNull(interTextLineSeparationArray);
		RealArray ref = new RealArray(new double[]{
				3.436,10.959,10.959,1.419,9.54,10.959,7.523,3.436,1.419,9.539,1.42,6.104,3.435,10.959,10.959,10.959});
		Assert.assertTrue("interline separation "+interTextLineSeparationArray, interTextLineSeparationArray.equals(ref, 0.001));
	}


	@Test
	/** 
	 * get coordinates of lines
	 */
	public void getTextLineCoordinateArray() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		RealArray textLineCoordinateArray = analyzerX.getTextLineCoordinateArray();
		Assert.assertNotNull(textLineCoordinateArray);
		RealArray ref = new RealArray(new double[] 
			        { 343.872,347.308,358.267,369.226,370.645,380.185,391.144,398.667,402.103,403.522,413.061,414.481,420.585,424.02,434.979,445.938,456.897});
		Assert.assertTrue("textline coordinates "+textLineCoordinateArray, textLineCoordinateArray.equals(ref, 0.001));
	}

	/** FIXTURES */
	private static List<TextLine> createTextLineList(File svgFile) {
		TextAnalyzerX textAnalyzer = createTextAnalyzerWithSortedLines(svgFile);
		List<TextLine> textLineList = textAnalyzer.getLinesInIncreasingY();
		return textLineList;
	}

	public static TextAnalyzerX createTextAnalyzerWithSortedLines(File svgFile) {
		SVGSVG svgPage = Fixtures.createSVGPage(svgFile);
		List<SVGText> textCharacters = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgPage, ".//svg:text"));
		TextAnalyzerX textAnalyzer = new TextAnalyzerX();
		textAnalyzer.analyzeTexts(textCharacters);
		textAnalyzer.getLinesInIncreasingY();
		return textAnalyzer;
	}

	/** attempts to test for Unicode
	 * doesn't seem to work
	 */
	@Test
	@Ignore
	public void unicodeTestNotRelevant() {
		Pattern pattern = Pattern.compile("\\p{Cn}");
		System.out.println("\\u0020 "+pattern.matcher("\u0020").matches());
		System.out.println("A "+pattern.matcher("A").matches());
		System.out.println("\\uf8f8 "+pattern.matcher("\uf8f8").matches());
	}
	
	

}
