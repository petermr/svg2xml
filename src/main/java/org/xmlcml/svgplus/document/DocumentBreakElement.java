package org.xmlcml.svgplus.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.core.AbstractAction;
import org.xmlcml.svgplus.core.AbstractActionElement;

public class DocumentBreakElement extends AbstractActionElement {

	public final static String TAG ="break";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionElement.ACTION);
	}

	/** constructor
	 */
	public DocumentBreakElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public DocumentBreakElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentBreakElement(this);
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
		return new DocumentBreakAction(this);
	}

}
