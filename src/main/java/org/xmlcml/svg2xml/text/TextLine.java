package org.xmlcml.svg2xml.text;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.html.HtmlSub;
import org.xmlcml.html.HtmlSup;
import org.xmlcml.pdf2svg.util.PDF2SVGUtil;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;
import org.xmlcml.svg2xml.old.SimpleFontOld;
import org.xmlcml.svg2xml.old.WordSequence;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** holds a list of characters, normally in a horizontal line
 * 
 * exclusively used by TextAnalyzer
 * 
 * @author pm286
 *
 */
public class TextLine implements Iterable<SVGText> {

	private static final String SERIF = "Serif";
	private final static Logger LOG = Logger.getLogger(TextLine.class);
	private final static PrintStream SYSOUT = System.out;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final double ITALIC = -0.18;
	private static final double BOLD = 0.25;
	private static final double EPS = 0.05;
	private static final double COORD_EPS = 0.0001;
	private static final SVGText SUP = new SVGText(new Real2(0., 0.), "SUP");
	private static final SVGText SUB = new SVGText(new Real2(0., 0.), "SUB");
	private static final Double FONT_Y_FACTOR = 0.5;
	
	private List<SVGText> characterList;
	private Double yCoord = null;
	private List<Double> yCoordList = null;
	private Double fontSize = null;
	private Set<SvgPlusCoordinate> fontSizeContainerSet = null;
	private String physicalStyle;
	private List<String> physicalStyleList;
	
	private String lineContent = null;
	private List<TextLine> subLines;
	private WordSequence wordSequence;
	private TextAnalyzerX textAnalyzerX;
	private SimpleFontOld simpleFont;
	private RealArray characterWidthArray;
	private final static Double SCALE = 0.001; // width multiplied by 1000
	private Double SPACE_WIDTH1000 = /*274.0*/ 200.;
	public final static Double DEFAULT_SPACE_FACTOR = 0.05;
	private Real2Range boundingBox = null;
	private Double meanFontSize;
	private RealArray fontSizeArray;
	private RealArray characterSeparationArray;
	private RealArray spaceWidthArray;
	private RealArray svgCharacterWidthArray;
	private RealArray excessWidthArray;
	private double spaceFactor = DEFAULT_SPACE_FACTOR;
	private Set<SvgPlusCoordinate> fontSizeSet;
	private Suscript suscript;
	private Set<String> fontFamilySet;
	private Multiset<String> fontFamilyMultiset;
	private Multiset<Double> fontSizeMultiset;

	private void resetWhenLineContentChanged() {
		characterList = null;
		yCoord = null;
		yCoordList = null;
		fontSize = null;
		fontSizeContainerSet = null;
		physicalStyle = null;
		physicalStyleList = null;
		boundingBox = null;
		meanFontSize =null;
		fontSizeArray = null;
		
		lineContent = null;
//		lineContentIncludingSpaces = null;
		subLines = null;
		wordSequence = null;
//		integerY = null;
		characterWidthArray = null;
	}
	
	public TextLine(TextAnalyzerX textAnalyzerX, List<SVGText> characterList) {
		this.characterList = characterList;
		this.textAnalyzerX = textAnalyzerX;
	}
	
	public TextLine(TextAnalyzerX textAnalyzerX) {
		this(textAnalyzerX, new ArrayList<SVGText>());
	}
	
	public TextLine(Collection<SVGText> texts, TextAnalyzerX textAnalyzer) {
		this.textAnalyzerX = textAnalyzer;
		characterList = new ArrayList<SVGText>();
		for (SVGText text : texts) {
			characterList.add(text);
		}
	}

	/**
	 * 
	 */
	public TextLine() {
	}

	/**
	 * split the list into smaller lists whenever there is a change
	 * in fontSize, physicalStyle or yCoord. This is heuristic and may need finer tuning
	 * 
	 * @return
	 */
	private List<TextLine> splitLineByCharacterAttributes() {
		if (subLines == null) {
			subLines = new ArrayList<TextLine>();
			Double lastFontSize = null;
			TextLine charList = null;
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				Double fontSize = text.getFontSize();
				LOG.trace("fontSize "+fontSize);
				if (i == 0 
//						|| LineAttributesHaveChanged(lastFontSize, lastYCoord, lastPhysicalStyle, fontSize, yCoord, physicalStyle)
					) {
					charList = new TextLine(this.textAnalyzerX);
					getSubLines().add(charList);
				}
				charList.add(text);
				lastFontSize = fontSize;
			}
			if (getSubLines().size() != 1) {
				for (TextLine chList : getSubLines()) {
					chList.normalizeAndCreateWords();
				}
			}
			if (lastFontSize != null) {
				fontSize = lastFontSize;
			}
		}
		return getSubLines();
	}

	private void normalizeAndCreateWords() {
		normalize();
	}
	
	private void normalize() {
		this.getFontSize();
		this.getYCoord();
		this.getLineContent();
		LOG.trace("words "+((wordSequence == null) ? "null" :  wordSequence.size()));
	}

	
	/** returns the common value of fontSize or null
	 * if there is any variation
	 * 
	 */
	public Double getFontSize() {
		Double fs = null;
		fontSizeContainerSet = getFontSizeContainerSet();
		for (SvgPlusCoordinate fontSize : fontSizeContainerSet) {
			LOG.trace("FSZ "+fontSize);
		}
		if (fontSizeContainerSet != null) {
			if (fontSizeContainerSet.size() == 1) {
				fs = fontSizeContainerSet.iterator().next().getDouble();
			}
		}
		return fs;
	}

	/** returns the common value of fontFamily
	 * if there is any variation
	 * 
	 */
	public String getFontFamily() {
		String family = null;
		getFontFamilySet();
		if (fontFamilySet != null) {
			if (fontFamilySet.size() == 1) {
				family = fontFamilySet.iterator().next();
			} else {
				LOG.trace("FF"+fontFamilySet);
			}
		}
		return family;
	}
			
	public Multiset<String> getFontFamilyMultiset() {
		if (fontFamilyMultiset == null) {
			fontFamilyMultiset = HashMultiset.create();
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				String family = text.getFontFamily();
				fontFamilyMultiset.add(family);
			}
		}
		return fontFamilyMultiset;
	}
	
	private Set<String> getFontFamilySet() {
		if (fontFamilySet == null) {
			fontFamilySet = new HashSet<String>();
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				String family = text.getFontFamily();
				fontFamilySet.add(family);
			}
		}
		return fontFamilySet;
	}
	
	public Set<SvgPlusCoordinate> getFontSizeContainerSet() {
		if (fontSizeContainerSet == null) {
			fontSizeContainerSet = new HashSet<SvgPlusCoordinate>();
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				SvgPlusCoordinate fontSize = new SvgPlusCoordinate(text.getFontSize());
				fontSizeContainerSet.add(fontSize);
			}
		}
		LOG.trace("FSSET "+fontSizeContainerSet);
		return fontSizeContainerSet;
	}
	
	/** returns the common value of yCoord or null
	 * if there is any variation
	 */
	public Double getYCoord() {
		if (yCoord == null) {
			getYCoordList();
			for (Double y : yCoordList) {
				if (y == null || (yCoord != null && !Real.isEqual(y, yCoord, COORD_EPS))) {
					yCoord = null;
					break;
				}
				yCoord = y;
			}
		}
		return yCoord;
	}
		
	private List<Double> getYCoordList() {
		if (yCoordList == null) {
			Double lastYCoord = null;
			yCoordList = new ArrayList<Double>();
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				Double yCoord = text.getY();
				if (yCoord == null) {
					throw new RuntimeException("text has no Y coord");
				} else if (lastYCoord == null) {
					yCoordList.add(yCoord);
				}else if (!Real.isEqual(yCoord, lastYCoord, EPS)) {
					yCoordList.add(yCoord);
				}
				lastYCoord = yCoord;
			}
		}
		return yCoordList;
	}

	public List<SVGText> getCharacterList() {
		return characterList;
	}

	public SVGText get(int i) {
		return characterList.get(i);
	}

	public void add(SVGText svgText) {
		if (svgText != null) {
			yCoord = (yCoord == null) ? svgText.getY() : yCoord;
			ensureCharacterList();
			characterList.add(svgText);
		}
	}

	
	private void ensureCharacterList() {
		if (characterList == null) {
			characterList = new ArrayList<SVGText>();
		}
	}

	public int size() {
		return characterList.size();
	}

	public Iterator<SVGText> iterator() {
		return characterList.iterator();
	}
	
	public List<SVGText> getSVGTextCharacters() {
		return characterList;
	}

	public void sortLineByX() {
		// assumes no coincident text??
		Map<Integer, SVGText> lineByXCoordMap = new HashMap<Integer, SVGText>();
		for (SVGText text : this) {
			lineByXCoordMap.put((int) Math.round(SVGUtil.getTransformedXY(text).getX()), text);
		}
		Set<Integer> xCoords = lineByXCoordMap.keySet();
		Integer[] xArray = xCoords.toArray(new Integer[xCoords.size()]);
		Arrays.sort(xArray);
		List<SVGText> newCharacterList = new ArrayList<SVGText>();
		for (int x : xArray) {
			newCharacterList.add(lineByXCoordMap.get(x));
		}
		this.characterList = newCharacterList;
		getFontSize();
		getYCoord();
//		getSinglePhysicalStyle();
		getLineContent();
		splitLineByCharacterAttributes();
	}
	
	/** 
	 * @return
	 */
	String getLineContent() {
		if (lineContent == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				String ch = text.getText();
				sb.append(ch);
			}
			lineContent = sb.toString();
		}
		LOG.trace("lineContent: "+lineContent);
		return lineContent;
	}

	public boolean isBold() {
		for (SVGText character : characterList) {
			if (!character.isBold()) return false;
		}
		return true;
	}

	public String toString() {
		String s;
		if (getSubLines() != null && getSubLines().size() > 1) {
			s = "split: \n";
			for (TextLine splitList : getSubLines()) {
				s += "   "+splitList+"\n";
			}
		} else {
			
			s = "chars: "+characterList.size() +
				" Y: "+yCoord+
				" fontSize: "+fontSize+
				" physicalStyle: "+physicalStyle+
				" >>"+getLineContent();
		}
		return s;
	}

	private List<TextLine> getSubLines() {
		return subLines;
	}

	public String getLineString() {
		StringBuilder sb = new StringBuilder();
		for (SVGText text : characterList) {
			sb.append(text.getText());
		}
		return sb.toString();
	}
	
	/** uses space factor (default .3 at present)
	 */
	public void insertSpaces() {
		insertSpaces(spaceFactor);
	}
	
	/** computes inter-char gaps. If >= computed width of space adds ONE space
	 * later routines can calculate exact number of spaces if wished
	 * this is essentially a word break detector and marker
	 */
	public void insertSpaces(double sFactor) {
		if (characterList.size() > 0) {
			List<SVGText> newCharacters = new ArrayList<SVGText>();
			SVGText lastText = characterList.get(0);
			newCharacters.add(lastText);
			Double fontSize = lastText.getFontSize();
			Double lastWidth = getWidth(lastText);
			Double lastX = lastText.getX();
			for (int i = 1; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				double x = text.getX();
				double separation = x -lastX;
				double extraWidth = separation - lastWidth;
			    if (extraWidth > sFactor * fontSize) {
			    	addSpaceCharacter(newCharacters, lastX + lastWidth, lastText);
			    }
			    newCharacters.add(text);
				lastWidth = getWidth(text);
				lastX = x;
			}
			resetWhenLineContentChanged();
			characterList = newCharacters;
		}
	}

	public Double getMeanFontSize() {
		if (meanFontSize == null) {
			getFontSizeArray();
			meanFontSize = fontSizeArray.getMean();
		}
		return meanFontSize;
	}

	private RealArray getFontSizeArray() {
		if (fontSizeArray == null || characterList != null || characterList.size() == 0) {
			fontSizeArray = new RealArray(characterList.size());
			for (int i = 0; i < characterList.size(); i++) {
				fontSizeArray.setElementAt(i, characterList.get(i).getFontSize());
			}
		}
		return fontSizeArray;
	}

	public Real2Range getBoundingBox() {{
		if (boundingBox == null) 
			if (characterList != null && characterList.size() > 0) {
				double xmin = characterList.get(0).getBoundingBox().getXRange().getMin();
				double xmax = characterList.get(characterList.size()-1).getBoundingBox().getXRange().getMax();
				RealRange xRange = new RealRange(xmin, xmax); 
				double ymin = characterList.get(0).getBoundingBox().getYRange().getMin();
				double ymax = characterList.get(characterList.size()-1).getBoundingBox().getYRange().getMax();
				RealRange yRange = new RealRange(ymin, ymax); 
				boundingBox = new Real2Range(xRange, yRange);
			}
		}
		return boundingBox;
	}

	/** Array of width from SVGText @svgx:width attribute
	 * @return array of widths
	 */
	private RealArray getSVGCharacterWidthArray() {
		if (svgCharacterWidthArray == null) { 
			svgCharacterWidthArray = new RealArray(characterList.size());
			for (int i = 0; i < characterList.size() ; i++) {
				Double width = getWidth(characterList.get(i));
				svgCharacterWidthArray.setElementAt(i,  width);
			}
			svgCharacterWidthArray.format(TextAnalyzerX.NDEC_FONTSIZE);
		}
		return svgCharacterWidthArray;
	}
	

	/** actual separation of characters by delta X of coordinates
	 * Last character cannot have separation, so array length is characterList.size()-1
	 * @return array of separations
	 */
	private RealArray getCharacterSeparationArray() {
		if (characterSeparationArray == null) { 
			characterSeparationArray = new RealArray(characterList.size() - 1);
			Double x = characterList.get(0).getX();
			for (int i = 0; i < characterList.size() - 1; i++) {
				Double nextX = characterList.get(i + 1).getX();
				Double separation = nextX - x;
				characterSeparationArray.setElementAt(i, separation);
				x = nextX;
			}
			characterSeparationArray.format(TextAnalyzerX.NDEC_FONTSIZE);
		}
		return characterSeparationArray;
	}
	
	/** actual separation of space characters by delta X of coordinates
	 * array is in order of space characters but normally shorter
	 * and does not directly map onto characters
	 * we may provide a mapping index
	 * initially used for stats on space sizes
	 * @return array of separations
	 */
	private RealArray getActualWidthsOfSpaceCharacters() {
		getCharacterSeparationArray();
		if (characterSeparationArray != null) { 
			spaceWidthArray = new RealArray();
			for (int i = 0; i < characterList.size() - 1; i++) {
				SVGText charx = characterList.get(i);
				String text = charx.getText();
				if (CMLConstants.S_SPACE.equals(charx.getText())) {
					spaceWidthArray.addElement(characterSeparationArray.elementAt(i));
				}
			}
		}
		return spaceWidthArray;
	}


	/**
	 * @param newCharacters
	 * @param spaceX
	 * @param templateText to copy attributes from
	 */
	private void addSpaceCharacter(List<SVGText> newCharacters, double spaceX, SVGText templateText) {
		SVGText spaceText = new SVGText();
		CMLUtil.copyAttributes(templateText, spaceText);
		spaceText.setText(" ");
		spaceText.setX(spaceX);
		PDF2SVGUtil.setSVGXAttribute(spaceText, PDF2SVGUtil.CHARACTER_WIDTH, ""+SPACE_WIDTH1000);
		newCharacters.add(spaceText);
	}

	public static Double getWidth(SVGText text) {
		String widthS = PDF2SVGUtil.getSVGXAttribute(text, PDF2SVGUtil.CHARACTER_WIDTH);
		Double fontSize = text.getFontSize();
		Double width = widthS == null ? null : new Double(widthS) * SCALE;
		return width == null ? null : width * fontSize;
	}

	public Double getMeanWidthOfSpaceCharacters() {
		RealArray spaceWidths = getActualWidthsOfSpaceCharacters();
		return spaceWidths == null ? null : spaceWidths.getMean();
	}

	public Set<SvgPlusCoordinate> getFontSizeSet() {
		if (fontSizeSet == null) {
			fontSizeSet = new HashSet<SvgPlusCoordinate>();
			for (SVGText text : characterList) {
				double fontSize = text.getFontSize();
				fontSizeSet.add(new SvgPlusCoordinate(fontSize));
			}
		}
		return fontSizeSet;
	}

	public TextLine getSuperscript() {
		Double fontSize = this.getFontSize();
		TextLine superscript = null;
		Integer ii = textAnalyzerX.getSerialNumber(this);
		if (ii != null && ii > 0) {
			Double thisY = this.getYCoord();
			TextLine previousLine = textAnalyzerX.getLinesInIncreasingY().get(ii-1);
			Double previousY = previousLine.getYCoord();
			if (previousY != null && thisY != null) {
				if (thisY - previousY < fontSize * FONT_Y_FACTOR) {
					superscript = previousLine;
				}
			}
		}
		return superscript;
	}

	public TextLine getSubscript() {
		Double fontSize = this.getFontSize();
		TextLine subscript = null;
		Integer ii = textAnalyzerX.getSerialNumber(this);
		if (ii != null && ii < textAnalyzerX.getLinesInIncreasingY().size()-1) {
			Double thisY = this.getYCoord();
			TextLine nextLine = textAnalyzerX.getLinesInIncreasingY().get(ii+1);
			Double nextY = nextLine.getYCoord();
			if (nextY != null && thisY != null) {
				if (nextY - thisY < fontSize * FONT_Y_FACTOR) {
					subscript = nextLine;
				}
			}
		}
		return subscript;
	}

	/** mainly debug
	 * 
	 * @return
	 */
	public List<SVGText> createSuscriptString() {
		List<SVGText> textList = new ArrayList<SVGText>();
		Integer thisIndex = 0;
		TextLine superscript = this.getSuperscript();
		List<SVGText> superChars = (superscript == null) ? new ArrayList<SVGText>() : superscript.characterList;
		Integer superIndex = 0;
		TextLine subscript = this.getSubscript();
		List<SVGText> subChars = (subscript == null) ? new ArrayList<SVGText>() : subscript.characterList;
		Integer subIndex = 0;
		while (true) {
			SVGText nextSup = peekNext(superChars, superIndex);
			SVGText nextThis = peekNext(characterList, thisIndex);
			SVGText nextSub = peekNext(subChars, subIndex);
			SVGText nextText = textWithLowestX(nextSup, nextThis, nextSub);
			if (nextText == null) {
				break;
			}
			SVGText mark = null;
			if (nextText.equals(nextSup)) {
				superIndex++;
				mark = (SVGText) SUP.copy();
			} else if (nextText.equals(nextThis)) {
				thisIndex++;
			} else if (nextText.equals(nextSub)) {
				subIndex++;
				mark = (SVGText) SUB.copy();
			}
			if (mark != null) {
				mark.setXY(nextText.getXY());
				textList.add(mark);
			}
			textList.add(nextText);
		}
		return textList;
	}

	/** mainly debug
	 * 
	 * @return
	 */
	public List<TextLine> createSuscriptTextLineList() {
		List<TextLine> textLineList = new ArrayList<TextLine>();
		Integer thisIndex = 0;
		TextLine superscript = this.getSuperscript();
		List<SVGText> superChars = (superscript == null) ? new ArrayList<SVGText>() : superscript.characterList;
		Integer superIndex = 0;
		TextLine subscript = this.getSubscript();
		List<SVGText> subChars = (subscript == null) ? new ArrayList<SVGText>() : subscript.characterList;
		Integer subIndex = 0;
		TextLine textLine = null;
		while (true) {
			SVGText nextSup = peekNext(superChars, superIndex);
			SVGText nextThis = peekNext(characterList, thisIndex);
			SVGText nextSub = peekNext(subChars, subIndex);
			SVGText nextText = textWithLowestX(nextSup, nextThis, nextSub);
			if (nextText == null) {
				break;
			}
			Suscript suscript = Suscript.NONE;
			if (nextText.equals(nextSup)) {
				superIndex++;
				suscript = Suscript.SUP;
			} else if (nextText.equals(nextThis)) {
				thisIndex++;
				suscript = Suscript.NONE;
			} else if (nextText.equals(nextSub)) {
				subIndex++;
				suscript = Suscript.SUB;
			}
			if (textLine == null || !(suscript.equals(textLine.getSuscript()))) {
				textLine = new TextLine(textAnalyzerX);
				textLine.setSuscript(suscript);
				textLineList.add(textLine);
			}
			textLine.add(nextText);
		}
		for (TextLine tLine : textLineList) {
			tLine.insertSpaces();
		}
		return textLineList;
	}
	
//	public HtmlElement createHtmlLine() {
//		List<TextLine> textLineList = createSuscriptTextLineList();
//		return createHtmlElement(textLineList);
//	}
//
	public static HtmlElement createHtmlElement(List<TextLine> textLineList) {
		HtmlP p = new HtmlP();
		for (TextLine textLine : textLineList) {
			HtmlElement pp = textLine.getHtmlElement();
			if (pp instanceof HtmlSpan) {
				SVG2XMLUtil.moveChildrenFromTo(pp, p);
			} else {
				p.appendChild(HtmlElement.create(pp));
			}
		}
		return p;
	}
//

	private HtmlElement getHtmlElement() {
		HtmlElement htmlElement = null;
		Suscript suscript = this.getSuscript();
		if (suscript == null || suscript.equals(Suscript.NONE)) {
			htmlElement = new HtmlSpan();
		} else if (suscript.equals(Suscript.SUB)) {
			htmlElement = new HtmlSub();
		} else if (suscript.equals(Suscript.SUP)) {
			htmlElement = new HtmlSup();
		}
		// this may create one or more span children as we encounter fonts and styles
		// even sub/super may have different styles within them
		addCharacters(htmlElement);
		LOG.trace("Html Element: "+htmlElement.toXML());
		return htmlElement;
	}

	private void addCharacters(HtmlElement htmlElement) {
		String currentFontFamily = null;
		String currentFontStyle = null;
		String currentFontWeight = null;
		String currentColor = null;
		Double currentFontSize = null;
		HtmlSpan span = null;
		StringBuffer sb = null;
		for (SVGText character : characterList) {
			String fontFamily = character.getFontFamily();
			String fontStyle = character.getFontStyle();
			String fontWeight = character.getFontWeight();
			String color = character.getFill();
			Double fontSize = character.getFontSize();
			if (!equals(currentFontSize, fontSize, 0.01) ||
				!equals(currentColor, color) ||
				!equals(currentFontStyle, fontStyle) ||
				!equals(currentFontWeight, fontWeight) ||
				!equals(currentFontFamily, fontFamily)
				) {
				if (span != null) {
					span.setValue(sb.toString());
				}
				span = new HtmlSpan();
				StringBuffer sbatt = new StringBuffer();
				addStyle(sbatt, "font-size", fontSize);
				addStyle(sbatt, "color", color);
				addStyle(sbatt, "font-style", fontStyle);
				addStyle(sbatt, "font-weight", fontWeight);
				addStyle(sbatt, "font-family", fontFamily);
				span.addAttribute(new Attribute("style", sbatt.toString()));
				htmlElement.appendChild(span);
				sb = new StringBuffer();
				currentFontFamily = fontFamily;
				currentFontStyle = fontStyle;
				currentFontWeight = fontWeight;
				currentColor = color;
				currentFontSize = fontSize;
			}
			sb.append(character.getValue());
		}
		if (span != null) {
			span.setValue(sb.toString());
		}
	}

	private static void addStyle(StringBuffer sbatt, String attName, String value) {
		if (value != null) {
			sbatt.append(attName+":"+value+";");
		}
	}

	private static void addStyle(StringBuffer sbatt, String attName, Double value) {
		if (value != null) {
			sbatt.append(attName+":"+value+"px;");
		}
	}

	private boolean equals(String s1, String s2) {
		return (s1 == null && s2 == null) ||
				(s1 != null && s2 != null && s1.equals(s2));
	}

	private boolean equals(Double s1, Double s2, double eps) {
		return (s1 == null && s2 == null) ||
				(s1 != null && s2 != null && Real.isEqual(s1, s2, eps));
	}

	public Suscript getSuscript() {
		return suscript;
	}

	void setSuscript(Suscript suscript) {
		this.suscript = suscript;
	}

	public static SVGText textWithLowestX(SVGText nextSup, SVGText nextThis, SVGText nextSub) {
		SVGText lowestText = null;
		if (nextSup != null && (lowestText == null || (lowestText.getX() > nextSup.getX()))) {
			lowestText = nextSup;
		}
		if (nextThis != null && (lowestText == null || (lowestText.getX() > nextThis.getX()))) {
			lowestText = nextThis;
		}
		if (nextSub != null && (lowestText == null || (lowestText.getX() > nextSub.getX()))) {
			lowestText = nextSub;
		}
		return lowestText;
	}

	public static SVGText peekNext(List<SVGText> characterList, Integer index) {
		SVGText text = null;
		if (characterList != null) {
			text = (index >= characterList.size()) ? null : characterList.get(index);
		}
		return text;
	}

	public String getSpacedLineString() {
		StringBuilder sb = new StringBuilder();
		if (characterList != null) {
			for (SVGText text : characterList) {
				String s = text.getValue();
				if (s.trim().length() == 0) {
					s = " ";
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public Double getFirstXCoordinate() {
		getBoundingBox();
		RealRange xRange = boundingBox == null ? null : boundingBox.getXRange();
		return xRange == null ? null : xRange.getMin();
	}

	public Double getLastXCoordinate() {
		getBoundingBox();
		RealRange xRange = boundingBox == null ? null : boundingBox.getXRange();
		return xRange == null ? null : xRange.getMax();
	}

	public String getRawValue() {
		StringBuilder sb = new StringBuilder();
		if (characterList != null) {
			for (SVGText text : characterList) {
				sb.append(text.getValue());
			}
		}
		return sb.toString();
	}

	/** merges two lines (at present only characters from second one)
	 * Used when two lines have same Y-coord but have been split into two parts
	 * 
	 * mean and communal properties are probably rubbish (maybe should be null'ed?)
	 * @param textLine
	 */
	public void merge(TextLine textLine) {
		for (SVGText character : textLine.characterList) {
			this.characterList.add(character);
		}
	}

	public Double getCommonestFontSize() {
		getFontSizeMultiset();
		Set<Entry<Double>> entrySet = fontSizeMultiset.entrySet();
		Double commonestSize = null;
		Integer commonestCount = null;
		for (Entry<Double> entry : entrySet) {
			Double size = entry.getElement();
			Integer count = entry.getCount();
			if (commonestSize == null) {
				commonestSize = size;
				commonestCount = count;
			} else {
				if (count > commonestCount) {
					commonestSize = size;
					commonestCount = count;
				}
			}
		}
		return commonestSize;
	}
	
	private Multiset<Double> getFontSizeMultiset() {
		if (fontSizeMultiset == null) {
			fontSizeMultiset = HashMultiset.create();
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				Double size = text.getFontSize();
				fontSizeMultiset.add(size);
			}
		}
		return fontSizeMultiset;
	}
		
}
