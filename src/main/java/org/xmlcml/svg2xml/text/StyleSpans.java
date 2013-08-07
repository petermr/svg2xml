package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

/** holds styleSpans within a ScriptLine or possibly later within a ScriptContainer
 * 
 * @author pm286
 *
 */
public class StyleSpans {

	private List<StyleSpan> ssList;

	public StyleSpans() {
	}
	
	public void add(StyleSpan styleSpan) {
		ensureSSList();
		ssList.add(styleSpan);
	}

	private void ensureSSList() {
		if (this.ssList == null) {
			ssList = new ArrayList<StyleSpan>();
		}
	}
	
	public List<StyleSpan> getStyleSpanList() {
		return ssList;
	}

	public String getTextContentWithSpaces() {
		StringBuilder sb = new StringBuilder();
		for (StyleSpan styleSpan : ssList) {
			sb.append(styleSpan.getTextContentWithSpaces());
		}
		return sb.toString();
	}

	public int size() {
		ensureSSList();
		return ssList.size();
	}

	public StyleSpan get(int j) {
		ensureSSList();
		return ssList.get(j);
	}
	
	public void addStyleSpans(StyleSpans styleSpans, boolean insertSpaces) {
		ensureSSList();
		if (ssList.size() > 0 && insertSpaces) {
			ssList.add(StyleSpan.createSpace());
		}
		ssList.addAll(styleSpans.getStyleSpanList());
	}

}
