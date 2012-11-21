package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class ElementStylerElement extends AbstractActionElement {

	public final static String TAG ="styler";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.FILL);
		ATTNAMES.add(PageActionElement.OPACITY);
		ATTNAMES.add(PageActionElement.STROKE_WIDTH);
		ATTNAMES.add(PageActionElement.STROKE);
		ATTNAMES.add(PageActionElement.TITLE);
		ATTNAMES.add(PageActionElement.XPATH);
	}

	/** constructor
	 */
	public ElementStylerElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public ElementStylerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new ElementStylerElement(this);
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
				AbstractActionElement.XPATH,
		});
	}

	@Override
	protected AbstractAction createAction() {
		return new ElementStylerAction(this);
	}

}
