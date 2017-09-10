package org.xmlcml.svg2xml.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGLineList;
import org.xmlcml.svg2xml.table.GenericRow.RowType;
import org.xmlcml.svg2xml.text.PhraseList;

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
public class GenericRow {
	
	public enum RowType {
		LONG_HORIZONTAL("L"),
		SHORT_HORIZONTAL("l"),
		LINE_LIST("Y"),
		CONTENT_BOX("B"),
		PHRASE_LIST("P"),
		SIBLING_PHRASE_LIST("S"),
		CONTENT_PANEL("C"), 
		SIBLING_RULES("Z");
		
		private String abbrev;

		private RowType(String abbrev) {
			this.abbrev = abbrev;
		}
		public String getAbbrev() {
			return abbrev;
		}
	}
	
	private static final Logger LOG = Logger.getLogger(GenericRow.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static double LINE_DELTA_Y = 0.5; // make lines at least 1 pixel thick
	private SVGLine line;
	private RowType type;
	private Real2Range box;
	private PhraseList phraseList;
	private SVGLineList lineList;

	private GenericRow(RowType type) {
		this.type = type;
		if (type == null) {
			throw new RuntimeException("null type in GenericRow");
		}
	}
	
	public GenericRow(SVGLine line, RowType type) {
		this(type);
		this.line = line;
	}

	public GenericRow(Real2Range box, RowType type) {
		this(type);
		this.box = box;
	}
	
	public GenericRow(PhraseList phraseList, RowType type) {
		this(type);
		this.phraseList = phraseList;
	}
	
	public GenericRow(SVGLineList lineList, RowType type) {
		this(type);
		this.lineList = lineList;
		
	}

	public Real2Range getOrCreateBoundingBox() {
		Real2Range bbox = null;
		if (bbox == null) {
			if (type.equals(RowType.LONG_HORIZONTAL) || type.equals(RowType.SHORT_HORIZONTAL)) {
				bbox = getLineBBox();
			} else if (type.equals(RowType.CONTENT_BOX) || type.equals(RowType.CONTENT_PANEL)) {
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
	
}
