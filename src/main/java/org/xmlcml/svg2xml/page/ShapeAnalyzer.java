package org.xmlcml.svg2xml.page;

import java.util.List;

import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.ShapeContainer;

import util.Path2ShapeConverter;

/**
 * Analyzes paths either direct children of chunks or extracted from them.
 * <p>
 * ShapeAnalyzer contains no member variables except PathContainer
 * </p>
 * @author pm286
 *
 */
public class ShapeAnalyzer extends ChunkAnalyzer {

	public final static Logger LOG = Logger.getLogger(ShapeAnalyzer.class);

	private ShapeContainer shapeContainer;
	
	public ShapeAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}

	/**
	 * Construct a ShapeAnalyzer with list of shapes
	 * 
	 * @param shapeList
	 * @param pageAnalyzer
	 */
	public ShapeAnalyzer(List<SVGShape> shapeList, PageAnalyzer pageAnalyzer) {
		this(pageAnalyzer);
		addShapeList(shapeList);
	}

	/**
	 * Construct a ShapeAnalyzer with descendant paths from svgElement
	 * 
	 * @param svgElement
	 * @param pageAnalyzer
	 */
	public ShapeAnalyzer(SVGElement svgElement, PageAnalyzer pageAnalyzer) {
		this(SVGShape.extractSelfAndDescendantShapes(svgElement), pageAnalyzer);
		this.svgChunk = svgElement;
	}

	public void addShapeList(List<SVGShape> shapeList) {
		if (shapeList != null) {
			ensureShapeContainer();
			shapeContainer.setShapeList(shapeList); 
		}
	}

	private void ensureShapeContainer() {
		if (this.shapeContainer == null) {
			this.shapeContainer = new ShapeContainer(this);
		}
	}

	public void setShapeContainer(ShapeContainer pathContainer) {
		this.shapeContainer = pathContainer;
	}
	
	/** a delegate accessing PathList in pathContainer.
	 * 
	 * @return pathContainer.getPathList() or null;
	 */
	public List<SVGShape> getShapeList() {
		ensureShapeContainer();
		return shapeContainer.getShapeList();
	}
	
	/** 
	 * 
	 * @param analyzerX
	 * @param suffix
	 * @param pageAnalyzer
	 * @return
	 */
	@Override
	public List<AbstractContainer> createContainers() {
		ensureShapeContainer();
		ensureAbstractContainerList();
		abstractContainerList.add(shapeContainer);
		return abstractContainerList;
	}

	public String toString() {
		ensureShapeContainer();
		String s = "";
		s += "shapes: "+shapeContainer.getShapeList().size();
		return s;
	}

	public ShapeContainer getPathContainer() {
		return shapeContainer;
	}

//	public List<SVGShape> getShapeList() {
//		return shapeContainer == null ? null : shapeContainer.getShapeList();
//	}

	public SVGElement getSVGChunk() {
		return shapeContainer == null ? null : shapeContainer.getSVGChunk();
	}

}
