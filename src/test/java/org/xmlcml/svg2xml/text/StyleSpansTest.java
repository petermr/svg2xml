package org.xmlcml.svg2xml.text;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.svg2xml.SVG2XMLFixtures;
import org.xmlcml.svg2xml.container.ScriptContainerOLD;
import org.xmlcml.svg2xml.page.PageAnalyzer;

public class StyleSpansTest {

	@Test
	public void testStyleSpans0() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
		ScriptContainerOLD scriptContainer = (ScriptContainerOLD) pageAnalyzer.getAbstractContainerList().get(0);
		List<ScriptLineOLD> scriptLineList = scriptContainer.getScriptLineList();
		ScriptLineOLD scriptLine0 = scriptLineList.get(0);
		StyleSpansOLD styleSpans = scriptLine0.getStyleSpans();
		Assert.assertEquals("line0", "Hiwatashi et al. BMC Evolutionary Biology 2011, 11:312", styleSpans.getTextContentWithSpaces());
		Assert.assertEquals("line0spans", 7, styleSpans.size());
		Assert.assertEquals("stylespans content", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Hiwatashi <i>et al</i>. <i>BMC Evolutionary Biology </i>2011, <b>11</b>:312</span>",
				styleSpans.createHtmlElement().toXML());
		Assert.assertEquals("stylespan size", 7.97, styleSpans.get(0).getFontSize(), StyleSpansOLD.EPS);
		Assert.assertEquals("stylespans size", 7.97, styleSpans.getFontSize(), StyleSpansOLD.EPS);
	}
	
	@Test
	public void testStyleSpans1() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
		ScriptContainerOLD scriptContainer = (ScriptContainerOLD) pageAnalyzer.getAbstractContainerList().get(1);
		List<ScriptLineOLD> scriptLineList = scriptContainer.getScriptLineList();
		ScriptLineOLD scriptLine0 = scriptLineList.get(0);
		StyleSpansOLD styleSpans = scriptLine0.getStyleSpans();
		Assert.assertEquals("line0", "Page 2 of 14", styleSpans.getTextContentWithSpaces());
		Assert.assertEquals("line0spans", 1, styleSpans.size());
		Assert.assertEquals("stylespans content", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Page 2 of 14</span>",
				styleSpans.createHtmlElement().toXML());
		Assert.assertEquals("stylespan size", 7.97, styleSpans.get(0).getFontSize(), StyleSpansOLD.EPS);
		Assert.assertEquals("stylespans size", 7.97, styleSpans.getFontSize(), StyleSpansOLD.EPS);
	}
	
	@Test
	public void testStyleSpans0a() {
		StyleSpansOLD styleSpans = getStyleSpans(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2, 0, 0);
		checkStyleSpans("0 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Hiwatashi <i>et al</i>. <i>BMC Evolutionary Biology </i>2011, <b>11</b>:312</span>",
				7.97, styleSpans);
	}
	
	@Test
	public void testStyleSpansSubcript() {
		StyleSpansOLD styleSpans = getStyleSpans(SVG2XMLFixtures.RAW_MATH311_SVG_PAGE2, 6, 5);
		checkStyleSpans("6 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">collection of <i>N </i>data sets <i>d</i><sub><i>1</i></sub><i>, d</i><sub><i>2</i></sub><i>, ..., d</i><sub><i>N </i></sub>(e.g. phylogenies of</span>",
				9.763, styleSpans);
	}
	
	@Test
	public void testStyleSpansSuperscript() {
		StyleSpansOLD styleSpans = getStyleSpans(SVG2XMLFixtures.RAW_GEO310_SVG_PAGE8, 3, 26);
		checkStyleSpans("6 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">erations and sampled every 1000<sup>th </sup>generation. The</span>",
				9.763, styleSpans);
	}
	
	//===================================================

	public static void checkStyleSpans(String msg, String html, double fontSize, StyleSpansOLD styleSpans) {
		Assert.assertEquals(msg+" fontSize", fontSize, styleSpans.getFontSize(), StyleSpansOLD.EPS);
		Assert.assertEquals(msg+" html", html, styleSpans.createHtmlElement().toXML());
	}

	public static StyleSpansOLD getStyleSpans(File svgPage, int chunk, int line) {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(svgPage);
		StyleSpansOLD styleSpans = ((ScriptContainerOLD) pageAnalyzer.getAbstractContainerList().get(chunk))
				.getScriptLineList().
				get(line).
				getStyleSpans();
		return styleSpans;
	}

}
