package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.util.MultisetUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRule;
import org.xmlcml.svg2xml.text.LineChunk;
import org.xmlcml.svg2xml.text.Phrase;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;

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
	public enum TableSectionTypeOLD {
		TITLE(0),
		HEADER(1),
		BODY(2),
		FOOTER(3),
		OTHER(-1);
		private int serial;

		private TableSectionTypeOLD(int serial) {
			this.serial = serial;
		}
	}

	public enum TableSectionType {
		TITLE("T"),
		TITLE_CONT("C"),
		HEADER("H"),
		BODY("B"),
		FOOTER("F"),
		OTHER("?");
		private String abbrev;

		private TableSectionType(String abbrev) {
			this.abbrev = abbrev;
		}
	}

	protected TableSectionType type;
	protected TableSectionTypeOLD typeOLD;
	protected List<HorizontalElement> horizontalElementList;
	protected Real2Range boundingBox;
	protected List<ColumnManager> columnManagerList;
	// what is the difference between these two?
	protected List<Phrase> allPhrasesInSection;
	protected PhraseListList sectionPhraseListList;
	protected List<PhraseListList> sectionChunks; // structure within the text (e.g. whitespace)
	protected double epsilon = 0.3;

	public TableSection(TableSectionTypeOLD typeOLD) {
		this();
		this.typeOLD = typeOLD;
	}
	
	/** copy constructor.
	 * 
	 * used from subclasses 
	 * 
	 * @param tableSection
	 */
	public TableSection(TableSection tableSection) {
		this.type = tableSection.type;
		this.typeOLD = tableSection.typeOLD;
		this.horizontalElementList = tableSection.horizontalElementList;
		this.allPhrasesInSection = tableSection.allPhrasesInSection;
		this.boundingBox = tableSection.boundingBox;
	}


	public TableSection() {
		this.horizontalElementList = new ArrayList<HorizontalElement>();
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
				sb.append("=>=>=>"+((HorizontalRule) horizontalElement).toString()+"<=<=<=\n");
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
		StringBuilder sb = new StringBuilder(typeOLD+": ");
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
	
	/** all phrases in section.
	 * 
	 * @return
	 */
	public List<Phrase> getOrCreateAllPhrasesInSection() {
		if (allPhrasesInSection == null) {
			allPhrasesInSection = new ArrayList<Phrase>();
			for (HorizontalElement element : this.getHorizontalElementList()) {
				if (element instanceof PhraseList) {
					PhraseList phraseList = (PhraseList) element;
					LOG.trace("PL "+ phraseList+" / "+phraseList.size());
					for (int i = 0; i < phraseList.size(); i++) {
						Phrase phrase = phraseList.get(i);
						if (phrase.getStringValue().trim().length() == 0) {
							continue;
						}
						allPhrasesInSection.add(phraseList.get(i));
					}
				}
			}
		}
		return allPhrasesInSection;
	}

	public PhraseListList getOrCreatePhraseListList() {
		if (sectionPhraseListList == null) {
			sectionPhraseListList = new PhraseListList();
			for (HorizontalElement element : this.getHorizontalElementList()) {
				if (element instanceof PhraseList) {
					PhraseList phraseList = (PhraseList) element;
					sectionPhraseListList.add(phraseList);
				}
			}
		}
		return sectionPhraseListList;
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
		List<Phrase> phrases = this.getOrCreateAllPhrasesInSection();
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

	public HtmlElement toHtml() {
		PhraseListList sectionPhraseListList = getOrCreatePhraseListList();
		HtmlElement sectionElement = sectionPhraseListList == null ? new HtmlP("missing section "+this.getClass().getSimpleName()) :
			sectionPhraseListList.toHtml();
		return sectionElement;
	}

	public boolean contains(Pattern regex) {
		getOrCreatePhraseListList();
		for (PhraseList phraseList : sectionPhraseListList) {
			if (phraseList.contains(regex)) {
				return true;
			}
		}
		return false;
	}

	/** debug down to the Phrase level.
	 * 
	 * @param tableSection
	 */
	public String debugPhrases() {
		StringBuilder sb = new StringBuilder();
		PhraseListList pll = this.getOrCreatePhraseListList();
		sb.append("pll: "+pll.size()+"\n");
		for (PhraseList pl : pll) {
			sb.append(">>pl: " + pl.size()+"\n");
			for (Phrase p : pl) {
				sb.append(">>>>>p: " + p.getY()+": "+p.getStringValue()+"\n");
			}
		}
		return sb.toString();
	}

	public void setType(TableSectionType type) {
		this.type = type;
	}

	public String matchAgainstIndividualPhrases(Pattern pattern) {
		getOrCreatePhraseListList();
		for (PhraseList phraseList : sectionPhraseListList) {
			String value = phraseList.getStringValue();
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) {
				return matcher.group(0);
			}
		}
		return null;
	}

	public boolean isTitleOrContinued() {
		return TableSectionType.TITLE.equals(type) || TableSectionType.TITLE_CONT.equals(type);
	}
	

}
