package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.analyzer.PDFIndex;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextStructurer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class ScriptContainer extends AbstractContainer implements Iterable<ScriptLine> {

	public final static Logger LOG = Logger.getLogger(ScriptContainer.class);

	private static final double FONT_EPS = 0.01;

	private Multiset<String> fontFamilySet;

	private List<ScriptLine> scriptList;
	
	public ScriptContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public static ScriptContainer createScriptContainer(TextStructurer textContainer, PageAnalyzer pageAnalyzer) {
		ScriptContainer scriptContainer = new ScriptContainer(pageAnalyzer);
		scriptContainer.add(textContainer.getScriptedLineList());
		return scriptContainer;
	}
	
	@Override
	public HtmlElement createHtmlElement() {
		HtmlDiv divElement = new HtmlDiv();
		HtmlP p = new HtmlP();
		p.appendChild("Script container NYI");
		divElement.appendChild(p);
		return divElement;
	}

	public List<ScriptLine> getScriptList() {
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
				if (!Real.isEqual(fontSize, size, FONT_EPS)) {
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
	

}
