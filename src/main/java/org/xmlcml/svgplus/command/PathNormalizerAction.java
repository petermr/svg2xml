package org.xmlcml.svgplus.command;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.paths.PathAnalyzer;

/**
	<pageAction xpath="//svg:g[@LEAF='3']/svg:g" action="drawBoxes" 
	stroke="blue" strokeWidth="3" fill="cyan" opacity="0.3" />
 * @author pm286
 *
 */
public class PathNormalizerAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(PathNormalizerAction.class);
	
	public final static double EPS = 0.001;
	
	public PathNormalizerAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		PathAnalyzer pathAnalyzer = getPageEditor().ensurePathAnalyzer();
		if (isTrue(PathNormalizerElement.REMOVE_DUPLICATE_PATHS)) {
			pathAnalyzer.removeDuplicatePaths();
		}
		if (isTrue(PathNormalizerElement.CREATE_HIGHER_PRIMITIVES)) {
			pathAnalyzer.removeRedundantMoveCommands();
			pathAnalyzer.splitAtMoveCommands();
			pathAnalyzer.interpretPathsAsRectCirclePolylineAndReplace();
		}
		// process min lines anyway
		Integer minLinesInPolyline = getMinLinesInPolyline();
		pathAnalyzer.splitPolylinesToLines(minLinesInPolyline);
		
		if (isTrue(PathNormalizerElement.JOIN_POLYLINES)) {
			throw new RuntimeException("refactor mergePolylinesAtContiguousEndPoints");
//			pathAnalyzer.mergePolylinesAtContiguousEndPoints(EPS);
		}
		if (isTrue(PathNormalizerElement.REMOVE_EMPTY_SVGG)) {
			getSVGPage().removeEmptySVGG();
		}
//		if (isTrue(PathNormalizerElement.ENFORCE_VISIBILITY)) {
//			pathAnalyzer.enforceVisibility();
//		}
	}
	
	

	private Integer getMinLinesInPolyline() {
		return getInteger(PathNormalizerElement.MIN_LINES_IN_POLY, 10);
	}

}
