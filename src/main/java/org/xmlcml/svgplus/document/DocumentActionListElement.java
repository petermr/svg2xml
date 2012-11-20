package org.xmlcml.svgplus.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.svgplus.core.AbstractAction;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.core.DocumentAnalyzer;
import org.xmlcml.svgplus.core.SemanticDocumentElement;
import org.xmlcml.svgplus.page.PageActionElement;

public class DocumentActionListElement extends AbstractActionElement {

	public final static String TAG ="documentActionList";

	private static final String FINAL = "final";
	
	private List<AbstractActionElement> documentActionCommands;
	private DocumentActionListAction documentActionListAction;
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	static {
		ATTNAMES.add(PageActionElement.SKIP_IF_EXISTS);
		ATTNAMES.add(OUT_DIR);
	}
	
	/** constructor
	 */
	public DocumentActionListElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
		this.documentActionListAction = new DocumentActionListAction(this);
	}
	
	public DocumentActionListAction getDocumentActionListAction() {
		return documentActionListAction;
	}

	/** constructor
	 */
	public DocumentActionListElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentActionListElement(this);
    }

	

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public List<AbstractActionElement> getDocumentActionCommandElements() {
		if (documentActionCommands == null) {
			Nodes nodes = this.query("*");
			documentActionCommands =  new ArrayList<AbstractActionElement>();
			for (int i = 0; i < nodes.size(); i++) {
				documentActionCommands.add((AbstractActionElement)nodes.get(i));
			}
		}
		return documentActionCommands;
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
		return new DocumentActionListAction(this);
	}

}
