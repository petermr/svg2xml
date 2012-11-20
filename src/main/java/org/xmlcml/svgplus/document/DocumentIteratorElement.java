package org.xmlcml.svgplus.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.core.SemanticDocumentElement;

public class DocumentIteratorElement extends AbstractActionElement {

	public final static String TAG ="documentIterator";

	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(FILENAME);
		ATTNAMES.add(FORMAT);
		ATTNAMES.add(MAX);
		ATTNAMES.add(REGEX);
		ATTNAMES.add(SKIP_IF_EXISTS);
	}

	private SemanticDocumentElement semanticDocumentElement;
	private DocumentActionListElement documentActionListElement;
	private DocumentIteratorAction documentIteratorAction;

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

	public DocumentIteratorAction getDocumentIteratorAction() {
		if (documentIteratorAction == null) {
			documentIteratorAction = new DocumentIteratorAction(this);
		}
		return documentIteratorAction;
	}

	public void setSemanticDocumentElement(SemanticDocumentElement commandFileElement) {
		this.semanticDocumentElement = commandFileElement;
	}

	public SemanticDocumentElement getCommandFileElement() {
		return semanticDocumentElement;
	}
	
	public DocumentActionListElement getDocumentActionListElement() {
		if (this.documentActionListElement == null) {
			Nodes nodes = this.query(DocumentActionListElement.TAG);
			documentActionListElement =  (nodes.size() == 1) ? (DocumentActionListElement) nodes.get(0) : null;
		}
		return documentActionListElement;
	}

}
