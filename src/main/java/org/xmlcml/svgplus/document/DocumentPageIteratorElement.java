package org.xmlcml.svgplus.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.page.PageActionElement;

public class DocumentPageIteratorElement extends AbstractActionElement {

	public final static String TAG ="pageIterator";
	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(PageActionElement.PAGE_RANGE);
	}
	
	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	/** constructor
	 */
	public DocumentPageIteratorElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public DocumentPageIteratorElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentPageIteratorElement(this);
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
		return new DocumentPageIteratorAction(this);
	}
}
