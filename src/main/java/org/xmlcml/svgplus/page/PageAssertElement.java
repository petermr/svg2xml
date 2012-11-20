package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.core.AbstractAction;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;

public class PageAssertElement extends AbstractActionElement {

	public final static String TAG ="pageAssert";
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
	public PageAssertElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public PageAssertElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageAssertElement(this);
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
		return new PageAssertAction(this);
	}
}
