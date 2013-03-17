package org.xmlcml.svg2xml.text;

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
import nu.xom.Elements;

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
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final double ITALIC = -0.18;
	private static final double BOLD = 0.25;
	private static final String DEFAULT_CHAR = "s";
	private static final double EPS = 0.05;
	private static final double COORD_EPS = 0.0001;
	private static final double FONT_EPS = 0.001;
	private static final double SPACE_FUDGE = 0.8;
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
	private String lineContentIncludingSpaces = null;
	private List<TextLine> subLines;
	private WordSequence wordSequence;
	private TextAnalyzerX textAnalyzerX;
	private SimpleFont simpleFont;
	private Integer integerY;
	private RealArray characterWidthArray;
	private Set<FontStyle> fontStyleSet;
	private Double SCALE = 0.001;
	private Double SPACE_WIDTH1000 = /*274.0*/ 200.;
	private Double SPACE_WIDTH = SPACE_WIDTH1000 * SCALE;
	private Double DEFAULT_SPACE_FACTOR = 0.12;
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
		lineContentIncludingSpaces = null;
		subLines = null;
		wordSequence = null;
		integerY = null;
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
	 * split the list into smaller lists whenever there is a change
	 * in fontSize, physicalStyle or yCoord. This is heuristic and may need finer tuning
	 * 
	 * @return
	 */
	private List<TextLine> splitLineByCharacterAttributes() {
		if (subLines == null) {
			subLines = new ArrayList<TextLine>();
			Double lastFontSize = null;
			Double lastYCoord = null;
			String lastPhysicalStyle = null;
			TextLine charList = null;
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				Double fontSize = text.getFontSize();
				LOG.trace("fontSize "+fontSize);
				Double yCoord = text.getY();
//				String physicalStyle = getPhysicalStyle(text);
				if (i == 0 
//						|| LineAttributesHaveChanged(lastFontSize, lastYCoord, lastPhysicalStyle, fontSize, yCoord, physicalStyle)
					) {
					charList = new TextLine(this.textAnalyzerX);
					getSubLines().add(charList);
				}
				charList.add(text);
				lastFontSize = fontSize;
				lastYCoord = yCoord;
//				lastPhysicalStyle = physicalStyle;
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
		createWords();
	}
	private void normalize() {
		this.getFontSize();
		this.getYCoord();
		this.getSinglePhysicalStyle();
		this.getLineContent();
		LOG.trace("words "+((wordSequence == null) ? "null" :  wordSequence.size()));
	}

	private boolean LineAttributesHaveChanged(Double lastFontSize, Double lastYCoord,
			String lastPhysicalStyle, Double fontSize, Double yCoord, String physicalStyle) {
		return (fontSize == null || (lastFontSize != null && !Real.isEqual(fontSize, lastFontSize, FONT_EPS))) ||
			(yCoord == null || (lastYCoord != null && !Real.isEqual(yCoord, lastYCoord, COORD_EPS))) ||
			(physicalStyle == null || (lastPhysicalStyle != null && !physicalStyle.equals(lastPhysicalStyle)));
	}
	
	
	/** returns the common value of physicalStyle or null
	 * if there is any variation
	 * @return
	 */
	private String getSinglePhysicalStyle() {
		if (physicalStyle == null && physicalStyleList != null) {
			for (String pstyle : physicalStyleList) {
				if (pstyle == null || (physicalStyle != null && !pstyle.equals(physicalStyle))) {
					physicalStyle = null;
					break;
				}
				physicalStyle = pstyle;
			}
		}
		return physicalStyle;
	}
		
	/** returns the common value of fontSize or null
	 * if there is any variation
	 * 
	 */
	public Double getFontSize() {
		Double fs = null;
		getFontSizeContainerSet();
		if (fontSizeContainerSet != null) {
			if (fontSizeContainerSet.size() == 1) {
				fs = fontSizeContainerSet.iterator().next().getDouble();
			}
		}
		return fs;
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

	public RealArray getCharacterWidthArray() {
		if (characterWidthArray == null) {
			characterWidthArray = new RealArray();
			for (int i = 1; i < characterList.size(); i++) {
				SVGText text0 = characterList.get(i-1);
				SVGText text = characterList.get(i);
				Double x0 = SVGUtil.getTransformedXY(text0).getX();
				Double x = SVGUtil.getTransformedXY(text).getX();
				Double width = x - x0;
				characterWidthArray.addElement(width);
			}
		}
		return characterWidthArray;
	}
	
	public /* for test */ Map<String, RealArray> getInterCharacterWidthsByCharacterNormalizedByFont() {
		Map<String, RealArray> widthByText = new HashMap<String, RealArray>();
		RealArray widthArray = getCharacterWidthArray();
		Double fontSize = this.getFontSize();
		for (int i = 0; i < characterList.size()-1; i++) {
			SVGText text = characterList.get(i);
			Double width = widthArray.get(i) / fontSize;
			String charr = text.getText();
			RealArray widthList = widthByText.get(charr);
			if (widthList == null) {
				widthList = new RealArray();
				widthByText.put(charr, widthList);
			}
			widthList.addElement(width);
		}
		return widthByText;
	}
	
	public List<SVGText> getCharacterList() {
		return characterList;
	}

	public SVGText get(int i) {
		return characterList.get(i);
	}

	public void add(SVGText svgText) {
		yCoord = (yCoord == null) ? svgText.getY() : yCoord;
		characterList.add(svgText);
	}

	
	public WordSequence getWordSequence() {
		return wordSequence;
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
		getSinglePhysicalStyle();
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

	
	/** add spaces according to our simple font metrics since we often don't
	 * have any better ones
	 * 
	 * @param simpleFont
	 * @return
	 */
	public /* for test */String guessAndApplySpacingInLine() {
		if (lineContentIncludingSpaces == null) {
			StringBuilder sb = new StringBuilder();
			ensureSimpleFont();
			LOG.trace("SF "+simpleFont);
			RealArray realArray = getCharacterWidthArray();
			if (realArray != null) {
				for (int i = 0; i < characterList.size(); i++) {
					SVGText text = characterList.get(i);
					String ch = text.getText();
					LOG.trace("CH "+ch+ " "+simpleFont);
					SimpleCharacter simpleCharacter = simpleFont.getSimpleCharacter(ch);
					if (simpleCharacter == null) {
						simpleCharacter = simpleFont.getSimpleCharacter(DEFAULT_CHAR);
					}
					sb.append(ch);
					Double width = simpleCharacter.getWidth();
					if (width == null) {
						width = simpleFont.getSimpleCharacter(DEFAULT_CHAR).getWidth();
					}
					Double delta = realArray.get(i) - width;
					if (delta > simpleFont.guessMinimumInterTextSpacing(fontSize)) {
						sb.append("     ");
					} else if (delta > simpleFont.guessMinimumSpaceSize(fontSize)) {
						sb.append(" ");
					}
				}
			}
			lineContentIncludingSpaces = sb.toString();
		}
		LOG.trace("LINE: "+lineContentIncludingSpaces);
		return lineContentIncludingSpaces;
	}
	
	/** create a single string from the characters
	 * may not be a good idea (try words instead)
	 * @return
	 */
	public /*for test */ SVGText createString() {
		Double lastX = null;
		Double lastFontSize = null;
		Double spaceFactor = 0.7;
		StringBuilder sb = new StringBuilder();
		for (SVGText text : this) {
			Double fontSize = text.getFontSize();
			fontSize = SVGUtil.getTransformedXY(text, new Real2(0, fontSize)).getY();
			if (fontSize == null || Double.isNaN(fontSize)) {
				throw new RuntimeException("font size null/NaN");
			}
			if (lastFontSize != null && !Real.isEqual(lastFontSize, fontSize, 0.01)) {
				System.err.println("fontsize changed "+lastFontSize+" -> "+fontSize);
				lastFontSize = fontSize;
			}
			Double x = SVGUtil.getTransformedXY(text).getX();
			if (lastX != null && x-lastX > spaceFactor * fontSize) {
				sb.append(" ");
			}
			lastX = x;
			sb.append(text.getText());
		}
		String textString = sb.toString();
		SVGText line = new SVGText(get(0));
		line.setText(textString);
		return line;
	}

	/** create words from the line
	 * 
	 * @return
	 */
	public WordSequence createWords() {
		if (wordSequence == null) {
			ensureSimpleFont();
			Real2 xy = (this.size() > 0) ? this.get(0).getXY() : null;
			wordSequence = new WordSequence();
//			TextAnalyzer.
			Map<String, Double> widths = simpleFont.getWidthsByCharacter(SERIF);
			if (widths == null) {
				throw new RuntimeException("cannot create widths");
			}
			Double lastX = null;
			Double spaceFactor = 1.4;
			List<SVGText> chars = null;
			List<Word> words = new ArrayList<Word>();
			Real2 origin = null;
			String lastChar = null;
			Double characterSeparation = null;
			Double sumdeltax = 0.0;
			for (SVGText text : this) {
				fontSize = fontSize == null ? text.getFontSize() : fontSize;
				if (fontSize == null) {
					throw new RuntimeException("missing fontSize: "+text.getText());
				}
				String ch = text.getText();
				LOG.trace("CH "+ch+" "+text.getXY());
				if (origin == null) {
					origin = text.getXY();
					sumdeltax = 0.0;
					chars = new ArrayList<SVGText>();
				}
				Double x = text.getX();
				if (lastChar != null) {
					Double estimatedLastWidth = null;
					Double width = widths.get(lastChar);
					if (width == null) {
						LOG.trace("char not in font "+lastChar);
						width = widths.get("e");
					}
					LOG.trace("FS "+fontSize);
					estimatedLastWidth = width * fontSize * spaceFactor;
					characterSeparation = x - lastX;
					if (characterSeparation / estimatedLastWidth > 1.0) {
						sumdeltax += estimatedLastWidth;
						Word word = createWord(chars, sumdeltax);
						words.add(word);
						origin = text.getXY();
						chars = new ArrayList<SVGText>();
						chars.add(text);
						sumdeltax = 0.0;
						lastX = x;
						lastChar = ch;
						continue;
					}
					sumdeltax += characterSeparation;
				}
				chars.add(text);
				lastX = x;
				lastChar = ch;
			}
			if (chars.size() > 0) {
				Word word = createWord(chars, sumdeltax);
				words.add(word);
			}
			wordSequence = new WordSequence(words);
			wordSequence.setXY(xy);
		}
		LOG.trace(wordSequence.toXML()+wordSequence.getStringValue());
		return wordSequence;
	}

	private void ensureSimpleFont() {
		if (simpleFont == null) {
			simpleFont = textAnalyzerX.ensureSimpleFont();
		}
	}

	private Word createWord(List<SVGText> chars, Double sumdeltax) {
		Word word = null;
		if (chars.size() > 0) {
			sumdeltax = chars.size() <= 1 ? 0.0 : sumdeltax / (double) (chars.size()-1);
			word = new Word(chars);
			// doesn't word reliably!
			if (sumdeltax < ITALIC ) {
	//			word.setFontStyle("italic");
	//			word.setText("ITALIC"+t);
			} else if (sumdeltax > BOLD) {
	//			word.setFontStyle("bold");
	//			word.setText("BOLD"+t);
			}
		}
		return word;
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

	public List<TextLine> getSubLines() {
		return subLines;
	}

	public void setY(Integer iy) {
		this.integerY = iy;
	}

	public Integer getIntegerY() {
		return this.integerY;
	}
	
	public Set<FontStyle> getFontStyleSet() {
		if (fontStyleSet == null) {
			fontStyleSet = new HashSet<FontStyle>();
			for (SVGText text : this) {
				FontStyle fontStyle = FontStyle.getFontStyle(text);
				fontStyleSet.add(fontStyle);
			}
		}
		return fontStyleSet;
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

	
	private double getWidthOfSpaceCharacter(Double fontSize) {
		return SPACE_FUDGE * SPACE_WIDTH * fontSize;
	}
	
	/** counts spaces between leftMargin and first character
	 * often used with bbox for chunk in which text occurs
	 * @param leftMarginX
	 * @return spaceCount (can be negative)
	 */
	public Double leadingSpaceCount(Double leftMarginX) {
		Double spaceCount = null;
		if (leftMarginX != null) {
			getBoundingBox();
			getMeanFontSize();
			if (boundingBox != null) {
				Double xmin = boundingBox.getXRange().getMin();
				Double separation = xmin - leftMarginX;
				spaceCount = separation / getWidthOfSpaceCharacter(meanFontSize);
			}
		}
		return spaceCount;
	}

	/** counts spaces between lastCharacter and rightMargin
	 * often used with bbox for chunk in which text occurs
	 * @param rightMarginX
	 * @return spaceCount (can be negative)
	 */
	public Double trailingSpaceCount(Double rightMarginX) {
		Double spaceCount = null;
		if (rightMarginX != null) {
			getBoundingBox();
			getMeanFontSize();
			if (boundingBox != null) {
				Double xmax = boundingBox.getXRange().getMax();
				Double separation = rightMarginX - xmax;
				spaceCount = separation / getWidthOfSpaceCharacter(meanFontSize);
			}
		}
		return spaceCount;
	}

	public Double getMeanFontSize() {
		if (meanFontSize == null) {
			getFontSizeArray();
			meanFontSize = fontSizeArray.getMean();
		}
		return meanFontSize;
	}

	public RealArray getFontSizeArray() {
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
				boundingBox = new Real2Range(characterList.get(0).getBoundingBox());
				for (int i = 1; i < characterList.size(); i++) {
					SVGText charx = characterList.get(i);
					Real2Range bbox = charx.getBoundingBox();
					boundingBox.plus(bbox);
				}
			}
		}
		return boundingBox;
	}

	/** Array of width from SVGText @svgx:width attribute
	 * @return array of widths
	 */
	public RealArray getSVGCharacterWidthArray() {
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
	public RealArray getCharacterSeparationArray() {
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
	public RealArray getActualWidthsOfSpaceCharacters() {
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

	private Double getWidth(SVGText text) {
		String widthS = PDF2SVGUtil.getSVGXAttribute(text, PDF2SVGUtil.CHARACTER_WIDTH);
		Double fontSize = text.getFontSize();
		Double width = new Double(widthS) * SCALE;
		return width * fontSize;
	}

	public Double getMeanWidthOfSpaceCharacters() {
		RealArray spaceWidths = getActualWidthsOfSpaceCharacters();
		return spaceWidths == null ? null : spaceWidths.getMean();
	}

	public Double getModalExcessWidth() {
		RealArray excessWidthArray = getExcessWidthArray();
//		excessWidthArray.sortAscending();
		System.out.println(this.getLineContent());
		System.out.println(excessWidthArray);
		return -99.9; // junk
	}

	public RealArray getExcessWidthArray() {
		if (excessWidthArray == null) {
			excessWidthArray = new RealArray(characterList.size() - 1);
			getCharacterSeparationArray();
			getSVGCharacterWidthArray();
			for (int i = 0; i < characterList.size() - 1; i++){
				double deltaX = characterSeparationArray.get(i) - svgCharacterWidthArray.get(i);
				excessWidthArray.setElementAt(i,  deltaX);
			}
			excessWidthArray.format(TextAnalyzerX.NDEC_FONTSIZE);
		}
		return excessWidthArray;
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

	public static RealArray getCoordinatesOfLines(List<TextLine> largeLines) {
		// TODO Auto-generated method stub
		return null;
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
	
	public HtmlElement createHtmlLine() {
		List<TextLine> textLineList = createSuscriptTextLineList();
		HtmlP p = new HtmlP();
		for (TextLine textLine : textLineList) {
			HtmlElement pp = textLine.getHtmlElements();
			if (pp instanceof HtmlSpan) {
				Elements childElements = pp.getChildElements();
				for (int i = 0; i < childElements.size(); i++) {
					p.appendChild(HtmlElement.create(childElements.get(i)));
				}
			} else {
				p.appendChild(HtmlElement.create(pp));
			}
		}
		return p;
	}


	private HtmlElement getHtmlElements() {
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

	private void setSuscript(Suscript suscript) {
		this.suscript = suscript;
	}

	private SVGText textWithLowestX(SVGText nextSup, SVGText nextThis, SVGText nextSub) {
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

	private SVGText peekNext(List<SVGText> characterList, Integer index) {
		return (index >= characterList.size()) ? null : characterList.get(index);
	}

	public String getSpacedLineString() {
		StringBuilder sb = new StringBuilder();
		for (SVGText text : characterList) {
			String s = text.getValue();
			if (s.trim().length() == 0) {
				s = " ";
			}
			sb.append(s);
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

}
