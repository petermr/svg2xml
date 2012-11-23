package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;


public class ChunkAnalyzerElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(ChunkAnalyzerElement.class);
	
	public final static String TAG ="chunkAnalyzer";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	public static final String SUBSUP = "subSup";
	public static final String REMOVE_NUMERIC_TSPANS = "removeNumericTSpans";
	public static final String SPLIT_AT_SPACES = "splitAtSpaces";
	
	/** attribute names
	 * 
	 */

	static {
		ATTNAMES.add(PageActionElement.XPATH);
		ATTNAMES.add(SUBSUP);
		ATTNAMES.add(REMOVE_NUMERIC_TSPANS);
	}

	/** constructor
	 */
	public ChunkAnalyzerElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public ChunkAnalyzerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new ChunkAnalyzerElement(this);
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
		return new ChunkAnalyzerAction(this);
	}

}
