package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlImg;
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

	private static final double IMAGE_MAG = 1.5;

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

	@Override
	public SVGG labelChunk() {
		SVGG g = new SVGG();
		for (int i = 0; i < imageList.size(); i++) {
			SVGImage image = imageList.get(i);
			annotateElement(image, "green", "blue", 0.5, 0.2);
			g.appendChild(image.copy());
		}
		String title = "IMAGE "+imageList.size();
		outputAnnotatedBox(g, 0.2, 0.7, title, 5.0, "cyan");
		g.setTitle(title);
		return g;
	}

	@Override
	protected HtmlElement createHtml() {
		LOG.trace("image html"+imageList.size());
		HtmlElement element = new HtmlDiv();
		for (int i = 0; i < imageList.size(); i++) {
			SVGImage image = imageList.get(i);
			HtmlImg img = new HtmlImg();
			element.appendChild(img);
			Double width = image.getWidth();
			Double height = image.getHeight();
			Double ratio = width / height;
			img.setSrc(image.getImageValue());
			if (ratio > 1.3) {
				width = Math.min(800., width*IMAGE_MAG);
				height = width / ratio;
			} else {
				height = Math.min(500., height*IMAGE_MAG);
				width = height * ratio;
			}
			img.addAttribute(new Attribute("width", ""+width));
			img.addAttribute(new Attribute("height", ""+height));
		}
		return element;
	}
	
	public String toString() {
		String s = "";
		s += "images: "+imageList.size();
		return s;
	}

}
