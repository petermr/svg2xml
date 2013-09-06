package org.xmlcml.svg2xml.page;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.ScriptContainer;
import org.xmlcml.svg2xml.text.TextCoordinate;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextStructurer;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/** attempts to assemble characters into meaningful text
 * 
 * @author pm286
 *
 */
public class TextAnalyzer extends PageChunkAnalyzer {

	private final static Logger LOG = Logger.getLogger(TextAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TEXT1 = "text1";
	public static final String CHUNK = "chunk";
	public static final String TEXT = "TEXT";
	
	public static final double DEFAULT_TEXTWIDTH_FACTOR = 0.9;
	public static final int NDEC_FONTSIZE = 3;
	public static final double INDENT_MIN = 1.0; //pixels
	public static Double TEXT_EPS = 1.0;


	private TextLine rawCharacterList;
	private Map<Integer, TextLine> characterByXCoordMap;

	public List<Real2Range> emptyYTextBoxes;
	public List<Real2Range> emptyXTextBoxes;

	private SVGElement svgParent;
    private List<SVGText> textCharacters;
	
	/** refactored container */
	private TextStructurer textStructurer;
	private HtmlElement htmlElement;
	
	public TextAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public TextAnalyzer(List<SVGText> characterList, PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
		this.setTextCharacters(characterList);
	}

	public TextAnalyzer(SVGElement svgElement, PageAnalyzer pageAnalyzer) {
		this(SVGText.extractTexts(svgElement), pageAnalyzer);
	}

	public String getTag() {
		return TEXT1;
	}
	
	public Map<Integer, TextLine> getCharacterByXCoordMap() {
		return characterByXCoordMap;
	}

	public void analyzeTexts(List<SVGText> textCharacters) {
		if (textCharacters == null) {
			throw new RuntimeException("null characters: ");
		} else {
			this.textCharacters = textCharacters;
			ensureTextContainerWithSortedLines().sortLineByXandMakeTextLineByYCoordMap(textCharacters);
		}
	}


	public List<TextLine> getLinesInIncreasingY() {
		return ensureTextContainerWithSortedLines().getLinesInIncreasingY();
	}

	private void getRawCharacterList(List<SVGElement> textElements) {
		this.rawCharacterList = new TextLine(this);
		for (int i = 0; i < textElements.size(); i++) {
			SVGText text = (SVGText) textElements.get(i);
			text.setBoundingBoxCached(true);
			rawCharacterList.add(text);
		}
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
	

	public void debug() {
		debug("xmap", characterByXCoordMap);
	}

	public List<TextLine> getTextLines() {
		return ensureTextContainerWithSortedLines().getTextLineList();
	}
	
	private TextStructurer ensureTextContainerWithSortedLines() {
		if (this.textStructurer == null) {
			this.textStructurer = TextStructurer.createTextStructurerWithSortedLines(textCharacters, this);
		} else {
			this.textStructurer.sortLineByXandMakeTextLineByYCoordMap(textCharacters);
		}
		return this.textStructurer;
	}
	
	@Override
	public SVGG annotateChunk(List<? extends SVGElement> svgElements) {
		return annotateElements(svgElements, 0.2, 0.7, 5.0, "pink");
	}

	
	
	

	// ===========utils============================
	
	private void debug(String string, Map<Integer, TextLine> textByCoordMap) {
		if (textByCoordMap == null) {
			LOG.debug("No textCoordMap "+textStructurer.getTextLineByYCoordMap());
		} else {
			Set<Integer> keys = textByCoordMap.keySet();
			Integer[] ii = keys.toArray(new Integer[keys.size()]);
			Arrays.sort(ii);
			for (int iz : ii) {
				TextLine textList = textByCoordMap.get(iz);
				for (SVGText text : textList) {
					LOG.trace(">> "+text.getXY()+" "+text.getText()+ " ");
				}
			}
//			System.out.println();
		}
	}


	public List<SVGText> getTextCharacters() {
		return textCharacters;
	}
	
	public void setTextCharacters(List<SVGText> textCharacters) {
		this.textCharacters = textCharacters;
	}

	// =========== Delegates ============
	public Double getMainInterTextLineSeparation(int decimalPlaces) {
		return ensureTextContainerWithSortedLines().getMainInterTextLineSeparation(decimalPlaces);
	}

	public RealArray getInterTextLineSeparationArray() {
		return ensureTextContainerWithSortedLines().getInterTextLineSeparationArray();
	}

	public TextStructurer getTextStructurer() {
		return ensureTextContainerWithSortedLines();
	}

	public List<TextLine> getLinesWithLargestFont() {
		return ensureTextContainerWithSortedLines().getLinesWithCommonestFont();
	}

	public Real2Range getTextLinesLargestFontBoundingBox() {
		return ensureTextContainerWithSortedLines().getLargestFontBoundingBox();
	}

	public Integer getSerialNumber(TextLine textLine) {
		return ensureTextContainerWithSortedLines().getSerialNumber(textLine);
	}

	public List<String> getTextLineContentList() {
		return ensureTextContainerWithSortedLines().getTextLineContentList();
	}

	public void insertSpaces() {
		ensureTextContainerWithSortedLines().insertSpaces();
	}

	public RealArray getTextLineCoordinateArray() {
		return ensureTextContainerWithSortedLines().getTextLineCoordinateArray();
	}

	public Multimap<TextCoordinate, TextLine> getTextLineListByFontSize() {
		return ensureTextContainerWithSortedLines().getTextLineListByFontSize();
	}

	public void insertSpaces(double d) {
		ensureTextContainerWithSortedLines().insertSpaces(d);
	}

	public void getTextLineByYCoordMap() {
		ensureTextContainerWithSortedLines().getTextLineByYCoordMap();
	}

//	public RealArray getModalExcessWidthArray() {
//		return ensureTextContainerWithSortedLines().getModalExcessWidthArray();
//	}

	public Multiset<Double> createSeparationSet(int decimalPlaces) {
		return ensureTextContainerWithSortedLines().createSeparationSet(decimalPlaces);
	}

//	public List<Double> getActualWidthsOfSpaceCharactersList() {
//		return ensureTextContainerWithSortedLines().getActualWidthsOfSpaceCharactersList();
//	}

	public void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	public HtmlElement createHtmlElement() {
		LOG.trace("createHTMLParasAndDivs");
		List<TextLine> textLines = this.getLinesInIncreasingY();
		LOG.trace("lines "+textLines.size());
		for (TextLine textLine : textLines){
			LOG.trace(">> "+textLine);
		}
		ensureTextContainerWithSortedLines();
		htmlElement = textStructurer.createHtmlElement();
		if (htmlElement != null) {
			PageChunkAnalyzer.tidyStyles(htmlElement);
		}
		return htmlElement;
	}
	
//	//FIXME to use Splitters customized for different dataTypes  and parameters
//	/** splits svgg into textStructurers using a list of splitters
//	 * 
//	 * @param gOrig
//	 * @param chunkId
//	 * @param splitters
//	 * @return
//	 */
//	public List<TextStructurer> createSplitTextContainers(SVGG gOrig, ChunkId chunkId, Splitter ...splitters) {
//		TextStructurer textStructurer = new TextStructurer(this);
//		textStructurer.getScriptedLineList();
//		List<TextStructurer> splitTLCList = new ArrayList<TextStructurer>();
//		splitTLCList.add(textStructurer);
//		for (Splitter splitter : splitters) {
//			List<TextStructurer> newSplitTLCList = new ArrayList<TextStructurer>();
//			for (TextStructurer tlc : splitTLCList) {
//				List<TextStructurer> splitList = textStructurer.split(splitter);
//				LOG.debug("SPLIT: "+splitList);
//				newSplitTLCList.addAll(splitList);
//			}
//			splitTLCList = newSplitTLCList;
//		}
//		LOG.debug("SPLIT "+splitTLCList.size());
//		return splitTLCList;
//	}

	/** counter is container counter
	 * 
	 * @param analyzerX
	 * @param suffix
	 * @param pageAnalyzer
	 * @return
	 */
	@Override
	public List<AbstractContainer> createContainers(PageAnalyzer pageAnalyzer) {
		TextStructurer textStructurer1 = this.getTextStructurer();
		textStructurer1.getScriptedLineList();
		List<TextStructurer> splitList = textStructurer1.splitOnFontBoldChange(-1);
		List<TextStructurer> textStructurerList = splitList;
		LOG.trace(" split LIST "+textStructurerList.size());
		if (textStructurerList.size() > 1) {
			splitBoldHeaderOnFontSize(textStructurerList);
		}
		ensureAbstractContainerList();
		for (TextStructurer textStructurer : textStructurerList) {
			ScriptContainer scriptContainer = ScriptContainer.createScriptContainer(textStructurer, pageAnalyzer);
			scriptContainer.setChunkId(this.getChunkId());
			abstractContainerList.add(scriptContainer);
		}
		return abstractContainerList;
	}

	private void splitBoldHeaderOnFontSize(List<TextStructurer> textStructurerList) {
		TextStructurer textStructurer0 = textStructurerList.get(0);
		if (textStructurer0.getScriptedLineList().size() > 1) {
			textStructurer0.getScriptedLineList();
			List<TextStructurer> splitList = textStructurer0.splitOnFontSizeChange(999);
			List<TextStructurer> fontSplitList = splitList;
			if (fontSplitList.size() > 1) {
				int index = textStructurerList.indexOf(textStructurer0);
				textStructurerList.remove(index);
				for (TextStructurer splitTC : fontSplitList) {
					textStructurerList.add(index++, splitTC);
				}
				LOG.trace("SPLIT FONT");
			}
		}
	}


	
}
