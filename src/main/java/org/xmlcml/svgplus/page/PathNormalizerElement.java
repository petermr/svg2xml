package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class PathNormalizerElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(PathNormalizerElement.class);
	
	public final static String TAG ="pathNormalizer";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static final String CREATE_HIGHER_PRIMITIVES = "createHigherPrimitives";
	static final String ENFORCE_VISIBILITY        = "enforceVisibility";
    static final String FIND_AXES                = "findAxes";
	static final String JOIN_POLYLINES           = "joinPolylines";
	static final String MIN_LINES_IN_POLY        = "minLinesInPolyline";
	static final String REMOVE_DUPLICATE_PATHS   = "removeDuplicatePaths";
	static final String REMOVE_EMPTY_SVGG        = "removeEmptySVGG";

	static {
		ATTNAMES.add(CREATE_HIGHER_PRIMITIVES);
		ATTNAMES.add(ENFORCE_VISIBILITY);
		ATTNAMES.add(FIND_AXES);
		ATTNAMES.add(JOIN_POLYLINES);
		ATTNAMES.add(MIN_LINES_IN_POLY);
		ATTNAMES.add(REMOVE_DUPLICATE_PATHS);
		ATTNAMES.add(REMOVE_EMPTY_SVGG);
	}

	/** constructor
	 */
	public PathNormalizerElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public PathNormalizerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PathNormalizerElement(this);
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
		return new PathNormalizerAction(this);
	}

}
