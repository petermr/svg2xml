package org.xmlcml.svgplus.command;

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
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.paths.LineAnalyzer;
import org.xmlcml.svgplus.paths.PolylineAnalyzer;
import org.xmlcml.svgplus.text.TextAnalyzer;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.tools.PlotBox;

public class ChunkAnalyzer extends AbstractPageAnalyzer {

	private static final Logger LOG = Logger.getLogger(ChunkAnalyzer.class);

	private static final int MIN_LINE_COUNT = 10;
	public static final int PLACES = 6;

	private List<SVGText> texts;
	private List<SVGPath> pathList;
	private TextAnalyzer textAnalyzer;
	private List<SVGLine> lines;
	private LineAnalyzer lineAnalyzer;
	private List<SVGPolyline> polylines;
	private PolylineAnalyzer polylineAnalyzer;
	private Chunk chunk;
	private PlotBox plotBox;

	@Deprecated
	public ChunkAnalyzer(SemanticDocumentAction semanticDocumentAction) {
		super(semanticDocumentAction);
	}

	public ChunkAnalyzer(SemanticDocumentActionX semanticDocumentActionX) {
//		super(semanticDocumentActionX);
		// FIXME 
		throw new RuntimeException("change ChunkAnalyzer to use new classes");
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
			textAnalyzer.analyzeTexts(svgg, texts);
		}
	}
	
	private TextAnalyzer ensureTextAnalyzer() {
		if (textAnalyzer == null) {
			textAnalyzer = new TextAnalyzer(semanticDocumentAction);
		}
		return textAnalyzer;
	}

	private void analyzePaths() {
		throw new RuntimeException("NYI");
	}

	private void analyzeLines() {
		lines = SVGLine.extractLines(SVGUtil.getQuerySVGElements(svgg, ".//svg:line"));
		if (lines.size() > 0) {
			lineAnalyzer = new LineAnalyzer(semanticDocumentAction);
			lineAnalyzer.analyzeLinesAsAxesAndWhatever(svgg, lines);
		}
	}

	public TextAnalyzer getTextAnalyzer() {
		ensureTextAnalyzer();
		return textAnalyzer;
	}

	private void analyzePolylines() {
		polylines = SVGPolyline.extractPolylines(SVGUtil.getQuerySVGElements(svgg, ".//svg:polyline"));
		if (polylines.size() > 0) {
			polylineAnalyzer = new PolylineAnalyzer(semanticDocumentAction);
			polylineAnalyzer.analyzePolylines(svgg, polylines);
		}
	}

//	private void debugLeaf() {
//		List<SVGElement> gList = SVGUtil.getQuerySVGElements(svgg, "./svg:g");
//		LOG.trace("G children: "+gList.size());
//		for (SVGElement g : gList) {
//			debugG();
//		}
//	}

//	private void debugG() {
//		List<SVGElement> texts = SVGUtil.getQuerySVGElements(svgg, "./svg:text");
//		List<SVGElement> lines = SVGUtil.getQuerySVGElements(svgg, "./svg:line");
//		if (lines.size() > 0) {
//			LineAnalyzer lineAnalyzer = new LineAnalyzer();
//			lineAnalyzer.addLines(SVGLine.extractLines(lines));
//			LOG.debug(lineAnalyzer.debug());
//		}
//		List<SVGElement> polylines = SVGUtil.getQuerySVGElements(svgg, "./svg:polyline");
//		LOG.debug("G "+texts.size()+" texts;    "+lines.size()+" lines;    "+polylines.size()+" polylines; ");
//	}

	public PlotBox getPlotBox() {
		if (plotBox == null) {
			if (lineAnalyzer != null) {
				plotBox = lineAnalyzer.getPlotBox();
			}
		}
		return plotBox;
	}

}
