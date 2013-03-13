package org.xmlcml.svg2xml.analyzer;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;

/**
 * Analyzes images.
 * empty at present
 * 
 * @author pm286
 *
 */
public class ImageAnalyzerX extends AbstractPageAnalyzerX {

	static final Logger LOG = Logger.getLogger(ImageAnalyzerX.class);

	private List<SVGImage> imageList;
	
	public ImageAnalyzerX() {
		super();
	}
	
	public ImageAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}
	
	public void readImageList(List<SVGImage> imageList) {
		this.imageList = new ArrayList<SVGImage>();
		for (SVGImage image : imageList) {
			this.imageList.add(image); 
		}
	}
	
	public List<SVGImage> getImageList() { return imageList;}
}
