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
public class FigureCaption extends FigureComponent {

	private final static Logger LOG = Logger.getLogger(FigureCaption.class);
	private ChunkAnalyzer figureAnalyzer;
	
	public FigureCaption(FigureAnalyzer figureAnalyzer) {
		super(figureAnalyzer);
	}
	public FigureCaption(TextAnalyzer textAnalyzer, ShapeAnalyzer pathAnalyzer, ImageAnalyzer imageAnalyzer)  {
		super(textAnalyzer, pathAnalyzer, imageAnalyzer);
	}

	public void processCaptionText(HtmlDiv div) {
		LOG.trace(svgContainer.getChildCount());
		List<SVGText> characters = SVGText.extractTexts(svgContainer);
		TextAnalyzer textAnalyzer1 = new TextAnalyzer(characters, pageAnalyzer);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(characters, textAnalyzer1);
		div.appendChild(textStructurer.createHtmlElement());
	}


}
