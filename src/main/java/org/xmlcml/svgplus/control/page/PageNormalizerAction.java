package org.xmlcml.svgplus.control.page;

import java.io.File;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Range;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.StyleBundle;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.util.CodePointConverter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class PageNormalizerAction extends PageAction  {

	private static final String OLD_FONT_SIZE = "oldFontSize";
	private static final String IMAGE_REMOVED = "IMAGE_REMOVED";
	private final static double EPS = 0.001;
	private final static Logger LOG = Logger.getLogger(PageNormalizerAction.class);
	

	/**
    <pageAction action="normalize" 
    	translateCipPathsToLogicalStyles="true"
        denormalizeFontSizes="true"
        removeUnwantedAttributes="true"
        removeImageData="true" 
        applyAndRemoveCumulativeTransforms="true"
        formatDecimalPlaces="3"/>
	 */
	/** attribute names
	 * 
	 */
	
	
	private static final String STYLE = "style";
	private static final double X_OFFSET = 0.;
	private static final double Y_OFFSET = 0.;
	private static final String ANGLE = "angle";
	private static final double[] UNIT_ARRAY = new double[]{1.,0.,0.,0.,1.,0.,0.,0.,1.};	
	private static final RealArray UNIT_REAL_ARRAY = new RealArray(UNIT_ARRAY);
	
	private Multimap<Integer, SVGText> textByRotation;
	private CodePointConverter codePointConverter = new CodePointConverter();

	public PageNormalizerAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	public void run() {
		if (isTrue(PageActionElement.NORMALIZE_HIGH_CODE_POINTS)) {
			normalizeHighCodePoints(getSVGPage());
		}
		if (isTrue(PageActionElement.REMOVE_IMAGE_DATA)) {
			removeImageData(getSVGPage());
		}
		if (isTrue(PageActionElement.DENORMALIZE_FONT_SIZES)) {
			SVGUtil.denormalizeFontSizes(getSVGPage());
		}
		if (isTrue(PageActionElement.REMOVE_UNWANTED_ATTRIBUTES)) {
			PageAnalyzer.removeUnwantedSVGAttributesAndAddIds(getSVGPage());
		}
		if (isTrue(PageActionElement.CLEAN_SVG_STYLES)) {
			removeCSSStyleAndExpandAsSeparateAttributes();
		}
		if (isTrue(PageActionElement.APPLY_AND_REMOVE_CUMULATIVE_TRANSFORMS)) {
			SVGUtil.applyAndRemoveCumulativeTransformsFromDocument(getSVGPage());
		}
				
		if (isTrue(PageActionElement.CAN_ROTATE_LANDSCAPE)) {
			guessAndApplyConvertToLandscape();
			debugFile("target/pageNorm1Rotation.svg");
		}
		
		Integer decimalPlaces = getDecimalPlaces();
		if (decimalPlaces != null) {
			getSVGPage().format(decimalPlaces);
			debugFile("target/pageNorm2Decimal.svg");
		}
		
		if (isTrue(PageActionElement.REMOVE_UNIT_TRANSFORMS)) {
			removeUnitTransforms(getSVGPage());
//			debugFile("target/pageNorm1Rotation.svg");
		}
		
	}

	private void removeUnitTransforms(SVGElement element) {
		Nodes transformAttributes = element.query(".//@transform");
		LOG.debug("TRANSFORM "+transformAttributes.size());
		for (int i = 0; i < transformAttributes.size(); i++) {
			Attribute transformAttribute = (Attribute) transformAttributes.get(i);
			String transformAttributeValue = transformAttribute.getValue();
			Transform2 t2 = SVGElement.createTransform2FromTransformAttribute(transformAttributeValue);
			RealArray matrixArray = new RealArray(t2.getMatrixAsArray());
			if (UNIT_REAL_ARRAY.equals(matrixArray, EPS)) {
				transformAttribute.detach();
			}
		}
	}

	private void normalizeHighCodePoints(SVGSVG svgPage) {
		List<SVGText> texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgPage, ".//svg:text"));
		int i =0;
		for (SVGText text : texts) {
			String s = text.getValue();
			int codePoint = s.codePointAt(0);
			if (codePoint > 127) {
				ensureCodePointConverter();
				String newChar = codePointConverter .convertCharacter(new Character((char)s.charAt(0)));
				if (newChar == null) {
					LOG.debug(" unknown codePoint " + codePoint);
				}
				text.setText(""+newChar);
				LOG.trace(" codePoint " + codePoint+ "["+text.getValue()+"]");
				if (newChar != null && newChar.startsWith("[")) {
					LOG.debug("codePoint "+debug(texts, i));
				}
			}
			i++;
		}
	}

	private String debug(List<SVGText> texts, int ipos) {
		int imin = Math.max(ipos-10,  0);
		int imax = Math.min(ipos+10,  texts.size()-1);
		StringBuilder sb = new StringBuilder();
		for (int i = imin; i < ipos; i++) {
			sb.append(texts.get(i).getValue());
			sb.append("_");
		}
		sb.append("_");
		sb.append(texts.get(ipos).getValue());
		sb.append("_");
		for (int i = ipos+1; i <= imax; i++) {
			sb.append("_");
			sb.append(texts.get(i).getValue());
		}
		return sb.toString();
	}

	private void ensureCodePointConverter() {
		if (codePointConverter == null) {
			codePointConverter = new CodePointConverter();
			codePointConverter.readResourceAndCreateMap();
		}
	}

	private void guessAndApplyConvertToLandscape() {
		List<SVGElement> textsNoTransform = SVGUtil.getQuerySVGElements(getSVGPage(), ".//svg:text[not(@transform)]");
		List<SVGElement> textsTransform = SVGUtil.getQuerySVGElements(getSVGPage(), ".//svg:text[@transform]");
		textByRotation = ArrayListMultimap.create();
		for (SVGElement textElement : textsNoTransform) {
			textByRotation.put(0, (SVGText) textElement);
		}
		storeRotationCounts(textsTransform);
		guessRotationAndApply();
		removeRedundantUnitMatrices();
	}

	private void removeRedundantUnitMatrices() {
		Transform2 unit = new Transform2();
		RealArray unitRa = new RealArray(unit.getMatrixAsArray());
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), ".//svg:*[@transform]");
		for (SVGElement element : elements) {
			Transform2 t2 = element.getTransform();
			RealArray t2Ra = new RealArray(t2.getMatrixAsArray());
			if (unitRa.equals(t2Ra, EPS)) {
				Attribute t2Att = element.getAttribute(SVGElement.TRANSFORM);
				t2Att.detach();
			}
		}
	}

	private void guessRotationAndApply() {
		int minus90Count = textByRotation.get(-90).size();
		int zeroCount = textByRotation.get(0).size();
		int plus90Count = textByRotation.get(90).size();
		int upsideDownCount = textByRotation.get(180).size();
		LOG.debug("rotated -90: "+minus90Count);
		LOG.debug("unrotated  : "+zeroCount);
		LOG.debug("rotated 90 : "+plus90Count);
		LOG.debug("upsideDown : "+upsideDownCount);
		Integer pageRotation = null;
		if (plus90Count > 0 && minus90Count == 0) {
			pageRotation = -90;
		} else if (plus90Count == 0 && minus90Count > 0) {
			pageRotation = 90;
		} else if (zeroCount > 0 && minus90Count == 0 && plus90Count == 0) {
			LOG.debug("no rotation required");
		}
		if (pageRotation != null) {
			rotatePage(pageRotation);
		}
	}

	private void storeRotationCounts(List<SVGElement> textsTransform) {
		for (SVGElement textElement : textsTransform) {
			SVGText text = (SVGText) textElement;
			Transform2 transform = text.getTransform();
			Angle rotAngle = transform.getAngleOfRotation();
			if (rotAngle == null) {
				LOG.debug("Cannot get rotation angle: "+transform);
			} else {
				rotAngle.setRange(Range.SIGNED);
				int angle = (int) Math.round(rotAngle.getDegrees());
				if (angle % 90 != 0) {
					LOG.warn("Unusual rotation angle of text: "+angle);
				} else {
					if ((double) angle < -180+EPS) {
						angle += 360;
					}
					textByRotation.put(angle, text);
					text.addAttribute(new Attribute(ANGLE, ""+angle));
				}
			}
		}
	}

	private void rotatePage(Integer angle) {
		this.pageAnalyzer.setRotationAngle(angle);
		Angle newAngle = new Angle((double) angle, Units.DEGREES);
		Transform2 t2 = new Transform2(newAngle);
		// get leaf nodes or text (might have tspans)
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), ".//svg:*[count(*)=0 or self::svg:text]");
		for (SVGElement element : elements) {
			Double fontSize = null;
			if (element instanceof SVGText) {
				fontSize = Math.abs(((SVGText)element).getFontSize());
				element.addAttribute(new Attribute(OLD_FONT_SIZE, ""+fontSize));
			}
			element.applyTransform(t2);
		}
		Real2Range bbox = getSVGPage().getBoundingBox();
		LOG.debug("BB "+bbox);
		Real2 origin = new Real2(bbox.getXRange().getMin()+X_OFFSET, bbox.getYRange().getMin()+Y_OFFSET);
		t2 = new Transform2(new Vector2(origin.multiplyBy(-1.0)));
		for (SVGElement element : elements) {
			element.applyTransform(t2);
			if (element instanceof SVGText) {
				SVGText text = (SVGText)element;
				String angleS = text.getAttributeValue(ANGLE);
				if (angleS == null) {
					angleS = "0.0";
				}
				Angle oldAngle = new Angle(new Double(angleS), Units.DEGREES);
				Angle deltaAngle = oldAngle.subtract(newAngle);
				deltaAngle = deltaAngle.plus(new Angle(Math.PI));
				Transform2 tang = new Transform2(deltaAngle);
				text.setTransformToRotateAboutTextOrigin(tang);
				Double fontSize = new Double(text.getAttributeValue(OLD_FONT_SIZE));
				text.setFontSize(fontSize);
			}
		}
	}

	private void removeImageData(SVGSVG svgPage) {
		List<SVGElement> images = SVGUtil.getQuerySVGElements(svgPage, ".//svg:image");
		LOG.trace("Images to remove: "+images.size());
		if (images.size() > 0) {
			for (SVGElement image : images) {
				replaceImageByTextMessage(image);
			}
			CMLUtil.outputQuietly(svgPage, new File("target/deimage"+pageAnalyzer.getPageNumber()+".xml"), 1);
		}
	}

	private void replaceImageByTextMessage(SVGElement image) {
		SVGRect rect = image.createGraphicalBoundingBox();
		SVGText text = new SVGText(rect.getBoundingBox().getCorners()[0], IMAGE_REMOVED);
//		text.setFontSize(20.0);
		image.getParent().appendChild(rect);
		image.getParent().appendChild(text);
		image.detach();
	}

	private void removeCSSStyleAndExpandAsSeparateAttributes() {
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), "//*[@style]");
		for (SVGElement element : elements) {
			PageNormalizerAction.removeCSSStyleAndExpandAsSeparateAttributes(element);
		}
		elements = SVGUtil.getQuerySVGElements(getSVGPage(), "//*[@style]");
	}

	public static void removeCSSStyleAndExpandAsSeparateAttributes(SVGElement element) {
		if (element.getAttribute(STYLE) != null) {
			StyleBundle bundle = element.getStyleBundle();
			if (bundle != null) {
				setExplicitStyle(element, bundle.getClipPath(), StyleBundle.CLIP_PATH);
				setExplicitStyle(element, bundle.getFill(), StyleBundle.FILL);
				setExplicitStyle(element, bundle.getFontFamily(), StyleBundle.FONT_FAMILY);
				setExplicitStyle(element, bundle.getFontSize(), StyleBundle.FONT_SIZE);
				setExplicitStyle(element, bundle.getFontWeight(), StyleBundle.FONT_WEIGHT);
				setExplicitStyle(element, bundle.getOpacity(), StyleBundle.OPACITY);
				setExplicitStyle(element, bundle.getStroke(), StyleBundle.STROKE);
				setExplicitStyle(element, bundle.getStrokeWidth(), StyleBundle.STROKE_WIDTH);
			}
			element.getAttribute(STYLE).detach();
		}
	}

	private static void setExplicitStyle(SVGElement element, Object value, String styleName) {
		if (value != null && element.getAttribute(styleName) == null) {
			element.addAttribute(new Attribute(styleName, value.toString()));
		}
	}

}
