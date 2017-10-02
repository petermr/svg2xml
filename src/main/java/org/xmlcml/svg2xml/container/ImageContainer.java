package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.html.HtmlDiv;
import org.xmlcml.graphics.html.HtmlElement;
import org.xmlcml.graphics.html.HtmlP;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.svg2xml.figure.FigureGraphic;
import org.xmlcml.svg2xml.page.ImageAnalyzer;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.pdf.ChunkId;
import org.xmlcml.svg2xml.pdf.PDFIndex;

public class ImageContainer extends AbstractContainerOLD  {

	public final static Logger LOG = Logger.getLogger(ImageContainer.class);
	
	private List<SVGImage> imageList;
	private ImageAnalyzer imageAnalyzer;
	
	public ImageContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public ImageContainer(ImageAnalyzer imageAnalyzer) {
		super(imageAnalyzer);
		this.imageAnalyzer = imageAnalyzer;
		this.imageAnalyzer.setImageContainer(this);
	}

	public void addImageList(List<SVGImage> imageList) {
		ensureImageList();
		this.imageList.addAll(imageList);
	}

	@Override
	public HtmlElement createHtmlElement() {
		if (htmlElement == null) {
			super.createHtmlElement();
			ChunkId chunkId = this.getChunkId();
			String id = chunkId == null ? String.valueOf(System.currentTimeMillis()) : chunkId.toString();
			if (imageList.size() > 1) {
				HtmlDiv div = new HtmlDiv();
				htmlElement.appendChild(div);
				LOG.trace("IMAGES: "+imageList.size());
				for (int i = 0; i < imageList.size(); i++ ) {
					SVGImage image =imageList.get(i);
					if (image.getImageValue().length() > 100) {
						writeHtmlImg(id+"."+(i + 1), div);
					}
				}
			} else {
				writeHtmlImg(id, htmlElement);
			}
		}
		return htmlElement;
	}

	private void writeHtmlImg(String id, HtmlElement parent) {
		String imageName = pageAnalyzer.getPageIO().createImageFilename(id);
		HtmlDiv div = FigureGraphic.createHtmlImgDivElement(imageName, "20%");
		parent.appendChild(div);
		HtmlP p = new HtmlP("IMAGE");
		parent.appendChild(p);
	}
	
	public List<SVGImage> getImageList() {
		ensureImageList();
		return imageList;
	}

//	public void add(SVGImage image) {
//		ensureImageList();
//		this.imageList.add(image);
//	}

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

	public void addToIndexes(PDFIndex pdfIndex) {
		String imageString = this.toString();
		pdfIndex.addToImageIndex(imageString, this);
	}
}
