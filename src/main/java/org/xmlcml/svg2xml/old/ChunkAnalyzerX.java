package org.xmlcml.svg2xml.old;

import java.util.List;


import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.page.PageChunkAnalyzer;
import org.xmlcml.svg2xml.page.PathAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.paths.LineAnalyzer;
import org.xmlcml.svg2xml.paths.PolylineAnalyzer;
import org.xmlcml.svg2xml.tools.Chunk;
import org.xmlcml.svg2xml.tools.PlotBox;

public class ChunkAnalyzerX extends PageChunkAnalyzer {

	private static final Logger LOG = Logger.getLogger(ChunkAnalyzerX.class);

	private static final int MIN_LINE_COUNT = 10;
	public static final int PLACES = 6;

	private List<SVGText> texts;
	private List<SVGPath> paths;
	private TextAnalyzer textAnalyzerX;
	private PathAnalyzer pathAnalyzerX;
	private List<SVGLine> lines;
	private LineAnalyzer lineAnalyzerX;
	private List<SVGPolyline> polylines;
	private PolylineAnalyzer polylineAnalyzerX;
	private Chunk chunk;
	private PlotBox plotBox;

	public ChunkAnalyzerX() {
		super();
	}

	public void analyzeChunk(Chunk chunk) {
		this.chunk = chunk;
		textAnalyzerX = analyzeTexts();
		analyzePaths();
		analyzeLines();
		analyzePolylines();
	}

	private void ensurePaths() {
		if (paths == null) {
			paths = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(chunk, ".//svg:path"));
		}
	}

	public List<SVGText> getTextCharacters() {
		throw new RuntimeException("NYI");
	}
	
	public List<SVGPath> getPaths() {
		throw new RuntimeException("NYI");
	}
	
	public List<SVGLine> getLines() {
		throw new RuntimeException("NYI");
	}
	
	public List<SVGRect> getRects() {
		throw new RuntimeException("NYI");
	}
	
	public List<SVGCircle> getCircles() {
		throw new RuntimeException("NYI");
	}
	
	public List<SVGPolyline> getPolylines() {
		throw new RuntimeException("NYI");
	}
	
	private TextAnalyzer analyzeTexts() {
		ensureTextAnalyzer();
		analyzeTexts(0);
		analyzeTexts(90);
		analyzeTexts(180);
		return textAnalyzerX;
	}

	private void analyzeTexts(int angle) {
		String angleCondition = (angle == 0) ? "@angle='0' or not(@angle)" : "@angle='"+angle+"'";
		texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(chunk, ".//svg:text["+angleCondition+"]"));
		LOG.trace("ROT "+angle+": "+texts.size());
		if (texts.size() > 0) {
			textAnalyzerX.analyzeTexts(texts);
		}
	}
	
	private PageChunkAnalyzer ensureTextAnalyzer() {
		if (textAnalyzerX == null) {
			textAnalyzerX = new TextAnalyzer();
		}
		return textAnalyzerX;
	}

	private void analyzePaths() {
		ensurePaths();
		if (paths.size() > 0) {
			pathAnalyzerX = new PathAnalyzer();
			pathAnalyzerX.interpretPathsAsRectCirclePolylineAndReplace();
		}
	}

	private void analyzeLines() {
		lines = SVGLine.extractLines(SVGUtil.getQuerySVGElements(chunk, ".//svg:line"));
		if (lines.size() > 0) {
			lineAnalyzerX = new LineAnalyzer();
			lineAnalyzerX.analyzeLinesAsAxesAndWhatever(chunk, lines);
		}
	}

	public TextAnalyzer getTextAnalyzerX() {
		ensureTextAnalyzer();
		return textAnalyzerX;
	}

	private void analyzePolylines() {
		polylines = SVGPolyline.extractPolylines(SVGUtil.getQuerySVGElements(chunk, ".//svg:polyline"));
		if (polylines.size() > 0) {
			polylineAnalyzerX = new PolylineAnalyzer();
			polylineAnalyzerX.analyzePolylines(chunk, polylines);
		}
	}

	public PlotBox getPlotBox() {
		if (plotBox == null) {
			if (lineAnalyzerX != null) {
				plotBox = lineAnalyzerX.getPlotBox();
			}
		}
		return plotBox;
	}

}
