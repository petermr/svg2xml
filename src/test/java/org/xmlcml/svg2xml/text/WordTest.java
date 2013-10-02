package org.xmlcml.svg2xml.text;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGText;

public class WordTest {

	public static Word WORD0 = RawWordsTest.RAW_WORDS.get(0);
	public static Word WORD1 = RawWordsTest.RAW_WORDS.get(1);
	public static Word WORD2 = RawWordsTest.RAW_WORDS.get(2);
	
	Word word1 = TextLineTest.PAGE_TEXT_LINE.getRawWords().get(1);

	@Test
	public void testGetWordAndSpaces() {
		Double spaces = Util.format(WORD0.getSpaceCountBetween(WORD1), 3);
		Assert.assertEquals("spaces", 0.596, spaces);
		spaces = Util.format(WORD0.getSpaceCountBetween(WORD2), 3);
		Assert.assertEquals("spaces", 2.122, spaces);
	}
	
	@Test
	public void testGetLength() {
		Assert.assertEquals("word length", 4, (int) WORD0.getCharacterCount());
	}

	@Test
	public void testGetXEnd() {
		Assert.assertEquals("word end", 511.861, (double) WORD0.getEndX(), 0.001);
	}

	@Test
	public void testGetXMid() {
		Assert.assertEquals("word mid", 503.6435, (double) WORD0.getMidX(), 0.001);
	}

	@Test
	public void testGetXStart() {
		Assert.assertEquals("word start", 495.426, (double) WORD0.getStartX(), 0.001);
	}

	@Test
	public void testGetDistance() {
		Assert.assertEquals("to word 1", 2.614, Util.format(WORD0.getSeparationBetween(WORD1), 3));
		Assert.assertEquals("to word 2", 9.301, Util.format(WORD0.getSeparationBetween(WORD2), 3));
	}
	
	@Test
	public void testTranslateToDouble() {
		Word word = Word.createTestWord(new SVGText(new Real2(1., 2.), "1.23"));
		Double d = word.translateToDouble();
		Assert.assertEquals("double", 1.23, d, 0.001);
	}

}
