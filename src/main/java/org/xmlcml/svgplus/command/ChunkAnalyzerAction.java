package org.xmlcml.svgplus.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.text.TextAnalyzer;

public class ChunkAnalyzerAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(ChunkAnalyzerAction.class);
	
	private boolean subSup;
	private boolean removeNumericTSpans;
	private boolean splitAtSpaces;
	
	public ChunkAnalyzerAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		String xpath = getXPath();
		if (xpath != null) {
			List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), xpath);
			LOG.debug("LEAFS "+elements.size());
			this.subSup = isTrue(ChunkAnalyzerElement.SUBSUP);
			this.splitAtSpaces = isTrue(ChunkAnalyzerElement.SPLIT_AT_SPACES);
			this.removeNumericTSpans = isTrue(ChunkAnalyzerElement.REMOVE_NUMERIC_TSPANS);

			for (SVGElement element : elements) {
				if (!(element instanceof SVGG)) {
					throw new RuntimeException("Must operate on <g> elements");
				}
				LOG.trace("*********************ELEMENT "+element.getId());
				analyzeChunk((SVGG)element);
			}
			debugFile("target/chunkAnalyzer1Axes.svg");
		}
	}
	
	private void analyzeChunk(SVGG svgg) {
		ChunkAnalyzer chunkAnalyzer = new ChunkAnalyzer(semanticDocumentAction);
		createTextAnalyzer(chunkAnalyzer);
		chunkAnalyzer.analyzeChunk(svgg);
	}

	private void createTextAnalyzer(ChunkAnalyzer chunkAnalyzer) {
		TextAnalyzer textAnalyzer = chunkAnalyzer.getTextAnalyzer();
		textAnalyzer.setSubSup(subSup);
		textAnalyzer.setRemoveNumericTSpans(removeNumericTSpans);
		textAnalyzer.setSplitAtSpaces(splitAtSpaces);
	}
	
}
