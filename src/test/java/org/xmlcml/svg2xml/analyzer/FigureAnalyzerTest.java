package org.xmlcml.svg2xml.analyzer;

import junit.framework.Assert;
import nu.xom.Element;

import org.junit.Test;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;

public class FigureAnalyzerTest {

	@Test
	public void testMatchShort() {
		String s = "Fig. 1. foo";
		AbstractPageAnalyzerX figureAnalyzer = new FigureAnalyzerX((SemanticDocumentActionX)null);
		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.PATTERN, s);
		Assert.assertEquals("serial", new Integer(1), i);
	}
	
	@Test
	public void testMatchLong() {
		String s = "Figure 1. foo";
		AbstractPageAnalyzerX figureAnalyzer = new FigureAnalyzerX((SemanticDocumentActionX)null);
		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.PATTERN, s);
		Assert.assertEquals("serial", new Integer(1), i);
	}
	
	@Test
	public void testMatchNoSerial() {
		String s = "Figure foo";
		AbstractPageAnalyzerX figureAnalyzer = new FigureAnalyzerX((SemanticDocumentActionX)null);
		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.PATTERN, s);
		Assert.assertEquals("serial", new Integer(-1), i);
	}
	
	@Test
	public void testNoMatchNoSerial() {
		String s = "Fogure 2. foo";
		AbstractPageAnalyzerX figureAnalyzer = new FigureAnalyzerX((SemanticDocumentActionX)null);
		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.PATTERN, s);
		Assert.assertNull("serial", i);
	}
	
	
	@Test
	public void testNoMatchNoSerialHigh() {
		String s = "Figure 2. foo+(char)1643";
		AbstractPageAnalyzerX figureAnalyzer = new FigureAnalyzerX((SemanticDocumentActionX)null);
		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.PATTERN, s);
		Assert.assertEquals("serial", new Integer(2), i);
	}
	
	@Test
	public void testDingbat() {
		String s = "Figure 2. foo"+(char)10110+"bar";
		System.out.println(s);
		AbstractPageAnalyzerX figureAnalyzer = new FigureAnalyzerX((SemanticDocumentActionX)null);
		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.PATTERN, s);
		Assert.assertEquals("serial", new Integer(2), i);
	}
	
	
	@Test
	public void testDingbat1() {
		Element e = new Element("x");
		add("F", e);
		add("i", e);
		add("g", e);
		Element a = add(""+(char)10110, e);
		Element t = new Element("title");
		t.appendChild("foo");
		a.appendChild(t);
		add("u", e);
		add("r", e);
		add("e", e);
		System.out.println(e.getValue());
	}

	private Element add(String s, Element e) {
		Element text = new Element("text");
		text.appendChild(s);
		e.appendChild(text);
		return text;
	}

}
