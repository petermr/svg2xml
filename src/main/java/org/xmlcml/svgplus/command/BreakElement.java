package org.xmlcml.svgplus.command;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import nu.xom.Node;


public class BreakElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(BreakElement.class);
	
	public final static String TAG ="break";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionElement.ACTION);
	}

	/** constructor
	 */
	public BreakElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public BreakElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new BreakElement(this);
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
	protected AbstractAction createAction() {
		return new BreakAction(this);
	}

}
