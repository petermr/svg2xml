package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGText;

public class Word {

	private final static Logger LOG = Logger.getLogger(Word.class);
	
	List<SVGText> textList;
	private Real2Range boundingBox;
	private boolean guessWidth = true;
	
	public Word() {
		
	}

	/** create word from characters in svgText.
	 * 
	 * mainly for testing purposes. Is unlikely to provide valid coordinates
	 * for characters.
	 * 
	 * splits svgText into characters.
	 * 
	 * @param svgText
	 */
	public static Word createTestWord(SVGText svgText) {
		Word word = new Word();
		List<SVGText> textList = new ArrayList<SVGText>();
		String value = svgText.getValue();
		for (int i = 0; i < value.length(); i++) {
			Real2 r2 = new Real2();
			textList.add(new SVGText(r2, String.valueOf(value.charAt(i))));
		}
		word.setTextList(textList);
		return word;
	}

	public void setTextList(List<SVGText> textList) {
		for (SVGText text : textList) {
			this.add(text);
		}
	}

	public void add(SVGText text) {
		ensureTextList();
		textList.add(text);
	}

	private void ensureTextList() {
		if (textList == null) {
			textList = new ArrayList<SVGText>();
		}
	}
	
	public String toString() {
		return getValue();
	}

	public String getValue() {
		StringBuilder sb = new StringBuilder();
		for (SVGText text : textList) {
			sb.append(text.getValue());
		}
		return sb.toString();
	}

	public Double getSpaceCountBetween(Word followingWord) {
		SVGText char0 = this.get(this.getCharacterCount() - 1);
		SVGText char1 = followingWord.get(0);
		return char0.getEnSpaceCount(char1);
	}

	public Double getSeparationBetween(Word followingWord) {
		SVGText char0 = this.get(this.getCharacterCount() - 1);
		SVGText char1 = followingWord.get(0);
		return char0.getSeparation(char1);
	}

	public Integer getCharacterCount() {
		return textList.size();
	}

	public SVGText get(int index) {
		return textList.get(index);
	}

	public Double getStartX() {
		return textList.get(0).getX();
	}

	public Double getEndX() {
		SVGText endText = textList.get(textList.size() - 1);
		Double x = endText == null ? null : endText.getX();
		Double w =  endText == null ? null : endText.getScaledWidth(guessWidth );
		return x == null || w == null ? null : x + w;
	}

	public Double getMidX() {
		return (getStartX() + getEndX()) / 2.;
	}

	public Double translateToDouble() {
		Double d = null;
		try {
			d = new Double(toString());
		} catch (NumberFormatException e) {
			// cannot translate
		}
		return d;
	}

	public Integer translateToInteger() {
		Integer i = null;
		try {
			i = new Integer(toString());
		} catch (NumberFormatException e) {
			// cannot translate
		}
		return i;
	}

	/** creates a list of Words split at spaces.
	 * 
	 * <p>If no spaces, returns this.</p>
	 * 
	 * @return
	 */
	List<Word> splitAtSpaces() {
		List<Word> newWordList = new ArrayList<Word>();
		Word newWord = null;
		for (SVGText text : textList) {
			String value = text.getValue();
			LOG.trace(value);
			//is it a space?
			if (value.trim().length() == 0) {
				newWord = null;
			} else {
				if (newWord == null) {
					newWord = new Word();
					newWordList.add(newWord);
				}
				newWord.add(text);
			}
		}
		return newWordList;
	}

	/** creates a Phrase of Words from raw word.
	 * 
	 * <p>Some raw Words contain explicit spaces and these can be split into a Phrase.
	 * If the word has no spaces the the Phrase contains a single Word.</p>
	 * 
	 * @return
	 */
	public Phrase createPhrase() {
		Phrase phrase = new Phrase();
		List<Word> splitWords = this.splitAtSpaces();
		for (Word word : splitWords) {
			phrase.add(word);
		}
		return phrase;
	}

	public Real2Range getBoundingBox() {
		if (boundingBox == null){
			boundingBox = new Real2Range();
			RealRange xrange = new RealRange(getStartX(), getEndX());
			RealRange yrange = textList.get(0).getBoundingBox().getYRange();
			boundingBox = new Real2Range(xrange, yrange);
		}
		return boundingBox;
	}

	public RealRange getYRange() {
		getBoundingBox();
		return boundingBox.getYRange();
	}
	
	public Real2 getXY() {
		return (textList.size() == 0) ? null : textList.get(0).getXY();
	}

	public static List<Real2Range> createBBoxList(List<Word> wordList) {
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		for (Word word : wordList) {
			bboxList.add(word.getBoundingBox());
		}
		return bboxList;
	}
}
