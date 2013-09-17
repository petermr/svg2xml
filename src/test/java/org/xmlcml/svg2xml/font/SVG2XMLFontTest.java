package org.xmlcml.svg2xml.font;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.Fixtures;

public class SVG2XMLFontTest {

	private final static Logger LOG = Logger.getLogger(SVG2XMLFontTest.class);
	
	@Test
	public void readCorpus() {
		SVGElement bmc = SVGElement.readAndCreateSVG(Fixtures.BMC_RUNNING_NORMAL_SVG);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(bmc);
		SVG2XMLFont font = new SVG2XMLFont("bmc.running");
		for (int i = 0; i < textList.size() - 1; i++) {
			SVGText svgText = textList.get(i);
			font.getOrCreateSVG2XMLCharacter(svgText);
		}
		LOG.debug(font.ensureSortedUnicodeList().size());
		font.debug("font");
	}

	@Test
	public void readCorpusAndWidths() {
		SVGElement bmc = SVGElement.readAndCreateSVG(Fixtures.BMC_RUNNING_NORMAL_SVG);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(bmc);
		SVG2XMLFont font = new SVG2XMLFont("bmc.running");
		font.addTextListAndGenerateSizes(textList);
//		LOG.debug(font.ensureSortedUnicodeList().size());
		font.debug("font");
	}
}
