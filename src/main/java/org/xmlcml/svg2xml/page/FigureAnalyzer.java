package org.xmlcml.svg2xml.page;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlA;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlImg;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.figure.Caption;
import org.xmlcml.svg2xml.figure.Figure;
import org.xmlcml.svg2xml.figure.FigureCaption;
import org.xmlcml.svg2xml.figure.FigureComponent;
import org.xmlcml.svg2xml.figure.FigureGraphic;
import org.xmlcml.svg2xml.figure.FigurePanel;
import org.xmlcml.svg2xml.paths.Chunk;
import org.xmlcml.svg2xml.text.ScriptLine;

/**
 * A figure is a complex and variable mixture of CaptionComponent and GraphicComponent.
 * 
 * Depending on the publication the caption may be below or above the graphics. The
 * graphics can consist of Images, Text and Paths in any combination. 
 * There is often very little whitspace between caption and graphics
 * 
 * Sometimes the original pakaging couples the caption and graphics (e.g. in a box, 
 * sometimes they are simply juxtaposed chunks.
 * 
 * @author pm286
 *
 */
public class FigureAnalyzer extends PageChunkAnalyzer {

	static final Logger LOG = Logger.getLogger(FigureAnalyzer.class);

	private static final String CHUNK_STYLE = "chunkStyle";
	public static final String FRAGMENT = "FRAGMENT";
	public static final String OUTLINE_BOX = "outlineBox";
	private static final Object USE_CAPTIONS = "useCaptions";

	public static final Pattern CAPTION_PATTERN = Pattern.compile("[Ff][Ii][Gg][Uu]?[Rr]?[Ee]?\\s*\\.?\\s*(\\d*).*", Pattern.DOTALL);

	private static final Double YEPS = 2.0;
	
	private List<Figure> figureList;
	private List<FigurePanel> panelList;
	private String locationStrategy;

	private String[] clusterColours = new String[]{"red", "blue", "green", "cyan", "yellow", "magenta", "grey"};
	private Real2 clusterWhitespaceBoxMargins = new Real2(5.0, 5.0);
	private Double panelSeparation = 3.0;

	private TextAnalyzer textAnalyzer;
	private PathAnalyzer pathAnalyzer;
	private ImageAnalyzer imageAnalyzer;

	private FigureCaption figureCaption;
	private FigureGraphic figureGraphic;


	public FigureAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public void analyze() {
		LOG.error("Figure NYI");
	}

//	public FigureAnalyzer(PDFIndex pdfIndex) {
//		super(pdfIndex);
//	}
	
	public FigureAnalyzer(TextAnalyzer textAnalyzer,
			PathAnalyzer pathAnalyzer, ImageAnalyzer imageAnalyzer, SVGElement svgElement) {
		super(textAnalyzer.getPageAnalyzer());
		this.textAnalyzer = textAnalyzer;
		this.pathAnalyzer = pathAnalyzer;
		this.imageAnalyzer = imageAnalyzer;
		this.setSVGElement(svgElement);
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

	public void createFragmentsInsidePanelsForAllFigures() {
		for (Figure figure : figureList) {
			figure.createFragmentsInsidePanels();
		}
	}

	public List<Figure> createFigures() {
		if (locationStrategy == null) {
			throw new RuntimeException("Must give strategy");
		}
		if (OUTLINE_BOX.equals(locationStrategy)) {
			createFiguresFromBoxes();
		} else if (USE_CAPTIONS.equals(locationStrategy)) {
			getListOfFiguresAndAssembleFromCaptions();
		} else {
			throw new RuntimeException("Unknown or missing locationStrategy: "+locationStrategy);
		}
		return figureList;
	}

	public void setLocationStrategy(String strategy) {
		this.locationStrategy = strategy;
	}

	private List<Figure> createFiguresFromBoxes() {
		figureList = new ArrayList<Figure>();
		List<SVGElement> boxChunks = null;
//				List<SVGElement> boxChunks = SVGUtil.getQuerySVGElements(
//				svgPage, ".//svg:g[@"+CHUNK_STYLE+"='"+ChunkStyle.OUTLINED_BOX+"']");
		// we have already chunked the text, so look for captions
		for (SVGElement boxChunk : boxChunks) {
			/**
			 *  of form 
			 *  <g xmlns="http://www.w3.org/2000/svg" clipPath="clipPath8" chunkStyle="CAPTION">
			 *    <text style=" stroke  ... 
			 *    <g name="para">
                    <text style=" stroke : none;" x="62.929" y="473.365" font-size="8.0">Figure 1 Multi-gene phylogenetic ...
                  </g>
                </g>
			 */
			// A figure must have a caption (to avoid mention of Figure 1 in main text)
			Chunk caption = getCaptionFromBox(boxChunk);
			if (caption != null) {
				Figure figure = createFigure(boxChunk);
				boxChunk.getParent().replaceChild(boxChunk, figure);
				figure.addCaption(caption);
				figureList.add(figure);
			}
		}
		return figureList;
	}

	/**
	 * this is where figures have no box and we have to assemble from chunks
	 */
	private void getListOfFiguresAndAssembleFromCaptions() {
		// find captions
		List<SVGElement> boxChunks = null;
//		List<SVGElement> boxChunks = SVGUtil.getQuerySVGElements(
//				svgPage, ".//svg:g[@"+CHUNK_STYLE+"='"+ChunkStyle.OUTLINED_BOX+"']");
		for (SVGElement boxChunk : boxChunks) {
			Chunk caption = getCaptionFromBox(boxChunk);
			if (caption != null) {
				throw new RuntimeException("NYI");
			}
		}
	}

	private Figure createFigure(SVGElement chunk) {
		Figure figure = new Figure(this/*, figureStyle*/);
		figure.copyAttributesAndChildrenFromSVGElement(chunk);
		figure.createElementListAndCalculateBoundingBoxes(/*chunk*/);
		return figure;
	}

	/** caption is identified by "Fig. " or "Figure " as dictated by stylesheet
	 * 
	 * @param boxChunk
	 * @return
	 */
	private Chunk getCaptionFromBox(SVGElement boxChunk) {
		Chunk caption = null;
		String startsWith = null;
		List<SVGElement> captionElements = SVGUtil.getQuerySVGElements(
				boxChunk, "./svg:g[svg:g[@name='para']/svg:text[starts-with(., '"+startsWith+"')]]");
		if (captionElements.size() == 1) {
			caption = createCaptionAndReplace((Chunk)captionElements.get(0));
		}
		return caption;
	}

	private Chunk createCaptionAndReplace(Chunk chunk) {
		Chunk caption = new Caption(chunk);
		caption.copyAttributesAndChildrenFromSVGElement(chunk);
		chunk.detach();
		caption.createElementListAndCalculateBoundingBoxes();
		return caption;
	}

	public void setColourClusters(String col) {
		if (col == null || col.trim().length() == 0) {
			clusterColours = null;
		} else {
			clusterColours = col.split(CMLConstants.S_WHITEREGEX);
		}
	}

	public String[] getColourClusters() {
		return clusterColours;
	}
	
	public Real2 getClusterWhitespaceBoxMargins() {
		return clusterWhitespaceBoxMargins;
	}

	public void setClusterMargins(String mm) {
		this.clusterWhitespaceBoxMargins = new Real2(mm);
	}
	
	public void setClusterMargins(Real2 clusterWhitespaceBoxMargins) {
		this.clusterWhitespaceBoxMargins = clusterWhitespaceBoxMargins;
	}
	
	public Double getPanelSeparation() {
		return panelSeparation;
	}

	public void setPanelSeparation(String s) {
		try {
			Double d = new Double(s);
			this.panelSeparation = d;
		} catch (Exception e) {
			throw new RuntimeException("Bad panel separation: "+s);
		}
	}
	
	public void setPanelSeparation(Double panelSeparation) {
		this.panelSeparation = panelSeparation;
	}
		
	public HtmlDiv createFigure() {
		String id = getIdFromSvgElement();
		List<ScriptLine> scriptLineList = textAnalyzer.getTextStructurer().getScriptedLineList(); 
		Double yCoordinateOfCaption = iterateThroughLinesToFindCaption(scriptLineList);
		if (yCoordinateOfCaption != null) {
			createCaptionAndGraphic(id, yCoordinateOfCaption);
		}
		String imageName = getPageIO().createImageFilename(id);
		HtmlDiv div = FigureGraphic.createHtmlImgDivElement(imageName, "50%");
		if (figureCaption != null) {
			figureCaption.processCaptionText(div);
		}
		if (figureGraphic != null) {
			String svgName = getPageIO().createSvgFilename(id);
			figureGraphic.createAndWriteImageAndSVG(imageName, div, svgName);
		}
		

		return div;
	}

	private PageIO getPageIO() {
		PageAnalyzer pageAnalyzer = getPageAnalyzer();
		return pageAnalyzer == null ? null : pageAnalyzer.getPageIO();
	}

	public String getIdFromSvgElement() {
		String id = svgElement == null ? null : svgElement.getId();
		return id;
	}

	private Double iterateThroughLinesToFindCaption(List<ScriptLine> scriptLineList) {
		Double ySplit = null;
		for (ScriptLine scriptLine : scriptLineList) {	
			String s = scriptLine.getTextContentWithSpaces();
			LOG.trace("Y "+scriptLine.getBoundingBox()+" "+s);
			if (CAPTION_PATTERN.matcher(s).matches()) {
				ySplit = scriptLine.getBoundingBox().getYMin();
				LOG.debug("Figure Caption: "+scriptLine.getTextContentWithSpaces());
				break;
			}
		}
		return ySplit;
	}

	private void createCaptionAndGraphic(String id, Double ySplit) {
		figureCaption = new FigureCaption(this);
		figureCaption.addElements(FigureComponent.ABOVE, ySplit - YEPS);
		LOG.debug("Caption "+figureCaption.getSvgContainer().getChildCount());
		figureGraphic = new FigureGraphic(this);
		figureGraphic.addElements(FigureComponent.BELOW, ySplit + YEPS);
		LOG.debug("Graphic "+figureGraphic.getSvgContainer().getChildCount());
		try {
			CMLUtil.debug(figureCaption.getSvgContainer(), new FileOutputStream("target/caption"+id+".svg"), 1);
			CMLUtil.debug(figureGraphic.getSvgContainer(), new FileOutputStream("target/graphic"+id+".svg"), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public TextAnalyzer getTextAnalyzer() {
		return textAnalyzer;
	}

	public PathAnalyzer getPathAnalyzer() {
		return pathAnalyzer;
	}

	public ImageAnalyzer getImageAnalyzer() {
		return imageAnalyzer;
	}


}
