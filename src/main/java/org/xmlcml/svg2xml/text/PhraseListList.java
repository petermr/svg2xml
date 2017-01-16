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
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

public class PhraseListList extends SVGG implements Iterable<PhraseList> {
	public static final Logger LOG = Logger.getLogger(PhraseListList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final Double SUPERSCRIPT_Y_RATIO = 0.5;
	public static final Double SUBSCRIPT_Y_RATIO = 0.75;
//	private static final Double SUPERSCRIPT_FONT_RATIO = 1.05;

	public final static String TAG = "phraseListList";
	private static final int EPS = 5;

	private List<PhraseList> childPhraseListList;
	private List<Phrase> phrases;

	public PhraseListList() {
		super();
		this.setClassName(TAG);
	}
	
	public Iterator<PhraseList> iterator() {
		getOrCreateChildPhraseList();
		return childPhraseListList.iterator();
	}

	public List<PhraseList> getOrCreateChildPhraseList() {
		if (childPhraseListList == null) {
			List<Element> phraseChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+PhraseList.TAG+"']");
			childPhraseListList = new ArrayList<PhraseList>();
			for (Element child : phraseChildren) {
				PhraseList phraseList = (PhraseList)child;
				childPhraseListList.add(phraseList);
			}
		}
		return childPhraseListList;
	}

	public String getStringValue() {
		getOrCreateChildPhraseList();
		StringBuilder sb = new StringBuilder();
		for (PhraseList phraseList : childPhraseListList) {
			sb.append(""+phraseList.getStringValue()+"//");
		}
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}

	public void add(PhraseList phraseList) {
		this.appendChild(phraseList);
		childPhraseListList = null;
		getOrCreateChildPhraseList();
	}

	public PhraseList get(int i) {
		getOrCreateChildPhraseList();
		return childPhraseListList.get(i);
	}
	
	public List<IntArray> getLeftMarginsList() {
		getOrCreateChildPhraseList();
		List<IntArray> leftMarginsList = new ArrayList<IntArray>();
		for (PhraseList phraseList : childPhraseListList) {
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
		for (PhraseList phraseList : childPhraseListList) {
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
		for (PhraseList phraseList : childPhraseListList) {
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
			bestWhitespaces.add(new IntRange(bestColumnRanges.get(0).getMin() - EPS, bestColumnRanges.get(0).getMax() - EPS));
			for (int i = 1; i < maxColumns; i++) {
				IntRange whitespace = new IntRange(bestColumnRanges.get(i - 1).getMax(), bestColumnRanges.get(i).getMax());
				bestWhitespaces.add(whitespace);
			}
		}
		return bestWhitespaces;
	}
	
	/** find rightmostWhitespace range which includes start of phrase.
	 * 
	 */
	public int getRightmostEnclosingWhitespace(List<IntRange> bestWhitespaces, Phrase phrase) {
		for (int i = bestWhitespaces.size() - 1; i >= 0; i--) {
			IntRange range = bestWhitespaces.get(i);
			int phraseX = (int)(double) phrase.getStartX();
			if (range.contains(phraseX)) {
				return i;
			}
		}
		return -1;
	}

	public int size() {
		getOrCreateChildPhraseList();
		return childPhraseListList.size();
	}

	public Real2Range getBoundingBox() {
		getOrCreateChildPhraseList();
		Real2Range bbox = null;
		if (childPhraseListList.size() > 0) {
			bbox = childPhraseListList.get(0).getBoundingBox();
			for (int i = 1; i < childPhraseListList.size(); i++) {
				bbox = bbox.plus(childPhraseListList.get(i).getBoundingBox());
			}
		}
		return bbox;
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateChildPhraseList();
		for (PhraseList phraseList : childPhraseListList) {
			phraseList.rotateAll(centreOfRotation, angle);
			LOG.trace("PL: "+phraseList.toXML());
		}
		updatePhraseListList();
	}
	
	public void updatePhraseListList() {
		for (int i = 0; i < childPhraseListList.size(); i++) {
			this.replaceChild(this.getChildElements().get(i), childPhraseListList.get(i));
		}
	}

	public Real2 getXY() {
		return this.getBoundingBox().getCorners()[0];
	}

	public boolean remove(PhraseList phraseList) {
		boolean remove = false;
		if (childPhraseListList != null && phraseList != null) {
			remove = childPhraseListList.remove(phraseList);
		}
		return remove;
	}

	/**
	 * analyses neighbouring PhraseLists to see if the font sizes and Y-coordinates
	 * are consistent with sub or superscripts. If so, merges the lines 
	 * phraseList.mergeByXCoord(lastPhraseList)
	 * and removes the sub/super line
	 * lines. The merged phraseList contains all the characters and coordinates in 
	 * phraseLists with sub/superscript boolean flags.
	 * 
	 * getStringValue() represents the sub and superscripts by TeX notation (_{foo} and ^{bar})
	 * but the actual content retains coordinates and can be output to HTML
	 * 
	 * The ratios for the y-values and font sizes are hardcoded but will be settable later.
	 */
	public void applySubAndSuperscripts() {
		Double lastY = null;
		Double lastFontSize = null;
		Double deltaY = null;
		PhraseList lastPhraseList = null;
		List<PhraseList> removeList = new ArrayList<PhraseList>();
		for (PhraseList phraseList : this) {
			Double fontSize = Util.format(phraseList.getFontSize(), 1);
			Double y = Util.format(phraseList.getXY().getY(), 1);
			if (lastY != null) {
				double fontRatio = fontSize / lastFontSize;
				deltaY = y - lastY;
				if (deltaY > 0 && deltaY < fontSize * SUPERSCRIPT_Y_RATIO && fontRatio >= 1.0) {
					LOG.debug("SUPER "+lastPhraseList.getStringValue()+" => "+phraseList.getStringValue());
					lastPhraseList.setSuperscript(true);
					phraseList.mergeByXCoord(lastPhraseList);
					removeList.add(lastPhraseList);
				} else if (deltaY > 0 && deltaY < lastFontSize * SUBSCRIPT_Y_RATIO && fontRatio <= 1.0) {
					LOG.debug("SUB "+phraseList.getStringValue()+" => "+lastPhraseList.getStringValue());
					phraseList.setSubscript(true);
					lastPhraseList.mergeByXCoord(phraseList);
					removeList.add(phraseList);
				}
			}
			lastPhraseList = phraseList;
			lastFontSize = fontSize;
			lastY = y;
		}
		for (PhraseList phraseList : removeList) {
			remove(phraseList);
		}
	}

	public List<Phrase> getOrCreatePhrases() {
		if (phrases == null) {
			phrases = new ArrayList<Phrase>();
			for (PhraseList phraseList : this) {
				for (Phrase phrase : phraseList) {
					phrases.add(phrase);
				}
			}
		}
		return phrases;
	}



}
