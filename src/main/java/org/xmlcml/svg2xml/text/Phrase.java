package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Util;

/** a list of Words.
 * 
 * Normally a subcomponent of TextLine. Phrases are separated by large whitespace (Blank)
 * (more than normal inter-word spacing which is normally 1). A Phrase normally contains
 * a list of Words. There are no implied linguistic semantics (a Phrase could be several
 * numbers).
 * 
 * 
 * @author pm286
 *
 */
public class Phrase  extends LineChunk implements Iterable<Word> {
	
	private static final Logger LOG = Logger.getLogger(Phrase.class);
	
	private List<Word> wordList;

	public Phrase() {
		this.wordList = new ArrayList<Word>();
	}

	public void add(Word word) {
		wordList.add(word);
	}
	
	public Word get(int index) {
		return wordList.get(index);
	}
	
	@Override
	public Iterator<Word> iterator() {
		return wordList.iterator();
	}
	
	public int size() {
		return wordList.size();
	}

	public RealArray getInterWordWhitePixels() {
		RealArray separationArray = new RealArray();
		for (int i = 1; i < wordList.size(); i++) {
			Word word0 = wordList.get(i-1);
			Word word = wordList.get(i);
			double separation = Util.format(word0.getSeparationBetween(word), 3);
			separationArray.addElement(separation);
		}
		return separationArray;
	}

	public RealArray getInterWordWhiteEnSpaces() {
		RealArray spaceCountArray = new RealArray();
		for (int i = 1; i < wordList.size(); i++) {
			Word word0 = wordList.get(i-1);
			Word word = wordList.get(i);
			double spaceCount = Util.format(word0.getSpaceCountBetween(word), 3);
			spaceCountArray.addElement(spaceCount);
		}
		return spaceCountArray;
	}

	public RealArray getStartXArray() {
		RealArray startArray = new RealArray();
		for (int i = 0; i < wordList.size(); i++) {
			Word word = wordList.get(i);
			double start = Util.format(word.getStartX(), 3);
			startArray.addElement(start);
		}
		return startArray;
	}
	
	public RealArray getMidXArray() {
		RealArray startArray = new RealArray();
		for (int i = 0; i < wordList.size(); i++) {
			Word word = wordList.get(i);
			double end = Util.format(word.getMidX(), 3);
			startArray.addElement(end);
		}
		return startArray;
	}
	
	public RealArray getEndXArray() {
		RealArray startArray = new RealArray();
		for (int i = 0; i < wordList.size(); i++) {
			Word word = wordList.get(i);
			double end = Util.format(word.getEndX(), 3);
			startArray.addElement(end);
		}
		return startArray;
	}

	public Word getLastWord() {
		return wordList.get(wordList.size() - 1);
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
		return wordList.get(0).getStartX();
	}
	
	/** translates words into integers if possible.

	 * @return null if translation impossible
	 */
	public IntArray translateToIntArray() {
		IntArray intArray = new IntArray();
		for (Word word : wordList) {
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
		RealArray realArray = new RealArray();
		for (Word word : wordList) {
			Double d = word.translateToDouble();
			if (d == null) {
				realArray = null;
				break;
			}
			realArray.addElement(d);
		}
		return realArray;
	}

	public List<Word> getWordList() {
		return wordList;
	}

	public String getPrintableString() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < wordList.size() - 1; i++) {
			Word word = wordList.get(i);
			sb.append(word.toString());
			Double spaceCount = word.getSpaceCountBetween(wordList.get(i + 1));
			for (int j = 0; j < spaceCount; j++) {
				sb.append(" ");
			}
		}
		sb.append(wordList.get(wordList.size() - 1).toString());
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

	private Real2Range getBoundingBox() {
		Word word0 = wordList.get(0);
		Word wordN = wordList.get(wordList.size() - 1);
		Real2Range bbox = new Real2Range(new RealRange(word0.getStartX(), wordN.getEndX()), word0.getYRange().plus(wordN.getYRange()));
		return bbox;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for (int i = 0; i < wordList.size() - 1; i++) {
			Word word = wordList.get(i);
			sb.append("("+word.toString()+")");
			Double spaceCount = word.getSpaceCountBetween(wordList.get(i + 1));
			for (int j = 0; j < spaceCount; j++) {
				sb.append(".");
			}
		}
		sb.append("("+wordList.get(wordList.size() - 1).toString()+")");
//		sb.append("["+this.getBoundingBox()+"]");
		sb.append("}");
		return sb.toString();
	}

}
