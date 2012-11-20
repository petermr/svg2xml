package org.xmlcml.svgplus.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.core.AbstractAction;
import org.xmlcml.svgplus.core.AbstractActionElement;

public class PageIteratorElement extends AbstractActionElement {

	public final static String TAG ="pageIterator";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	public static final String REPORTED_PAGE_COUNT = "reportedPageCount";
	
	static {
//		ATTNAMES.add(PageActionElement.PAGE_RANGE);
	}
	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	/** constructor
	 */
	public PageIteratorElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public PageIteratorElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageIteratorElement(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}

	@Override
	protected AbstractAction createAction() {
		return null;
//		return new PageIteratorAction(this);
	}
}
