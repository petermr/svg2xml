package org.xmlcml.svgplus.command;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;


public class DebugElement extends AbstractActionElement {

	public final static String TAG ="debug";

	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionElement.ACTION);
	}

	/** constructor
	 */
	public DebugElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public DebugElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DebugElement(this);
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
		return new DebugAction(this);
	}
}
