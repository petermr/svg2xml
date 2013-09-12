package org.xmlcml.svg2xml.tree;

import java.io.FileOutputStream;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.paths.ComplexLine;
import org.xmlcml.svg2xml.paths.ComplexLine.LineOrientation;
import org.xmlcml.svg2xml.paths.ComplexLine.SideOrientation;
import org.xmlcml.svg2xml.paths.ComplexLineTest;
import org.xmlcml.svg2xml.paths.LineMerger;
import org.xmlcml.svg2xml.util.GraphUtil;

public class TreeTest {
	private final static Logger LOG = Logger.getLogger(TreeTest.class);

	public static final double EPS = 0.01;
	
	@Test
	public void testCluster() throws Exception {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.PATHS_CLUSTER1_SVG);
		List<SVGLine> svgLines = ComplexLineTest.extractLines((SVGElement)svg.getChildElements().get(0));
		Assert.assertEquals("lines", 249, svgLines.size());
		List<SVGLine> zeroLines = ComplexLine.createSubsetAndRemove(svgLines, LineOrientation.ZERO, EPS);
		Assert.assertEquals("lines", 71, zeroLines.size());
		Assert.assertEquals("lines", 178, svgLines.size());
		System.out.println(svgLines.size());
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		Assert.assertEquals(83, verticalLines.size());
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		Assert.assertEquals(95, horizontalLines.size());
		CMLUtil.debug(svg, new FileOutputStream(Fixtures.PATHS_CLUSTER1A_SVG),1);
	}

	@Test
	public void testClusterRedrawTree() throws Exception {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.PATHS_CLUSTER1_SVG);
		List<SVGLine> svgLines = ComplexLineTest.extractLines((SVGElement)svg.getChildElements().get(0));
		List<SVGLine> zeroLines = ComplexLine.createSubsetAndRemove(svgLines, LineOrientation.ZERO, EPS);
		extendZeroLines(zeroLines, LineOrientation.HORIZONTAL, 1.5f);
		CMLUtil.debug(svg, new FileOutputStream(Fixtures.PATHS_CLUSTER1A_SVG),1);
	}

	private void extendZeroLines(List<SVGLine> svgLines, LineOrientation lineOrientation, double length) {
		for (SVGLine line : svgLines) {
			if (line.isZero(EPS)) {
				Real2 point = line.getXY(0);
				Real2 extension = (lineOrientation.equals(LineOrientation.HORIZONTAL)) ? new Real2(length, 0.0) : new Real2(0.0, length);
				point = point.plus(extension);
				line.setXY(point, 1);
			}
		}
	}

	
	@Test
	public void testCluster1() throws Exception {
		double EPS1 = 0.3;
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.PATHS_CLUSTER1_SVG);
		List<SVGLine> svgLines = ComplexLineTest.extractLines((SVGElement)svg.getChildElements().get(0));
		for (SVGLine line : svgLines) {
			line.setStrokeWidth(0.3);
		}
		List<SVGLine> zeroLines = ComplexLine.createSubsetAndRemove(svgLines, LineOrientation.ZERO, EPS1);
		Assert.assertTrue(72 >= zeroLines.size() && zeroLines.size() >= 71);
//		addZeroAsPoints(zeroLines, svg);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS1);
		Assert.assertEquals(83, verticalLines.size());
		List<SVGLine> joinedVerticalLines = LineMerger.mergeLines(verticalLines, EPS1);
		int size = joinedVerticalLines.size();
		Assert.assertTrue("size "+size, 61 >= size && size >= 60);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS1);
		extendZeroLines(zeroLines, LineOrientation.HORIZONTAL, 0.5f);
		List<ComplexLine> verticalComplexLines = 
				ComplexLine.createComplexLines(joinedVerticalLines, horizontalLines, EPS1);
//		SVGXTree tree = new SVGXTree();
//		tree.analyzeLines(verticalComplexLines, SideOrientation.MINUS);
		
		CMLUtil.debug(svg, new FileOutputStream(Fixtures.PATHS_CLUSTER2A_SVG),1);
	}

	@Test
	public void testCluster7() throws Exception {
		double EPS1 = 0.3;
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.PATHS_CLUSTER1_SVG);
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svg,  ".//svg:line");
		List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
		Assert.assertEquals(249, svgLines.size());
		for (SVGLine line : svgLines) {
			line.setStrokeWidth(0.3);
		}
		List<SVGLine> zeroLines = ComplexLine.createSubsetAndRemove(svgLines, LineOrientation.ZERO, EPS1);
		Assert.assertEquals(72, zeroLines.size());
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS1);
		Assert.assertEquals(83, verticalLines.size());
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS1);
		Assert.assertEquals(94, horizontalLines.size());
		List<ComplexLine> verticalComplexLines = ComplexLine.createComplexLines(verticalLines, horizontalLines, EPS1);
		List<ComplexLine> horizontalComplexLines = ComplexLine.createComplexLines(horizontalLines, verticalLines, EPS1);
		
		SVGXTree tree = new SVGXTree((SVGG)svgLines.get(0).getParent());
		
		List<ComplexLine> emptyEndedHorizontalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(horizontalComplexLines, SideOrientation.EMPTYLIST);
		Assert.assertEquals(0, emptyEndedHorizontalLines.size());
		List<ComplexLine> doubleEndedHorizontalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(horizontalComplexLines, SideOrientation.MINUSPLUSLIST);
		Assert.assertEquals(57, doubleEndedHorizontalLines.size());
		List<ComplexLine> minusEndedHorizontalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(horizontalComplexLines, SideOrientation.MINUSLIST);
		Assert.assertEquals(37, minusEndedHorizontalLines.size());
		List<ComplexLine> plusEndedHorizontalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(horizontalComplexLines, SideOrientation.PLUSLIST);
		Assert.assertEquals(0, plusEndedHorizontalLines.size()); // 0

		
		List<ComplexLine> emptyEndedVerticalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(verticalComplexLines, SideOrientation.EMPTYLIST);
		Assert.assertEquals(18, emptyEndedVerticalLines.size());
		List<ComplexLine> doubleEndedVerticalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(verticalComplexLines, SideOrientation.MINUSPLUSLIST);
		Assert.assertEquals(31, doubleEndedVerticalLines.size());
		List<ComplexLine> minusEndedVerticalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(verticalComplexLines, SideOrientation.MINUSLIST);
		Assert.assertEquals(11, minusEndedVerticalLines.size());
		List<ComplexLine> plusEndedVerticalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(verticalComplexLines, SideOrientation.PLUSLIST);
		Assert.assertEquals(23, plusEndedVerticalLines.size()); // 0

		tree.getTreeAnalyzer().extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.EMPTYLIST);
		
		CMLUtil.debug(svg, new FileOutputStream(Fixtures.PATHS_CLUSTER2A_SVG),1);
	}

	@Test
	public void testCluster7a() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.PATHS_PANEL1_SVG);
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svg,  ".//svg:line");
		List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
		
		SVGXTree tree = new SVGXTree((SVGG)svgLines.get(0).getParent());
		TreeAnalyzer treeAnalyzer = tree.getTreeAnalyzer();
		treeAnalyzer.analyzeBranchesAtLineEnds(tree, svgLines, EPS);
		
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.EMPTYLIST).size());
		Assert.assertEquals(13, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.MINUSPLUSLIST).size());
		Assert.assertEquals(15, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.MINUSLIST).size());
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.PLUSLIST).size());
		
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.VERTICAL, SideOrientation.EMPTYLIST).size());
		Assert.assertEquals(14, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.VERTICAL, SideOrientation.MINUSPLUSLIST).size());
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.VERTICAL, SideOrientation.MINUSLIST).size());
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.VERTICAL, SideOrientation.PLUSLIST).size());
	}
	
	@Test
	public void testCluster7Tree() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.PATHS_PANEL1_SVG);
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svg, ".//svg:line");
		List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
		
		SVGXTree tree = new SVGXTree((SVGG)svgLines.get(0).getParent());
		TreeAnalyzer treeAnalyzer = tree.getTreeAnalyzer();
		treeAnalyzer.analyzeBranchesAtLineEnds(tree, svgLines, EPS);
		tree.buildTree();
		
//		tree.debug("Tree");
	}

}
