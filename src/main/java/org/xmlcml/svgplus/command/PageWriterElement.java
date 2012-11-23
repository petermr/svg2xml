package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;


public class PageWriterElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(PageWriterElement.class);
	
	public final static String TAG ="pageWriter";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	public static String MAKE_DISPLAY = "makeDisplay";

	static {
		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.DELETE_XPATHS);
		ATTNAMES.add(PageActionElement.DELETE_NAMESPACES);
		ATTNAMES.add(PageActionElement.FILENAME);
		ATTNAMES.add(PageActionElement.FORMAT);
		ATTNAMES.add(PageActionElement.NAME);
		ATTNAMES.add(PageActionElement.XPATH);
		ATTNAMES.add(MAKE_DISPLAY);
	}

	/** constructor
	 */
	public PageWriterElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public PageWriterElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageWriterElement(this);
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
				AbstractActionElement.FILENAME,
		});
	}

	@Override
	protected AbstractAction createAction() {
		return new PageWriterAction(this);
	}

}
