package org.xmlcml.svg2xml.text;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

public class PhraseTest {

	private TextLine BERICHT_PAGE6_34_TEXTLINE = null;

	@Before
	public void setup() {
		TextStructurer BERICHT_PAGE6_TXTSTR = 
				TextStructurer.createTextStructurerWithSortedLines(Fixtures.BERICHT_PAGE6_SVG);
		List<TextLine> BERICHT_PAGE6_TEXT_LINES = BERICHT_PAGE6_TXTSTR.getLinesInIncreasingY();
		BERICHT_PAGE6_34_TEXTLINE = BERICHT_PAGE6_TEXT_LINES.get(34);
	}

	@Test
	public void testPhraseList() {
		TextLine textLine = TextStructurer.createTextLine(Fixtures.RAWWORDS_SVG, 0);
		List<Phrase> phraseList = textLine.createPhraseList();
		Assert.assertEquals("phraseList", 1, phraseList.size());
		Assert.assertEquals("phrase", "Phenotypic tarsus (mm)", phraseList.get(0).getPrintableString());
		Assert.assertEquals("phrase", "{Phenotypic tarsus (mm)}", phraseList.get(0).toString());
	}
	
	@Test
	public void testPhraseList1() {
		TextLine textLine = BERICHT_PAGE6_34_TEXTLINE;
		List<Phrase> phraseList = textLine.createPhraseList();
		Assert.assertEquals("phraseList", 5, phraseList.size());
		Assert.assertEquals("phrase", "Total Topf 1", phraseList.get(0).getPrintableString());
		Assert.assertEquals("phrase", "{Total Topf 1}", phraseList.get(0).toString());
		Assert.assertEquals("phrase1", "231", phraseList.get(1).getPrintableString());
		Assert.assertEquals("phrase2", "343", phraseList.get(2).getPrintableString());
		Assert.assertEquals("phrase3", "453", phraseList.get(3).getPrintableString());
		Assert.assertEquals("phrase4", "491", phraseList.get(4).getPrintableString());
	}
	

}
