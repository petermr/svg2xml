package org.xmlcml.svgplus.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import nu.xom.Node;

public class IncludeActionX extends AbstractActionX {

	private static final Logger LOG = Logger.getLogger(IncludeActionX.class);

	public final static String TAG = "include";
	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(FILENAME);
	}

	/** constructor
	 */
	public IncludeActionX() {
		super(TAG);
	}
	
	/** constructor
	 */
	public IncludeActionX(AbstractActionX element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new IncludeActionX(this);
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

	public void run() {
		throw new RuntimeException("Should never call run()");
	}
}
