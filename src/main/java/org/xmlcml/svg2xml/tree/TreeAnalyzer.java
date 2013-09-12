package org.xmlcml.svg2xml.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.svg2xml.paths.ComplexLine;
import org.xmlcml.svg2xml.paths.ComplexLine.LineOrientation;
import org.xmlcml.svg2xml.paths.ComplexLine.SideOrientation;

/** internal engine that does the hard work of creating the tree
 * output is delegated from SVGXTree
 * holds annotation of lines and also 
 * @author pm286
 *
 */
public class TreeAnalyzer {
	private static final Logger LOG = Logger.getLogger(TreeAnalyzer.class);
	
	private List<ComplexLine> doubleEndedHorizontalLines;
	private List<ComplexLine> doubleEndedVerticalLines;
	private List<ComplexLine> emptyEndedHorizontalLines;
	private List<ComplexLine> emptyEndedVerticalLines;
	private List<ComplexLine> minusEndedHorizontalLines;
	private List<ComplexLine> minusEndedVerticalLines;
	private List<ComplexLine> plusEndedHorizontalLines;
	private List<ComplexLine> plusEndedVerticalLines;
	private List<ComplexLine> horizontalComplexLines;
	private List<SVGLine> horizontalLines;
	private List<ComplexLine> verticalComplexLines;
	private List<SVGLine> verticalLines;
	private List<ComplexLine> singleEndedLines;
	private Stack<SVGXTreeEdge> edgeStack;
	private Map<SVGLine, SVGXTreeEdge> treeEdgeByLineMap;
	private Map<String, SVGXTreeNode> treeNodeByIdMap;
	private Map<SVGLine, SVGXTreeNode> treeNodeByLineMap;
	private LineOrientation treeOrientation;
	SVGXTree tree;
	
	Map<SVGLine, ComplexLine> complexLineByLineMap;
	Map<String, SVGXTreeEdge> treeEdgeByIdMap;
	List<SVGXTreeEdge> edgeList;
	List<SVGXTreeNode> nodeList;
	SideOrientation treeSideOrientation;

	public TreeAnalyzer(SVGXTree tree) {
		this.tree = tree;
	}
	/** separates lines into horizontal and vertical and classifies them by
	 * what branches or none they have at the ends
	 * these can be returned by
	 * extractLinesWithBranchAtEnd(LineOrientation, SideOrientation)
	 * 
	 * @param svgxTree TODO
	 * @param svgLines
	 */
	public void analyzeBranchesAtLineEnds(SVGXTree svgxTree, List<SVGLine> svgLines, double eps) {
		this.verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, eps);
		this.horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, eps);
		this.verticalComplexLines = ComplexLine.createComplexLines(this.verticalLines, this.horizontalLines, eps);
		this.horizontalComplexLines = ComplexLine.createComplexLines(this.horizontalLines, this.verticalLines, eps);
		
		this.emptyEndedHorizontalLines = ComplexLine.extractLinesWithBranchAtEnd(this.horizontalComplexLines, SideOrientation.EMPTYLIST);
		this.doubleEndedHorizontalLines = ComplexLine.extractLinesWithBranchAtEnd(this.horizontalComplexLines, SideOrientation.MINUSPLUSLIST);
		this.minusEndedHorizontalLines = ComplexLine.extractLinesWithBranchAtEnd(this.horizontalComplexLines, SideOrientation.MINUSLIST);
		this.plusEndedHorizontalLines = ComplexLine.extractLinesWithBranchAtEnd(this.horizontalComplexLines, SideOrientation.PLUSLIST);
	
		this.emptyEndedVerticalLines = ComplexLine.extractLinesWithBranchAtEnd(this.verticalComplexLines, SideOrientation.EMPTYLIST);
		this.doubleEndedVerticalLines = ComplexLine.extractLinesWithBranchAtEnd(this.verticalComplexLines, SideOrientation.MINUSPLUSLIST);
		this.minusEndedVerticalLines = ComplexLine.extractLinesWithBranchAtEnd(this.verticalComplexLines, SideOrientation.MINUSLIST);
		this.plusEndedVerticalLines = ComplexLine.extractLinesWithBranchAtEnd(this.verticalComplexLines, SideOrientation.PLUSLIST);
	
		this.indexComplexLines();
		this.checkEndLineCounts();
	}

	private boolean checkEndLineCounts() {
		singleEndedLines = null;
		setTreeOrientation(null);
		treeSideOrientation = null;
		boolean goodTree = true;
		if (horizontalLines.size() > verticalLines.size()) {
			setTreeOrientation(LineOrientation.HORIZONTAL);
			if (minusEndedHorizontalLines.size() > plusEndedHorizontalLines.size()) {
				treeSideOrientation = SideOrientation.PLUS;
				goodTree = (plusEndedHorizontalLines.size() - 1 != doubleEndedVerticalLines.size());
				goodTree |= (doubleEndedHorizontalLines.size() + 1 != doubleEndedVerticalLines.size());
				singleEndedLines = minusEndedHorizontalLines;
			} else if (minusEndedHorizontalLines.size() < plusEndedHorizontalLines.size()) {
				treeSideOrientation = SideOrientation.MINUS;
				goodTree = (minusEndedHorizontalLines.size() - 1 != doubleEndedVerticalLines.size());
				goodTree |= (doubleEndedHorizontalLines.size() + 1 != doubleEndedVerticalLines.size());
				singleEndedLines = plusEndedHorizontalLines;
			}
		} else if (verticalLines.size() > horizontalLines.size()) {
			setTreeOrientation(LineOrientation.VERTICAL);
			if (minusEndedVerticalLines.size() > plusEndedVerticalLines.size()) {
				treeSideOrientation = SideOrientation.PLUS;
				goodTree = (plusEndedVerticalLines.size() - 1 != doubleEndedHorizontalLines.size());
				goodTree |= (doubleEndedVerticalLines.size() + 1 != doubleEndedHorizontalLines.size());
				singleEndedLines = minusEndedVerticalLines;
			} else if (minusEndedHorizontalLines.size() < plusEndedHorizontalLines.size()) {
				treeSideOrientation = SideOrientation.MINUS;
				goodTree = (minusEndedVerticalLines.size() - 1 != doubleEndedHorizontalLines.size());
				goodTree |= (doubleEndedVerticalLines.size() + 1 != doubleEndedHorizontalLines.size());
				singleEndedLines = plusEndedVerticalLines;
			}
		}
		return goodTree;
	}

	private void indexComplexLines() {
		complexLineByLineMap = new HashMap<SVGLine, ComplexLine>();
		for (ComplexLine complexLine : verticalComplexLines) {
			complexLineByLineMap.put(complexLine.getBackbone(), complexLine);
		}
		for (ComplexLine complexLine : horizontalComplexLines) {
			complexLineByLineMap.put(complexLine.getBackbone(), complexLine);
		}
	}

	public List<ComplexLine> extractLinesWithBranchAtEnd(LineOrientation lineOrientation, List<SideOrientation> sideOrientationList) {
		List<ComplexLine> complexLineList = null;
		if (LineOrientation.HORIZONTAL.equals(lineOrientation)) {
			if (SideOrientation.EMPTYLIST.equals(sideOrientationList)) {
				complexLineList = emptyEndedHorizontalLines;
			} else if (SideOrientation.MINUSPLUSLIST.equals(sideOrientationList)) {
				complexLineList = doubleEndedHorizontalLines;
			} else if (SideOrientation.MINUSLIST.equals(sideOrientationList)) {
				complexLineList = minusEndedHorizontalLines;
			} else if (SideOrientation.PLUSLIST.equals(sideOrientationList)) {
				complexLineList = plusEndedHorizontalLines;
			}
			
		} else if (LineOrientation.VERTICAL.equals(lineOrientation)) {
			
			if (SideOrientation.EMPTYLIST.equals(sideOrientationList)) {
				complexLineList = emptyEndedVerticalLines;
			} else if (SideOrientation.MINUSPLUSLIST.equals(sideOrientationList)) {
				complexLineList = doubleEndedVerticalLines;
			} else if (SideOrientation.MINUSLIST.equals(sideOrientationList)) {
				complexLineList = minusEndedVerticalLines;
			} else if (SideOrientation.PLUSLIST.equals(sideOrientationList)) {
				complexLineList = plusEndedVerticalLines;
			}
		}
		return complexLineList;
	}

	private void findEmptyEndsAndPushCreatedEdgesOntoStack() {
		if (singleEndedLines != null) {
			for (ComplexLine singleEndedLine : singleEndedLines) {
				Real2 otherPoint = singleEndedLine.getCornerAt(treeSideOrientation);
				SVGXTreeNode node = new SVGXTreeNode(this, otherPoint);
				SVGXTreeEdge edge = node.addParentEdge(singleEndedLine);
				edgeStack.push(edge);
			}
		}
	}

	public void buildTree() {
		edgeStack = new Stack<SVGXTreeEdge>();
		Set<SVGXTreeEdge> edgeSet = new HashSet<SVGXTreeEdge>();
		nodeList = new ArrayList<SVGXTreeNode>();
		edgeList = new ArrayList<SVGXTreeEdge>();
		treeEdgeByIdMap = new HashMap<String, SVGXTreeEdge>();
		// starting nodes
		findEmptyEndsAndPushCreatedEdgesOntoStack();
		processEdgeStackTillEmpty(edgeSet);
		addEdgesToNodes();
	}
	
	private void processEdgeStackTillEmpty(Set<SVGXTreeEdge> edgeSet) {
		while (!edgeStack.empty()) {
			SVGXTreeEdge edge = edgeStack.pop();
			SVGXTreeNode node = edge.createAndAddParentNode();
			if (node != null) {
				edge = node.getParentEdge();
				if (edge != null && !edgeSet.contains(edge)) {
					edgeStack.push(edge);
					edgeSet.add(edge);
				}
			}
		}
	}
	
	private void addEdgesToNodes() {
		for (SVGXTreeEdge edge : edgeList) {
			LOG.trace("\n"+edge.getString());
			SVGXTreeNode childNode = edge.getChildNode();
			SVGXTreeNode parentNode = edge.getParentNode();
			childNode.addParentAndChild(parentNode, edge);
			
		}
	}

	void ensureNodeList() {
		if (nodeList == null) {
			nodeList = new ArrayList<SVGXTreeNode>();
		}
	}

	void ensureEdgeList() {
		if (edgeList == null) {
			edgeList = new ArrayList<SVGXTreeEdge>();
		}
	}

	Map<String, SVGXTreeEdge> ensureTreeEdgeByIdMap() {
		if (treeEdgeByIdMap == null) {
			treeEdgeByIdMap = new HashMap<String, SVGXTreeEdge>();
		}
		return treeEdgeByIdMap;						
	}

	Map<SVGLine, SVGXTreeEdge> ensureTreeEdgeByLineMap() {
		if (treeEdgeByLineMap == null) {
			treeEdgeByLineMap = new HashMap<SVGLine, SVGXTreeEdge>();
		}
		return treeEdgeByLineMap;						
	}

	private Map<String, SVGXTreeNode> ensureTreeNodeByIdMap() {
		if (treeNodeByIdMap == null) {
			treeNodeByIdMap = new HashMap<String, SVGXTreeNode>();
		}
		return treeNodeByIdMap;
	}

	private Map<SVGLine, SVGXTreeNode> ensureTreeNodeByLineMap() {
		if (treeNodeByLineMap == null) {
			treeNodeByLineMap = new HashMap<SVGLine, SVGXTreeNode>();
		}
		return treeNodeByLineMap;
	}

	SVGXTreeNode getOrCreateNode(SVGLine line) {
		SVGXTreeNode node = null;
		ensureTreeNodeByLineMap();
		ensureTreeNodeByIdMap();
		node = treeNodeByLineMap.get(line);
		if (node == null) {
			ComplexLine complexLine = complexLineByLineMap.get(line);
			node = new SVGXTreeNode(this, complexLine);
			treeNodeByLineMap.put(line, node);
			treeNodeByIdMap.put(node.getId(), node);
		}
		return node;
	}
	
	public List<SVGXTreeNode> getNodeList() {
		ensureNodeList();
		return nodeList;
	}

	public List<SVGXTreeEdge> getEdgeList() {
		ensureEdgeList();
		return edgeList;
	}
	
	public SideOrientation getTreeSideOrientation() {
		return treeSideOrientation;
	}
	
	public LineOrientation getTreeOrientation() {
		return treeOrientation;
	}
	
	public void setTreeOrientation(LineOrientation treeOrientation) {
		this.treeOrientation = treeOrientation;
	}
	
}
