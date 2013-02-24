package org.xmlcml.svg2xml.action;


import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;

public class NodeDeleterActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(NodeDeleterActionX.class);
	
	public NodeDeleterActionX(AbstractActionX actionElement) {
		super(actionElement);
	}

	public final static String TAG ="deleteNodes";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionX.PAGE_RANGE);
		ATTNAMES.add(AbstractActionX.TITLE);
		ATTNAMES.add(AbstractActionX.XPATH);
	}

	/** constructor
	 */
	public NodeDeleterActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new NodeDeleterActionX(this);
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
				AbstractActionX.XPATH,
		});
	}

	@Override
	public void run() {
		deleteNodes(getXPath());
	}

}
