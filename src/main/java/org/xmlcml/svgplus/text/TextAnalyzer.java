package org.xmlcml.svgplus.text;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;

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
import org.xmlcml.svgplus.command.AbstractPageAnalyzer;
import org.xmlcml.svgplus.command.DocumentAnalyzer;
import org.xmlcml.svgplus.command.PageEditor;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.tools.BoundingBoxManager;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/** attempts to assemble characters into meaningful text
 * 
 * @author pm286
 *
 */
public class TextAnalyzer extends AbstractPageAnalyzer {

	private final static Logger LOG = Logger.getLogger(TextAnalyzer.class);
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
	public static Double TEXT_EPS = 1.0;


	private HorizontalCharacterList rawCharacterList;
	private Map<Integer, HorizontalCharacterList> characterByXCoordMap;
	private Map<Integer, HorizontalCharacterList> characterByYCoordMap;
	private Map<String, RealArray> widthByCharacter;
	private Multimap<Integer, HorizontalCharacterList> subLineByYCoord;
	private List<HorizontalCharacterList> horizontalCharacterList;
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
	private boolean createTSpans;
	private boolean createHTML;
	private SimpleFont simpleFont;
	
	private boolean subSup;
	private boolean removeNumericTSpans;
	private boolean splitAtSpaces;
	
//	public TextAnalyzer() {
//		super(new CurrentPage());
//	}
	
	public TextAnalyzer(SemanticDocumentAction semanticDocumentAction) {
		super(semanticDocumentAction);
	}

	public String getTag() {
		return TEXT1;
	}
	
	public Map<Integer, HorizontalCharacterList> getCharacterByXCoordMap() {
		return characterByXCoordMap;
	}

	public Map<Integer, HorizontalCharacterList> getTextByYCoordMap() {
		return characterByYCoordMap;
	}

	public Map<String, RealArray> getWidthByText() {
		return widthByCharacter;
	}

	public Multimap<Integer, HorizontalCharacterList> getLineByYCoord() {
		return subLineByYCoord;
	}

	public List<HorizontalCharacterList> getSortedHorizontalLineList() {
		createHorizontalCharacterListAndCreateWords();
		return horizontalCharacterList;
	}

	public void analyzeTexts(SVGG svgg, List<SVGText> textCharacters) {
		LOG.trace("ANALYZE TEXT "+textCharacters.size());
		this.svgg = svgg;
		createHorizontalCharacterLists(textCharacters);
		analyzeSpaces();
		createWordsInSublines();
		mergeSubSup();
		addNumericValues();
		splitAtSpaces();
		normalizeTSpanToText();
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

	private void createHorizontalCharacterLists(List<SVGText> textCharacters) {
		// refactor
		this.createRawCharacterListText(textCharacters);
		this.createRawTextCharacterPositionMaps();
		this.makeHorizontalCharacterList();
	}

	public TextChunk analyzeRawText(Chunk chunk) {
		this.chunk = chunk;
		this.svgParent = (chunk != null) ? chunk : pageEditor.getSVGPage();
		
		this.getRawTextCharacterList();
		this.createRawTextCharacterPositionMaps();
		this.createHorizontalCharacterListAndCreateWords();
		analyzeSpaces();
		TextChunk textChunk = this.createTextChunkWithParagraphs();
		return textChunk;
	}
	

	
	public void applyAndRemoveCumulativeTransforms() {
		Long time0 = System.currentTimeMillis();
		SVGUtil.applyAndRemoveCumulativeTransformsFromDocument(pageEditor.getSVGPage());
		LOG.trace("cumulative transforms on text: "+(System.currentTimeMillis()-time0));
	}

	@Deprecated // use getRawCharacterListText
	private void getRawCharacterList(List<SVGElement> textElements) {
		this.rawCharacterList = new HorizontalCharacterList(this);
		for (int i = 0; i < textElements.size(); i++) {
			SVGText text = (SVGText) textElements.get(i);
			text.setBoundingBoxCached(true);
			rawCharacterList.add(text);
		}
	}
	
	private void createRawCharacterListText(List<SVGText> texts) {
		this.rawCharacterList = new HorizontalCharacterList(this);
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
	public HorizontalCharacterList getRawTextCharacterList() {
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
			characterByXCoordMap = new HashMap<Integer, HorizontalCharacterList>();
			characterByYCoordMap = new HashMap<Integer, HorizontalCharacterList>();
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
				HorizontalCharacterList line = characterByYCoordMap.get(y);
				line.sortLineByX();
				List<HorizontalCharacterList> splitLines = line.getSubLines();
				for (HorizontalCharacterList subLine : splitLines) {
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

	private void addSubLine(Integer y, HorizontalCharacterList subLine) {
		subLineByYCoord.put(y, subLine);
		subLine.setY(y);
	}
	
	/** create a list of the sorted sublines in the document
	 * 
	 * @return
	 */
	@Deprecated // use createWords separately
	public List<HorizontalCharacterList> createHorizontalCharacterListAndCreateWords() {
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
			this.horizontalCharacterList = new ArrayList<HorizontalCharacterList>();
			for (Integer y : yList) {
				Collection<HorizontalCharacterList> subLineList = subLineByYCoord.get(y);
				LOG.trace("SUBLINELIST "+subLineList.size());
				for (HorizontalCharacterList subLine : subLineList) {
					horizontalCharacterList.add(subLine);
				}
			}
			LOG.trace("created lines: "+horizontalCharacterList.size());
			if (Level.TRACE.equals(LOG.getLevel())) {
				for (HorizontalCharacterList cl : horizontalCharacterList) {
					LOG.debug("sorted line "+cl.toString());
				}
			}
		}
	}
	
	private void createWordsInSublines() {
		for (HorizontalCharacterList subline : horizontalCharacterList) {
			LOG.trace("SUBLINE "+subline);
			LOG.trace("Guess "+subline.guessAndApplySpacingInLine());
			WordSequence wordSequence = subline.createWords();
			LOG.trace("WordSeq "+wordSequence.getWords().size()+" .. "+wordSequence.getStringValue());
		}
	}
	
	private void addNumericValues() {
		// not quite sure when the TSpans get added so this is messy
		List<SVGText> texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgg, ".//svg:g[@class='word']/svg:text"));
		for (SVGText text : texts) {
			System.out.println(text.toXML());
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
			for (HorizontalCharacterList characterList : horizontalCharacterList) {
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
		for (HorizontalCharacterList sortedLine : horizontalCharacterList) {
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
		SVGG g = (SVGG) pageEditor.getSVGPage().query("svg:g", SVGConstants.SVG_XPATH).get(0);
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
	
	private void debug(String string, Map<Integer, HorizontalCharacterList> textByCoordMap) {
		Set<Integer> keys = textByCoordMap.keySet();
		Integer[] ii = keys.toArray(new Integer[keys.size()]);
		Arrays.sort(ii);
		for (int iz : ii) {
			HorizontalCharacterList textList = textByCoordMap.get(iz);
			for (SVGText text : textList) {
//				System.out.print(text.getXY()+" "+text.getText()+ " ");
			}
		}
//		System.out.println();
	}

	private void addCharacterToMap(Map<Integer, HorizontalCharacterList> map, Integer x, SVGText rawCharacter) {
		HorizontalCharacterList textList = map.get(x);
		if (textList == null) {
			textList = new HorizontalCharacterList(this);
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
			double textWidthFactor = TextAnalyzer.DEFAULT_TEXTWIDTH_FACTOR;
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
					for (HorizontalCharacterList sortedLine : horizontalCharacterList) {
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
					TextAnalyzer textAnalyzer = new TextAnalyzer(semanticDocumentAction);
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
				TextAnalyzer.cleanAndWordWrapText(chunk);
				parentChunk.addAttribute(new Attribute(CHUNK, TEXT));
				textChunkList.add(parentChunk);
			}
			id++;
		}
	}
	
	public void analyzeSingleWordsOrLines(List<SVGElement> elements) {
		horizontalCharacterList = new ArrayList<HorizontalCharacterList>();
		subLineByYCoord = ArrayListMultimap.create();
		for (SVGElement element : elements) {
			if (hasNoSVGGChildren()) {
				TextAnalyzer textAnalyzer = new TextAnalyzer(semanticDocumentAction);
				textAnalyzer.analyzeRawText(element);
				horizontalCharacterList.addAll(textAnalyzer.horizontalCharacterList);
				for (HorizontalCharacterList subline : horizontalCharacterList) {
					addSubLine(subline.getY(), subline);
				}
			}
		}
	}
	
	public void sortLines(List<SVGElement> elements) {
		horizontalCharacterList = new ArrayList<HorizontalCharacterList>();
		subLineByYCoord = ArrayListMultimap.create();
		for (SVGElement element : elements) {
			if (hasNoSVGGChildren()) {
				TextAnalyzer textAnalyzer = new TextAnalyzer(semanticDocumentAction);
				textAnalyzer.analyzeRawText(element);
				horizontalCharacterList.addAll(textAnalyzer.horizontalCharacterList);
				for (HorizontalCharacterList subline : horizontalCharacterList) {
					addSubLine(subline.getY(), subline);
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
		List<SVGElement> textChunkList1 = SVGUtil.getQuerySVGElements(pageEditor.getSVGPage(), ".//svg:g[@"+TEXT+"='"+CHUNK+"']");
		for (SVGElement textChunk : textChunkList1) {
			List<SVGElement> paraList = SVGUtil.getQuerySVGElements(textChunk, "./*/svg:g[@"+Paragraph.NAME+"='"+Paragraph.PARA+"']");
			for (SVGElement paraElement : paraList) {
				Paragraph paragraph = Paragraph.createElement(paraElement);
				paragraph.createAndAddHTML();
			}
		}
	}

	public void setCreateTSpans(boolean createTSpans) {
		this.createTSpans = createTSpans;
	}
	
	public void setCreateHTML(boolean createHTML) {
		this.createHTML = createHTML;
	}

	SimpleFont ensureSimpleFont() {
		if (this.simpleFont == null) {
			simpleFont = pageEditor.getSemanticDocumentAction().getSimpleFont();
			if (this.simpleFont == null) {
				simpleFont = SimpleFont.SIMPLE_FONT;
			}
			if (this.simpleFont == null) {
				throw new RuntimeException("Cannot make simpleFont");
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
		SubSupAnalyzer subSupAnalyzer = new SubSupAnalyzer(this);
		subSupAnalyzer.mergeTexts(texts);
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
	
}
