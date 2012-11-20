package org.xmlcml.svgplus.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.page.PageAnalyzerElement;

public class DocumentActionElement extends AbstractActionElement {

	private static final Logger LOG = Logger.getLogger(DocumentActionElement.class);
	
	public final static String TAG ="documentAction";

	private static final String FILE = "file";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(ACTION);  // is this needed?
		ATTNAMES.add(FILE);
		ATTNAMES.add(FILENAME);
	}
	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	private AbstractActionElement pageActionList;
	/** constructor
	 */
	public DocumentActionElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public DocumentActionElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentActionElement(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public AbstractActionElement getPageActionList() {
		if (pageActionList == null) {
			Nodes nodes = this.query(PageAnalyzerElement.TAG);
			pageActionList =  (nodes.size() == 1) ? (PageAnalyzerElement) nodes.get(0) : null;
		}
		return pageActionList;
	}

	private void readPages(DocumentActionElement documentActionElement) {
	}

	private void selectPages(DocumentActionElement documentActionElement) {
		LOG.warn("selectPages NYI");
	}

	public String getActionValue() {
		return this.getAttributeValue(ACTION);
	}
	
	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
				ACTION,
		});
	}

}
