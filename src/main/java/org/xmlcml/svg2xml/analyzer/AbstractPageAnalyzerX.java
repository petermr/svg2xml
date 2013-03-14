package org.xmlcml.svg2xml.analyzer;

import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlB;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlI;
import org.xmlcml.svg2xml.action.PageEditorX;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

public abstract class AbstractPageAnalyzerX {
	
	private final static Logger LOG = Logger.getLogger(AbstractPageAnalyzerX.class);

	protected SVGG svgg; // current svg:gelement
	protected SemanticDocumentActionX semanticDocumentActionX;
	protected PageEditorX pageEditorX;
	
	protected AbstractPageAnalyzerX() {
	}

	protected AbstractPageAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		this();
		this.semanticDocumentActionX = semanticDocumentActionX;
		this.pageEditorX = getPageEditor();
	}
	
	public PageEditorX getPageEditor() {
		return semanticDocumentActionX.getPageEditor();
	}
	
	public SVGSVG getSVGPage() {
		return getPageEditor().getSVGPage();
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
			analyzer = TextAnalyzerX.createTextAnalyzerWithSortedLines(textList);
		} else if (pathList.size() != 0 && (textList.size() == 0 && imageList.size() == 0)) {
			analyzer = new PathAnalyzerX();
			((PathAnalyzerX)analyzer).readPathList(pathList);
		} else if (imageList.size() != 0 && (textList.size() == 0 && pathList.size() == 0)) {
			analyzer = new ImageAnalyzerX();
			((ImageAnalyzerX)analyzer).readImageList(imageList);
		} else {
			analyzer = new MixedAnalyzer();
			((MixedAnalyzer)analyzer).readImageList(imageList);
			((MixedAnalyzer)analyzer).readPathList(pathList);
			((MixedAnalyzer)analyzer).readTextList(textList);
		}

		return analyzer;
	}

}
