package org.xmlcml.svgplus.control.page;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.text.TextAnalyzer;

public class TextChunkerAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(TextChunkerAction.class);
	
	public TextChunkerAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	@Override
	public void run() {
		TextAnalyzer textAnalyzer = pageAnalyzer.ensureTextAnalyzer();
		String xpath = getXPath();
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), xpath);
		LOG.trace(xpath+" => "+elements.size());
		textAnalyzer.setCreateTSpans(isTrue(TextChunkerElement.CREATE_TSPANS));
		textAnalyzer.setCreateHTML(isTrue(TextChunkerElement.CREATE_HTML));
		if (isTrue(TextChunkerElement.CREATE_WORDS_LINES_PARAS_SUB_SUP)) {
			textAnalyzer.analyzeTextChunksCreateWordsLinesParasAndSubSup(elements);
		}
		if (isTrue(TextChunkerElement.CREATE_WORDS_LINES)) {
			textAnalyzer.analyzeSingleWordsOrLines(elements);
		}
		
		if (isTrue(TextChunkerElement.MERGE_CHUNKS)) {
			textAnalyzer.mergeChunks();
		}
	}

}
