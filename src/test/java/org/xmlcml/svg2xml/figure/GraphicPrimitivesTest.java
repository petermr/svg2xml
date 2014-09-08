package org.xmlcml.svg2xml.figure;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.page.GraphicAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer.TextOrientation;
import org.xmlcml.svg2xml.paths.ComplexLine;
import org.xmlcml.svg2xml.paths.ComplexLine.CombType;
import org.xmlcml.svg2xml.paths.ComplexLine.LineOrientation;
import org.xmlcml.svg2xml.paths.Joint;
import org.xmlcml.svg2xml.text.TextStructurer;

public class GraphicPrimitivesTest {

	private static final PrintStream SYSOUT = System.out;
	final static Double EPS = 0.05;
	private static final Logger LOG = Logger.getLogger(GraphicPrimitives.class);

	@Test
	public void testAllXYaxisinPlot() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOT_FIVE_7_2_SVG,  "./svg:g", 0);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(g);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		Assert.assertEquals("hor", 36, horizontalLines.size());
		Assert.assertEquals("vert", 35, verticalLines.size());
		List<ComplexLine> horizontalComplexLineList = new ArrayList<ComplexLine>();
		for (SVGLine horizontalLine : horizontalLines) {
			if (horizontalLine.getLength() > 50) {
				ComplexLine horizontalComplexLine = ComplexLine.createComplexLine(horizontalLine, EPS);
				List<Joint> joints = horizontalComplexLine.addLines(verticalLines);
				Assert.assertEquals("jointType", CombType.PLUS_OR_MINUS, horizontalComplexLine.getCombType());
				if (CombType.PLUS_OR_MINUS.equals(horizontalComplexLine.getCombType())) {
					horizontalComplexLineList.add(horizontalComplexLine);
					LOG.trace("\nHHH "+horizontalComplexLine.getBackbone()+"\n");
				}
			}
		}
		Assert.assertEquals(5, horizontalComplexLineList.size());
//		LOG.trace("======================");
		List<ComplexLine> verticalComplexLineList = new ArrayList<ComplexLine>();
		for (SVGLine verticalLine : verticalLines) {
			if (verticalLine.getLength() > 50) {
				ComplexLine verticalComplexLine = ComplexLine.createComplexLine(verticalLine, EPS);
				List<Joint> joints = verticalComplexLine.addLines(horizontalLines);
				Assert.assertEquals("jointType", CombType.PLUS_OR_MINUS, verticalComplexLine.getCombType());
				if (CombType.PLUS_OR_MINUS.equals(verticalComplexLine.getCombType())) {
					verticalComplexLineList.add(verticalComplexLine);
				}
			}
		}
		Assert.assertEquals(5, verticalComplexLineList.size());
	}


	@Test
	public void testXaxis() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.XAXIS_SVG,  "./svg:g", 0);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(g);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		Assert.assertEquals("hor", 1, horizontalLines.size());
		SVGLine horizontalLine = horizontalLines.get(0);
		Assert.assertEquals("vert", 8, verticalLines.size());
		ComplexLine horizontalComplexLine = ComplexLine.createComplexLine(horizontalLine, EPS);
		String id = horizontalLine.getId();
		List<Joint> joints = horizontalComplexLine.addLines(verticalLines);
		Assert.assertEquals("joint: "+id, 8, joints.size());
		Assert.assertEquals("type", CombType.PLUS, horizontalComplexLine.getCombType());
	}


	@Test
	public void testXaxis1() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.XAXIS_SVG,  "./svg:g", 0);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(g);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		Assert.assertEquals("hor", 1, horizontalLines.size());
		SVGLine horizontalLine = horizontalLines.get(0);
		Assert.assertEquals("vert", 8, verticalLines.size());
		ComplexLine horizontalComplexLine = ComplexLine.createComplexLine(horizontalLine, EPS);
		String id = horizontalLine.getId();
		List<Joint> joints = horizontalComplexLine.addLines(verticalLines);
		Assert.assertEquals("joint: "+id, 8, joints.size());
	}


	@Test
	public void testXYaxisinPlot() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOTRED_7_2_SVG,  "./svg:g", 0);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(g);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		Assert.assertEquals("hor", 6, horizontalLines.size());
		Assert.assertEquals("vert", 7, verticalLines.size());
		ComplexLine horizontalComplexLine = null;
		for (SVGLine horizontalLine : horizontalLines) {
			if (horizontalLine.getLength() > 50) {
				horizontalComplexLine = ComplexLine.createComplexLine(horizontalLine, EPS);
				List<Joint> joints = horizontalComplexLine.addLines(verticalLines);
				Assert.assertEquals("jointType", CombType.PLUS_OR_MINUS, horizontalComplexLine.getCombType());
				Assert.assertEquals("joints: "+horizontalLine.getId(), 7, joints.size());
				for (Joint joint : joints) {
					LOG.trace("j "+joint);
				}
			}
		}
		LOG.trace("======================");
		ComplexLine verticalComplexLine = null;
		for (SVGLine verticalLine : verticalLines) {
			if (verticalLine.getLength() > 50) {
				verticalComplexLine = ComplexLine.createComplexLine(verticalLine, EPS);
				List<Joint> joints = verticalComplexLine.addLines(horizontalLines);
				Assert.assertEquals("joint: "+verticalLine.getId(), 6, joints.size());
				Assert.assertEquals("jointType", CombType.PLUS_OR_MINUS, verticalComplexLine.getCombType());
				for (Joint joint : joints) {
					LOG.trace("j "+joint);
				}
			}
		}
		horizontalComplexLine.detach();
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/noHorizontalAxis.svg"));
		verticalComplexLine.detach();
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/noHorizontalOrVerticalAxis.svg"));
		GraphicAnalyzer graphicAnalyzer = new GraphicAnalyzer(g);
		TextStructurer textStructurer = graphicAnalyzer.createTextStructurer(TextOrientation.ROT_0);
		textStructurer.detachCharacters();
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/noHorizontalText.svg"));
		textStructurer = graphicAnalyzer.createTextStructurer(TextOrientation.ROT_PI2);
		textStructurer.detachCharacters();
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/noText.svg"));
	}



	@Test
	public void testScatterplotAnalysis() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOT_7_2_SVG,  "./svg:g", 0);
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(g,
				".//svg:*[not(local-name()='g') and not(local-name()='svg')]");
		Assert.assertEquals(GraphicPrimitives.ALL, 746, elementList.size());
		
		List<ComplexLine> horizontalList = GraphicPrimitives.makeComplexLines(g, LineOrientation.HORIZONTAL, GraphicPrimitives.MIN_BACKBONE);
		Assert.assertEquals("horiz", 1, horizontalList.size());
		
		List<ComplexLine> verticalList = GraphicPrimitives.makeComplexLines(g, LineOrientation.VERTICAL, GraphicPrimitives.MIN_BACKBONE);
		Assert.assertEquals("vert", 1, verticalList.size());
		
		ComplexLine.detach(horizontalList); // only now, because lines overlap
		elementList = SVGUtil.getQuerySVGElements(g,
				".//svg:*[not(local-name()='g') and not(local-name()='svg')]");
		Assert.assertEquals(GraphicPrimitives.ALL, 738, elementList.size());
		ComplexLine.detach(verticalList);
		elementList = SVGUtil.getQuerySVGElements(g,
				".//svg:*[not(local-name()='g') and not(local-name()='svg')]");
		Assert.assertEquals(GraphicPrimitives.ALL, 733, elementList.size());
		
		List<SVGPath> pathList = SVGPath.extractSelfAndDescendantPaths(g);
		Assert.assertEquals("path", 0, pathList.size());
		List<SVGPolyline> polylineList = SVGPolyline.extractSelfAndDescendantPolylines(g);
		Assert.assertEquals("polyline", 0, polylineList.size());
		List<SVGPolygon> polygonList = SVGPolygon.extractSelfAndDescendantPolygons(g);
		Assert.assertEquals("polygon", 78, polygonList.size());
		List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(g);
		Assert.assertEquals("line", 392, lineList.size());
		List<SVGCircle> circleList = SVGCircle.extractSelfAndDescendantCircles(g);
		Assert.assertEquals("circle", 118, circleList.size());
		List<SVGRect> rectList = SVGRect.extractSelfAndDescendantRects(g);
		Assert.assertEquals("rect", 54, rectList.size());
		
// check circles all same size		
		Set<Double> circleSet = new HashSet<Double>();
		for (SVGCircle circle : circleList) {
			circleSet.add(Util.format(circle.getRad(), 1));
		}
		Assert.assertEquals("circle", 1, circleSet.size());
		//Assert.assertEquals("radius", 1.50, circleSet.iterator().next());
		
		// check polygons all triangles		
		Set<Integer> polygonSet = new HashSet<Integer>();
		for (SVGPolygon polygon : polygonList) {
			polygonSet.add(polygon.size());
		}
		Assert.assertEquals("polygon", 1, polygonSet.size());
		Assert.assertEquals("sides", 3, (int) polygonSet.iterator().next());
		
		// check rectangles all same size		
		Set<String> rectSet = new HashSet<String>();
		for (SVGRect rect : rectList) {
			Real2 dimension = new Real2(rect.getWidth(), rect.getHeight());
			rectSet.add(dimension.format(1).toString());
		}
		Assert.assertEquals("rectSet", 1, rectSet.size());
		Assert.assertEquals("rect", "(4.0,4.0)", rectSet.iterator().next());
		
		
		// check lines all same size		
		Set<Double> lineSet = new HashSet<Double>();
		for (SVGLine line : lineList) {
			Double length = Util.format(line.getLength(), 1);
			lineSet.add(length);
		}
		Assert.assertEquals("lineSet", 1, lineSet.size());
		//Assert.assertEquals("line", 5.7, lineSet.iterator().next());

		// now their vectors
		Set<String> vectorSet = new HashSet<String>();
		for (SVGLine line : lineList) {
			LOG.trace(line);
			String vectorS = line.getEuclidLine().getVector().format(1).toString();
			vectorSet.add(vectorS);
		}
		Assert.assertEquals("vectorSet", 2, vectorSet.size());
		Iterator<String> iterator = vectorSet.iterator();
		Assert.assertEquals("line", "(4.0,-4.0)", iterator.next());
		Assert.assertEquals("line", "(-4.0,-4.0)", iterator.next());
		
	}
	

	@Test
	public void testLinePlots() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		GraphicPrimitives graphicPrimitives = new GraphicPrimitives(g);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.ALL, 
				GraphicPrimitivesNavigator.COUNT, 343);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.LINE, 
				GraphicPrimitivesNavigator.COUNT, 15, 
				GraphicPrimitivesNavigator.LENGTH, "[139.4, 138.0, 139.8, 2.3, 195.2]",
				GraphicPrimitivesNavigator.VECTOR, "[(0.0,2.3), (136.6,-29.5), (195.2,0.5), (136.6,-19.4), (0.0,-139.4), (2.3,0.0)]");
		graphicPrimitives.addPlotComponent(GraphicPrimitives.CIRCLE, 
				GraphicPrimitivesNavigator.COUNT, 0);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.PATH, 
				GraphicPrimitivesNavigator.COUNT, 0);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.POLYGON, 
				GraphicPrimitivesNavigator.COUNT, 65,
				GraphicPrimitivesNavigator.POINTS, "[3]",
				GraphicPrimitivesNavigator.DIMENSION, "[(1.8,1.8)]"
				);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.POLYLINE, 
				GraphicPrimitivesNavigator.COUNT, 65,
				GraphicPrimitivesNavigator.POINTS, "[2]",
				GraphicPrimitivesNavigator.DIMENSION, "[(1.8,1.8)]"
				);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.RECT, 
				GraphicPrimitivesNavigator.COUNT, 124,
				GraphicPrimitivesNavigator.DIMENSION, "[(2.3,2.8), (2.8,2.8), (195.2,139.4), (2.8,2.3), (223.1,168.5), (2.3,2.3)]");
		graphicPrimitives.addPlotComponent(GraphicPrimitives.TEXT, 
				GraphicPrimitivesNavigator.COUNT, 74,
				GraphicPrimitivesNavigator.DIMENSION, "d");
		
		graphicPrimitives.addPlotComponent(GraphicPrimitives.HORIZONTAL, GraphicPrimitivesNavigator.COUNT, 8);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.VERTICAL, GraphicPrimitivesNavigator.COUNT, 5);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.HORIZONTAL_BACKBONE, GraphicPrimitivesNavigator.COUNT, 1);
		graphicPrimitives.addPlotComponent(GraphicPrimitives.VERTICAL_BACKBONE, GraphicPrimitivesNavigator.COUNT, 1);
////	ComplexLine.detach(horizontalList); // only now, because lines overlap
////	elementList = SVGUtil.getQuerySVGElements(g,
////			".//svg:*[not(local-name()='g') and not(local-name()='svg')]");
////	Assert.assertEquals("all", 738, elementList.size());
////	ComplexLine.detach(verticalList);
////	elementList = SVGUtil.getQuerySVGElements(g,
////			".//svg:*[not(local-name()='g') and not(local-name()='svg')]");
////	Assert.assertEquals("all", 733, elementList.size());
//	

		this.analyzePlot(graphicPrimitives);
	}

// =======================================================================================	

	public void analyzePlot(GraphicPrimitives graphicPrimitives) {
		graphicPrimitives.createComponentLists();
		GraphicPrimitivesAssert tester = new GraphicPrimitivesAssert(graphicPrimitives);
		// order may matter
		tester.assertAll();
		tester.assertLines();
		tester.assertCircles();
		tester.assertPaths();
		tester.assertPolygons();
		tester.assertPolylines();
		tester.assertRects();
		tester.assertTexts();
		tester.assertHorizontalLines();
		tester.assertVerticalLines();
		tester.assertHorizontalBackbones();
		tester.assertVerticalBackbones();
	}


}
