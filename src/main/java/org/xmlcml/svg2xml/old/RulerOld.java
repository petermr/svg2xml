package org.xmlcml.svg2xml.old;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGShape;

import nu.xom.Element;

@Deprecated // move to SVG
public abstract class RulerOld extends LineChunkOld {

	private static final Logger LOG = Logger.getLogger(RulerOld.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "ruler";
	
	protected static double epsilon = 0.01;
	
	public RulerOld() {
		super();
		this.setClassName(TAG);
	}

	public RulerOld(SVGLine line) {
		this();
		this.appendChild(line.copy());
	}


	public Real2 getMidPoint() {
		SVGLine svgLine = getSVGLine();
		return svgLine == null ? null : svgLine.getMidPoint();
	}

	public SVGLine getSVGLine() {
		return (SVGLine) this.getChildElements().get(0);
	}

	public static void formatStrokeWidth(List<? extends RulerOld> rulerList, int d) {
		for (RulerOld ruler : rulerList) {
			ruler.formatStrokeWidth(d);
		}
	}

	private void formatStrokeWidth(int d) {
		SVGLine svgLine = getSVGLine();
		if (svgLine != null) {
			svgLine.setStrokeWidth(Util.format(svgLine.getStrokeWidth(), d));
		}
	}
	
	public Real2Range getBoundingBox() {
		SVGLine svgLine = getSVGLine();
		return (svgLine == null) ? null : svgLine.getBoundingBox();
	}
	
	@Override
	public String toString() {
		String s = "";
		SVGShape svgLine = getSVGLine();
		if (svgLine != null) { 
			s += svgLine.toString();
		}
		return s;
	}

	public Double getLength() {
		Real2Range bbox = this.getBoundingBox();
		return bbox == null ? null : (Double) bbox.getXRange().getRange();
	}

	public Double getY() {
		Double d = null;
		SVGLine svgLine = getSVGLine();
		if (svgLine != null) {
			d = svgLine.getMidPoint().getY();
		}
		return d;
	}

	public Element copyElement() {
		Element element = (Element) this.copy();
		return element;
	}

	protected List<? extends LineChunkOld> getChildChunks() {
		return new ArrayList<LineChunkOld>();
	}


}
