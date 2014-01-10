package org.xmlcml.svg2xml.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer.TextOrientation;
import org.xmlcml.svg2xml.text.RawWords;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.text.Word;
import org.xmlcml.svgbuilder.geom.Junction;
import org.xmlcml.svgbuilder.geom.SimpleBuilder;

/**
 * Builds higher-level primitives from SVGPaths, SVGLines, etc. to create SVG objects 
 * such as TramLine and (later) Arrow.
 * 
 * <p>GeometryBuilder's main function is to:
 * <ul>
 * <li>Read derived lists of SVGPath and SVGText SVGRect, SVGCircle, SVGPoly, SVGLine </li>
 * <li>identify Junctions (line-line, line-text, and probably more)</li>
 * <li>join lines where they meet into higher level objects (TramLines, SVGRect, crosses, arrows, etc.)</li>
 * <li>create topologies (e.g. connection of lines and Junctions)</li>
 * </ul>
 * 
 * GeometryBuilder uses the services of the org.xmlcml.graphics.svg.path package and may later use
 * org.xmlcml.graphics.svg.symbol.
 * </p>
 * 
 * <p>Input lists of SVGText and SVGShape
 * </p>
 * 
 * <h3>Strategy</h3>
 * 
 * UPDATE: 2013-10-23Split into GeometryBuilder and "SimpleBuilder" as it doesn't deal with Words (which
 * require TextStructurer.) it's possible the whole higherlevel primitive stuff should be removed to another
 * project.
 * 
 * @author pm286
 *
 */
public class GeometryBuilder extends SimpleBuilder {

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

	public List<SVGPath> createArraysFromPaths() {
		List<SVGPath> pathList = SVGPath.extractPaths(getSVGRoot());
		return pathList;
	}

//	public List<Junction> getRawJunctionList() {
//		return higherPrimitives == null ? null : higherPrimitives.getRawJunctionList();
//	}
//
//	public List<Junction> getMergedJunctionList() {
//		return higherPrimitives == null ? null : higherPrimitives.getMergedJunctionList();
//	}

	public List<SVGPolygon> getPolygonList() {
		return rawPrimitives.getPolygonList();
	}

	public List<SVGPath> getPathList() {
		return rawPrimitives.getPathList();
	}

	public List<SVGPolyline> getPolylineList() {
		return rawPrimitives.getPolylineList();
	}


}
