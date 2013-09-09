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
import org.xmlcml.svg2xml.figure.FigureGraphic;
import org.xmlcml.svg2xml.page.ImageAnalyzer;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.pdf.ChunkId;
import org.xmlcml.svg2xml.pdf.PDFIndex;

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
	public static ImageContainer createImageContainer(PageAnalyzer pageAnalyzer, ImageAnalyzer imageAnalyzer) {
		ImageContainer imageContainer = new ImageContainer(pageAnalyzer);
		addSVGElements(imageContainer, imageAnalyzer);
		return imageContainer;
	}
	
	private static void addSVGElements(ImageContainer imageContainer, ImageAnalyzer imageAnalyzer) {
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
			super.createHtmlElement();
			ChunkId chunkId = getChunkId();
			String id = chunkId == null ? String.valueOf(System.currentTimeMillis()) : chunkId.toString();
			String imageName = pageAnalyzer.getPageIO().createImageFilename(id);
			HtmlDiv div = FigureGraphic.createHtmlImgDivElement(imageName, "20%");
			htmlElement.appendChild(div);
			HtmlP p = new HtmlP("IMAGE");
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
