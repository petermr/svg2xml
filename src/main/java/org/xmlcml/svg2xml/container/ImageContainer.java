package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlImg;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.analyzer.ImageAnalyzerX;
import org.xmlcml.svg2xml.analyzer.PDFIndex;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;

public class ImageContainer extends AbstractContainer  {

	public final static Logger LOG = Logger.getLogger(ImageContainer.class);

	private List<SVGImage> imageList;
	
	public ImageContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	/** move to ImageAnalyzerX
	 * 
	 * @param pageAnalyzer
	 * @param imageAnalyzer
	 * @return
	 */
	public static ImageContainer createImageContainer(PageAnalyzer pageAnalyzer, ImageAnalyzerX imageAnalyzer) {
		ImageContainer imageContainer = new ImageContainer(pageAnalyzer);
		addSVGElements(imageContainer, imageAnalyzer);
		return imageContainer;
	}
	
	private static void addSVGElements(ImageContainer imageContainer, ImageAnalyzerX imageAnalyzer) {
		List<SVGImage> imageList = imageAnalyzer.getImageList();
		if (imageList != null && imageList.size() > 0){
			imageContainer.addImageList(imageList);
		}
	}

	private void addImageList(List<SVGImage> imageList) {
		ensureImageList();
		this.imageList.addAll(imageList);
	}


	@Override
	public HtmlElement createHtmlElement() {
		if (htmlElement == null) {
			htmlElement = new HtmlDiv();
			HtmlImg imgElement = new HtmlImg();
			imgElement.setAlt("Image NYI");
			htmlElement.appendChild(imgElement);
			HtmlP p = new HtmlP("PATH");
			htmlElement.appendChild(p);
		}
		return htmlElement;
	}
	
	public List<SVGImage> getImageList() {
		return imageList;
	}

	public void add(SVGImage image) {
		ensureImageList();
		this.imageList.add(image);
	}

	private void ensureImageList() {
		if (imageList == null) {
			this.imageList = new ArrayList<SVGImage>();
		}
	}
	
	@Override
	public SVGG createSVGGChunk() {
		SVGG g = new SVGG();
		ensureImageList();
		for (SVGImage image : imageList) {
			g.appendChild(image.copy());
		}
		return g;
	}
	
	@Override
	public String summaryString() {
		StringBuilder sb = new StringBuilder(">>>ImageContainer>>>"+" images: "+imageList.size()+"\n");
		sb.append("<<<ImageContainer<<<");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString()+"\n");
		sb.append(outputSVGList("Images", imageList));
		return sb.toString();
	}

	public void add(List<SVGImage> imageList) {
		ensureImageList();
		this.imageList.addAll(imageList);
	}
	
	public void addToIndexes(PDFIndex pdfIndex) {
		String imageString = this.toString();
		pdfIndex.addToImageIndex(imageString, this);
	}
}
