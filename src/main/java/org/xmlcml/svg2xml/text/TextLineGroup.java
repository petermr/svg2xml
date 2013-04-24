package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;

/** holds one or more TextLines in a chunk
 * bounding boxes of textLines overlap
 * @author pm286
 *
 */
public class TextLineGroup implements Iterable<TextLine> {

	private final static Logger LOG = Logger.getLogger(TextLineGroup.class);
	private List<TextLine> textLineList = null;
	
	public TextLineGroup() {
		textLineList = new ArrayList<TextLine>();
	}
	
	public TextLineGroup(List<TextLine> textLineList) {
		this.textLineList = textLineList;
	}

	public Iterator<TextLine> iterator() {
		return textLineList.iterator();
	}
	
	public int size() {
		return textLineList.size();
	}
	
	public void add(TextLine textLine) {
		textLineList.add(textLine);
	}
	
	public TextLine get(int i) {
		return textLineList.get(i);
	}

	public List<TextLineGroup> splitIntoUniqueChunks() {
		IntArray primaryArray = createSerialNumbersOfPrimaryLines();
		List<TextLineGroup> splitArray = new ArrayList<TextLineGroup>();
		if (primaryArray.size() < 2) {
			splitArray.add(this);
		} else {
			Integer lastPrimary = null;
			Integer groupStart = 0;
			Double lastY = null;
			for (int serial = 0; serial < textLineList.size(); serial++) {
				TextLine textLine = this.get(serial);
				Double currentY = textLine.getYCoord();
				if (textLine.isPrimary()) {
					if (lastPrimary != null) {
						int delta = serial - lastPrimary;
						// two adjacent primary lines
						if (delta == 1) {
							packageAsGroup(groupStart, lastPrimary, splitArray);
							groupStart = serial;
						} else if (delta == 2) {
							TextLine midLine = this.textLineList.get(serial - 1);
							Double midY = midLine.getYCoord();
							if (midY == null || lastY == null || currentY == null) {
								LOG.error("null "+midY+" / "+currentY + " / "+lastY);
							} else if (midY - lastY > currentY - midY) {
								packageAsGroup(groupStart, serial - 1, splitArray);
								groupStart = serial;
							} else {
								packageAsGroup(groupStart, lastPrimary, splitArray);
								groupStart = serial - 1;
							}
						} else if (delta == 3) {
							// assume subscript and then superscript
							packageAsGroup(groupStart, serial - 2, splitArray);
							groupStart = serial-1;
						} else {
							reportErrorOrMaths(splitArray);
//							reportProblem("too many lines between Primary "+lastPrimary+" / "+serial);
						}
					} else {
						if (serial >= 2) {
							reportErrorOrMaths(splitArray);
//							reportProblem("too many lines before before Primary " + serial);
						}
						// continue processing
					}
					lastPrimary = serial;
					lastY = textLineList.get(serial).getYCoord();
					// last line of group?
					if (serial == textLineList.size()-1) {
						packageAsGroup(groupStart, serial, splitArray);
					}
				}
			}
		}
		return splitArray;
	}

	private void reportProblem(String msg) {
		throw new RuntimeException(msg);
	}

	private TextLineGroup packageAsGroup(int groupStart, int groupEnd, List<TextLineGroup> splitArray) {
		TextLineGroup group = new TextLineGroup();
		for (int i = groupStart; i <= groupEnd; i++) {
			group.add(textLineList.get(i));
		}
		splitArray.add(group);
		return group;
	}

	private IntArray createSerialNumbersOfPrimaryLines() {
		int iline = 0;
		IntArray primaryArray = new IntArray();
		for (TextLine textLine : textLineList) {
			if (textLine.isPrimary()) {
				primaryArray.addElement(iline);
				if (primaryArray.size() > 1) {
					LOG.trace("PRIMARY "+primaryArray.size());
				}
			}
			iline++;
		}
		return primaryArray;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		for (TextLine textLine : textLineList) {
			sb.append(textLine+"\n");
		}
		sb.append("----\n");
		return sb.toString();
	}
	
	
	public List<TextLine> createSuscriptTextLineList() {
		List<TextLine> outputTextLineList = null;
		TextLine superscript = null;
		TextLine middleLine = null;
		TextLine subscript = null;
		if (this.textLineList.size() == 1) {
			middleLine = textLineList.get(0);
		} else if (this.textLineList.size() == 2) {
			TextLine text0 = textLineList.get(0);
			TextLine text1 = textLineList.get(1);
			if (!text0.isPrimary() && !text1.isPrimary()) {
				Double fontSize0 = text0.getFontSize();
				Double fontSize1 = text1.getFontSize();
				if (fontSize1 == null) {
					superscript = null;
					middleLine = text0;
					subscript = text1;
				} else if(fontSize0 == null) {
					superscript = text0;
					middleLine = text1;
					subscript = null;
			    } else if(fontSize0 > fontSize1) {
					superscript = null;
					middleLine = text0;
					subscript = text1;
				} else {
					superscript = text0;
					middleLine = text1;
					subscript = null;
				}
			} else if (text0.isPrimary() && !text1.isPrimary()) {
					superscript = null;
					middleLine = text0;
					subscript = text1;
			} else if (!text0.isPrimary() && text1.isPrimary()) {
				superscript = textLineList.get(0);
				middleLine = textLineList.get(1);
				subscript = null;
			} else {
				for (TextLine tLine : textLineList) {
					LOG.debug(">>>> "+tLine);
				}
				LOG.error("Only one primary allowed for 2 line textLineGroup");
			}
		} else if (this.textLineList.size() == 3) {
			if (!textLineList.get(0).isPrimary() &&
//				textLineList.get(1).isPrimary() && 
				!textLineList.get(2).isPrimary()) {
				superscript = textLineList.get(0);
				middleLine = textLineList.get(1);
				subscript = textLineList.get(2);
			} else {
				reportErrorOrMathsSuscript();
				middleLine = new TextLine();
				subscript = null;
				superscript = null;
			}
		} else {
			reportErrorOrMathsSuscript();
			middleLine = new TextLine();
			subscript = null;
			superscript = null;
		}
		outputTextLineList = createSuscriptTextLineList(superscript, middleLine, subscript);
		return outputTextLineList;
	}

	private TextLineGroup reportErrorOrMathsSuscript() {
		LOG.debug("Suscript problem: Maths or table? "+textLineList.size());
		TextLineGroup group = new TextLineGroup();
//	    splitArray.add(group);

		for (TextLine textLine : textLineList) {
			LOG.trace("text "+textLine);
		}
		return group;
	}
	
	private TextLineGroup reportErrorOrMaths(List<TextLineGroup> splitArray) {
		LOG.debug("Maths or table? "+textLineList.size());
//		TextLineGroup group = new TextLineGroup();
		TextLineGroup group = null;
	    splitArray.add(group);
	    splitArray.add(null);

		for (TextLine textLine : textLineList) {
			LOG.trace("text "+textLine);
		}
		return group;
	}
	
	/** preparation for HTML
	 * 
	 * @return
	 */
	public static List<TextLine> createSuscriptTextLineList(TextLine superscript, TextLine middleLine, TextLine subscript) {
		List<TextLine> textLineList = new ArrayList<TextLine>();
		if (subscript == null && middleLine == null && superscript == null) {
			textLineList.add(null);
			return textLineList;
		}
		List<SVGText> middleChars = middleLine == null ? null : middleLine.getCharacterList();
		Integer thisIndex = 0;
		List<SVGText> superChars = (superscript == null) ? new ArrayList<SVGText>() : superscript.getCharacterList();
		Integer superIndex = 0;
		List<SVGText> subChars = (subscript == null) ? new ArrayList<SVGText>() : subscript.getCharacterList();
		Integer subIndex = 0;
		TextLine textLine = null;
		while (true) {
			SVGText nextSup = TextLine.peekNext(superChars, superIndex);
			SVGText nextThis = TextLine.peekNext(middleChars, thisIndex);
			SVGText nextSub = TextLine.peekNext(subChars, subIndex);
			SVGText nextText = TextLine.textWithLowestX(nextSup, nextThis, nextSub);
			if (nextText == null) {
				break;
			}
			Suscript suscript = Suscript.NONE;
			if (nextText.equals(nextSup)) {
				superIndex++;
				suscript = Suscript.SUP;
			} else if (nextText.equals(nextThis)) {
				thisIndex++;
				suscript = Suscript.NONE;
			} else if (nextText.equals(nextSub)) {
				subIndex++;
				suscript = Suscript.SUB;
			}
			if (textLine == null || !(suscript.equals(textLine.getSuscript()))) {
				TextAnalyzerX textAnalyzerX = null;
				textLine = new TextLine(textAnalyzerX);
				textLine.setSuscript(suscript);
				textLineList.add(textLine);
			}
			textLine.add(nextText);
		}
		for (TextLine tLine : textLineList) {
			tLine.insertSpaces();
		}
		return textLineList;
	}

	public HtmlElement createHtml() {
		List<TextLine> lineList = this.createSuscriptTextLineList();
		HtmlElement element = TextLine.createHtmlElement(lineList);
		return element;
	}
	
}
