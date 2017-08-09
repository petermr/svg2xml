package org.xmlcml.svg2xml.table;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Int2Range;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange.Direction;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/** move this later.
 * 
 * @author pm286
 *
 */
public class FilledTableAnalyzer {
	
	
	private static final Logger LOG = Logger.getLogger(FilledTableAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String COLSPAN = "colspan=";
	private static final String ROWSPAN = "rowspan=";
	private static final String DEFAULT_TEXT_CSS = 
			"font-size:8;font-weight:bold;fill:yellow;stroke:blue;stroke-width:0.3;font-family:sans-serif;";
	private static final String DEFAULT_BOUNDARY_CSS = "stroke:blue;stroke-width:2.0";

	private static final double XLINE_MAX = 15.;
	private static final double XLINE_MIN= 10.0;
	private static final double YLINE_MAX = 15.;
	private static final double YLINE_MIN= 10.0;

	private SVGElement svgInElement;
	private SVGSVG svgSvg;
	private String colCssValue = DEFAULT_TEXT_CSS;
	private String rowCssValue = DEFAULT_TEXT_CSS;
	private String colBoundaryCss = DEFAULT_BOUNDARY_CSS;
	private String rowBoundaryCss = DEFAULT_BOUNDARY_CSS;

	public FilledTableAnalyzer() {
		
	}

	public void readSVGElement(File svgFile) {
		svgInElement = SVGElement.readAndCreateSVG(svgFile);
	}
	
	public void createRows() {
		Multimap<IntRange, SVGRect> rectByIntYRange = createRectByIntRange();
		
		svgSvg = new SVGSVG();
		SVGG g = new SVGG();
		
		List<IntRange> rowRangeList = IntRange.createSortedList(rectByIntYRange.keySet());
		List<Integer> rowBoundaryList = createRowBoundaryList(rowRangeList);
		
		drawRowBoundaries(g, rowBoundaryList);

		List<Integer> colBoundaryList = createColumnBoundaryList(rectByIntYRange, rowRangeList);
		
		createAndDrawColspans(rectByIntYRange, g, rowRangeList, rowBoundaryList, colBoundaryList);
		drawColumnBoundaries(g, colBoundaryList);
		
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgInElement);
		for (SVGText text : textList) {
			g.appendChild(text.copy());
		}
		svgSvg.appendChild(g);
	}

	private Multimap<IntRange, SVGRect> createRectByIntRange() {
		List<SVGRect> rectList = SVGRect.extractSelfAndDescendantRects(svgInElement);
		Multimap<IntRange, SVGRect> rectByIntYRange = ArrayListMultimap.create();
		for (SVGRect rect : rectList) {
			rect.setBoundingBoxCached(true);
			Real2Range r2range = rect.getBoundingBox();
			Int2Range i2range = new Int2Range(r2range);
			IntRange irange = new IntRange(r2range.getRealRange(Direction.VERTICAL));
			rectByIntYRange.put(irange, rect);
		}
		return rectByIntYRange;
	}
	
	private void createAndDrawColspans(Multimap<IntRange, SVGRect> rectByIntYRange, SVGG g, List<IntRange> rowRangeList,
			List<Integer> rowBoundaryList, List<Integer> colBoundaryList) {
		for (IntRange rowRange : rowRangeList) {
			List<SVGRect> rowList = new ArrayList<SVGRect>(rectByIntYRange.get(rowRange));
			for (SVGRect rowRect : rowList) {
				SVGRect r1 = new SVGRect(rowRect);
				g.appendChild(r1);
				Real2Range bbox = r1.getBoundingBox();
				createAndOutputColspans(g, colBoundaryList, bbox);
				createAndOutputRowspans(g, rowBoundaryList, bbox);
			}
		}
	}

	private void createAndOutputRowspans(SVGG g, List<Integer> rowBoundaryList, Real2Range bbox) {
		IntRange yrange = new IntRange(bbox.getYRange());
		int rowspans = createSpanCounts(yrange, rowBoundaryList);
		if (rowspans > 1) {
			SVGText rowspanText = new SVGText(bbox.getAllCornerPoints()[3], ROWSPAN+rowspans);
			rowspanText.setCSSStyle(rowCssValue);
			g.appendChild(rowspanText);
		}
	}

	private void createAndOutputColspans(SVGG g, List<Integer> colBoundaryList, Real2Range bbox) {
		IntRange xrange = new IntRange(bbox.getXRange());
		int colspans = createSpanCounts(xrange, colBoundaryList);
		if (colspans > 1) {
			SVGText colspanText = new SVGText(bbox.getAllCornerPoints()[3], COLSPAN+colspans);
			colspanText.setCSSStyle(colCssValue);
			g.appendChild(colspanText);
		}
	}

	private static List<Integer> createRowBoundaryList(List<IntRange> rowRangeList) {
		Set<Integer> rowBoundarySet = new HashSet<Integer>();
		for (IntRange yrange : rowRangeList) {
			int min = yrange.getMin();
			int max = yrange.getMax();
			rowBoundarySet.add(min);
			rowBoundarySet.add(max);
		}
		List<Integer> rowBoundaryList = new ArrayList<Integer>(rowBoundarySet);
		Collections.sort(rowBoundaryList);
		return rowBoundaryList;
	}

	private int createSpanCounts(IntRange range, List<Integer> boundaryList) {
		Integer min = range.getMin();
		Integer max = range.getMax();
		int idx = boundaryList.indexOf(min);
		if (idx >= 0) {
			for (int i = idx + 1; i < boundaryList.size(); i++) {
				if (boundaryList.get(i).equals(max)) {
					return i - idx;
				}
			}
		}
		return 0;
	}

	private List<Integer> createColumnBoundaryList(Multimap<IntRange, SVGRect> rectByIntYRange, List<IntRange> rowRangeList) {
		Set<IntRange> colRangeSet = new HashSet<IntRange>();
		for (IntRange rowRange : rowRangeList) {
			List<SVGRect> rowList = new ArrayList<SVGRect>(rectByIntYRange.get(rowRange));
			for (SVGRect rowRect : rowList) {
				IntRange colRange = new IntRange(rowRect.getBoundingBox().getXRange());
				colRangeSet.add(colRange);
			}
		}
		List<Integer> colBoundaryList = createSortedColumnBoundaryList(colRangeSet);
		return colBoundaryList;
	}

	private void drawColumnBoundaries(SVGG g, List<Integer> colBoundaryList) {
		for (int i = 0; i < colBoundaryList.size(); i++) {
			SVGLine line = new SVGLine(new Real2(colBoundaryList.get(i), YLINE_MIN), new Real2(colBoundaryList.get(i), YLINE_MAX));
			line.setCSSStyle(colBoundaryCss);
			g.appendChild(line);
		}
	}

	private void drawRowBoundaries(SVGG g, List<Integer> rowBoundaryList) {
		for (Integer rowBoundary : rowBoundaryList) {
			SVGLine line = new SVGLine(new Real2(XLINE_MIN, rowBoundary), new Real2(XLINE_MAX, rowBoundary));
			line.setCSSStyle(rowBoundaryCss);
			g.appendChild(line);
		}
	}

	private static List<Integer> createSortedColumnBoundaryList(Set<IntRange> colRangeSet) {
		List<IntRange> colRangeList = IntRange.createSortedList(colRangeSet);
		List<Integer> colBoundaryList = createRowBoundaryList(colRangeList);
		return colBoundaryList;
	}

	public SVGSVG createSVGSVG() {
		return svgSvg;
	}


}
