package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;

/** a grid of rectangular boxes, touching or overlapping.
 * not fully worked out.
 * 
 * @author pm286
 *
 */
public class ContentBoxGrid {

	private static final Logger LOG = Logger.getLogger(ContentBoxGrid.class);
	private static final String CONTEXT_BOX_GRID = "contextBoxGrid";
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<Real2Range> bboxList;
	private double delta = 0.5;

	public ContentBoxGrid() {
		bboxList = new ArrayList<Real2Range>();
	}
	
	public boolean add(SVGRect rect) {
		return Real2Range.agglomerateIntersections(rect.getBoundingBox(), bboxList, delta);
	}

	void add(List<SVGRect> rectList) {
		for (SVGRect rect : rectList) {
			add(rect);
		}
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public List<Real2Range> getBboxList() {
		return bboxList;
	}

	public SVGElement getOrCreateSVGElement() {
		SVGG g = new SVGG();
		g.setClassName(CONTEXT_BOX_GRID);
		for (Real2Range box : bboxList) {
			SVGRect rect = SVGRect.createFromReal2Range(box);
			g.appendChild(rect);
			rect.setCSSStyle("stroke-width:1.0);fill:pink;opacity:0.3;");
		}
		return g;
	}

}
