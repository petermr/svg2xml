package org.xmlcml.svgplus.action;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;

/**
	<pageAction xpath="//svg:g[@LEAF='3']/svg:g" action="drawBoxes" 
	stroke="blue" strokeWidth="3" fill="cyan" opacity="0.3" />
 * @author pm286
 *
 */
public class PathNormalizerActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(PathNormalizerActionX.class);
	
	public final static double EPS = 0.001;
	
	public PathNormalizerActionX(AbstractActionX actionElement) {
		super(actionElement);
	}

	
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
	public PathNormalizerActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PathNormalizerActionX(this);
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
	public void run() {
		PathAnalyzerX pathAnalyzerX = getPageEditor().ensurePathAnalyzer();
		if (isTrue(PathNormalizerActionX.REMOVE_DUPLICATE_PATHS)) {
			pathAnalyzerX.removeDuplicatePaths();
		}
		if (isTrue(PathNormalizerActionX.CREATE_HIGHER_PRIMITIVES)) {
			pathAnalyzerX.removeRedundantMoveCommands();
			pathAnalyzerX.splitAtMoveCommands();
			pathAnalyzerX.interpretPathsAsRectCirclePolylineAndReplace();
		}
		// process min lines anyway
		Integer minLinesInPolyline = getMinLinesInPolyline();
		pathAnalyzerX.splitPolylinesToLines(minLinesInPolyline);
		
		if (isTrue(PathNormalizerActionX.JOIN_POLYLINES)) {
			throw new RuntimeException("refactor mergePolylinesAtContiguousEndPoints");
//			pathAnalyzerX.mergePolylinesAtContiguousEndPoints(EPS);
		}
		if (isTrue(PathNormalizerActionX.REMOVE_EMPTY_SVGG)) {
			getSVGPage().removeEmptySVGG();
		}
//		if (isTrue(PathNormalizerActionX.ENFORCE_VISIBILITY)) {
//			pathAnalyzer.enforceVisibility();
//		}
	}
	
	

	private Integer getMinLinesInPolyline() {
		return getInteger(PathNormalizerActionX.MIN_LINES_IN_POLY, 10);
	}

}
