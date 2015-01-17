package org.xmlcml.svg2xml.figure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.linestuff.ComplexLine;
import org.xmlcml.graphics.svg.linestuff.ComplexLine.CombType;
import org.xmlcml.graphics.svg.linestuff.ComplexLine.LineOrientation;
import org.xmlcml.graphics.svg.linestuff.Joint;

public class GraphicPrimitives {

	private final static Logger LOG = Logger.getLogger(GraphicPrimitives.class);
	
	public static final double MIN_BACKBONE = 50.0;
	final static Double HORIZ_VERT_EPS = 0.5; //this has to be quite large, as xaxis is not always flat

	public static final String ALL = "all";
	public static final String CIRCLE = "circle";
	public static final String HORIZONTAL = "horizontal";
	public static final String HORIZONTAL_BACKBONE = "horizontalBackbone";
	public static final String LINE = "line";
	public static final String PATH = "path";
	public static final String POLYGON = "polygon";
	public static final String POLYLINE = "polyline";
	public static final String RECT = "rect";
	public static final String TEXT = "text";
	public static final String VERTICAL = "vertical";
	public static final String VERTICAL_BACKBONE = "verticalBackbone";

	private List<SVGElement> allElementList = null;
	private List<SVGLine> horizontalLineList = null;
	List<SVGLine> verticalLineList = null;
	private List<ComplexLine> horizontalBackboneList = null;
	private List<ComplexLine> verticalBackboneList = null;
	private List<SVGPath> pathList = null;
	private List<SVGPolyline> polylineList = null;
	private List<SVGPolygon> polygonList = null;
	private List<SVGLine> lineList = null;
	private List<SVGCircle> circleList = null;
	private List<SVGRect> rectList = null;
	private List<SVGText> textList = null;
	
	public List<SVGElement> getAllElementList() {
		return allElementList;
	}

	public List<SVGLine> getHorizontalLineList() {
		return horizontalLineList;
	}

	public List<SVGLine> getVerticalLineList() {
		return verticalLineList;
	}

	public List<ComplexLine> getHorizontalBackboneList() {
		return horizontalBackboneList;
	}

	public List<ComplexLine> getVerticalBackboneList() {
		return verticalBackboneList;
	}

	public List<SVGPath> getPathList() {
		return pathList;
	}

	public List<SVGPolyline> getPolylineList() {
		return polylineList;
	}

	public List<SVGPolygon> getPolygonList() {
		return polygonList;
	}

	public List<SVGLine> getLineList() {
		return lineList;
	}

	public List<SVGCircle> getCircleList() {
		return circleList;
	}

	public List<SVGRect> getRectList() {
		return rectList;
	}

	public List<SVGText> getTextList() {
		return textList;
	}

	public Map<String, GraphicPrimitivesNavigator> getPlotComponentTable() {
		return plotComponentTable;
	}

	private Map<String, GraphicPrimitivesNavigator> plotComponentTable;

	private SVGG g;
	
	public GraphicPrimitives(SVGG g) {
		plotComponentTable = new HashMap<String, GraphicPrimitivesNavigator>();
		this.g = g;
	}
	
	public void createComponentLists() {
		allElementList = SVGUtil.getQuerySVGElements(g,
						".//svg:*[not(local-name()='g') and not(local-name()='svg')]");
		lineList = SVGLine.extractSelfAndDescendantLines(g);
		horizontalLineList = ComplexLine.createSubset(lineList, LineOrientation.HORIZONTAL, HORIZ_VERT_EPS);
		verticalLineList = ComplexLine.createSubset(lineList, LineOrientation.VERTICAL, HORIZ_VERT_EPS);
		horizontalBackboneList = makeComplexLines(g, LineOrientation.HORIZONTAL, MIN_BACKBONE);
		verticalBackboneList = makeComplexLines(g, LineOrientation.VERTICAL, MIN_BACKBONE);
		pathList = SVGPath.extractSelfAndDescendantPaths(g);
		polylineList = SVGPolyline.extractSelfAndDescendantPolylines(g);
		polygonList = SVGPolygon.extractSelfAndDescendantPolygons(g);
		circleList = SVGCircle.extractSelfAndDescendantCircles(g);
		rectList = SVGRect.extractSelfAndDescendantRects(g);
		textList = SVGText.extractSelfAndDescendantTexts(g);
	}
	public void addPlotComponent(String type, Object ...objects ) {
		GraphicPrimitivesNavigator plotComponent = new GraphicPrimitivesNavigator(type, objects);
		plotComponentTable.put(type,  plotComponent);
	}
	
	public GraphicPrimitivesNavigator get(String key) {
		return plotComponentTable.get(key);
	}
	public Object get(String mainKey, String subsidKey) {
		GraphicPrimitivesNavigator component = this.get(mainKey);
		return (component == null) ? null : component.getObject(subsidKey);
	}
	
	public Set<Double> createCircleSet() {
		Set<Double> circleSet = new HashSet<Double>();
		for (SVGCircle circle : circleList) {
			circleSet.add(Util.format(circle.getRad(), 1));
		}
		return circleSet;
	}

	Set<Double> createLineLengthSet() {
		Set<Double> lineLengthSet = new HashSet<Double>();
		for (SVGLine line : lineList) {
			Double length = Util.format(line.getLength(), 1);
			lineLengthSet.add(length);
		}
		return lineLengthSet;
	}

	Set<Integer> createPolygonPointsSet() {
		Set<Integer> polygonPointsSet = new HashSet<Integer>();
		for (SVGPolygon polygon : polygonList) {
			polygonPointsSet.add(polygon.size());
		}
		return polygonPointsSet;
	}

	public Set<String> createLineVectorSet() {
		Set<String> lineVectorSet = new HashSet<String>();
		for (SVGLine line : lineList) {
			String vectorS = line.getEuclidLine().getVector().format(1).toString();
			lineVectorSet.add(vectorS);
		}
		return lineVectorSet;
	}

	public Set<Integer> createPolylinePointsSet() {
		Set<Integer> polylinePointsSet = new HashSet<Integer>();
		for (SVGPolyline polyline : polylineList) {
			polylinePointsSet.add(polyline.size());
		}
		return polylinePointsSet;
	}

	Set<String> getDimensionSet(List<? extends SVGElement> elementList) {
		Set<String> elementDimensionSet = new HashSet<String>();
		for (SVGElement element : elementList) {
			Real2Range bbox = element.getBoundingBox();
			Real2 dimension = new Real2(bbox.getXRange().getRange(), bbox.getYRange().getRange());
			elementDimensionSet.add(dimension.format(1).toString());
		}
		return elementDimensionSet;
	}

	public Set<String> getRectDimensionSet() {
		Set<String> rectDimensionSet = new HashSet<String>();
		for (SVGRect rect : rectList) {
			Real2 dimension = new Real2(rect.getWidth(), rect.getHeight());
			rectDimensionSet.add(dimension.format(1).toString());
		}
		return rectDimensionSet;
	}

	static List<ComplexLine> makeComplexLines(SVGG g, LineOrientation lineOrientation, double minBackboneLength) { 
		LOG.trace(" ========= orientation: "+lineOrientation);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(g);
		List<SVGLine> mainLines = ComplexLine.createSubset(svgLines, lineOrientation, HORIZ_VERT_EPS);
		List<SVGLine> perpendicularLines = ComplexLine.createSubset(svgLines, lineOrientation.getOtherOrientation(), HORIZ_VERT_EPS);
		List<ComplexLine> complexLineList = new ArrayList<ComplexLine>();
		for (SVGLine mainLine : mainLines) {
			double mainLength = mainLine.getLength();
			LOG.trace("length: "+mainLength);
			if (mainLength > minBackboneLength) {
				ComplexLine mainComplexLine = ComplexLine.createComplexLine(mainLine, HORIZ_VERT_EPS);
				List<Joint> joints = mainComplexLine.addLines(perpendicularLines);
				if (CombType.PLUS_OR_MINUS.equals(mainComplexLine.getCombType()) ||
					CombType.PLUS.equals(mainComplexLine.getCombType()) ||
					CombType.MINUS.equals(mainComplexLine.getCombType()) ) {
					complexLineList.add(mainComplexLine);
				} else {
					LOG.warn("Unexpected comb: "+mainComplexLine.getCombType());
				}
			}
		}
		return complexLineList;
	}


}

