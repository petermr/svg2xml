package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGLine;

import nu.xom.Element;

public abstract class Ruler extends LineChunk {

	private static final Logger LOG = Logger.getLogger(Ruler.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "ruler";
	
	public Ruler() {
		super();
		this.setClassName(TAG);
	}

	public Ruler(SVGLine line) {
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

	public static void formatStrokeWidth(List<? extends Ruler> rulerList, int d) {
		for (Ruler ruler : rulerList) {
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
		SVGLine svgLine = getSVGLine();
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

	protected List<? extends LineChunk> getChildChunks() {
		return new ArrayList<LineChunk>();
	}


}
