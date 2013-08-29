package org.xmlcml.svg2xml.analyzer;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.HiddenGraphics;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.figure.Figure;
import org.xmlcml.svg2xml.figure.FigurePanel;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.tools.Caption;
import org.xmlcml.svg2xml.tools.Chunk;

/**
 * @author pm286
 *
 */
public class FigureAnalyzerX extends AbstractAnalyzer {

	static final Logger LOG = Logger.getLogger(FigureAnalyzerX.class);

	private static final String CHUNK_STYLE = "chunkStyle";
	public static final String FRAGMENT = "FRAGMENT";
	public static final String OUTLINE_BOX = "outlineBox";
	private static final Object USE_CAPTIONS = "useCaptions";

	public static final Pattern PATTERN = Pattern.compile("[Ff][Ii][Gg][Uu]?[Rr]?[Ee]?\\s*\\.?\\s*(\\d*).*", Pattern.DOTALL);
	public static final String TITLE = "FIGURE";

	private static final String ABOVE = "above";
	private static final String BELOW = "below";

	private static final Double YEPS = 2.0;
	
	private List<Figure> figureList;
	private List<FigurePanel> panelList;
	private String locationStrategy;

	private String[] clusterColours = new String[]{"red", "blue", "green", "cyan", "yellow", "magenta", "grey"};
	private Real2 clusterWhitespaceBoxMargins = new Real2(5.0, 5.0);
	private Double panelSeparation = 3.0;

	private TextAnalyzerX textAnalyzer;
	private PathAnalyzerX pathAnalyzer;
	private ImageAnalyzerX imageAnalyzer;

	private SVGSVG top;
	private SVGSVG above;

	public FigureAnalyzerX() {
		super();
	}
	
	public void analyze() {
		LOG.error("Figure NYI");
	}

	public FigureAnalyzerX(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	public FigureAnalyzerX(TextAnalyzerX textAnalyzer,
			PathAnalyzerX pathAnalyzer, ImageAnalyzerX imageAnalyzer, SVGElement svgElement) {
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
	
	@Override
	public SVGG oldAnnotateChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
	public Integer indexAndLabelChunk(String content, ChunkId id, Set<ChunkId> usedChunkSet) {
		Integer serial = super.indexAndLabelChunk(content, id);
		// index...
		return serial;
	}
	
	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

	public HtmlDiv createFigure() {
		String id = svgElement.getId();
		List<ScriptLine> scriptLineList = textAnalyzer.getTextStructurer().getScriptedLineList(); 
		LOG.debug("BB: "+textAnalyzer.getTextStructurer().getBoundingBox());
		Double ySplit = null;
		for (ScriptLine scriptLine : scriptLineList) {	
			String s = scriptLine.getTextContentWithSpaces();
			LOG.trace("Y "+scriptLine.getBoundingBox()+" "+s);
			if (PATTERN.matcher(s).matches()) {
//				if (ySplit != null) {
//					LOG.debug("Possible Duplicate Figure Caption: "+scriptLine.getTextContentWithSpaces());
//				}
				ySplit = scriptLine.getBoundingBox().getYRange().getMin();
				LOG.debug("Figure Caption: "+scriptLine.getTextContentWithSpaces());
				break;
			}
		}
		if (ySplit != null) {
			top = new SVGSVG();
			addElements(top, ABOVE, ySplit - YEPS);
			above = new SVGSVG();
			addElements(above, BELOW, ySplit + YEPS);
			try {
				CMLUtil.debug(top, new FileOutputStream("caption"+id+".svg"), 1);
				CMLUtil.debug(above, new FileOutputStream("above"+id+".svg"), 1);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		HtmlDiv div = new HtmlDiv();
		div.appendChild(top);
		HiddenGraphics hg = new HiddenGraphics();
		hg.setDimension(new Dimension(600, 800));
		hg.createImage(top);
		try {
			hg.write(new File("target/figureCaption"+id+".png"));
		} catch (IOException e) {
			throw new RuntimeException("Cannot write image", e);
		}

		div.appendChild(new HtmlP("===================="));
		div.appendChild(above);
		Real2Range bb = above.getBoundingBox();
		Real2 translateToOrigin = new Real2(-bb.getXRange().getMin() + 10, -bb.getYRange().getMin() + 10);
		above.setTransform(new Transform2(new Vector2(translateToOrigin)));
		hg = new HiddenGraphics();
		hg.setDimension(new Dimension(600, 800));
		hg.createImage(above);
		try {
			hg.write(new File("target/figureAbove"+id+".png"));
		} catch (IOException e) {
			throw new RuntimeException("Cannot write image", e);
		}
		return div;
	}

	private void addElements(SVGElement svgg, String where, Double ySplit) {
		List<SVGElement> elementList = new ArrayList<SVGElement>();
		if (textAnalyzer != null) {
			for (SVGElement element : textAnalyzer.getTextCharacters()) {
				if (isLocated(element, where, ySplit)) {
					elementList.add(element);
				}
			}
		}
		if (pathAnalyzer != null) {
			for (SVGElement element : pathAnalyzer.getPathList()) {
				if (isLocated(element, where, ySplit)) {
					elementList.add(element);
				}
			}
		}
		if (imageAnalyzer != null) {
			for (SVGElement element : imageAnalyzer.getImageList()) {
				if (isLocated(element, where, ySplit)) {
					elementList.add(element);
				}
			}
		}
		for (SVGElement element : elementList) {
			svgg.appendChild(element.copy());
		}
	}
	
	private boolean isLocated(SVGElement element, String where, Double ySplit) {
		RealRange yRange = element.getBoundingBox().getYRange();
		return (where.equals(ABOVE) && yRange.getMin() > ySplit ||
			where.equals(BELOW) && yRange.getMax() < ySplit);
	}

}
