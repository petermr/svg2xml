package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

@Deprecated // moved to svg
public class PhraseListOld extends LineChunkOld implements Iterable<PhraseOld> {
	private static final Logger LOG = Logger.getLogger(PhraseListOld.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "phraseList";

	// this is not exposed
	private List<PhraseOld> childPhraseList; 

	public PhraseListOld() {
		super();
		this.setClassName(TAG);
	}
	
	public PhraseListOld(PhraseListOld phraseList) {
		super(phraseList);
	}

	public Iterator<PhraseOld> iterator() {
		getOrCreateChildPhraseList();
		return childPhraseList.iterator();
	}
	
	public void add(PhraseOld phrase) {
		this.appendChild(phrase);
	}

	protected List<? extends LineChunkOld> getChildChunks() {
		getOrCreateChildPhraseList();
		return childPhraseList;
	}


	public IntArray getLeftMargins() {
		getOrCreateChildPhraseList();
		IntArray leftMargins = new IntArray();
		for (PhraseOld phrase : childPhraseList) {
			Double firstX = phrase.getFirstX();
			if (firstX != null) {
				leftMargins.addElement((int)(double) firstX);
			}
		}
		return leftMargins;
	}
	
	public PhraseOld get(int i) {
		getOrCreateChildPhraseList();
		return i < 0 || i >= size() ? null : childPhraseList.get(i);
	}

	public List<PhraseOld> getOrCreateChildPhraseList() {
		if (childPhraseList == null) {
			List<Element> phraseChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+PhraseOld.TAG+"']");
			childPhraseList = new ArrayList<PhraseOld>();
			for (Element child : phraseChildren) {
				// FIXME 
				childPhraseList.add(new PhraseOld((SVGG)child));
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
			PhraseOld phrase = childPhraseList.get(i);
			Real2Range bbox = phrase.getBoundingBox();
			if (i == 0) {
				bboxTotal = bbox;
			} else if (bboxTotal != null) {
				bboxTotal = bboxTotal.plus(bbox);
			}
		}
		return bboxTotal;
				
	}
	
	public Real2 getXY() {
		Real2Range bbox = this.getBoundingBox();
		return bbox == null ? null : bbox.getLLURCorners()[0];
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
		for (LineChunkOld phrase : childPhraseList) {
			element.appendChild(phrase.copyElement());
		}
		return element;
	}

	public String getStringValue() {
		// this needs memoization
		getOrCreateChildPhraseList();
		StringBuilder sb = new StringBuilder();
		LineChunkOld lastPhrase = null;
		for (int i = 0; i < childPhraseList.size(); i++) {
			PhraseOld phrase = childPhraseList.get(i);
			if (lastPhrase != null) {
				if (lastPhrase.shouldAddSpaceBefore(phrase)) {
					sb.append(SPACE);
				}
			}
			sb.append(phrase.getStringValue());
			lastPhrase = phrase;
		}
		String stringValue = sb.toString();
		LOG.trace("SV "+stringValue);
		this.setStringValueAttribute(stringValue);
		return stringValue;
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateChildPhraseList();
		for (PhraseOld phrase : childPhraseList) {
			phrase.rotateAll(centreOfRotation, angle);
			LOG.trace("P: "+phrase.toXML());
		}
		updateChildPhraseList();
	}

	public void updateChildPhraseList() {
		for (int i = 0; i < childPhraseList.size(); i++) {
			this.replaceChild(this.getChildElements().get(i), childPhraseList.get(i));
		}
	}

	@Override
	public String toString() {
		return this.getStringValue();
	}

	public PhraseListOld extractIncludedLists(IntRange tableSpan) {
		PhraseListOld includedPhraseList = new PhraseListOld();
		for (PhraseOld phrase : this) {
			if (tableSpan.includes(phrase.getIntRange())) {
				includedPhraseList.add(new PhraseOld(phrase));
			} else {
				LOG.trace("excluded phrase by tableSpan: "+phrase);
			}
		}
		return includedPhraseList;
	}

	public void mergeByXCoord(PhraseListOld otherPhraseList) {
		Queue<PhraseOld> otherQueue = otherPhraseList.getPhraseQueue();
		Queue<PhraseOld> thisQueue = this.getPhraseQueue();
		PhraseOld thisPhrase = thisQueue.isEmpty() ? null : thisQueue.remove();
		PhraseOld otherPhrase = otherQueue.isEmpty() ? null : otherQueue.remove();
		List<PhraseOld> newPhraseList = new ArrayList<PhraseOld>();
		while (thisPhrase != null || otherPhrase != null) {
			if (thisPhrase == null && !thisQueue.isEmpty()) {
				thisPhrase = thisQueue.remove();
			}
			if (otherPhrase == null && !otherQueue.isEmpty()) {
				otherPhrase = otherQueue.remove();
			}
			if (thisPhrase == null && otherPhrase != null) {
				newPhraseList.add(otherPhrase);
				otherPhrase = null;
			} else if (otherPhrase == null && thisPhrase != null) {
				newPhraseList.add(thisPhrase);
				thisPhrase = null;
			} else if (thisPhrase.getX() < otherPhrase.getX()) {
				newPhraseList.add(thisPhrase);
				thisPhrase = null;
			} else {
				newPhraseList.add(otherPhrase);
				otherPhrase = null;
			}
		}
		this.childPhraseList = newPhraseList;
	}

	public Queue<PhraseOld> getPhraseQueue() {
		Queue<PhraseOld> phraseQueue = new LinkedList<PhraseOld>();
		for (PhraseOld phrase : this) {
			phraseQueue.add(phrase);
		}
		return phraseQueue;
	}

	private void addSuperscript(PhraseOld superPhrase) {
		for (int index = 0; index <= this.size(); index++) {
			LineChunkOld phrase1 = (index == 0) ? null : this.get(index - 1);
			LineChunkOld phrase2 = index == this.size() ? null : this.get(index);
			if (canHaveSuperscript(phrase1, superPhrase, phrase2)) {
				this.insertSuperscript(index, superPhrase);
				break;
			}
		}
	}

	public void insertSuperscript(int index, PhraseOld superPhrase) {
		this.childPhraseList.add(index, superPhrase);
		superPhrase.setSuperscript(true);
	}
	
	public boolean canHaveSuperscript(LineChunkOld phrase1, PhraseOld superPhrase, LineChunkOld phrase2) {
		boolean overlap = false;
		Real2Range bbox1 = phrase1 == null ? null : phrase1.getOrCreateBoundingBox().format(1).getReal2RangeExtendedInX(0.0, 1.0);
		Real2Range bbox2 = phrase2 == null ? null : phrase2.getOrCreateBoundingBox().format(1).getReal2RangeExtendedInX(0.0, 1.0);
		Real2Range superBBox = superPhrase.getOrCreateBoundingBox().format(1).getReal2RangeExtendedInX(1.0, 0.0);
		Real2Range over1 = bbox1 == null ? null : bbox1.intersectionWith(superBBox);
		Real2Range over2 = bbox2 == null ? null : bbox2.intersectionWith(superBBox);
		if ((phrase1 == null || over1 != null) ||
			(phrase2 == null || over2 != null)) {			
			LOG.trace("OVER "+this.getStringValue()+" / "+superPhrase.getStringValue());
			overlap = true;
		}
		return overlap;
	}

	public void setSuperscript(boolean b) {
		for (LineChunkOld phrase : this) {
			phrase.setSuperscript(b);
		}
	}

	public void setSubscript(boolean b) {
		for (LineChunkOld phrase : this) {
			phrase.setSubscript(b);
		}
	}

	public HtmlElement toHtml() {
		HtmlElement span = new HtmlSpan();
		span.setClassAttribute("phraseList");
		span = addSuscriptsAndStyle(span);
		PhraseOld lastPhrase = null;
		for (int i = 0; i < this.size(); i++) {
			PhraseOld phrase = this.get(i);
			HtmlElement phraseElement = phrase.toHtml();
			if (i > 0) {
				if (lastPhrase.shouldAddSpaceBefore(phrase)) {
					span.appendChild(LineChunkOld.SPACE);
				} else {
				}
			}
			if (phraseElement instanceof HtmlSpan) {
				span.appendChild(phraseElement.getValue());
			} else {
				span.appendChild(phraseElement.copy());
			}
			lastPhrase = phrase;
		}
		LOG.trace("PHRASE_LIST" + span.toXML());
		return span;
	}

	public List<HtmlTh> getThList() {
		List<HtmlTh> thList = new ArrayList<HtmlTh>();
		if (childPhraseList != null) {
			for (PhraseOld phrase : childPhraseList) {
				HtmlElement element = phrase.toHtml();
				HtmlTh th = new HtmlTh();
				th.appendChild(element.copy());
				thList.add(th);
			}
		}
		return thList;
	}

	public boolean contains(Pattern regex) {
		getOrCreateChildPhraseList();
		for (PhraseOld phrase : childPhraseList) {
			if (phrase.contains(regex)) {
				return true;
			}
		}
		return false;
	}

	public String getCSSStyle() {
		String plStyle = null;
		for (PhraseOld phrase : this) {
			String pStyle = phrase.getCSSStyle();
			if (plStyle == null) {
				pStyle = plStyle;
			} else if (plStyle.equals(pStyle)) {
				// OK
			} else {
				plStyle = "mixed";
			}
		}
		return plStyle;
	}


}
