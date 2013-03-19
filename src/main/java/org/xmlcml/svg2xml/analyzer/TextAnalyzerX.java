package org.xmlcml.svg2xml.analyzer;


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

import nu.xom.Attribute;
import nu.xom.Elements;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.EuclidConstants;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Univariate;
import org.xmlcml.graphics.svg.SVGConstants;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGTSpan;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.text.Paragraph;
import org.xmlcml.svg2xml.text.SimpleFont;
import org.xmlcml.svg2xml.text.SvgPlusCoordinate;
import org.xmlcml.svg2xml.text.TextChunk;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextLineContainer;
import org.xmlcml.svg2xml.text.TextLineSet;
import org.xmlcml.svg2xml.text.TypedNumber;
import org.xmlcml.svg2xml.text.TypedNumberList;
import org.xmlcml.svg2xml.text.Word;
import org.xmlcml.svg2xml.text.WordSequence;
import org.xmlcml.svg2xml.tools.BoundingBoxManager;
import org.xmlcml.svg2xml.tools.BoundingBoxManager.BoxEdge;
import org.xmlcml.svg2xml.tools.Chunk;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** attempts to assemble characters into meaningful text
 * 
 * @author pm286
 *
 */
public class TextAnalyzerX extends AbstractPageAnalyzerX {

	private final static Logger LOG = Logger.getLogger(TextAnalyzerX.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TEXT1 = "text1";
	public static final String CHUNK = "chunk";
	public static final String DATA_TYPE = "dataType";
	private static final Double INDENT = 6.0; // appears to be about 1.5 char widths for BMC
	public static final String TEXT = "TEXT";
	private static final String WORD_LIST = "wordList";
	public static final String NUMBER = "number";
	public static final String NUMBERS = "numbers";
	private static final String PREVIOUS = "previous";
	private static final double YPARA_SEPARATION_FACTOR = 1.15;
	
	public static final double DEFAULT_TEXTWIDTH_FACTOR = 0.9;
	private static final double Y_SCALE = 10;
	public static final int NDEC_FONTSIZE = 3;
	private static final double INDENT_MIN = 1.0; //pixels
	public static Double TEXT_EPS = 1.0;


	private TextLine rawCharacterList;
	private Map<Integer, TextLine> characterByXCoordMap;
	@Deprecated
	private Map<Integer, TextLine> characterByYCoordMap;
	private Map<Integer, TextLine> textLineByYCoordMap;
	private Map<String, RealArray> widthByCharacter;
	private Multimap<Integer, TextLine> subLineByYCoord;
	private List<TextLine> horizontalCharacterList;
	private List<WordSequence> wordSequenceList;
	private List<Real2Range> subLineBBoxList;
	private BoundingBoxManager boundingBoxManager;

	private Map<String, Double> medianWidthOfCharacterNormalizedByFontMap;
	private RealArray spaceSizes;

	public List<Real2Range> emptyYTextBoxes;
	public List<Real2Range> emptyXTextBoxes;

	private Chunk chunk;
	private SVGElement svgParent;
	private TextChunk textChunk;
	private List<Paragraph> paragraphList;

	private List<Chunk> textChunkList;
	// refactor
	private boolean createTSpans;
	// refactor
	private boolean createHTML;
	private SimpleFont simpleFont;
	
	private boolean subSup;
	private boolean removeNumericTSpans;
	private boolean splitAtSpaces;
	private List<TextLine> textLineList;
	private RealArray meanFontSizeArray;
	private List<Double> actualWidthsOfSpaceCharactersList;
	private List<String> textLineContentList;
	private RealArray modalExcessWidthArray;
//	private Set<SvgPlusCoordinate> fontSizeSet;
	private Multimap<SvgPlusCoordinate, TextLine> textLineListByFontSize;
	private RealArray textLineCoordinateArray;
	private RealArray interTextLineSeparationArray;
	private Multiset<Double> separationSet;
//X	private SvgPlusCoordinate largestFontSize;
//X	private List<TextLine> linesWithLargestFont;
	private Map<TextLine, Integer> textLineSerialMap;
//	private Real2Range textLinesLargetFontBoundingBox;
//	private List<TextLine> textLineListWithLargestFont;
	private List<SVGText> textCharacters;
//	private List<TextLine> textLineListWithCommonestFont;
	
	/** refactored container */
	private TextLineContainer textLineContainer;
	
	public TextAnalyzerX() {
		this(new SemanticDocumentActionX());
	}
	
	public TextAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}

	public String getTag() {
		return TEXT1;
	}
	
	public Map<Integer, TextLine> getCharacterByXCoordMap() {
		return characterByXCoordMap;
	}

//	public Map<Integer, TextLine> getTextByYCoordMap() {
//		return characterByYCoordMap;
//	}

	public Map<String, RealArray> getWidthByText() {
		return widthByCharacter;
	}

	public Multimap<Integer, TextLine> getLineByYCoord() {
		return subLineByYCoord;
	}

	public List<TextLine> getSortedHorizontalLineListOld() {
		createHorizontalCharacterListAndCreateWords();
		return horizontalCharacterList;
	}

	public void analyzeTextsOld(List<SVGText> textCharacters) {
		createHorizontalCharacterListsOld(textCharacters);
		analyzeSpaces();
		createWordsInSublines();
		mergeSubSup();
		addNumericValues();
		splitAtSpaces();
		normalizeTSpanToText();
	}

	public void analyzeTexts(List<SVGText> textCharacters) {
		this.textCharacters = textCharacters;
		getSortedTextLines(textCharacters);
	}


	private void getSortedTextLines(List<SVGText> textCharacters) {
		if (textLineByYCoordMap == null) {
			textLineByYCoordMap = new HashMap<Integer, TextLine>();
			Multimap<Integer, SVGText> charactersByY = createCharactersByY(textCharacters);
			for (Integer yCoord : charactersByY.keySet()) {
				Collection<SVGText> characters = charactersByY.get(yCoord);
				TextLine textLine = new TextLine(characters, this);
				textLine.sortLineByX();
				textLineByYCoordMap.put(yCoord, textLine);
			}
		}
	}

	private Multimap<Integer, SVGText> createCharactersByY(List<SVGText> textCharacters) {
		Multimap<Integer, SVGText> charactersByY = ArrayListMultimap.create();
		for (SVGText text : textCharacters) {
			Integer yCoord = getScaledYCoord(text);
			LOG.trace("Y "+yCoord);
			charactersByY.put(yCoord, text);
		}
		return charactersByY;
	}

	/** replace by SVGPlusCoordinate
	 * // FIXME
	 * @param text
	 * @return
	 */
	private Integer getScaledYCoord(SVGText text) {
		Double y = text.getY();
		return (y == null) ? null : (int) Math.round((y*Y_SCALE));
	}

	private void normalizeTSpanToText() {
		List<SVGText> textWithTSpans = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgg, ".//svg:text[svg:tspan]"));
		for (SVGText textWithTSpan : textWithTSpans) {
			SVGElement parent = (SVGElement) textWithTSpan.getParent();
			List<SVGTSpan> tSpanList = textWithTSpan.getChildTSpans();
			for (SVGTSpan tSpan : tSpanList) {
				SVGText text = createText(tSpan);
				parent.appendChild(text);
			}
			textWithTSpan.detach();
		}
	}

	public static SVGText createText(SVGTSpan tSpan) {
		SVGText text = new SVGText();
		CMLUtil.copyAttributes(tSpan, text);
		text.setText(tSpan.getText());
		return text;
	}

	private void normalizeTSpanToText(SVGTSpan tSpan, SVGElement parent) {
	}

	private void analyzeSpaces() {
		this.getWidthByCharacterNormalizedByFontSize();
		this.getMedianWidthOfCharacterNormalizedByFont();
		this.getSpaceSizes();
	}

	private void createHorizontalCharacterListsOld(List<SVGText> textCharacters) {
		// refactor
		this.createRawCharacterListText(textCharacters);
		this.createRawTextCharacterPositionMaps();
		this.makeHorizontalCharacterList();
	}

	public TextChunk analyzeRawText(Chunk chunk) {
		this.chunk = chunk;
		this.svgParent = (chunk != null) ? chunk : pageEditorX.getSVGPage();
		
		this.getRawTextCharacterList();
		this.createRawTextCharacterPositionMaps();
		this.createHorizontalCharacterListAndCreateWords();
		analyzeSpaces();
		TextChunk textChunk = this.createTextChunkWithParagraphs();
		return textChunk;
	}
	

	
	public void applyAndRemoveCumulativeTransforms() {
		Long time0 = System.currentTimeMillis();
		SVGUtil.applyAndRemoveCumulativeTransformsFromDocument(pageEditorX.getSVGPage());
		LOG.trace("cumulative transforms on text: "+(System.currentTimeMillis()-time0));
	}

	@Deprecated // use getRawCharacterListText
	private void getRawCharacterList(List<SVGElement> textElements) {
		this.rawCharacterList = new TextLine(this);
		for (int i = 0; i < textElements.size(); i++) {
			SVGText text = (SVGText) textElements.get(i);
			text.setBoundingBoxCached(true);
			rawCharacterList.add(text);
		}
	}
	
	private void createRawCharacterListText(List<SVGText> texts) {
		this.rawCharacterList = new TextLine(this);
		for (SVGText text : texts) {
			text.setBoundingBoxCached(true);
			rawCharacterList.add(text);
		}
		LOG.trace("new Raw character list "+rawCharacterList.toString());
	}
	
	/** puts all characters (usually SVGText of length 1) into
	 * a single container
	 * @return
	 */
	public TextLine getRawTextCharacterList() {
		if (rawCharacterList == null) {
			List<SVGElement> textElements = SVGUtil.getQuerySVGElements(svgParent, ".//svg:text");
			getRawCharacterList(textElements);
			LOG.trace("read "+rawCharacterList.size()+" raw characters "+rawCharacterList.toString());
		}
		return rawCharacterList;
	}
	
	/** creates integer indexed maps of all character positions
	 * indexes to nearest pixel
	 */
	public void createRawTextCharacterPositionMaps() {
		if (characterByXCoordMap == null) {
			getRawTextCharacterList();
			characterByXCoordMap = new HashMap<Integer, TextLine>();
			characterByYCoordMap = new HashMap<Integer, TextLine>();
			for (SVGText rawCharacter : rawCharacterList) {
				Real2 xy = SVGUtil.getTransformedXY(rawCharacter);
				Integer xx = (int) Math.round(xy.getX());
				Integer yy = (int) Math.round(xy.getY());
				addCharacterToMap(characterByXCoordMap, xx, rawCharacter);
				addCharacterToMap(characterByYCoordMap, yy, rawCharacter);
			}
			LOG.trace("createRawTextCharacterPositionMaps: X int coords "+characterByXCoordMap.size()+
					"  / Y int coords: " +characterByYCoordMap.size());
		}
	}

	/** sorts lines into coordinate order
	 * 
	 */
	public void sortCharacterLineMaps() {
		if (subLineByYCoord == null) {
			createRawTextCharacterPositionMaps();
			Set<Integer> keys = characterByYCoordMap.keySet();
			Integer[] yList = keys.toArray(new Integer[keys.size()]);
			Arrays.sort(yList);
			subLineByYCoord = ArrayListMultimap.create();
			for (Integer y : yList) {
				TextLine line = characterByYCoordMap.get(y);
				line.sortLineByX();
				List<TextLine> splitLines = line.getSubLines();
				for (TextLine subLine : splitLines) {
					addSubLine(y, subLine);
				}
			}
			LOG.trace("created subLines: "+subLineByYCoord.size());
		}
	}
	
	public static Double getCommonYCoordinate(List<SVGText> texts, double eps) {
		Double dubble = null;
		for (SVGText text : texts) {
			double d = text.getXY().getY();
			if (dubble == null) {
				dubble = d;
			} else if (!Real.isEqual(dubble, d, eps)) {
				dubble = null;
				break;
			}
		}
		return dubble;
	}

	public static Double getCommonLeftXCoordinate(List<SVGText> texts, double eps) {
		Double dubble = null;
		for (SVGText text : texts) {
			double d = text.getXY().getX();
			if (dubble == null) {
				dubble = d;
			} else if (!Real.isEqual(dubble, d, eps)) {
				dubble = null;
				break;
			}
		}
		return dubble;
	}

	public static Double getCommonRightXCoordinate(List<SVGText> texts, double eps) {
		Double dubble = null;
		for (SVGText text : texts) {
			double d = text.getBoundingBox().getXRange().getMax();
			if (dubble == null) {
				dubble = d;
			} else if (!Real.isEqual(dubble, d, eps)) {
				dubble = null;
				break;
			}
		}
		return dubble;
	}

	private void addSubLine(Integer y, TextLine subLine) {
		subLineByYCoord.put(y, subLine);
		subLine.setY(y);
	}
	
	/** create a list of the sorted sublines in the document
	 * 
	 * @return
	 */
	@Deprecated // use createWords separately
	public List<TextLine> createHorizontalCharacterListAndCreateWords() {
		Long time0 = System.currentTimeMillis();
		if (horizontalCharacterList == null) {
			makeHorizontalCharacterList();
			createWordsInSublines();
		}
		LOG.trace("createYSortedLineList "+(System.currentTimeMillis()-time0));
		return horizontalCharacterList;
	}

	private void makeHorizontalCharacterList() {
		if (horizontalCharacterList == null) {
			sortCharacterLineMaps();
			Set<Integer> keys = subLineByYCoord.keySet();
			Integer[] yList = keys.toArray(new Integer[keys.size()]);
			Arrays.sort(yList);
			this.horizontalCharacterList = new ArrayList<TextLine>();
			for (Integer y : yList) {
				Collection<TextLine> subLineList = subLineByYCoord.get(y);
				LOG.trace("SUBLINELIST "+subLineList.size());
				for (TextLine subLine : subLineList) {
					horizontalCharacterList.add(subLine);
				}
			}
			LOG.trace("created lines: "+horizontalCharacterList.size());
			if (Level.TRACE.equals(LOG.getLevel())) {
				for (TextLine cl : horizontalCharacterList) {
					LOG.debug("sorted line "+cl.toString());
				}
			}
		}
	}
	
	private void createWordsInSublines() {
		for (TextLine subline : horizontalCharacterList) {
			LOG.trace("SUBLINE "+subline);
//			LOG.trace("Guess "+subline.guessAndApplySpacingInLine());
			WordSequence wordSequence = subline.createWords();
			LOG.trace("WordSeq "+wordSequence.getWords().size()+" .. "+wordSequence.getStringValue());
		}
	}
	
	private void addNumericValues() {
		// not quite sure when the TSpans get added so this is messy
		List<SVGText> texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgg, ".//svg:g[@class='word']/svg:text"));
		for (SVGText text : texts) {
			TypedNumberList typedNumberList = interpretTypedNumberList(text);
			TypedNumber typedNumber = interpretTypedNumber(text);
		}
	}

	public TypedNumber interpretTypedNumber(SVGText text) {
		TypedNumber typedNumber = TypedNumber.createNumber(text);
		if (typedNumber != null) {
			String number = ""+typedNumber.getNumber();
			text.addAttribute(new Attribute(NUMBER, number));
			text.addAttribute(new Attribute(DATA_TYPE, typedNumber.getDataType()));
			if (removeNumericTSpans) {
				removeNumericTSpans(text, number);
			}
		}
		return typedNumber;
	}
	
	public TypedNumberList interpretTypedNumberList(SVGText text) {
		TypedNumberList typedNumberList = TypedNumberList.createFromTextSpans(text);
		if (typedNumberList != null) {
			String numbers = typedNumberList.getNumberString();
			text.addAttribute(new Attribute(NUMBERS, numbers));
			text.addAttribute(new Attribute(DATA_TYPE, typedNumberList.getDataType()));
			if (removeNumericTSpans) {
				removeNumericTSpanList(text, numbers);
			}
		}
		return typedNumberList;
	}
	
	private void removeNumericTSpans(SVGText text, String number) {
		List<SVGTSpan> tSpans = text.getChildTSpans();
		if (tSpans.size() == 1) {
			tSpans.get(0).detach();
			text.setText(number);
		} else if (tSpans.size() == 2) {
			tSpans.get(0).detach();
			tSpans.get(1).detach();
			text.setText(number);
		}
	}

	private void removeNumericTSpanList(SVGText text, String number) {
		List<SVGTSpan> tSpans = text.getChildTSpans();
		text.setText(number);
		for (SVGTSpan tSpan : tSpans) {
			tSpan.detach();
		}
	}

	/** uses all lines to estimate the width of a character
	 * 
	 * @param lineListlist
	 * @return all observed widths of the character
	 */
	public Map<String, RealArray> getWidthByCharacterNormalizedByFontSize() {
		if (widthByCharacter == null) {
			createHorizontalCharacterListAndCreateWords();
			widthByCharacter = new HashMap<String, RealArray>();
			for (TextLine characterList : horizontalCharacterList) {
				Double fontSize = characterList.getFontSize();
				Map<String, RealArray> widthByCharacter0 = characterList.getInterCharacterWidthsByCharacterNormalizedByFont();
				for (String charr : widthByCharacter0.keySet()) {
					RealArray widthList0 = widthByCharacter0.get(charr);
					RealArray widthList = widthByCharacter.get(charr);
					if (widthList == null) {
						widthList = new RealArray();
						widthByCharacter.put(charr, widthList);
					}
					widthList.addArray(widthList0);
				}
			}
		}
		return widthByCharacter;
	}

	/** estimates the likely width of a character
	 * maybe this should be the mode?
	 * @param widthByCharacter
	 * @return
	 */
	public Map<String, Double> getMedianWidthOfCharacterNormalizedByFont() {
		if (medianWidthOfCharacterNormalizedByFontMap == null) {
			getWidthByCharacterNormalizedByFontSize();
			medianWidthOfCharacterNormalizedByFontMap = new HashMap<String, Double>();
			for (String charr : widthByCharacter.keySet()) {
				RealArray widthList = widthByCharacter.get(charr);
				Univariate univariate = new Univariate(widthList);
				Double median = univariate.getMedian();
				medianWidthOfCharacterNormalizedByFontMap.put(charr, median);
			}
		}
		return medianWidthOfCharacterNormalizedByFontMap;
	}

	/** utility method for analysing corpus
	 * FIXME
	 * @param element
	 * @return simpleFont with estimated character widths
	 */
	public SimpleFont extractFont(String name, String style) {
//		getMedianWidthOfCharacterNormalizedByFont();
//		SimpleFont simpleFont = new SimpleFont(name, style);
//		for (String ch : medianWidthOfCharacterNormalizedByFontMap.keySet()) {
//			simpleFont.addCharacter(new SimpleCharacter(ch, medianWidthOfCharacterNormalizedByFontMap.get(ch)));
//		}
//		return simpleFont;
		return null;
	}

	/** surveys all the inter-character spaces and attempts to create space metrics
	 * uses all lines on page
	 * @return
	 */
	public RealArray getSpaceSizes() {
		spaceSizes = new RealArray();
		getMedianWidthOfCharacterNormalizedByFont();
		for (TextLine sortedLine : horizontalCharacterList) {
			RealArray interCharacterSpaces = sortedLine.getCharacterWidthArray();
			for (int i = 0; i < sortedLine.size()-1; i++) {
				String charr = sortedLine.get(i).getValue();
				Double medianWidth = medianWidthOfCharacterNormalizedByFontMap.get(charr);
				double space = SVGUtil.decimalPlaces(interCharacterSpaces.get(i) - medianWidth, 3);
				if (space > 0.1) {
					spaceSizes.addElement(space);
				}
			}
		}
		return spaceSizes;
	}
	
	public void /*SVGElement*/ drawSubLineBoundingBoxes() {
		BoundingBoxManager boundingBoxManager = this.getSubLineBoundingBoxManager(SimpleFont.SIMPLE_FONT);
		List<Real2Range> boxes = boundingBoxManager.getBBoxList();
		/*return */ drawBoxes(boxes, "blue", "yellow", 0.3);
	}

	public void /*SVGElement*/ drawBoxes(List<Real2Range> boxes, String stroke, String fill, Double opacity) {
		SVGG g = (SVGG) pageEditorX.getSVGPage().query("svg:g", SVGConstants.SVG_XPATH).get(0);
		SVGG g1 = new SVGG();
		g.appendChild(g1);
		for (Real2Range bbox : boxes) {
			SVGRect rect = new SVGRect( bbox);
			rect.setStroke(stroke);
			rect.setFill(fill);
			rect.setStrokeWidth(0.9);
			rect.setOpacity(opacity);
			g1.appendChild(rect);
		}
//		return svgPage;
	}

	public void debug() {
		debug("xmap", characterByXCoordMap);
		debug("ymap", characterByYCoordMap);
	}

	// =======================================
	
	private void debug(String string, Map<Integer, TextLine> textByCoordMap) {
		Set<Integer> keys = textByCoordMap.keySet();
		Integer[] ii = keys.toArray(new Integer[keys.size()]);
		Arrays.sort(ii);
		for (int iz : ii) {
			TextLine textList = textByCoordMap.get(iz);
			for (SVGText text : textList) {
				System.out.print(">> "+text.getXY()+" "+text.getText()+ " ");
			}
		}
		System.out.println();
	}

	private void addCharacterToMap(Map<Integer, TextLine> map, Integer x, SVGText rawCharacter) {
		TextLine textList = map.get(x);
		if (textList == null) {
			textList = new TextLine(this);
			map.put(x, textList);
		}
		textList.add(rawCharacter);
	}

	/** creates a list of the listOfWordsInLine
	 * 
	 * @param simpleFont
	 * @return
	 */
	public TextChunk createTextChunkWithParagraphs() {
		createWordsAndAddToWordSequenceList();
		textChunk = new TextChunk(chunk, wordSequenceList);
		paragraphList = textChunk.createParagraphs();
		addParagraphsAsSVG();
		return textChunk;
	}
	
	/**
<text font-size="10" font-family="Verdana">
      <tspan x="10" y="10">Here is a paragraph that</tspan>
      <tspan x="10" y="20">requires word wrap.</tspan>
    </text>	 
    @param hasGParaSvgTextChild <g><text/><text/>...<g name='para'><text/>
    */
	public static void cleanAndWordWrapText(Chunk textChunk) {
		SVGText svgText = getConcatenatedText(textChunk);
		if (svgText != null) {
			LOG.trace("wrapped text "+textChunk.getId());
			double textWidthFactor = TextAnalyzerX.DEFAULT_TEXTWIDTH_FACTOR;
			List<SVGElement> rawTextList = SVGUtil.getQuerySVGElements(textChunk, "./svg:text");
			Real2Range rawBoundingBox = SVGUtil.createBoundingBox(rawTextList);
			if (rawBoundingBox == null) {
				throw new RuntimeException("null BB "+textChunk.getId());
			}
			Real2Range scaledBox = scaleBoxX(textWidthFactor, rawBoundingBox);
			for (SVGElement element : rawTextList) {
				element.detach();
			}
			String title = svgText.getText();
			svgText.createWordWrappedTSpans(textWidthFactor, scaledBox, svgText.getFontSize());
			svgText.setTitle(title);
		} else {
			LOG.trace("concatenated text not processed for "+textChunk.getId());
		}
	}

	private static Real2Range scaleBoxX(double scale,	Real2Range rawBoundingBox) {
		if (rawBoundingBox == null) {
			LOG.trace("RAWBB");
		}
		RealRange xRange = rawBoundingBox.getXRange();
		double r = xRange.getRange();
		Real2Range scaledBox = new Real2Range(
				new RealRange(xRange.getMin(), xRange.getMin() + scale * xRange.getRange()),
				rawBoundingBox.getYRange());
		return scaledBox;
	}

	public static SVGText getConcatenatedText(Chunk textChunk) {
		SVGText svgText = null;
		String id = textChunk.getId();
		LOG.trace("text "+id);
		List<SVGElement> gList = SVGUtil.getQuerySVGElements(textChunk, "./svg:g[@name='para' and svg:text]");
		if (gList.size() == 1) {
			svgText = findSingleSVGText(gList);
		} else if (gList.size() > 0) {
			svgText = findSingleSVGText(gList);
			svgText.setTitle("more than one sibling para, omitted >1 as possible subscript");
		}
		return svgText;
	}

	private static SVGText findSingleSVGText(List<SVGElement> gList) {
		SVGText svgText = null;
		SVGG gName = (SVGG) gList.get(0);
		List<SVGElement> texts = SVGUtil.getQuerySVGElements(gName, "./svg:text");
		if (texts.size() == 1) {
			svgText = (SVGText) texts.get(0);
		}
		return svgText;
	}

	public void removeSVGTextFromParagraphs() {
		for (Paragraph paragraph : paragraphList) {
			paragraph.removeSVGText();
		}
	}

	public void removeOriginaSVGTextFromParagraphs() {
		for (Paragraph paragraph : paragraphList) {
			paragraph.removeOriginalSVGText();
		}
	}

	private void addParagraphsAsSVG() {
		for (Paragraph paragraph : paragraphList) {
			paragraph.detach();
			chunk.appendChild(paragraph);
		}
	}

	private List<WordSequence> createWordsAndAddToWordSequenceList() {
		ensureSimpleFont();
		if (wordSequenceList == null) {
			RealArray ySeparationArray = new RealArray();
			Double lastY = null;
			if (svgParent != null) {
				Real2Range bbox = svgParent.getBoundingBox();
				if (bbox != null) {
					Double xLeft = bbox.getXRange().getMin();
					Double deltaY = null;
					LOG.trace("XL "+xLeft);
					createHorizontalCharacterListAndCreateWords();
					Double interlineSeparation = getInterlineSeparation();
					wordSequenceList = new ArrayList<WordSequence>();
					for (TextLine sortedLine : horizontalCharacterList) {
						boolean added = false;
						WordSequence wordSequence = sortedLine.createWords();
						Real2 xy = wordSequence.getXY();
						double y = xy.getY();
						if (lastY != null) {
							deltaY = y - lastY;
							if (interlineSeparation != null && deltaY > YPARA_SEPARATION_FACTOR * interlineSeparation) {
								addParagraphMarker();
								added = true;
							}
						}
						lastY = y;
						if (!added) {
							Double indent = xy.getX() - xLeft;
							if (indent > INDENT) {
								addParagraphMarker();
							}
						}
						wordSequenceList.add(wordSequence);
					}
				}
			}
		}
		return wordSequenceList;
	}

	private Double getInterlineSeparation() {
		// TODO Auto-generated method stub
		return null;
	}

	private void addParagraphMarker() {
		WordSequence paragraphMarkerSequence = WordSequence.createParagraphMarker();
		wordSequenceList.add(paragraphMarkerSequence);
	}

	public BoundingBoxManager getSubLineBoundingBoxManager(SimpleFont simpleFont) {
		if (boundingBoxManager == null) {
//			getSubLineBBoxList(simpleFont);
			boundingBoxManager = new BoundingBoxManager();
			boundingBoxManager.setBBoxList(subLineBBoxList);
		}
		return boundingBoxManager;
	}

	public void drawEmptyBoxes() {
		BoundingBoxManager boundingBoxManager = this.getSubLineBoundingBoxManager(SimpleFont.SIMPLE_FONT);
		drawEmptyBoxes(boundingBoxManager);
	}

	private void drawEmptyBoxes(BoundingBoxManager boundingBoxManager) {
		emptyYTextBoxes = drawEmptyBoxes(boundingBoxManager, BoxEdge.YMIN, "red", "pink", 0.5);
		emptyXTextBoxes = drawEmptyBoxes(boundingBoxManager, BoxEdge.XMIN, "green", "cyan", 0.5);
	}

	private List<Real2Range> drawEmptyBoxes(BoundingBoxManager boundingBoxManager, BoxEdge edge, String stroke, String fill, Double opacity) {
		List<Real2Range> emptyBoxList = boundingBoxManager.createEmptyBoxList(edge);
		drawBoxes(emptyBoxList, stroke, fill, opacity);
		return emptyBoxList;
	}
	
	public void analyzeTextChunksCreateWordsLinesParasAndSubSup(List<SVGElement> chunkElements) {
		TextChunk lastMainTextChunk = null;
		TextChunk lastScriptTextChunk = null;
		TextChunk textChunk = null;
		List<Chunk> chunks = castToChunks(chunkElements);
		ensureTextChunkList();
		int id = 0;
		for (Chunk chunk : chunks) {
			if (chunk.isTextChunk()) {
				chunk.setId("textChunk"+id);
				if (SVGUtil.getQuerySVGElements(chunk, "svg:g").size() == 0) {
					TextAnalyzerX textAnalyzer = new TextAnalyzerX(semanticDocumentActionX);
					textChunk = textAnalyzer.analyzeRawText(chunk);
					// processing a probable sub/superscript
					if (textChunk.isSuscript()) {
						lastScriptTextChunk = analyzeSubSup(lastMainTextChunk, lastScriptTextChunk, textChunk);
					} else {
						analyzeBodyText(lastScriptTextChunk, textChunk);
						lastMainTextChunk = textChunk;
					}
					ensureParagraphList();
					List<Paragraph> paraList = textAnalyzer.getParagraphList();
					paragraphList.addAll(paraList);
				}
				Chunk parentChunk = (Chunk)chunk.getParent();
//				parentChunk.setChunkStyleValue(ChunkStyle.TEXT_CHUNK);
				removeIfSubSup(chunk);
				TextAnalyzerX.cleanAndWordWrapText(chunk);
				parentChunk.addAttribute(new Attribute(CHUNK, TEXT));
				textChunkList.add(parentChunk);
			}
			id++;
		}
	}
	
	public void analyzeSingleWordsOrLines(List<SVGElement> elements) {
		horizontalCharacterList = new ArrayList<TextLine>();
		subLineByYCoord = ArrayListMultimap.create();
		for (SVGElement element : elements) {
			if (hasNoSVGGChildren()) {
				TextAnalyzerX textAnalyzer = new TextAnalyzerX(semanticDocumentActionX);
				textAnalyzer.analyzeRawText(element);
				horizontalCharacterList.addAll(textAnalyzer.horizontalCharacterList);
				for (TextLine subline : horizontalCharacterList) {
					addSubLine(subline.getIntegerY(), subline);
				}
			}
		}
	}
	
	public void sortLines(List<SVGElement> elements) {
		horizontalCharacterList = new ArrayList<TextLine>();
		subLineByYCoord = ArrayListMultimap.create();
		for (SVGElement element : elements) {
			if (hasNoSVGGChildren()) {
				TextAnalyzerX textAnalyzer = new TextAnalyzerX(semanticDocumentActionX);
				textAnalyzer.analyzeRawText(element);
				horizontalCharacterList.addAll(textAnalyzer.horizontalCharacterList);
				for (TextLine subline : horizontalCharacterList) {
					addSubLine(subline.getIntegerY(), subline);
				}
			}
		}
	}
	
	private boolean hasNoSVGGChildren() {
		return SVGUtil.getQuerySVGElements(chunk, "svg:g").size() == 0;
	}
	
	public void analyzeRawText(SVGElement element) {
		this.svgParent = element;
		this.getRawTextCharacterList();
		this.createRawTextCharacterPositionMaps();
		this.createHorizontalCharacterListAndCreateWords();
		analyzeSpaces();
		createWordsAndAddToWordSequenceList();
		copyWordSequencesToParent();
		detachRawCharacters();
	}

	private void copyWordSequencesToParent() {
		if (rawCharacterList.size() > 0) {
			LOG.trace("WordSequences "+wordSequenceList.size());
			SVGElement parent = (SVGElement) rawCharacterList.get(0).getParent();
			if (parent == null) {
				LOG.warn("No parent for rawCharacterList: "+rawCharacterList);
			} else {
				LOG.trace("P "+parent.getId());
				int wsSerial = 0;
				for (WordSequence ws : wordSequenceList) {
					SVGText svgText = ws.createSVGText();
					LOG.trace("Text "+svgText.getId());
					List<Word> words = ws.getWords();
					LOG.trace("words "+words.size());
					if (words.size() > 0) {
						SVGG wordListG = new SVGG();
						wordListG.setClassName(WORD_LIST);
						wordListG.setId("tw_"+words.get(0).getId());
						if (!Word.S_PARA.equals(svgText.getValue())) {
							parent.appendChild(wordListG);
							for (Word word : words) {
								word.addToParentAndReplaceCharacters(wordListG);
							}
						} else {
							for (Word word : words) {
								word.detachCharacters();
							}
						}
						LOG.trace("TXT "+svgText.getValue()+" "+svgText.toXML());
					}
				}
			}
		}
	}

	private void detachRawCharacters() {
		for (SVGText charx : rawCharacterList) {
			charx.detach();
		}
	}
	
	
	private List<Chunk> castToChunks(List<SVGElement> chunkElements) {
		List<Chunk> chunks = new ArrayList<Chunk>();
		for (SVGElement chunkElement : chunkElements) {
			Chunk chunk = null;
			if (!(chunkElement instanceof Chunk)) {
				chunk = Chunk.createAndReplace(chunkElement);
			} else {
				chunk = (Chunk) chunkElement;
			}
			chunks.add(chunk);
		}
		return chunks;
	}

	private void removeIfSubSup(Chunk chunk) {
	}

	private void ensureTextChunkList() {
		if (textChunkList == null) {
			textChunkList = new ArrayList<Chunk>();
		}
	}

	private void ensureParagraphList() {
		if (paragraphList == null) {
			paragraphList = new ArrayList<Paragraph>();
		}
	}

	private List<Paragraph> getParagraphList() {
		return paragraphList;
	}

	private TextChunk analyzeBodyText(TextChunk lastScriptTextChunk, TextChunk textChunk) {
		if (lastScriptTextChunk != null) {
			if (textChunk.hasSuperscriptsIn(lastScriptTextChunk)) {
				textChunk.addSuperscriptsFrom(lastScriptTextChunk);
				textChunk.detachChunk();
				lastScriptTextChunk = null;
			}
		}
		return lastScriptTextChunk;
	}
	
	private TextChunk analyzeSubSup(
			TextChunk lastMainTextChunk, TextChunk lastScriptTextChunk, TextChunk textChunk) {
		if (lastMainTextChunk != null) {
			if (lastMainTextChunk.hasSubscriptsIn(textChunk)) {
				lastMainTextChunk.addSubscriptsFrom(textChunk);
				textChunk.detachChunk();
				lastScriptTextChunk = null;
			} else {
				// save for superscript
				lastScriptTextChunk = textChunk;
			}
		}
		return lastScriptTextChunk;
	}

	public void mergeChunks() {
		List<SVGElement> textChunkList1 = SVGUtil.getQuerySVGElements(pageEditorX.getSVGPage(), ".//svg:g[@"+TEXT+"='"+CHUNK+"']");
		for (SVGElement textChunk : textChunkList1) {
			List<SVGElement> paraList = SVGUtil.getQuerySVGElements(textChunk, "./*/svg:g[@"+Paragraph.NAME+"='"+Paragraph.PARA+"']");
			for (SVGElement paraElement : paraList) {
				Paragraph paragraph = Paragraph.createElement(paraElement);
				paragraph.createAndAddHTML();
			}
		}
	}

	// obsolete?
	public void setCreateTSpans(boolean createTSpans) {
		this.createTSpans = createTSpans;
	}
	
	// obsolete?
	public void setCreateHTML(boolean createHTML) {
		this.createHTML = createHTML;
	}

	public SimpleFont ensureSimpleFont() {
		if (this.simpleFont == null) {
			simpleFont = pageEditorX.getSemanticDocumentAction().getSimpleFont();
			if (this.simpleFont == null) {
				simpleFont = SimpleFont.SIMPLE_FONT;
			}
			if (this.simpleFont == null) {
				//throw new RuntimeException("Cannot make simpleFont");
				LOG.warn("Cannot make simpleFont");
			}
		}
		return simpleFont;
	}

	public List<WordSequence> getWordSequenceList() {
		return wordSequenceList;
	}

	/** merge subsups and remove old subsup elements
	 * assumes lines are in order Y slowest, X fastest
	 * not yet recursive
	 * 
	 * @param texts
	 * @return
	 */
	public void mergeSubSup() {
		if (subSup) {
			List<SVGText> texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgg, ".//svg:g[@class='word']/svg:text"));
			LOG.trace("SUBSUP....."+texts.size());
			mergeSubSup(texts);
		}
	}

	public void mergeSubSup(List<SVGText> texts) {
		SubSupAnalyzerX subSupAnalyzerX = new SubSupAnalyzerX(this);
		subSupAnalyzerX.mergeTexts(texts);
	}

	public void splitAtSpaces() {
		// select texts which contains spaces
		List<SVGText> texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(
				svgg, ".//svg:text[contains(text(),' ')] | .//svg:tspan[contains(.,' ')]"));
		for (SVGText text : texts) {
			splitAtSpaces(text);
		}
	}

	public void splitAtSpaces(SVGText textOrSpan) {
		String s = textOrSpan.getText();
		String id = textOrSpan.getId();
		String coords = textOrSpan.getAttributeValue(Word.COORDS);
		List<SVGTSpan> spans = textOrSpan.getChildTSpans();
		if (spans.size() > 0) {
			processSpans(textOrSpan, spans);
		} else {
			Real2Array real2Array = (coords == null) ? null : Real2Array.createFromCoords(coords);
			if (real2Array == null || s == null || real2Array.size() !=s.length()) {
				LOG.debug("Cannot match array: "+coords);
				real2Array = null;
			} else {
				processLeafSpanOrText(textOrSpan, s, id, real2Array);
			}
		}
	}

	private void processLeafSpanOrText(SVGText textOrSpan, String s, String id, Real2Array real2Array) {
		Integer index = null;
		SVGText parent = null;
		if (textOrSpan instanceof SVGTSpan) {
			parent = (SVGText) textOrSpan.getParent();
			index = parent.indexOf(textOrSpan);
			textOrSpan.detach();
		} else {
			parent = textOrSpan;
			parent.setText(null);
		}
		SVGTSpan lastSpan = null;
		int last = 0;
		int l = s.length();
		for (int i = 0; i < l; i++) {
			if (s.charAt(i) == EuclidConstants.C_SPACE || i == l-1) {
				i = (i == l-1) ? l : i;
				String ss = s.substring(last, i);
				if (ss.trim().length() > 0) {
					SVGTSpan tSpan = new SVGTSpan(real2Array.get(last), ss);
					CMLUtil.copyAttributes(textOrSpan, tSpan);
					tSpan.setId(id+"_"+last);
					Real2Array subArray = real2Array.createSubArray(last, i-1);
					tSpan.addAttribute(new Attribute(Word.COORDS, subArray.toString()));
					tSpan.setXY(subArray.get(0));
					if (index == null) {
						parent.appendChild(tSpan);
					} else {
						parent.insertChild(tSpan, index);
						index++;
					}
					if (lastSpan != null) {
						tSpan.addAttribute(new Attribute(PREVIOUS, lastSpan.getId()));
					}
					lastSpan = tSpan;
				}
				last = i+1;
			}
		}
	}
	
	private void processSpans(SVGText textOrSpan, List<SVGTSpan> spans) {
		textOrSpan.setText(null);
		for (SVGTSpan span : spans) {
			splitAtSpaces(span);
		}
	}

	public void setSubSup(boolean subSup) {
		this.subSup = subSup;
	}

	public void setRemoveNumericTSpans(boolean removeNumericTSpans) {
		this.removeNumericTSpans = removeNumericTSpans;
	}

	public void setSplitAtSpaces(boolean splitAtSpaces) {
		this.splitAtSpaces = splitAtSpaces;
	}

	public static String getNumericValue(SVGText numericText) {
		return numericText.getAttributeValue(NUMBER);
	}

	public Map<Integer, TextLine> getTextLineByYCoordMap() {
		return textLineByYCoordMap;
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
	
	public Integer getSerialNumber(TextLine textLine) {
		return (textLineSerialMap == null) ? null : textLineSerialMap.get(textLine);
	}
	

	private void ensureTextLineByYCoordMap() {
		if (textLineByYCoordMap == null) {
			textLineByYCoordMap = new HashMap<Integer, TextLine>();
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
			meanFontSizeArray.format(NDEC_FONTSIZE);
		}
		return meanFontSizeArray;
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
					meanWidth = meanWidth == null ? null : Real.normalize(meanWidth, NDEC_FONTSIZE);
					actualWidthsOfSpaceCharactersList.add(meanWidth);
				}
			}
//			actualWidthsOfSpaceCharactersArray.format(NDEC_FONTSIZE);
		}
		return actualWidthsOfSpaceCharactersList;
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
			modalExcessWidthArray.format(NDEC_FONTSIZE);
		}
		return modalExcessWidthArray;
	}

	// X
//	public Set<SvgPlusCoordinate> getFontSizeSet() {
//		if (fontSizeSet == null) {
//			if (textLineList != null) {
//				fontSizeSet = new HashSet<SvgPlusCoordinate>();
//				for (TextLine textLine : textLineList) {
//					Set<SvgPlusCoordinate> textLineFontSizeSet = textLine.getFontSizeSet();
//					fontSizeSet.addAll(textLineFontSizeSet);
//				}
//			}
//		}
//		return fontSizeSet;
//	}

	public List<TextLine> getTextLines() {
		return textLineList;
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

	public TextLineSet getTextLineSetByFontSize(double fontSize) {
		Multimap<SvgPlusCoordinate, TextLine> textLineListByFontSize = this.getTextLineListByFontSize();
		List<TextLine> textLines = (List<TextLine>) textLineListByFontSize.get(new SvgPlusCoordinate(fontSize));
		return new TextLineSet(textLines);
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
			interTextLineSeparationArray.format(NDEC_FONTSIZE);
		}
		return interTextLineSeparationArray;
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
			textLineCoordinateArray.format(NDEC_FONTSIZE);
		}
		return textLineCoordinateArray;
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

	// X
//	public SvgPlusCoordinate getLargestFont() {
//		largestFontSize = null;
//		Set<SvgPlusCoordinate> fontSizes = this.getFontSizeSet();
//		for (SvgPlusCoordinate fontSize : fontSizes) {
//			if (largestFontSize == null || largestFontSize.getDouble() < fontSize.getDouble()) {
//				largestFontSize = fontSize;
//			}
//		}
//		return largestFontSize;
//	}
//
// X
//	public List<TextLine> getLinesWithLargestFont() {
//		if (linesWithLargestFont == null) {
//			linesWithLargestFont = new ArrayList<TextLine>();
//			getLargestFont();
//			for (int i = 0; i < textLineList.size(); i++){
//				TextLine textLine = textLineList.get(i);
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

	// NEW
//	public List<TextLine> getLinesWithCommonestFont() {
//		if (textLineListWithCommonestFont == null) {
//			textLineListWithCommonestFont = new ArrayList<TextLine>();
//			getCommonestFont();
//			for (int i = 0; i < textLineList.size(); i++){
//				TextLine textLine = textLineList.get(i);
//				Double fontSize = (textLine == null) ? null : textLine.getFontSize();
//				if (fontSize != null) {
//					if (Real.isEqual(fontSize, largestFontSize.getDouble(), 0.001)) {
//						textLineListWithCommonestFont.add( textLine);
//					}
//				}
//			}
//		}
//		return textLineListWithCommonestFont;
//	}

	private void getCommonestFont() {
		throw new RuntimeException("NYI");
	}

	/** creates one "para" per line
	 * usually needs tidying with createHtmlDivWithParas
	 * @return
	 */
	public HtmlElement createHtmlRawDiv() {
		ensureTextLineContainer();
//		List<TextLine> textLineList = textLineContainer.getLinesWithLargestFont();
		List<TextLine> textLineList = textLineContainer.getLinesWithCommonestFont();
		HtmlDiv div = new HtmlDiv();
		for (TextLine textLine : textLineList) {
			HtmlElement p = textLine.createHtmlLine();
			div.appendChild(p);
		}
		return div;
	}

	/** creates one "para" per line
	 * usually needs tidying with createHtmlDivWithParas
	 * @return
	 */
	public HtmlElement createHtmlRawDiv(List<TextLine> linesToBeAnalyzed) {
//		textLineListWithLargestFont = getLinesWithLargestFont();
		HtmlDiv div = new HtmlDiv();
		for (TextLine textLine : linesToBeAnalyzed) {
			HtmlElement p = textLine.createHtmlLine();
			div.appendChild(p);
		}
		return div;
	}

	public HtmlElement createHtmlDivWithParas() {
		ensureTextLineContainer();
		List<TextLine> textLineListWithLargestFont = textLineContainer.getLinesWithCommonestFont();
		HtmlElement div = null;
		if (textLineListWithLargestFont.size() == 0){
			 div = null;
		} else if (textLineListWithLargestFont.size() == 1){
			 div = textLineListWithLargestFont.get(0).createHtmlLine();
		} else {
			HtmlElement rawDiv = createHtmlRawDiv();
			Double leftIndent = this.getMaximumLeftIndentForLargestFont();
			Real2Range leftBB = this.getTextLinesLargestFontBoundingBox();
			if (leftBB != null) {
				Double deltaLeftIndent = (leftIndent == null) ? 0 : (leftIndent - this.getTextLinesLargestFontBoundingBox().getXRange().getMin());
				Real2Range largestFontBB = textLineContainer.getLargestFontBoundingBox();
				if (largestFontBB != null) {
					RealRange xRange = largestFontBB.getXRange();
					Double indentBoundary = largestFontBB.getXRange().getMin() + deltaLeftIndent/2.0;
					LOG.trace("left, delta, boundary "+leftIndent+"; "+deltaLeftIndent+"; "+indentBoundary);
					div = new HtmlDiv();
					Elements htmlLines = rawDiv.getChildElements();
					// always start with para
					HtmlP pCurrent = createAndAddNewPara(div, (HtmlP) htmlLines.get(0));
					for (int i = 1; i < textLineListWithLargestFont.size(); i++) {
						TextLine textLine = textLineListWithLargestFont.get(i);
						HtmlP pNext = (HtmlP) HtmlElement.create(htmlLines.get(i));
						// indent, create new para
						if (textLine.getFirstXCoordinate() > indentBoundary) {
							pCurrent = createAndAddNewPara(div, pNext);
						} else {
							mergeParas(pCurrent, pNext);
						}
					}
				}
			}
		}
		return div;
	}

	public HtmlElement createHtmlDivWithParasNew() {
		throw new RuntimeException("NYI");
//		textLineListWithLargestFont = getLinesWithLargestFont();
//		textLineListWithCommonestFont = getLinesWithCommonestFont();
//		List<TextLine> linesToBeAnalyzed = textLineListWithCommonestFont;
//		HtmlElement div = null;
//		if (linesToBeAnalyzed.size() == 0){
//			 div = null;
//		} else if (linesToBeAnalyzed.size() == 1){
//			 div = linesToBeAnalyzed.get(0).createHtmlLine();
//		} else {
//			HtmlElement rawDiv = createHtmlRawDiv(linesToBeAnalyzed);
//			Double leftIndent = this.getMaximumLeftIndentForLargestFont();
//			Double deltaLeftIndent = (leftIndent == null) ? 0 : (leftIndent - this.getTextLinesLargestFontBoundingBox().getXRange().getMin());
//			this.getTextLinesLargestFontBoundingBox();
//			Double indentBoundary = textLineListWithCommonestFont.getXRange().getMin() + deltaLeftIndent/2.0;
//			LOG.trace("left, delta, boundary "+leftIndent+"; "+deltaLeftIndent+"; "+indentBoundary);
//			div = new HtmlDiv();
//			Elements htmlLines = rawDiv.getChildElements();
//			// always start with para
//			HtmlP pCurrent = createAndAddNewPara(div, (HtmlP) htmlLines.get(0));
//			for (int i = 1; i < linesToBeAnalyzed.size(); i++) {
//				TextLine textLine = linesToBeAnalyzed.get(i);
//				HtmlP pNext = (HtmlP) HtmlElement.create(htmlLines.get(i));
//				// indent, create new para
//				if (textLine.getFirstXCoordinate() > indentBoundary) {
//					pCurrent = createAndAddNewPara(div, pNext);
//				} else {
//					mergeParas(pCurrent, pNext);
//				}
//			}
//		}
//		return div;
	}

	private void mergeParas(HtmlP pCurrent, HtmlP pNext) {
		Elements currentChildren = pCurrent.getChildElements();
		HtmlElement lastCurrent = (HtmlElement) currentChildren.get(currentChildren.size() - 1);
		HtmlSpan currentLastSpan = (lastCurrent instanceof HtmlSpan) ? (HtmlSpan) lastCurrent : null;
		Elements nextChildren = pNext.getChildElements();
		HtmlElement firstNext = (HtmlElement) nextChildren.get(0);
		HtmlSpan nextFirstSpan = (firstNext instanceof HtmlSpan) ? (HtmlSpan) firstNext : null;
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

	private String mergeLineText(String last, String next) {
		//merge hyphen minus
		if (last.endsWith("-")) {
			return last.substring(0, last.length()-1) + next;
		} else {
			return last + " " + next;
		}
	}

	private HtmlP createAndAddNewPara(HtmlElement div, HtmlP p) {
		HtmlP pNew = (HtmlP) HtmlElement.create(p);
		div.appendChild(pNew);
		return pNew;
	}

//	private Real2Range getTextLinesLargestFontBoundingBox() {
//		if (textLinesLargetFontBoundingBox == null) {
//			if (textLineListWithLargestFont.size() > 0) {
//				textLinesLargetFontBoundingBox = new Real2Range(new Real2Range(textLineListWithLargestFont.get(0).getBoundingBox()));
//				for (int i = 1; i < textLineListWithLargestFont.size(); i++) {
//					textLinesLargetFontBoundingBox.plus(textLineListWithLargestFont.get(i).getBoundingBox());
//				}
//			}
//		}
//		return textLinesLargetFontBoundingBox;
//	}

	/** finds maximum indent of lines
	 * must be at least 2 lines
	 * currently does not check for capitals, etc.
	 * 
	 */
	public Double getMaximumLeftIndentForLargestFont() {
		Double indent = null;
		Double xLeft = null;
		ensureTextLineContainer();
		List<TextLine> textLineListWithLargestFont = textLineContainer.getLinesWithCommonestFont();
		if (textLineListWithLargestFont != null && textLineListWithLargestFont.size() > 1) {
			for (TextLine textLine : textLineListWithLargestFont) {
				Double xStart = textLine.getFirstXCoordinate();
				if (xStart == null) {
					throw new RuntimeException("null start");
				}
				if (xLeft == null) {
					xLeft = xStart;
				}
				if (xLeft - xStart > INDENT_MIN) {
					indent = xLeft;
				} else if (xStart - xLeft > INDENT_MIN) {
					indent = xStart;
				}
			}
		}
		return indent;
	}

	private void ensureTextLineContainer() {
		if (this.textLineContainer == null) {
			this.textLineContainer = new TextLineContainer();
			this.textLineContainer.setTextLines(textLineList);
		}
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
				if (xRight - xLast > INDENT_MIN) {
					indent = xLast;
				} else if (xLast - xRight > INDENT_MIN) {
					indent = xRight;
				}
			}
		}
		return indent;
	}

	public static TextAnalyzerX createTextAnalyzerWithSortedLines(File svgFile) {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
		List<SVGText> textCharacters = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgPage, ".//svg:text"));
		return createTextAnalyzerWithSortedLines(textCharacters);
	}

	public static TextAnalyzerX createTextAnalyzerWithSortedLines(List<SVGText> textCharacters) {
		TextAnalyzerX textAnalyzer = new TextAnalyzerX();
		textAnalyzer.analyzeTexts(textCharacters);
		textAnalyzer.getLinesInIncreasingY();
		return textAnalyzer;
	}

	public static List<TextLine> createTextLineList(File svgFile) {
		TextAnalyzerX textAnalyzer = createTextAnalyzerWithSortedLines(svgFile);
		List<TextLine> textLineList = textAnalyzer.getLinesInIncreasingY();
		return textLineList;
	}

	public List<SVGText> getTextCharacters() {return textCharacters;}

	public TextLineContainer getTextLineContainer() {
		ensureTextLineContainer();
		return textLineContainer;
	}

	public List<TextLine> getLinesWithLargestFont() {
		ensureTextLineContainer();
		return textLineContainer.getLinesWithCommonestFont();
	}

	public Real2Range getTextLinesLargestFontBoundingBox() {
		ensureTextLineContainer();
		return textLineContainer.getLargestFontBoundingBox();
	}

}
