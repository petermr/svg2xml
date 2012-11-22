package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class BoxProcessorElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(BoxProcessorElement.class);
	
	public final static String TAG ="boxProcessor";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.BOX_COUNT);
		ATTNAMES.add(PageActionElement.MARGIN_X);
		ATTNAMES.add(PageActionElement.MARGIN_Y);
		ATTNAMES.add(PageActionElement.TITLE);
		ATTNAMES.add(PageActionElement.XPATH);
	}

	/** constructor
	 */
	public BoxProcessorElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public BoxProcessorElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new BoxProcessorElement(this);
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
			PageActionElement.BOX_COUNT,
			PageActionElement.MARGIN_X,
			PageActionElement.MARGIN_Y,
		});
	}

	@Override
	protected AbstractAction createAction() {
		return new BoxProcessorAction(this);
	}

}
