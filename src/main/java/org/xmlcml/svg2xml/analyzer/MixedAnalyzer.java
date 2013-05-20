package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.container.DivContainer;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

public class MixedAnalyzer extends AbstractAnalyzer {

	static final Logger LOG = Logger.getLogger(MixedAnalyzer.class);

	private ImageAnalyzerX imageAnalyzer = null;
	private PathAnalyzerX pathAnalyzer = null;
	private TextAnalyzerX textAnalyzer = null;

	private List<AbstractAnalyzer> analyzerList;

	private Real2Range boundingBox;

	private List<SVGImage> imageList;
	private List<SVGPath> pathList;
	private List<SVGText> textList;
	
	public MixedAnalyzer() {
		super();
	}
	
	public MixedAnalyzer(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}
	
	public void readImageList(List<SVGImage> imageList) {
		if (imageList != null && imageList.size() > 0) {
			imageAnalyzer = new ImageAnalyzerX();
			imageAnalyzer.readImageList(imageList);
		}
	}
	
	public void readPathList(List<SVGPath> pathList) {
		if (pathList != null && pathList.size() > 0) {
			pathAnalyzer = new PathAnalyzerX();
			pathAnalyzer.readPathList(pathList);
		}
	}
	
	public void readTextList(List<SVGText> textCharacters) {
		if (textCharacters != null && textCharacters.size() > 0) {
			textAnalyzer = new TextAnalyzerX();
			textAnalyzer.analyzeTexts(textCharacters);
		}
	}
	
	public List<SVGImage> getImageList() {
		if (imageList == null) {
			imageList = (imageAnalyzer == null) ? null : imageAnalyzer.getImageList();
		}
		return imageList;
	}
	
	public List<SVGPath> getPathList() {
		if (pathList == null) {
			pathList = (pathAnalyzer == null) ? null : pathAnalyzer.getPathList();
		}
		return pathList;
	}
	
	public List<SVGText> getTextList() {
		if (textList == null) {
			textList = (textAnalyzer == null) ? null : textAnalyzer.getTextCharacters();
		}
		return textList;
	}
	
	public String toString() {
		return "" +
				"image "+(getImageList() == null ? "0" : getImageList().size())+"; "+
				"path "+(getPathList() == null ? "0" : getPathList().size())+"; "+
				"text "+(getTextList() == null ? null : getTextList().size())+"; ";

	}

	public ImageAnalyzerX getImageAnalyzer() {return imageAnalyzer;}
	public PathAnalyzerX getPathAnalyzer() {return pathAnalyzer;}
	public TextAnalyzerX getTextAnalyzer() {return textAnalyzer;}

	/** annotates each section with its own analyzer.annotateChunk()
	 * 
	 */
	@Override
	public SVGG annotateChunk() {
		ensureAnalyzerList();
		SVGG g = new SVGG();
		for (AbstractAnalyzer analyzer : analyzerList) {
			SVGG gg = analyzer.annotateChunk();
			if (gg != null) {
				g.appendChild(gg.copy());
			}
		}
		String title = "MIXED: "+this;
		g.setTitle(title);
		return g;
	}
	
	@Override
	protected HtmlElement createHtml() {
		HtmlDiv element = new HtmlDiv();
		for (AbstractAnalyzer analyzer : analyzerList) {
			HtmlDiv div = new HtmlDiv();
			element.appendChild(div);
			HtmlElement childElement = analyzer.createHtml();
			if (childElement != null) {
				div.appendChild(childElement);
			}
		}
		return element;
	}

	public void add(AbstractAnalyzer analyzer) {
		ensureAnalyzerList();
		LOG.trace("Added "+analyzer);
		setTypedAnalyzer(analyzer);
		analyzerList.add(analyzer);
	}

	private void setTypedAnalyzer(AbstractAnalyzer analyzer) {
		if (analyzer instanceof ImageAnalyzerX) {
			imageAnalyzer = (ImageAnalyzerX) analyzer;
		} else if (analyzer instanceof PathAnalyzerX) {
			pathAnalyzer = (PathAnalyzerX) analyzer;
		} else if (analyzer instanceof TextAnalyzerX) {
			textAnalyzer = (TextAnalyzerX) analyzer;
		}
	}

	private void ensureAnalyzerList() {
		if (analyzerList == null) {
			analyzerList = new ArrayList<AbstractAnalyzer>();
		}
	}

	/** identify a box round the object.
	 * often (some of) the paths create a frame, either a rect or 
	 * rounded rect. This is often made of separate paths along the edges
	 * and perhaps with rounded corners. This is a simple heuristic. 
	 * Maybe we'll add more later.
	 * 
	 * The box can then be removed by detach()ing the paths
	 * 
	 * @return empty list if none found
	 */
	public List<SVGPath> getFrameBox(double eps) {
		List<SVGPath> box = new ArrayList<SVGPath>();
		List<SVGPath> pathList = getPathList();
		if (pathList != null) {
			this.getBoundingBox();
			List<Real2Range> edgeBoxes = createEdgeBoxes(boundingBox, eps);
			for (SVGPath path : pathList) {
				Real2Range bbox = path.getBoundingBox();
				if (bbox.isContainedInAnyRange(edgeBoxes)) {
					box.add(path);
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
			addToBoundingBox(getPathList());
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

	public boolean removeFrameBoxFromPathList() {
		List<SVGPath> frameBox = getFrameBox(5.0);
		if (frameBox.size() >= 4) {
			for (SVGPath path : frameBox) {
				if (!pathList.remove(path)) {
					LOG.debug("cannot remove path");
				}
			}
			return true;
		}
		return false;
	}

	public SVGG getSVGG() {
		SVGG svgG = new SVGG();
		svgG.copyElementsFrom(textList);
		svgG.copyElementsFrom(pathList);
		svgG.copyElementsFrom(imageList);
		return svgG;
	}

	public void normalize() {
		normalizePathAnalyzers();
		normalizeTextAnalyzers();
		normalizeImageAnalyzers();
	}

	void normalizePathAnalyzers() {
		if (pathList != null && pathList.size() == 0) {
			pathList = null;
		}
		if (pathList == null) {
			pathAnalyzer = null;
		}
		if (analyzerList != null) {
			List<AbstractAnalyzer> newAnalyzerList = new ArrayList<AbstractAnalyzer>();
			for (AbstractAnalyzer analyzer : analyzerList) {
				if (!(analyzer instanceof PathAnalyzerX)) {
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
			List<AbstractAnalyzer> newAnalyzerList = new ArrayList<AbstractAnalyzer>();
			for (AbstractAnalyzer analyzer : analyzerList) {
				if (!(analyzer instanceof ImageAnalyzerX)) {
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
			List<AbstractAnalyzer> newAnalyzerList = new ArrayList<AbstractAnalyzer>();
			for (AbstractAnalyzer analyzer : analyzerList) {
				if (!(analyzer instanceof TextAnalyzerX)) {
					newAnalyzerList.add(analyzer);
				}
			}
			analyzerList = newAnalyzerList;
		}
	}

	public String getAnalyzerType() {
		String type = "";
		if (imageList != null) {
			type += "+"+ImageAnalyzerX.class.getSimpleName();
		}
		if (pathList != null) {
			type += "+"+PathAnalyzerX.class.getSimpleName();
		}
		if (textList != null) {
			type += "+"+TextAnalyzerX.class.getSimpleName();
		}
		return type;
	}
	
	private SVGG createSVGAndOutput(int humanPageNumber, int counter, SVGG gOrig,
			AbstractAnalyzer analyzerX,  String suffix, MixedAnalyzer mixedAnalyzer) {
		ChunkId chunkId;
//		if (mixedAnalyzer.removeFrameBoxFromPathList()) {
//			gOrig = mixedAnalyzer.getSVGG();
//			SVG2XMLUtil.writeToSVGFile(new File("target"), "mixed."+humanPageNumber+"."+(counter)+"D.svg", gOrig);
//			mixedAnalyzer.normalizePathAnalyzers();
//			LOG.trace("New Mixed AnalyzerType: "+mixedAnalyzer.getAnalyzerType());
//		}
//		DivContainer divContainer = DivContainer.createDivContainer(this, mixedAnalyzer);
//		containerList.add(divContainer);
//		chunkId = new ChunkId(humanPageNumber, counter);
//		SVGG gOut = annotateChunkAndAddIdAndAttributes(gOrig, chunkId, analyzerX);
//		SVG2XMLUtil.writeToSVGFile(new File("target"), "chunk"+humanPageNumber+"."+(counter)+suffix, gOut);
//		return gOut;
		return null;
	}

}
