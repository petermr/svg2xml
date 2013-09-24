package org.xmlcml.svg2xml.figure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

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
import org.xmlcml.svg2xml.paths.ComplexLine;
import org.xmlcml.svg2xml.paths.ComplexLine.CombType;
import org.xmlcml.svg2xml.paths.ComplexLine.LineOrientation;
import org.xmlcml.svg2xml.paths.Joint;

public class PlotComponents {

	private final static Logger LOG = Logger.getLogger(PlotComponents.class);
	
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

	public List<SVGElement> allElementList = null;
	public List<SVGLine> horizontalLineList = null;
	public List<SVGLine> verticalLineList = null;
	public List<ComplexLine> horizontalBackboneList = null;
	public List<ComplexLine> verticalBackboneList = null;
	public List<SVGPath> pathList = null;
	public List<SVGPolyline> polylineList = null;
	public List<SVGPolygon> polygonList = null;
	public List<SVGLine> lineList = null;
	public List<SVGCircle> circleList = null;
	public List<SVGRect> rectList = null;
	public List<SVGText> textList = null;
	
	private Map<String, PlotComponent> plotComponentTable;

	private SVGG g;
	
	public PlotComponents() {
		plotComponentTable = new HashMap<String, PlotComponent>();
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
		PlotComponent plotComponent = new PlotComponent(type, objects);
		plotComponentTable.put(type,  plotComponent);
	}
	
	public PlotComponent get(String key) {
		return plotComponentTable.get(key);
	}
	public Object get(String mainKey, String subsidKey) {
		PlotComponent component = this.get(mainKey);
		return (component == null) ? null : component.getObject(subsidKey);
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

	public void analyzePlot(SVGG g) {
		this.g = g;
		createComponentLists();
		// order may matter
		this.checkAll();
		this.checkLines();
		this.checkCircles();
		this.checkPaths();
		this.checkPolygons();
		this.checkPolylines();
		this.checkRects();
		this.checkTexts();
		this.checkHorizontalLines();
		this.checkVerticalLines();
		this.checkHorizontalBackbones();
		this.checkVerticalBackbones();
	}

	void checkAll() {
		assertStringValuesEqual(PlotComponents.ALL, PlotComponent.COUNT, allElementList.size());
	}

	private void checkHorizontalLines() {
		assertStringValuesEqual(PlotComponents.HORIZONTAL, PlotComponent.COUNT, horizontalLineList.size());
	}

	private void checkCircles() {
		assertStringValuesEqual(PlotComponents.CIRCLE, PlotComponent.COUNT, circleList.size());
		Set<Double> circleSet = new HashSet<Double>();
		for (SVGCircle circle : circleList) {
			circleSet.add(Util.format(circle.getRad(), 1));
		}
		assertStringValuesEqual(PlotComponents.CIRCLE, PlotComponent.RADIUS, circleSet.toString());
	}

	private void checkLines() {
		assertStringValuesEqual(PlotComponents.LINE, PlotComponent.COUNT, lineList.size());
		Set<Double> lineLengthSet = new HashSet<Double>();
		for (SVGLine line : lineList) {
			Double length = Util.format(line.getLength(), 1);
			lineLengthSet.add(length);
		}
		assertStringValuesEqual(PlotComponents.LINE, PlotComponent.LENGTH, lineLengthSet);

		Set<String> lineVectorSet = new HashSet<String>();
		for (SVGLine line : lineList) {
			String vectorS = line.getEuclidLine().getVector().format(1).toString();
			lineVectorSet.add(vectorS);
		}
		assertStringValuesEqual(PlotComponents.LINE, PlotComponent.VECTOR, lineVectorSet);
	}

	private void checkPaths() {
		assertStringValuesEqual(PlotComponents.PATH, PlotComponent.COUNT, pathList.size());
	}

	private void checkPolygons() {
		assertStringValuesEqual(PlotComponents.POLYGON, PlotComponent.COUNT, polygonList.size());
		assertPolygonSizes();
		assertPolygonDimensions();
	}

	private void assertPolygonSizes() {
		Set<Integer> polygonPointsSet = new HashSet<Integer>();
		for (SVGPolygon polygon : polygonList) {
			polygonPointsSet.add(polygon.size());
		}
		assertStringValuesEqual(PlotComponents.POLYGON, PlotComponent.POINTS, polygonPointsSet);
	}
	private void assertPolygonDimensions() {
		assertStringValuesEqual(PlotComponents.POLYGON, PlotComponent.DIMENSION, getDimensionSet(polygonList));
	}

	private void checkPolylines() {
		assertStringValuesEqual(PlotComponents.POLYLINE, PlotComponent.COUNT, polylineList.size());
		assertPolylineSizes();
		assertPolylineDimensions();
	}

	private void assertPolylineSizes() {
		Set<Integer> polylinePointsSet = new HashSet<Integer>();
		for (SVGPolyline polyline : polylineList) {
			polylinePointsSet.add(polyline.size());
		}
		assertStringValuesEqual(PlotComponents.POLYLINE, PlotComponent.POINTS, polylinePointsSet);
	}

	private void assertPolylineDimensions() {
		assertStringValuesEqual(PlotComponents.POLYLINE, PlotComponent.DIMENSION, getDimensionSet(polylineList));
	}
	
	private Set<String> getDimensionSet(List<? extends SVGElement> elementList) {
		Set<String> elementDimensionSet = new HashSet<String>();
		for (SVGElement element : elementList) {
			Real2Range bbox = element.getBoundingBox();
			Real2 dimension = new Real2(bbox.getXRange().getRange(), bbox.getYRange().getRange());
			elementDimensionSet.add(dimension.format(1).toString());
		}
		return elementDimensionSet;
	}
	

	private void checkRects() {
		assertStringValuesEqual(PlotComponents.RECT, PlotComponent.COUNT, rectList.size());
		Set<String> rectDimensionSet = new HashSet<String>();
		for (SVGRect rect : rectList) {
			Real2 dimension = new Real2(rect.getWidth(), rect.getHeight());
			rectDimensionSet.add(dimension.format(1).toString());
		}
		assertStringValuesEqual(PlotComponents.RECT, PlotComponent.DIMENSION, rectDimensionSet);
	}
	
	private void checkTexts() {
		assertStringValuesEqual(PlotComponents.TEXT, PlotComponent.COUNT, textList.size());
	}

	private void checkVerticalBackbones() {
		assertStringValuesEqual(PlotComponents.VERTICAL_BACKBONE, PlotComponent.COUNT, verticalBackboneList.size());
	}

	private void checkHorizontalBackbones() {
		assertStringValuesEqual(PlotComponents.HORIZONTAL_BACKBONE, PlotComponent.COUNT, horizontalBackboneList.size());
	}

	private void checkVerticalLines() {
		assertStringValuesEqual(PlotComponents.VERTICAL, PlotComponent.COUNT, verticalLineList.size());
	}

	private void assertStringValuesEqual(String type, String key, Object actual) {
		PlotComponent component = this.get(type);
		if (component == null) {
			throw new RuntimeException("No component for: "+type);
		}
		Object expected = component.getObject(key);
		if (expected != null) {
			Assert.assertEquals(type+":"+key, String.valueOf(expected), String.valueOf(actual));
		}
	}


}

