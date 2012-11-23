package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;

public class AssertElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(AssertElement.class);

	public final static String TAG ="assert";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.COUNT);
		ATTNAMES.add(PageActionElement.FAIL);
		ATTNAMES.add(PageActionElement.FILENAME);
		ATTNAMES.add(PageActionElement.NAME);
		ATTNAMES.add(PageActionElement.MESSAGE);
		ATTNAMES.add(PageActionElement.VALUE);
		ATTNAMES.add(PageActionElement.XPATH);
	}

	/** constructor
	 */
	public AssertElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public AssertElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new AssertElement(this);
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
		return new AssertAction(this);
	}
}
