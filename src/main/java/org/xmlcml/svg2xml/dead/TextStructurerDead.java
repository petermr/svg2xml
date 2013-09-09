package org.xmlcml.svg2xml.dead;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.svg2xml.page.ChunkAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzerUtils;
import org.xmlcml.svg2xml.pdf.ChunkId;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.TextCoordinate;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextStructurer;

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
public class TextStructurerDead {

//	private static final Logger LOG = Logger.getLogger(TextStructurerOld.class);
//
//	/** used for splitting between lineGroups
//	 * 
//	 * @author pm286
//	 *
//	 */
//	public enum Splitter {
//		BOLD,
//		FONTSIZE,
//		FONTFAMILY,
//	};
//	
//	Pattern NUMBER_ITEM_PATTERN = Pattern.compile("^\\s*[\\[\\(]?\\s*(\\d+)\\s*\\.?[\\]\\)]?\\.?\\s*.*");
//	
//	/** default ratio for "isLargerThan" */
//	public static final double LARGER_FONT_SIZE_RATIO = 1.02;
//
//	private static final double Y_EPS = 0.5; // line can wobble 
//
//	private TextAnalyzerXOld textAnalyzer;
//	
//	private List<TextLineOld> linesWithCommonestFont;
//	private List<TextLineOld> linesWithLargestFont;
//	private List<TextLineOld> textLineList;
//	private TextCoordinate largestFontSize;
//	private TextCoordinate commonestFontSize;
//	private Real2Range textLinesLargetFontBoundingBox;
//	private Set<TextCoordinate> fontSizeSet;
//
//	private Multiset<String> fontFamilySet;
//	private List<Double> actualWidthsOfSpaceCharactersList;
//	private Map<TextLineOld, Integer> textLineSerialMap;
//	private List<String> textLineContentList;
//
//	private RealArray interTextLineSeparationArray;
//	private RealArray meanFontSizeArray;
//	private RealArray modalExcessWidthArray;
//	private Multiset<Double> separationSet;
//	private Map<Integer, TextLineOld> textLineByYCoordMap;
//	private RealArray textLineCoordinateArray;
//	private Multimap<TextCoordinate, TextLineOld> textLineListByFontSize;
//
//	private List<Real2Range> textLineChunkBoxes;
//
//	private List<ScriptLine> initialScriptLineList;
//	private List<TextLineOld> commonestFontSizeTextLineList;
//	private List<ScriptLine> scriptedLineList;
//	private HtmlElement createdHtmlElement;
//	private SVGElement svgChunk;
//
//	private Real2Range boundingBox;
//
//	/** this COPIES the lines in the textAnalyzer
//	 * this may not be a good idea
//	 * @param textAnalyzer to copy lines from
//	 */
//	public TextStructurerOld(TextAnalyzerXOld textAnalyzer) {
//		this.textAnalyzer = textAnalyzer;
//		if (textAnalyzer != null) {
//			textAnalyzer.setTextStructurer(this);
//			List<SVGText> characters = textAnalyzer.getTextCharacters();
//			this.createLinesSortedInXThenY(characters, textAnalyzer);
//		}
//	}
//
//	public static TextStructurerOld createTextStructurer(File svgFile) {
//		return createTextStructurer(svgFile, null);
//	}
//
//	public static TextStructurerOld createTextStructurer(File svgFile, TextAnalyzerXOld textAnalyzer) {
//		TextStructurerOld container = new TextStructurerOld(textAnalyzer);
//		List<TextLineOld> textLineList = TextStructurerOld.createTextLineList(svgFile);
//		if (textLineList != null) {
//			container.setTextLines(textLineList);
//		}
//		return container;
//	}
//
//	public List<TextLineOld> getLinesInIncreasingY() {
//		if (textLineList == null) {
//			ensureTextLineByYCoordMap();
//			List<Integer> yCoordList = Arrays.asList(textLineByYCoordMap.keySet().toArray(new Integer[0]));
//			Collections.sort(yCoordList);
//			textLineList = new ArrayList<TextLineOld>();
//			int i = 0;
//			textLineSerialMap = new HashMap<TextLineOld, Integer>();
//			for (Integer y : yCoordList) {
//				TextLineOld textLine = textLineByYCoordMap.get(y);
//				textLineList.add(textLine);
//				textLineSerialMap.put(textLine, i++);
//			}
//		}
//		return textLineList;
//	}
//	
//	/** some lines may not have spaces
//	 * 
//	 * @return
//	 */
//	public List<Double> getActualWidthsOfSpaceCharactersList() {
//		if (actualWidthsOfSpaceCharactersList == null) {
//			getLinesInIncreasingY();
//			if (textLineList != null && textLineList.size() > 0) {
//				actualWidthsOfSpaceCharactersList = new ArrayList<Double>();
//				for (int i = 0; i < textLineList.size(); i++) {
//					Double meanWidth = textLineList.get(i).getMeanWidthOfSpaceCharacters();
//					meanWidth = meanWidth == null ? null : Real.normalize(meanWidth, TextAnalyzerXOld.NDEC_FONTSIZE);
//					actualWidthsOfSpaceCharactersList.add(meanWidth);
//				}
//			}
////			actualWidthsOfSpaceCharactersArray.format(NDEC_FONTSIZE);
//		}
//		return actualWidthsOfSpaceCharactersList;
//	}
//
//	private void ensureTextLineByYCoordMap() {
//		if (textLineByYCoordMap == null) {
//			textLineByYCoordMap = new HashMap<Integer, TextLineOld>();
//		}
//	}
//	
//	public Integer getSerialNumber(TextLineOld textLine) {
//		return (textLineSerialMap == null) ? null : textLineSerialMap.get(textLine);
//	}
//	
//
//	public List<String> getTextLineContentList() {
//		textLineContentList = null;
//		if (textLineList != null) {
//			textLineContentList = new ArrayList<String>();
//			for (TextLineOld textLine : textLineList) {
//				textLineContentList.add(textLine.getLineString());
//			}
//		}
//		return textLineContentList;
//	}
//	
//	public List<TextLineOld>getTextLineList() {
//		return textLineList;
//	}
//
//	public void insertSpaces() {
//		if (textLineList != null) {
//			for (TextLineOld textLine : textLineList) {
//				textLine.insertSpaces();
//			}
//		}
//	}
//
//	public void insertSpaces(double scaleFactor) {
//		if (textLineList != null) {
//			for (TextLineOld textLine : textLineList) {
//				textLine.insertSpaces(scaleFactor);
//			}
//		}
//	}
//
//
//	
//	public Set<TextCoordinate> getFontSizeContainerSet() {
//		Set<TextCoordinate> fontSizeContainerSet = new HashSet<TextCoordinate>();
//		if (fontSizeContainerSet != null) {
//			for (TextLineOld textLine : textLineList) {
//				fontSizeContainerSet.addAll(textLine.getFontSizeContainerSet());
//			}
//		}
//		return fontSizeContainerSet;
//	}
//
//	public RealArray getMeanFontSizeArray() {
//		if (meanFontSizeArray == null) {
//			getLinesInIncreasingY();
//			if (textLineList != null && textLineList.size() > 0) {
//				meanFontSizeArray = new RealArray(textLineList.size());
//				for (int i = 0; i < textLineList.size(); i++) {
//					meanFontSizeArray.setElementAt(i, textLineList.get(i).getMeanFontSize());
//				}
//			}
//			meanFontSizeArray.format(TextAnalyzerXOld.NDEC_FONTSIZE);
//		}
//		return meanFontSizeArray;
//	}
//
//	public void setTextLines(List<TextLineOld> textLineList) {
//		if (textLineList != null) {
//			this.textLineList = new ArrayList<TextLineOld>();
//			for (TextLineOld textLine : textLineList) {
//				add(textLine);
//			}
//		}
//	}
//
//	private void add(TextLineOld textLine) {
//		ensureTextLineList();
//		this.textLineList.add(textLine);
//	}
//	
//	private void ensureTextLineList() {
//		if (this.textLineList == null) {
//			this.textLineList = new ArrayList<TextLineOld>();
//		}
//	}
//
//	public List<TextLineOld> getLinesWithLargestFont() {
//		if (linesWithLargestFont == null) {
//			linesWithLargestFont = new ArrayList<TextLineOld>();
//			getLargestFontSize();
//			for (int i = 0; i < textLineList.size(); i++){
//				TextLineOld textLine = textLineList.get(i);
//				Double fontSize = (textLine == null) ? null : textLine.getFontSize();
//				if (fontSize != null) {
//					if (Real.isEqual(fontSize, largestFontSize.getDouble(), 0.001)) {
//						linesWithLargestFont.add( textLine);
//					}
//				}
//			}
//		}
//		return linesWithLargestFont;
//	}
//
//	public List<TextLineOld>getLinesWithCommonestFont() {
//		if (linesWithCommonestFont == null) {
//			linesWithCommonestFont = new ArrayList<TextLineOld>();
//			getCommonestFontSize();
//			for (int i = 0; i < textLineList.size(); i++){
//				TextLineOld textLine = textLineList.get(i);
//				Double fontSize = (textLine == null) ? null : textLine.getFontSize();
//				if (fontSize != null) {
//					if (Real.isEqual(fontSize, commonestFontSize.getDouble(), 0.001)) {
//						linesWithCommonestFont.add( textLine);
//					}
//				}
//			}
//		}
//		return linesWithCommonestFont;
//	}
//
//	public TextCoordinate getCommonestFontSize() {
//		commonestFontSize = null;
//		Map<Double, Integer> fontCountMap = new HashMap<Double, Integer>();
//		for (TextLineOld textLine : textLineList) {
//			Double fontSize = textLine.getFontSize();
//			Integer ntext = textLine.getCharacterList().size();
//			if (fontSize != null) {
//				Integer sum = fontCountMap.get(fontSize);
//				if (sum == null) {
//					sum = ntext;
//				} else {
//					sum += ntext;
//				}
//				fontCountMap.put(fontSize, sum);
//			}
//		}
//		getCommonestFontSize(fontCountMap);
//		return commonestFontSize;
//	}
//
//	private void getCommonestFontSize(Map<Double, Integer> fontCountMap) {
//		int frequency = -1;
//		for (Double fontSize : fontCountMap.keySet()) {
//			int count = fontCountMap.get(fontSize);
//			LOG.trace(">> "+fontSize+" .. "+fontCountMap.get(fontSize));
//			if (commonestFontSize == null || count > frequency) {
//			    commonestFontSize = new TextCoordinate(fontSize);
//			    frequency = count;
//			}
//		}
//		if (commonestFontSize != null) LOG.trace("commonest "+commonestFontSize.getDouble());
//	}
//	
//	public TextCoordinate getLargestFontSize() {
//		largestFontSize = null;
//		Set<TextCoordinate> fontSizes = this.getFontSizeSet();
//		for (TextCoordinate fontSize : fontSizes) {
//			if (largestFontSize == null || largestFontSize.getDouble() < fontSize.getDouble()) {
//				largestFontSize = fontSize;
//			}
//		}
//		return largestFontSize;
//	}
//	
//	public Real2Range getLargestFontBoundingBox() {
//		if (textLinesLargetFontBoundingBox == null) {
//			getLinesWithLargestFont();
//			getBoundingBox(linesWithLargestFont);
//		}
//		return textLinesLargetFontBoundingBox;
//	}
//
//	public static Real2Range getBoundingBox(List<TextLineOld>textLines) {
//		Real2Range boundingBox = null;
//		if (textLines.size() > 0) {
//			boundingBox = new Real2Range(new Real2Range(textLines.get(0).getBoundingBox()));
//			for (int i = 1; i < textLines.size(); i++) {
//				boundingBox.plus(textLines.get(i).getBoundingBox());
//			}
//		}
//		return boundingBox;
//	}
//
//	public Set<TextCoordinate> getFontSizeSet() {
//		if (fontSizeSet == null) {
//			if (textLineList != null) {
//				fontSizeSet = new HashSet<TextCoordinate>();
//				for (TextLineOld textLine : textLineList) {
//					Set<TextCoordinate> textLineFontSizeSet = textLine.getFontSizeSet();
//					fontSizeSet.addAll(textLineFontSizeSet);
//				}
//			}
//		}
//		return fontSizeSet;
//	}
//
//	/** creates a multiset from addAll() on multisets for each line
//	 *  
//	 * @return
//	 */
//	public Multiset<String> getFontFamilyMultiset() {
//		if (fontFamilySet == null) {
//			fontFamilySet = HashMultiset.create();
//			for (TextLineOld textLine : textLineList) {
//				Multiset<String> listFontFamilySet = textLine.getFontFamilyMultiset();
//				fontFamilySet.addAll(listFontFamilySet);
//			}
//		}
//		return fontFamilySet;
//	}
//
//	/** gets commonest font
//	 *  
//	 * @return
//	 */
//	public String getCommonestFontFamily() {
//		getFontFamilyMultiset();
//		String commonestFontFamily = null;
//		int highestCount = -1;
//		Set<String> fontFamilyElementSet = fontFamilySet.elementSet();
//		for (String fontFamily : fontFamilyElementSet) {
//			int count = fontFamilySet.count(fontFamily);
//			if (count > highestCount) {
//				highestCount = count;
//				commonestFontFamily = fontFamily;
//			}
//		}
//		return commonestFontFamily;
//	}
//
//	/** gets commonest font
//	 *  
//	 * @return
//	 */
//	public int getFontFamilyCount() {
//		getFontFamilyMultiset();
//		return fontFamilySet.elementSet().size();
//	}
//
//	/** get non-overlapping boundingBoxes
//	 * @return
//	 */
//	public List<Real2Range> getDiscreteLineBoxes() {
//		List<Real2Range> discreteLineBoxes = new ArrayList<Real2Range>();
////		List<TextLineOld>textLines = this.getLinesSortedByYCoord();
//		return discreteLineBoxes;
//	}
//
//	public RealArray getInterTextLineSeparationArray() {
//		getTextLineCoordinateArray();
//		if (textLineList != null && textLineList.size() > 0) {
//			interTextLineSeparationArray = new RealArray();
//			Double y0 = textLineCoordinateArray.get(0);
//			for (int i = 1; i < textLineCoordinateArray.size(); i++) {
//				Double y = textLineCoordinateArray.get(i);
//				interTextLineSeparationArray.addElement(y - y0);
//				y0 = y;
//			}
//			interTextLineSeparationArray.format(TextAnalyzerXOld.NDEC_FONTSIZE);
//		}
//		return interTextLineSeparationArray;
//	}
//
//	public Multimap<TextCoordinate, TextLineOld> getTextLineListByFontSize() {
//		if (textLineListByFontSize == null) {
//			textLineListByFontSize = ArrayListMultimap.create();
//			for (TextLineOld textLine : textLineList) {
//				Set<TextCoordinate> fontSizeSet = textLine.getFontSizeSet();
//				if (fontSizeSet != null) {
//					for (TextCoordinate fontSize : fontSizeSet) {
//						textLineListByFontSize.put(fontSize, textLine);
//					}
//				}
//			}
//		}
//		return textLineListByFontSize;
//		
//	}
//
//	public Map<Integer, TextLineOld> getTextLineByYCoordMap() {
//		return textLineByYCoordMap;
//	}
//
//	public RealArray getModalExcessWidthArray() {
//		if (modalExcessWidthArray == null) {
//			getLinesInIncreasingY();
//			if (textLineList != null && textLineList.size() > 0) {
//				modalExcessWidthArray = new RealArray(textLineList.size());
//				for (int i = 0; i < textLineList.size(); i++) {
//					Double modalExcessWidth = textLineList.get(i).getModalExcessWidth();
//					modalExcessWidthArray.setElementAt(i, modalExcessWidth);
//				}
//			}
//			modalExcessWidthArray.format(TextAnalyzerXOld.NDEC_FONTSIZE);
//		}
//		return modalExcessWidthArray;
//	}
//
//	public Multiset<Double> createSeparationSet(int decimalPlaces) {
//		getInterTextLineSeparationArray();
//		interTextLineSeparationArray.format(decimalPlaces);
//		separationSet = HashMultiset.create();
//		for (int i = 0; i < interTextLineSeparationArray.size(); i++) {
//			separationSet.add(interTextLineSeparationArray.get(i));
//		}
//		return separationSet;
//	}
//
//	public Double getMainInterTextLineSeparation(int decimalPlaces) {
//		Double mainTextLineSeparation = null;
//		createSeparationSet(decimalPlaces);
//		Set<Entry<Double>> ddSet = separationSet.entrySet();
//		Entry<Double> maxCountEntry = null;
//		Entry<Double> maxSeparationEntry = null;
//		for (Entry<Double> dd : ddSet) {
//			if (maxCountEntry == null || maxCountEntry.getCount() < dd.getCount()) {
//				maxCountEntry = dd;
//			}
//			if (maxSeparationEntry == null || maxSeparationEntry.getElement() < dd.getElement()) {
//				maxSeparationEntry = dd;
//			}
//		}
//		if (maxCountEntry.equals(maxSeparationEntry)) {
//			mainTextLineSeparation = maxSeparationEntry.getElement();
//		}
//		return mainTextLineSeparation;
//	}
//
//	public void sortLineByXandMakeTextLineByYCoordMap(List<SVGText> textCharacters) {
//		if (textLineByYCoordMap == null) {
//			textLineByYCoordMap = new HashMap<Integer, TextLineOld>();
//			Multimap<Integer, SVGText> charactersByY = TextAnalyzerUtils.createCharactersByY(textCharacters);
//			for (Integer yCoord : charactersByY.keySet()) {
//				Collection<SVGText> characters = charactersByY.get(yCoord);
//				TextLineOld textLine = new TextLineOld(characters, this.textAnalyzer);
//				textLine.sortLineByX();
//				textLineByYCoordMap.put(yCoord, textLine);
//			}
//		}
//	}
//
//	public RealArray getTextLineCoordinateArray() {
//		if (textLineCoordinateArray == null) {
//			getLinesInIncreasingY();
//			if (textLineList != null && textLineList.size() > 0) {
//				textLineCoordinateArray = new RealArray();
//				for (TextLineOld textLine : textLineList) {
//					Double y0 = textLine.getYCoord();
//					textLineCoordinateArray.addElement(y0);
//				}
//			}
//			textLineCoordinateArray.format(TextAnalyzerXOld.NDEC_FONTSIZE);
//		}
//		return textLineCoordinateArray;
//	}
//
//	/** finds maximum indent of lines
//	 * must be at least 2 lines
//	 * currently does not check for capitals, etc.
//	 * 
//	 */
//	public Double getMaxiumumRightIndent() {
//		Double indent = null;
//		Double xRight = null;
//		if (textLineList != null && textLineList.size() > 1) {
//			for (TextLineOld textLine : textLineList) {
//				Double xLast = textLine.getLastXCoordinate();
//				if (xRight == null) {
//					xRight = xLast;
//				}
//				if (xRight - xLast > TextAnalyzerXOld.INDENT_MIN) {
//					indent = xLast;
//				} else if (xLast - xRight > TextAnalyzerXOld.INDENT_MIN) {
//					indent = xRight;
//				}
//			}
//		}
//		return indent;
//	}
//
//	public TextLineOldSet getTextLineSetByFontSize(double fontSize) {
//		Multimap<TextCoordinate, TextLineOld> textLineListByFontSize = this.getTextLineListByFontSize();
//		List<TextLineOld>textLines = (List<TextLineOld>) textLineListByFontSize.get(new TextCoordinate(fontSize));
//		return new TextLineOldSet(textLines);
//	}
//
//	public List<ScriptLine> getInitialScriptLineList() {
//		getTextLineChunkBoxesAndInitialiScriptLineList();
//		return initialScriptLineList;
//	}
//
//	/**
//	 * This is heuristic. At present it is font-size equality. Font families
//	 * are suspect as there are "synonyms", e.g. TimesRoman and TimesNR
//	 * 
//	 * @return
//	 */
//	public List<TextLineOld>getCommonestFontSizeTextLineList() {
//		if (commonestFontSizeTextLineList == null) {
//			TextCoordinate commonestFontSize = getCommonestFontSize();
//			Double commonestFontSizeValue = (commonestFontSize == null) ?
//					null : commonestFontSize.getDouble();
//			commonestFontSizeTextLineList = new ArrayList<TextLineOld>();
//			for (TextLineOld textLine : textLineList) {
//				Double fontSize = textLine.getCommonestFontSize();
//				if (fontSize != null && Real.isEqual(fontSize, commonestFontSizeValue, 0.01)) {
//					commonestFontSizeTextLineList.add(textLine);
//					LOG.trace("COMMONEST FONT SIZE "+textLine);
//				}
//			}
//		}
//		return commonestFontSizeTextLineList;
//	}
//
//	public List<ScriptLine> getScriptedLineList() {
//		if (scriptedLineList == null) {
//			commonestFontSizeTextLineList = getCommonestFontSizeTextLineList();
//			for (TextLineOld textLine : commonestFontSizeTextLineList) {
//				LOG.trace("COMMONTL "+textLine);
//			}
//			initialScriptLineList = getInitialScriptLineList();
//			scriptedLineList = new ArrayList<ScriptLine>();
//			int i = 0;
//			for (ScriptLine textLineGroup : initialScriptLineList) {
//				List<ScriptLine> splitChunks = textLineGroup.splitIntoUniqueChunks((TextStructurer)null/*this*/);
//				for (ScriptLine textLineChunk0 : splitChunks) {
//					scriptedLineList.add(textLineChunk0);
//				}
//				i++;
//			}
//		}
//		LOG.trace("separated "+scriptedLineList.size());
//		return scriptedLineList;
//	}
//
//	public List<Real2Range> getTextLineChunkBoxesAndInitialiScriptLineList() {
//		if (textLineChunkBoxes == null) {
//			List<TextLineOld>textLineList = getLinesInIncreasingY();
//			textLineList = mergeLinesWithSameY(textLineList, Y_EPS);
//			textLineChunkBoxes = new ArrayList<Real2Range>();
//			Real2Range bbox = null;
//			ScriptLine scriptLine = null;
//			int i = 0;
//			initialScriptLineList = new ArrayList<ScriptLine>();
//			for (TextLineOld textLine : textLineList) {
//				Real2Range bbox0 = textLine.getBoundingBox();
//				LOG.trace("TL>> "+textLine.getLineString());
//				if (bbox == null) {
//					bbox = bbox0;
//					scriptLine = new ScriptLine((TextStructurer)null/*this*/);
//					addBoxAndScriptLines(bbox, scriptLine);
//				} else {
//					Real2Range intersectionBox = bbox.intersectionWith(bbox0);
//					if (intersectionBox == null) {
//						bbox = bbox0;
//						scriptLine = new ScriptLine((TextStructurer)null/*this*/);
//						addBoxAndScriptLines(bbox, scriptLine);
//					} else {
//						bbox = bbox.plusEquals(bbox0);
//					}
//				}
//				
//				scriptLine.add((TextLine)null/*textLine*/);
//				LOG.trace("SL >>"+scriptLine);
//			}
//		}
//		return textLineChunkBoxes;
//	}
//
//	private List<TextLineOld>mergeLinesWithSameY(List<TextLineOld>textLineList, Double yEps) {
//		List<TextLineOld>newTextLineList = new ArrayList<TextLineOld>();
//		TextLineOld lastTextLine = null;
//		Double lastY = null;
//		for (TextLineOld textLine : textLineList) {
//			Double y = (textLine == null) ? null : textLine.getYCoord();
//			// lines with same Y?
//			if (lastTextLine != null && Real.isEqual(lastY, y, yEps)) {
//				lastTextLine.merge(textLine);
//			} else {
//				newTextLineList.add(textLine);
//				lastTextLine = textLine;
//				lastY = y;
//			}
//		}
//		return newTextLineList;
//	}
//
//	private void addBoxAndScriptLines(Real2Range bbox, ScriptLine scriptLine) {
//		textLineChunkBoxes.add(bbox);
//		initialScriptLineList.add(scriptLine);
//	}
//
//	public static TextStructurerOld createTextStructurerWithSortedLines(File svgFile) {
//		SVGElement svgChunk = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
//		List<SVGText> textCharacters = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgChunk, ".//svg:text"));
//		TextStructurerOld textStructurer = createTextStructurerWithSortedLines(textCharacters);
//		textStructurer.setSvgChunk(svgChunk);
//		return textStructurer;
//	}
//
//	private void setSvgChunk(SVGElement svgChunk) {
//		this.svgChunk = svgChunk;
//	}
//
//	public static TextStructurerOld createTextStructurerWithSortedLines(List<SVGText> textCharacters, TextAnalyzerXOld textAnalyzer) {
//		TextStructurerOld textStructurer = new TextStructurerOld(textAnalyzer);
//		textStructurer.createLinesSortedInXThenY(textCharacters, textAnalyzer);
//		return textStructurer;
//	}
//
//	private void createLinesSortedInXThenY(List<SVGText> textCharacters,
//			TextAnalyzerXOld textAnalyzer) {
//		this.sortLineByXandMakeTextLineByYCoordMap(textCharacters);
//		textLineList = this.getLinesInIncreasingY();
//		for (TextLineOld textLine : textLineList) {
//			LOG.trace("TL "+textLine);
//		}
//		if (false) {
//			textAnalyzer.setTextCharacters(textCharacters);
//		}
//		textAnalyzer.setTextStructurer(this);
//	}
//	
//	public static TextStructurerOld createTextStructurerWithSortedLines(List<SVGText> textCharacters) {
//		TextAnalyzerXOld textAnalyzer = new TextAnalyzerXOld();
//		textAnalyzer.setTextCharacters(textCharacters);
//		TextStructurerOld textStructurer = new TextStructurerOld(textAnalyzer);
//		// the next two lines may be unnecessary
//		textStructurer.sortLineByXandMakeTextLineByYCoordMap(textCharacters);
//		List<TextLineOld>textLineList = textStructurer.getLinesInIncreasingY(); 
//		for (TextLineOld textLine : textLineList) {
//			LOG.trace("TLY "+textLine);
//		}
//		textAnalyzer.setTextStructurer(textStructurer);
//		return textStructurer;
//	}
//	
//	public TextAnalyzerXOld getTextAnalyzer() {
//		return textAnalyzer;
//	}
//
//	/** finds maximum indent of lines
//	 * must be at least 2 lines
//	 * currently does not check for capitals, etc.
//	 * 
//	 */
//	public Double getMaximumLeftIndentForLargestFont() {
//		Double indent = null;
//		Double xLeft = null;
//		List<TextLineOld>textLineListWithLargestFont = this.getLinesWithCommonestFont();
//		if (textLineListWithLargestFont != null && textLineListWithLargestFont.size() > 1) {
//			for (TextLineOld textLine : textLineListWithLargestFont) {
//				Double xStart = textLine.getFirstXCoordinate();
//				if (xStart == null) {
//					throw new RuntimeException("null start");
//				}
//				if (xLeft == null) {
//					xLeft = xStart;
//				}
//				if (xLeft - xStart > TextAnalyzerXOld.INDENT_MIN) {
//					indent = xLeft;
//				} else if (xStart - xLeft > TextAnalyzerXOld.INDENT_MIN) {
//					indent = xStart;
//				}
//			}
//		}
//		return indent;
//	}
//
//	/** finds maximum indent of lines
//	 * must be at least 2 lines
//	 * currently does not check for capitals, etc.
//	 * 
//	 */
//	public static Double getMaximumLeftIndent(List<TextLineOld>textLineList) {
//		Double indent = null;
//		Double xLeft = null;
//		if (textLineList != null && textLineList.size() > 1) {
//			for (TextLineOld textLine : textLineList) {
//				Double xStart = textLine.getFirstXCoordinate();
//				if (xStart == null) {
//					throw new RuntimeException("null start");
//				}
//				if (xLeft == null) {
//					xLeft = xStart;
//				}
//				if (xLeft - xStart > TextAnalyzerXOld.INDENT_MIN) {
//					indent = xLeft;
//				} else if (xStart - xLeft > TextAnalyzerXOld.INDENT_MIN) {
//					indent = xStart;
//				}
//			}
//		}
//		return indent;
//	}
//
//	public static List<TextLineOld> createTextLineList(File svgFile) {
//		TextStructurerOld textStructurer = createTextStructurerWithSortedLines(svgFile);
//		List<TextLineOld> textLineList = textStructurer.getLinesInIncreasingY();
//		return textLineList;
//	}
//
//	public static PageChunkAnalyzer createTextAnalyzerWithSortedLines(List<SVGText> characters) {
//			TextAnalyzerXOld textAnalyzer = new TextAnalyzerXOld();
//			/*TextStructurer textStructurer = */TextStructurerOld.createTextStructurerWithSortedLines(characters, textAnalyzer);
//			return textAnalyzer;
//	}
//
//
//	public static HtmlElement createHtmlDiv(List<ScriptLine> textLineGroupList) {
//		HtmlDiv div = new HtmlDiv();
//		for (ScriptLine group : textLineGroupList) {
//			HtmlElement el = null;
//			if (group == null) {
////				el = new HtmlP();
////				el.appendChild("PROBLEM");
////				div.appendChild(el);
////				div.debug("XXXXXXXXXXXXXXXXXX");
//			} else {
//				el = group.createHtmlElement();
//				div.appendChild(el);
//			}
//		}
//		return div;
//	}
//
//	public HtmlElement createHtmlDivWithParas() {
//		List<ScriptLine> textLineGroupList = this.getScriptedLineList();
//		LOG.trace("TEXTLINEGROUP splt heres "+textLineGroupList);
//		if (textLineGroupList.size() == 0) {
//			LOG.trace("TextLineList: "+textLineList);
//			// debug
//		}
//		boolean bb = false;
//		createHtmlElementWithParas(textLineGroupList);
//		return createdHtmlElement;
//	}
//
//	/** only used in tests?
//	 * 
//	 * @param textLineGroupList
//	 * @return
//	 */
//	 public HtmlElement createHtmlElementWithParas(List<ScriptLine> textLineGroupList) {
//		List<TextLineOld>commonestTextLineList = this.getCommonestFontSizeTextLineList();
//		createdHtmlElement = null;
//		if (commonestTextLineList.size() == 0){
//			 createdHtmlElement = null;
//		} else if (commonestTextLineList.size() == 1){
//			 createdHtmlElement = commonestTextLineList.get(0).createHtmlLine();
//		} else {
//			HtmlElement rawDiv = createHtmlDiv(textLineGroupList);
//			createdHtmlElement = createDivWithParas(commonestTextLineList, rawDiv);
//		}
//		return createdHtmlElement;
//	}
//
//	private HtmlDiv createDivWithParas(List<TextLineOld>textLineList, HtmlElement rawDiv) {
//		HtmlDiv div = null;
//		Double leftIndent = TextStructurerOld.getMaximumLeftIndent(textLineList);
//		Real2Range leftBB = TextStructurerOld.getBoundingBox(textLineList);
//		Elements htmlLines = rawDiv.getChildElements();
//		LOG.trace("textLine "+textLineList.size()+"; html: "+ htmlLines.size());
//		
//		if (leftBB != null) {
//			Double deltaLeftIndent = (leftIndent == null) ? 0 : (leftIndent - leftBB.getXMin());
//			Real2Range largestFontBB = TextStructurerOld.getBoundingBox(textLineList);
//			if (largestFontBB != null) {
//				RealRange xRange = largestFontBB.getXRange();
//				Double indentBoundary = largestFontBB.getXMin() + deltaLeftIndent/2.0;
//				LOG.trace("left, delta, boundary "+leftIndent+"; "+deltaLeftIndent+"; "+indentBoundary);
//				div = new HtmlDiv();
//				// always start with para
//				HtmlP pCurrent = (htmlLines.size() == 0) ? null : 
//					TextStructurerOld.createAndAddNewPara(div, (HtmlP) htmlLines.get(0));
//				int size = htmlLines.size();
//				for (int i = 1; i < size/*textLineList.size()*/; i++) {
//					TextLineOld textLine = (textLineList.size() <= i) ? null : textLineList.get(i);
//					LOG.trace(">"+i+"> "+textLine);
//					HtmlP pNext = i < htmlLines.size() ? (HtmlP) HtmlElement.create(htmlLines.get(i)) : null;
//					// indent, create new para
//					if (pNext == null) {
//						LOG.error("Skipping HTML "+pCurrent+" // "+textLine);
//					} else if (textLine != null && textLine.getFirstXCoordinate() > indentBoundary) {
//						pCurrent = createAndAddNewPara(div, pNext);
//					} else {
//						mergeParas(pCurrent, pNext);
//					}
//				}
//			}
//		}
//		return div;
//	}
//	
//	public static HtmlP createAndAddNewPara(HtmlElement div, HtmlP p) {
//		HtmlP pNew = (HtmlP) HtmlElement.create(p);
//		div.appendChild(pNew);
//		return pNew;
//	}
//
//	public static void mergeParas(HtmlP pCurrent, HtmlP pNext) {
//		Elements currentChildren = pCurrent.getChildElements();
//		if (currentChildren.size() > 0) {
//			HtmlElement lastCurrent = (HtmlElement) currentChildren.get(currentChildren.size() - 1);
//			HtmlSpan currentLastSpan = (lastCurrent instanceof HtmlSpan) ? (HtmlSpan) lastCurrent : null;
//			Elements nextChildren = pNext.getChildElements();
//			HtmlElement firstNext = nextChildren.size() == 0 ? null : (HtmlElement) nextChildren.get(0);
//			HtmlSpan nextFirstSpan = (firstNext != null && firstNext instanceof HtmlSpan) ? (HtmlSpan) firstNext : null;
//			int nextCounter = 0;
//			// merge texts
//			if (currentLastSpan != null && nextFirstSpan != null) {
//				String mergedText = mergeLineText(currentLastSpan.getValue(), nextFirstSpan.getValue());
//				LOG.trace("Merged "+mergedText);
//				lastCurrent.setValue(mergedText);
//				nextCounter = 1;
//			}
//			//merge next line's children
//			for (int i = nextCounter; i < nextChildren.size(); i++) {
//				pCurrent.appendChild(HtmlElement.create(nextChildren.get(i)));
//			}
//		}
//	}
//
//	private static String mergeLineText(String last, String next) {
//		//merge hyphen minus
//		if (last.endsWith("-")) {
//			return last.substring(0, last.length()-1) + next;
//		} else {
//			return last + " " + next;
//		}
//	}
//
//	public boolean endsWithRaggedLine() {
//		return createdHtmlElement != null &&
//				!createdHtmlElement.getValue().endsWith(".");
//	}
//
//	public boolean startsWithRaggedLine() {
//		boolean starts = false;
//		if (createdHtmlElement != null && createdHtmlElement.getValue().length() > 0) {
//			Character c = createdHtmlElement.getValue().charAt(0);
//			if (c != null) {
//				starts = !Character.isUpperCase(c);
//			}
//		}
//		return starts;
//	}
//
//	public boolean lineIsLargerThanCommonestFontSize(int lineNumber) {
//		TextLineOld textLine = (lineNumber < 0 || lineNumber >= textLineList.size()) ?
//				null : textLineList.get(lineNumber);
//		return lineIsLargerThanCommonestFontSize(textLine);
//	}
//
//	public boolean lineIsLargerThanCommonestFontSize(TextLineOld textLine) {
//		boolean isLargerThan = false;
//		Double commonestFontSize = getCommonestFontSize().getDouble();
//		if (textLine != null && commonestFontSize != null) {
//			Double fontSize = textLine.getFontSize();
//			if (fontSize != null) {
//				isLargerThan = fontSize / commonestFontSize > LARGER_FONT_SIZE_RATIO;
//			}
//		}
//		return isLargerThan;
//	}
//
//	public boolean isCommonestFontSize(TextLineOld textLine) {
//		this.getCommonestFontSizeTextLineList();
//		return textLine != null && commonestFontSizeTextLineList.contains(textLine);
//	}
//
//	public boolean isCommonestFontSize(ScriptLine textLineGroup) {
//		this.getCommonestFontSizeTextLineList();
////		return textLineGroup != null && commonestFontSizeTextLineList.contains(largestLine);
//		return false;
//	}
//
//	public boolean lineGroupIsLargerThanCommonestFontSize(int lineNumber) {
//		TextLineOld textLine = (lineNumber < 0 || lineNumber >= textLineList.size()) ?
//				null : textLineList.get(lineNumber);
//		return lineIsLargerThanCommonestFontSize(textLine);
//	}
//
//	public boolean lineGroupIsLargerThanCommonestFontSize(ScriptLine textLineGroup) {
//		boolean isLargerThan = false;
//		Double commonestFontSize = getCommonestFontSize().getDouble();
//		if (textLineGroup != null && commonestFontSize != null) {
//			Double fontSize = textLineGroup.getFontSize();
//			if (fontSize != null) {
//				isLargerThan = fontSize / commonestFontSize > LARGER_FONT_SIZE_RATIO;
//			}
//		}
//		return isLargerThan;
//	}
//
//	
//	/** split after line where font size changes to/from bigger than commonest
//	 * dangerous if there are sub or superscripts (use splitGroupBiggerThanCommonest)
//	 * @return
//	 */
//	public IntArray splitBiggerThanCommonest() {
//		Double commonestFontSize = this.getCommonestFontSize().getDouble();
//		IntArray splitArray = new IntArray();
//		for (int i = 0; i < textLineList.size() - 1; i++) {
//			TextLineOld textLineA = textLineList.get(i);
//			Double fontSizeA = textLineA.getFontSize();
//			TextLineOld textLineB = textLineList.get(i+1);
//			Double fontSizeB = textLineB.getFontSize();
//			if (fontSizeA != null && fontSizeB != null) {
//				double ratioAB = fontSizeA / fontSizeB;
//				// line increases beyond commonest size?
//				if (Real.isEqual(fontSizeA, commonestFontSize, 0.01) 
//						&& ratioAB < 1./LARGER_FONT_SIZE_RATIO) {
//					splitArray.addElement(i);
//				} else if (Real.isEqual(fontSizeB, commonestFontSize, 0.01) 
//						&& ratioAB > LARGER_FONT_SIZE_RATIO) {
//					splitArray.addElement(i);
//				}
//			}
//		}
//		return splitArray;
//	}
//
//	
//	/** split after textLineGroup where font size changes to/from bigger than commonest
//	 * 
//	 * @return
//	 */
//	public IntArray splitGroupBiggerThanCommonest() {
//		getScriptedLineList();
//		Double commonestFontSize = this.getCommonestFontSize().getDouble();
//		IntArray splitArray = new IntArray();
//		for (int i = 0; i < scriptedLineList.size() - 1; i++) {
//			ScriptLine textLineGroupA = scriptedLineList.get(i);
//			Double fontSizeA = textLineGroupA.getFontSize();
//			ScriptLine textLineB = scriptedLineList.get(i+1);
//			Double fontSizeB = textLineB.getFontSize();
//			if (fontSizeA != null && fontSizeB != null) {
//				double ratioAB = fontSizeA / fontSizeB;
//				// line increases beyond commonest size?
//				if (Real.isEqual(fontSizeA, commonestFontSize, 0.01) 
//						&& ratioAB < 1./LARGER_FONT_SIZE_RATIO) {
//					splitArray.addElement(i);
//				} else if (Real.isEqual(fontSizeB, commonestFontSize, 0.01) 
//						&& ratioAB > LARGER_FONT_SIZE_RATIO) {
//					splitArray.addElement(i);
//				}
//			}
//		}
//		return splitArray;
//	}
//
//	/** split the textStructurer after the lines in array.
//	 * if null or size() == 0, returns list with 'this'. so a returned list of size 0
//	 * effectively does nothing
//	 * @param afterLineGroups if null or size() == 0, returns list with 'this';
//	 * @return
//	 */
//	public List<TextStructurerOld> splitLineGroupsAfter(IntArray afterLineGroups) {
//		getScriptedLineList();
//		List<TextStructurerOld> textStructurerList = new ArrayList<TextStructurerOld>();
//		if (afterLineGroups == null || afterLineGroups.size() == 0) {
//			textStructurerList.add(this);
//		} else {
//			int start = 0;
//			for (int i = 0; i < afterLineGroups.size();i++) {
//				int lineNumber = afterLineGroups.elementAt(i);
//				if (lineNumber > scriptedLineList.size() -1) {
//					throw new RuntimeException("bad index: "+lineNumber);
//				}
//				TextStructurerOld newTextStructurer = createTextStructurerFromTextLineGroups(start, lineNumber);
//				textStructurerList.add(newTextStructurer);
//				start = lineNumber + 1;
//			}
//			TextStructurerOld newTextStructurer = createTextStructurerFromTextLineGroups(start, scriptedLineList.size() - 1);
//			textStructurerList.add(newTextStructurer);
//		}
//		return textStructurerList;
//	}
//
//	private TextStructurerOld createTextStructurerFromTextLineGroups(int startLineGroup, int lineGroupNumber) {
//		getScriptedLineList();
//		TextStructurerOld textStructurer = new TextStructurerOld(null);
//		textStructurer.textAnalyzer = this.textAnalyzer;
//		for (int iGroup = startLineGroup; iGroup <= lineGroupNumber; iGroup++) {
//			ScriptLine textLineGroup = scriptedLineList.get(iGroup);
//			if (textLineGroup != null) {
//				/**
//				List<TextLineOld>textLineList = textLineGroup.getTextLineList();
//				textStructurer.add(textLineList);
//				*/
//			}
//		}
//		return textStructurer;
//	}
//
//	private void add(List<TextLineOld>textLineList) {
//		for (TextLineOld textLine : textLineList) {
//			this.add(textLine);
//		}
//	}
//
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		if (textLineList == null) {
//			sb.append("null");
//		} else {
//			sb.append("TextStructurer: "+ textLineList.size());
//			for (TextLineOld textLine :textLineList) {
//				sb.append(textLine.toString()+"\n");
//			}
//		}
//		return sb.toString();
//	}
//
//	public List<TextStructurerOld> split(Splitter splitter) {
//		if (Splitter.BOLD.equals(splitter)) {
//			return splitOnFontBoldChange(0);
//		}
//		if (Splitter.FONTSIZE.equals(splitter)) {
//			return splitOnFontSizeChange(0);
//		}
//		if (Splitter.FONTFAMILY.equals(splitter)) {
//			return splitOnFontFamilyChange(0);
//		}
//		throw new RuntimeException("Unknown splitter: "+splitter);
//	}
//
//	/** splits bold line(s) from succeeeding ones.
//	 * may trap smaller headers - must catch this later
//	 * @return
//	 */
//	public List<TextStructurerOld> splitOnFontBoldChange(int maxFlip) {
//		IntArray splitter = getSplitArrayForFontWeightChange(maxFlip);
//		LOG.trace("SPLIT "+splitter);
//		return splitIntoList(splitter);
//	}
//	
//	/** splits line(s) on fontSize.
//	 * @return
//	 */
//	public List<TextStructurerOld> splitOnFontSizeChange(int maxFlip) {
//		IntArray splitter = getSplitArrayForFontSizeChange(maxFlip);
//		return splitIntoList(splitter);
//	}
//
//	/** splits line(s) on fontFamily.
//	 * @return
//	 */
//	public List<TextStructurerOld> splitOnFontFamilyChange(int maxFlip) {
//		IntArray splitter = getSplitArrayForFontFamilyChange(maxFlip);
//		return splitIntoList(splitter);
//	}
//
//	/** splits line(s) on fontSize.
//	 * @return
//	 */
//	public IntArray getSplitArrayForFontWeightChange(int maxFlip) {
//		getScriptedLineList();
//		Boolean currentBold = null;
//		IntArray splitArray = new IntArray();
//		if (scriptedLineList.size() > 0) {
//			int nFlip = 0;
//			for (int i = 0; i < scriptedLineList.size(); i++) {
//				ScriptLine scriptLine = scriptedLineList.get(i);
//				Boolean isBold = (scriptLine == null) ? null : scriptLine.isBold();
//				if (currentBold == null) { 
//					currentBold = isBold;
//					// insist on leading bold
//					if (maxFlip < 0 && !isBold) {
//						return splitArray;
//					}
//				} else if (!currentBold.equals(isBold)) {
//					splitArray.addElement(i - 1);
//					currentBold = isBold;
//					if (nFlip++ >= maxFlip) break;
//				}
//			}
//		}
//		return splitArray;
//	}
//	
//	
//	/** splits line(s) on fontSize.
//	 * @return
//	 */
//	public IntArray getSplitArrayForFontSizeChange(int maxFlip) {
//		double EPS = 0.01;
//		getScriptedLineList();
//		Double currentFontSize = null;
//		IntArray splitArray = new IntArray();
//		if (scriptedLineList.size() > 0) {
//			int nFlip = 0;
//			for (int i = 0; i < scriptedLineList.size(); i++) {
//				Double fontSize = scriptedLineList.get(i).getFontSize();
//				if (currentFontSize == null) {
//					currentFontSize = fontSize;
//				} else if (!Real.isEqual(fontSize, currentFontSize, EPS)) {
//					splitArray.addElement(i - 1);
//					currentFontSize = fontSize;
//					if (nFlip++ >= maxFlip) break;
//				}
//			}
//		}
//		return splitArray;
//	}
//	
//	/** splits line(s) on fontSize.
//	 * @return
//	 */
//	public IntArray getSplitArrayForFontFamilyChange(int maxFlip) {
//		getScriptedLineList();
//		String currentFontFamily = null;
//		IntArray splitArray = new IntArray();
//		if (scriptedLineList.size() > 0) {
//			int nFlip = 0;
//			for (int i = 0; i < scriptedLineList.size(); i++) {
//				String fontFamily = scriptedLineList.get(i).getFontFamily();
//				if (currentFontFamily == null) {
//					currentFontFamily = fontFamily;
//				} else if (!fontFamily.equals(currentFontFamily)) {
//					splitArray.addElement(i - 1);
//					currentFontFamily = fontFamily;
//					if (nFlip++ >= maxFlip) break;
//				}
//			}
//		}
//		return splitArray;
//	}
//
//	private List<TextStructurerOld> splitIntoList(IntArray splitter) {
//		List<TextStructurerOld> splitList = null;
//		if (splitter != null && splitter.size() != 0) {
//			splitList = this.splitLineGroupsAfter(splitter);
//		}  else {
//			splitList = new ArrayList<TextStructurerOld>();
//			splitList.add(this);
//		}
//		return splitList;
//	}
//
//	public SVGG oldCreateSVGGChunk() {
//		SVGG g = new SVGG();
//		for (TextLineOld textLine : textLineList) {
//			for (SVGText text : textLine) {
//				g.appendChild(new SVGText(text));
//			}
//		}
//		return g;
//	}
//
//	/** attempts to split into numbered list by line starts.
//	 * 
//	 * @return
//	 */
//	public List<TextStructurerOld> splitNumberedList() {
//		getScriptedLineList();
//		List<TextStructurerOld> splitLineGroups = new ArrayList<TextStructurerOld>();
//		int last = 0;
//		for (int i = 0; i < scriptedLineList.size(); i++) {
//			ScriptLine tlg = scriptedLineList.get(i);
//			String value = tlg.getRawValue();
//			LOG.trace(value);
//			Matcher matcher = NUMBER_ITEM_PATTERN.matcher(value);
//			if (matcher.matches()) {
//				Integer serial = Integer.parseInt(matcher.group(1));
//				LOG.trace(">> "+serial);
//				addTextLineGroups(splitLineGroups, last, i);
//				last = i;
//				LOG.trace("split: "+i);
//			}
//		}
//		addTextLineGroups(splitLineGroups, last, scriptedLineList.size());
//		return splitLineGroups;
//	}
//
//	private void addTextLineGroups(List<TextStructurerOld> splitLineGroups, int last, int next) {
//		if (next > last) {
//			TextStructurerOld tc = new TextStructurerOld(null);
//			splitLineGroups.add(tc);
//			for (int j = last; j < next; j++) {
//				tc.add(scriptedLineList.get(j));
//			}
//		}
//	}
//
//	private void add(ScriptLine textLineGroup) {
//		ensureScriptedLineList();
//		scriptedLineList.add(textLineGroup);
//		// just to compile
////		for (TextLineOld textLine : textLineGroup) {
////			this.add(textLine);
////		}
//	}
//
//	private List<ScriptLine> ensureScriptedLineList() {
//		if (scriptedLineList == null) {
//			scriptedLineList = new ArrayList<ScriptLine>();
//		}
//		return scriptedLineList;
//	}
//
//	public ChunkId getChunkId() {
//		return (textAnalyzer == null) ? null : textAnalyzer.getChunkId(); 
//	}
//
//	public SVGElement getSVGChunk() {
//		return svgChunk;
//	}
//	
//	public Real2Range ensureBoundingBox() {
//		if (boundingBox == null) {
//			if (svgChunk != null) {
//				boundingBox = svgChunk.getBoundingBox();
//			}
//		}
//		return boundingBox;
//	}
//	
//	public Real2Range getBoundingBox() {
//		ensureBoundingBox();
//		return boundingBox;
//	}
//	
//	public RealRange getXRange() {
//		ensureBoundingBox();
//		return boundingBox == null ? null : boundingBox.getXRange();
//	}
//	
//	public RealRange getYRange() {
//		ensureBoundingBox();
//		return boundingBox == null ? null : boundingBox.getYRange();
//	}
}
