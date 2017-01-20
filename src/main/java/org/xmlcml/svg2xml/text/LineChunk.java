package org.xmlcml.svg2xml.text;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;

/** chunks in a TextLine such as Phrases and Blanks
 * 
 * @author pm286
 *
 */
public abstract class LineChunk extends SVGG implements HorizontalElement {
	private static final Logger LOG = Logger.getLogger(LineChunk.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

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
		Real2Range bbox = this.getBoundingBox();
		return bbox == null ? null : bbox.getCorners()[0];
	}

	public Double getX() {
		Real2 xy = this.getXY();
		return xy == null ? null : xy.getX();
	}

	public Double getY() {
		Real2 xy = this.getXY();
		return xy == null ? null : xy.getY();
	}

	public String getFontFamily() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFontFamily();
			for (int i = 1; i < childChunks.size(); i++) {
				String ss = childChunks.get(i).getFontFamily();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.warn("Font Family changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public Double getFontSize() {
		Double s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFontSize();
			for (int i = 1; i < childChunks.size(); i++) {
				Double ss = childChunks.get(i).getFontSize();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.warn("Font Size changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public String getFontWeight() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFontWeight();
			for (int i = 1; i < childChunks.size(); i++) {
				String ss = childChunks.get(i).getFontWeight();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.warn("Font Weight changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public String getFontStyle() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFontStyle();
			for (int i = 1; i < childChunks.size(); i++) {
				String ss = childChunks.get(i).getFontStyle();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.warn("Font Style changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	protected abstract List<? extends LineChunk> getChildChunks();
}
