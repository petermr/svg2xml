package org.xmlcml.svg2xml.text;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerTest;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class TextLineTest {

	@Test
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
	 *
	 */
	public void insertSpaceFactorTest() {

		TextLine textLine5 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());

		textLine5 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 5);
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

		TextLine textLine5 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
		textLine5 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
		textLine5.insertSpaces();
		Assert.assertEquals("default spaceFactor", "activation energy. Taking the natural logarithm of this equa-", textLine5.getLineContent());
	}

	private static void testScalefactor(double spaceFactor, int lineNumber, String expected) {
		TextLine textLine5;
		textLine5 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, lineNumber);
		textLine5.insertSpaces(spaceFactor);
		Assert.assertEquals("spaceFactor: "+spaceFactor, expected, textLine5.getLineContent());
	}

	@Test
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
	 *
	 */
	public void addSpacesTest() {
		TextLine textLine5 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
	}

	@Test
	public void testFontSizeSetLine0() {
		TextLine textLine0 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 0);
		Set<SvgPlusCoordinate> fontSizeSet = textLine0.getFontSizeSet();
		Assert.assertNotNull("line0 set", fontSizeSet);
		Assert.assertEquals("line0 size", 1, fontSizeSet.size());
		Assert.assertEquals("line0 fontSize", 7.07, fontSizeSet.iterator().next().getDouble(), 0.01);
	}

	@Test
	public void testFontSizeSetLine5() {
		TextLine textLine5 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 5);
		Set<SvgPlusCoordinate> fontSizeSet = textLine5.getFontSizeSet();
		Assert.assertNotNull("line5 set", fontSizeSet);
		Assert.assertEquals("line5 size", 1, fontSizeSet.size());
		Assert.assertEquals("line5 fontSize", 9.465, fontSizeSet.iterator().next().getDouble(), 0.01);
	}

	@Test
	public void testCreateHtmlLine1() {
		// no suscripts
		TextLine textLine2 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 1);
		HtmlElement element = textLine2.createHtmlLine();
		String ref = "" +
		"<p xmlns='http://www.w3.org/1999/xhtml'>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>The rate constant is 0.61795 mg L</span>" +
		"<sup>" +
		"<span style='font-size:7.074px;color:red;font-family:MTSYN;'>− </span>" +
		"<span style='font-size:7.074px;font-family:TimesNewRoman;'>1</span>" +
		"</sup>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>h</span>" +
		"<sup>" +
		"<span style='font-size:7.074px;color:red;font-family:MTSYN;'>− </span>" +
		"<span style='font-size:7.074px;font-family:TimesNewRoman;'>1</span>" +
		"</sup>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>.</span>" +
		"</p>";
		JumboTestUtils.assertEqualsIncludingFloat("htmlLine ", ref, element, true, 0.00001);
	}

	@Test
	public void testCreateHtmlLine2() {
		// no suscripts
		TextLine textLine2 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 2);
		HtmlElement element = textLine2.createHtmlLine();
		String ref = "" +
			"<p xmlns='http://www.w3.org/1999/xhtml'>"+
			"<span style='font-size:9.465px;font-family:TimesNewRoman;'>The temperature dependence of the rate constants is described</span>"+
			"</p>";
		JumboTestUtils.assertEqualsIncludingFloat("htmlLine ", ref, element, true, 0.00001);
	}
	
	@Test
	public void testCreateHtmlLine8() {
		// sub and super
		TextLine textLine8 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 8);
		HtmlElement element = textLine8.createHtmlLine();
		String ref = "" +
		"<p xmlns='http://www.w3.org/1999/xhtml'>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>130 and 200</span>" +
		"<sup>" +
		"<span style='font-size:7.074px;color:red;font-family:MTSYN;'>"+(char)9702+"</span>" +
		"</sup>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>C yields the results of </span>" +
		"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>k</span>" +
		"<sub>" +
		"<span style='font-size:7.074px;font-family:TimesNewRoman;'>0</span>" +
		"</sub>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>and </span>" +
		"<span style='font-size:9.465px;font-style:italic;font-family:TimesNewRoman;'>E</span>" +
		"<sub>" +
		"<span style='font-size:7.074px;font-family:TimesNewRoman;'>a</span>" +
		"</sub>" +
		"<span style='font-size:9.465px;font-family:TimesNewRoman;'>at higher tem-</span>" +
		"</p>";
		JumboTestUtils.assertEqualsIncludingFloat("htmlLine ", ref, element, true, 0.00001);
	}

	@Test
	public void testgetSimpleFontFamilyMultiset8() {
		TextLine textLine8 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 8);
		Multiset<String> fontFamilyMultiset = textLine8.getFontFamilyMultiset();
		Assert.assertNotNull("fontFamilyMultiset", fontFamilyMultiset);
		Assert.assertEquals("single", 45, fontFamilyMultiset.size());
		Assert.assertEquals("single", 1, fontFamilyMultiset.entrySet().size());
	}
	
	@Test
	public void testgetSimpleFontFamilyMultiset0() {
		TextLine textLine8 = TextLineTest.getTextLine(Fixtures.PARA_SUSCRIPT_SVG, 0);
		Multiset<String> fontFamilyMultiset = textLine8.getFontFamilyMultiset();
		Assert.assertNotNull("fontFamilyMultiset", fontFamilyMultiset);
		Assert.assertEquals("single", 4, fontFamilyMultiset.size());
		Set<Entry<String>> entrySet = fontFamilyMultiset.entrySet();
		Assert.assertEquals("single", 2, entrySet.size());
		Assert.assertEquals(2, fontFamilyMultiset.count("TimesNewRoman"));
		Assert.assertEquals(0, fontFamilyMultiset.count("Zark"));
		Assert.assertEquals(2, fontFamilyMultiset.count("MTSYN"));
		Iterator<Entry<String>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String> entry = iterator.next();
//			System.out.println(entry.getElement()+" "+entry.getCount());
		}
	}
	



// ==========================================================================
	
	// FIXTURES
	private static TextLine getTextLine(File file, int lineNumber) {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(file);
		textLineContainer.getLinesInIncreasingY();
		List<TextLine> textLines = textLineContainer.getLinesInIncreasingY();
		TextLine textLine = textLines.get(lineNumber);
		return textLine;
	}


}
