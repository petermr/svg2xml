package org.xmlcml.svgplus.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.page.PageActionElement;

public class DocumentReaderElement extends AbstractActionElement {

	public final static String TAG ="documentReader";

	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(PageActionElement.FILENAME);
		ATTNAMES.add(PageActionElement.FORMAT);
	}

	/** constructor
	 */
	public DocumentReaderElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public DocumentReaderElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentReaderElement(this);
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
//				AbstractActionElement.FILENAME,
		});
	}

}
