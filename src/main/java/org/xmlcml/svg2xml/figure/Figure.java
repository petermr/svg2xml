package org.xmlcml.svg2xml.figure;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.page.BoundingBoxManager.BoxEdge;
import org.xmlcml.svg2xml.paths.Chunk;
import org.xmlcml.svg2xml.util.SVG2XMLConstantsX;

/** a figure consists of (possibly optional) Caption and FigureBody
 * these are directly accessible but are also child elements of Figure
 * @author pm286
 *
 */
public class Figure extends Chunk {
	private final static Logger LOG = Logger.getLogger(Figure.class);

	private static final String FIGURE_TAG = "figure";
	private static final String CAPTION_TAG = "caption";
	public final static String FIGURE = "FIGURE";
	
	private Chunk caption;
	private FigureBody figureBody;
	private FigureAnalyzer figureAnalyzerX;
	private List<FigurePanel> panelList;
	private List<FigureFragment> fragmentList;
	private Element figureAnalysis;
	private List<Figure> figureList;
	private Double panelSeparation = 3.0;

	private String[] clusterColours = new String[]{"red", "blue", "green", "cyan", "yellow", "magenta", "grey"};

	private Real2 clusterWhitespaceBoxMargins = new Real2(5.0, 5.0);
	
	protected String getChunkStyleName() {
		return FIGURE;
	}

	public Figure() {
		super();
	}

	public Figure(FigureAnalyzer figureAnalyzerX) {
		this();
		this.figureAnalyzerX = figureAnalyzerX;
	}

	public void addCaption(Chunk caption) {
		this.caption = caption;
		caption.detach();
		this.appendChild(caption);
	}

	public Chunk getCaption() {
		return this.caption;
	}

	public FigureBody getFigureBody() {
		return figureBody;
	}

	private FigureBody createFigureBody() {
		// assume caption is separated from rest by horizontal line. May need to check more later
		ensureFigureBody();
//		List<SVGElement> oldGList = SVGUtil.getQuerySVGElements(this, "./svg:g[not(@chunkStyle='"+Caption.CAPTION+"')]");
		List<SVGElement> oldGList = SVGUtil.getQuerySVGElements(this, "./svg:g[not(@chunkStyle='"+"CAPTION"+"')]");
		figureBody.transferGChildren(oldGList);
		// clean up old Gs
		this.removeEmptySVGG();
		return figureBody;
	}
	
	private List<FigurePanel> createPanelList() {
		// chunk the figureBody by horizontal whitespace and add to new container
		// (may have to do vertical space later)
		List<Chunk> panelChunkList = figureBody.splitIntoChunks(this.getPanelSeparation(), BoxEdge.XMIN);
		String id = this.getId();
		int i = 0;
		ensurePanelList();
		for (Chunk panel : panelChunkList) {
			FigurePanel figurePanel = new FigurePanel(panel);
			figurePanel.setId(id+"."+(i++));
			this.addFigurePanel(figurePanel);
			SVGElement.drawBox(figurePanel.getBoundingBox(), this, "blue", "cyan", 1.0, 0.3);
			panelList.add(figurePanel);
		}
		LOG.trace("Split figureBody into figurePanels: "+panelList.size());
		return panelList;
	}
	
	private void ensurePanelList() {
		if (panelList == null) {
			panelList = new ArrayList<FigurePanel>();
		}
	}

	public List<FigureFragment> createFragmentsInsidePanels() {
		fragmentList = new ArrayList<FigureFragment>();
		if (figureBody != null) {
			fragmentList = figureBody.createFragmentsInsidePanels();
		}
		return fragmentList;
	}

	public List<FigurePanel> splitByHorizontalWhitespace() {
		createFigureBody();
		createPanelList();
		this.annotateWithBoxes();
		return panelList;
	}

	void annotateWithBoxes() {
		Chunk caption = getCaption();
		caption.setBoundingBoxCached(false);
		SVGElement.drawBox(caption.getBoundingBox(), caption, "magenta", "#ffffaa", 3.0, 0.3);
		createElementListAndCalculateBoundingBoxes();
		SVGElement.drawBox(getBoundingBox(), this, "green", "#ffffaa", 3.0, 0.3);
	}

	private void ensureFigureBody() {
		if (figureBody == null) {
			figureBody = new FigureBody(this);
			this.addFigureBody(figureBody);
		}
	}

	private void addFigureBody(FigureBody figureBody) {
		this.figureBody = figureBody;
		figureBody.detach();
		this.appendChild(figureBody);
	}

	void addFigurePanel(FigurePanel figurePanel) {
		ensureFigureBody();
		figureBody.addFigurePanel(figurePanel);
	}

	void removeFigurePanel(FigurePanel figurePanel) {
		figureBody.remove(figurePanel);
	}

	public List<FigurePanel> getFigurePanelList() {
		return figureBody == null ? null : figureBody.getFigurePanelList();
	}

	public FigureAnalyzer getFigureAnalyzer() {
		return figureAnalyzerX;
	}

	public Element getFigureAnalysis() {
		figureAnalysis = new Element(FIGURE_TAG);
		String id = this.getId();
		if (id != null) {
			figureAnalysis.addAttribute(new Attribute(SVG2XMLConstantsX.ID, id));
		}
		if (figureBody.getBodyAnalysis() != null) {
			figureAnalysis.appendChild(figureBody.getBodyAnalysis().copy());
		}
		
		if (caption != null) {
			Element captionTitle = new Element(CAPTION_TAG);
			captionTitle.appendChild(caption.query("./*/*/@title").get(0).getValue());
			figureAnalysis.appendChild(captionTitle);
		}
		return figureAnalysis;
	}

	public void createFragmentsInsidePanelsForAllFigures() {
		for (Figure figure : figureList) {
			figure.createFragmentsInsidePanels();
		}
	}

	public List<FigurePanel> createPanelsUsingWhitespace() {
		if (panelList == null) {
			panelList = new ArrayList<FigurePanel>();
			for (Figure figure : figureList) {
				List<FigurePanel> pList = figure.splitByHorizontalWhitespace();
				if (pList == null) {
					throw new RuntimeException("Null panelList");
				}
				panelList.addAll(pList);
			}
		}
		return panelList;
	}

	public Double getPanelSeparation() {
		return panelSeparation;
	}

	private void setPanelSeparation(Double panelSeparation) {
		this.panelSeparation = panelSeparation;
	}

	private void setPanelSeparation(String s) {
		try {
			Double d = new Double(s);
			this.panelSeparation = d;
		} catch (Exception e) {
			throw new RuntimeException("Bad panel separation: "+s);
		}
	}

	Real2 getClusterWhitespaceBoxMargins() {
		return clusterWhitespaceBoxMargins;
	}

	public String[] getColourClusters() {
		return clusterColours;
	}

	private void setClusterMargins(Real2 clusterWhitespaceBoxMargins) {
		this.clusterWhitespaceBoxMargins = clusterWhitespaceBoxMargins;
	}

	//	public List<Figure> createFigures() {
	//		if (locationStrategy == null) {
	//			throw new RuntimeException("Must give strategy");
	//		}
	//		if (OUTLINE_BOX.equals(locationStrategy)) {
	//			createFiguresFromBoxes();
	//		} else if (USE_CAPTIONS.equals(locationStrategy)) {
	//			getListOfFiguresAndAssembleFromCaptions();
	//		} else {
	//			throw new RuntimeException("Unknown or missing locationStrategy: "+locationStrategy);
	//		}
	//		return figureList;
	//	}
	
	//	public void setLocationStrategy(String strategy) {
	//		this.locationStrategy = strategy;
	//	}
	
	//	private List<Figure> createFiguresFromBoxes() {
	//		figureList = new ArrayList<Figure>();
	//		List<SVGElement> boxChunks = null;
	////				List<SVGElement> boxChunks = SVGUtil.getQuerySVGElements(
	////				svgPage, ".//svg:g[@"+CHUNK_STYLE+"='"+ChunkStyle.OUTLINED_BOX+"']");
	//		// we have already chunked the text, so look for captions
	//		for (SVGElement boxChunk : boxChunks) {
	//			/**
	//			 *  of form 
	//			 *  <g xmlns="http://www.w3.org/2000/svg" clipPath="clipPath8" chunkStyle="CAPTION">
	//			 *    <text style=" stroke  ... 
	//			 *    <g name="para">
	//                    <text style=" stroke : none;" x="62.929" y="473.365" font-size="8.0">Figure 1 Multi-gene phylogenetic ...
	//                  </g>
	//                </g>
	//			 */
	//			// A figure must have a caption (to avoid mention of Figure 1 in main text)
	//			Chunk caption = getCaptionFromBox(boxChunk);
	//			if (caption != null) {
	//				Figure figure = createFigure(boxChunk);
	//				boxChunk.getParent().replaceChild(boxChunk, figure);
	//				figure.addCaption(caption);
	//				figureList.add(figure);
	//			}
	//		}
	//		return figureList;
	//	}
	
	//	/**
	//	 * this is where figures have no box and we have to assemble from chunks
	//	 */
	//	private void getListOfFiguresAndAssembleFromCaptions() {
	//		// find captions
	//		List<SVGElement> boxChunks = null;
	////		List<SVGElement> boxChunks = SVGUtil.getQuerySVGElements(
	////				svgPage, ".//svg:g[@"+CHUNK_STYLE+"='"+ChunkStyle.OUTLINED_BOX+"']");
	//		for (SVGElement boxChunk : boxChunks) {
	//			Chunk caption = getCaptionFromBox(boxChunk);
	//			if (caption != null) {
	//				throw new RuntimeException("NYI");
	//			}
	//		}
	//	}
	
	//	private Figure createFigure(SVGElement chunk) {
	//		Figure figure = new Figure(this/*, figureStyle*/);
	//		figure.copyAttributesAndChildrenFromSVGElement(chunk);
	//		figure.createElementListAndCalculateBoundingBoxes(/*chunk*/);
	//		return figure;
	//	}
	
	//	/** caption is identified by "Fig. " or "Figure " as dictated by stylesheet
	//	 * 
	//	 * @param boxChunk
	//	 * @return
	//	 */
	//	private Chunk getCaptionFromBox(SVGElement boxChunk) {
	//		Chunk caption = null;
	//		String startsWith = null;
	//		List<SVGElement> captionElements = SVGUtil.getQuerySVGElements(
	//				boxChunk, "./svg:g[svg:g[@name='para']/svg:text[starts-with(., '"+startsWith+"')]]");
	//		if (captionElements.size() == 1) {
	//			caption = createCaptionAndReplace((Chunk)captionElements.get(0));
	//		}
	//		return caption;
	//	}
	
	//	private Chunk createCaptionAndReplace(Chunk chunk) {
	//		Chunk caption = new Caption(chunk);
	//		caption.copyAttributesAndChildrenFromSVGElement(chunk);
	//		chunk.detach();
	//		caption.createElementListAndCalculateBoundingBoxes();
	//		return caption;
	//	}
	
		public void setColourClusters(String col) {
			if (col == null || col.trim().length() == 0) {
				clusterColours = null;
			} else {
				clusterColours = col.split(CMLConstants.S_WHITEREGEX);
			}
		}

}
