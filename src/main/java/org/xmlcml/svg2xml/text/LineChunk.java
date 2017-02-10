package org.xmlcml.svg2xml.text;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;

import nu.xom.Attribute;

/** chunks in a TextLine such as Phrases and Blanks
 * 
 * @author pm286
 *
 */
public abstract class LineChunk extends SVGG implements HorizontalElement {
	private static final String TRUE = "true";
	private static final Logger LOG = Logger.getLogger(LineChunk.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String SUPERSCRIPT = "superscript";
	private static final String SUBSCRIPT = "subscript";

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
					LOG.trace("Font Family changed "+ss+" => "+s);
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
					LOG.trace("Font Size changed "+ss+" => "+s);
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
					LOG.trace("Font Weight changed "+ss+" => "+s);
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
					LOG.trace("Font Style changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	protected abstract List<? extends LineChunk> getChildChunks();

	public void setSuperscript(boolean superscript) {
		if (superscript) {
			this.addAttribute(new Attribute(SUPERSCRIPT, TRUE));
		} else {
			this.removeAttribute(SUPERSCRIPT);
			this.removeAttribute(SUBSCRIPT);
		}
	}

	private void removeAttribute(String attName) {
		Attribute attribute = this.getAttribute(attName);
		if (attribute != null) {
			this.removeAttribute(attribute);
		}
	}

	public void setSubscript(boolean subscript) {
		if (subscript) {
			this.addAttribute(new Attribute(SUBSCRIPT, TRUE));
		} else {
			this.removeAttribute(SUPERSCRIPT);
			this.removeAttribute(SUBSCRIPT);
		}
	}
	
	public boolean hasSuperscript() {
		return TRUE.equals(this.getAttributeValue(SUPERSCRIPT));
	}

	public boolean hasSubscript() {
		return TRUE.equals(this.getAttributeValue(SUBSCRIPT));
	}

	public void setSuscript(SusType susType, boolean onoff) {
		if (SusType.SUB.equals(susType)) {
			this.setSubscript(onoff);
		} else if (SusType.SUPER.equals(susType)) {
			this.setSuperscript(onoff);
		}
	}
}
