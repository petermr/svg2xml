package org.xmlcml.svgplus.document;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.page.PageActionElement;

public class PageIteratorElement extends AbstractActionElement {

	private static final Logger LOG = Logger.getLogger(PageIteratorElement.class);
	
	public final static String TAG ="pageIterator";		

	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(PageActionElement.PAGE_RANGE);
		ATTNAMES.add(PageActionElement.MAX_MBYTE);
		ATTNAMES.add(PageActionElement.TIMEOUT);
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
		return new PageIteratorAction(this);
	}

}
