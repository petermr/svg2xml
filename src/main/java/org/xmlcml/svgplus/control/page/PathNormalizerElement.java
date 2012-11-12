package org.xmlcml.svgplus.control.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.control.CommandElement;


public class PathNormalizerElement extends AbstractActionElement {

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
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public PathNormalizerElement(CommandElement element) {
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


}
