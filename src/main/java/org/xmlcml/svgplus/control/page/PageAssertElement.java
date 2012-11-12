package org.xmlcml.svgplus.control.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.control.CommandElement;

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
	public PageAssertElement(CommandElement element) {
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

}
