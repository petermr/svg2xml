package org.xmlcml.svgplus.action;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;

public class BreakActionX extends AbstractActionX {

	private final static Logger LOG = Logger.getLogger(BreakActionX.class);

	public final static String TAG ="break";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionElement.ACTION);
	}

	/** constructor
	 */
	public BreakActionX() {
		super(TAG);
	}
	
	public BreakActionX(AbstractActionX actionX) {
		super(actionX);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new BreakActionX(this);
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
		throw new RuntimeException("BREAK");
	}


}
