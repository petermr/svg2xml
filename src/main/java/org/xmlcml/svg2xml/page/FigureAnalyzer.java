package org.xmlcml.svg2xml.page;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.svg2xml.figure.FigureCaption;
import org.xmlcml.svg2xml.figure.FigureComponent;
import org.xmlcml.svg2xml.figure.FigureGraphic;
import org.xmlcml.svg2xml.text.ScriptLine;

/**
 * A figure is a complex and variable mixture of CaptionComponent and GraphicComponent.
 * <p>
 * Depending on the publication the caption may be below or above the graphics. The
 * graphics can consist of Images, Text and Paths in any combination. 
 * There is often very little whitespace between caption and graphics.
 * </p>
 * <p>
 * Sometimes the original packaging couples the caption and graphics (e.g. in a box, 
 * sometimes they are simply juxtaposed chunks.
 * </p>
 * <p>
 * FigureAnalyzer serves both to create the figures and to analyze the graphics. 
 * These functionalities should be split.
 * </p>
 * @author pm286
 *
 */
public class FigureAnalyzer extends ChunkAnalyzer {

	static final Logger LOG = Logger.getLogger(FigureAnalyzer.class);

	public static final String FRAGMENT = "FRAGMENT";
	public static final String OUTLINE_BOX = "outlineBox";
	public static final Pattern CAPTION_PATTERN = Pattern.compile("[Ff][Ii][Gg][Uu]?[Rr]?[Ee]?\\s*\\.?\\s*(\\d*).*", Pattern.DOTALL);

	private static final Double YEPS = 2.0;
	
	private TextAnalyzer textAnalyzer;
	private ShapeAnalyzer shapeAnalyzer;
	private ImageAnalyzer imageAnalyzer;

	private FigureCaption figureCaption;
	private FigureGraphic figureGraphic;


	public FigureAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public FigureAnalyzer(TextAnalyzer textAnalyzer,
			ShapeAnalyzer shapeAnalyzer, ImageAnalyzer imageAnalyzer, SVGElement svgElement) {
		super(textAnalyzer.getPageAnalyzer());
		this.textAnalyzer = textAnalyzer;
		this.shapeAnalyzer = shapeAnalyzer;
		this.imageAnalyzer = imageAnalyzer;
		this.setSVGChunk(svgElement);
	}

	public HtmlDiv createHtmlFigure() {
		String id = String.valueOf(getChunkId());
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
			int nfig = figureGraphic.getImageList().size();
			if (nfig > 0) {
				LOG.trace("Skipped svg with "+nfig+" PNGs");
			} else {
				LOG.trace("writing SVG "+svgName);
				figureGraphic.createAndWriteImageAndSVG(imageName, div, svgName);
			}
		}
		

		return div;
	}

	private Double iterateThroughLinesToFindCaption(List<ScriptLine> scriptLineList) {
		Double ySplit = null;
		for (ScriptLine scriptLine : scriptLineList) {	
			String s = scriptLine.getTextContentWithSpaces();
			LOG.trace("Y "+scriptLine.getBoundingBox()+" "+s);
			if (CAPTION_PATTERN.matcher(s).matches()) {
				ySplit = scriptLine.getBoundingBox().getYMin();
				LOG.trace("Figure Caption: "+scriptLine.getTextContentWithSpaces());
				break;
			}
		}
		return ySplit;
	}

	private void createCaptionAndGraphic(String id, Double ySplit) {
		figureCaption = new FigureCaption(this);
		figureCaption.addElements(FigureComponent.ABOVE, ySplit - YEPS);
		LOG.trace("Caption "+figureCaption.getSvgContainer().getChildCount());
		figureGraphic = new FigureGraphic(this);
		figureGraphic.addElements(FigureComponent.BELOW, ySplit + YEPS);
		LOG.trace("Graphic "+figureGraphic.getSvgContainer().getChildCount());
		try {
			File file = new File("target/");
			file.mkdirs();
			SVGUtil.debug(figureCaption.getSvgContainer(), new FileOutputStream("target/caption"+id+".svg"), 1);
			SVGUtil.debug(figureGraphic.getSvgContainer(), new FileOutputStream("target/graphic"+id+".svg"), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public TextAnalyzer getTextAnalyzer() {
		return textAnalyzer;
	}

	public ShapeAnalyzer getShapeAnalyzer() {
		return shapeAnalyzer;
	}

	public ImageAnalyzer getImageAnalyzer() {
		return imageAnalyzer;
	}


}
