package org.xmlcml.svg2xml.page;

import java.util.ArrayList;

import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlImg;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.ImageContainer;
import org.xmlcml.svg2xml.container.PathContainer;

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
