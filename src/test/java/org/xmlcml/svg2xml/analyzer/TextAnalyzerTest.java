package org.xmlcml.svg2xml.analyzer;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.SVG2XMLFixtures;
import org.xmlcml.svg2xml.text.ScriptLineOLD;
import org.xmlcml.svg2xml.text.TextCoordinateOLD;
import org.xmlcml.svg2xml.text.TextLineOLD;
import org.xmlcml.svg2xml.text.TextLineSetOLD;
import org.xmlcml.svg2xml.text.TextStructurerOLD;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class TextAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(TextAnalyzerTest.class);

	private final static char MINUS = (char)8722;
	private final static char WHITE_BULLET = (char)9702;
	
	/** checks there are 4 lines in para
	 * 
	 */
	@Test
	public void analyze1ParaTest() {
		List<TextLineOLD> textLineList = TextStructurerOLD.createTextLineList(SVG2XMLFixtures.PARA1_SVG);
		Assert.assertEquals("lines ", 4, textLineList.size());
	}

	/** checks 52 characters in first line */
	@Test
	public void analyzeLine() {
		List<TextLineOLD> textLineList = TextStructurerOLD.createTextLineList(SVG2XMLFixtures.PARA1_SVG);
		TextLineOLD textLine0 = textLineList.get(0);
		List<SVGText> characters = textLine0.getSVGTextCharacters();
		Assert.assertEquals("textLine0", 52, characters.size());
	}

	@Test
	/**checks content of line 0 - note no spaces
	 * 
	 */
	public void getTextLineStringTest() {
		List<TextLineOLD> textLineList = TextStructurerOLD.createTextLineList(SVG2XMLFixtures.PARA1_SVG);
		TextLineOLD textLine0 = textLineList.get(0);
		String lineContent = textLine0.getLineString();
		Assert.assertEquals("text line", "dependentonreactiontimet,whichisafeatureofzero-order", lineContent);
	}

	/** inserts spaces into line 0 */
	@Test
	public void insertSpacesInTextLineStringTest() {
		List<TextLineOLD> textLineList = TextStructurerOLD.createTextLineList(SVG2XMLFixtures.PARA1_SVG);
		TextLineOLD textLine0 = textLineList.get(0);
		textLine0.insertSpaces();
		String lineContent = textLine0.getLineString();
		Assert.assertEquals("text line", "dependent on reaction time t, which is a feature of zero-order", lineContent);
	}

	/** finds mean font sizes in each line (normally only one size per line)
	 */
	@Test
	public void getMeanFontSizeArrayTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA1_SVG);
		 RealArray meanFontSizeArray = textStructurer.getMeanFontSizeArray();
		 Assert.assertNotNull(meanFontSizeArray);
		 Assert.assertTrue(meanFontSizeArray.equals(new RealArray(new double[] {9.465,9.465,9.465,9.465}), 0.001));
	}
	
	@Test
	/** gets all the lines in a suscripted para.
	 * does not detect spaces
	 * 
	 */

	public void getTextLinesParaSuscriptTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		textStructurer.getLinesInIncreasingY();
		List<String> textLineContentList = textStructurer.getTextLineContentList();
		StringTestBase.assertEquals("unspaced strings", 
		    new String[]{String.valueOf(MINUS)+"1"+MINUS+"1",
			"Therateconstantis0.61795mgLh.",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"bytheArrheniusequationk=kexp("+MINUS+"E/RT),whereEisthe",
			"0aa",
			"activationenergy.Takingthenaturallogarithmofthisequa-",
			"tionandcombiningthekvaluesobtainedforthereactionat",
			String.valueOf(WHITE_BULLET),
			"130and200CyieldstheresultsofkandEathighertem-",
			"0a",
			"peratures.Therefore,thecalculatedactivationenergy(E)is",
			"a",
			"5"+MINUS+"1",
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
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		textStructurer.getLinesInIncreasingY();
		textStructurer.insertSpaces();
		List<String> textLineContentList = textStructurer.getTextLineContentList();
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{String.valueOf(MINUS)+" "+"1"+" "+MINUS+" "+"1",
			"The rate constant is 0.61795mgL h .",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"by theArrhenius equation k =k exp(��� E /RT), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			String.valueOf(WHITE_BULLET),
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+MINUS+" "+"1",
			"1.11��10 Jmol .",
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
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		textStructurer.getLinesInIncreasingY();
		textStructurer.insertSpaces();
		List<String> textLineContentList = textStructurer.getTextLineContentList();
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{String.valueOf(MINUS)+" "+"1"+" "+MINUS+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"Thetemperaturedependenceoftherateconstantsisdescribed",
			"by theArrhenius equation k =k exp(��� E /RT), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			String.valueOf(WHITE_BULLET),
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+MINUS+" "+"1",
			"1.11��10 Jmol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micellesolutioniscontrolledbydifferentfactors.Thereactionat"
			},
		textLineContentList.toArray(new String[0]));

	}

	@Test
	/**
	 * It's still not quite right
	 * 
	 */
	@Ignore
	public void minSpaceFactorTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		textStructurer.getLinesInIncreasingY();
		textStructurer.insertSpaces(0.05); // this seems to be minimum
//		textStructurer.insertSpaces(0.12); // this seems to be maximum
		List<String> textLineContentList = textStructurer.getTextLineContentList();
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{String.valueOf(MINUS)+" "+"1"+" "+MINUS+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"The temperature dependence of the rate constants is described",
			"by the Arrhenius equation k = k exp(��� E /RT ), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			String.valueOf(WHITE_BULLET),
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+MINUS+" "+"1",
			"1.11 �� 10 J mol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micelle solution is controlled by different factors. The reaction at"
			},
		textLineContentList.toArray(new String[0]));

	}

	@Test
	/**
	 * It's still not quite right
	 * 
	 */
	@Ignore
	public void maxSpaceFactorTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		textStructurer.getLinesInIncreasingY();
//		textStructurer.insertSpaces(0.05); // this seems to be minimum
		textStructurer.insertSpaces(0.12); // this seems to be maximum
		                              // but very critically balanced
		List<String> textLineContentList = textStructurer.getTextLineContentList();
		StringTestBase.assertEquals("spaced strings", 
		    new String[]{String.valueOf(MINUS)+" "+"1"+" "+MINUS+" "+"1",
			"The rate constant is 0.61795 mg L h .",
			"The temperature dependence of the rate constants is described",
			"by the Arrhenius equation k = k exp(��� E /RT ), where E is the",
			"0 a a",
			"activation energy. Taking the natural logarithm of this equa-",
			"tion and combining the k values obtained for the reaction at",
			String.valueOf(WHITE_BULLET),
			"130 and 200 C yields the results of k and E at higher tem-",
			"0 a",
			"peratures. Therefore, the calculated activation energy (E ) is",
			"a",
			"5"+" "+MINUS+" "+"1",
			"1.11 �� 10 J mol .",
			"The above analysis seems to indicate that in different reac-",
			"tion temperature ranges the solvothermal reaction in the reverse",
			"micelle solution is controlled by different factors. The reaction at"
			},
		textLineContentList.toArray(new String[0]));

	}

	
//	@Test
//	public void getMeanSpaceSeparationArrayParaSuscriptTest() {
//		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
//		textStructurer.insertSpaces();
//		List<TextLine> textLineList = textStructurer.getLinesInIncreasingY();
//		List<String> textLineContentList = textStructurer.getTextLineContentList();
//		List<Double> meanSpaceWidthList = textStructurer.getActualWidthsOfSpaceCharactersList();
//		Assert.assertNotNull(meanSpaceWidthList);
//	}
	
	@Test
	public void getMeanFontSizeArrayParaSuscriptTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		RealArray meanFontSizeArray = textStructurer.getMeanFontSizeArray();
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

//	@Test
//	@Ignore // FIXME
//	/** not sure what this does
//	 * 
//	 */
//	public void getModalExcessWidthArrayTest() {
//		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
//		textStructurer.insertSpaces();
//		RealArray modalExcessWidthArray = textStructurer.getModalExcessWidthArray();
//		Assert.assertNotNull(modalExcessWidthArray);
//		Assert.assertTrue("fontSizes "+modalExcessWidthArray, modalExcessWidthArray.equals(
//			 new RealArray(new double[] 
//        {7.074,9.465,9.465,9.465,7.074,9.465,9.465,7.074,9.465,7.074,9.465,7.074,7.074,9.465,9.465,9.465,9.465}), 0.001));
//	}
	
	@Test
	/** test contains normal and superscripts so two fontsizes
	 * 
	 */
	public void getFontSizeSetTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		Set<TextCoordinateOLD> fontSizeSet = textStructurer.getFontSizeSet();
		Assert.assertEquals("font sizes", 2, fontSizeSet.size());
		Assert.assertTrue("font large", fontSizeSet.contains(new TextCoordinateOLD(9.465)));
		Assert.assertTrue("font small", fontSizeSet.contains(new TextCoordinateOLD(7.07)));
	}

	@Test
	/** 
	 * indexes lines by font sizes
	 */
	public void getTextLinesByFontSizeTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		Multimap<TextCoordinateOLD, TextLineOLD> textLineListByFontSize = textStructurer.getTextLineListByFontSize();
		Assert.assertEquals("font sizes", 17, textLineListByFontSize.size());
		List<TextLineOLD> largeLines = (List<TextLineOLD>) textLineListByFontSize.get(new TextCoordinateOLD(9.465));
		Assert.assertEquals("font large", 11, largeLines.size());
		Assert.assertEquals("font small", 6, ((List<TextLineOLD>) textLineListByFontSize.get(new TextCoordinateOLD(7.07))).size());
	}

	@Test
	/** 
	 * retrieve by font size
	 */
	public void getTextLineSetByFontSizeTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		TextLineSetOLD textLineSetByFontSize = textStructurer.getTextLineSetByFontSize(9.465);
		Assert.assertEquals("textLineSet", 11, textLineSetByFontSize.size());
	}

	@Test
	/** 
	 * get Mainlines
	 */
	public void getLargestFontTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		TextCoordinateOLD maxSize = textStructurer.getLargestFontSize();
		Assert.assertEquals("largest font", 9.47, maxSize.getDouble(), 0.001);
	}

	@Test
	/** 
	 * get Mainlines
	 */
	public void getLinesWithLargestFontTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> largestLineList = textStructurer.getLinesWithLargestFont();
		Assert.assertEquals("largest", 11, largestLineList.size());
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts0() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> largestLineList = textStructurer.getLinesWithLargestFont();
		TextLineOLD largeLine = largestLineList.get(0);
		TextLineOLD superscript = largeLine.getSuperscript();
		Assert.assertNotNull(superscript);
		Assert.assertEquals("sup", "−1−1", superscript.getLineString());
		TextLineOLD subscript = largeLine.getSubscript();
		Assert.assertNull(subscript);
	}


	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts1() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> largestLineList = textStructurer.getLinesWithLargestFont();
		TextLineOLD largeLine = largestLineList.get(1);
		TextLineOLD superscript = largeLine.getSuperscript();
		Assert.assertNull(superscript);
		TextLineOLD subscript = largeLine.getSubscript();
		Assert.assertNull(subscript);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts2() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> largestLineList = textStructurer.getLinesWithLargestFont();
		TextLineOLD largeLine = largestLineList.get(2);
		TextLineOLD superscript = largeLine.getSuperscript();
		Assert.assertNull(superscript);
		TextLineOLD subscript = largeLine.getSubscript();
		Assert.assertNotNull(subscript);
		Assert.assertEquals("sub", "0aa", subscript.getLineString());
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testAnalyzeSuscripts5() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> largestLineList = textStructurer.getLinesWithLargestFont();
		TextLineOLD largeLine = largestLineList.get(5);
		TextLineOLD superscript = largeLine.getSuperscript();
		Assert.assertNotNull(superscript);
		String s = superscript.getLineString();
		// this is a WHITE BULLET (should be a degree sign)
		Assert.assertEquals("sup"+(int)s.charAt(0), String.valueOf(WHITE_BULLET), superscript.getLineString());
		TextLineOLD subscript = largeLine.getSubscript();
		Assert.assertNotNull(subscript);
		Assert.assertEquals("sub", "0a", subscript.getLineString());
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines0() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		TextLineOLD largeLine = textStructurer.getLinesWithLargestFont().get(0);
		List<TextLineOLD> suscriptLines = largeLine.createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}
	
	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines1() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> suscriptLines = textStructurer.getLinesWithLargestFont().get(1).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines2() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> suscriptLines = textStructurer.getLinesWithLargestFont().get(2).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines5() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> suscriptLines = textStructurer.getLinesWithLargestFont().get(5).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}

	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptTextLines7() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> suscriptLines = textStructurer.getLinesWithLargestFont().get(7).createSuscriptTextLineList();
		printTextLines(suscriptLines);
	}
	
	@Test
	/** 
	 * suscripts
	 */
	public void testCreateSuscriptWordTextLines0() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		TextLineOLD largeLine = textStructurer.getLinesWithLargestFont().get(0);
		List<TextLineOLD> suscriptLines = largeLine.createSuscriptTextLineList();
		for (TextLineOLD textLine : suscriptLines) {
			textLine.insertSpaces();
		}
	}
	

	private void printTextLines(List<TextLineOLD> suscriptLines) {
		for (TextLineOLD textLine : suscriptLines){
			LOG.trace(String.valueOf(textLine.getSuscript())+" ");
			printLine(textLine.getSVGTextCharacters());
		}
	}


	/** no-op
	 * 
	 * @param largeLineSVG
	 */
	private void printLine(List<SVGText> largeLineSVG) {
//		SYSOUT.print("LINE: ");
//		for (SVGText large : largeLineSVG) {
//			SYSOUT.print(" "+large.getValue());
//		}
	}


	@Test
	/** 
	 * get serial of text
	 */
	public void testgetSerial() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> largestLineList = textStructurer.getLinesWithLargestFont();
		Assert.assertEquals("super", 1, (int) textStructurer.getSerialNumber(largestLineList.get(0)));
	}



	@Test
	/** 
	 * get Interline separation
	 */
	public void getInterTextLineSeparationSetTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		Multiset<Double> separationSet = textStructurer.createSeparationSet(2);
		Assert.assertEquals("separationSet", 16, separationSet.size());
		Assert.assertEquals("separationSet", 6, separationSet.entrySet().size());
	}

	@Test
	/** 
	 * get Interline separation
	 */
	public void getMainInterTextLineSeparationTest() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		Double sep = textStructurer.getMainInterTextLineSeparation(2);
		Assert.assertEquals("sep ", 10.96, sep, 0.001);
	}

	@Test
	/** 
	 * get Interline separation
	 */
	public void getInterTextLineSeparation() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		RealArray interTextLineSeparationArray = textStructurer.getInterTextLineSeparationArray();
		Assert.assertNotNull(interTextLineSeparationArray);
		RealArray ref = new RealArray(new double[]{
				3.436,10.959,10.959,1.419,9.54,10.959,7.523,3.436,1.419,9.539,1.42,6.104,3.435,10.959,10.959,10.959});
		Assert.assertTrue("interline separation "+interTextLineSeparationArray, interTextLineSeparationArray.equals(ref, 0.001));
	}

	@Test
	/** 
	 * get merged boxes
	 */
	public void testgetDiscreteBoxes() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<Real2Range> discreteBoxes  = textStructurer.getTextLineChunkBoxesAndInitialiScriptLineList();
		Assert.assertNotNull(discreteBoxes);
		// lines 7subscript and 8superscrip overlap 
		Assert.assertEquals("boxes", 10, discreteBoxes.size());
	}

	@Test
	/** 
	 * get lines in merged boxes
	 */
	public void testLinesInDiscreteBoxes() {
		int[] count = {2, 1, 2, 1, 1, 3, 4, 1, 1, 1};
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLineOLD> textLineChunkList  = textStructurer.getInitialScriptLineList();
		Assert.assertNotNull(textLineChunkList);
		Assert.assertEquals("boxes", 10, textLineChunkList.size());
		int i = 0;
		for (ScriptLineOLD textLineChunk : textLineChunkList) {
			Assert.assertEquals("box"+i, count[i], textLineChunk.size());
			LOG.trace(">>");
			for (TextLineOLD textLine: textLineChunk) {
				LOG.trace(textLine);
			}
			LOG.trace("<<");
			i++;
		}
	}

	@Test
	/** 
	 * get lines in merged boxes
	 */
	public void testGetInitialTextLineChunkList() {
		int[] count = {2, 1, 2, 1, 1, 3, 4, 1, 1, 1};
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLineOLD> textLineChunkList  = textStructurer.getInitialScriptLineList();
		Assert.assertNotNull(textLineChunkList);
		Assert.assertEquals("boxes", 10, textLineChunkList.size());
	}


	@Test
	/** 
	 * get coordinates of lines
	 */
	public void getTextLineCoordinateArray() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		RealArray textLineCoordinateArray = textStructurer.getTextLineCoordinateArray();
		Assert.assertNotNull(textLineCoordinateArray);
		RealArray ref = new RealArray(new double[] 
			        { 343.872,347.308,358.267,369.226,370.645,380.185,391.144,398.667,402.103,403.522,413.061,414.481,420.585,424.02,434.979,445.938,456.897});
		Assert.assertTrue("textline coordinates "+textLineCoordinateArray, textLineCoordinateArray.equals(ref, 0.001));
	}

	@Test
	/**
	 * 
	 */
	public void testGetCommonestFontSizeTextLineList() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<TextLineOLD> isCommonestFontSize = textStructurer.getCommonestFontSizeTextLineList();
		Assert.assertEquals("commonestFontSize", 11, isCommonestFontSize.size());
	}
	
	@Test
	/** splits textGroups into lines with sub/superscripts
	 * 
	 */
	public void testGetScriptedLineGroupList() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLineOLD> textLineChunkList  = textStructurer.getInitialScriptLineList();
		Assert.assertEquals("TextLines ", 10, textLineChunkList.size());
		List<ScriptLineOLD> separated = textStructurer.getScriptedLineListForCommonestFont();
		Assert.assertEquals("split", 11, separated.size());
		for (ScriptLineOLD group : separated) {
			LOG.trace(group);
		}
	}
	
	@Test
	public void testCreateTextListLines0() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		ScriptLineOLD group0 = textStructurer.getScriptedLineListForCommonestFont().get(0);
		Assert.assertEquals("group0", 2, group0.size());
		List<TextLineOLD> textLineList = group0.createSuscriptTextLineList();
		Assert.assertEquals("group0", 5, textLineList.size());
	}
	
	@Test
	public void testCreateTextListLinesAll() {
		int[] groupSize = new int[]{5,1,7,1,1,7,3,5,1,1,1};
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		List<ScriptLineOLD> groupList = textStructurer.getScriptedLineListForCommonestFont();
		Assert.assertEquals("groups", 11, groupList.size());
		int i = 0;
		for (ScriptLineOLD group : groupList) {
			List<TextLineOLD> textLineList = group.createSuscriptTextLineList();
			Assert.assertEquals("group"+i, groupSize[i], textLineList.size());
			i++;
		}
	}
	
	@Test
	public void testCreateTextListHtml0() {
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(SVG2XMLFixtures.PARA_SUSCRIPT_SVG);
		ScriptLineOLD group0 = textStructurer.getScriptedLineListForCommonestFont().get(0);
		HtmlElement textLineHtml = group0.createHtmlElement();
		Assert.assertEquals("group0", 
				"<p xmlns=\"http://www.w3.org/1999/xhtml\"><span>The rate constant is 0.61795 mg L</span><sup><span>− </span>" +
				"<span>1</span></sup><span>h</span><sup><span>− </span><span>1</span></sup><span>.</span></p>",	

//				"<p xmlns=\"http://www.w3.org/1999/xhtml\">" +
//				"<span style=\"font-size:9.465px;font-family:TimesNewRoman;\">The rate constant is 0.61795 mg L</span>" +
//				"<sup><span style=\"font-size:7.074px;color:red;font-family:MTSYN;\">"+MINUS+" </span>" +
//				"<span style=\"font-size:7.074px;font-family:TimesNewRoman;\">1</span></sup>" +
//				"<span style=\"font-size:9.465px;font-family:TimesNewRoman;\">h</span>" +
//				"<sup><span style=\"font-size:7.074px;color:red;font-family:MTSYN;\">"+MINUS+" </span><span style=\"font-size:7.074px;font-family:TimesNewRoman;\">1</span></sup>" +
//				"<span style=\"font-size:9.465px;font-family:TimesNewRoman;\">.</span></p>",
				textLineHtml.toXML());
	}
//	<p xmlns="http://www.w3.org/1999/xhtml"><span>The rate constant is 0.61795 mg L</span><sup><span>��� </span><span>1</span></sup><span>h</span><sup><span>��� </span><span>1</span></sup><span>.</span></p>	
	
//	@Test
//	public void testCreateTextListHtmlDiv() {
//		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(Fixtures.PARA_SUSCRIPT_SVG);
//		List<ScriptLine> textLineGroupList = textStructurer.getScriptedLineList();
//		HtmlElement divElement = TextStructurer.createHtmlDiv(textLineGroupList);
//		Element ref = XMLUtil.parseQuietlyToDocument(new File("src/test/resources/org/xmlcml/svg2xml/analyzer/textLineGroup0.html")).getRootElement();
//		CMLXOMTestUtils.assertEqualsCanonically("html", ref, divElement, true);
//	}
	

	/** FIXTURES */

	/** attempts to test for Unicode
	 * doesn't seem to work
	 */
	@Test
	@Ignore
	public void unicodeTestNotRelevant() {
		Pattern pattern = Pattern.compile("\\p{Cn}");
		LOG.trace("\\u0020 "+pattern.matcher("\u0020").matches());
		LOG.trace("A "+pattern.matcher("A").matches());
		LOG.trace("\\uf8f8 "+pattern.matcher("\uf8f8").matches());
	}
	
	

}
