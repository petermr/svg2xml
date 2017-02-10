package org.xmlcml.svg2xml.text;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

public class SubSuperscriptTest {

	private static final Logger LOG = Logger.getLogger(SubSuperscriptTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testSuperscripts() {
		File inputFile = new File(Fixtures.TEXT_DIR, "superscript.svg");
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		PhraseListList phraseListList = textStructurer.getPhraseListList();
		phraseListList.applySubAndSuperscripts();
		LOG.trace(phraseListList.getStringValue());
	}

	@Test
	public void testSubscripts() {
		File inputFile = new File(Fixtures.TEXT_DIR, "subscript.svg");
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		PhraseListList phraseListList = textStructurer.getPhraseListList();
		phraseListList.applySubAndSuperscripts();
		LOG.trace(phraseListList.getStringValue());
	}
}
