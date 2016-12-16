package org.xmlcml.svg2xml.text;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.page.PageAnalyzer;

public class WordTest {

	public static Word WORD0 = RawWordsTest.RAW_WORDS.get(0);
	public static Word WORD1 = RawWordsTest.RAW_WORDS.get(1);
	public static Word WORD2 = RawWordsTest.RAW_WORDS.get(2);
	
	SVGElement word1 = TextLineTest.PAGE_TEXT_LINE.getRawWords().get(1);

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
	
	@Test
	public void testSplitWords() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(
						Fixtures.RAWWORDS_SVG, (PageAnalyzer) null);
		List<RawWords> rawWordsList = textStructurer.createRawWordsListFromTextLineList();
		Assert.assertEquals("rawWordsList", 1, rawWordsList.size());
		RawWords rawWords = rawWordsList.get(0);
		Assert.assertEquals("rawWords", 1, rawWords.size());
		Word word = rawWords.get(0);
		Assert.assertEquals("unsplit value", "Phenotypic tarsus (mm)", word.toString());
		List<Word> splitWords = word.splitAtSpaces();
		Assert.assertEquals("rawWords", 3, splitWords.size());
		Assert.assertEquals("word0", "Phenotypic", splitWords.get(0).toString());
		Assert.assertEquals("word1", "tarsus", splitWords.get(1).toString());
		Assert.assertEquals("word2", "(mm)", splitWords.get(2).toString());
	}

}
