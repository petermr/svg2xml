package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;


public class VariableElement extends AbstractActionElement {

	private static final Logger LOG = Logger.getLogger(VariableElement.class);

	public final static String TAG ="variable";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.LOGAT);
		ATTNAMES.add(PageActionElement.NAME);
		ATTNAMES.add(PageActionElement.VALUE);
	}

	/** constructor
	 */
	public VariableElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public VariableElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new VariableElement(this);
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
				AbstractActionElement.NAME,
				PageActionElement.VALUE,
		});
	}
	
	@Override
	protected AbstractAction createAction() {
		return new VariableAction(this);
	}
}
