package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.core.AbstractAction;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class PageVariableElement extends AbstractActionElement {

	public final static String TAG ="pageVariable";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.LOG);
		ATTNAMES.add(PageActionElement.NAME);
		ATTNAMES.add(PageActionElement.VALUE);
	}

	/** constructor
	 */
	public PageVariableElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public PageVariableElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageVariableElement(this);
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
				AbstractActionElement.NAME,
				PageActionElement.VALUE,
		});
	}
	
	@Override
	protected AbstractAction createAction() {
		return new PageVariableAction(this);
	}
}
