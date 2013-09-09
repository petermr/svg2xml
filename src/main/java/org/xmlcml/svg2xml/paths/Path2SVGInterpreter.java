package org.xmlcml.svg2xml.paths;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.graphics.svg.MovePrimitive;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPathPrimitive;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.StyleBundle;
import org.xmlcml.svg2xml.page.PageIO;

public class Path2SVGInterpreter {

	private final static Logger LOG = Logger.getLogger(Path2SVGInterpreter.class);
	
	private Integer minLinesInPolyline = 8;
	private SVGPolygon polygon;
	private boolean removeDuplicatePaths = true;
	private boolean removeRedundantMoveCommands = true;
	private List<SVGPath> pathList;

	private boolean splitAtMoveCommands = true;

	private SVGG svgChunk;

	private static final double _CIRCLE_EPS = 0.7;

	private static final double MOVE_EPS = 0.001;
	private static final double RECT_EPS = 0.01;
	
	public Path2SVGInterpreter(List<SVGPath> pathList) {
		this.pathList = pathList;
	}
	
	
	/** its seems many paths are drawn twice
	 * if their paths are equal, remove the later one(s)
	 */
	public void forceRemoveDuplicatePaths() {
		boolean saveDuplicatePaths = this.removeDuplicatePaths;
		this.removeDuplicatePaths = true;
		removeDuplicatePaths();
		this.removeDuplicatePaths = saveDuplicatePaths;
	}
	public Integer getMinLinesInPolyline() {
		return minLinesInPolyline;
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
				polyline.format(PageIO.DECIMAL_PLACES);
				boolean duplicate = polyline.removeDuplicateLines();
				if (duplicate) {
					LOG.trace("polyline has duplicate lines");
				}
				SVGLine line = polyline.createSingleLine();
				if (line != null) {
					line.setId("line"+id);
					LOG.trace("created line");
					line.format(PageIO.DECIMAL_PLACES);
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
								polygon.format(PageIO.DECIMAL_PLACES);
								replace(path, polygon);
							}
						} else {
							polygon.format(PageIO.DECIMAL_PLACES);
							replace(path, polygon);
						}
					} else {
						replace(path, polyline);
					}
				}
			}
			if (newSVGElement != null) {
				copyAttributes(path, newSVGElement);
			}
			id++;
		}
	}
	public boolean isRemoveDuplicatePaths() {
		return removeDuplicatePaths;
	}
	public boolean isRemoveRedundantMoveCommands() {
		return removeRedundantMoveCommands;
	}
	/** its seems many paths are drawn twice
	 * if their paths are equal, remove the later one(s)
	 */
	public void removeDuplicatePaths() {
		if (this.removeDuplicatePaths) {
			pathList = removeDuplicatePaths(pathList);
		}
	}
	/** modifies the paths
	 * 
	 * @param pathList
	 */
	public void removeRedundantMoveCommands() {
		if (this.removeRedundantMoveCommands ) {
			for (SVGPath path : pathList) {
				removeRedundantMoveCommands(path, MOVE_EPS);
			}
		}
	}
	/** runs components having set true/false flags if required
	 * 
	 */
	public void runAnalyses(List<SVGPath> pathList) {
		readPathList(pathList);
		this.removeDuplicatePaths();
		this.removeRedundantMoveCommands();
		this.splitAtMoveCommands();
		this.interpretPathsAsRectCirclePolylineAndReplace();
		this.splitPolylinesToLines(minLinesInPolyline);
//		ensureSVGChunk();
//		svgChunk.removeEmptySVGG();
	}
	
	public void readPathList(List<SVGPath> pathListIn) {
		this.pathList = new ArrayList<SVGPath>();
		if (pathListIn != null) {
			for (SVGPath path : pathListIn) {
				this.pathList.add(path); 
			}
		}
	}


	
	public void setMinLinesInPolyline(Integer minLinesInPolyline) {
		this.minLinesInPolyline = minLinesInPolyline;
	}
	public void setRemoveDuplicatePaths(boolean removeDuplicatePaths) {
		this.removeDuplicatePaths = removeDuplicatePaths;
	}
	public void setRemoveRedundantMoveCommands(boolean removeRedundantMoveCommands) {
		this.removeRedundantMoveCommands = removeRedundantMoveCommands;
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
	public void setSplitAtMoveCommands(boolean splitAtMoveCommands) {
		this.splitAtMoveCommands = splitAtMoveCommands;
	}
	public void splitAtMoveCommands() {
		if (this.splitAtMoveCommands ) {
			 for (SVGPath path : pathList) {
				 splitAtMoveCommands(path);
			 }
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
	public List<SVGLine> splitPolylinesToLines(Integer minLinesInPolyline) {
		LOG.trace("minLines: "+minLinesInPolyline);
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		ensureSvgChunk();
		List<SVGElement> polylineList = SVGUtil.getQuerySVGElements(svgChunk, ".//svg:polyline");
		for (SVGElement polyline : polylineList) {
			List<SVGLine> lines = ((SVGPolyline)polyline).createLineList();
			if (lines.size() < minLinesInPolyline) {
				ParentNode parent = polyline.getParent();
				for (int i = 0; i < lines.size(); i++) {
					SVGLine line = lines.get(i);
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
	
	private void ensureSvgChunk() {
		if (svgChunk == null) {
			svgChunk = new SVGG();
			// need to copy lines
		}
	}


	private SVGElement createCircleIfPossible(int id, SVGPath path, SVGElement newSVGElement) {
		SVGCircle circle = path.createCircle(_CIRCLE_EPS);
		if (circle != null) {
			LOG.trace("created circle");
			circle.format(PageIO.DECIMAL_PLACES);
			circle.setId("circle"+id);
			replace(path, circle);
			newSVGElement = circle;
		}
		return newSVGElement;
	}
	private void createRect(SVGPath path, SVGRect rect, int id) {
		LOG.trace("created rect: "+rect);
		rect.format(PageIO.DECIMAL_PLACES);
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


	public boolean isSplitAtMoveCommands() {
		return splitAtMoveCommands;
	}


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


	public static List<SVGPath> removeDuplicatePaths(List<SVGPath> pathList) {
		if (pathList != null) {
			Set<String> dStringSet = new HashSet<String>();
			int count = 0;
			List<SVGPath> newPathList = new ArrayList<SVGPath>();
			for (SVGPath path : pathList) {
				String dString = path.getDString();
				if (dStringSet.contains(dString)) {
					LOG.trace("detached a duplicate path "+dString);
					path.detach();
					count++;
				} else {
					dStringSet.add(dString);
					newPathList.add(path);
				}
			}
			if (count > 0) {
				LOG.trace("detached "+count+" duplicate paths");
				pathList = newPathList;
			}
		}
		return pathList;
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

}
