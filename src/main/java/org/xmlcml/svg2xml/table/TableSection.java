package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.util.MultisetUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.LineChunk;
import org.xmlcml.svg2xml.text.Phrase;
import org.xmlcml.svg2xml.text.PhraseList;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** holds sections such as title, header, body, footer
 * 
 * @author pm286
 *
 */
public class TableSection {

	private static final Logger LOG = Logger.getLogger(TableSection.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public enum TableSectionType {
		TITLE(0),
		HEADER(1),
		BODY(2),
		FOOTER(3),
		OTHER(-1);
		private int serial;

		private TableSectionType(int serial) {
			this.serial = serial;
		}
	}

	protected TableSectionType type;
	protected List<HorizontalElement> horizontalElementList;
	protected Real2Range boundingBox;
	protected List<ColumnManager> columnManagerList;
	protected List<Phrase> phrases;
	protected List<PhraseList> phraseLists;
	protected double epsilon = 0.3;

	public TableSection(TableSectionType type) {
		this.type = type;
		this.horizontalElementList = new ArrayList<HorizontalElement>();
	}
	
	public TableSection(TableSection tableSection) {
		this.type = tableSection.type;
		this.horizontalElementList = tableSection.horizontalElementList;
		this.phrases = tableSection.phrases;
		this.boundingBox = tableSection.boundingBox;
	}


	public void add(HorizontalElement horizontalElement) {
		this.horizontalElementList.add(horizontalElement);
		Real2Range bbox = ((SVGElement)horizontalElement).getBoundingBox();
		boundingBox = (boundingBox == null) ? bbox : boundingBox.plus(bbox);
	}

	public List<HorizontalElement> getHorizontalElementList() {
		return horizontalElementList;
	}

	public String getStringValue() {
		StringBuilder sb = new StringBuilder();
		for (HorizontalElement horizontalElement : horizontalElementList) {
			if (horizontalElement instanceof PhraseList) {
				sb.append(((PhraseList) horizontalElement).getStringValue()+"\n");
			} else {
				sb.append("=>=>=>"+((HorizontalRuler) horizontalElement).toString()+"<=<=<=\n");
			}
		}
		return sb.toString();
	}
	
	public int getPhraseListCount() {
		int i = 0;
		for (HorizontalElement horizontalElement : horizontalElementList) {
			if (horizontalElement instanceof PhraseList) {
				i++;
			}
		}
		return i;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(type+": ");
		sb.append(horizontalElementList.size()+"\n");
//		if (horizontalElementList.size() > 0) {
//			sb.append(String.valueOf(horizontalElementList.get(0))+"...\n");
//			sb.append(String.valueOf("..."+horizontalElementList.get(horizontalElementList.size()-1))+"\n");
//		}
		for (int i = 0; i < horizontalElementList.size(); i++) {
			HorizontalElement horizontalElement = horizontalElementList.get(i);
			String s = String.valueOf(horizontalElement+"\n");
			sb.append(s);
		}
		return sb.toString();
	}
	
	public List<Phrase> getOrCreatePhrases() {
		if (phrases == null) {
			phrases = new ArrayList<Phrase>();
			for (HorizontalElement element : this.getHorizontalElementList()) {
				if (element instanceof PhraseList) {
					PhraseList phraseList = (PhraseList) element;
					for (int i = 0; i < phraseList.size(); i++) {
						Phrase phrase = phraseList.get(i);
						if (phrase.getStringValue().trim().length() == 0) {
							continue;
						}
						phrases.add(phraseList.get(i));
					}
				}
			}
		}
		return phrases;
	}

	public List<PhraseList> getOrCreatePhraseLists() {
		if (phraseLists == null) {
			phraseLists = new ArrayList<PhraseList>();
			for (HorizontalElement element : this.getHorizontalElementList()) {
				if (element instanceof PhraseList) {
					PhraseList phraseList = (PhraseList) element;
					phraseLists.add(phraseList);
				}
			}
		}
		return phraseLists;
	}

	public Real2Range getBoundingBox() {
		return boundingBox;
	}

	protected void createSortedColumnManagerListFromUnassignedPhrases(List<Phrase> currentPhrases) {
		if (currentPhrases == null) {
			LOG.trace("no current phrases");
			return;
		}
		columnManagerList = new ArrayList<ColumnManager>();
		for (Phrase phrase : currentPhrases) {
			IntRange phraseRange = phrase.getIntRange();
			ColumnManager existingColumnManager = null;
			for (int i = 0; i < columnManagerList.size(); i++) {
				ColumnManager columnManager = columnManagerList.get(i);
				IntRange columnManagerRange = columnManager.getEnclosingRange();
				if (columnManagerRange != null && columnManagerRange.intersectsWith(phraseRange)) {
					columnManager.addPhrase(phrase);
					existingColumnManager = columnManager;
					break;
				}
			}
			if (existingColumnManager == null) {
				existingColumnManager = new ColumnManager();
				existingColumnManager.addPhrase(phrase);
				columnManagerList.add(existingColumnManager);
			}
		}
		Collections.sort(columnManagerList, ColumnManager.X_COMPARATOR);
	}

	public List<ColumnManager> getOrCreateColumnManagerList() {
		if (columnManagerList == null) {
			columnManagerList = new ArrayList<ColumnManager>();
		}
		return columnManagerList;
	}

	protected String getFontInfo() {
		List<Phrase> phrases = this.getOrCreatePhrases();
		Multiset<Double> fontSizeSet = HashMultiset.create();
		Multiset<String> fontFamilySet = HashMultiset.create();
		Multiset<String> fontWeightSet = HashMultiset.create();
		Multiset<String> fontStyleSet = HashMultiset.create();
		for (LineChunk phrase : phrases) {
			fontSizeSet.add(phrase.getFontSize());
			fontFamilySet.add(String.valueOf(phrase.getFontFamily()));
			fontWeightSet.add(String.valueOf(phrase.getFontWeight()));
			fontStyleSet.add(String.valueOf(phrase.getFontStyle()));
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{"+MultisetUtil.getEntriesSortedByCount(fontFamilySet).toString()+"}");
		sb.append("{"+MultisetUtil.getDoubleEntriesSortedByCount(fontSizeSet).toString()+"}");
		sb.append("{"+MultisetUtil.getEntriesSortedByCount(fontWeightSet).toString()+"}");
		sb.append("{"+MultisetUtil.getEntriesSortedByCount(fontStyleSet).toString()+"}");
		return sb.toString();
	}

}
