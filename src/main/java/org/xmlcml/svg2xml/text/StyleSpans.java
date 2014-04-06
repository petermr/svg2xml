package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

/** holds styleSpans within a ScriptLine or possibly later within a ScriptContainer
 * 
 * @author pm286
 *
 */
public class StyleSpans {

	private final static Logger LOG = Logger.getLogger(StyleSpans.class);
	public static final double EPS = 0.01;
	
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
		if (ssList != null) {
			for (StyleSpan styleSpan : ssList) {
				sb.append(styleSpan.getTextContentWithSpaces());
			}
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
	
	public HtmlElement createHtmlElement() {
		HtmlElement htmlElement = new HtmlSpan();
		for (StyleSpan styleSpan : ssList) {
			HtmlElement spanElement = styleSpan.createHtmlElement();
			SVG2XMLUtil.moveChildrenFromTo(spanElement, htmlElement);
		}
		return htmlElement;
	}

	public Double getFontSize() {
		Double fontSize = null;
		for (StyleSpan styleSpan : ssList) {
			Double fontSize0 = styleSpan.getFontSize();
			if (fontSize == null) {
				fontSize = fontSize0;
			} else if (!Real.isEqual(fontSize, fontSize0, EPS)) {
				//may be subscripts
				LOG.trace("fontsize changed in spans: "+fontSize+" => "+fontSize0);
			}
		}
		return fontSize;
	}
	

}
