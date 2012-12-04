package org.xmlcml.svgplus.analyzer;

import java.util.List;


import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.action.SemanticDocumentActionX;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.tools.PlotBox;

public class ChunkAnalyzerX extends AbstractPageAnalyzerX {

	private static final Logger LOG = Logger.getLogger(ChunkAnalyzerX.class);

	private static final int MIN_LINE_COUNT = 10;
	public static final int PLACES = 6;

	private List<SVGText> texts;
	private List<SVGPath> pathList;
	private TextAnalyzerX textAnalyzerX;
	private List<SVGLine> lines;
	private LineAnalyzerX lineAnalyzerX;
	private List<SVGPolyline> polylines;
	private PolylineAnalyzerX polylineAnalyzerX;
	private Chunk chunk;
	private PlotBox plotBox;


	public ChunkAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}

	public void analyzeChunk(Chunk chunk) {
		this.chunk = chunk;
		ensurePaths();
//		debugLeaf();
		analyzeTexts();
		analyzePaths();
		analyzeLines();
		analyzePolylines();
	}

	private void ensurePaths() {
		if (pathList == null) {
			pathList = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(chunk, ".//svg:path"));
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
	
	private void analyzeTexts() {
		analyzeTexts(0);
		analyzeTexts(90);
		analyzeTexts(180);
	}

	private void analyzeTexts(int angle) {
		ensureTextAnalyzer();
		String angleCondition = (angle == 0) ? "@angle='0' or not(@angle)" : "@angle='"+angle+"'";
		texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgg, ".//svg:text["+angleCondition+"]"));
		LOG.trace("ROT "+angle+": "+texts.size());
		if (texts.size() > 0) {
//			textAnalyzer = new TextAnalyzer(pageEditor);
			textAnalyzerX.analyzeTexts(svgg, texts);
		}
	}
	
	private TextAnalyzerX ensureTextAnalyzer() {
		if (textAnalyzerX == null) {
			textAnalyzerX = new TextAnalyzerX(semanticDocumentActionX);
		}
		return textAnalyzerX;
	}

	private void analyzePaths() {
		throw new RuntimeException("NYI");
	}

	private void analyzeLines() {
		lines = SVGLine.extractLines(SVGUtil.getQuerySVGElements(svgg, ".//svg:line"));
		if (lines.size() > 0) {
			lineAnalyzerX = new LineAnalyzerX(semanticDocumentActionX);
			lineAnalyzerX.analyzeLinesAsAxesAndWhatever(svgg, lines);
		}
	}

	public TextAnalyzerX getTextAnalyzerX() {
		ensureTextAnalyzer();
		return textAnalyzerX;
	}

	private void analyzePolylines() {
		polylines = SVGPolyline.extractPolylines(SVGUtil.getQuerySVGElements(svgg, ".//svg:polyline"));
		if (polylines.size() > 0) {
			polylineAnalyzerX = new PolylineAnalyzerX(semanticDocumentActionX);
			polylineAnalyzerX.analyzePolylines(svgg, polylines);
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
