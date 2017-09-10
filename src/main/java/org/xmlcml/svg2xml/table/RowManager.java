package org.xmlcml.svg2xml.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGLineList;
import org.xmlcml.graphics.svg.SVGLineList.SiblingType;
import org.xmlcml.svg2xml.table.GenericRow.RowType;
import org.xmlcml.svg2xml.text.PhraseList;

/** manages the addition and sorting of the row-like objects in a table.
 * these include:
 * - long and short horizontalRules
 * - sibling rules
 * - phraseLists with common Y
 * - contentBox panels
 * 
 * @author pm286
 *
 */
public class RowManager {


	private static final Logger LOG = Logger.getLogger(RowManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private double lineEps = 0.2; // are lines at same height?

	/** at some stage the objects need interfaces.
	 * 
	 */
	private Map<RealRange, GenericRow> rowByYRange = new HashMap<RealRange, GenericRow>();
	private RealRangeArray yRangeArray;

	public RowManager() {
		yRangeArray = new RealRangeArray();
	}
	public void addContentGridPanelBox(Real2Range contentGridPanelBox) {
		GenericRow row = new GenericRow(contentGridPanelBox, RowType.CONTENT_BOX);
		RealRange yRange = contentGridPanelBox.getYRange();
		yRangeArray.add(yRange);
		rowByYRange.put(yRange, row);
	}
	
	public void addHorizontalRules(SVGLineList lineList, RowType type) {
		for (SVGLine line : lineList) {
			addHorizontalRule(line, type);
		}
	}

	public void addHorizontalRule(SVGLine horizontalLine, RowType type) {
		GenericRow row = new GenericRow(horizontalLine, type);
		RealRange yRange = row.getOrCreateBoundingBox().getYRange();
		yRangeArray.add(yRange);
		rowByYRange.put(yRange, row);
	}

	/** these are sibling lines with identical Y coordinate.
	 * 
	 * @param horizontalLineList
	 * @param type
	 */
	public void addSiblingHorizontalRules(SVGLineList horizontalLineList) {
		if (horizontalLineList.checkLines(SiblingType.HORIZONTAL_SIBLINGS)) {
			GenericRow row = new GenericRow(horizontalLineList, RowType.SIBLING_RULES);
			RealRange yRange = row.getOrCreateBoundingBox().getYRange();
			yRangeArray.add(yRange);
			rowByYRange.put(yRange, row);
		}
	}

	public void addPhraseList(PhraseList phraseList) {
//		if (phraseList.getCommonY() != null) {
			GenericRow row = new GenericRow(phraseList, RowType.PHRASE_LIST);
			RealRange yRange = row.getOrCreateBoundingBox().getYRange();
			yRangeArray.add(yRange);
			rowByYRange.put(yRange, row);
//		}
	}

	public String getSignature() {
		StringBuilder sb = new StringBuilder();
		for (RealRange yRange : yRangeArray) {
			GenericRow row = rowByYRange.get(yRange);
			sb.append((row == null ? "?" : row.getSignature()));
		}
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(yRangeArray.size()+": "+yRangeArray.toString()+"\n");
		sb.append(getSignature()+"\n");
		return sb.toString();
	}
	public void sort() {
		yRangeArray.sortAndRemoveOverlapping();
	}
	public void addHorizontalSiblingsList(List<SVGLineList> horizontalSiblingsList) {
		for (SVGLineList siblings : horizontalSiblingsList) {
			this.addSiblingHorizontalRules(siblings);
		}
	}

}
