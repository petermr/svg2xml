package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.core.AbstractActionElement;


public class WhitespaceChunkerElement extends AbstractActionElement {

	public final static String TAG ="whitespaceChunker";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.DEPTH);
	}

	/** constructor
	 */
	public WhitespaceChunkerElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public WhitespaceChunkerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new WhitespaceChunkerElement(this);
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
		return null;
	}


}
