package org.xmlcml.svg2xml.text;

import java.io.File;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerTest;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;

public class TextLineTest {

	@Test
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
	 *
	 */
	public void insertSpaceFactorTest() {

		TextLine textLine5 = TextLineTest.getTextLine(TextAnalyzerTest.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());

		textLine5 = TextLineTest.getTextLine(TextAnalyzerTest.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
		double spaceFactor = 0.0;
		textLine5.insertSpaces(spaceFactor);
		Assert.assertEquals("spaceFactor: "+spaceFactor, "activation energy. Taking the natural logarithm of this equa-", textLine5.getLineContent());

		testScalefactor(0.10, 5, "activation energy. Taking the natural logarithm of this equa-");
		testScalefactor(0.36, 5, "activation energy. Taking the natural logarithm of this equa-");
		testScalefactor(0.39, 5, "activation energy.Taking the natural logarithm of this equa-");
		testScalefactor(0.42, 5, "activation energy.Taking the natural logarithm of this equa-");
		testScalefactor(0.43, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");
		testScalefactor(0.45, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");
		testScalefactor(0.50, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");
		testScalefactor(1.00, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");
		testScalefactor(10.0, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");

	}

	/** uses default spaceFactor
	 *
	 */
	@Test
	public void insertSpaceTest() {

		TextLine textLine5 = TextLineTest.getTextLine(TextAnalyzerTest.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
		textLine5 = TextLineTest.getTextLine(TextAnalyzerTest.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
		textLine5.insertSpaces();
		Assert.assertEquals("default spaceFactor", "activation energy. Taking the natural logarithm of this equa-", textLine5.getLineContent());
	}

	private static void testScalefactor(double spaceFactor, int lineNumber, String expected) {
		TextLine textLine5;
		textLine5 = TextLineTest.getTextLine(TextAnalyzerTest.PARA_SUSCRIPT_SVG, lineNumber);
		textLine5.insertSpaces(spaceFactor);
		Assert.assertEquals("spaceFactor: "+spaceFactor, expected, textLine5.getLineContent());
	}

	@Test
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
	 *
	 */
	public void addSpacesTest() {
		TextLine textLine5 = TextLineTest.getTextLine(TextAnalyzerTest.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
	}

	@Test
	public void testFontSizeSetLine0() {
		TextLine textLine0 = TextLineTest.getTextLine(TextAnalyzerTest.PARA_SUSCRIPT_SVG, 0);
		Set<SvgPlusCoordinate> fontSizeSet = textLine0.getFontSizeSet();
		Assert.assertNotNull("line0 set", fontSizeSet);
		Assert.assertEquals("line0 size", 1, fontSizeSet.size());
		Assert.assertEquals("line0 fontSize", 7.07, fontSizeSet.iterator().next().getDouble(), 0.01);
	}

	@Test
	public void testFontSizeSetLine5() {
		TextLine textLine5 = TextLineTest.getTextLine(TextAnalyzerTest.PARA_SUSCRIPT_SVG, 5);
		Set<SvgPlusCoordinate> fontSizeSet = textLine5.getFontSizeSet();
		Assert.assertNotNull("line5 set", fontSizeSet);
		Assert.assertEquals("line5 size", 1, fontSizeSet.size());
		Assert.assertEquals("line5 fontSize", 9.465, fontSizeSet.iterator().next().getDouble(), 0.01);
	}

	// FIXTURES
	private static TextLine getTextLine(File file, int lineNumber) {
		TextAnalyzerX analyzerX = TextAnalyzerTest.createTextAnalyzerWithSortedLines(file);
		analyzerX.getLinesInIncreasingY();
		List<TextLine> textLines = analyzerX.getLinesInIncreasingY();
		TextLine textLine = textLines.get(lineNumber);
		return textLine;
	}


}
