package org.xmlcml.svgplus.figure;

import java.util.List;

import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Univariate;
import org.xmlcml.graphics.svg.SVGLine;

public class LineUnivariate {

	private List<SVGLine> lineElements;
	private Univariate univariate;
	private int nlines;

	public LineUnivariate(List<SVGLine> lineElements) {
		this.lineElements = lineElements;
		computeLineStats();
	}

	private void computeLineStats() {
		nlines = lineElements.size();
		double length[] = new double[nlines];
		int i = 0;
		for (SVGLine svgLine : lineElements) {
			Line2 line = svgLine.getEuclidLine();
			length[i++] = line.getLength(); 
		}
		univariate = new Univariate(new RealArray(length));
	}
	
	public Double getMin() {
		return nlines == 0 ? null : univariate.getMin();
	}

	public Double getMax() {
		return nlines == 0 ? null : univariate.getMax();
	}

	public Double getStandardDeviation() {
		return nlines <= 1 ? null : univariate.getStandardDeviation();
	}

	public Double getMedian() {
		return nlines == 0 ? null : univariate.getMedian();
	}

	public int[] getHistogramCounts() {
		return nlines == 0 ? null : univariate.getHistogramCounts();
	}

	public Double getMean() {
		return nlines == 0 ? null : univariate.getMean();
	}
	
}
