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
		debugFile("target/pathNorm0.svg");
		PathAnalyzer pathAnalyzer = getPageEditor().ensurePathAnalyzer();
		if (isTrue(PathNormalizerElement.REMOVE_DUPLICATE_PATHS)) {
			pathAnalyzer.removeDuplicatePaths();
			debugFile("target/pathNorm1Duplicate.svg");
		}
		if (isTrue(PathNormalizerElement.CREATE_HIGHER_PRIMITIVES)) {
			pathAnalyzer.removeRedundantMoveCommands();
			pathAnalyzer.splitAtMoveCommands();
			pathAnalyzer.interpretPathsAsRectCirclePolylineAndReplace();
		}
		// process min lines anyway
		Integer minLinesInPolyline = getMinLinesInPolyline();
		pathAnalyzer.splitPolylinesToLines(minLinesInPolyline);
		
		debugFile("target/pathNorm2Polyline.svg");
		if (isTrue(PathNormalizerElement.JOIN_POLYLINES)) {
			pathAnalyzer.mergePolylinesAtContiguousEndPoints(EPS);
			debugFile("target/pathNorm3Merge.svg");
		}
		if (isTrue(PathNormalizerElement.REMOVE_EMPTY_SVGG)) {
			getSVGPage().removeEmptySVGG();
			debugFile("target/pathNorm4EmptySVG.svg");
		}
		if (isTrue(PathNormalizerElement.ENFORCE_VISIBILITY)) {
			pathAnalyzer.enforceVisibility();
			debugFile("target/enforceVisibility.svg");
		}
	}

	private Integer getMinLinesInPolyline() {
		return getInteger(PathNormalizerElement.MIN_LINES_IN_POLY, 10);
	}

}
