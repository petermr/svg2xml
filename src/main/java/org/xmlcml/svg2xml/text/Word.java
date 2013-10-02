package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGText;

public class Word {

	List<SVGText> textList;
	
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
		StringBuilder sb = new StringBuilder();
		for (SVGText text : textList) {
			sb.append(text.getValue());
		}
		return sb.toString();
	}

	public Double getSpaceCountBetween(Word followingWord) {
		SVGText char0 = this.get(this.getCharacterCount()-1);
		SVGText char1 = followingWord.get(0);
		return char0.getEnSpaceCount(char1);
	}

	public Double getSeparationBetween(Word followingWord) {
		SVGText char0 = this.get(this.getCharacterCount()-1);
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
		return endText.getX() + endText.getScaledWidth();
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

}
