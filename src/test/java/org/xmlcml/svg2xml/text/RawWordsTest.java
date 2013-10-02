package org.xmlcml.svg2xml.text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.svg2xml.Fixtures;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class RawWordsTest {

	public static RawWords RAW_WORDS = TextLineTest.PAGE_TEXT_LINE.getRawWords();
	public static RawWords RAW_WORDS1 = TextLineTest.PAGE_TEXT_LINE1.getRawWords();
	public static List<TextLine> DK_LIST = TextLine.createSortedTextLineList(
			CMLUtil.parseQuietlyToDocument(Fixtures.DK_PAGE1_SVG).getRootElement());


	@Test
	public void testGetRawWords() {
		Assert.assertEquals("rawWords", 4, RAW_WORDS.size());
	}
	
	@Test
	public void testGetEndX() {
		Assert.assertEquals("word end", 538.593, (double) RAW_WORDS.getEndX(), 0.001);
	}

	@Test
	public void testGetMidX() {
		Assert.assertEquals("word mid", 517.009, (double) RAW_WORDS.getMidX(), 0.001);
	}

	@Test
	public void testGetStartX() {
		Assert.assertEquals("word start", 495.426, (double) RAW_WORDS.getStartX(), 0.001);
	}


	@Test
	public void testGetInterWordWhitePixels() {
		RealArray separationArray = RAW_WORDS.getInterWordWhitePixels();
		separationArray.format(3);
		Assert.assertEquals("word separation", "(2.614,2.622,2.67)", separationArray.toString());
	}

	@Test
	public void testGetInterWordWhiteEnSpaces() {
		RealArray spaceCountArray = RAW_WORDS.getInterWordWhiteEnSpaces();
		spaceCountArray.format(3);
		Assert.assertEquals("word separation", "(0.596,0.598,0.609)", spaceCountArray.toString());
	}
	
	@Test
	public void testGetStartXArray() {
		RealArray startArray = RAW_WORDS1.getStartXArray();
		startArray.format(3);
		Assert.assertEquals("word start", "(82.484,147.669,212.854,278.039)", startArray.toString());
		RealArray deltaArray = startArray.calculateDifferences().format(3);
		Assert.assertEquals("subtract", "(65.185,65.185,65.185)", deltaArray.toString());

	}
	
	@Test
	public void testGetEndXArray() {
		RealArray endArray = RAW_WORDS1.getEndXArray();
		endArray.format(3);
		Assert.assertEquals("word end", "(90.764,155.949,221.135,286.32)", endArray.toString());
		RealArray deltaArray = endArray.calculateDifferences().format(3);
		Assert.assertEquals("subtract", "(65.185,65.186,65.185)", deltaArray.toString());
	}
	
	@Test
	public void testGetMidXArray() {
		RealArray midArray = RAW_WORDS1.getMidXArray();
		midArray.format(3);
		Assert.assertEquals("word start", "(86.624,151.809,216.994,282.179)", midArray.toString());
		RealArray deltaArray = midArray.calculateDifferences().format(3);
		Assert.assertEquals("subtract", "(65.185,65.185,65.185)", deltaArray.toString());
	}
	
	@Test
	public void testTranslateTolRealArray() {
		RealArray realArray = RAW_WORDS1.translateToRealArray().format(3);
		Assert.assertEquals("translate", "(16.0,17.0,18.0,19.0)", realArray.toString());
	}
	
	@Test
	public void testPageWithColumns() {
//		Assert.assertEquals("dk", 57, DK_LIST.size());
//		Multiset<Integer> startSet = WordList. getStartXIntSet(DK_LIST);
//		Assert.assertEquals("sets", 489, startSet.size());
//		Assert.assertEquals("sets", 24, startSet.entrySet().size());
//		List<Entry<Integer>> entryList = new ArrayList<Entry<Integer>>();
//		for (Entry<Integer> entry: startSet.entrySet()) {
//			entryList.add(entry);
//		}
//		for (Entry<Integer> entry: entryList) {
//			if (entry.getCount() <= 3) {
//				startSet.remove(entry.getElement());
//			}
//		}
//		Assert.assertEquals("sets", 11, startSet.entrySet().size());
//		System.out.println(startSet);
	}
}
