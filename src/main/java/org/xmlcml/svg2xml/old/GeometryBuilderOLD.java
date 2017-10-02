package org.xmlcml.svg2xml.old;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.html.HtmlElement;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.text.build.WordNew;
import org.xmlcml.svg2xml.page.TextAnalyzerOLD;
import org.xmlcml.svg2xml.page.TextAnalyzerOLD.TextOrientation;
import org.xmlcml.svg2xml.text.RawWordsOLD;
import org.xmlcml.svg2xml.text.TextStructurerOLD;

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
 * require TextStructurer). It's possible the whole higher-level primitive stuff should be removed to another
 * project.
 * 
 * @author pm286
 *
 */
public class GeometryBuilderOLD extends SimpleBuilderOLD {

	private final static Logger LOG = Logger.getLogger(GeometryBuilderOLD.class);
	
	private List<WordNew> wordList;
	private TextStructurerOLD textStructurer;
	private TextOrientation textOrientation;
	private TextAnalyzerOLD textAnalyzer;

	public GeometryBuilderOLD(GraphicsElement svgElement) {
		super(svgElement);
		init();
	}
	
	private void init() {
		textAnalyzer = new TextAnalyzerOLD();
	}

	public List<WordNew> getWordList() {
		createWordList();
		return wordList;
	}
	
	/** 
	 * Gets list of space-separated words.
	 * <p>
	 * Should probably move elsewhere for more general use.
	 * 
	 * @return
	 */
	public List<WordNew> createWordList() {
		if (wordList == null) {
			createTextStructurerWithRotation();
			extractWordList();
		}
		return wordList;
	}

	private void extractWordList() {
		List<RawWordsOLD> rawWordsList = textStructurer.createRawWordsListFromTextLineList();
		for (RawWordsOLD rawWords : rawWordsList) {
			List<WordNew> rwordList = rawWords.getWordList();
			for (WordNew word :rwordList) {
				wordList.add(word);
			}
		}
	}

	private TextStructurerOLD createTextStructurerWithRotation() {
		wordList = new ArrayList<WordNew>();
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(getSVGRoot());
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

	public TextStructurerOLD getTextStructurer() {
//		ensureTextStructurer();
		return textStructurer;
	}

	public void setTextOrientation(TextOrientation orientation) {
		this.textOrientation = orientation;
	}

	public HtmlElement createHtmlElement() {
		textStructurer = createTextStructurerWithRotation();
		return (textStructurer == null ? null : textStructurer.createHtmlElement());
	}

	public List<SVGPath> createArraysFromPaths() {
		List<SVGPath> pathList = SVGPath.extractPaths(getSVGRoot());
		return pathList;
	}
	
	/*@Override
	protected void createJoinableList() {
		super.createJoinableList();
		//TODO words
	}

	public List<Junction> getRawJunctionList() {
		return higherPrimitives == null ? null : higherPrimitives.getRawJunctionList();
	}

	public List<Junction> getMergedJunctionList() {
		return higherPrimitives == null ? null : higherPrimitives.getMergedJunctionList();
	}

	public List<SVGPolygon> getPolygonList() {
		return rawPrimitives.getPolygonList();
	}

	public List<SVGPath> getPathList() {
		return rawPrimitives.getPathList();
	}

	public List<SVGPolyline> getPolylineList() {
		return rawPrimitives.getPolylineList();
	}*/

}
