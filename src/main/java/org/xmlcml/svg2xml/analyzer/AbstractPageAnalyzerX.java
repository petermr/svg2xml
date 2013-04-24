package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.xmlcml.svg2xml.action.PageEditorX;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.text.TextLineContainer;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

public abstract class AbstractPageAnalyzerX implements Annotatable {
	
	private final static Logger LOG = Logger.getLogger(AbstractPageAnalyzerX.class);

	protected SVGG svgg; // current svg:gelement
	protected SemanticDocumentActionX semanticDocumentActionX;
	protected PageEditorX pageEditorX;
	protected Real2Range bbox;
	protected SVGElement parentElement;
	
	static List<String> titleList = new ArrayList<String>();
	static {
		titleList.add(new FigureAnalyzerX((PDFIndex)null).getTitle());
		titleList.add(new TableAnalyzerX((PDFIndex)null).getTitle());
//		titleList.add(new SnippetAnalyzer((PDFIndex)null).getTitle());
	}

	List<ChunkId> idList;
	List<Integer> serialList;

	protected PDFIndex pdfIndex;

	private Map<ChunkId, HtmlElement> htmlElementByIdMap;

	protected AbstractPageAnalyzerX() {
	}

	protected AbstractPageAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		this();
		this.semanticDocumentActionX = semanticDocumentActionX;
		this.pageEditorX = getPageEditor();
	}
	
	public AbstractPageAnalyzerX(PDFIndex pdfIndex) {
		this();
		this.pdfIndex = pdfIndex;
	}

	public PageEditorX getPageEditor() {
		return semanticDocumentActionX == null ? null : semanticDocumentActionX.getPageEditor();
	}
	
	public SVGSVG getSVGPage() {
		return getPageEditor() == null ? null : getPageEditor().getSVGPage();
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
			convertFontWeightStyleToHTML(element);
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

	private static void convertFontWeightStyleToHTML(Element element) {
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

	public static AbstractPageAnalyzerX getAnalyzer(SVGElement svgElement) {
		AbstractPageAnalyzerX analyzer = null;
		List<SVGText> textList = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgElement, ".//svg:text"));
		List<SVGPath> pathList = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(svgElement, ".//svg:path"));
		List<SVGImage> imageList = SVGImage.extractImages(SVGUtil.getQuerySVGElements(svgElement, ".//svg:image"));
		if (textList.size() != 0 && (pathList.size() == 0 && imageList.size() == 0)) {
			analyzer = TextLineContainer.createTextAnalyzerWithSortedLines(textList);
		} else if (pathList.size() != 0 && (textList.size() == 0 && imageList.size() == 0)) {
			analyzer = createPathAnalyzer(pathList);
		} else if (imageList.size() != 0 && (textList.size() == 0 && pathList.size() == 0)) {
			analyzer = createImageAnalyzer(imageList);
		} else {
			analyzer = new MixedAnalyzer();
			AbstractPageAnalyzerX childAnalyzer = null;
			if (imageList.size() != 0) {
				childAnalyzer = createImageAnalyzer(imageList);
				((MixedAnalyzer) analyzer).add(childAnalyzer);
			}
			if (textList.size() != 0) {
				childAnalyzer = TextLineContainer.createTextAnalyzerWithSortedLines(textList);
				((MixedAnalyzer) analyzer).add(childAnalyzer);
			}
			if (pathList.size() != 0) {
				childAnalyzer = createPathAnalyzer(pathList);
				((MixedAnalyzer) analyzer).add(childAnalyzer);
			}
		}

		return analyzer;
	}

	private static AbstractPageAnalyzerX createImageAnalyzer(List<SVGImage> imageList) {
		AbstractPageAnalyzerX analyzer;
		analyzer = new ImageAnalyzerX();
		((ImageAnalyzerX)analyzer).readImageList(imageList);
		return analyzer;
	}

	private static AbstractPageAnalyzerX createPathAnalyzer(List<SVGPath> pathList) {
		AbstractPageAnalyzerX analyzer;
		analyzer = new PathAnalyzerX();
		((PathAnalyzerX)analyzer).readPathList(pathList);
		return analyzer;
	}

	public abstract SVGG labelChunk();

	protected HtmlElement createHTML() {
		HtmlElement htmlElement = new HtmlDiv();
		htmlElement.appendChild("no content yet");
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
				SVGRect rect = SVGRect.createFromReal2Range(bbox);
				rect.setTitle(message);
				rect.setFill(rectFill);
				rect.setOpacity(rectOpacity);
				g.appendChild(rect);
			}

	public Integer indexAndLabelChunk(String content, ChunkId id) {
		Pattern pattern = getPattern();
		String title = getTitle();
		Integer serial = getSerial(pattern, content);
		if (serial != null) {
			LOG.debug(title+serial);
			ensureIdSerialList();
			serialList.add(serial);
			idList.add(id);
			pdfIndex.addUsedId(id);
			labelChunk(id, title, serial);
		}
		return serial;
	}

	/** label HtmlElement
	 * 
	 * @param id
	 * @param title
	 * @param serial
	 */
	private void labelChunk(ChunkId id, String title, Integer serial) {
		getHtmlElement(id);
		HtmlElement htmlElement = getHtmlElement(id);
		if (htmlElement != null) {
			String classX = htmlElement.getClassAttribute();
			if (classX == null) {
				htmlElement.setClassAttribute(title+" "+serial);
			}
		}
	}

	protected HtmlElement getHtmlElement(ChunkId id) {
		ensureHtmlElementByIdMap();
		return (htmlElementByIdMap == null) ? null : htmlElementByIdMap.get(id);
	}

	protected void ensureHtmlElementByIdMap() {
		if (htmlElementByIdMap == null && pdfIndex != null) {
			htmlElementByIdMap = pdfIndex.getHtmlElementByIdMap();
		}
	}

	private void ensureIdSerialList() {
		if (idList == null) {
			idList = new ArrayList<ChunkId>();
			serialList = new ArrayList<Integer>();
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
			SVGText text = createTextInBox(opacity, bbox, message, fontSize);
			if (text == null) {
				throw new RuntimeException("Null text: "+bbox);
			}
//			g.appendChild(text);
			g.setTitle(text.getValue());
		}
		return g;
	}

	public static SVGText createTextInBox(Double opacity, Real2Range bbox, String message,
			Double fontSize) {

		SVGText text = null;
		if (bbox != null &&  bbox.getCorners() != null) {
			text = new SVGText(bbox.getCorners()[0], message);
			text.setOpacity(opacity);
			text.setFontSize(fontSize);
		}
		return text;
	}

	/** returns null if no match, -1 if serial not found, else serial
	 * 
	 * @param pattern
	 * @param content
	 * @return
	 */
	public static Integer getSerial(Pattern pattern, String content) {
		Integer serial = null;
		if (content != null) {
			Matcher matcher = pattern.matcher(content);
			if (matcher.matches()) {
				if (matcher.groupCount() == 0) {
					serial = -1;
				} else {
					String s = matcher.group(1);
					if (s != null) {
						s = s.trim();
						serial= -1;
						try {
							serial = new Integer(s);
						} catch (Exception e) {
							// not a number
						}
					}
				}
			}
		}
		return serial;
	}

	public boolean isChunk(String classAttribute) {
		return classAttribute != null && classAttribute.startsWith(getTitle());
	}

	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	protected Pattern getPattern() {
		return null;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	public String getTitle() {
		return null;
	}
}
