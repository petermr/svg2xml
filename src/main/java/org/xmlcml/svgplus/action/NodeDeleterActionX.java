package org.xmlcml.svgplus.action;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.command.NodeDeleterElement;
import org.xmlcml.svgplus.command.PageActionElement;

public class NodeDeleterActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(NodeDeleterActionX.class);
	
	public NodeDeleterActionX(AbstractActionX actionElement) {
		super(actionElement);
	}

	public final static String TAG ="deleteNodes";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.PAGE_RANGE);
		ATTNAMES.add(PageActionElement.TITLE);
		ATTNAMES.add(PageActionElement.XPATH);
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
				AbstractActionElement.XPATH,
		});
	}

	@Override
	public void run() {
		deleteNodes(getXPath());
	}

}
