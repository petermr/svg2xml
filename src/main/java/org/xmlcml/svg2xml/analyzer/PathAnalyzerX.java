package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.MovePrimitive;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPathPrimitive;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.StyleBundle;
import org.xmlcml.svg2xml.action.PageEditorX;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.tools.Chunk;

/**
 * Tries to interpret svg:path as
 * 
 * svg:rect
 * svg:poly
 * svg:circle
 * svg:symbol/svg:use
 * svg:marker
 * svg:text (single characters)
 * 
 * VERY heuristic
 * omit clipPath children
 * 
 * @author pm286
 *
 */
public class PathAnalyzerX extends AbstractPageAnalyzerX {

	private static final String NONE = "none";
	private static final String DEFAULT_STROKE = "gray";

	public final static Logger LOG = Logger.getLogger(PathAnalyzerX.class);

	private static final double _CIRCLE_EPS = 0.7;
	private static final double RECT_EPS = 0.01;
	private static final double MOVE_EPS = 0.001;
	private static final Double DEFAULT_MARGIN_X = 5.0;
	private static final Double DEFAULT_MARGIN_Y = 5.0;

	private SVGPolygon polygon;
	
	private boolean removeDuplicatePaths = true;
	private boolean removeRedundantMoveCommands = true;
	private boolean splitAtMoveCommands = true;
	private Integer minLinesInPolyline = 8;
	private List<SVGPath> pathList;

	public PathAnalyzerX() {
	}

	public PathAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}

	/** runs components having set true/false flags if required
	 * 
	 */
	public void runAnalyses(List<SVGPath> pathList) {
		this.pathList = pathList;
		this.removeDuplicatePaths();
		this.removeRedundantMoveCommands();
		this.splitAtMoveCommands();
		this.interpretPathsAsRectCirclePolylineAndReplace();
		this.splitPolylinesToLines(minLinesInPolyline);
		getSVGPage().removeEmptySVGG();
	}

	public void splitAtMoveCommands() {
		if (this.splitAtMoveCommands ) {
			 for (SVGPath path : pathList) {
				 splitAtMoveCommands(path);
			 }
		}
	}

	private void splitAtMoveCommands(SVGPath path) {
		 List<SVGPath> splitPaths = new ArrayList<SVGPath>();
		 String d = path.getDString();
		 List<String> dd = splitAtMoveCommands(d);
		 if (dd.size() == 1) {
			 splitPaths.add(path);
		 } else {
			 ParentNode parent = path.getParent();
			 int index = parent.indexOf(path);
			 for (String d0 : dd) {
				 SVGPath newPath = new SVGPath();
				 CMLUtil.copyAttributes(path, newPath);
				 newPath.setDString(d0);
				 parent.insertChild(newPath, ++index);
				 splitPaths.add(newPath);
			 }
			 path.detach();
		 }
	}

	private List<String> splitAtMoveCommands(String d) {
		List<String> strings = new ArrayList<String>();
		int current = -1;
		while (true) {
			int i = d.indexOf(SVGPathPrimitive.ABS_MOVE, current+1);
			if (i == -1 && current >= 0) {
				strings.add(d.substring(current));
				break;
			}
			if (i > current+1) {
				strings.add(d.substring(current, i));
			}
			current = i;
		}
		return strings;
	}

//	private void formatClipPaths() {
//		List<SVGElement> clipPaths = SVGUtil.getQuerySVGElements(getSVGPage(), ".//svg:clipPath/svg:path");
//		for (SVGElement clipPath : clipPaths) {
//			clipPath.format(PageEditor.DECIMAL_PLACES);
//		}
//	}
	
//	/** not yet implemented
//	 * 
//	 */
//	private void identifyAndRemoveBoxedChunksAndTidy() {
//		List<SVGElement> chunkElements = SVGUtil.getQuerySVGElements(getSVGPage(), ".//svg:g[@LEAF and [svg:path | svg:line | svg:rect ");
//		
//	}


	/** with help from
http://stackoverflow.com/questions/4958161/determine-the-centre-center-of-a-circle-using-multiple-points
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	public static SVGCircle findCircleFrom3Points(Real2 p1, Real2 p2, Real2 p3, Double eps) {
		SVGCircle circle = null;
		if (p1 != null && p2 != null && p3 != null) {
			Double d2 = p2.x * p2.x + p2.y * p2.y;
			Double bc = (p1.x * p1.x + p1.y * p1.y - d2) / 2;
			Double cd = (d2 - p3.x * p3.x - p3.y * p3.y) / 2;
			Double det = (p1.x - p2.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p2.y);
			if (Math.abs(det) > eps) {
				Real2 center = new Real2(
						(bc * (p2.y - p3.y) - cd * (p1.y - p2.y)) / det,
						((p1.x - p2.x) * cd - (p2.x - p3.x) * bc) / det);
				Double rad = center.getDistance(p1);
				circle = new SVGCircle(center, rad);
			}
		}
		return circle;
	}

	/** main routine?
	 * 
	 * @param pathList
	 */
	public void interpretPathsAsRectCirclePolylineAndReplace() {
		int id = 0;
		for (SVGPath path : pathList) {
			SVGElement newSVGElement = null;
			
			SVGRect rect = path.createRectangle(RECT_EPS);
			if (rect != null) {
				LOG.trace("R1"+rect);
				createRect(path, rect, id);
				newSVGElement = rect;
			}
			newSVGElement = createCircleIfPossible(id, path, newSVGElement);
			SVGPolyline polyline = path.createPolyline();
			if (polyline != null) {
				newSVGElement = polyline;
				polyline.setId("polyline"+id);
				LOG.trace("created polyline with lines: "+polyline.getLineList().size());
				polyline.format(PageEditorX.DECIMAL_PLACES);
				boolean duplicate = polyline.removeDuplicateLines();
				if (duplicate) {
					LOG.trace("polyline has duplicate lines");
				}
				SVGLine line = polyline.createSingleLine();
				if (line != null) {
					line.setId("line"+id);
					LOG.trace("created line");
					line.format(PageEditorX.DECIMAL_PLACES);
					replace(path, line);
					newSVGElement = line;
				} else {
					polygon = polyline.createPolygon(RECT_EPS);
					if (polygon != null) {
						newSVGElement = polygon;
						polygon.setId("polygon"+id);
						if (polygon.size() == 4) {
							rect = polyline.createRect(RECT_EPS);
							if (rect != null) {
								createRect(path, rect, id);
								rect.setTitle("was_polyline");
								newSVGElement = rect;
							} else {
								polygon.format(PageEditorX.DECIMAL_PLACES);
								replace(path, polygon);
							}
						} else {
							polygon.format(PageEditorX.DECIMAL_PLACES);
							replace(path, polygon);
						}
					} else {
						replace(path, polyline);
					}
				}
			}
			if (newSVGElement != null) {
				copyAttributes(path, newSVGElement);
//				PageNormalizerAction.removeCSSStyleAndExpandAsSeparateAttributes(newSVGElement);
			}
			id++;
		}
	}

	private SVGElement createCircleIfPossible(int id, SVGPath path, SVGElement newSVGElement) {
		SVGCircle circle = path.createCircle(_CIRCLE_EPS);
		if (circle != null) {
			LOG.trace("created circle");
			circle.format(PageEditorX.DECIMAL_PLACES);
			circle.setId("circle"+id);
			replace(path, circle);
			newSVGElement = circle;
		}
		return newSVGElement;
	}

	public static void copyAttributes(SVGPath path, SVGElement result) {
		for (String attName : new String[]{
				StyleBundle.FILL, 
				StyleBundle.OPACITY, 
				StyleBundle.STROKE, 
				StyleBundle.STROKE_WIDTH, 
				}) {
			String val = path.getAttributeValue(attName);
			if (val != null) {
				result.addAttribute(new Attribute(attName, val));
			}
		}
	}

	private void createRect(SVGPath path, SVGRect rect, int id) {
		LOG.trace("created rect: "+rect);
		rect.format(PageEditorX.DECIMAL_PLACES);
		rect.setId("rect"+id);
		replace(path, rect);
	}
	
	private void replace(SVGPath path, SVGElement newElem) {
		if (path != null && newElem != null) {
			newElem.detach();
			ParentNode parent = path.getParent();
			if (parent != null) {
				parent.replaceChild(path,  newElem);
//				newElem.addAttribute(new Attribute(Chunk.CHUNK_STYLE, "fromPath"));
			}
		}
	}

	private Real2Range restoreBoundingBox(Chunk chunk) {
		String bboxS = chunk.getAttributeValue("boundingBox", CMLConstants.CMLX_NS);
		Real2Range bbox = Real2Range.createFrom(bboxS);
		return bbox;
	}

	public List<SVGElement> getBoxOutsideMargins(Chunk chunk, Double marginX, Double marginY) {
		chunk.createElementListAndCalculateBoundingBoxes();
		marginX = (marginX == null) ? DEFAULT_MARGIN_X : marginX;
		marginY = (marginY == null) ? DEFAULT_MARGIN_Y : marginY;
		Real2Range boundingBox = chunk.getBoundingBox();
		Real2[] corners = boundingBox.getCorners();
		Real2Range innerBox = new Real2Range(
			new RealRange(corners[0].getX()+marginX, corners[1].getX()-marginX),
			new RealRange(corners[0].getY()+marginY, corners[1].getY()-marginY)
		);
		LOG.trace("InnerBox "+ innerBox);
		List<SVGElement> svgElementList = new ArrayList<SVGElement>();
		for (SVGElement svgElement : chunk.getDescendantSVGElementListWithoutDefsDescendants()) {
			String id = svgElement.getId();
			if (svgElement instanceof SVGPolyline) {
//				svgElement.debug("POLY");
			}
			Real2Range elementBox = svgElement.getBoundingBox();
			Real2Range overlap = innerBox.intersectionWith(elementBox);
			// delete if might be box element
			if (Real2Range.isNull(overlap)) {
				if (svgElement instanceof SVGPolyline ||
					svgElement instanceof SVGPath ||
					svgElement instanceof SVGLine ||
					svgElement instanceof SVGRect 
					) {
					LOG.trace("added for delete "+svgElement.toXML());
					svgElementList.add(svgElement);
				}
			} else {
			}
		}
		return svgElementList;
	}

	/** its seems many paths are drawn twice
	 * if their paths are equal, remove the later one(s)
	 */
	public void removeDuplicatePaths() {
		if (this.removeDuplicatePaths && pathList != null) {
			Set<String> dStringSet = new HashSet<String>();
			int count = 0;
			for (SVGPath path : pathList) {
				String dString = path.getDString();
				if (dStringSet.contains(dString)) {
					LOG.trace("detached a duplicate path "+dString);
					path.detach();
					count++;
				} else {
					dStringSet.add(dString);
				}
			}
			if (count > 0) {
				LOG.trace("detached "+count+" duplicate paths");
			}
		}
	}

	public List<SVGLine> splitPolylinesToLines(Integer minLinesInPolyline) {
		LOG.trace("minLines: "+minLinesInPolyline);
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		List<SVGElement> polylineList = SVGUtil.getQuerySVGElements(getSVGPage(), ".//svg:polyline");
		for (SVGElement polyline : polylineList) {
			List<SVGLine> lines = ((SVGPolyline)polyline).createLineList();
			if (lines.size() < minLinesInPolyline) {
				ParentNode parent = polyline.getParent();
				for (int i = 0; i < lines.size(); i++) {
					SVGLine line = lines.get(i);
//					line.addAttribute((Attribute)polyline.getAttribute(StyleManager.PHYSICAL_STYLE).copy());
					parent.appendChild(line);
					line.setId(line.getId()+"."+i);
					lineList.add(line);
				}
				polyline.detach();
				LOG.trace("split: "+lines.size());
			} else {
				LOG.trace("not split: "+lines.size());
			}
		}
		return lineList;
	}

	/** modifies the paths
	 * 
	 * @param pathList
	 */
	public void removeRedundantMoveCommands() {
		if (this.removeRedundantMoveCommands ) {
//			List<SVGElement> paths = SVGUtil.getQuerySVGElements(getSVGPage(), ".//svg:*[not(self::svg:clipPath)]/svg:path");
			for (SVGPath path : pathList) {
				removeRedundantMoveCommands(path, MOVE_EPS);
			}
		}
	}

	/** modifies the path
	 * 
	 * @param path
	 * @param eps
	 */
	private void removeRedundantMoveCommands(SVGPath path, double eps) {
		String d = path.getDString();
		if (d != null) {
			List<SVGPathPrimitive> newPrimitives = new ArrayList<SVGPathPrimitive>();
			List<SVGPathPrimitive> primitives = SVGPathPrimitive.parseDString(d);
			int primitiveCount = primitives.size();
			SVGPathPrimitive lastPrimitive = null;
			for (int i = 0; i < primitives.size(); i++) {
				SVGPathPrimitive currentPrimitive = primitives.get(i);
				boolean skip = false;
				if (currentPrimitive instanceof MovePrimitive) {
					if (i == primitives.size() -1) { // final primitive
						skip = true;
					} else if (lastPrimitive != null) {
						// move is to end of last primitive
						Real2 lastLastCoord = lastPrimitive.getLastCoord();
						Real2 currentFirstCoord = currentPrimitive.getFirstCoord();
						skip = (lastLastCoord != null) && lastLastCoord.isEqualTo(currentFirstCoord, eps);
					}
					if (!skip && lastPrimitive != null) {
						SVGPathPrimitive nextPrimitive = primitives.get(i+1);
						Real2 currentLastCoord = currentPrimitive.getLastCoord();
						Real2 nextFirstCoord = nextPrimitive.getFirstCoord();
						skip = (nextFirstCoord != null) && currentLastCoord.isEqualTo(nextFirstCoord, eps);
					}
				}
				if (!skip) {
					newPrimitives.add(currentPrimitive);
				} else {
					LOG.trace("skipped "+lastPrimitive+ "== "+currentPrimitive);
				}
				lastPrimitive = currentPrimitive;
			}
			int newPrimitiveCount = newPrimitives.size();
			if (newPrimitiveCount != primitiveCount) {
				LOG.trace("Deleted "+(primitiveCount - newPrimitiveCount)+" redundant moves");
				String newD = SVGPath.constructDString(newPrimitives);
				SVGPath newPath = new SVGPath(newD);
				CMLUtil.copyAttributes(path,  newPath);
				newPath.setDString(newD);
				path.getParent().replaceChild(path,  newPath);
				LOG.trace(">>>"+d+"\n>>>"+newD);
			}
		}
	}

//	public void enforceVisibility() {
//		List<SVGElement> elements = SVGUtil.getQuerySVGElements(
//				getSVGPage(), "//svg:path | //svg:polygon | //svg:line | //svg:polyline | //svg:circle | //svg:rect");
//		for (SVGElement element : elements) {
//			String stroke = element.getStroke();
//			String fill = element.getFill();
//			if ((stroke == null || NONE.equals(stroke)) && (fill == null || NONE.equals(fill))) {
//				element.setStroke(DEFAULT_STROKE);
//			}
//		}
//	}

	public static SVGCircle findCircleFromPoints(Real2Array r2a, double eps) {
		SVGCircle circle = null;
		if (r2a == null || r2a.size() < 3) {
			//
		} else if (r2a.size() == 3) {
			circle = findCircleFrom3Points(r2a.get(0), r2a.get(1), r2a.get(2), eps);
		} else {
			RealArray x2y2Array = new RealArray();
			RealArray xArray = new RealArray();
			RealArray yArray = new RealArray();
			for (int i = 0; i < r2a.size(); i++) {
				Real2 point = r2a.get(i);
				double x = point.x;
				double y = point.y;
				x2y2Array.addElement(x * x + y * y);
				xArray.addElement(x);
				yArray.addElement(y);
			}
			Real2Range bbox =r2a.getRange2();
			// check if scatter in both directions
			if (bbox.getXRange().getRange() > eps && bbox.getYRange().getRange() > eps) {
				// don't lnow the distribution and can't afford to find all triplets
				// so find the extreme points
				Real2 minXPoint = r2a.getPointWithMinimumX();
				Real2 maxXPoint = r2a.getPointWithMaximumX();
				Real2 minYPoint = r2a.getPointWithMinimumY();
				Real2 maxYPoint = r2a.getPointWithMaximumY();
			}
		}
		return circle;
	}
	
	public boolean isRemoveDuplicatePaths() {
		return removeDuplicatePaths;
	}

	public void setRemoveDuplicatePaths(boolean removeDuplicatePaths) {
		this.removeDuplicatePaths = removeDuplicatePaths;
	}

	public boolean isRemoveRedundantMoveCommands() {
		return removeRedundantMoveCommands;
	}

	public void setRemoveRedundantMoveCommands(boolean removeRedundantMoveCommands) {
		this.removeRedundantMoveCommands = removeRedundantMoveCommands;
	}

	public boolean isSplitAtMoveCommands() {
		return splitAtMoveCommands;
	}

	public void setSplitAtMoveCommands(boolean splitAtMoveCommands) {
		this.splitAtMoveCommands = splitAtMoveCommands;
	}

	public Integer getMinLinesInPolyline() {
		return minLinesInPolyline;
	}

	public void setMinLinesInPolyline(Integer minLinesInPolyline) {
		this.minLinesInPolyline = minLinesInPolyline;
	}

	
}
