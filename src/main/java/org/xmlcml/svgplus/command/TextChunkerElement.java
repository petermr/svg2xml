package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;


public class TextChunkerElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(TextChunkerElement.class);
	
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
	}
	
	/** constructor
	 */
	public TextChunkerElement(AbstractActionElement element) {
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

	@Override
	protected AbstractAction createAction() {
		return new TextChunkerAction(this);
	}

}
