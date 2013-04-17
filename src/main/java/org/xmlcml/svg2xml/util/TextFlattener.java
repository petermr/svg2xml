package org.xmlcml.svg2xml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

/** "flattens" numbers and dates in text to canonical forms.
 * Used for comparing strings which differ only be nunbers ands dates
 * e.g. 25, 33, are both flattened to 00
 * @author pm286
 *
 */
public class TextFlattener {

	private final static Logger LOG = Logger.getLogger(TextFlattener.class);
	
	public final static String META = "\\(){}[]?-+*|&^.$\"\'#";
	
	private Pattern integerPattern;

	private Matcher matcher;
	
	public TextFlattener() {
		
	}

//	public static List<Integer> extractIntegerList(Pattern pattern, String value) {
//		
//	}

	public Pattern createIntegerPattern(String s) {
		this.integerPattern = TextFlattener.createDigitStringMatchingPatternCapture(s);
		return integerPattern;
	}

	public List<Integer> captureIntegers(String s) {
		List<Integer> integerList = new ArrayList<Integer>();
		matcher = (s == null) ? null : integerPattern.matcher(s);
		if (matcher != null && matcher.matches()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				String g = matcher.group(i+1);
				Integer ii = new Integer(g);
				integerList.add(ii);
			}
		}
		return integerList;
	}


	@Test
	public void testCreateDigitStringMatchingPatternCapture() {
		String s = "3 - 45";
		Pattern pattern = TextFlattener.createDigitStringMatchingPatternCapture(s);
		Matcher matcher = pattern.matcher(s);
		Assert.assertEquals("pattern", "\\Q\\E(\\d+)\\Q - \\E(\\d+)\\Q\\E", pattern.toString());
		Assert.assertTrue("orig", matcher.matches());
		Assert.assertEquals("orig", 2, matcher.groupCount());
		Assert.assertEquals("group1", "3", matcher.group(1));
		Assert.assertEquals("group1", "45", matcher.group(2));
		matcher = pattern.matcher("34 - 67");
		Assert.assertTrue(matcher.matches());
		Assert.assertEquals("new", "67", matcher.group(2));
	}


	/** replaces all digits with zero
	 * e.g. "221B Baker Street" =>  "000B Baker Street"
	 * @param s
	 * @return
	 */
	public static String flattenDigits(String s) {
		return s == null ? null : s.replaceAll("\\d", "0");
	}
	
	/** replaces all integers (\d+) with zero
	 * e.g. "221B Baker Street" =>  "0B Baker Street"
	 * @param s
	 * @return
	 */
	public static String flattenDigitStrings(String s) {
		return s == null ? null : s.replaceAll("\\d+", "0");
	}
	
	/** replaces all signed integers with zero
	 * e.g. "a = -100" =>  "a = 0"
	 * @param s
	 * @return
	 */
	public static String flattenSignedIntegers(String s) {
		return s == null ? null : s.replaceAll("[\\-\\+]?\\d+", "0");
	}
	
/** replace all digits by \d+ in a pattern
 *  see http://stackoverflow.com/questions/16034337/generating-a-regular-expression-from-a-string#16034486
 *  thanks to @dasblinkenlight
 *  
 *  This would let an expression produced from Page 3 of 23 match strings like Page 13 of 23 and Page 6 of 8.
 *
 * String p = Pattern.quote(orig).replaceAll("\\d+", "\\\\\\\\d+");

 * This would produce "Page \\d+ of \\d+" no matter what page numbers and counts were there originally.
 * @param ss
 * @return
 */
	public static Pattern createDigitStringMatchingPattern(String ss) {
        return createPattern(ss, "\\d+", "\\\\E\\\\d+\\\\Q");
	}

	public static Pattern createDigitStringMatchingPatternCapture(String ss) {
        return createPattern(ss, "\\d+", "\\\\E"+"("+"\\\\d+"+")"+"\\\\Q");
	}

	/** replace all digits by \d in a pattern
	 *  see http://stackoverflow.com/questions/16034337/generating-a-regular-expression-from-a-string#16034486
	 *  thanks to @dasblinkenlight
	 *  
	 *  This would let an expression produced from Page 3 of 23 match strings like Page 8 of 17 
	 *  but not Page 6 of 8.
	 *
	 * String p = Pattern.quote(orig).replaceAll("\\d", "\\\\\\\\d");

	 * This would produce "Page \\d+ of \\d+" no matter what page numbers and counts were there originally.
	 * @param ss
	 * @return
	 */
	public static Pattern createDigitMatchingPattern(String ss) {
		return createPattern(ss, "\\d", "\\\\\\\\d");
	}

	/**
	 * quotes metacharacters
	 * @param s
	 * @return
	 */
	public static String quoteMetaCharacters(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (META.indexOf(ch) != -1) {
				sb.append("\\");
			}
			sb.append(ch);
		}
		return sb.toString();
	}
	
	
	private static Pattern createPattern(String ss, String regexIn, String regexOut) {
		Pattern pp = null;
		if (ss != null && regexIn != null && regexOut != null) {
			String p = (ss == null) ? null : Pattern.quote(ss).replaceAll(regexIn, regexOut);
			pp = Pattern.compile(p);
		}
		return pp;
	}

	/** NYI
	 * 
	 * @param s
	 * @return
	 */
	public static String flattenFloats(String s) {
		return null;
	}

	public Pattern getIntegerPattern() {
		return integerPattern;
	}


}
