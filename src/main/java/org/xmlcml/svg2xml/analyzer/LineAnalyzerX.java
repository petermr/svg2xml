package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.svgplus.action.PathNormalizerActionX;
import org.xmlcml.svgplus.action.SemanticDocumentActionX;
import org.xmlcml.svgplus.paths.Axis;
import org.xmlcml.svgplus.tools.PlotBox;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class LineAnalyzerX extends AbstractPageAnalyzerX {

	private final static Logger LOG = Logger.getLogger(LineAnalyzerX.class);

	private final static Vector2 XAXIS = new Vector2(1.0, 0.0);
	private List<SVGLine> lines;
	private Multimap<Integer, SVGLine> lineAngleMap;
	private Axis horizontalAxis;
	private Axis verticalAxis;
	private List<PlotBox> plotBoxList;
	private PlotBox plotBox;

	protected LineAnalyzerX() {
	}
	
	public LineAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}

	/** copy lines for analysis
	 * 
	 * @param lines
	 */
	private void addLines(List<SVGLine> lines) {
		this.lines = new ArrayList<SVGLine>();
		for (SVGLine line : lines) {
//			this.lines.add(new SVGLine(line));
			this.lines.add(line);
		}
	}
	
	public Multimap<Integer, SVGLine> getLineAngleMap() {
		if (lineAngleMap == null) {
			lineAngleMap = ArrayListMultimap.create();
			if (lines != null) {
				for (SVGLine line : lines) {
					Vector2 vector = line.getEuclidLine().getUnitVector();
					Angle angle = vector.getAngleMadeWith(XAXIS);
					Integer degrees =  (int) Math.round(angle.getDegrees());
					// normalize direction
					if (degrees > 180) {
						degrees = degrees - 180;
					}
					if (degrees < 0) {
						degrees = degrees + 180;
					}
					lineAngleMap.put(degrees, line);
				}
			}
		}
		return lineAngleMap;
	}
	
	public void analyzeLinesAsAxesAndWhatever(SVGG svgg, List<SVGLine> lines) {
		this.svgg = svgg;
		this.addLines(lines);
		findAxes();
	}

	private void findAxes() {
		plotBoxList = new ArrayList<PlotBox>();
		AxisAnalyzerX axisAnalyzerX = new AxisAnalyzerX(svgg, semanticDocumentActionX);
		axisAnalyzerX.createVerticalHorizontalAxisList(lines, PathNormalizerActionX.EPS);
		plotBox = axisAnalyzerX.getPlotBox();
		if (plotBox != null) {
			plotBoxList.add(plotBox);
		}
	}

	public String debug() {
		getLineAngleMap();
		StringBuilder sb = new StringBuilder("\nAngles: "+"\n");
		Integer[] deg = lineAngleMap.keySet().toArray(new Integer[0]);
		Arrays.sort(deg);
		List<Integer> degreeList = (Arrays.asList(deg));
		for (Integer degrees : degreeList) {
			sb.append("> "+degrees+" "+lineAngleMap.get(degrees).size()+"\n");
		}
		return sb.toString();
	}

	public PlotBox getPlotBox() {
		return plotBox;
	}
}
