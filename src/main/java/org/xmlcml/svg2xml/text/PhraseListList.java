package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.html.HtmlTr;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

public class PhraseListList extends SVGG implements Iterable<PhraseList> {
	private static final Logger LOG = Logger.getLogger(PhraseListList.class);
	private static final int EPS = 5;
	private List<PhraseList> phraseListList;
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "phraseListList";

	public PhraseListList() {
		super();
		this.setClassName(TAG);
	}
	
	public Iterator<PhraseList> iterator() {
		getOrCreateChildPhraseList();
		return phraseListList.iterator();
	}

	private List<PhraseList> getOrCreateChildPhraseList() {
		if (phraseListList == null) {
			List<Element> phraseChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+PhraseList.TAG+"']");
			phraseListList = new ArrayList<PhraseList>();
			for (Element child : phraseChildren) {
				PhraseList phraseList = (PhraseList)child;
				phraseListList.add(phraseList);
			}
		}
		return phraseListList;
	}

	public String getStringValue() {
		StringBuilder sb = new StringBuilder();
		for (PhraseList phraseList : phraseListList) {
			sb.append(""+phraseList.getStringValue()+"//");
		}
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}

	public void add(PhraseList phraseList) {
		this.appendChild(phraseList);
		phraseListList = null;
		getOrCreateChildPhraseList();
	}

	public PhraseList get(int i) {
		getOrCreateChildPhraseList();
		return phraseListList.get(i);
	}
	
	public List<IntArray> getLeftMarginsList() {
		getOrCreateChildPhraseList();
		List<IntArray> leftMarginsList = new ArrayList<IntArray>();
		for (PhraseList phraseList : phraseListList) {
			IntArray leftMargins = phraseList.getLeftMargins();
			leftMarginsList.add(leftMargins);
		}
		return leftMarginsList;
	}
	
	/** assumes the largest index in phraseList is main body of table.
	 * 
	 * @return
	 */
	public int getMaxColumns() {
		getOrCreateChildPhraseList();
		int maxColumns = 0;
		for (PhraseList phraseList : phraseListList) {
			maxColumns = Math.max(maxColumns, phraseList.size());
		}
		return maxColumns;
	}

	public List<IntRange> getBestColumnRanges() {
		getOrCreateChildPhraseList();
		int maxColumns = getMaxColumns();
		List<IntRange> columnRanges = new ArrayList<IntRange>();
		for (int i = 0; i < maxColumns; i++) {
			columnRanges.add(i, null);
		}
		for (PhraseList phraseList : phraseListList) {
			if (phraseList.size() == maxColumns) {
				for (int i = 0; i < phraseList.size(); i++) {
					Phrase phrase = phraseList.get(i);
					IntRange range = phrase.getIntRange();
					IntRange oldRange = columnRanges.get(i);
					range = (oldRange == null) ? range : range.plus(oldRange);
					columnRanges.set(i, range);
				}
			}
		}
		return columnRanges;
	}
	
	public List<IntRange> getBestWhitespaceList() {
		getOrCreateChildPhraseList();
		int maxColumns = getMaxColumns();
		List<IntRange> bestColumnRanges = getBestColumnRanges();
		List<IntRange> bestWhitespaces = new ArrayList<IntRange>();
		if (maxColumns > 0) {
			bestWhitespaces.add(new IntRange(bestColumnRanges.get(0).getMin() - EPS, bestColumnRanges.get(0).getMin() - EPS));
			for (int i = 1; i < maxColumns; i++) {
				IntRange whitespace = new IntRange(bestColumnRanges.get(i - 1).getMax(), bestColumnRanges.get(i).getMax());
				bestWhitespaces.add(whitespace);
			}
		}
		return bestWhitespaces;
	}
	
	public HtmlTr createTableRow(PhraseList phraseList, Class clazz) {
		getOrCreateChildPhraseList();
		int maxColumns = getMaxColumns();
		List<IntRange> bestWhitespaces = getBestWhitespaceList();
		HtmlTr row = new HtmlTr();
		int iPhrase = 0;
		for (int icol = 0; icol < maxColumns; icol++) {
			HtmlElement cell = (clazz.equals(HtmlTh.class)) ? new HtmlTh() : new HtmlTd();
			row.appendChild(cell);
			Phrase phrase = phraseList.get(iPhrase);
			int nextPhraseX = (int) phrase.getFirstX();
			if (icol >= bestWhitespaces.size() - 1 || nextPhraseX < bestWhitespaces.get(icol + 1).getMin()) {
				cell.appendChild(phrase.getStringValue());
				if (++iPhrase >= phraseList.size()) {
					break;
				};
			}
		}
		return row;
	}
	
	public List<HtmlTr> createBodyTableRows(int start, int end, Class clazz) {
		getOrCreateChildPhraseList();
		List<HtmlTr> rows = new ArrayList<HtmlTr>();
		for (int i = start; i <= end; i++) {
			PhraseList phraseList = phraseListList.get(i);
			HtmlTr row = createTableRow(phraseList, clazz);
			rows.add(row);
		}
		return rows;
	}

	public int size() {
		getOrCreateChildPhraseList();
		return phraseListList.size();
	}

	public Real2Range getBoundingBox() {
		getOrCreateChildPhraseList();
		Real2Range bbox = null;
		if (phraseListList.size() > 0) {
			bbox = phraseListList.get(0).getBoundingBox();
			for (int i = 1; i < phraseListList.size(); i++) {
				bbox = bbox.plus(phraseListList.get(i).getBoundingBox());
			}
		}
		return bbox;
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateChildPhraseList();
		for (PhraseList phraseList : phraseListList) {
			phraseList.rotateAll(centreOfRotation, angle);
			LOG.debug("PL: "+phraseList.toXML());
		}
		updatePhraseListList();
	}
	
	public void updatePhraseListList() {
		for (int i = 0; i < phraseListList.size(); i++) {
			this.replaceChild(this.getChildElements().get(i), phraseListList.get(i));
		}
	}


}
