package org.xmlcml.svgplus.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.core.SemanticDocumentElement;

public class DocumentIteratorElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(DocumentIteratorElement.class);
	
	public final static String TAG ="documentIterator";

	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(FILENAME);
		ATTNAMES.add(FORMAT);
		ATTNAMES.add(MAX);
		ATTNAMES.add(REGEX);
		ATTNAMES.add(SKIP_IF_EXISTS);
	}

//	private SemanticDocumentElement semanticDocumentElement;
//	private DocumentIteratorAction documentIteratorAction;

	/** constructor
	 */
	public DocumentIteratorElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public DocumentIteratorElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentIteratorElement(this);
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
		return new DocumentIteratorAction(this);
	}
}
