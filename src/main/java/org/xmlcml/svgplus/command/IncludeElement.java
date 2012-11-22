package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import nu.xom.Node;

public class IncludeElement extends AbstractActionElement {

	private static final Logger LOG = Logger.getLogger(IncludeElement.class);

	public final static String TAG = "include";
	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(FILENAME);
	}

	/** constructor
	 */
	public IncludeElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public IncludeElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new IncludeElement(this);
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
				FILENAME,
		});
	}

	@Override
	protected AbstractAction createAction() {
		return null;
	}

}
