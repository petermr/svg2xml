package org.xmlcml.svg2xml.text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGText;

public class SuscriptEditorTest {

	private static final Logger LOG = Logger.getLogger(SuscriptEditorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private PhraseListList H3OPLUS;
	private PhraseListList H3OPLUS_SO4;
	private PhraseList PLUS;
	private PhraseList H_SPACE_O;
	private PhraseList SUB_3;
	
	@Before
	public void setup() {
		createH3OPlus();
		H3OPLUS_SO4 = createH3OPlusSO4();
	}

	private PhraseListList createH3OPlus() {
		SVGText t1 = new SVGText(new Real2(10., 20.), "H");
		t1.setFontSize(8.0);
		Word w1 = new Word(t1);
		Phrase p1 = new Phrase(w1);

		SVGText t2 = new SVGText(new Real2(15., 23.), "3");
		t2.setFontSize(6.0);
		Word w2 = new Word(t2);
		Phrase p2 = new Phrase(w2);
		
		SVGText t3 = new SVGText(new Real2(20., 20.), "O");
		t3.setFontSize(8.0);
		Word w3 = new Word(t3);
		Phrase p3 = new Phrase(w3);
		
		SVGText t11 = new SVGText(new Real2(25., 16.), "+");
		t11.setFontSize(6.0);
		Word w11 = new Word(t11);
		Phrase p11 = new Phrase(w11);
		
		H3OPLUS = new PhraseListList();
		PLUS = new PhraseList();
		PLUS.add(p11);
		H3OPLUS.add(PLUS);
		
		PhraseList H_SPACE_O = new PhraseList();
		H_SPACE_O.add(p1);
		H_SPACE_O.add(p3);
		H3OPLUS.add(H_SPACE_O);
		SUB_3 = new PhraseList();
		SUB_3.add(p2);
		H3OPLUS.add(SUB_3);
		return H3OPLUS;
	}

	private PhraseListList createH3OPlusSO4() {
		PhraseListList phraseListList = new PhraseListList(createH3OPlus());
		PhraseList sup = new PhraseList();
		phraseListList.add(sup);
		PhraseList mainx = new PhraseList();
		phraseListList.add(mainx);
		PhraseList sub = new PhraseList();
		phraseListList.add(sub);
		
		SVGText t1 = new SVGText(new Real2(10., 40.), "S");
		t1.setFontSize(8.0);
		mainx.add(new Phrase(new Word(t1)));

		SVGText t2 = new SVGText(new Real2(15., 40.), "O");
		t2.setFontSize(6.0);
		mainx.add(new Phrase(new Word(t2)));		
		
		SVGText tSub = new SVGText(new Real2(20., 43.), "4");
		tSub.setFontSize(6.0);
		sub.add(new Phrase(new Word(tSub)));		
		
		SVGText tSup = new SVGText(new Real2(25., 36.), "2-");
		tSup.setFontSize(6.0);
		sup.add(new Phrase(new Word(tSup)));		
		
		return phraseListList;
				
	}

	@Test
	public void testSuscriptLocal() {
		
		SuscriptEditor suscriptEditor = new SuscriptEditor(H3OPLUS);
		LOG.trace(0 + " ?? "+1);
		PhraseList phraseList0 = H3OPLUS.get(0);
		PhraseList phraseList1 = H3OPLUS.get(1);
		PhraseList phraseList01 = suscriptEditor.mergeSuscripts(SusType.SUPER, phraseList0, phraseList1);
		Assert.assertNotNull("01 not null", phraseList01);
		Assert.assertEquals("H O^{+}", phraseList01.toString());
		Assert.assertEquals("H O^{+}", phraseList01.getStringValue());
		LOG.trace(1 + " ?? "+2);
		phraseList0 = H3OPLUS.get(1);
		phraseList1 = H3OPLUS.get(2);
		PhraseList phraseList12 = suscriptEditor.mergeSuscripts(SusType.SUB, phraseList0, phraseList1);
		Assert.assertNotNull("12 not null", phraseList12);
		Assert.assertEquals("H_{3} O", phraseList12.toString());
		Assert.assertEquals("H_{3} O", phraseList12.getStringValue());

		PhraseList phraseList012 = suscriptEditor.mergeSuscripts(SusType.SUPER, PLUS, phraseList12);
		Assert.assertEquals("H_{3} O^{+}", phraseList012.toString());
		Assert.assertEquals("H_{3} O^{+}", phraseList012.getStringValue());
		Assert.assertEquals(""
			+ "<g xmlns=\"http://www.w3.org/2000/svg\" class=\"phraseList\" string-value=\"H_{3} O^{+}\">"
			+ "<g class=\"phrase\" string-value=\"H\"><g class=\"word\"><text stroke=\"none\" x=\"10.0\" y=\"20.0\" font-size=\"8.0\">H</text></g></g>"
			+ "<g class=\"phrase\" subscript=\"true\" string-value=\"_{3}\"><g class=\"word\"><text stroke=\"none\" x=\"15.0\" y=\"23.0\" font-size=\"6.0\">3</text></g></g>"
			+ "<g class=\"phrase\" string-value=\"O\"><g class=\"word\"><text stroke=\"none\" x=\"20.0\" y=\"20.0\" font-size=\"8.0\">O</text></g></g>"
			+ "<g class=\"phrase\" string-value=\"+\" superscript=\"true\"><g class=\"word\"><text stroke=\"none\" x=\"25.0\" y=\"16.0\" font-size=\"6.0\">+</text></g></g>"
			+ "</g>"
		, phraseList012.toXML());
	}

	@Test
	public void testSuscriptAll() {
		
		SuscriptEditor suscriptEditor = new SuscriptEditor(H3OPLUS);
		PhraseListList phraseListList = suscriptEditor.mergeAll();
		suscriptEditor.mergeAll();
		LOG.trace(">pp>"+phraseListList);
		
	}

	@Test
	public void testSuscriptAll1() {
		
		SuscriptEditor suscriptEditor = new SuscriptEditor(H3OPLUS_SO4);
		PhraseListList phraseListList = suscriptEditor.mergeAll();
		suscriptEditor.mergeAll();
		LOG.trace(">pp>"+phraseListList);
		
	}


}
