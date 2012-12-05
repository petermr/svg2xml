package org.xmlcml.svgplus.figure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.action.SVGPlusConstantsX;
import org.xmlcml.svgplus.paths.ComplexLine;
import org.xmlcml.svgplus.paths.ComplexLine.LineOrientation;
import org.xmlcml.svgplus.util.GraphUtil;

public class FigureFragment {

	private static final String OTHER = "other";
	private static final String FRAGMENT_TAG = "fragment";

	private final static Logger LOG = Logger.getLogger(FigureFragment.class);

	private static final String RNA_CHARS = "ACGU";
	private static final String PROTEIN1_CHARS = "ACDEFGHIKLMNPQRSTVWY";
	
	private SVGG g;
	private List<SVGElement> allElements;
	private List<SVGCircle> circleElements;
	private List<SVGImage> imageElements;
	private List<SVGLine> lineElements;
	private List<SVGPath> pathElements;
	private List<SVGPolygon> polygonElements;
	private List<SVGPolyline> polylineElements;
	private List<SVGRect> rectElements;
	private List<SVGText> textElements;
	private List<SVGElement> otherElements;
	private Element primitivesElement;
	
	private static double EPS = 0.01;

	private int spaceCount;
	private int lowerCount;
	private int upperCount;
	private int digitCount;
	private int otherCharCount;
	
	private List<Double> doubleList;
	private List<Integer> integerList;
	private List<String> singleCharList;
	private List<String> singleWordList;
	private List<String> multiWordList;
	private List<SVGLine> zeroLines;
	private List<SVGLine> horizontalLines;
	private List<SVGLine> verticalLines;
	private List<SVGLine> otherLines;

	private Element circleElement;
	private Element imageElement;
	private Element lineElement;
	private Element pathElement;
	private Element polygonElement;
	private Element polylineElement;
	private Element rectElement;
	private Element textElement;

	private String id;

	private List<String> rnaCharList;
	private List<String> protein1CharList;
	private List<String> word2List;
	
	public FigureFragment(SVGG g) {
		this.g = g;
		this.id = g.getId();
	}

	public void analyzePrimitives() {
		extractPrimitives();
		LOG.trace(toString());
		primitivesElement = new Element(FRAGMENT_TAG);
		primitivesElement.addAttribute(new Attribute(SVGPlusConstantsX.ID, id));
		analyzeCircles();
		analyzeImages();
		analyzeLines();
		analyzePaths();
		analyzePolygons();
		analyzePolylines();
		analyzeRects();
		analyzeTexts();
		analyzeOthers();
		LOG.trace("counts "+primitivesElement.toXML());
	}

	public Element getPrimitivesElement() {
		if (primitivesElement == null) {
			analyzePrimitives();
		}
		return primitivesElement;
	}

	private void extractPrimitives() {
		allElements = SVGUtil.getQuerySVGElements(g, "./svg:*");
		circleElements = SVGCircle.extractCircles(SVGUtil.getQuerySVGElements(g, "./svg:circle"));
		imageElements = SVGImage.extractImages(SVGUtil.getQuerySVGElements(g, "./svg:image"));
		lineElements = SVGLine.extractLines(SVGUtil.getQuerySVGElements(g, "./svg:line"));
		pathElements = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(g, "./svg:path"));
		polygonElements = SVGPolygon.extractPolygons(SVGUtil.getQuerySVGElements(g, "./svg:polygon"));
		polylineElements = SVGPolyline.extractPolylines(SVGUtil.getQuerySVGElements(g, "./svg:polyline"));
		rectElements = SVGRect.extractRects(SVGUtil.getQuerySVGElements(g, "./svg:rect"));
		textElements = SVGText.extractTexts(SVGUtil.getQuerySVGElements(g, "./svg:text"));
		otherElements = SVGUtil.getQuerySVGElements(g, "./svg:*[not(" +
			" self::svg:circle   or" +
			" self::svg:image    or" +
			" self::svg:line     or" +
			" self::svg:path     or" +
			" self::svg:polygon  or" +
			" self::svg:polyline or" +
			" self::svg:rect     or" +
			" self::svg:text)]");
	}

	private void analyzeCircles() {
		if (circleElements.size()> 0) {
			circleElement = new Element(SVGCircle.TAG);
			primitivesElement.appendChild(circleElement);
			circleElement.addAttribute(new Attribute("count", ""+circleElements.size()));
		}
	}

	private void analyzeImages() {
		if (imageElements.size()> 0) {
			imageElement = new Element(SVGImage.TAG);
			primitivesElement.appendChild(imageElement);
			imageElement.addAttribute(new Attribute("count", ""+imageElements.size()));
		}
	}

	private void analyzeLines() {
		if (lineElements.size()> 0) {
			lineElement = new Element(SVGLine.TAG);
			primitivesElement.appendChild(lineElement);
			List<SVGLine> copyLines = new ArrayList<SVGLine>();
			copyLines.addAll(lineElements);
			lineElement.addAttribute(new Attribute("count", ""+lineElements.size()));
			zeroLines = ComplexLine.createZeroLengthSubsetAndRemove(lineElements, EPS);
			horizontalLines = ComplexLine.createSubsetAndRemove(lineElements, LineOrientation.HORIZONTAL, EPS);
			verticalLines = ComplexLine.createSubsetAndRemove(lineElements, LineOrientation.VERTICAL, EPS);
			otherLines = lineElements;
			if (zeroLines.size()> 0) {
				lineElement.addAttribute(new Attribute("zeroLines", ""+zeroLines.size()));
			}
			if (horizontalLines.size()> 0) {
				lineElement.addAttribute(new Attribute("horizontalLines", ""+horizontalLines.size()));
			}
			if (verticalLines.size()> 0) {
				lineElement.addAttribute(new Attribute("verticalLines", ""+verticalLines.size()));
			}
			if (otherLines.size()> 0) {
				lineElement.addAttribute(new Attribute("otherLines", ""+otherLines.size()));
			}
			LineUnivariate lineUnivariate = new LineUnivariate(copyLines);
			Double min = lineUnivariate.getMin();
			Double max = lineUnivariate.getMax();
			Double median = lineUnivariate.getMedian();
			Double mean = lineUnivariate.getMean();
			Double sd = lineUnivariate.getStandardDeviation();
			if (min != null) {
				lineElement.addAttribute(new Attribute("min", ""+Util.format(min, 1)));
			}
			if (max != null) {
				lineElement.addAttribute(new Attribute("max", ""+Util.format(max, 1)));
			}
			if (median != null) {
				lineElement.addAttribute(new Attribute("median", ""+Util.format(median, 1)));
			}
			if (mean != null) {
				lineElement.addAttribute(new Attribute("mean", ""+Util.format(mean, 1)));
			}
			if (sd != null) {
				lineElement.addAttribute(new Attribute("sd", ""+Util.format(sd, 1)));
			}
		}
	}

	private void analyzePaths() {
		if (pathElements.size()> 0) {
			pathElement = new Element(SVGPath.TAG);
			primitivesElement.appendChild(pathElement);
			pathElement.addAttribute(new Attribute("paths", ""+pathElements.size()));
		}
	}

	private void analyzePolygons() {
		if (polygonElements.size()> 0) {
			polygonElement = new Element(SVGPolygon.TAG);
			primitivesElement.appendChild(polygonElement);
			polygonElement.addAttribute(new Attribute("polygons", ""+polygonElements.size()));
		}
	}

	private void analyzePolylines() {
		if (polylineElements.size()> 0) {
			polylineElement = new Element(SVGPolyline.TAG);
			primitivesElement.appendChild(polylineElement);
			polylineElement.addAttribute(new Attribute("polylines", ""+polylineElements.size()));
		}
	}

	private void analyzeRects() {
		if (rectElements.size()> 0) {
			rectElement = new Element(SVGRect.TAG);
			primitivesElement.appendChild(rectElement);
			rectElement.addAttribute(new Attribute("rects", ""+rectElements.size()));
		}
	}

	private void analyzeTexts() {
		if (textElements.size()> 0) {
			textElement = new Element(SVGText.TAG);
			primitivesElement.appendChild(textElement);
			textElement.addAttribute(new Attribute("texts", ""+textElements.size()));
			spaceCount = 0;
			lowerCount = 0;
			upperCount = 0;
			digitCount = 0;
			otherCharCount = 0;
			doubleList = new ArrayList<Double>();
			integerList = new ArrayList<Integer>();
			singleCharList = new ArrayList<String>();
			rnaCharList = new ArrayList<String>();
			protein1CharList = new ArrayList<String>();
			singleWordList = new ArrayList<String>();
			word2List = new ArrayList<String>();
			multiWordList = new ArrayList<String>();
			for (SVGText text : textElements) {
				String value = text.getValue().trim();
				Double d = GraphUtil.parseDouble(value);
				if (d != null) {
					doubleList.add(d);
				} else {
					Integer i = GraphUtil.parseInteger(value);
					if (i != null) {
						integerList.add(i);
					} else if (value.length() == 0) {
						spaceCount++;
					} else if (value.length() == 1) {
						char charx = value.charAt(0);
						singleCharList.add(value);
						if (Character.isLowerCase(charx)) {
							lowerCount++;
						} else if (Character.isUpperCase(charx)) {
							upperCount++;
						} else if (Character.isDigit(charx)) {
							digitCount++;
						} else {
							otherCharCount++;
						}
						if (RNA_CHARS.indexOf(value) != -1) {
							rnaCharList.add(value);
						}
						if (PROTEIN1_CHARS.indexOf(value) != -1) {
							protein1CharList.add(value);
						}
					} else if (value.indexOf(CMLConstants.S_SPACE) == -1) {
						singleWordList.add(value);
					} else {
						String[] words = value.split(CMLConstants.S_WHITEREGEX);
						if (words.length == 2) {
							word2List.add(value);
						} else {
							multiWordList.add(value);
						}
					}
				}
			}
			if (doubleList.size() >0) {
				textElement.addAttribute(new Attribute("double", ""+doubleList.size()));
			}
			if (integerList.size() >0) {
				textElement.addAttribute(new Attribute("integer", ""+integerList.size()));
			}
			if (singleCharList.size() >0) {
				textElement.addAttribute(new Attribute("singleChar", ""+singleCharList.size()));
			}
			if (rnaCharList.size() >0) {
				textElement.addAttribute(new Attribute("rnaChar", ""+rnaCharList.size()));
			}
			if (protein1CharList.size() >0) {
				textElement.addAttribute(new Attribute("protein1Char", ""+protein1CharList.size()));
			}
			if (singleWordList.size() >0) {
				textElement.addAttribute(new Attribute("singleWord", ""+singleWordList.size()));
			}
			if (word2List.size() >0) {
				textElement.addAttribute(new Attribute("word2", ""+word2List.size()));
			}
			if (multiWordList.size() >0) {
				textElement.addAttribute(new Attribute("multiWord", ""+multiWordList.size()));
			}
			if (spaceCount >0) {
				textElement.addAttribute(new Attribute("space", ""+spaceCount));
			}
			if (lowerCount >0) {
				textElement.addAttribute(new Attribute("lower", ""+lowerCount));
			}
			if (upperCount >0) {
				textElement.addAttribute(new Attribute("upper", ""+upperCount));
			}
			if (digitCount >0) {
				textElement.addAttribute(new Attribute("digit", ""+digitCount));
			}
			if (otherCharCount >0) {
				textElement.addAttribute(new Attribute("otherChar", ""+otherCharCount));
			}
		}
	}

	private void analyzeOthers() {
		if (otherElements.size()> 0) {
			Set<String> otherElementSet = new HashSet<String>();
			for (SVGElement otherElement : otherElements) {
				otherElementSet.add(otherElement.getClass().getSimpleName());
			}
			primitivesElement.addAttribute(new Attribute(OTHER, ""+otherElementSet.toString()+" "+otherElements.size()));
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("All     : "+allElements.size()+"\n");
		if (circleElements.size()> 0) {
			sb.append("Circle  : "+circleElements.size()+"\n");
		}
		if (imageElements.size()> 0) {
			sb.append("Image  : "+imageElements.size()+"\n");
		}
		if (lineElements.size()> 0) {
			sb.append("Line    : "+lineElements.size()+"\n"); 
		}
		if (pathElements.size()> 0) {
			sb.append("Path    : "+pathElements.size()+"\n");
		}
		if (polygonElements.size()> 0) {
			sb.append("Polygon : "+polygonElements.size()+"\n");
		}
		if (polylineElements.size()> 0) {
			sb.append("Polyline: "+polylineElements.size()+"\n");
		}
		if (rectElements.size()> 0) {
			sb.append("Rect    : "+rectElements.size()+"\n");
		}
		if (textElements.size()> 0) {
			sb.append("Text    : "+textElements.size()+"\n");
		}
		if (otherElements.size()> 0) {
			sb.append("Other   : "+otherElements.size()+"\n");
			for (SVGElement otherElement : otherElements) {
				sb.append(" "+otherElement.getClass().getSimpleName());
			}
		}
		return sb.toString();
	}

}
