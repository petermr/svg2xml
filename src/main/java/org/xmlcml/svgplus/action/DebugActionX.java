package org.xmlcml.svgplus.action;


import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;

public class DebugActionX extends DocumentActionX {

	private final static Logger LOG = Logger.getLogger(DebugActionX.class);
	
	public DebugActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	
	
	public final static String TAG ="debug";

	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionElement.ACTION);
	}

	/** constructor
	 */
	public DebugActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DebugActionX(this);
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
		});
	}
	
	@Override
	public void run() {
		LOG.debug(semanticDocumentActionX.getDebugString());
	}

}
