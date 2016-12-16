package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Util;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

/** 
 * A list of Words.
 * 
 * Normally a subcomponent of TextLine. Phrases are separated by large whitespace (Blank)
 * (more than normal inter-word spacing which is normally 1). A Phrase normally contains
 * a list of Words. There are no implied linguistic semantics (a Phrase could be several
 * numbers).
 * 
 * @author pm286
 */
public class Phrase extends LineChunk implements Iterable<Word> {
	
	private static final Logger LOG = Logger.getLogger(Phrase.class);
	
	public final static String TAG = "phrase";
	
	private List<Word> childWordList;

	public Phrase() {
		super();
		this.setClassName(TAG);
	}

	public Phrase(Phrase phrase) {
		super(phrase);
		// TODO Auto-generated constructor stub
	}

	public Phrase(SVGG g) {
		super(g);
	}

	public void add(SVGElement word) {
//		word.detach();
		this.appendChild(word);
	}
	
	public Word get(int index) {
		getOrCreateWordList();
		return childWordList.get(index);
	}
	
	public Iterator<Word> iterator() {
		getOrCreateWordList();
		return childWordList.iterator();
	}
	
	public int size() {
		getOrCreateWordList();
		return childWordList.size();
	}

	public RealArray getInterWordWhitePixels() {
		getOrCreateWordList();
		RealArray separationArray = new RealArray();
		for (int i = 1; i < childWordList.size(); i++) {
			Word word0 = childWordList.get(i-1);
			Word word = childWordList.get(i);
			double separation = Util.format(word0.getSeparationBetween(word), 3);
			separationArray.addElement(separation);
		}
		return separationArray;
	}

	public RealArray getInterWordWhiteEnSpaces() {
		getOrCreateWordList();
		RealArray spaceCountArray = new RealArray();
		for (int i = 1; i < childWordList.size(); i++) {
			Word word0 = childWordList.get(i - 1);
			Word word = childWordList.get(i);
			double spaceCount = Util.format(word0.getSpaceCountBetween(word), 3);
			spaceCountArray.addElement(spaceCount);
		}
		return spaceCountArray;
	}

	public RealArray getStartXArray() {
		getOrCreateWordList();
		RealArray startArray = new RealArray();
		for (int i = 0; i < childWordList.size(); i++) {
			Word word = childWordList.get(i);
			double start = Util.format(word.getStartX(), 3);
			startArray.addElement(start);
		}
		return startArray;
	}
	
	public RealArray getMidXArray() {
		getOrCreateWordList();
		RealArray startArray = new RealArray();
		for (int i = 0; i < childWordList.size(); i++) {
			Word word = childWordList.get(i);
			double end = Util.format(word.getMidX(), 3);
			startArray.addElement(end);
		}
		return startArray;
	}
	
	public RealArray getEndXArray() {
		getOrCreateWordList();
		RealArray startArray = new RealArray();
		for (int i = 0; i < childWordList.size(); i++) {
			Word word = childWordList.get(i);
			double end = Util.format(word.getEndX(), 3);
			startArray.addElement(end);
		}
		return startArray;
	}

	public Word getLastWord() {
		getOrCreateWordList();
		return childWordList.get(childWordList.size() - 1);
	}
	
	/** start of first word.
	 * 
	 * @return
	 */
	public double getFirstX() {
		return get(0).getStartX();
	}
	
	/** middle coordinate (average of startX and endX. 
.	 * 
	 * @return
	 */
	public double getMidX() {
		return (getStartX() + getEndX()) / 2.;
	}
	
	/** end of last word.
	 * 
	 * @return
	 */
	public double getEndX() {
		return getLastWord().getEndX();
	}

	/** start of first word.
	 * 
	 * @return
	 */
	public double getStartX() {
		getOrCreateWordList();
		return childWordList.get(0).getStartX();
	}
	
	/** translates words into integers if possible.

	 * @return null if translation impossible
	 */
	public IntArray translateToIntArray() {
		getOrCreateWordList();
		IntArray intArray = new IntArray();
		for (Word word : childWordList) {
			Integer i = word.translateToInteger();
			if (i == null) {
				intArray = null;
				break;
			}
			intArray.addElement(i);
		}
		return intArray;
	}

	/** translates words into numbers if possible.

	 * doesn't yet deal with superscripts.
	 * 
	 * @return null if translation impossible
	 */
	public RealArray translateToRealArray() {
		getOrCreateWordList();
		RealArray realArray = new RealArray();
		for (Word word : childWordList) {
			Double d = word.translateToDouble();
			if (d == null) {
				realArray = null;
				break;
			}
			realArray.addElement(d);
		}
		return realArray;
	}

	public List<Word> getOrCreateWordList() {
		if (childWordList == null) {
			List<Element> wordChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+Word.TAG+"']");
			childWordList = new ArrayList<Word>();
			for (Element child : wordChildren) {
				childWordList.add(new Word((SVGG)child));
			}
		}
		return childWordList;
	}

	public String getPrintableString() {
		getOrCreateWordList();
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < childWordList.size() - 1; i++) {
			Word word = childWordList.get(i);
			sb.append(word.toString());
			Double spaceCount = word.getSpaceCountBetween(childWordList.get(i + 1));
			for (int j = 0; j < spaceCount; j++) {
				sb.append(" ");
			}
		}
		sb.append(childWordList.get(childWordList.size() - 1).toString());
		return sb.toString();
	}

	/** creates rectangle between two phrases.
	 * 
	 * <b>sets bounding box in Blank</b>
	 * @param nextPhrase
	 * @return
	 */
	public Blank createBlankBetween(Phrase nextPhrase) {
		
		Real2Range thisBBox = this.getBoundingBox();
		Real2Range nextBBox = nextPhrase.getBoundingBox();
		RealRange xrange = new RealRange(thisBBox.getXMax(), nextBBox.getXMin());
		RealRange yrange = new RealRange(
				Math.min(thisBBox.getYMin(), nextBBox.getYMin()),
				Math.max(thisBBox.getYMax(), nextBBox.getYMax()));
		Blank blank = new Blank(new Real2Range(xrange, yrange));
		return blank;
	}

	public Real2Range getBoundingBox() {
		getOrCreateWordList();
		Word word0 = childWordList.get(0);
		Word wordN = childWordList.get(childWordList.size() - 1);
		RealRange xRange = word0.getStartX() == null || wordN.getEndX() == null ?
				null : new RealRange(word0.getStartX(), wordN.getEndX());
		RealRange yRange = word0.getYRange().plus(wordN.getYRange());
		Real2Range bbox = (word0 == null || wordN == null) ? null :
			new Real2Range(xRange, yRange);
		return bbox;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append(getStringValue());
		sb.append("}");
		return sb.toString();
	}

	public String getStringValue() {
		getOrCreateWordList();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < childWordList.size() - 1; i++) {
			Word word = childWordList.get(i);
			sb.append(""+word.getStringValue()+"");
			Double spaceCount = word.getSpaceCountBetween(childWordList.get(i + 1));
			for (int j = 0; j < spaceCount; j++) {
				sb.append(Word.SPACE_SYMBOL);
			}
		}
		sb.append(""+childWordList.get(childWordList.size() - 1).toString()+"");
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}

	public IntRange getIntRange() {
		return new IntRange((int)getFirstX(), (int)getEndX());
	}

	public Double getFontSize() {
		getOrCreateWordList();
		Double f = null;
		if (childWordList.size() > 0) {
			f = childWordList.get(0).getFontSize();
			for (int i = 1; i < childWordList.size(); i++) {
				Double ff = childWordList.get(i).getFontSize();
				if (ff != null) {
					f = Math.max(f,  ff);
				}
			}
		}
		return f;
	}

	public Element copyElement() {
		getOrCreateWordList();
		Element element = (Element) this.copy();
		for (Word word : childWordList) {
			element.appendChild(word.copyElement());
		}
		return element;
	}


}
