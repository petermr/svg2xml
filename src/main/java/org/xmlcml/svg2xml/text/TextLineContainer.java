package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2Range;

/** holds text lines in order
 * to simplify TextAnalyzer
 * 
 * @author pm286
 *
 */
public class TextLineContainer {

	private List<TextLine> linesWithLargestFont;
	private List<TextLine> textLineList;
	private SvgPlusCoordinate largestFontSize;
	private Real2Range textLinesLargetFontBoundingBox;
	private Set<SvgPlusCoordinate> fontSizeSet;
	
	public TextLineContainer() {
		
	}

	public void setTextLines(List<TextLine> textLineList) {
		this.textLineList = new ArrayList<TextLine>();
		for (TextLine textLine : textLineList) {
			this.textLineList.add(textLine);
		}
	}
	
	public List<TextLine> getLinesWithLargestFont() {
		if (linesWithLargestFont == null) {
			linesWithLargestFont = new ArrayList<TextLine>();
			getLargestFont();
			for (int i = 0; i < textLineList.size(); i++){
				TextLine textLine = textLineList.get(i);
				Double fontSize = (textLine == null) ? null : textLine.getFontSize();
				if (fontSize != null) {
					if (Real.isEqual(fontSize, largestFontSize.getDouble(), 0.001)) {
						linesWithLargestFont.add( textLine);
					}
				}
			}
		}
		return linesWithLargestFont;
	}

	public SvgPlusCoordinate getLargestFont() {
		largestFontSize = null;
		Set<SvgPlusCoordinate> fontSizes = this.getFontSizeSet();
		for (SvgPlusCoordinate fontSize : fontSizes) {
			if (largestFontSize == null || largestFontSize.getDouble() < fontSize.getDouble()) {
				largestFontSize = fontSize;
			}
		}
		return largestFontSize;
	}
	
	public Real2Range getLargestFontBoundingBox() {
		if (textLinesLargetFontBoundingBox == null) {
			if (linesWithLargestFont.size() > 0) {
				textLinesLargetFontBoundingBox = new Real2Range(new Real2Range(linesWithLargestFont.get(0).getBoundingBox()));
				for (int i = 1; i < linesWithLargestFont.size(); i++) {
					textLinesLargetFontBoundingBox.plus(linesWithLargestFont.get(i).getBoundingBox());
				}
			}
		}
		return textLinesLargetFontBoundingBox;
	}

	public Set<SvgPlusCoordinate> getFontSizeSet() {
		if (fontSizeSet == null) {
			if (textLineList != null) {
				fontSizeSet = new HashSet<SvgPlusCoordinate>();
				for (TextLine textLine : textLineList) {
					Set<SvgPlusCoordinate> textLineFontSizeSet = textLine.getFontSizeSet();
					fontSizeSet.addAll(textLineFontSizeSet);
				}
			}
		}
		return fontSizeSet;
	}


}
