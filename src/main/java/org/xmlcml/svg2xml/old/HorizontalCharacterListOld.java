package org.xmlcml.svg2xml.old;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;

/** holds a list of characters, normally in a horizontal line
 * 
 * exclusively used by TextAnalyzer
 * 
 * @author pm286
 *
 */
public class HorizontalCharacterListOld implements Iterable<SVGText> {

	private static final String SERIF = "Serif";
	private final static Logger LOG = Logger.getLogger(HorizontalCharacterListOld.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final double ITALIC = -0.18;
	private static final double BOLD = 0.25;
	private static final String DEFAULT_CHAR = "s";
	private static final double EPS = 0.05;
	private static final double COORD_EPS = 0.0001;
	private static final double FONT_EPS = 0.001;
	
	private List<SVGText> characterList;
	private Double yCoord = null;
	private List<Double> yCoordList = null;
	private Double fontSize = null;
	private List<Double> fontSizeList = null;
	private String physicalStyle;
	private List<String> physicalStyleList;
	
	private String lineContent = null;
	private String lineContentIncludingSpaces = null;
	private List<HorizontalCharacterListOld> subLines;
	private WordSequence wordSequence;
	private TextAnalyzerX textAnalyzerX;
	private SimpleFontOld simpleFont;
	private Integer y;
	private RealArray characterWidthArray;

	public HorizontalCharacterListOld(TextAnalyzerX textAnalyzerX, List<SVGText> characterList) {
		this.characterList = characterList;
		this.textAnalyzerX = textAnalyzerX;
	}
	
	public HorizontalCharacterListOld(TextAnalyzerX textAnalyzerX) {
		this(textAnalyzerX, new ArrayList<SVGText>());
	}
	
	/**
	 * split the list into smaller lists whenever there is a change
	 * in fontSize, physicalStyle or yCoord. This is heuristic and may need finer tuning
	 * 
	 * @return
	 */
	private List<HorizontalCharacterListOld> splitLineByCharacterAttributes() {
		if (subLines == null) {
			subLines = new ArrayList<HorizontalCharacterListOld>();
			Double lastFontSize = null;
			Double lastYCoord = null;
			String lastPhysicalStyle = null;
			HorizontalCharacterListOld charList = null;
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				Double fontSize = text.getFontSize();
				LOG.trace("fontSize "+fontSize);
				Double yCoord = text.getY();
//				String physicalStyle = getPhysicalStyle(text);
				if (i == 0 
//						|| LineAttributesHaveChanged(lastFontSize, lastYCoord, lastPhysicalStyle, fontSize, yCoord, physicalStyle)
					) {
					charList = new HorizontalCharacterListOld(this.textAnalyzerX);
					getSubLines().add(charList);
				}
				charList.add(text);
				lastFontSize = fontSize;
				lastYCoord = yCoord;
//				lastPhysicalStyle = physicalStyle;
			}
			if (getSubLines().size() != 1) {
				for (HorizontalCharacterListOld chList : getSubLines()) {
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
		if (fontSize == null) {
			getFontSizeList();
			for (Double fSize : fontSizeList) {
				if (fSize == null || (fontSize != null && !Real.isEqual(fSize, fontSize, FONT_EPS))) {
					fontSize = null;
					break;
				}
				fontSize = fSize;
			}
		}
		return fontSize;
	}
		
	private List<Double> getFontSizeList() {
		if (fontSizeList == null) {
			fontSizeList = new ArrayList<Double>();
			for (int i = 0; i < characterList.size(); i++) {
				SVGText text = characterList.get(i);
				Double fontSize = text.getFontSize();
				fontSizeList.add(fontSize);
			}
		}
		return fontSizeList;
	}
	
	/** returns the common value of yCoord or null
	 * if there is any variation
	 */
	private Double getYCoord() {
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
					SimpleCharacterOld simpleCharacter = simpleFont.getSimpleCharacter(ch);
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
				LOG.trace("fontsize changed "+lastFontSize+" -> "+fontSize);
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
//			simpleFont = textAnalyzerX.ensureSimpleFont();
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
			for (HorizontalCharacterListOld splitList : getSubLines()) {
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

	public List<HorizontalCharacterListOld> getSubLines() {
		return subLines;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getY() {
		return this.y;
	}

}
