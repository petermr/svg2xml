package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.svg2xml.analyzer.ChunkId;
import org.xmlcml.svg2xml.analyzer.PDFIndex;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.analyzer.PageIO;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.StyleSpan;
import org.xmlcml.svg2xml.text.StyleSpans;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextStructurer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class ScriptContainer extends AbstractContainer implements Iterable<ScriptLine> {

	public enum Side {
		LEFT,
		RIGHT,
	};

	public final static Logger LOG = Logger.getLogger(ScriptContainer.class);

	private static final double FONT_EPS = 0.01;

	private static final String SOFT_HYPHEN = "~";

	private Multiset<String> fontFamilySet;
	private List<ScriptLine> scriptLineList;
	Multiset<Double> leftIndentSet;

	private Double leftIndent0;
	private Double leftIndent1;

	private TextStructurer textStructurer;
	
	public ScriptContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public static ScriptContainer createScriptContainer(TextStructurer textStructurer, PageAnalyzer pageAnalyzer) {
		List<TextLine> textLineList = textStructurer.getTextLineList();
		for (TextLine textLine : textLineList) {
			LOG.trace("TLSC "+textLine);
		}
		ScriptContainer scriptContainer = new ScriptContainer(pageAnalyzer);
		List<ScriptLine> scriptedLineList = textStructurer.getScriptedLineList();
		for (ScriptLine scriptLine : scriptedLineList) {
			LOG.trace("SCL "+scriptLine);
		}
		scriptContainer.setTextStructurer(textStructurer);
		scriptContainer.add(scriptedLineList);
		return scriptContainer;
	}
	
	private void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	@Override
	public HtmlElement createHtmlElement() {
		if (htmlElement == null) {
			htmlElement = new HtmlDiv();
			if (svgChunk != null) htmlElement.setId(svgChunk.getId());
			List<StyleSpans> styleSpansList = this.getStyleSpansList();
			for (int i = 0; i < styleSpansList.size(); i++) {
				StyleSpans styleSpans = styleSpansList.get(i);
				for (int j = 0; j < styleSpans.size(); j++) {
					StyleSpan styleSpan = styleSpans.get(j);
					HtmlElement htmlElement1 = styleSpan.getHtmlElement();
					addJoiningSpace(htmlElement1);
					PageIO.copyChildElementsFromTo(htmlElement1, htmlElement);
				}
			}
			cleanSpaceSpans(htmlElement);
			cleanEmptySpans(htmlElement);
			cleanMultipleSpaces(htmlElement);
		}
		return htmlElement;
	}
	
	/** remove any spans with just whitespace
	 * 
	 * @param htmlElement
	 */
	private void cleanSpaceSpans(HtmlElement htmlElement) {
		Nodes spans = htmlElement.query("//*[local-name()='span' and count(*) = 0 and text()[normalize-space(.)='']]");
		for (int i = 0; i < spans.size(); i++) {
			Element span = (Element) spans.get(i);
			String value = span.getValue();
			ParentNode parent = span.getParent();
			parent.replaceChild(span, new Text(value));
		}
	}

	/** remove any spans with just whitespace
	 * 
	 * @param htmlElement
	 */
	private void cleanMultipleSpaces(HtmlElement htmlElement) {
		Nodes spans = htmlElement.query("//text()[normalize-space(.)='' and string-length(.) > 1]");
		for (int i = 0; i < spans.size(); i++) {
			Text text = (Text) spans.get(i);
			text.setValue(" ");
		}
	}

	/** remove any spans with just whitespace
	 * 
	 * @param htmlElement
	 */
	private void cleanEmptySpans(HtmlElement htmlElement) {
		Nodes spans = htmlElement.query("//*[local-name()='span' and count(node()) = 0]");
		for (int i = 0; i < spans.size(); i++) {
			Element span = (Element) spans.get(i);
			span.detach();
		}
	}

	private void addJoiningSpace(HtmlElement htmlElement) {
		String value = htmlElement.getValue();
		HtmlElement spaceElement = new HtmlSpan();
		if (/*!(value.endsWith(".")) && */ !(value.endsWith("-"))) {
			spaceElement.setValue(" ");
		} else {
			addSoftHyphen(spaceElement);
			LOG.trace("no space: "+value);
		}
		htmlElement.appendChild(spaceElement);
	}

	private void addSoftHyphen(HtmlElement spaceElement) {
		Nodes texts = spaceElement.query("//text()");
		if (texts.size() > 0 ) {
			Text lastText = (Text) texts.get(texts.size() - 1);
			String textValue = lastText.getValue();
			textValue += SOFT_HYPHEN;
			lastText.setValue(textValue);
			LOG.debug(".. "+lastText);
		}
	}


	public List<ScriptLine> getScriptLineList() {
		return scriptLineList;
	}

	public void add(ScriptLine scriptLine) {
		ensureScriptList();
		scriptLineList.add(scriptLine);
	}

	private void ensureScriptList() {
		if (scriptLineList == null) {
			this.scriptLineList = new ArrayList<ScriptLine>();
		}
	}

	public SVGG createSVGGChunk() {
		SVGG g = new SVGG();
		for (ScriptLine scriptLine : scriptLineList) {
			if (scriptLine != null) {
				List<SVGText> textList = scriptLine.getTextList();
				for (SVGText text : textList) {
					g.appendChild(new SVGText(text));
				}
			}
		}
		return g;
	}

	public void add(List<ScriptLine> scriptList) {
		ensureScriptList();
		this.scriptLineList.addAll(scriptList);
	}

	public Double getSingleFontSize() {
		Double fontSize = null;
		for (ScriptLine script : scriptLineList) {
			if (script == null) continue;
			Double size = script.getFontSize();
			if (fontSize == null) {
				fontSize = size;
			} else {
				if (fontSize == null || size == null || !Real.isEqual(fontSize, size, FONT_EPS)) {
					return null;
				}
			}
		}
		return fontSize;
	}

	public Double getLargestFontSize() {
		Double fontSize = null;
		for (ScriptLine script : scriptLineList) {
			Double size = script.getFontSize();
			if (fontSize == null) {
				fontSize = size;
			} else {
				if (size > fontSize) {
					fontSize = size;
				}
			}
		}
		return fontSize;
	}

	public String getSingleFontFamily() {
		String fontFamily = null;
		for (ScriptLine script : scriptLineList) {
			String family = script.getFontFamily();
			if (fontFamily == null) {
				fontFamily = family;
			} else {
				if (!fontFamily.equals(family)) {
					return null;
				}
			}
		}
		return fontFamily;
	}

	/** creates a multiset from addAll() on multisets for each line
	 *  
	 * @return
	 */
	public Multiset<String> getFontFamilyMultiset() {
		if (fontFamilySet == null) {
			fontFamilySet = HashMultiset.create();
			for (ScriptLine script : scriptLineList) {
				String family = script.getFontFamily();
				fontFamilySet.add(family);
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

	public Boolean isBold() {
		Boolean fontWeight = null;
		for (ScriptLine script : scriptLineList) {
			if (script == null) continue;
			boolean weight = script.isBold();
			if (fontWeight == null) {
				fontWeight = weight;
			} else {
				if (!fontWeight.equals(weight)) {
					return null;
				}
			}
		}
		return fontWeight;
	}

	@Override 
	public String summaryString() {
		StringBuilder sb = new StringBuilder(">>>Script>>>"+" lines: "+scriptLineList.size()+"\n");
		for (ScriptLine script : scriptLineList) {
			if (script != null) {
				sb.append(script.summaryString()+"");
			}
		}
		sb.append("<<<Script<<<");
		String s = sb.toString();
		return s;
	}
	
	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()+" lines: "+scriptLineList.size()+"\n");
		for (ScriptLine script : scriptLineList) {
			if (script != null) {
				sb.append(script.toString());
			}
		}
		String s = sb.toString();
		return s;
	}

	public void addToBoldIndex(Double fontSize) {
		throw new RuntimeException("index "+fontSize);
	}

	public void addToIndexes(PDFIndex pdfIndex) {
		indexBoldTextByFontSize(pdfIndex);
		indexByTextContent(pdfIndex);
	}

	private void indexByTextContent(PDFIndex pdfIndex) {
		String content = getTextContentWithSpaces();
		pdfIndex.indexByTextContent(content, this.getChunkId());
	}

	public ChunkId getChunkId() {
		super.getChunkId();
		if (this.chunkId == null) {
			this.chunkId = textStructurer == null ? null : textStructurer.getChunkId();
			if (chunkId == null) {
				chunkId = pageAnalyzer.getChunkId();
			}
		} 
		return this.chunkId;
	}

	public String getTextContentWithSpaces() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (ScriptLine scriptLine : scriptLineList) {
			String s = scriptLine.getTextContentWithSpaces();
			if (i++ > 0) sb.append(" ");
			sb.append(s);
		}
		return sb.toString();
	}

	private void indexBoldTextByFontSize(PDFIndex pdfIndex) {
		Double fontSize = getSingleFontSize();
		Boolean isBold = isBold();
		if (isBold != null && isBold) {
			pdfIndex.addToBoldIndex(fontSize, this);
		}
	}
	
	@Override
	public String getRawValue() {
		StringBuilder sb = new StringBuilder();
		for (ScriptLine script : scriptLineList) {
			sb.append(script.getRawValue());
		}
		return sb.toString();
	}

	public Iterator<ScriptLine> iterator() {
		return scriptLineList.iterator();
	}

	public List<StyleSpans> getStyleSpansList() {
		List<StyleSpans> styleSpansList = new ArrayList<StyleSpans>();
		for (ScriptLine script : scriptLineList) {
			if (script == null) continue;
			StyleSpans styleSpans = script.getStyleSpans();
			styleSpansList.add(styleSpans);
		}
		return styleSpansList;
	}
	
	void createLeftIndent01() {
		setLeftIndent0(null);
		setLeftIndent1(null);
		for (Double d : leftIndentSet.elementSet()) {
			if (getLeftIndent0() == null) {
				setLeftIndent0(d);
			} else {
				if (d < getLeftIndent0()) {
					setLeftIndent1(getLeftIndent0());
					setLeftIndent0(d);
				} else {
					setLeftIndent1(d);
				}
			}
		}
	}


	Multiset<Double> createLeftIndentSet(int decimalPlaces) {
		if (leftIndentSet == null) {
			leftIndentSet = HashMultiset.create();
			for (ScriptLine scriptLine : this) {
				Real2Range boundingBox = scriptLine.getBoundingBox();
				boundingBox.format(decimalPlaces);
				Double leftIndent = boundingBox.getXRange().getMin();
				LOG.debug("BB "+boundingBox+" / "+leftIndent+" / "+((int)scriptLine.toString().charAt(0))+" / "+scriptLine);
				leftIndentSet.add(leftIndent);
			}
		}
		return leftIndentSet;
	}

	public Multiset<Double> getLeftIndentSet() {
		return leftIndentSet;
	}

	public Double getLeftIndent0() {
		return leftIndent0;
	}

	public void setLeftIndent0(Double leftIndent0) {
		this.leftIndent0 = leftIndent0;
	}

	public Double getLeftIndent1() {
		return leftIndent1;
	}

	public void setLeftIndent1(Double leftIndent1) {
		this.leftIndent1 = leftIndent1;
	}


}
