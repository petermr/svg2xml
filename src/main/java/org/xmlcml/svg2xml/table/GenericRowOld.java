package org.xmlcml.svg2xml.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGLineList;
import org.xmlcml.graphics.svg.text.phrase.PhraseChunk;
import org.xmlcml.svg2xml.box.SVGContentBoxOld;

/** generic horizontally-based object in a table.
 * replaces HorizontalElement
 * Can be a line, PhraseList, GridPanelBox, etc.
 * Messy, because the tables are messy.
 * 
 * 
 * 
 * @author pm286
 *
 */
public class GenericRowOld {
	

	public enum RowType {
		LONG_HORIZONTAL("H"),
		SHORT_HORIZONTAL("h"),
		LINE_LIST("L"),
		CONTENT_BOX("B"),
		PHRASE_LIST("P"),
		SIBLING_PHRASE_LIST("S"),
//		CONTENT_PANEL("C"), 
		SIBLING_RULES("="), 
		CONTENT_GRID_PANEL("G");
		
		private String abbrev;

		private RowType(String abbrev) {
			this.abbrev = abbrev;
		}
		public String getAbbrev() {
			return abbrev;
		}
	}
	
	private static final Logger LOG = Logger.getLogger(GenericRowOld.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static double LINE_DELTA_Y = 0.5; // make lines at least 1 pixel thick
	private static final double LINE_THICKNESS_FACTOR  = (1.0 + 1.0) + 0.1;
	private SVGLine line;
	private RowType type;
	private Real2Range box;
	private PhraseChunk phraseList;
	private SVGLineList lineList;
	private SVGContentBoxOld contentBox;

	private GenericRowOld(RowType type) {
		this.type = type;
		if (type == null) {
			throw new RuntimeException("null type in GenericRow");
		}
	}
	
	public GenericRowOld(SVGLine line, RowType type) {
		this(type);
		this.line = line;
	}

	public GenericRowOld(Real2Range box, RowType type) {
		this(type);
		this.box = box;
	}
	
	public GenericRowOld(PhraseChunk phraseList, RowType type) {
		this(type);
		this.phraseList = phraseList;
	}
	
	public GenericRowOld(SVGLineList lineList, RowType type) {
		this(type);
		this.lineList = lineList;
		
	}

	public GenericRowOld(SVGContentBoxOld contentBox) {
		this(RowType.CONTENT_BOX);
		this.contentBox = contentBox;
	}

	public Real2Range getOrCreateBoundingBox() {
		Real2Range bbox = null;
		if (bbox == null) {
			if (type.equals(RowType.LONG_HORIZONTAL) || type.equals(RowType.SHORT_HORIZONTAL)) {
				bbox = getLineBBox();
			} else if (type.equals(RowType.CONTENT_BOX) || type.equals(RowType.CONTENT_GRID_PANEL)) {
				bbox = box;
			} else if (type.equals(RowType.PHRASE_LIST)) {
				bbox = phraseList == null ? null: phraseList.getBoundingBox();
			} else if (type.equals(RowType.SIBLING_RULES)) {
				bbox = lineList == null ? null: lineList.getBoundingBox();
			} else {
				throw new RuntimeException("Unknown type: "+type);
			}
		}
		return bbox;
	}

	private Real2Range getLineBBox() {
		Real2Range bbox = null;
		if (line != null) {
			bbox = line.getBoundingBox();
			if (bbox.getYRange().getRange() < 1.0) {
				bbox = bbox.getReal2RangeExtendedInY(LINE_DELTA_Y, LINE_DELTA_Y);
			}
		}
		return bbox;
	}

	public String getSignature() {
		String s;
		s = type.getAbbrev();
		return s;
	}
	
	/*
	private SVGLine line;
	private RowType type;
	private Real2Range box;
	private PhraseList phraseList;
	private SVGLineList lineList;
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type.abbrev);
		sb.append("; ");
		sb.append((line == null ? "" : line.toString()));
		sb.append((contentBox == null ? "" : contentBox.toString()));
		sb.append((box == null ? "" : box.toString()));
		sb.append((phraseList == null ? "" : phraseList.toString()));
		sb.append((lineList == null ? "" : lineList.toString()));
		return sb.toString();
	}

	public SVGContentBoxOld getContentBox() {
		return contentBox;
	}

	boolean intersectsBoxRange(RealRange boxYRange) {
		boolean intersects = false;
		RealRange rowYRange = getOrCreateBoundingBox().getYRange();
		// equality
		if (boxYRange.isEqualTo(rowYRange, LINE_DELTA_Y)) {
			LOG.trace("ROW "+this.toString()+" EQUALS "+rowYRange+"; "+getSignature());
			intersects = true;
		} else if (boxYRange.includes(rowYRange)) {
			LOG.trace("ROW "+this.toString()+" INCLUDES "+rowYRange+"; "+getSignature());
			intersects = true;
		} else if (boxYRange.intersects(rowYRange)) {
			boolean touchesLow = Real.isEqual(boxYRange.getMin(), rowYRange.getMin(), LINE_DELTA_Y);
			boolean touchesHigh = Real.isEqual(boxYRange.getMax(), rowYRange.getMax(), LINE_DELTA_Y);
			if (touchesLow && touchesHigh) {
				LOG.trace("ROW "+this.toString()+" TOUCHES_BOTH "+rowYRange+"; "+getSignature());
			} else if (touchesLow) {
				LOG.trace("ROW "+this.toString()+" TOUCHES_LOW "+rowYRange+"; "+getSignature());
			} else if (touchesHigh) {
				LOG.trace("ROW "+this.toString()+" TOUCHES_HIGH "+rowYRange+"; "+getSignature());
			} else {
				LOG.trace("ROW "+this.toString()+" INTERSECTS "+rowYRange+"; "+getSignature());
			}
			if (rowYRange.getRange() < LINE_THICKNESS_FACTOR  * LINE_DELTA_Y) {
				// a line on edge of box is EXCLUDED
				intersects = false;
				LOG.trace("EXCLUDED");
			} else {
				intersects = true;
			}
		} else {
			// doesn't intersect
		}
		return intersects;
	}
	
	public RowType getRowType() {
		return type;
	}

	public SVGLine getLine() {
		return line;
	}

	public PhraseChunk getPhraseList() {
		return phraseList;
	}

	public boolean addLineToContentBox(SVGContentBoxOld contentBox) {
		boolean added = false;
		if (line != null) {
			added = contentBox.addLine(line);
		}
		return added;
	}
	
	public boolean addLineListToContentBox(SVGContentBoxOld contentBox) {
		boolean added = false;
		if (lineList != null) {
			added = contentBox.addLineList(lineList);
		}
		return added;
	}

	public boolean addPhraseListToContentBox(SVGContentBoxOld contentBox) {
		boolean added = false;
		if (phraseList != null) {
			added = contentBox.addPhraseList(phraseList);
		}
		return added;
	}


}
