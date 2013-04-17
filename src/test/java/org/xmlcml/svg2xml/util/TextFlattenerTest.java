package org.xmlcml.svg2xml.util;

import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

public class TextFlattenerTest {

	@Test
	public void testFlattenDigitsNull() {
		String s = null;
		Assert.assertNull("digits", TextFlattener.flattenDigits(s));
	}

	@Test
	public void testFlattenDigits() {
		String s = "Page 3 of 45";
		Assert.assertEquals("digits", "Page 0 of 00", TextFlattener.flattenDigits(s));
	}

	@Test
	public void testFlattenIntegersNull() {
		String s = null;
		Assert.assertNull("digits", TextFlattener.flattenDigitStrings(s));
	}

	@Test
	public void testFlattenDigitStrings() {
		String s = "Page 3 of 45";
		Assert.assertEquals("integer", "Page 0 of 0", TextFlattener.flattenDigitStrings(s));
	}

	@Test
	public void testFlattenDigitStringsNoMinus() {
		String s = "Page 3-45";
		Assert.assertEquals("integer", "Page 0-0", TextFlattener.flattenDigitStrings(s));
	}

	@Test
	public void testFlattenSignedIntegers() {
		String s = "3 -45";
		Assert.assertEquals("integer", "0 0", TextFlattener.flattenSignedIntegers(s));
	}

	@Test
	public void testFlattenSignedIntegers1() {
		String s = "3-45";
		Assert.assertEquals("integer", "00", TextFlattener.flattenSignedIntegers(s));
	}

	@Test
	public void testPatternQuote() {
		String s = "3 - 45";
		String ss = Pattern.quote(s);
		Assert.assertEquals("quote", "\\Q3 - 45\\E", ss);
	}

	@Test
	public void testCreateDigitStringMatchingPattern() {
		String s = "3 - 45";
		Pattern pattern = TextFlattener.createDigitStringMatchingPattern(s);
		Assert.assertEquals("pattern", "\\Q\\E\\d+\\Q - \\E\\d+\\Q\\E", pattern.toString());
		Assert.assertTrue("orig", pattern.matcher(s).matches());
		Assert.assertTrue("new", pattern.matcher("34 - 67").matches());
		Assert.assertFalse("new", pattern.matcher("34- 67").matches());
	}

	@Test
	public void testCreateDigitStringMatchingPatternCapture() {
		TextFlattener textFlattener = new TextFlattener();
		textFlattener.createIntegerPattern("3 - 45");
		List<Integer> integerList = textFlattener.captureIntegers("27 - 45");
		System.out.println(">> "+integerList);
	}

	@Test
	@Ignore
	public void testFlattenFloats() {
		String s = "3.0 -33.";
		Assert.assertEquals("float", "0.0 0.0", TextFlattener.flattenFloats(s));
	}
	
}
