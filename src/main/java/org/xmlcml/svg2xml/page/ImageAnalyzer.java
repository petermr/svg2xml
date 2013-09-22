package org.xmlcml.svg2xml.page;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.ImageContainer;

/**
 * Analyzes images.
 * empty at present
 * 
 * @author pm286
 *
 */
public class ImageAnalyzer extends ChunkAnalyzer {


	static final Logger LOG = Logger.getLogger(ImageAnalyzer.class);

//	private static final double IMAGE_MAG = 1.5;

	private ImageContainer imageContainer;
	
	public ImageAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	/**
	 * Construct a ImageAnalyzer with list of images
	 * 
	 * @param imageList
	 * @param pageAnalyzer
	 */
	public ImageAnalyzer(List<SVGImage> imageList, PageAnalyzer pageAnalyzer) {
		this(pageAnalyzer);
		addImageList(imageList);
	}

	/**
	 * Construct a ImageAnalyzer with descendant paths from svgElement
	 * 
	 * @param svgElement
	 * @param pageAnalyzer
	 */
	public ImageAnalyzer(SVGElement svgElement, PageAnalyzer pageAnalyzer) {
		this(SVGImage.extractSelfAndDescendantImages(svgElement), pageAnalyzer);
		this.svgChunk = svgElement;
	}

	
	public void addImageList(List<SVGImage> imageList) {
		if (imageList != null) {
			ensureImageContainer();
			imageContainer.addImageList(imageList); 
		}
	}
	
	private void ensureImageContainer() {
		if (this.imageContainer == null) {
			this.imageContainer = new ImageContainer(this);
		}
	}

	public void setImageContainer(ImageContainer imageContainer) {
		this.imageContainer = imageContainer;
	}

	/** a delegate accessing ImageList in ImageContainer.
	 * 
	 * @return ImageContainer.getImageList() or null;
	 */
	public List<SVGImage> getImageList() {
		ensureImageContainer();
		return imageContainer.getImageList();
	}
	
	/** 
	 * 
	 * @return
	 */
	@Override
	public List<AbstractContainer> createContainers() {
		ensureImageContainer();
		ensureAbstractContainerList();
		abstractContainerList.add(imageContainer);
		return abstractContainerList;
	}

	public String toString() {
		ensureImageContainer();
		String s = "";
		s += "paths: "+imageContainer.getImageList().size();
		return s;
	}

}
