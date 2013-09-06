package org.xmlcml.svg2xml.page;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlB;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlI;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.dead.PageEditorDead;
import org.xmlcml.svg2xml.pdf.ChunkId;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;
import org.xmlcml.svg2xml.pdf.PDFIndex;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

/** superclass of raw components of PDFPage SVG.
 * 
 * Components are:
 *  FigureAnalyzer, ImageAnalyzer, PathAnalyzer,TableAnalyzer, TextAnalyzer
 *  
 *  Each component can access the PageAnalyzer , and through that the PDFAnalyzer
 *  for the document. Most analyzers have an AbstractContainer which processes the raw
 *  SVG.
 * @author pm286
 *
 */
public abstract class PageChunkAnalyzer {
	
	private static final PrintStream SYSOUT = System.out;

	private final static Logger LOG = Logger.getLogger(PageChunkAnalyzer.class);

	protected SVGG svgg; // current svg:gelement
	protected Real2Range bbox;
	protected SVGElement parentElement;
	protected ChunkId chunkId;
	protected SVGElement svgElement;
	protected PageAnalyzer pageAnalyzer;

	/** main routine to analyze page
		 * 
		 */
	//	protected PDFIndex pdfIndex;
	protected List<AbstractContainer> abstractContainerList;
	
	protected PageChunkAnalyzer(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
	}
	
	public SVGSVG getSVGPage() {
		throw new RuntimeException("BUG");
	}
	
	public PageAnalyzer getPageAnalyzer() {
		return pageAnalyzer;
	}

	private static void addBoldOrItalic(HtmlElement bi, Element parent) {
		for (int j = 0; j < parent.getChildCount(); j++) {
			Node child = parent.getChild(0);
			child.detach();
			bi.appendChild(child);
		}
		parent.appendChild(bi);
	}

	public static void tidyStyles(Element element) {
		if (element != null) {
			convertFontWeightStyleToHtml(element);
			mergeSpans(element);
			SVG2XMLUtil.tidyTagWhiteTag(element, HtmlI.TAG);
			SVG2XMLUtil.tidyTagWhiteTag(element, HtmlB.TAG);
		}
	}

	public static void mergeSpans(Element element) {
		while (true) {
			Nodes spanNodes = element.query(".//*[local-name()='span']");
			if (spanNodes.size() == 0) {
				break;
			}
			for (int i = 0; i < spanNodes.size(); i++) {
				SVG2XMLUtil.replaceNodeByChildren((Element) spanNodes.get(i));
			}
		}
	}

	private static void convertFontWeightStyleToHtml(Element element) {
		Nodes styleAtts = element.query("//@style");
		for (int i = 0; i < styleAtts.size(); i++) {
			Node styleAtt = styleAtts.get(i);
			String style = styleAtt.getValue(); 
			Element parent = (Element) styleAtt.getParent();
			if (style.contains("font-style:italic")) {
				addBoldOrItalic(new HtmlI(), parent);
			}
			if (style.contains("font-weight:bold")) {
				addBoldOrItalic(new HtmlB(), parent);
			}
			styleAtt.detach();
		}
	}

	/** decides whether chunk is Text, Path, Image or Mixed
	 * 
	 * @param svgElement
	 * @return analyzer suited to type (e.g. TextAnalyzer)
	 */
	public static PageChunkAnalyzer createSpecificAnalyzer(SVGElement svgElement, PageAnalyzer pageAnalyzer) {
		PageChunkAnalyzer analyzer = null;
		List<SVGText> textList = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgElement, ".//svg:text"));
		List<SVGPath> pathList = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(svgElement, ".//svg:path"));
		List<SVGImage> imageList = SVGImage.extractImages(SVGUtil.getQuerySVGElements(svgElement, ".//svg:image"));
		if (textList.size() != 0 && (pathList.size() == 0 && imageList.size() == 0)) {
			analyzer = new TextAnalyzer(textList, pageAnalyzer);
		} else if (pathList.size() != 0 && (textList.size() == 0 && imageList.size() == 0)) {
			analyzer = createPathAnalyzer(pathList, pageAnalyzer);
		} else if (imageList.size() != 0 && (textList.size() == 0 && pathList.size() == 0)) {
			analyzer = createImageAnalyzer(imageList, pageAnalyzer);
		} else {
			analyzer = new MixedAnalyzer(pageAnalyzer);
			PageChunkAnalyzer childAnalyzer = null;
			if (imageList.size() != 0) {
				childAnalyzer = createImageAnalyzer(imageList, pageAnalyzer);
				((MixedAnalyzer) analyzer).add(childAnalyzer);
			}
			if (textList.size() != 0) {
				childAnalyzer = new TextAnalyzer(textList, pageAnalyzer);
				((MixedAnalyzer) analyzer).add(childAnalyzer);
			}
			if (pathList.size() != 0) {
				childAnalyzer = createPathAnalyzer(pathList, pageAnalyzer);
				((MixedAnalyzer) analyzer).add(childAnalyzer);
			}
			LOG.trace("MIXED: "+analyzer);
		}
		analyzer.setSVGElement(svgElement);
		return analyzer;
	}

	protected void setSVGElement(SVGElement svgElement) {
		this.svgElement = svgElement;
	}

	private static PageChunkAnalyzer createImageAnalyzer(List<SVGImage> imageList, PageAnalyzer pageAnalyzer) {
		PageChunkAnalyzer analyzer;
		analyzer = new ImageAnalyzer(pageAnalyzer);
		((ImageAnalyzer)analyzer).readImageList(imageList);
		return analyzer;
	}

	private static PageChunkAnalyzer createPathAnalyzer(List<SVGPath> pathList, PageAnalyzer pageAnalyzer) {
		PageChunkAnalyzer analyzer;
		analyzer = new PathAnalyzer(pageAnalyzer);
		((PathAnalyzer)analyzer).readPathList(pathList);
		return analyzer;
	}

	public HtmlElement createHtmlElement() {
		HtmlElement htmlElement = new HtmlDiv();
		htmlElement.appendChild("no content - subclass me?");
		return htmlElement;
	}

	protected void getBoundingBoxAndParent(SVGElement element) {
		parentElement = (SVGElement) element.getParent();
		bbox = parentElement == null ? null : parentElement.getBoundingBox();
	}

	protected Real2Range annotateElement(SVGElement element, String fill, String stroke,
			Double strokeWidth, Double opacity) {
		Real2Range bbox = element.getBoundingBox();
		SVGElement parent =  (SVGElement) element.getParent();
		SVGElement.drawBox(bbox, parent, fill, stroke, strokeWidth, opacity);
		return bbox;
	}

	protected void outputAnnotatedBox(SVGG g, double rectOpacity, double textOpacity,
			String message, double fontSize, String rectFill) {
		Real2Range bbox = g.getBoundingBox();
//				g.appendChild(AbstractPageAnalyzerX.createTextInBox(textOpacity, bbox, message, fontSize));
		if (bbox != null) {
			SVGRect rect = SVGRect.createFromReal2Range(bbox);
			if (rect != null) {
				rect.setTitle(message);
				rect.setFill(rectFill);
				rect.setOpacity(rectOpacity);
				g.appendChild(rect);
			}
		}
	}

	public static SVGG createAnnotationDetails(String fill, Double opacity, Real2Range bbox, String message, Double fontSize) {
		if (bbox == null) {
			throw new RuntimeException("Null bbox");
		}
		SVGG g = new SVGG();
		SVGRect rect = SVGRect.createFromReal2Range(bbox);
		if (rect == null) {
			rect = new SVGRect(10., 10., 100., 20.); // dummy
		}
		rect.setFill(fill);
		rect.setOpacity(opacity);
		g.appendChild(rect);
		if (message != null) {
			SVGElement text = createTextInBox(opacity, bbox, message, fontSize);
			if (text == null) {
				throw new RuntimeException("Null text: "+bbox);
			}
//			g.appendChild(text);
			g.setTitle(text.getValue());
		}
		return g;
	}

	public static SVGElement createTextInBox(Double opacity, Real2Range bbox, String message,
			Double fontSize) {

		SVGElement text = null;
		if (bbox != null &&  bbox.getCorners() != null) {
			text = new SVGText(bbox.getCorners()[0], message);
			text.setOpacity(opacity);
			text.setFontSize(fontSize);
		}
		return text;
	}

	public List<AbstractContainer> createContainers(PageAnalyzer pageAnalyzer) {
		throw new RuntimeException("Override for: "+this.getClass());
	}

	protected void ensureAbstractContainerList() {
		if (abstractContainerList == null) {
			abstractContainerList = new ArrayList<AbstractContainer>();
		}
	}

	public SVGG annotateChunk(List<? extends SVGElement> svgElements) {
		throw new RuntimeException("Override annotateChunk in "+this.getClass());
	}

	public SVGG annotateElements(List<? extends SVGElement> svgElements, double rectOpacity, double textOpacity,
			double fontSize, String rectFill) {
		SVGG g = new SVGG();
		for (int i = 0; i < svgElements.size(); i++) {
			SVGElement element = svgElements.get(i);
			g.appendChild(element.copy());
		}
		String title = this.getClass().getName()+svgElements.size();
		outputAnnotatedBox(g, rectOpacity, textOpacity, title, fontSize, rectFill);
		g.setTitle(title);
		return g;
	}
	
	public ChunkId getChunkId() {
		if (chunkId == null) {
			String id = (svgg == null) ? null : svgg.getId();
			if (id == null) {
				if (svgElement != null) {
					id = svgElement.getId();
					if (id == null) {
						Nodes idNodes = svgElement.query("ancestor::*/@id");
						id = (idNodes.size() == 0) ? null : idNodes.get(0).getValue();
//						SYSOUT.println(svgElement.toXML());
					}
				}
			}
			chunkId = (id == null) ? null : new ChunkId(id);
		}
		return chunkId;
	}
}
