package org.xmlcml.svgplus.control.document;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.control.CommandElement;
import org.xmlcml.svgplus.control.page.PageAnalyzerElement;

public class DocumentActionElement extends AbstractActionElement {

	private final static Logger LOGGER = Logger.getLogger(DocumentActionElement.class);
	
	public final static String TAG ="documentAction";

	/** attribute names
	 * 
	 */
	public static final String ACTION = "action";
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
	public DocumentActionElement(CommandElement element) {
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
		LOGGER.warn("selectPages NYI");
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
