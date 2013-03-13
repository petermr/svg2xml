package org.xmlcml.svg2xml.analyzer;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.action.PageEditorX;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;

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
