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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (bold) {sb.append("<B>");}
		if (italic) {sb.append("<I>");}
		for (SVGText character : characterList) {
			sb.append(character.getText());
		}
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
}
