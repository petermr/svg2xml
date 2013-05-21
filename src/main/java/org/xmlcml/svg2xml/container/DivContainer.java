package org.xmlcml.svg2xml.container;


import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.analyzer.MixedAnalyzer;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.text.TextLine;

public class DivContainer extends AbstractContainer {

	public final static Logger LOG = Logger.getLogger(DivContainer.class);
	private boolean box;

	public DivContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}

	public static DivContainer createDivContainer(PageAnalyzer pageAnalyzer, MixedAnalyzer mixedAnalyzer) {
		DivContainer divContainer = new DivContainer(pageAnalyzer);
		addSVGElements(divContainer, mixedAnalyzer);
		return divContainer;
	}

	private static void addSVGElements(DivContainer divContainer, MixedAnalyzer mixedAnalyzer) {
		List<SVGPath> pathList = mixedAnalyzer.getPathList();
		if (pathList != null && pathList.size() > 0){
			divContainer.addPathList(pathList);
		}
		List<SVGImage> imageList = mixedAnalyzer.getImageList();
		if (imageList != null && imageList.size() > 0){
			divContainer.addImageList(imageList);
		}
		List<SVGText> textList = mixedAnalyzer.getTextList();
		if (textList != null && textList.size() > 0){
			divContainer.addTextList(textList);
		}
	}

	public void addImageList(List<SVGImage> imageList) {
		if (imageList != null && imageList.size() > 0) {
			ImageContainer imageContainer = new ImageContainer(pageAnalyzer);
			imageContainer.add(imageList);
			this.add(imageContainer);
		}
	}

	public void addPathList(List<SVGPath> pathList) {
		if (pathList != null && pathList.size() > 0) {
			PathContainer pathContainer = new PathContainer(pageAnalyzer);
			pathContainer.add(pathList);
			this.add(pathContainer);
		}
	}

	public void addTextList(List<SVGText> characterList) {
		if (characterList != null && characterList.size() > 0) {
			TextAnalyzerX textAnalyzerX = new TextAnalyzerX();
			textAnalyzerX.setTextCharacters(characterList);
			List<TextLine> textLineList = textAnalyzerX.getTextLines();
			TextStructurer textContainer = new TextStructurer(null);
			textContainer.setTextLines(textLineList);
			List<ScriptLine> scriptList = textContainer.getScriptedLineList();
			ScriptContainer scriptContainer = new ScriptContainer(pageAnalyzer);
			scriptContainer.add(scriptList);
			this.add(scriptContainer);
		}
	}

	@Override
	public HtmlElement createHtmlElement() {
		HtmlDiv divElement = new HtmlDiv();
		HtmlP p = new HtmlP();
		p.appendChild("DIV NYI");
		divElement.appendChild(p);
		return divElement;
	}
	
	@Override
	public SVGG createSVGGChunk() {
		SVGG g = new SVGG();
		for (AbstractContainer container : containerList) {
			SVGG childG = container.createSVGGChunk();
			g.appendChild(childG);
		}
		return g;
	}

	@Override
	public String toString() {
		LOG.trace("");
		StringBuilder sb = new StringBuilder("<MIXED>\n"+super.toString());
		sb.append("</MIXED>\n");
		String s = sb.toString();
		return s;
	}

	public void setBox(boolean b) {
		this.box = b;
	}
	
	public boolean hasBox() {return box;}

	@Override
	public String getRawValue() {
		StringBuilder sb = new StringBuilder();
		for (AbstractContainer container : containerList) {
			sb.append(container.getRawValue()+"\n");
		}
		return sb.toString();
	}

}
