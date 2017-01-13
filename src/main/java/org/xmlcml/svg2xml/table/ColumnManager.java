package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.svg2xml.text.Phrase;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class ColumnManager {
	private static final Logger LOG = Logger.getLogger(ColumnManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private IntRange enclosingRange;
	private Multiset<Integer> startXMultiset;
	private Multiset<Integer> endXMultiset;
	private List<Phrase> columnPhraseList;

	public ColumnManager() {
	}

	public void setEnclosingRange(IntRange enclosingRange) {
		if (this.enclosingRange == null) {
			this.enclosingRange = enclosingRange;
		} else {
			if (!this.enclosingRange.equals(enclosingRange)) {
				LOG.warn("new EnclosingRange: "+this.enclosingRange+"=>"+enclosingRange);
//				this.enclosingRange = enclosingRange;
			}
		}
	}

	public void setStartX(Double startX) {
		if (startX != null) {
			ensureStartXMultiset();
			startXMultiset.add((int)(double)startX);
		}
	}

	private void ensureStartXMultiset() {
		if (startXMultiset == null) {
			startXMultiset = HashMultiset.create();
		}
	}

	public void setEndX(Double endX) {
		if (endX != null) {
			ensureEndXMultiset();
			endXMultiset.add((int)(double)endX);
		}
	}
	
	private void ensureEndXMultiset() {
		if (endXMultiset == null) {
			endXMultiset = HashMultiset.create();
		}
	}

	public void debug() {
		ensureStartXMultiset();
		ensureEndXMultiset();
		LOG.debug(startXMultiset+"\n"+endXMultiset);
	}

	public void addPhrase(Phrase phrase) {
		ensureColumnPhraseList();
		columnPhraseList.add(phrase);
	}

	private void ensureColumnPhraseList() {
		if (columnPhraseList == null) {
			columnPhraseList = new ArrayList<Phrase>();
		}
	}

	public Phrase getPhrase(int iRow) {
		ensureColumnPhraseList();
		return (iRow < 0 || iRow >= columnPhraseList.size()) ? null : columnPhraseList.get(iRow);
	}

}
