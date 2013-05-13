package org.xmlcml.svg2xml.text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.svg2xml.analyzer.AbstractPageAnalyzerX;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerUtils;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** holds text lines in order
 * to simplify TextAnalyzer
 * 
 * @author pm286
 *
 */
public class TextLineContainer {

	private static final Logger LOG = Logger.getLogger(TextLineContainer.class);

	/** default ratio for "isLargerThan" */
	public static final double LARGER_FONT_SIZE_RATIO = 1.02;

	private TextAnalyzerX textAnalyzer;
	
	private List<TextLine> linesWithCommonestFont;
	private List<TextLine> linesWithLargestFont;
	private List<TextLine> textLineList;
	private SvgPlusCoordinate largestFontSize;
	private SvgPlusCoordinate commonestFontSize;
	private Real2Range textLinesLargetFontBoundingBox;
	private Set<SvgPlusCoordinate> fontSizeSet;

	private Multiset<String> fontFamilySet;
	private List<Double> actualWidthsOfSpaceCharactersList;
	private Map<TextLine, Integer> textLineSerialMap;
	private List<String> textLineContentList;

	private RealArray interTextLineSeparationArray;
	private RealArray meanFontSizeArray;
	private RealArray modalExcessWidthArray;
	private Multiset<Double> separationSet;
	private Map<Integer, TextLine> textLineByYCoordMap;
	private RealArray textLineCoordinateArray;
	private Multimap<SvgPlusCoordinate, TextLine> textLineListByFontSize;

	private List<Real2Range> textLineChunkBoxes;

	private List<TextLineGroup> initialTextLineGroupList;
	private List<TextLine> commonestFontSizeTextLineList;
//	private List<TextLineGroup> textLineGroupList;

	private List<TextLineGroup> separatedTextLineGroupList;

	private HtmlElement createdHtmlElement;
	
	public TextLineContainer(TextAnalyzerX textAnalyzer) {
		this.textAnalyzer = textAnalyzer;
	}

	public static TextLineContainer createTextLineContainer(File svgFile) {
		return createTextLineContainer(svgFile, null);
	}

	public static TextLineContainer createTextLineContainer(File svgFile, TextAnalyzerX textAnalyzer) {
		TextLineContainer container = new TextLineContainer(textAnalyzer);
		List<TextLine> textLineList = TextLineContainer.createTextLineList(svgFile);
		if (textLineList != null) {
			container.setTextLines(textLineList);
		}
		return container;
	}

	public List<TextLine> getLinesInIncreasingY() {
		if (textLineList == null) {
			ensureTextLineByYCoordMap();
			List<Integer> yCoordList = Arrays.asList(textLineByYCoordMap.keySet().toArray(new Integer[0]));
			Collections.sort(yCoordList);
			textLineList = new ArrayList<TextLine>();
			int i = 0;
			textLineSerialMap = new HashMap<TextLine, Integer>();
			for (Integer y : yCoordList) {
				TextLine textLine = textLineByYCoordMap.get(y);
				textLineList.add(textLine);
				textLineSerialMap.put(textLine, i++);
			}
		}
		return textLineList;
	}
	
	/** some lines may not have spaces
	 * 
	 * @return
	 */
	public List<Double> getActualWidthsOfSpaceCharactersList() {
		if (actualWidthsOfSpaceCharactersList == null) {
			getLinesInIncreasingY();
			if (textLineList != null && textLineList.size() > 0) {
				actualWidthsOfSpaceCharactersList = new ArrayList<Double>();
				for (int i = 0; i < textLineList.size(); i++) {
					Double meanWidth = textLineList.get(i).getMeanWidthOfSpaceCharacters();
					meanWidth = meanWidth == null ? null : Real.normalize(meanWidth, TextAnalyzerX.NDEC_FONTSIZE);
					actualWidthsOfSpaceCharactersList.add(meanWidth);
				}
			}
//			actualWidthsOfSpaceCharactersArray.format(NDEC_FONTSIZE);
		}
		return actualWidthsOfSpaceCharactersList;
	}

	private void ensureTextLineByYCoordMap() {
		if (textLineByYCoordMap == null) {
			textLineByYCoordMap = new HashMap<Integer, TextLine>();
		}
	}
	
	public Integer getSerialNumber(TextLine textLine) {
		return (textLineSerialMap == null) ? null : textLineSerialMap.get(textLine);
	}
	

	public List<String> getTextLineContentList() {
		textLineContentList = null;
		if (textLineList != null) {
			textLineContentList = new ArrayList<String>();
			for (TextLine textLine : textLineList) {
				textLineContentList.add(textLine.getLineString());
			}
		}
		return textLineContentList;
	}
	
	public List<TextLine> getTextLineList() {
		return textLineList;
	}

	public void insertSpaces() {
		if (textLineList != null) {
			for (TextLine textLine : textLineList) {
				textLine.insertSpaces();
			}
		}
	}

	public void insertSpaces(double scaleFactor) {
		if (textLineList != null) {
			for (TextLine textLine : textLineList) {
				textLine.insertSpaces(scaleFactor);
			}
		}
	}


	
	public Set<SvgPlusCoordinate> getFontSizeContainerSet() {
		Set<SvgPlusCoordinate> fontSizeContainerSet = new HashSet<SvgPlusCoordinate>();
		if (fontSizeContainerSet == null) {
			for (TextLine textLine : textLineList) {
				fontSizeContainerSet.addAll(textLine.getFontSizeContainerSet());
			}
		}
		return fontSizeContainerSet;
	}

	public RealArray getMeanFontSizeArray() {
		if (meanFontSizeArray == null) {
			getLinesInIncreasingY();
			if (textLineList != null && textLineList.size() > 0) {
				meanFontSizeArray = new RealArray(textLineList.size());
				for (int i = 0; i < textLineList.size(); i++) {
					meanFontSizeArray.setElementAt(i, textLineList.get(i).getMeanFontSize());
				}
			}
			meanFontSizeArray.format(TextAnalyzerX.NDEC_FONTSIZE);
		}
		return meanFontSizeArray;
	}

	public void setTextLines(List<TextLine> textLineList) {
		this.textLineList = new ArrayList<TextLine>();
		for (TextLine textLine : textLineList) {
			add(textLine);
		}
	}

	private void add(TextLine textLine) {
		ensureTextLineList();
		this.textLineList.add(textLine);
	}
	
	private void ensureTextLineList() {
		if (this.textLineList == null) {
			this.textLineList = new ArrayList<TextLine>();
		}
	}

	public List<TextLine> getLinesWithLargestFont() {
		if (linesWithLargestFont == null) {
			linesWithLargestFont = new ArrayList<TextLine>();
			getLargestFontSize();
			for (int i = 0; i < textLineList.size(); i++){
				TextLine textLine = textLineList.get(i);
				Double fontSize = (textLine == null) ? null : textLine.getFontSize();
				if (fontSize != null) {
					if (Real.isEqual(fontSize, largestFontSize.getDouble(), 0.001)) {
						linesWithLargestFont.add( textLine);
					}
				}
			}
		}
		return linesWithLargestFont;
	}

	public List<TextLine> getLinesWithCommonestFont() {
		if (linesWithCommonestFont == null) {
			linesWithCommonestFont = new ArrayList<TextLine>();
			getCommonestFontSize();
			for (int i = 0; i < textLineList.size(); i++){
				TextLine textLine = textLineList.get(i);
				Double fontSize = (textLine == null) ? null : textLine.getFontSize();
				if (fontSize != null) {
					if (Real.isEqual(fontSize, commonestFontSize.getDouble(), 0.001)) {
						linesWithCommonestFont.add( textLine);
					}
				}
			}
		}
		return linesWithCommonestFont;
	}

	public SvgPlusCoordinate getCommonestFontSize() {
		commonestFontSize = null;
		Map<Double, Integer> fontCountMap = new HashMap<Double, Integer>();
		for (TextLine textLine : textLineList) {
			Double fontSize = textLine.getFontSize();
			Integer ntext = textLine.getCharacterList().size();
			if (fontSize != null) {
				Integer sum = fontCountMap.get(fontSize);
				if (sum == null) {
					sum = ntext;
				} else {
					sum += ntext;
				}
				fontCountMap.put(fontSize, sum);
			}
		}
		getCommonestFontSize(fontCountMap);
		return commonestFontSize;
	}

	private void getCommonestFontSize(Map<Double, Integer> fontCountMap) {
		int frequency = -1;
		for (Double fontSize : fontCountMap.keySet()) {
			int count = fontCountMap.get(fontSize);
			LOG.trace(">> "+fontSize+" .. "+fontCountMap.get(fontSize));
			if (commonestFontSize == null || count > frequency) {
			    commonestFontSize = new SvgPlusCoordinate(fontSize);
			    frequency = count;
			}
		}
		if (commonestFontSize != null) LOG.trace("commonest "+commonestFontSize.getDouble());
	}
	
	public SvgPlusCoordinate getLargestFontSize() {
		largestFontSize = null;
		Set<SvgPlusCoordinate> fontSizes = this.getFontSizeSet();
		for (SvgPlusCoordinate fontSize : fontSizes) {
			if (largestFontSize == null || largestFontSize.getDouble() < fontSize.getDouble()) {
				largestFontSize = fontSize;
			}
		}
		return largestFontSize;
	}
	
	public Real2Range getLargestFontBoundingBox() {
		if (textLinesLargetFontBoundingBox == null) {
			getLinesWithLargestFont();
			getBoundingBox(linesWithLargestFont);
		}
		return textLinesLargetFontBoundingBox;
	}

	public static Real2Range getBoundingBox(List<TextLine> textLines) {
		Real2Range boundingBox = null;
		if (textLines.size() > 0) {
			boundingBox = new Real2Range(new Real2Range(textLines.get(0).getBoundingBox()));
			for (int i = 1; i < textLines.size(); i++) {
				boundingBox.plus(textLines.get(i).getBoundingBox());
			}
		}
		return boundingBox;
	}

	public Set<SvgPlusCoordinate> getFontSizeSet() {
		if (fontSizeSet == null) {
			if (textLineList != null) {
				fontSizeSet = new HashSet<SvgPlusCoordinate>();
				for (TextLine textLine : textLineList) {
					Set<SvgPlusCoordinate> textLineFontSizeSet = textLine.getFontSizeSet();
					fontSizeSet.addAll(textLineFontSizeSet);
				}
			}
		}
		return fontSizeSet;
	}

	/** creates a multiset from addAll() on multisets for each line
	 *  
	 * @return
	 */
	public Multiset<String> getFontFamilyMultiset() {
		if (fontFamilySet == null) {
			fontFamilySet = HashMultiset.create();
			for (TextLine textLine : textLineList) {
				Multiset<String> listFontFamilySet = textLine.getFontFamilyMultiset();
				fontFamilySet.addAll(listFontFamilySet);
			}
		}
		return fontFamilySet;
	}

	/** gets commonest font
	 *  
	 * @return
	 */
	public String getCommonestFontFamily() {
		getFontFamilyMultiset();
		String commonestFontFamily = null;
		int highestCount = -1;
		Set<String> fontFamilyElementSet = fontFamilySet.elementSet();
		for (String fontFamily : fontFamilyElementSet) {
			int count = fontFamilySet.count(fontFamily);
			if (count > highestCount) {
				highestCount = count;
				commonestFontFamily = fontFamily;
			}
		}
		return commonestFontFamily;
	}

	/** gets commonest font
	 *  
	 * @return
	 */
	public int getFontFamilyCount() {
		getFontFamilyMultiset();
		return fontFamilySet.elementSet().size();
	}

	/** get non-overlapping boundingBoxes
	 * @return
	 */
	public List<Real2Range> getDiscreteLineBoxes() {
		List<Real2Range> discreteLineBoxes = new ArrayList<Real2Range>();
//		List<TextLine> textLines = this.getLinesSortedByYCoord();
		return discreteLineBoxes;
	}

	public RealArray getInterTextLineSeparationArray() {
		getTextLineCoordinateArray();
		if (textLineList != null && textLineList.size() > 0) {
			interTextLineSeparationArray = new RealArray();
			Double y0 = textLineCoordinateArray.get(0);
			for (int i = 1; i < textLineCoordinateArray.size(); i++) {
				Double y = textLineCoordinateArray.get(i);
				interTextLineSeparationArray.addElement(y - y0);
				y0 = y;
			}
			interTextLineSeparationArray.format(TextAnalyzerX.NDEC_FONTSIZE);
		}
		return interTextLineSeparationArray;
	}

	public Multimap<SvgPlusCoordinate, TextLine> getTextLineListByFontSize() {
		if (textLineListByFontSize == null) {
			textLineListByFontSize = ArrayListMultimap.create();
			for (TextLine textLine : textLineList) {
				Set<SvgPlusCoordinate> fontSizeSet = textLine.getFontSizeSet();
				if (fontSizeSet != null) {
					for (SvgPlusCoordinate fontSize : fontSizeSet) {
						textLineListByFontSize.put(fontSize, textLine);
					}
				}
			}
		}
		return textLineListByFontSize;
		
	}

	public Map<Integer, TextLine> getTextLineByYCoordMap() {
		return textLineByYCoordMap;
	}

	public RealArray getModalExcessWidthArray() {
		if (modalExcessWidthArray == null) {
			getLinesInIncreasingY();
			if (textLineList != null && textLineList.size() > 0) {
				modalExcessWidthArray = new RealArray(textLineList.size());
				for (int i = 0; i < textLineList.size(); i++) {
					Double modalExcessWidth = textLineList.get(i).getModalExcessWidth();
					modalExcessWidthArray.setElementAt(i, modalExcessWidth);
				}
			}
			modalExcessWidthArray.format(TextAnalyzerX.NDEC_FONTSIZE);
		}
		return modalExcessWidthArray;
	}

	public Multiset<Double> createSeparationSet(int decimalPlaces) {
		getInterTextLineSeparationArray();
		interTextLineSeparationArray.format(decimalPlaces);
		separationSet = HashMultiset.create();
		for (int i = 0; i < interTextLineSeparationArray.size(); i++) {
			separationSet.add(interTextLineSeparationArray.get(i));
		}
		return separationSet;
	}

	public Double getMainInterTextLineSeparation(int decimalPlaces) {
		Double mainTextLineSeparation = null;
		createSeparationSet(decimalPlaces);
		Set<Entry<Double>> ddSet = separationSet.entrySet();
		Entry<Double> maxCountEntry = null;
		Entry<Double> maxSeparationEntry = null;
		for (Entry<Double> dd : ddSet) {
			if (maxCountEntry == null || maxCountEntry.getCount() < dd.getCount()) {
				maxCountEntry = dd;
			}
			if (maxSeparationEntry == null || maxSeparationEntry.getElement() < dd.getElement()) {
				maxSeparationEntry = dd;
			}
		}
		if (maxCountEntry.equals(maxSeparationEntry)) {
			mainTextLineSeparation = maxSeparationEntry.getElement();
		}
		return mainTextLineSeparation;
	}

	public void getSortedTextLines(List<SVGText> textCharacters) {
		if (textLineByYCoordMap == null) {
			textLineByYCoordMap = new HashMap<Integer, TextLine>();
			Multimap<Integer, SVGText> charactersByY = TextAnalyzerUtils.createCharactersByY(textCharacters);
			for (Integer yCoord : charactersByY.keySet()) {
				Collection<SVGText> characters = charactersByY.get(yCoord);
				TextLine textLine = new TextLine(characters, this.textAnalyzer);
				textLine.sortLineByX();
				textLineByYCoordMap.put(yCoord, textLine);
			}
		}
	}

	public RealArray getTextLineCoordinateArray() {
		if (textLineCoordinateArray == null) {
			getLinesInIncreasingY();
			if (textLineList != null && textLineList.size() > 0) {
				textLineCoordinateArray = new RealArray();
				for (TextLine textLine : textLineList) {
					Double y0 = textLine.getYCoord();
					textLineCoordinateArray.addElement(y0);
				}
			}
			textLineCoordinateArray.format(TextAnalyzerX.NDEC_FONTSIZE);
		}
		return textLineCoordinateArray;
	}

	/** finds maximum indent of lines
	 * must be at least 2 lines
	 * currently does not check for capitals, etc.
	 * 
	 */
	public Double getMaxiumumRightIndent() {
		Double indent = null;
		Double xRight = null;
		if (textLineList != null && textLineList.size() > 1) {
			for (TextLine textLine : textLineList) {
				Double xLast = textLine.getLastXCoordinate();
				if (xRight == null) {
					xRight = xLast;
				}
				if (xRight - xLast > TextAnalyzerX.INDENT_MIN) {
					indent = xLast;
				} else if (xLast - xRight > TextAnalyzerX.INDENT_MIN) {
					indent = xRight;
				}
			}
		}
		return indent;
	}

	public TextLineSet getTextLineSetByFontSize(double fontSize) {
		Multimap<SvgPlusCoordinate, TextLine> textLineListByFontSize = this.getTextLineListByFontSize();
		List<TextLine> textLines = (List<TextLine>) textLineListByFontSize.get(new SvgPlusCoordinate(fontSize));
		return new TextLineSet(textLines);
	}

	public List<TextLineGroup> getInitialTextLineGroupList() {
		getTextLineChunkBoxes();
		return initialTextLineGroupList;
	}

	/**
	 * This is heuristic. At present it is font-size equality. Font families
	 * are suspect as there are "synonyms", e.g. TimesRoman and TimesNR
	 * 
	 * @return
	 */
	public List<TextLine> getCommonestFontSizeTextLineList() {
		if (commonestFontSizeTextLineList == null) {
			SvgPlusCoordinate commonestFontSize = getCommonestFontSize();
			Double commonestFontSizeValue = (commonestFontSize == null) ?
					null : commonestFontSize.getDouble();
			commonestFontSizeTextLineList = new ArrayList<TextLine>();
			for (TextLine textLine : textLineList) {
				Double fontSize = textLine.getFontSize();
				if (fontSize != null && Real.isEqual(fontSize, commonestFontSizeValue, 0.01)) {
					commonestFontSizeTextLineList.add(textLine);
					LOG.trace("COMMONEST FONT SIZE "+textLine);
				}
			}
		}
		return commonestFontSizeTextLineList;
	}

	public List<TextLineGroup> getSeparatedTextLineGroupList() {
		if (separatedTextLineGroupList == null) {
			getCommonestFontSizeTextLineList();
			getInitialTextLineGroupList();
			separatedTextLineGroupList = new ArrayList<TextLineGroup>();
			int i = 0;
			for (TextLineGroup textLineGroup : initialTextLineGroupList) {
				List<TextLineGroup> splitChunks = textLineGroup.splitIntoUniqueChunks(this);
				for (TextLineGroup textLineChunk0 : splitChunks) {
					separatedTextLineGroupList.add(textLineChunk0);
				}
				i++;
			}
		}
		LOG.trace("separated "+separatedTextLineGroupList.size());
		return separatedTextLineGroupList;
	}

	public List<Real2Range> getTextLineChunkBoxes() {
		if (textLineChunkBoxes == null) {
			List<TextLine> textLineList = getLinesInIncreasingY();
			textLineChunkBoxes = new ArrayList<Real2Range>();
			Real2Range bbox = null;
			TextLineGroup textLineGroup = null;
			int i = 0;
			initialTextLineGroupList = new ArrayList<TextLineGroup>();
			for (TextLine textLine : textLineList) {
				Real2Range bbox0 = textLine.getBoundingBox();
				LOG.trace(">> "+textLine.getLineString());
				if (bbox == null) {
					bbox = bbox0;
					textLineGroup = new TextLineGroup(this);
					addBoxAndLines(bbox, textLineGroup);
				} else {
					Real2Range intersectionBox = bbox.intersectionWith(bbox0);
					if (intersectionBox == null) {
						bbox = bbox0;
						textLineGroup = new TextLineGroup(this);
						addBoxAndLines(bbox, textLineGroup);
					} else {
						bbox = bbox.plusEquals(bbox0);
					}
				}
				textLineGroup.add(textLine);
			}
		}
		return textLineChunkBoxes;
	}

	private void addBoxAndLines(Real2Range bbox, TextLineGroup textLineGroup) {
		textLineChunkBoxes.add(bbox);
		initialTextLineGroupList.add(textLineGroup);
	}

	public static TextLineContainer createTextLineContainerWithSortedLines(File svgFile) {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
		List<SVGText> textCharacters = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgPage, ".//svg:text"));
		return createTextLineContainerWithSortedLines(textCharacters);
	}

	public static TextLineContainer createTextLineContainerWithSortedLines(List<SVGText> textCharacters, TextAnalyzerX textAnalyzer) {
		TextLineContainer textLineContainer = new TextLineContainer(textAnalyzer);
		textLineContainer.getSortedTextLines(textCharacters);
		textLineContainer.getLinesInIncreasingY();
		textAnalyzer.setTextCharacters(textCharacters);
		textAnalyzer.setTextLineContainer(textLineContainer);
		return textLineContainer;
	}
	
	public static TextLineContainer createTextLineContainerWithSortedLines(List<SVGText> textCharacters) {
		TextAnalyzerX textAnalyzer = new TextAnalyzerX();
		TextLineContainer textLineContainer = new TextLineContainer(textAnalyzer);
		textLineContainer.getSortedTextLines(textCharacters);
		textLineContainer.getLinesInIncreasingY();
		textAnalyzer.setTextLineContainer(textLineContainer);
		return textLineContainer;
	}
	
	public TextAnalyzerX getTextAnalyzer() {
		return textAnalyzer;
	}

	/** finds maximum indent of lines
	 * must be at least 2 lines
	 * currently does not check for capitals, etc.
	 * 
	 */
	public Double getMaximumLeftIndentForLargestFont() {
		Double indent = null;
		Double xLeft = null;
		List<TextLine> textLineListWithLargestFont = this.getLinesWithCommonestFont();
		if (textLineListWithLargestFont != null && textLineListWithLargestFont.size() > 1) {
			for (TextLine textLine : textLineListWithLargestFont) {
				Double xStart = textLine.getFirstXCoordinate();
				if (xStart == null) {
					throw new RuntimeException("null start");
				}
				if (xLeft == null) {
					xLeft = xStart;
				}
				if (xLeft - xStart > TextAnalyzerX.INDENT_MIN) {
					indent = xLeft;
				} else if (xStart - xLeft > TextAnalyzerX.INDENT_MIN) {
					indent = xStart;
				}
			}
		}
		return indent;
	}

	/** finds maximum indent of lines
	 * must be at least 2 lines
	 * currently does not check for capitals, etc.
	 * 
	 */
	public static Double getMaximumLeftIndent(List<TextLine> textLineList) {
		Double indent = null;
		Double xLeft = null;
		if (textLineList != null && textLineList.size() > 1) {
			for (TextLine textLine : textLineList) {
				Double xStart = textLine.getFirstXCoordinate();
				if (xStart == null) {
					throw new RuntimeException("null start");
				}
				if (xLeft == null) {
					xLeft = xStart;
				}
				if (xLeft - xStart > TextAnalyzerX.INDENT_MIN) {
					indent = xLeft;
				} else if (xStart - xLeft > TextAnalyzerX.INDENT_MIN) {
					indent = xStart;
				}
			}
		}
		return indent;
	}

	public static List<TextLine> createTextLineList(File svgFile) {
		TextLineContainer textLineContainer = createTextLineContainerWithSortedLines(svgFile);
		List<TextLine> textLineList = textLineContainer.getLinesInIncreasingY();
		return textLineList;
	}

	public static AbstractPageAnalyzerX createTextAnalyzerWithSortedLines(List<SVGText> characters) {
			TextAnalyzerX textAnalyzer = new TextAnalyzerX();
			TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(characters, textAnalyzer);
			return textAnalyzer;
	}

	private static TextLineContainer createTextLineContainer(List<SVGText> characters, TextAnalyzerX textAnalyzer) {
//		TextLineContainer textLineContainer = new TextLineContainer(textAnalyzer);
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainerWithSortedLines(characters, textAnalyzer);
		return textLineContainer;
	}

	public static HtmlElement createHtmlDiv(List<TextLineGroup> textLineGroupList) {
		HtmlDiv div = new HtmlDiv();
		for (TextLineGroup group : textLineGroupList) {
			HtmlElement el = null;
			if (group == null) {
//				el = new HtmlP();
//				el.appendChild("PROBLEM");
//				div.appendChild(el);
//				div.debug("XXXXXXXXXXXXXXXXXX");
			} else {
				el = group.createHtml();
				div.appendChild(el);
			}
		}
		return div;
	}

	public HtmlElement createHtmlDivWithParas() {
		List<TextLineGroup> textLineGroupList = this.getSeparatedTextLineGroupList();
		LOG.trace("TLG "+textLineGroupList);
		if (textLineGroupList.size() == 0) {
			LOG.trace("TextLineList: "+textLineList);
			// debug
		}
		boolean bb = false;
		createHtmlElementWithParas(textLineGroupList);
		return createdHtmlElement;
	}

	/** only used in tests?
	 * 
	 * @param textLineGroupList
	 * @return
	 */
	 public HtmlElement createHtmlElementWithParas(List<TextLineGroup> textLineGroupList) {
		List<TextLine> commonestTextLineList = this.getCommonestFontSizeTextLineList();
		createdHtmlElement = null;
		if (commonestTextLineList.size() == 0){
			 createdHtmlElement = null;
		} else if (commonestTextLineList.size() == 1){
			 createdHtmlElement = commonestTextLineList.get(0).createHtmlLine();
		} else {
			HtmlElement rawDiv = createHtmlDiv(textLineGroupList);
			createdHtmlElement = createDivWithParas(commonestTextLineList, rawDiv);
		}
		return createdHtmlElement;
	}

	private HtmlDiv createDivWithParas(List<TextLine> textLineList, HtmlElement rawDiv) {
		HtmlDiv div = null;
		Double leftIndent = TextLineContainer.getMaximumLeftIndent(textLineList);
		Real2Range leftBB = TextLineContainer.getBoundingBox(textLineList);
		Elements htmlLines = rawDiv.getChildElements();
		LOG.trace("textLine "+textLineList.size()+"; html: "+ htmlLines.size());
		
		if (leftBB != null) {
			Double deltaLeftIndent = (leftIndent == null) ? 0 : (leftIndent - leftBB.getXRange().getMin());
			Real2Range largestFontBB = TextLineContainer.getBoundingBox(textLineList);
			if (largestFontBB != null) {
				RealRange xRange = largestFontBB.getXRange();
				Double indentBoundary = largestFontBB.getXRange().getMin() + deltaLeftIndent/2.0;
				LOG.trace("left, delta, boundary "+leftIndent+"; "+deltaLeftIndent+"; "+indentBoundary);
				div = new HtmlDiv();
				// always start with para
				HtmlP pCurrent = (htmlLines.size() == 0) ? null : 
					TextLineContainer.createAndAddNewPara(div, (HtmlP) htmlLines.get(0));
				int size = htmlLines.size();
				for (int i = 1; i < size/*textLineList.size()*/; i++) {
					TextLine textLine = (textLineList.size() <= i) ? null : textLineList.get(i);
					LOG.trace(">"+i+"> "+textLine);
					HtmlP pNext = i < htmlLines.size() ? (HtmlP) HtmlElement.create(htmlLines.get(i)) : null;
					// indent, create new para
					if (pNext == null) {
						LOG.error("Skipping HTML "+pCurrent+" // "+textLine);
					} else if (textLine != null && textLine.getFirstXCoordinate() > indentBoundary) {
						pCurrent = createAndAddNewPara(div, pNext);
					} else {
						mergeParas(pCurrent, pNext);
					}
				}
			}
		}
		return div;
	}
	
	public static HtmlP createAndAddNewPara(HtmlElement div, HtmlP p) {
		HtmlP pNew = (HtmlP) HtmlElement.create(p);
		div.appendChild(pNew);
		return pNew;
	}

	public static void mergeParas(HtmlP pCurrent, HtmlP pNext) {
		Elements currentChildren = pCurrent.getChildElements();
		if (currentChildren.size() > 0) {
			HtmlElement lastCurrent = (HtmlElement) currentChildren.get(currentChildren.size() - 1);
			HtmlSpan currentLastSpan = (lastCurrent instanceof HtmlSpan) ? (HtmlSpan) lastCurrent : null;
			Elements nextChildren = pNext.getChildElements();
			HtmlElement firstNext = nextChildren.size() == 0 ? null : (HtmlElement) nextChildren.get(0);
			HtmlSpan nextFirstSpan = (firstNext != null && firstNext instanceof HtmlSpan) ? (HtmlSpan) firstNext : null;
			int nextCounter = 0;
			// merge texts
			if (currentLastSpan != null && nextFirstSpan != null) {
				String mergedText = mergeLineText(currentLastSpan.getValue(), nextFirstSpan.getValue());
				LOG.trace("Merged "+mergedText);
				lastCurrent.setValue(mergedText);
				nextCounter = 1;
			}
			//merge next line's children
			for (int i = nextCounter; i < nextChildren.size(); i++) {
				pCurrent.appendChild(HtmlElement.create(nextChildren.get(i)));
			}
		}
	}

	private static String mergeLineText(String last, String next) {
		//merge hyphen minus
		if (last.endsWith("-")) {
			return last.substring(0, last.length()-1) + next;
		} else {
			return last + " " + next;
		}
	}

	public boolean endsWithRaggedLine() {
		return createdHtmlElement != null &&
				!createdHtmlElement.getValue().endsWith(".");
	}

	public boolean startsWithRaggedLine() {
		boolean starts = false;
		if (createdHtmlElement != null && createdHtmlElement.getValue().length() > 0) {
			Character c = createdHtmlElement.getValue().charAt(0);
			if (c != null) {
				starts = !Character.isUpperCase(c);
			}
		}
		return starts;
	}

	public boolean lineIsLargerThanCommonestFontSize(int lineNumber) {
		TextLine textLine = (lineNumber < 0 || lineNumber >= textLineList.size()) ?
				null : textLineList.get(lineNumber);
		return lineIsLargerThanCommonestFontSize(textLine);
	}

	public boolean lineIsLargerThanCommonestFontSize(TextLine textLine) {
		boolean isLargerThan = false;
		Double commonestFontSize = getCommonestFontSize().getDouble();
		if (textLine != null && commonestFontSize != null) {
			Double fontSize = textLine.getFontSize();
			if (fontSize != null) {
				isLargerThan = fontSize / commonestFontSize > LARGER_FONT_SIZE_RATIO;
			}
		}
		return isLargerThan;
	}

	public boolean isCommonestFontSize(TextLine textLine) {
		this.getCommonestFontSizeTextLineList();
		return textLine != null && commonestFontSizeTextLineList.contains(textLine);
	}

//	public boolean isCommonestFontSize(int i) {
//		TextLine textLine = (i < 0 || i >= textLineList.size()) ?
//				null : textLineList.get(i);
//		return isCommonestFontSize(textLine);
//	}


	public boolean isCommonestFontSize(TextLineGroup textLineGroup) {
		this.getCommonestFontSizeTextLineList();
		TextLine largestLine = textLineGroup.getLargestLine();
		return textLineGroup != null && commonestFontSizeTextLineList.contains(largestLine);
	}

//	public boolean isCommonestFontSize(int i) {
//		getSeparatedTextLineGroupList();
//		TextLineGroup textLineGroup = (i < 0 || i >= separatedTextLineGroupList.size()) ?
//				null : separatedTextLineGroupList.get(i);
//		return isCommonestFontSize(textLineGroup);
//	}

	public boolean lineGroupIsLargerThanCommonestFontSize(int lineNumber) {
		TextLine textLine = (lineNumber < 0 || lineNumber >= textLineList.size()) ?
				null : textLineList.get(lineNumber);
		return lineIsLargerThanCommonestFontSize(textLine);
	}

	public boolean lineGroupIsLargerThanCommonestFontSize(TextLineGroup textLineGroup) {
		boolean isLargerThan = false;
		Double commonestFontSize = getCommonestFontSize().getDouble();
		if (textLineGroup != null && commonestFontSize != null) {
			Double fontSize = textLineGroup.getFontSize();
			if (fontSize != null) {
				isLargerThan = fontSize / commonestFontSize > LARGER_FONT_SIZE_RATIO;
			}
		}
		return isLargerThan;
	}

	
	/** split after line where font size changes to/from bigger than commonest
	 * dangerous if there are sub or superscripts (use splitGroupBiggerThanCommonest)
	 * @return
	 */
	public IntArray splitBiggerThanCommonest() {
		Double commonestFontSize = this.getCommonestFontSize().getDouble();
		IntArray splitArray = new IntArray();
		for (int i = 0; i < textLineList.size() - 1; i++) {
			TextLine textLineA = textLineList.get(i);
			Double fontSizeA = textLineA.getFontSize();
			TextLine textLineB = textLineList.get(i+1);
			Double fontSizeB = textLineB.getFontSize();
			if (fontSizeA != null && fontSizeB != null) {
				double ratioAB = fontSizeA / fontSizeB;
				// line increases beyond commonest size?
				if (Real.isEqual(fontSizeA, commonestFontSize, 0.01) 
						&& ratioAB < 1./LARGER_FONT_SIZE_RATIO) {
					splitArray.addElement(i);
				} else if (Real.isEqual(fontSizeB, commonestFontSize, 0.01) 
						&& ratioAB > LARGER_FONT_SIZE_RATIO) {
					splitArray.addElement(i);
				}
			}
		}
		return splitArray;
	}

	
	/** split after textLineGroup where font size changes to/from bigger than commonest
	 * 
	 * @return
	 */
	public IntArray splitGroupBiggerThanCommonest() {
		getSeparatedTextLineGroupList();
		Double commonestFontSize = this.getCommonestFontSize().getDouble();
		IntArray splitArray = new IntArray();
		for (int i = 0; i < separatedTextLineGroupList.size() - 1; i++) {
			TextLineGroup textLineGroupA = separatedTextLineGroupList.get(i);
			Double fontSizeA = textLineGroupA.getFontSize();
			TextLineGroup textLineB = separatedTextLineGroupList.get(i+1);
			Double fontSizeB = textLineB.getFontSize();
			if (fontSizeA != null && fontSizeB != null) {
				double ratioAB = fontSizeA / fontSizeB;
				// line increases beyond commonest size?
				if (Real.isEqual(fontSizeA, commonestFontSize, 0.01) 
						&& ratioAB < 1./LARGER_FONT_SIZE_RATIO) {
					splitArray.addElement(i);
				} else if (Real.isEqual(fontSizeB, commonestFontSize, 0.01) 
						&& ratioAB > LARGER_FONT_SIZE_RATIO) {
					splitArray.addElement(i);
				}
			}
		}
		return splitArray;
	}

	/** split the textLineContainer after the lines in array.
	 * if null or size() == 0, returns list with 'this'. so a returned list of size 0
	 * effectively does nothing
	 * @param afterLineGroups if null or size() == 0, returns list with 'this';
	 * @return
	 */
	public List<TextLineContainer> splitLineGroupsAfter(IntArray afterLineGroups) {
		getSeparatedTextLineGroupList();
		List<TextLineContainer> textLineContainerList = new ArrayList<TextLineContainer>();
		if (afterLineGroups == null || afterLineGroups.size() == 0) {
			textLineContainerList.add(this);
		} else {
			int start = 0;
			for (int i = 0; i < afterLineGroups.size();i++) {
				int lineNumber = afterLineGroups.elementAt(i);
				if (lineNumber > separatedTextLineGroupList.size() -1) {
					throw new RuntimeException("bad index: "+lineNumber);
				}
				TextLineContainer newTextLineContainer = createTextLineContainer(start, lineNumber);
				textLineContainerList.add(newTextLineContainer);
				start = lineNumber + 1;
			}
			TextLineContainer newTextLineContainer = createTextLineContainer(start, separatedTextLineGroupList.size() - 1);
			textLineContainerList.add(newTextLineContainer);
		}
		return textLineContainerList;
	}

	private TextLineContainer createTextLineContainer(int startLineGroup, int lineGroupNumber) {
		getSeparatedTextLineGroupList();
		TextLineContainer textLineContainer = new TextLineContainer(textAnalyzer);
		for (int iGroup = startLineGroup; iGroup <= lineGroupNumber; iGroup++) {
			TextLineGroup textLineGroup = separatedTextLineGroupList.get(iGroup);
			List<TextLine> textLineList = textLineGroup.getTextLineList();
			textLineContainer.add(textLineList);
		}
		return textLineContainer;
	}

	private void add(List<TextLine> textLineList) {
		for (TextLine textLine : textLineList) {
			this.add(textLine);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (textLineList == null) {
			sb.append("null");
		} else {
			sb.append("TextLineContainer: "+ textLineList.size());
			for (TextLine textLine :textLineList) {
				sb.append(textLine.toString()+"\n");
			}
		}
		return sb.toString();
	}

	/** splits bold line(s) from succeeeding ones.
	 * may trap smaller headers - must catch this later
	 * @return
	 */
	public List<TextLineContainer> splitBoldHeader() {
		getSeparatedTextLineGroupList();
		List<TextLineContainer> splitList = null;
		if (separatedTextLineGroupList.size() > 0) {
			int after = -1;
			for (int i = 0; i < separatedTextLineGroupList.size(); i++) {
				if (!separatedTextLineGroupList.get(i).isBold()) {
					break;
				}
				after = i;
			}
			if (after >= 0) {
				IntArray splitter = new IntArray(new int[]{after});
				splitList = this.splitLineGroupsAfter(splitter);
			}
		}
		return splitList;
	}
	
	/** splits line(s) on fontSize.
	 * @return
	 */
	public IntArray getSplitArrayForFontSizeChange() {
		double EPS = 0.01;
		getSeparatedTextLineGroupList();
		List<TextLineContainer> splitList = null;
		Double currentFontSize = null;
		IntArray splitArray = new IntArray();
		if (separatedTextLineGroupList.size() > 0) {
			for (int i = 0; i < separatedTextLineGroupList.size(); i++) {
				Double fontSize = separatedTextLineGroupList.get(i).getFontSize();
				if (currentFontSize == null) {
					currentFontSize = fontSize;
				} else if (!Real.isEqual(fontSize, currentFontSize, EPS)) {
					splitArray.addElement(i - 1);
				}
			}
		}
		return splitArray;
	}
	
	/** splits line(s) on fontSize.
	 * @return
	 */
	public List<TextLineContainer> splitOnFontSizeChange() {
		IntArray splitter = getSplitArrayForFontSizeChange();
		List<TextLineContainer> splitList = null;
		if (splitter != null) {
			splitList = this.splitLineGroupsAfter(splitter);
		}
		return splitList;
	}
	
}
