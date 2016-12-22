package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

public class PhraseList extends LineChunk implements Iterable<Phrase> {
	
	private static final Logger LOG = Logger.getLogger(PhraseList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "phraseList";
	// this is not exposed
	private List<Phrase> childPhraseList; 

	public PhraseList() {
		super();
		this.setClassName(TAG);
	}
	
	public PhraseList(PhraseList phraseList) {
		super(phraseList);
	}

	public Iterator<Phrase> iterator() {
		getOrCreateChildPhraseList();
		return childPhraseList.iterator();
	}
	
	public String toString() {
		return this.toXML();
	}

	public void add(Phrase phrase) {
		this.appendChild(phrase);
	}

	public IntArray getLeftMargins() {
		getOrCreateChildPhraseList();
		IntArray leftMargins = new IntArray();
		for (Phrase phrase : childPhraseList) {
			leftMargins.addElement((int) phrase.getFirstX());
		}
		return leftMargins;
	}
	
	public Phrase get(int i) {
		getOrCreateChildPhraseList();
		return i < 0 || i >= size() ? null : childPhraseList.get(i);
	}

	private List<Phrase> getOrCreateChildPhraseList() {
		if (childPhraseList == null) {
			List<Element> phraseChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+Phrase.TAG+"']");
			childPhraseList = new ArrayList<Phrase>();
			for (Element child : phraseChildren) {
				childPhraseList.add(new Phrase((SVGG)child));
			}
		}
		return childPhraseList;
	}

	public int size() {
		getOrCreateChildPhraseList();
		return childPhraseList.size();
	}

	public Real2Range getBoundingBox() {
		getOrCreateChildPhraseList();
		Real2Range bboxTotal = null;
		for (int i = 0; i < childPhraseList.size(); i++) {
			Phrase phrase = childPhraseList.get(i);
			Real2Range bbox = phrase.getBoundingBox();
			if (i == 0) {
				bboxTotal = bbox;
			} else {
				bboxTotal = bboxTotal.plus(bbox);
			}
		}
		return bboxTotal;
				
	}
	
	public Double getFontSize() {
		getOrCreateChildPhraseList();
		Double f = null;
		if (childPhraseList.size() > 0) {
			f = childPhraseList.get(0).getFontSize();
			for (int i = 1; i < childPhraseList.size(); i++) {
				Double ff = childPhraseList.get(i).getFontSize();
				if (ff != null) {
					f = Math.max(f,  ff);
				}
			}
		}
		return f;
	}

	public Element copyElement() {
//		return (Element) this.copy();
		getOrCreateChildPhraseList();
		Element element = (Element) this.copy();
		for (Phrase phrase : childPhraseList) {
			element.appendChild(phrase.copyElement());
		}
		return element;
	}

	public String getStringValue() {
		getOrCreateChildPhraseList();
		StringBuilder sb = new StringBuilder();
		for (Phrase phrase : childPhraseList) {
			sb.append(phrase.getStringValue());
			sb.append(" ");
		}
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateChildPhraseList();
		for (Phrase phrase : childPhraseList) {
			phrase.rotateAll(centreOfRotation, angle);
			LOG.debug("P: "+phrase.toXML());
		}
		updateChildPhraseList();
	}

	public void updateChildPhraseList() {
		for (int i = 0; i < childPhraseList.size(); i++) {
			this.replaceChild(this.getChildElements().get(i), childPhraseList.get(i));
		}
	}


}
