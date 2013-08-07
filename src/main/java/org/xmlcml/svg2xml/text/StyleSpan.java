package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlB;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlI;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.html.HtmlSub;
import org.xmlcml.html.HtmlSup;

public class StyleSpan {

	private boolean bold;
	private boolean italic;
	private List<SVGText> characterList;

	public StyleSpan() {
		
	}

	public StyleSpan(boolean bold, boolean italic) {
		this.bold = bold;
		this.italic = italic;
	}

	public void addCharacter(SVGText character) {
		ensureCharacterList();
		characterList.add(character);
	}

	private void ensureCharacterList() {
		if (characterList == null) {
			characterList = new ArrayList<SVGText>();
		}
	}
	
	public boolean isBold() {return bold;}
	public boolean isItalic() {return italic;}
	
	public String getTextContentWithStyleAndSpaces() {
		StringBuilder sb = new StringBuilder();
		if (bold) {sb.append("<B>");}
		if (italic) {sb.append("<I>");}
		sb.append(getTextContentWithSpaces());
		if (italic) {sb.append("</I>");}
		if (bold) {sb.append("</B>");}
		return sb.toString();
	}

	public HtmlElement getHtmlElement() {
		HtmlElement htmlElement = new HtmlSpan();
		HtmlElement currentHtml = htmlElement;
		SVGText character = (characterList.size() == 0) ? null : characterList.get(0);
		String suscript = (character == null) ? null : SVGUtil.getSVGXAttribute(character, ScriptLine.SUSCRIPT); 
		boolean sub = ScriptLine.SUB.equals(suscript);
		boolean sup = ScriptLine.SUP.equals(suscript);
		if (sub) {
			HtmlElement subElement = new HtmlSub();
			currentHtml.appendChild(subElement);
			currentHtml = subElement;
		}
		if (sup) {
			HtmlElement supElement = new HtmlSup();
			currentHtml.appendChild(supElement);
			currentHtml = supElement;
		}
		if (bold) {
			HtmlElement bold = new HtmlB();
			currentHtml.appendChild(bold);
			currentHtml = bold;
		}
		if (italic) {
			HtmlElement italic = new HtmlI();
			currentHtml.appendChild(italic);
			currentHtml = italic;
		}
		StringBuilder sb = new StringBuilder();
		for (SVGText charact : characterList) {
			sb.append(charact.getText());
		}
		currentHtml.appendChild(sb.toString());
		return htmlElement;
	}

	
	public static StyleSpan createSpace() {
		StyleSpan styleSpan = new StyleSpan();
		SVGText text = new SVGText();
		text.setText(" ");
		styleSpan.addCharacter(text);
		return styleSpan;
	}

	public String getTextContentWithSpaces() {
		StringBuilder sb = new StringBuilder();
		SVGText lastText = null;
		for (SVGText text : characterList) {
			String sp = StyleSpan.computeInterveningSpaces(lastText, text);
			if (sp != null && !sp.equals("")) {
				sb.append(sp);
			}
			sb.append(text.getText());
			lastText = text;
		}
		return sb.toString();
	}

	/** add spaces corresponding to distance between last text and text
	 * 
	 * uses fontSize and width of last Text
	 * nspaces = (text.getX()-lastText.getBoundingBox().getXRange().getMax()) / lastText.getFontSize()*lastText.getFontWidth()
	 * nspaces == null => no action else returns string with computed spaces
	 * 
	 * @param lastText if null no action
	 * @param text if null no action
	 * @return null if no spaces else computed number of spaces
	 */
	public static String computeInterveningSpaces(SVGText lastText, SVGText text) {
		String spaces = null;
		if (lastText != null && text != null) {
			 Double x0 = lastText.getBoundingBox().getXRange().getMax();
			 Double x1 = text.getX();
			 if (x0 != null && x1 != null) {
				 double deltax = x1 - x0;
				 Double fontSize = lastText.getFontSize();
				 Double fontWidth = lastText.getSVGXFontWidth();
				 if (fontSize != null && fontWidth != null) {
					 Double spaceWidth = fontSize * fontWidth;
					 double sp = deltax / spaceWidth;
					 int nspaces = (int) sp;
					 if (nspaces > 0) {
						 StringBuilder sb = new StringBuilder();
						 for (int i = 0; i < nspaces; i++) {
							 sb.append(" ");
						 }
						 spaces = sb.toString();
					 }
				 }
			 }
		}
		return spaces;
	}
	
	@Override
	public String toString() {
		return getTextContentWithStyleAndSpaces();
	}
	
}
