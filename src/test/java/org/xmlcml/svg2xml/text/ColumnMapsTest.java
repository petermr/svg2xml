package org.xmlcml.svg2xml.text;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

public class ColumnMapsTest {

	@Test
	public void testTextLine() {
		TextLine textLine = Fixtures.BERICHT_PAGE6_34_TEXTLINE;
		Assert.assertEquals("textline", "chars: 24 Y: 536.4 fontSize: 10.193 >>Total Topf 1231343453491", textLine.toString());
		RawWords rawWords = textLine.getRawWords();
		Assert.assertEquals("raw", 5, rawWords.size());
		Assert.assertEquals("word0", "Total Topf 1", rawWords.get(0).toString());
		Assert.assertEquals("word1", "231", rawWords.get(1).toString());
		Assert.assertEquals("word2", "343", rawWords.get(2).toString());
		Assert.assertEquals("word3", "453", rawWords.get(3).toString());
		Assert.assertEquals("word4", "491", rawWords.get(4).toString());
	}
	
}
