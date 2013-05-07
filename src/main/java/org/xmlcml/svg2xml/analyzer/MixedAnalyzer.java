package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlImg;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;

public class MixedAnalyzer extends AbstractPageAnalyzerX {

	static final Logger LOG = Logger.getLogger(MixedAnalyzer.class);

	private ImageAnalyzerX imageAnalyzer = null;
	private PathAnalyzerX pathAnalyzer = null;
	private TextAnalyzerX textAnalyzer = null;

	private List<AbstractPageAnalyzerX> analyzerList;
	
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
	
	public List<SVGImage> getImageList() { return imageAnalyzer == null ? null : imageAnalyzer.getImageList();}
	public List<SVGPath> getPathList() { return pathAnalyzer == null ? null : pathAnalyzer.getPathList();}
	public List<SVGText> getTextList() { return textAnalyzer == null ? null : textAnalyzer.getTextCharacters();}
	
	public String toString() {
		return "" +
				"image "+(getImageList() == null ? "0" : getImageList().size())+"; "+
				"path "+(getPathList() == null ? "0" : getPathList().size())+"; "+
				"text "+(getTextList() == null ? null : getTextList().size())+"; ";

	}

	public ImageAnalyzerX getImageAnalyzer() {return imageAnalyzer;}
	public PathAnalyzerX getPathAnalyzer() {return pathAnalyzer;}
	public TextAnalyzerX getTextAnalyzer() {return textAnalyzer;}

	@Override
	public SVGG labelChunk() {
		ensureAnalyzerList();
		SVGG g = new SVGG();
		for (AbstractPageAnalyzerX analyzer : analyzerList) {
			SVGG gg = analyzer.labelChunk();
			g.appendChild(gg.copy());
		}
		String title = "MIXED: "+this;
		g.setTitle(title);
		return g;
	}
	
	@Override
	protected HtmlElement createHtml() {
		HtmlDiv element = new HtmlDiv();
		for (AbstractPageAnalyzerX analyzer : analyzerList) {
			HtmlDiv div = new HtmlDiv();
			element.appendChild(div);
			HtmlElement childElement = analyzer.createHtml();
			if (childElement != null) {
				div.appendChild(childElement);
			}
		}
		return element;
	}

	public void add(AbstractPageAnalyzerX analyzer) {
		ensureAnalyzerList();
		LOG.debug("Added "+analyzer);
		setTypedAnalyzer(analyzer);
		analyzerList.add(analyzer);
	}

	private void setTypedAnalyzer(AbstractPageAnalyzerX analyzer) {
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
			analyzerList = new ArrayList<AbstractPageAnalyzerX>();
		}
	}
	


}
