package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class PathAnalyzerElement extends AbstractActionElement {

	public final static String TAG ="variableExtractor";
	
	/** attribute names
	 * 
	 */

	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(FILENAME);
	}

	/** constructor
	 */
	public PathAnalyzerElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public PathAnalyzerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PathAnalyzerElement(this);
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
		});
	}

	@Override
	protected AbstractAction createAction() {
		return null;
//		return new PathAnalyzerAction(this);
	}

}
