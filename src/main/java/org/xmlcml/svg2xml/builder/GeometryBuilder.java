package org.xmlcml.svg2xml.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.builder.SimpleGeometryBuilder;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer.TextOrientation;
import org.xmlcml.svg2xml.text.RawWords;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.text.Word;

/** adds text functionality to Geometry Building.
 * 
 * @author pm286
 *
 */
public class GeometryBuilder extends SimpleGeometryBuilder {

	private final static Logger LOG = Logger.getLogger(GeometryBuilder.class);
	
	private List<Word> wordList;
	private TextStructurer textStructurer;
	private TextOrientation textOrientation;
	private TextAnalyzer textAnalyzer;

	public GeometryBuilder() {
		super();
		init();
	}

	public GeometryBuilder(SVGElement svgElement) {
		super(svgElement);
		init();
	}
	
	private void init() {
		this.textAnalyzer = new TextAnalyzer((PageAnalyzer)null);
	}

	public List<Word> getWordList() {
		createWordList();
		return wordList;
	}
	
	/** gets list of space-separated words.
	 * 
	 * should probably move elsewhere for more general use.
	 * 
	 * @return
	 */
	public List<Word> createWordList() {
		if (wordList == null) {
			createTextStructurerWithRotation();
			extractWordList();
		}
		return wordList;
	}

	private void extractWordList() {
		List<RawWords> rawWordsList = textStructurer.createRawWordsList();
		for (RawWords rawWords : rawWordsList) {
			List<Word> rwordList = rawWords.getWordList();
			for (Word word :rwordList) {
				wordList.add(word);
			}
		}
	}

	private TextStructurer createTextStructurerWithRotation() {
		wordList = new ArrayList<Word>();
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(this.getSVGRoot());
		textAnalyzer.setTextList(textList);
		textStructurer = textAnalyzer.getTextStructurer();
		if (textOrientation != null) {
			textAnalyzer.setTextList(textList);
			getRotatedTextAnalyzer();
			textStructurer = textAnalyzer.getTextStructurer();
		}
		return textStructurer;
	}

	private void getRotatedTextAnalyzer() {
		textAnalyzer.createRotatedTextAnalyzers();
		if (TextOrientation.ROT_0.equals(textOrientation)) {
			textAnalyzer = textAnalyzer.getRot0TextAnalyzer();
		} else if (TextOrientation.ROT_PI2.equals(textOrientation)) {
			textAnalyzer = textAnalyzer.getRotPi2TextAnalyzer();
		} else if (TextOrientation.ROT_PI.equals(textOrientation)) {
			textAnalyzer = textAnalyzer.getRotPiTextAnalyzer();
		} else if (TextOrientation.ROT_3PI2.equals(textOrientation)) {
			textAnalyzer = textAnalyzer.getRot3Pi2TextAnalyzer();
		}
	}

	public TextStructurer getTextStructurer() {
//		ensureTextStructurer();
		return textStructurer;
	}

	public void setTextOrientation(TextOrientation orientation) {
		this.textOrientation = orientation;
	}

	public HtmlElement createHtmlElement() {
		textStructurer = createTextStructurerWithRotation();
		return textStructurer == null ? null : textStructurer.createHtmlElement();
	}

}
