package org.xmlcml.svgplus.command;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;

public class DocumentReaderElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(DocumentReaderElement.class);

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

	@Override
	protected AbstractAction createAction() {
		return new DocumentReaderAction(this);
	}
}
