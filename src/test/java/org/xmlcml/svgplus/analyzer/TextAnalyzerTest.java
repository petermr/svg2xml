package org.xmlcml.svgplus.analyzer;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.text.SvgPlusCoordinate;
import org.xmlcml.svgplus.text.TextLine;
import org.xmlcml.svgplus.text.TextLineSet;

import com.google.common.collect.Multimap;

public class TextAnalyzerTest {

	private static final File TEXT_ANALYZER_DIR = new File("src/test/resources/org/xmlcml/svgplus/analyzer/");
	private static final File PARA1_SVG = new File(TEXT_ANALYZER_DIR, "1parachunk.svg");
	public static final File PARA_SUSCRIPT_SVG = new File(TEXT_ANALYZER_DIR, "parasWithSuscripts.svg");
	private static final File LINE1_SVG = new File(TEXT_ANALYZER_DIR, "singleLine.svg");

	@Test
	public void analyze1ParaTest() {
		List<TextLine> textLineList = createTextLineList(PARA1_SVG);
		Assert.assertEquals("lines ", 4, textLineList.size());
	}

	@Test
	public void analyzeLine() {
		List<TextLine> textLineList = createTextLineList(PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		List<SVGText> characters = textLine0.getSVGTextCharacters();
		Assert.assertEquals("textLine0", 52, characters.size());
	}

	@Test
	/** note no spaces
	 * 
	 */
	public void getTextLineStringTest() {
		List<TextLine> textLineList = createTextLineList(PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		String lineContent = textLine0.getLineString();
		Assert.assertEquals("text line", "dependentonreactiontimet,whichisafeatureofzero-order", lineContent);
	}

	@Test
	public void insertSpacesInTextLineStringTest() {
		List<TextLine> textLineList = createTextLineList(PARA1_SVG);
		TextLine textLine0 = textLineList.get(0);
		textLine0.insertSpaces();
		String lineContent = textLine0.getLineString();
		Assert.assertEquals("text line", "dependent on reaction time t, which is a feature of zero-order", lineContent);
	}

	@Test
	public void getMeanFontSizeArrayTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA1_SVG);
		 RealArray meanFontSizeArray = analyzerX.getMeanFontSizeArray();
		 Assert.assertNotNull(meanFontSizeArray);
		 Assert.assertTrue(meanFontSizeArray.equals(new RealArray(new double[] {9.465,9.465,9.465,9.465}), 0.001));
	}
	
	@Test
	/** note this test uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
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
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
	 * It's still not quite right
	 * 
	 */
	public void getTextLinesParaSuscriptWithSpacesTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.getLinesInIncreasingY();
		analyzerX.insertSpaces();
		List<String> textLineContentList = analyzerX.getTextLineContentList();
		for (String s : textLineContentList) {
			System.out.println(s);
		}
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
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
	 * It's still not quite right
	 * 
	 */
	public void defaultSpaceTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.getLinesInIncreasingY();
		analyzerX.insertSpaces();
		List<String> textLineContentList = analyzerX.getTextLineContentList();
		for (String s : textLineContentList) {
			System.out.println(s);
		}
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

		for (String s : textLineContentList) {
			System.out.println(s);
		}
	}

	@Test
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
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

		for (String s : textLineContentList) {
			System.out.println(s);
		}
	}

	@Test
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
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
		for (String s : textLineContentList) {
			System.out.println(s);
		}
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

		for (String s : textLineContentList) {
			System.out.println(s);
		}
	}

	
	@Test
	public void getMeanSpaceSeparationArrayParaSuscriptTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		analyzerX.insertSpaces();
		List<TextLine> textLineList = analyzerX.getLinesInIncreasingY();
		for (TextLine textLine : textLineList) {
			RealArray ra = textLine.getActualWidthsOfSpaceCharacters();
			System.out.println(ra);
		}
		List<String> textLineContentList = analyzerX.getTextLineContentList();
		for (String s : textLineContentList) {
			System.out.println(s);
		}
		List<Double> meanSpaceWidthList = analyzerX.getActualWidthsOfSpaceCharactersList();
		Assert.assertNotNull(meanSpaceWidthList);
		for (Double dd : meanSpaceWidthList) {
			System.out.println("> "+dd);
		}
	}
	
	@Test
	public void getMeanFontSizeArrayParaSuscriptTest() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		RealArray meanFontSizeArray = analyzerX.getMeanFontSizeArray();
		Assert.assertNotNull(meanFontSizeArray);
		Assert.assertTrue("fontSizes "+meanFontSizeArray, meanFontSizeArray.equals(
			 new RealArray(new double[] 
        {7.074,9.465,9.465,9.465,7.074,9.465,9.465,7.074,9.465,7.074,9.465,7.074,7.074,9.465,9.465,9.465,9.465}), 0.001));
	}

	@Test
	@Ignore // FIXME
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
	 * 
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
	 * 
	 */
	public void getTextLineSetByFontSize() {
		TextAnalyzerX analyzerX = createTextAnalyzerWithSortedLines(PARA_SUSCRIPT_SVG);
		TextLineSet textLineSetByFontSize = analyzerX.getTextLineSetByFontSize(9.465);
		Assert.assertEquals("textLineSet", 11, textLineSetByFontSize.size());
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

	@Test
	public void unicodeTestNotRelevant() {
		Pattern pattern = Pattern.compile("\\p{Cn}");
		System.out.println("\\u0020 "+pattern.matcher("\u0020").matches());
		System.out.println("A "+pattern.matcher("A").matches());
		System.out.println("\\uf8f8 "+pattern.matcher("\uf8f8").matches());
	}
	
	

}
