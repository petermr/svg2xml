package org.xmlcml.svgplus.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.command.PageActionElement;
import org.xmlcml.svgplus.command.TextChunkerElement;
import org.xmlcml.svgplus.text.TextAnalyzer;

public class TextChunkerActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(TextChunkerActionX.class);
	
	public TextChunkerActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	
	
	public final static String TAG ="textChunker";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static String CREATE_HTML = "createHTML";
	static String CREATE_TSPANS = "createTSpans";
	static String CREATE_WORDS_LINES = "createWordsLines";
	static String CREATE_WORDS_LINES_PARAS_SUB_SUP = "createWordsLinesParasAndSubSup";
	static String MERGE_CHUNKS = "mergeChunks";

	static {
		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.TITLE);
		ATTNAMES.add(PageActionElement.XPATH);
		ATTNAMES.add(CREATE_WORDS_LINES_PARAS_SUB_SUP);
		ATTNAMES.add(CREATE_WORDS_LINES);
		ATTNAMES.add(CREATE_TSPANS);
		ATTNAMES.add(CREATE_HTML);
		ATTNAMES.add(MERGE_CHUNKS);
	}

	/** constructor
	 */
	public TextChunkerActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new TextChunkerActionX(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
				AbstractActionElement.XPATH,
		});
	}
	
	@Override
	public void run() {
		TextAnalyzer textAnalyzer = getPageEditor().ensureTextAnalyzer();
		String xpath = getXPath();
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), xpath);
		LOG.trace(xpath+" => "+elements.size());
		textAnalyzer.setCreateTSpans(isTrue(TextChunkerActionX.CREATE_TSPANS));
		textAnalyzer.setCreateHTML(isTrue(TextChunkerActionX.CREATE_HTML));
		if (isTrue(TextChunkerActionX.CREATE_WORDS_LINES_PARAS_SUB_SUP)) {
			textAnalyzer.analyzeTextChunksCreateWordsLinesParasAndSubSup(elements);
		}
		if (isTrue(TextChunkerActionX.CREATE_WORDS_LINES)) {
			textAnalyzer.analyzeSingleWordsOrLines(elements);
		}
		
		if (isTrue(TextChunkerActionX.MERGE_CHUNKS)) {
			textAnalyzer.mergeChunks();
		}
	}

}
