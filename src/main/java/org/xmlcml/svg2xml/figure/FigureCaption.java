package org.xmlcml.svg2xml.figure;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.svg2xml.page.ChunkAnalyzer;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.page.ImageAnalyzer;
import org.xmlcml.svg2xml.page.ShapeAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzerOLD;
import org.xmlcml.svg2xml.text.TextStructurerOLD;
public class FigureCaption extends FigureComponent {

	private final static Logger LOG = Logger.getLogger(FigureCaption.class);
	private ChunkAnalyzer figureAnalyzer;
	
	public FigureCaption(FigureAnalyzer figureAnalyzer) {
		super(figureAnalyzer);
	}
	public FigureCaption(TextAnalyzerOLD textAnalyzer, ShapeAnalyzer shapeAnalyzer, ImageAnalyzer imageAnalyzer)  {
		super(textAnalyzer, shapeAnalyzer, imageAnalyzer);
	}

	public void processCaptionText(HtmlDiv div) {
		LOG.trace(svgContainer.getChildCount());
		List<SVGText> characters = SVGText.extractSelfAndDescendantTexts(svgContainer);
		TextAnalyzerOLD textAnalyzer1 = new TextAnalyzerOLD(characters, pageAnalyzer);
		TextStructurerOLD textStructurer = TextStructurerOLD.createTextStructurerWithSortedLines(characters, textAnalyzer1);
		div.appendChild(textStructurer.createHtmlElement());
	}


}
