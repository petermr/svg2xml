package org.xmlcml.svg2xml.figure;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.svg2xml.page.ChunkAnalyzer;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.page.ImageAnalyzer;
import org.xmlcml.svg2xml.page.ShapeAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.text.TextStructurer;
public class NewCaption extends NewComponent {

	private final static Logger LOG = Logger.getLogger(NewCaption.class);
	private ChunkAnalyzer figureAnalyzer;
	
	public NewCaption(FigureAnalyzer figureAnalyzer) {
		super(figureAnalyzer);
	}
	public NewCaption(TextAnalyzer textAnalyzer, ShapeAnalyzer shapeAnalyzer, ImageAnalyzer imageAnalyzer)  {
		super(textAnalyzer, shapeAnalyzer, imageAnalyzer);
	}

	public void processCaptionText(HtmlDiv div) {
		LOG.trace(svgContainer.getChildCount());
		List<SVGText> characters = SVGText.extractSelfAndDescendantTexts(svgContainer);
		TextAnalyzer textAnalyzer1 = new TextAnalyzer(characters, pageAnalyzer);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(characters, textAnalyzer1);
		div.appendChild(textStructurer.createHtmlElement());
	}


}
