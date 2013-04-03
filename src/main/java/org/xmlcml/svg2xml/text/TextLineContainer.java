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

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
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

	private List<Real2Range> discreteBoxes;

	private List<List<TextLine>> textLineBoxList;
	
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
			this.textLineList.add(textLine);
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
		for (Double fontSize : fontCountMap.keySet()) {
			LOG.trace(">> "+fontSize+" .. "+fontCountMap.get(fontSize));
			if (commonestFontSize == null || commonestFontSize.getDouble() < fontSize)  {
			    commonestFontSize = new SvgPlusCoordinate(fontSize);
			}
		}
		return commonestFontSize;
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
			if (linesWithLargestFont.size() > 0) {
				textLinesLargetFontBoundingBox = new Real2Range(new Real2Range(linesWithLargestFont.get(0).getBoundingBox()));
				for (int i = 1; i < linesWithLargestFont.size(); i++) {
					textLinesLargetFontBoundingBox.plus(linesWithLargestFont.get(i).getBoundingBox());
				}
			}
		}
		return textLinesLargetFontBoundingBox;
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

	public List<List<TextLine>> getTextLineBoxList() {
		getDiscreteBoxes();
		return textLineBoxList;
	}

	public List<Real2Range> getDiscreteBoxes() {
		if (discreteBoxes == null) {
			List<TextLine> textLineList = getLinesInIncreasingY();
			discreteBoxes = new ArrayList<Real2Range>();
			Real2Range bbox = null;
			List<TextLine> boxTextLineList = null;
			int i = 0;
			textLineBoxList = new ArrayList<List<TextLine>>();
			for (TextLine textLine : textLineList) {
				Real2Range bbox0 = textLine.getBoundingBox();
				LOG.trace(">> "+textLine.getLineString());
				if (bbox == null) {
					bbox = bbox0;
					boxTextLineList = new ArrayList<TextLine>();
					addBoxAndLines(bbox, boxTextLineList);
				} else {
					Real2Range intersectionBox = bbox.intersectionWith(bbox0);
					if (intersectionBox == null) {
						bbox = bbox0;
						boxTextLineList = new ArrayList<TextLine>();
						addBoxAndLines(bbox, boxTextLineList);
					} else {
						bbox = bbox.plusEquals(bbox0);
					}
				}
				boxTextLineList.add(textLine);
			}
		}
		return discreteBoxes;
	}

	private void addBoxAndLines(Real2Range bbox, List<TextLine> textLineList) {
		discreteBoxes.add(bbox);
		textLineBoxList.add(textLineList);
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

	public List<TextLine> getTextlineList() {
		return textLineList;
	}

}
