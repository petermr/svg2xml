package org.xmlcml.svg2xml.figure;

    import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.page.ChunkAnalyzer;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.page.ImageAnalyzer;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.PathAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;

public abstract class FigureComponent {


	public enum FigureType {
		IMAGE,
		IMAGES,
		MIXED,
		PATHS,
		PATHS_TEXT,
		TEXT,
		UNKNOWN
	}
	
	private final static Logger LOG = Logger.getLogger(FigureComponent.class);

	public static final String ABOVE = "above";
	public static final String BELOW = "below";

	protected SVGG svgContainer;
	protected TextAnalyzer textAnalyzer;
	protected PathAnalyzer pathAnalyzer;
	protected ImageAnalyzer imageAnalyzer;
	protected Real2Range boundingBox;

	private List<SVGText> textList;
	private List<SVGPath> pathList;
	private List<SVGImage> imageList;
	private List<SVGText> filteredTextList;
	private List<SVGPath> filteredPathList;
	private List<SVGImage> filteredImageList;

	private ChunkAnalyzer figureAnalyzer;
	protected PageAnalyzer pageAnalyzer;

	protected FigureComponent(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
	}

	protected FigureComponent(FigureAnalyzer figureAnalyzer) {
		this(figureAnalyzer.getTextAnalyzer(), figureAnalyzer.getPathAnalyzer(), figureAnalyzer.getImageAnalyzer());
		this.figureAnalyzer = figureAnalyzer;
		this.pageAnalyzer = figureAnalyzer.getPageAnalyzer();
	}

	protected FigureComponent(TextAnalyzer textAnalyzer, PathAnalyzer pathAnalyzer, ImageAnalyzer imageAnalyzer)  {
		this.textAnalyzer = textAnalyzer;
		this.pathAnalyzer = pathAnalyzer;
		this.imageAnalyzer = imageAnalyzer;
		this.textList = new ArrayList<SVGText>();
		this.pathList = new ArrayList<SVGPath>();
		this.imageList = new ArrayList<SVGImage>();
	}

	public void addElements(String where, Double ySplit) {
		ensureSVGContainer();
		List<SVGElement> allElementList = new ArrayList<SVGElement>();
		int count = 0;
		if (textAnalyzer != null) {
			textList = textAnalyzer.getTextCharacters();
			filteredTextList = SVGText.extractTexts(createElementList("TEXT", where, ySplit, textList));
			allElementList.addAll(filteredTextList);
		}
		if (pathAnalyzer != null) {
			pathList = pathAnalyzer.getPathList();
			filteredPathList = SVGPath.extractPaths(createElementList("PATH", where, ySplit, pathList));
			allElementList.addAll(filteredPathList);
		}
		if (imageAnalyzer != null) {
			imageList = imageAnalyzer.getImageList();
			filteredImageList = SVGImage.extractImages(createElementList("IMAGE", where, ySplit, imageList));
			allElementList.addAll(filteredImageList);
		}
		for (SVGElement element : allElementList) {
			svgContainer.appendChild(element.copy());
		}
		LOG.trace("Container "+svgContainer.getChildCount());
		boundingBox = svgContainer.getBoundingBox();
		getFigureType();
	}

	private List<SVGElement> createElementList(String title, String where, Double ySplit,
			List<? extends SVGElement> rawList) {
		int count = 0;
		List<SVGElement> resultList = new ArrayList<SVGElement>();
		for (SVGElement element : rawList) {
			if (isLocated(element, where, ySplit)) {
				resultList.add((SVGElement) element);
			} else {
				count++;
			}
		}
		LOG.trace(title+": "+count);
		return resultList;
	}

	private void ensureSVGContainer() {
		if (svgContainer == null) {
			svgContainer = new SVGG();
		}
	}
	
	public SVGG getSvgContainer() {
		ensureSVGContainer();
		return svgContainer;
	}

	private boolean isLocated(SVGElement element, String where, Double ySplit) {
		RealRange yRange = element.getBoundingBox().getYRange();
		boolean above = where.equals(ABOVE) && yRange.getMin() > ySplit;
		boolean below =	where.equals(BELOW) && yRange.getMax() < ySplit;
		boolean isLocated = above || below;
		return isLocated;
	}
	
	public FigureType getFigureType() {
		FigureType figureType = FigureType.UNKNOWN;
		if (filteredImageList.size() > 0 && filteredPathList.size() == 0 && filteredTextList.size() == 0 ) {
			if (filteredImageList.size() > 1) {
				figureType = FigureType.IMAGES;
			} else {
				figureType = FigureType.IMAGE;
			}
//			for (SVGImage image : filteredImageList) {
//				PageAnalyzer pdfAnalyzer = figureAnalyzer.getPageAnalyzer();
//				String id = figureAnalyzer.getChunkId().toString();
//				LOG.debug("IMAGE "+image.getWidth()+" "+image.getHeight());
//				File file = new File("target/image"+id+".png");
//				try {
//					image.writeImage(file.toString(), SVGImage.IMAGE_PNG);
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			}
		} else if (filteredImageList.size() == 0 && filteredPathList.size() > 0 && filteredTextList.size() == 0 ) {
			figureType = FigureType.PATHS;
		} else if (filteredImageList.size() == 0 && filteredPathList.size() == 0 && filteredTextList.size() > 0 ) {
			figureType = FigureType.TEXT;
		} else if (filteredImageList.size() == 0 && filteredPathList.size() > 0 && filteredTextList.size() > 0 ) {
			figureType = FigureType.PATHS_TEXT;
		} else if (filteredImageList.size() > 0 && filteredPathList.size() > 0 && filteredTextList.size() > 0 ) {
			figureType = FigureType.MIXED;
		} else {
			figureType = FigureType.UNKNOWN;
		}
		LOG.trace("FIGURE"+this.getClass().getName()+"************************************************** "+figureType+ 
				" "+filteredTextList.size()+" "+filteredPathList.size()+" "+filteredImageList.size());
		return figureType;
	}

	public void setSVGContainer(SVGG svgContainer) {
		this.svgContainer = svgContainer;
		boundingBox = svgContainer == null ? null : svgContainer.getBoundingBox();
	}


}
