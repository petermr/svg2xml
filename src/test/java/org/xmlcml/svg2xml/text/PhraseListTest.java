package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGText;

public class PhraseListTest {
	private static final Logger LOG = Logger.getLogger(PhraseListTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testCreatePhraseList() {
		SVGText t11 = new SVGText(new Real2(25., 16.), "+");
		t11.setFontSize(8.0);
		Word w11 = new Word(t11);
		Assert.assertEquals(""
				+ "<g xmlns=\"http://www.w3.org/2000/svg\" class=\"word\">"
				+   "<text stroke=\"none\" x=\"25.0\" y=\"16.0\" font-size=\"8.0\">+</text>"
				+ "</g>",  w11.toXML());
		
		Phrase p11 = new Phrase(w11);
		p11.getStringValue();
		Assert.assertEquals(""
				+ "<g xmlns=\"http://www.w3.org/2000/svg\" class=\"phrase\" string-value=\"+\">"
				+ "<g class=\"word\">"
				+ "<text stroke=\"none\" x=\"25.0\" y=\"16.0\" font-size=\"8.0\">+</text>"
				+ "</g>"
				+ "</g>",  p11.toXML());
		Assert.assertEquals("((25.0,31.359375),(8.0,16.0))", p11.getOrCreateBoundingBox().toString());
		List<Word> wordList = p11.getOrCreateWordList();
		Assert.assertEquals(1,  wordList.size());
		Word word = wordList.get(0);
		word.getStringValue();
		Assert.assertEquals(""
				+ "<g xmlns=\"http://www.w3.org/2000/svg\" class=\"word\" string-value=\"+\">"
				+   "<text stroke=\"none\" x=\"25.0\" y=\"16.0\" font-size=\"8.0\">+</text>"
				+ "</g>",  word.toXML());
		
		PhraseList phraseList11 = new PhraseList();
		phraseList11.add(p11);
		List<Phrase> phraseList = phraseList11.getOrCreateChildPhraseList();
		Assert.assertEquals(1,  phraseList.size());
		Phrase phrase = phraseList.get(0);
		Assert.assertEquals(""
				+ "<g xmlns=\"http://www.w3.org/2000/svg\" class=\"phrase\" string-value=\"+\">"
				+ "<g class=\"word\">"
				+ "<text stroke=\"none\" x=\"25.0\" y=\"16.0\" font-size=\"8.0\">+</text>"
				+ "</g>"
				+ "</g>",
				phrase.toXML());
		Assert.assertEquals("((25.0,31.359375),(8.0,16.0))", phraseList11.getOrCreateBoundingBox().toString());
	}

}
