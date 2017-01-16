package org.xmlcml.svg2xml.text;

import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;

/** chunks in a TextLine such as Phrases and Blanks
 * 
 * @author pm286
 *
 */
public abstract class LineChunk extends SVGG implements HorizontalElement {
	
	public LineChunk() {
		super();
	}
	
	public LineChunk(SVGG e) {
		super(e);
	}

	protected Real2Range getOrCreateBoundingBox() {
		if (boundingBox == null) {
			boundingBox = getBoundingBox();
		}
		return boundingBox;
	}

	public Real2 getXY() {
		return this.getBoundingBox().getCorners()[0];
	}

	public Double getX() {
		Real2 xy = this.getXY();
		return xy == null ? null : xy.getX();
	}

	public Double getY() {
		Real2 xy = this.getXY();
		return xy == null ? null : xy.getY();
	}
}
