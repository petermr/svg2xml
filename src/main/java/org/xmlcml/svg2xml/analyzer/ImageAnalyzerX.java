package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGText;
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

	@Override
	public SVGG annotate() {
		SVGG g = new SVGG();
		for (int i = 0; i < imageList.size(); i++) {
			SVGImage image = imageList.get(i);
			annotateElement(image, "green", "blue", 0.5, 0.2);
			g.appendChild(image.copy());
		}
		String title = "IMAGE "+imageList.size();
//		outputAnnotatedBox(g, 0.2, 0.7, title, 5.0, "cyan");
		g.setTitle(title);
		return g;
	}

//	private void annotateImage(SVGImage image, Real2Range bboxTot) {
//		Real2Range bbox = image.getBoundingBox();
//		LOG.debug("image "+bbox);
//		SVGElement parent = (SVGElement) (SVGElement) image.getParent();
//		SVGElement.drawBox(bbox, parent, "green", "blue", 0.5, 0.2);
//		bboxTot.plusEquals(bbox);
//	}

	public String toString() {
		String s = "";
		s += "images: "+imageList.size();
		return s;
	}

}
