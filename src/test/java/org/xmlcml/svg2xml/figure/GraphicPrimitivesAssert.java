package org.xmlcml.svg2xml.figure;

import java.util.Set;

import org.junit.Assert;

/** support library for testing Figures/Plots.
 * 
 * @author pm286
 *
 */
public class GraphicPrimitivesAssert {

	private GraphicPrimitives graphicPrimitives;
	
	public GraphicPrimitivesAssert(GraphicPrimitives graphicPrimitives) {
		this.graphicPrimitives = graphicPrimitives;
	}

	void assertAll() {
		assertStringValuesEqual(GraphicPrimitives.ALL, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getAllElementList().size());
	}

	void assertHorizontalLines() {
		assertStringValuesEqual(GraphicPrimitives.HORIZONTAL, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getHorizontalLineList().size());
	}

	void assertCircles() {
		assertStringValuesEqual(GraphicPrimitives.CIRCLE, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getCircleList().size());
		Set<Double> circleSet = graphicPrimitives.createCircleSet();
		assertStringValuesEqual(GraphicPrimitives.CIRCLE, GraphicPrimitivesNavigator.RADIUS, circleSet.toString());
	}

	void assertLines() {
		assertStringValuesEqual(GraphicPrimitives.LINE, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getLineList().size());
		Set<Double> lineLengthSet = graphicPrimitives.createLineLengthSet();
		assertStringValuesEqual(GraphicPrimitives.LINE, GraphicPrimitivesNavigator.LENGTH, lineLengthSet);

		Set<String> lineVectorSet = graphicPrimitives.createLineVectorSet();
		assertStringValuesEqual(GraphicPrimitives.LINE, GraphicPrimitivesNavigator.VECTOR, lineVectorSet);
	}

	void assertPaths() {
		assertStringValuesEqual(GraphicPrimitives.PATH, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getPathList().size());
	}

	void assertPolygons() {
		assertStringValuesEqual(GraphicPrimitives.POLYGON, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getPolygonList().size());
		assertPolygonSizes();
		assertPolygonDimensions();
	}

	private void assertPolygonSizes() {
		Set<Integer> polygonPointsSet = graphicPrimitives.createPolygonPointsSet();
		assertStringValuesEqual(GraphicPrimitives.POLYGON, GraphicPrimitivesNavigator.POINTS, polygonPointsSet);
	}


	private void assertPolygonDimensions() {
		assertStringValuesEqual(GraphicPrimitives.POLYGON, 
				GraphicPrimitivesNavigator.DIMENSION, graphicPrimitives.getDimensionSet(graphicPrimitives.getPolygonList()));
	}

	void assertPolylines() {
		assertStringValuesEqual(GraphicPrimitives.POLYLINE, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getPolylineList().size());
		assertPolylineSizes();
		assertPolylineDimensions();
	}

	private void assertPolylineSizes() {
		Set<Integer> polylinePointsSet = graphicPrimitives.createPolylinePointsSet();
		assertStringValuesEqual(GraphicPrimitives.POLYLINE, GraphicPrimitivesNavigator.POINTS, polylinePointsSet);
	}


	private void assertPolylineDimensions() {
		assertStringValuesEqual(GraphicPrimitives.POLYLINE, 
				GraphicPrimitivesNavigator.DIMENSION, graphicPrimitives.getDimensionSet(graphicPrimitives.getPolylineList()));
	}
	
	void assertRects() {
		assertStringValuesEqual(GraphicPrimitives.RECT, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getRectList().size());
		Set<String> rectDimensionSet = graphicPrimitives.getRectDimensionSet();
		assertStringValuesEqual(GraphicPrimitives.RECT, 
				GraphicPrimitivesNavigator.DIMENSION, rectDimensionSet);
	}

	void assertTexts() {
		assertStringValuesEqual(GraphicPrimitives.TEXT, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getTextList().size());
	}

	void assertVerticalBackbones() {
		assertStringValuesEqual(GraphicPrimitives.VERTICAL_BACKBONE, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getVerticalBackboneList().size());
	}

	void assertHorizontalBackbones() {
		assertStringValuesEqual(GraphicPrimitives.HORIZONTAL_BACKBONE, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getHorizontalBackboneList().size());
	}

	void assertVerticalLines() {
		assertStringValuesEqual(GraphicPrimitives.VERTICAL, 
				GraphicPrimitivesNavigator.COUNT, graphicPrimitives.getVerticalLineList().size());
	}

	public void assertStringValuesEqual(String type, String key, Object actual) {
		GraphicPrimitivesNavigator primitive = graphicPrimitives.get(type);
		if (primitive == null) {
			throw new RuntimeException("No component for: "+type);
		}
		Object expected = primitive.getObject(key);
		if (expected != null) {
			Assert.assertEquals(type+":"+key, String.valueOf(expected), String.valueOf(actual));
		}
	}
}
