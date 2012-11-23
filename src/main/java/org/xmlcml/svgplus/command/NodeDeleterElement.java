package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;


public class NodeDeleterElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(NodeDeleterElement.class);
	
	public final static String TAG ="deleteNodes";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.PAGE_RANGE);
		ATTNAMES.add(PageActionElement.TITLE);
		ATTNAMES.add(PageActionElement.XPATH);
	}

	/** constructor
	 */
	public NodeDeleterElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public NodeDeleterElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new NodeDeleterElement(this);
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
		return new NodeDeleterAction(this);
	}

}
