package org.xmlcml.svg2xml.tree;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.page.BoundingBoxManager;
import org.xmlcml.svg2xml.paths.ComplexLine.LineOrientation;
import org.xmlcml.svg2xml.paths.ComplexLine.SideOrientation;
import org.xmlcml.svg2xml.paths.LineMerger;
import org.xmlcml.svg2xml.paths.LineMerger.MergeMethod;
import org.xmlcml.xml.XMLConstants;
import org.xmlcml.xml.XMLUtil;

public class SVGXTree extends SVGG {
	
	private final static Logger LOG = Logger.getLogger(SVGXTree.class);
	
	private static final String CHILD = "child";
	private static final String EDGE = "edge";
	private static final String FLOAT_TREE = "FloatTree";
	private static final String ID = "id";
	private static final String LABEL = "label";
	private static final String NEX = "nex";
	private static final String NEXML = "nexml";
	private static final String NEXML_NS = "http://www.nexml.org/2009";
	private static final String NODE= "node";
	private static final String OTU = "otu";
	private static final String OTUS = "otus";
	private static final String ROOT = "root";
	private static final String SOURCE = "source";
	private static final String T = "t";
	private static final String TARGET = "target";
	private static final String TAX1 = "tax1";
	private static final String TREE = "tree";
	private static final String TREE1 = "tree1";
	private static final String TREES = "trees";
	private static final String TRUE = "true";
	private static final String TYPE = "type";
	private static final String XSI = "xsi";
	private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	
	public static final double EPS = 0.01;

	
	private double eps;
	private SVGElement parentSVGElement;
	private TreeAnalyzer treeAnalyzer;
	private List<SVGXTreeNode> rootNodeList;
	private ArrayList<SVGXTreeNode> childlessNodeList;
	private double edgeLengthFontSize = 6.;
	private String edgeLengthFill = "red";

	private Element xmlTree;

	public SVGXTree(SVGElement parentSVGG) {
		this.eps = EPS;
		this.parentSVGElement = parentSVGG;
		this.setId("tree."+parentSVGG.getId());
		this.treeAnalyzer = new TreeAnalyzer(this);
		this.setId(parentSVGG.getId());
	}

	/** create tree from SVG
	 * 
	 * @param container selected SVG container (normally a G or SVG)
	 * @param eps error margin
	 * @param method of line join (try MergeMethod.TOUCHING_LINES)
	 * @return
	 */
	public static SVGXTree makeTree(SVGElement container, double eps, MergeMethod method) {
		SVGPolyline.replacePolyLinesBySplitLines(container);
		List<SVGLine> lines = SVGLine.extractSelfAndDescendantLines(container);
		lines = LineMerger.mergeLines(lines, eps, method);		
		SVGXTree tree = new SVGXTree(container);
		TreeAnalyzer treeAnalyzer = tree.getTreeAnalyzer();
		treeAnalyzer.analyzeBranchesAtLineEnds(tree, lines, eps);
		tree.buildTree();
		return tree;
	}
	
	public void setEpsilon(double eps) {
		this.eps = eps;
	}
	
	public LineOrientation getTreeOrientation() {
		return getTreeAnalyzer().getTreeOrientation();
	}
	
	public SideOrientation getTreeSideOrientation() {
		return getTreeAnalyzer().getTreeSideOrientation();
	}
	
	public List<SVGXTreeEdge> getEdgeList() {
		return getTreeAnalyzer().getEdgeList();
	}

	public List<SVGXTreeNode> getNodeList() {
		return getTreeAnalyzer().getNodeList();
	}

	public List<SVGXTreeNode> getChildlessNodeList() {
		if (childlessNodeList == null) {
			this.childlessNodeList = new ArrayList<SVGXTreeNode>();
			for (SVGXTreeNode node : getNodeList()) {
				if (node.childNodeList == null || node.childNodeList.size() == 0) {
					childlessNodeList.add(node);
				}
			}
		}
		return childlessNodeList;
	}

	public SVGXTreeNode getRootNode() {
		ensureRootNodeList();
		return (rootNodeList.size() == 1) ? rootNodeList.get(0): null;
	} 

	public void buildTree() {
		getTreeAnalyzer().buildTree();
		decorateWithLengthsAndTexts();
	}

	private void decorateWithLengthsAndTexts() {
		addSVGLinkLinesAndRootNode();
		addLengthsToSVG();
		addTexts();
		Element xmlTree = buildXMLTree();
		if (xmlTree != null) {
			LOG.trace("TREE "+xmlTree.toXML());
			XMLUtil.outputQuietly(xmlTree, new File("target/temp.tree.xml"), 1);
			displayTree("target/temp.svg");
		}
	}
	
	private Element buildXMLTree() {
		if (xmlTree == null) {
			xmlTree = null;
			SVGXTreeNode rootNode = getRootNode();
			xmlTree = createElementAndAddDescendants(rootNode);
		}
		return xmlTree;
	}

	public String createNewick() {
		buildXMLTree();
		String s = "no tree";
		if (xmlTree != null) {
			StringBuilder sb = new StringBuilder();
			Element element = xmlTree;
			sb.insert(0,  XMLConstants.S_SEMICOLON);
			processElement(sb, element);
			s = sb.toString();
		}
		return s;
	}
	
	void processElement(StringBuilder sb, Element element) {
		String label = element.getAttributeValue(LABEL);
		if (label == null) {
			label = element.getAttributeValue(ID);
		}
		sb.insert(0, label);
		Elements childElements = element.getChildElements();
		if (childElements.size() > 0) {
			sb.insert(0,XMLConstants.S_RBRAK);
			for (int i = 0; i < childElements.size(); i++) {
				if (i > 0) {
					sb.insert(0,  XMLConstants.S_COMMA);
				}
				processElement(sb, childElements.get(i));
			}
			sb.insert(0,XMLConstants.S_LBRAK);
		}
	}
	
	private Element createElementAndAddDescendants(SVGXTreeNode treeNode) {
		Element node = null;
		if (treeNode !=null) {
			node = new Element("node");
			if (treeNode.getId() != null) {
				node.addAttribute(new Attribute(ID, treeNode.getId()));
				LOG.trace("ID "+treeNode.getId());
			}
			if (treeNode.text != null) {
				node.addAttribute(new Attribute(LABEL, treeNode.text.getValue()));
			}
			if (treeNode.childNodeList != null) {
				for (SVGXTreeNode childTreeNode : treeNode.childNodeList) {
					Element childNode = createElementAndAddDescendants(childTreeNode);
					node.appendChild(childNode);
				}
			}
		}
		return node;
	}

	private void addTexts() {
		double minNodeDelta = 0.0;
		double maxNodeDelta = 5.0;

		List<Real2Range> childlessBBoxes = createExtendedBoxes(getChildlessNodeList(), minNodeDelta, maxNodeDelta,
			getTreeOrientation(), getTreeSideOrientation());
		List<SVGElement> textList = SVGUtil.getQuerySVGElements(parentSVGElement, "./svg:text");
		SideOrientation sideOrientation = getTreeSideOrientation();
		if (sideOrientation != null) {
			List<Real2Range> textBBoxes = createExtendedBoxes(textList, minNodeDelta, maxNodeDelta,
					getTreeOrientation(), sideOrientation.getOtherOrientation());
			for (int i = 0; i < getChildlessNodeList().size(); i++) {
				Real2Range nodeBox = childlessBBoxes.get(i);
				for (int j = 0; j < textBBoxes.size(); j++) {
					Real2Range textBox = textBBoxes.get(j);
					Real2Range inter = nodeBox.intersectionWith(textBox);
					if (inter != null) {
						childlessNodeList.get(i).addText(OTU, textList.get(j).getValue());
					}
				}
			}
		}
	}

	private List<Real2Range> createExtendedBoxes(List<? extends SVGElement> nodeList, double minNodeDelta, double maxNodeDelta,
			LineOrientation orientation, SideOrientation direction) {
		RealRange xExtension = null;
		RealRange yExtension = null;
		if (LineOrientation.HORIZONTAL.equals(orientation)) {
			yExtension = new RealRange(-minNodeDelta, minNodeDelta);
			if (SideOrientation.PLUS.equals(direction)) {
				xExtension = new RealRange(-minNodeDelta, maxNodeDelta);
			} else if (SideOrientation.MINUS.equals(direction)) {
				xExtension = new RealRange(-maxNodeDelta, minNodeDelta);
			}
		} else if (LineOrientation.VERTICAL.equals(orientation)) {
			xExtension = new RealRange(-minNodeDelta, minNodeDelta);
			if (SideOrientation.PLUS.equals(direction)) {
				yExtension = new RealRange(-minNodeDelta, maxNodeDelta);
			} else if (SideOrientation.MINUS.equals(direction)) {
				yExtension = new RealRange(-maxNodeDelta, minNodeDelta);
			}
		}
		LOG.trace(xExtension+" | "+yExtension);
		List<Real2Range> extendedBBoxes = BoundingBoxManager.createExtendedBBoxList(nodeList, 
				xExtension, yExtension);
		for (Real2Range r2r : extendedBBoxes) {
			LOG.trace(r2r);
		}
		return extendedBBoxes;
	}

	private void addSVGLinkLinesAndRootNode() {
		for (SVGXTreeNode node : getNodeList()) {
			SVGXTreeNode parentNode = node.getParentTreeNode();
			if (parentNode != null) {
				Real2 thisPoint = node.getCentroid();
				Real2 parentPoint = parentNode.getCentroid();
				SVGLine line = new SVGLine(thisPoint, parentPoint);
				line.setStroke("blue");
				this.appendChild(line);
			} else {
				ensureRootNodeList();
				rootNodeList.add(node);
			}
			
		}
	}

	private void ensureRootNodeList() {
		if (rootNodeList == null) {
			rootNodeList = new ArrayList<SVGXTreeNode>();
		}
	}
	
	private void addLengthsToSVG() {
		for (SVGXTreeEdge edge : getEdgeList()) {
			SVGLine line = edge.getLine(); 
			Real2 mid = line.getBoundingBox().getCentroid();
			double d = edge.getLength();
			SVGText text = new SVGText(mid, ""+(int)d);
			text.setFontSize(edgeLengthFontSize);
			text.setFill(edgeLengthFill);
			this.appendChild(text);
		}
	}

	private void displayTree(String filename) {
		SVGSVG svg = new SVGSVG();
		svg.appendChild(this.copy());
		try {
			SVGUtil.debug(svg, new FileOutputStream(filename), 1);
		} catch (Exception e) {
			throw new RuntimeException("Cannot write: ", e);
		}
	}

	public TreeAnalyzer getTreeAnalyzer() {
		return treeAnalyzer;
	}

	void createFromChildren(SVGG svgg, String xpath, double eps) {
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svgg, xpath);
		if (lineElements.size() != 0) {
			LOG.trace("making tree with "+lineElements.size()+" lines");
			List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
			getTreeAnalyzer().analyzeBranchesAtLineEnds(this, svgLines, eps);
			buildTree();
			svgg.appendChild(this);
		}
	}

	public Element getNEXML() {
		// rot node
		Element nexml = new Element(NEXML, NEXML_NS);
		nexml.addAttribute(new Attribute(ID, this.getId()));
		nexml.addNamespaceDeclaration(NEX, NEXML_NS);
		nexml.addNamespaceDeclaration(XSI, XSI_NS);
		// otus block
		Element otus = new Element(OTUS, NEXML_NS);
		otus.addAttribute(new Attribute(ID, TAX1));
		otus.addAttribute(new Attribute(LABEL, "RootTaxaBlock"));
		nexml.appendChild(otus);
		// trees block
		Element trees = new Element(TREES, NEXML_NS);
		trees.addAttribute(new Attribute(LABEL, "TreesBlockFromXML"));
		trees.addAttribute(new Attribute(ID, "Trees"));
		trees.addAttribute(new Attribute(OTUS, TAX1));
		nexml.appendChild(trees);
		// tree
		Element tree = new Element(TREE, NEXML_NS);
		tree.addAttribute(new Attribute(ID, TREE1));
		tree.addAttribute(new Attribute(LABEL, TREE1));
		tree.addAttribute(new Attribute(XSI+":"+TYPE, XSI_NS, NEX+":"+FLOAT_TREE));
		trees.appendChild(tree);
		addNodesAndOtus(otus, tree);
		addEdges(tree);
		return nexml;
	}

	private void addNodesAndOtus(Element otus, Element tree) {
		int otuCount = 0;
		for (SVGXTreeNode svgxNode : getNodeList()) {
			Element node = new Element(NODE, NEXML_NS);
			tree.appendChild(node);
			node.addAttribute(new Attribute(ID, svgxNode.getId()));
			node.addAttribute(new Attribute(LABEL, svgxNode.getId()));
			if (svgxNode.getParentTreeNode() == null) {
				node.addAttribute(new Attribute(ROOT, TRUE));
			}
			List<SVGElement> elements = SVGUtil.getQuerySVGElements(svgxNode, "./svg:text[@title='"+OTU+"']");
			if (elements.size() == 1) {
				String otuId = T+(++otuCount);
				node.addAttribute(new Attribute(OTU, otuId));
				Element otu = new Element(OTU, NEXML_NS);
				otu.addAttribute(new Attribute(ID, otuId));
				node.addAttribute(new Attribute(LABEL, elements.get(0).getValue()));
				otus.appendChild(otu);
			}
		}
	}

	private void addEdges(Element tree) {
		for (SVGXTreeEdge svgxEdge : getEdgeList()) {
			Element edge = new Element(EDGE, NEXML_NS);
			tree.appendChild(edge);
			edge.addAttribute(new Attribute(ID, svgxEdge.getId()));
			edge.addAttribute(new Attribute(LABEL, svgxEdge.getId()));
			String parentId = svgxEdge.getAttributeValue(SVGXTreeEdge.PARENT);
			if (parentId != null) {
				edge.addAttribute(new Attribute(SOURCE, parentId));
			}
			String childId = svgxEdge.getAttributeValue(SVGXTreeEdge.CHILD);
			if (childId != null) {
				edge.addAttribute(new Attribute(TARGET, childId));
			}
		}
	}

}
