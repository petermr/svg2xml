package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;

public class MixedAnalyzer extends AbstractPageAnalyzerX {

	static final Logger LOG = Logger.getLogger(ImageAnalyzerX.class);

	private List<SVGImage> imageList;
	private List<SVGPath> pathList;
	private List<SVGText> textList;
	
	public MixedAnalyzer() {
		super();
	}
	
	public MixedAnalyzer(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}
	
	public void readImageList(List<SVGImage> imageList) {
		this.imageList = new ArrayList<SVGImage>();
		for (SVGImage image : imageList) {
			this.imageList.add(image); 
		}
	}
	
	public List<SVGImage> getImageList() { return imageList;}
	public List<SVGPath> getPathList() { return pathList;}
	public List<SVGText> getTextList() { return textList;}
	
	public void readPathList(List<SVGPath> pathList) {
		this.pathList = new ArrayList<SVGPath>();
		for (SVGPath path : pathList) {
			this.pathList.add(path); 
		}
	}
	
	public void readTextList(List<SVGText> textList) {
		this.textList = new ArrayList<SVGText>();
		for (SVGText text : textList) {
			this.textList.add(text); 
		}
	}
	
	public String toString() {
		return "image "+imageList.size()+"; path "+pathList.size()+"; text "+textList.size();
	}
	
}
