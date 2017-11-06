package org.xmlcml.svg2xml.page;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.SVGElement;

/** extracts parts of page through user-supplied crop boxes
 * 
 * will gradually accrete unit conversion, offsets, etc.
 * 
 * The user informs PageCropper of their cropping media box,
 * then creates a cropBox in their own units,
 * 
 * @author pm286
 *
 */
public class PageCropper {
	private static final Logger LOG = Logger.getLogger(PageCropper.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Real2Range localCropBox;
	private Real2Range localMediaBox;
	private Transform2 crop2LocalTransform;

	public PageCropper() {
		setDefaults();
	}

	private void setDefaults() {
		this.localMediaBox = new Real2Range(new Real2(0,0), new Real2(600, 800));
	}

	/** coordinates used in native user coordinates.
	 * 
	 * @param mediaBox
	 */
	public void setMediaBox(Real2Range mediaBox) {
		this.localMediaBox = mediaBox;
	}

	/** the transformation used by the cropper.
	 * this allows for transformation between units, y-direction, offsets, etc.
	 * 
	 * Note that Real2Range cannot hold negative ranges so we don't have a traditional box
	 * e.g. Inkscape has Y coordinates UP the page from bottom = 0 to top = 800
	 * so Inkscape cropMediaBox = Real2(0, 800), Real2(600, 0)
	 * 
	 * @param xy0 one corner
	 * @param xy1 opposite corner
	 */
	public void setCropMediaBox(Real2 xy0, Real2 xy1) {
		if (this.localMediaBox == null) {
			throw new RuntimeException("Must have local mediaBox");
		}
		double X0 = this.localMediaBox.getXMin();
		double X1 = this.localMediaBox.getXMax();
		double xScale = (xy1.x - xy0.x) / (X1 - X0);
		double xConstant = -X0 * xScale + xy0.x;
		
		double Y0 = this.localMediaBox.getYMin();
		double Y1 = this.localMediaBox.getYMax();
		double yScale = (xy1.y - xy0.y) / (Y1 - Y0);
		double yConstant = -Y0 * yScale + xy0.y;
		
		crop2LocalTransform = Transform2.createScaleTransform(xScale, yScale);
		crop2LocalTransform.setTranslation(new Real2(xConstant, yConstant));
		
	}

	/** crop box in cropping coordinates.
	 * 
	 * @param cropBox
	 */
	public void setCropBox(Real2 xy0, Real2 xy1) {
		Real2 xy0new = new Real2(xy0);
		xy0new.transformBy(crop2LocalTransform);
		Real2 xy1new = new Real2(xy1);
		xy1new.transformBy(crop2LocalTransform);
		
		this.localCropBox = new Real2Range(xy0new, xy1new);
	}

	public Real2Range getLocalCropBox() {
		return localCropBox;
	}

	public Transform2 getCropToLocalTransformation() {
		return crop2LocalTransform;
	}

	public List<SVGElement> extractContainedElements(List<SVGElement> descendants) {
		List<SVGElement> contained = SVGElement.extractElementsContainedInBox(descendants, localCropBox);
		return contained;
	}

	public List<SVGElement> extractDescendants(SVGElement svgElement) {
		List<SVGElement> descendants = SVGElement.extractSelfAndDescendantElements(svgElement);
		return descendants;
	}

	public void detachElementsOutsideBox(SVGElement element) {
		element.detachDescendantElementsOutsideBox(localCropBox);
	}
	
}
