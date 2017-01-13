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
	
	/** dangerous - remove */
	private SVGLine svgLine;
	private List<SVGLine> svgLineList;

	public Ruler() {
		super();
		this.setClassName(TAG);
	}

	public Ruler(SVGLine line) {
		this();
		this.svgLine = line;
	}

	protected void add(Ruler ruler) {
		if (svgLineList == null) {
			svgLineList = new ArrayList<SVGLine>();
			svgLineList.add(svgLine);
			svgLineList.add(ruler.svgLine);
			svgLine = null;
		} else if (svgLineList != null) {
			svgLineList.add(ruler.svgLine);
		}
	}

	public Real2 getMidPoint() {
		return svgLine == null ? null : svgLine.getMidPoint();
	}

	public SVGLine getSVGLine() {
		return svgLine;
	}

	public List<SVGLine> getSVGLineList() {
		return svgLineList;
	}
	public void setSVGLineList(List<SVGLine> lineList) {
		this.svgLineList = lineList;
	}



	public static void formatStrokeWidth(List<? extends Ruler> rulerList, int d) {
		for (Ruler ruler : rulerList) {
			ruler.formatStrokeWidth(d);
		}
	}

	private void formatStrokeWidth(int d) {
		if (svgLine != null) {
			svgLine.setStrokeWidth(Util.format(svgLine.getStrokeWidth(), d));
		} else if (svgLineList != null) {
			for (SVGLine line : svgLineList) {
				line.format(d);
			}
		}
	}
	
	public Real2Range getBoundingBox() {
		Real2Range bbox = null;
		if (svgLine != null) {
			bbox = svgLine.getBoundingBox();
		} else if (svgLineList != null) {
			Real2Range bboxTotal = null;
			for (SVGLine line : svgLineList) {
				if (bboxTotal == null) {
					bboxTotal = line.getBoundingBox();
				} else {
					bboxTotal.plus(bbox);
				}
			}
			bbox = bboxTotal;
		}
		return bbox;
	}
	
	@Override
	public String toString() {
		String s = "";
		getAllSVGLineList();
		if (svgLine != null) {
			s += toString(svgLine);
		} else {
			for (SVGLine line : svgLineList) {
				s += toString(svgLine)+";";
			}
		}
		return /*this.getClass().getSimpleName()*/ /*+": "+*/ /*this.getXY()+": " +*/s;
	}

	protected String toString(SVGLine svgLine) {
		List<SVGLine> allLineList = getAllSVGLineList();
		StringBuilder sb = new StringBuilder(allLineList.size()+": ");
		for (SVGLine line : allLineList) {
			sb.append(line.getXY(0));
			sb.append(line.getXY(1));
			sb.append("("+Util.format(line.getStrokeWidth(), 2)+")\n");
		}
		return sb.toString();
	}

	public Double getLength() {
		Real2Range bbox = this.getBoundingBox();
		return bbox == null ? null : (Double) bbox.getXRange().getRange();
	}

	public Double getY() {
		Double d = null;
		if (svgLine != null) {
			d = svgLine.getMidPoint().getY();
		} else {
			Real2Range bbox = this.getBoundingBox();
			d = bbox == null ? null : bbox.getYMax();
		}
		return d;
	}

	public Element copyElement() {
		Element element = (Element) this.copy();
		addLinesAsChildren(element);
		return element;
	}

	private void addLinesAsChildren(Element element) {
		if (svgLine != null) {
			element.appendChild(svgLine.copy());
		} else if (svgLineList != null) {
			for (SVGLine line : svgLineList) {
				element.appendChild(line);
			}
		}
	}
	
	public List<SVGLine> getAllSVGLineList() {
		List<SVGLine> childLineList = new ArrayList<SVGLine>();
		if (svgLine != null) {
			childLineList.add(svgLine);
		}
		if (svgLineList != null) {
			childLineList.addAll(svgLineList);
		}
		return childLineList;
	}
	


}
