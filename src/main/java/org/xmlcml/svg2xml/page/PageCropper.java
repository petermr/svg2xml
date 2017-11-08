package org.xmlcml.svg2xml.page;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;

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
	private SVGElement svgElement;
	private Real2 userCropxy0;
	private Real2 userCropxy1;

	public PageCropper() {
		setDefaults();
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length != 5) {
			usage();
			return;
		}
		PageCropper pageCropper = new PageCropper();
		pageCropper.readSVG(args[0]);
		pageCropper.setTLBRUserMediaBox(new Real2(0,850), new Real2(600,0));
		pageCropper.setTLBRUserCropBox(args[1],args[2],args[3],args[4]);
	}

	private static void usage() {
		System.out.println("pageCropper <infile> x0 y0 x1 y1)");
		System.out.println("    x0 ... y1 is user crop box");
		System.out.println("user media box defaults to (0,800)(600,0)");
	}

	private void setDefaults() {
		this.localMediaBox = new Real2Range(new Real2(0,0), new Real2(600, 800));
	}

	/** coordinates used in native user coordinates.
	 * 
	 * @param mediaBox
	 */
	public void setLocalMediaBox(Real2Range mediaBox) {
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
	public void setTLBRUserMediaBox(Real2 xy0, Real2 xy1) {
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
	/** box as 4 numbers
	 * x0,y0,x1,y1
	 * 
	 * @param xy0xy1
	 */
	private void setTLBRUserCropBox(String x0, String y0, String x1, String y1) {
		Real2 xy0 = new Real2(x0, y0);
		Real2 xy1 = new Real2(x1, y1);
		this.setTLBRUserCropBox(xy0, xy1);
	}


	/** crop box in cropping coordinates.
	 * 
	 * @param cropBox
	 */
	public void setTLBRUserCropBox(Real2 xy0, Real2 xy1) {
		this.userCropxy0 = new Real2(xy0);
		this.userCropxy0.transformBy(crop2LocalTransform);
		this.userCropxy1 = new Real2(xy1);
		this.userCropxy1.transformBy(crop2LocalTransform);
		
		this.localCropBox = new Real2Range(userCropxy0, userCropxy1);
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

	public void detachElementsOutsideBox() {
		svgElement.detachDescendantElementsOutsideBox(localCropBox);
	}

	public void readSVG(String filename) throws FileNotFoundException {
		File file = new File(filename);
		readSVG(file);
	}

	public void readSVG(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.toString());
		}
		svgElement = SVGElement.readAndCreateSVG(file);
	}

	public SVGElement getSVGElement() {
		return svgElement;
	}

	public void displayCropBox(File svgFile) {
		SVGRect box = SVGRect.createFromReal2Range(getLocalCropBox());
		box.setCSSStyle("stroke:blue;stroke-width:1.0;fill:none;");
		getSVGElement().appendChild(box);
		SVGSVG.wrapAndWriteAsSVG(getSVGElement(), svgFile);
		box.detach();
	}

	public SVGElement cropFile(String fileroot, File inputFile, Real2 tl, Real2 br)
			throws FileNotFoundException {
		readSVG(inputFile);
		setTLBRUserMediaBox(new Real2(0, 800), new Real2(600, 0));
		setTLBRUserCropBox(tl, br);
		// just for display
		displayCropBox(new File(new File("target/crop/"), fileroot + ".raw.box.svg"));
		detachElementsOutsideBox();
		return svgElement;
	}

}
