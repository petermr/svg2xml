package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.euclid.RealRange.Direction;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGLineList;
import org.xmlcml.graphics.svg.SVGLineList.SiblingType;
import org.xmlcml.svg2xml.box.SVGContentBox;
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


	static final Logger LOG = Logger.getLogger(RowManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private double lineEps = 0.2; // are lines at same height?

	/** at some stage the objects need interfaces.
	 * 
	 */
	private Map<RealRange, GenericRow> rowByYRange = new HashMap<RealRange, GenericRow>();
	private RealRangeArray yRangeArray;
	private List<SVGContentBox> contentBoxList;

	public RowManager() {
		yRangeArray = new RealRangeArray();
	}
	// obsolete this for ContentBox
	public void addContentGridPanelBox(Real2Range contentGridPanelBox) {
		GenericRow row = new GenericRow(contentGridPanelBox, RowType.CONTENT_GRID_PANEL);
		RealRange yRange = contentGridPanelBox.getYRange();
		addRowAndRange(row, yRange);
	}
	
	public void addContentBox(SVGContentBox contentBox) {
		RealRange yRange = contentBox.getRealRange(Direction.VERTICAL);
		GenericRow row = new GenericRow(contentBox);
		addRowAndRange(row, yRange);
	}
	
	public void addHorizontalRules(SVGLineList lineList, RowType type) {
		for (SVGLine line : lineList) {
			addHorizontalRule(line, type);
		}
	}

	public void addHorizontalRule(SVGLine horizontalLine, RowType type) {
		GenericRow row = new GenericRow(horizontalLine, type);
		RealRange yRange = row.getOrCreateBoundingBox().getYRange();
		addRowAndRange(row, yRange);
	}

	/** these are sibling lines with identical Y coordinate.
	 * 
	 * @param horizontalLineList
	 * @param typeOLD
	 */
	public void addSiblingHorizontalRules(SVGLineList horizontalLineList) {
		if (horizontalLineList.checkLines(SiblingType.HORIZONTAL_SIBLINGS)) {
			GenericRow row = new GenericRow(horizontalLineList, RowType.SIBLING_RULES);
			RealRange yRange = row.getOrCreateBoundingBox().getYRange();
			addRowAndRange(row, yRange);
		}
	}
	private void addRowAndRange(GenericRow row, RealRange yRange) {
//		LOG.debug("add "+yRange+"; "+row);
		yRangeArray.add(yRange);
		rowByYRange.put(yRange, row);
	}

	public void addPhraseList(PhraseList phraseList) {
//		if (phraseList.getCommonY() != null) {
			GenericRow row = new GenericRow(phraseList, RowType.PHRASE_LIST);
			
			RealRange yRange = row.getOrCreateBoundingBox().getYRange();
			addRowAndRange(row, yRange);
	}

	public String getSignature() {
		StringBuilder sb = new StringBuilder();
		for (RealRange yRange : yRangeArray) {
			GenericRow row = rowByYRange.get(yRange);
			if (row == null) {
				LOG.debug("keys: "+yRange+"; "+rowByYRange.keySet());
				LOG.debug("array: "+yRangeArray.size()+"; "+yRangeArray);
				throw new RuntimeException("null row");
			}
			sb.append((row == null ? "?" : row.getSignature()));
		}
		return sb.toString();
	}

//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(yRangeArray.size()+": "+yRangeArray.toString()+"\n");
//		sb.append(getSignature()+"\n");
//		return sb.toString();
//	}
	public void sortAndAddBoxContent() {
//		yRangeArray.sortAndRemoveOverlapping(); // dont necessarily want to remove
		yRangeArray.sort();
		transferRowsIntoBoxes();
	}
	
	private void transferRowsIntoBoxes() {
		getOrCreateContentBoxList();
		List<RealRange> rangesToBeRemoved = new ArrayList<RealRange>();
		LOG.debug("rows:: "+rowByYRange.size());
		for (SVGContentBox contentBox : contentBoxList) {
			LOG.trace("CONTENT BOX "+contentBox);
			RealRange contentBoxYRange = contentBox.getRealRange(Direction.VERTICAL);
			for (RealRange yRange : rowByYRange.keySet()) {
				GenericRow row = rowByYRange.get(yRange);
				if (!RowType.CONTENT_BOX.equals(row.getRowType())) {
					if (row.intersectsBoxRange(contentBoxYRange)) {
						contentBox.add(row);
						rangesToBeRemoved.add(yRange);
					}
				}
			}
		}
		for (RealRange yRange : rangesToBeRemoved) {
			removeRowAndRange(yRange);
		}
		LOG.debug("rows:: "+rowByYRange.size());
	}
	private void removeRowAndRange(RealRange yRange) {
		rowByYRange.remove(yRange);
		yRangeArray.remove(yRange);
	}
	
	public List<SVGContentBox> getOrCreateContentBoxList() {
		contentBoxList = new ArrayList<SVGContentBox>();
		for (GenericRow row : rowByYRange.values()) {
			if (row.getSignature().equals("B")) {
				SVGContentBox contentBox = row.getContentBox();
				LOG.trace("ContentBox:" +contentBox);
				contentBoxList.add(contentBox);
			}
		}
		return contentBoxList;
	}
	
	public void addHorizontalSiblingsList(List<SVGLineList> horizontalSiblingsList) {
		for (SVGLineList siblings : horizontalSiblingsList) {
			this.addSiblingHorizontalRules(siblings);
		}
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		checkArraySizes();
		sb.append("rows: "+yRangeArray.size()+"; sig: "+getSignature()+"\n");
		for (RealRange yRange : yRangeArray) {
			GenericRow row = rowByYRange.get(yRange);
//			sb.append("<ROW> "+yRange+": "+row+"</ROW>\n");
			sb.append(row.getSignature()+"/");
		}
		return sb.toString();
	}
	
	private void checkArraySizes() {
		if (yRangeArray.size() != rowByYRange.size()) {
			LOG.error("Range array ("+yRangeArray.size()+") != map ("+rowByYRange.size()+")");
		}
	}
}
