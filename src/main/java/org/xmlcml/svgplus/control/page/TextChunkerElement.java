package org.xmlcml.svgplus.control.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.control.CommandElement;


public class TextChunkerElement extends AbstractActionElement {

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
	public TextChunkerElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public TextChunkerElement(CommandElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new TextChunkerElement(this);
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


}
