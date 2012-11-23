package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;


public class VariableExtractorElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(VariableExtractorElement.class);
	
	public final static String TAG ="variableExtractor";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.ACTION);
		ATTNAMES.add(PageActionElement.PAGE_RANGE);
		ATTNAMES.add(PageActionElement.REGEX);
		ATTNAMES.add(PageActionElement.TITLE);
		ATTNAMES.add(PageActionElement.VARIABLES);
		ATTNAMES.add(PageActionElement.XPATH);
	}

	/** constructor
	 */
	public VariableExtractorElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public VariableExtractorElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new VariableExtractorElement(this);
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
				AbstractActionElement.REGEX,
				PageActionElement.VARIABLES,
				AbstractActionElement.XPATH,
		});
	}

	@Override
	protected AbstractAction createAction() {
		return new VariableExtractorAction(this);
	}

}
