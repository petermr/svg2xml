package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.Phrase;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;

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

	private TableSectionType type;
	private List<HorizontalElement> horizontalElementList;
	private List<Phrase> phrases;
//	private PhraseListList phraseListList;

	public TableSection(TableSectionType type) {
		this.type = type;
		this.horizontalElementList = new ArrayList<HorizontalElement>();
	}

	public void add(HorizontalElement horizontalElement) {
		this.horizontalElementList.add(horizontalElement);
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
						phrases.add(phraseList.get(i));
					}
				}
			}
		}
		return phrases;
	}

}
