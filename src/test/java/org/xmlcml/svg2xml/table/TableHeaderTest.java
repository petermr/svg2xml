package org.xmlcml.svg2xml.table;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.Phrase;

public class TableHeaderTest {

	private static final Logger LOG = Logger.getLogger(TableHeaderTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testReadHeader() {
		File inputFile = new File(Fixtures.TABLE_DIR, "LWW61463_TABLE1..image.g.2.9.svg");
		TableContentCreator tableContentCreator = new TableContentCreator(); //refresh to make sure
		HtmlHtml html = tableContentCreator.createHTMLFromSVG(inputFile);
		TableSection headerSection = tableContentCreator.getTableStructurer().getTableSectionList().get(1);
		LOG.debug("HEADER "+headerSection);
		List<Phrase> phrases = headerSection.getOrCreatePhrases();
		LOG.debug("PP"+phrases.size());
		for (HorizontalElement element : headerSection.getHorizontalElementList()) {
			if (element instanceof HorizontalRuler) {
				HorizontalRuler ruler = (HorizontalRuler) element;
				IntRange rulerRange = ruler.getIntRange();
				LOG.debug(">>"+rulerRange);
				for (Phrase phrase : phrases) {
//					LOG.debug("PY"+phrase.getY()+"/"+ruler.getY());
					if (rulerRange.includes(phrase.getIntRange()) && phrase.getY() > ruler.getY()) {
						LOG.debug("P: "+phrase.getStringValue());
					}
				}
			}
		}
	}
}
