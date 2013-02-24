package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlP;

/** A paragraph groups Words into a section determined by geometrical features
 * 
 * normally starts at the start of a textChunk or a left-side indent or a large interline separation
 * 
 * The words are NOT rooted with coordinates, but the origin of the paragraph is given.
 * The para has a single SVGText string with no breaks for display
 * Later we will create flowing text
 * 
 * @author pm286
 *
 */
public class Paragraph extends SVGG {
	public static final String PARA = "para";
	public static final String NAME = "name";

	private final static Logger LOG = Logger.getLogger(Paragraph.class);

	private static final double DEFAULT_FONT_SIZE = 8.0;
	private List<Word> wordList = new ArrayList<Word>();
	private StringBuilder sb = null;
	private SVGText svgText = null;
	
	public Paragraph() {
		this.addAttribute(new Attribute(NAME, PARA));
	}
	
	public static Paragraph createElement(SVGElement elem) {
		Paragraph paragraph = null;
		if (PARA.equals(elem.getAttributeValue(NAME))) {
			paragraph = new Paragraph();
			paragraph.copyAttributes(elem);
			paragraph.copyChildrenFrom(elem);
			elem.getParent().replaceChild(elem,  paragraph);
		}
		return paragraph;
	}

	/** adds a word and also takes care of hyphenation
	 * 
	 * @param word
	 */
	private void addWordAndElideHyphenation(Word word) {
		if (wordList == null) {
			wordList = new ArrayList<Word>();
			wordList.add(word);
		} else {
			Word lastWord = (wordList.size() == 0) ? null : wordList.get(wordList.size()-1); 
			// hyphenated?
			if (lastWord != null && lastWord.endsWith(CMLConstants.S_MINUS) &&
				Character.isLowerCase(word.getCharAt(0))) {
					String lastString = lastWord.getStringValue();
					String thisString = word.getStringValue();
					lastWord.setStringValue(lastString.substring(0,  lastString.length()-1)+thisString);
			} else {
				wordList.add(word);
			}
		}
	}

	/** adds words including paragraph breaks to single paragraph
	 * we split later
	 * @param wordSequence
	 */
	void addWordSequence(WordSequence wordSequence) {
		for (Word word : wordSequence) {
			this.addWordAndElideHyphenation(word);
		}
	}

	/** if embedded markers have been added, splits at them and adds to paragraph list
	 * markers are discarded.
	 *  
	 * @return
	 */
	List<Paragraph> splitAtParagraphBreaks() {
		List<Paragraph> paragraphList = new ArrayList<Paragraph>();
		Paragraph paragraph = null;
		for (Word word : wordList) {
			if (paragraph == null) {
				paragraph = new Paragraph();
				paragraphList.add(paragraph);
			}
			if (word.isParagraphMarker()) {
				paragraph = null;
			} else {
				paragraph.addWordAndElideHyphenation(word);
			}
		}
		return paragraphList;
	}
	
	public String getStringValue() {
		if (sb == null) {
			sb = new StringBuilder();
			int i = 0;
			for (Word word : wordList) {
				if (i++ > 0) {
					sb.append(CMLConstants.S_SPACE);
				}
				sb.append(word.getStringValue());
			}
		}
		return sb.toString();
	}

	public List<Word> getWordList() {
		return wordList;
	}
	
	public SVGText getText() {
		return svgText;
	}
	
	public void setText(Real2 xy) {
		if (svgText == null) {
			svgText = new SVGText();
			this.appendChild(svgText);
		}
		svgText.setXY(xy);
		svgText.setText(getStringValue());
		svgText.setFontSize(DEFAULT_FONT_SIZE);
	}
	
	public HtmlP createAndAddHTML() {
		this.debug("PPP");
		HtmlP p = null;
		List<SVGElement> svgTexts = SVGUtil.getQuerySVGElements(this, "./svg:text");
		svgText = (svgTexts.size() == 1) ? (SVGText) svgTexts.get(0) : null;
		if (svgText != null) {
			String s = svgText.getText();
			p = new HtmlP();
			p.appendChild(s);
			this.appendChild(p);
			this.setXY(svgText.getXY());
			this.debug("PARA");
			this.addAttribute(new Attribute("HTML", "P"));
		}
		return p;
	}

	public void removeOriginalSVGText() {
		List<SVGElement> textList = SVGUtil.getQuerySVGElements(this, "../../*[self::svg:g]/svg:text");
		LOG.trace("Detaching text "+textList.size());
		for (SVGElement text : textList) {
			if (text instanceof SVGText) {
				text.detach();
			}
		}
	}

	public void removeSVGText() {
		if (svgText != null) {
			svgText.detach();
		}
	}
}
