package org.xmlcml.svg2xml.analyzer;


import java.util.ArrayList;
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
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.ScriptContainer;
import org.xmlcml.svg2xml.text.SvgPlusCoordinate;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.text.TextStructurer.Splitter;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/** attempts to assemble characters into meaningful text
 * 
 * @author pm286
 *
 */
public class TextAnalyzerX extends AbstractAnalyzer {

	private final static Logger LOG = Logger.getLogger(TextAnalyzerX.class);
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
	private HtmlElement createdHtmlElement;
	
	public TextAnalyzerX() {
		super();
	}
	
	public TextAnalyzerX(List<SVGText> characterList) {
		this.setTextCharacters(characterList);
	}

	public TextAnalyzerX(SVGElement svgElement) {
		this(SVGText.extractTexts(svgElement));
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

	/** creates one "para" per line
	 * usually needs tidying with createHtmlDivWithParas
	 * @return
	 */
	public HtmlElement createHtmlRawDiv() {
		ensureTextContainerWithSortedLines();
//		List<TextLine> textLineList = textContainer.getLinesWithLargestFont();
		List<TextLine> textLineList = textStructurer.getLinesWithCommonestFont();
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
	public static HtmlElement createHtmlRawDiv(List<TextLine> linesToBeAnalyzed) {
		HtmlDiv div = new HtmlDiv();
		for (TextLine textLine : linesToBeAnalyzed) {
			HtmlElement p = textLine.createHtmlLine();
			div.appendChild(p);
		}
		return div;
	}

	public HtmlElement createHtmlDivWithParas() {
		ensureTextContainerWithSortedLines();
		HtmlElement div = textStructurer.createHtmlDivWithParas();
		return div;
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

	public TextStructurer getTextContainer() {
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

	public Multimap<SvgPlusCoordinate, TextLine> getTextLineListByFontSize() {
		return ensureTextContainerWithSortedLines().getTextLineListByFontSize();
	}

	public void insertSpaces(double d) {
		ensureTextContainerWithSortedLines().insertSpaces(d);
	}

	public void getTextLineByYCoordMap() {
		ensureTextContainerWithSortedLines().getTextLineByYCoordMap();
	}

	public RealArray getModalExcessWidthArray() {
		return ensureTextContainerWithSortedLines().getModalExcessWidthArray();
	}

	public Multiset<Double> createSeparationSet(int decimalPlaces) {
		return ensureTextContainerWithSortedLines().createSeparationSet(decimalPlaces);
	}

	public List<Double> getActualWidthsOfSpaceCharactersList() {
		return ensureTextContainerWithSortedLines().getActualWidthsOfSpaceCharactersList();
	}

	public void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	public HtmlElement createHtml() {
		LOG.trace("createHTMLParasAndDivs");
		List<TextLine> textLines = this.getLinesInIncreasingY();
		LOG.trace("lines "+textLines.size());
		for (TextLine textLine : textLines){
			LOG.trace(">> "+textLine);
		}
		createdHtmlElement = this.createHtmlDivWithParas();
		if (createdHtmlElement != null) {
			AbstractAnalyzer.tidyStyles(createdHtmlElement);
		}
		return createdHtmlElement;
	}
	
	//FIXME to use Splitters customized for different dataTypes  and parameters
	/** splits svgg into textContainers using a list of splitters
	 * 
	 * @param gOrig
	 * @param chunkId
	 * @param splitters
	 * @return
	 */
	public List<TextStructurer> createSplitTextContainers(SVGG gOrig, ChunkId chunkId, Splitter ...splitters) {
		TextStructurer textContainer = new TextStructurer(this);
		textContainer.getScriptedLineList();
		List<TextStructurer> splitTLCList = new ArrayList<TextStructurer>();
		splitTLCList.add(textContainer);
		for (Splitter splitter : splitters) {
			List<TextStructurer> newSplitTLCList = new ArrayList<TextStructurer>();
			for (TextStructurer tlc : splitTLCList) {
				List<TextStructurer> splitList = textContainer.split(splitter);
				LOG.debug("SPLIT: "+splitList);
				newSplitTLCList.addAll(splitList);
			}
			splitTLCList = newSplitTLCList;
		}
		LOG.debug("SPLIT "+splitTLCList.size());
		return splitTLCList;
	}

	/** counter is container counter
	 * 
	 * @param analyzerX
	 * @param suffix
	 * @param pageAnalyzer
	 * @return
	 */
	@Override
	public List<AbstractContainer> createContainers(PageAnalyzer pageAnalyzer) {
		TextStructurer textContainer1 = this.getTextContainer();
		textContainer1.getScriptedLineList();
		List<TextStructurer> splitList = textContainer1.splitOnFontBoldChange(-1);
		List<TextStructurer> textContainerList = splitList;
		LOG.trace(" split LIST "+textContainerList.size());
		if (textContainerList.size() > 1) {
			splitBoldHeaderOnFontSize(textContainerList);
		}
		ensureAbstractContainerList();
		for (TextStructurer textContainer : textContainerList) {
			ScriptContainer scriptContainer = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
//			scriptContainer.setChunkId(textContainer.getChunkId());
			scriptContainer.setChunkId(this.getChunkId());
			abstractContainerList.add(scriptContainer);
		}
		return abstractContainerList;
	}

	private void splitBoldHeaderOnFontSize(List<TextStructurer> textContainerList) {
		TextStructurer textContainer0 = textContainerList.get(0);
		if (textContainer0.getScriptedLineList().size() > 1) {
			textContainer0.getScriptedLineList();
			List<TextStructurer> splitList = textContainer0.splitOnFontSizeChange(999);
			List<TextStructurer> fontSplitList =
				splitList;
			if (fontSplitList.size() > 1) {
				int index = textContainerList.indexOf(textContainer0);
				textContainerList.remove(index);
				for (TextStructurer splitTC : fontSplitList) {
					textContainerList.add(index++, splitTC);
				}
				LOG.trace("SPLIT FONT");
			}
		}
	}


	
}
