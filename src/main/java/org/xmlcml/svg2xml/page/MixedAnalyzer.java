package org.xmlcml.svg2xml.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.MixedContainer;

public class MixedAnalyzer extends ChunkAnalyzer {

	static final Logger LOG = Logger.getLogger(MixedAnalyzer.class);

	private ImageAnalyzer imageAnalyzer = null;
	private ShapeAnalyzer shapeAnalyzer = null;
	private TextAnalyzer textAnalyzer = null;

	private List<ChunkAnalyzer> analyzerList;

	private Real2Range boundingBox;

	private List<SVGImage> imageList;
	private List<SVGShape> shapeList;
	private List<SVGText> textList;
	
	public MixedAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public void readImageList(List<SVGImage> imageList) {
		if (imageList != null && imageList.size() > 0) {
			imageAnalyzer = new ImageAnalyzer(pageAnalyzer);
			imageAnalyzer.addImageList(imageList);
		}
	}
	
	public void readShapeList(List<SVGShape> shapeList) {
		if (shapeList != null && shapeList.size() > 0) {
			shapeAnalyzer = new ShapeAnalyzer(pageAnalyzer);
			shapeAnalyzer.addShapeList(shapeList);
		}
	}
	
	public void readTextList(List<SVGText> textCharacters) {
		if (textCharacters != null && textCharacters.size() > 0) {
			textAnalyzer = new TextAnalyzer(pageAnalyzer);
			textAnalyzer.analyzeTexts(textCharacters);
		}
	}
	
	public List<SVGImage> getImageList() {
		if (imageList == null) {
			imageList = (imageAnalyzer == null) ? null : imageAnalyzer.getImageList();
		}
		return imageList;
	}
	
	public List<SVGShape> getShapeList() {
		if (shapeList == null) {
			shapeList = (shapeAnalyzer == null ? null : shapeAnalyzer.getShapeList());
		}
		return shapeList;
	}
	
	public List<SVGText> getTextList() {
		if (textList == null) {
			textList = (textAnalyzer == null ? null : textAnalyzer.getTextCharacters());
		}
		return textList;
	}
	
	public String toString() {
		return "" +
				"image "+(getImageList() == null ? "0" : getImageList().size())+"; "+
				"shape "+(getShapeList() == null ? "0" : getShapeList().size())+"; "+
				"text "+(getTextList() == null ? null : getTextList().size())+"; ";
	}

	public ImageAnalyzer getImageAnalyzer() {
		return imageAnalyzer;
	}
	
	public ShapeAnalyzer getShapeAnalyzer() {
		return shapeAnalyzer;
	}
	
	public TextAnalyzer getTextAnalyzer() {
		return textAnalyzer;
	}

	public void add(ChunkAnalyzer analyzer) {
		ensureAnalyzerList();
		LOG.trace("Added "+analyzer);
		setTypedAnalyzer(analyzer);
		analyzerList.add(analyzer);
	}

	private void setTypedAnalyzer(ChunkAnalyzer analyzer) {
		if (analyzer instanceof ImageAnalyzer) {
			imageAnalyzer = (ImageAnalyzer) analyzer;
		} else if (analyzer instanceof ShapeAnalyzer) {
			shapeAnalyzer = (ShapeAnalyzer) analyzer;
		} else if (analyzer instanceof TextAnalyzer) {
			textAnalyzer = (TextAnalyzer) analyzer;
		}
	}

	private void ensureAnalyzerList() {
		if (analyzerList == null) {
			analyzerList = new ArrayList<ChunkAnalyzer>();
		}
	}

	/** identify a box round the object.
	 * often (some of) the shapes create a frame, either a rect or 
	 * rounded rect. This is often made of separate shapes along the edges
	 * and perhaps with rounded corners. This is a simple heuristic. 
	 * Maybe we'll add more later.
	 * 
	 * The box can then be removed by detach()ing the shapes
	 * 
	 * @return empty list if none found
	 */
	public List<SVGShape> getFrameBox(double eps) {
		List<SVGShape> box = new ArrayList<SVGShape>();
		List<SVGShape> shapeList = getShapeList();
		if (shapeList != null) {
			this.getBoundingBox();
			List<Real2Range> edgeBoxes = createEdgeBoxes(boundingBox, eps);
			for (SVGShape shape : shapeList) {
				Real2Range bbox = shape.getBoundingBox();
				if (bbox.isContainedInAnyRange(edgeBoxes)) {
					box.add(shape);
				}
			}
		}
		return box;
	}

	private List<Real2Range> createEdgeBoxes(Real2Range boundingBox, double eps) {
		List<Real2Range> edgeBoxes = new ArrayList<Real2Range>();
		Real2 corner0 = boundingBox.getCorners()[0];
		Real2 corner1 = boundingBox.getCorners()[1];
		Double width = boundingBox.getXRange().getRange();
		Double height = boundingBox.getYRange().getRange();
		edgeBoxes.add(new Real2Range(corner0, corner0.plus(new Real2(eps, height)))); // left
		edgeBoxes.add(new Real2Range(corner1.plus(new Real2(-eps, -height)), corner1)); // right
		edgeBoxes.add(new Real2Range(corner0, corner0.plus(new Real2(width, eps)))); // bottom
		edgeBoxes.add(new Real2Range(corner1.plus(new Real2(-width, -eps)), corner1)); // top
		
		return edgeBoxes;
	}

	public Real2Range getBoundingBox() {
		if (boundingBox == null) {
			addToBoundingBox(getTextList());
			addToBoundingBox(getShapeList());
			addToBoundingBox(getImageList());
		}
		return boundingBox;
	}

	private void addToBoundingBox(List<? extends SVGElement> svgElementList) {
		if (svgElementList != null) {
			Real2Range bbox = SVGElement.createBoundingBox(svgElementList);
			if (boundingBox == null) {
				boundingBox = bbox;
			} else {
				boundingBox = boundingBox.plus(bbox);
			}
		}
	}

	public boolean removeFrameBoxFromShapeList() {
		List<SVGShape> frameBox = getFrameBox(5.0);
		if (frameBox.size() >= 4) {
			for (SVGShape shape : frameBox) {
				if (!shapeList.remove(shape)) {
					LOG.debug("cannot remove shape");
				}
			}
			return true;
		}
		return false;
	}

	public SVGG getSVGG() {
		SVGG svgG = new SVGG();
		svgG.copyElementsFrom(textList);
		svgG.copyElementsFrom(shapeList);
		svgG.copyElementsFrom(imageList);
		return svgG;
	}

	public void normalize() {
		normalizeShapeAnalyzers();
		normalizeTextAnalyzers();
		normalizeImageAnalyzers();
	}

	void normalizeShapeAnalyzers() {
		if (shapeList != null && shapeList.size() == 0) {
			shapeList = null;
		}
		if (shapeList == null) {
			shapeAnalyzer = null;
		}
		if (analyzerList != null) {
			List<ChunkAnalyzer> newAnalyzerList = new ArrayList<ChunkAnalyzer>();
			for (ChunkAnalyzer analyzer : analyzerList) {
				if (!(analyzer instanceof ShapeAnalyzer)) {
					newAnalyzerList.add(analyzer);
				}
			}
			analyzerList = newAnalyzerList;
		}
	}

	void normalizeImageAnalyzers() {
		if (imageList != null && imageList.size() == 0) {
			imageList = null;
		}
		if (imageList == null) {
			imageAnalyzer = null;
		}
		if (analyzerList != null) {
			List<ChunkAnalyzer> newAnalyzerList = new ArrayList<ChunkAnalyzer>();
			for (ChunkAnalyzer analyzer : analyzerList) {
				if (!(analyzer instanceof ImageAnalyzer)) {
					newAnalyzerList.add(analyzer);
				}
			}
			analyzerList = newAnalyzerList;
		}
	}

	void normalizeTextAnalyzers() {
		if (textList != null && textList.size() == 0) {
			textList = null;
		}
		if (textList == null) {
			textAnalyzer = null;
		}
		if (analyzerList != null) {
			List<ChunkAnalyzer> newAnalyzerList = new ArrayList<ChunkAnalyzer>();
			for (ChunkAnalyzer analyzer : analyzerList) {
				if (!(analyzer instanceof TextAnalyzer)) {
					newAnalyzerList.add(analyzer);
				}
			}
			analyzerList = newAnalyzerList;
		}
	}

	public String getAnalyzerType() {
		String type = "";
		if (imageList != null) {
			type += "+"+ImageAnalyzer.class.getSimpleName();
		}
		if (shapeList != null) {
			type += "+"+ShapeAnalyzer.class.getSimpleName();
		}
		if (textList != null) {
			type += "+"+TextAnalyzer.class.getSimpleName();
		}
		return type;
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
		MixedContainer mixedContainer = new MixedContainer(pageAnalyzer);
		if (removeFrameBoxFromShapeList()) {
			mixedContainer.setBox(true);
		}
		mixedContainer.setChunkId(getChunkId());
		mixedContainer.addImageList(getImageList());
		mixedContainer.addShapeList(getShapeList());
		mixedContainer.addTextList(getTextList());
		ensureAbstractContainerList();
		abstractContainerList.add(mixedContainer);
		return abstractContainerList;
	}

}
