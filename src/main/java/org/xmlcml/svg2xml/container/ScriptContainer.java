package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.svg2xml.analyzer.PDFIndex;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.StyleSpan;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextStructurer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class ScriptContainer extends AbstractContainer implements Iterable<ScriptLine> {

	public enum Side {
		LEFT,
		RIGHT,
	};

	public final static Logger LOG = Logger.getLogger(ScriptContainer.class);

	private static final double FONT_EPS = 0.01;

	private Multiset<String> fontFamilySet;
	private List<ScriptLine> scriptList;
	Multiset<Double> leftIndentSet;

	private Double leftIndent0;

	private Double leftIndent1;
	
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
		scriptContainer.add(scriptedLineList);
		return scriptContainer;
	}
	
	@Override
	public HtmlElement createHtmlElement() {
		HtmlDiv divElement = new HtmlDiv();
		List<List<StyleSpan>> styleSpanListList = this.getStyleSpanListList();
		for (int i = 0; i < styleSpanListList.size(); i++) {
			List<StyleSpan> styleSpanList = styleSpanListList.get(i);
			for (int j = 0; j < styleSpanList.size(); j++) {
				StyleSpan styleSpan = styleSpanList.get(j);
				HtmlElement htmlElement = styleSpan.getHtmlElement();
				addJoiningSpace(htmlElement);
				divElement.appendChild(htmlElement);
			}
		}
		return divElement;
	}
	
	private void addJoiningSpace(HtmlElement htmlElement) {
		String value = htmlElement.getValue();
		if (!(value.endsWith(".")) && !(value.endsWith("-"))) {
			HtmlElement spaceElement = new HtmlSpan();
			spaceElement.setValue(" ");
			htmlElement.appendChild(spaceElement);
		}
	}


	public List<ScriptLine> getScriptLineList() {
		return scriptList;
	}

	public void add(ScriptLine scriptLine) {
		ensureScriptList();
		scriptList.add(scriptLine);
	}

	private void ensureScriptList() {
		if (scriptList == null) {
			this.scriptList = new ArrayList<ScriptLine>();
		}
	}

	public SVGG createSVGGChunk() {
		SVGG g = new SVGG();
		for (ScriptLine scriptLine : scriptList) {
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
		this.scriptList.addAll(scriptList);
	}

	public Double getSingleFontSize() {
		Double fontSize = null;
		for (ScriptLine script : scriptList) {
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
		for (ScriptLine script : scriptList) {
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
		for (ScriptLine script : scriptList) {
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
			for (ScriptLine script : scriptList) {
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
		for (ScriptLine script : scriptList) {
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
		StringBuilder sb = new StringBuilder(">>>Script>>>"+" lines: "+scriptList.size()+"\n");
		for (ScriptLine script : scriptList) {
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
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()+" lines: "+scriptList.size()+"\n");
		for (ScriptLine script : scriptList) {
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
		Double fontSize = getSingleFontSize();
		Boolean isBold = isBold();
		if (isBold != null && isBold) {
			pdfIndex.addToBoldIndex(fontSize, this);
		}
	}
	
	@Override
	public String getRawValue() {
		StringBuilder sb = new StringBuilder();
		for (ScriptLine script : scriptList) {
			sb.append(script.getRawValue());
		}
		return sb.toString();
	}

	public Iterator<ScriptLine> iterator() {
		return scriptList.iterator();
	}

	public List<List<StyleSpan>> getStyleSpanListList() {
		List<List<StyleSpan>> styleSpanListList = new ArrayList<List<StyleSpan>>();
		for (ScriptLine script : scriptList) {
			if (script == null) continue;
			List<StyleSpan> styleSpanList = script.getStyleSpanList();
			styleSpanListList.add(styleSpanList);
		}
		return styleSpanListList;
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
