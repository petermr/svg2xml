package org.xmlcml.svg2xml.table;

import java.util.List;
import java.util.regex.Matcher;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.html.HtmlB;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.page.TableAnalyzer;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

public class TableCaption extends TableChunk {

	private final static Logger LOG = Logger.getLogger(TableCaption.class);
	private HtmlCaption caption;
	
	public TableCaption(List<? extends SVGElement> elementList) {
		super(elementList);
	}

	public TableCaption(TableChunk chunk) {		
		this(chunk.getElementList());
	}

	public static void addCaptionTo(HtmlTable table, HtmlCaption caption) {
		Nodes captions = table.query("*[local-name()='caption']");
		HtmlP p = new HtmlP();
		if (captions.size() == 0) {
			HtmlB b = new HtmlB();
			CMLUtil.transferChildren(caption,  b);
			p.appendChild(b);
			caption.appendChild(p);
			table.insertChild(caption, 1);  // because <head> is first
		} else {
			((Element)captions.get(0)).appendChild(p);
			CMLUtil.transferChildren(caption,  p);
		}
	}


	public static Integer getNumber(HtmlCaption caption) {
		Integer number = null;
		if (caption != null) {
			String value = caption.getValue();
			Matcher matcher = TableAnalyzer.PATTERN.matcher(value);
			if (matcher.matches()) {
				String tableId = matcher.group(1);
				number = new Integer(tableId);
			}
		}
		return number;
	}

	/** default simple value without spaces or subscripts
	 * 
	 * @return
	 */
	public HtmlElement createHtmlElement() {
		caption = new HtmlCaption();
		HtmlElement captionBody = createHtmlThroughTextStructurer();
		if (captionBody == null) {
			throw new RuntimeException("Null caption");
		}
		captionBody = SVG2XMLUtil.removeStyles(captionBody);
		caption.appendChild(captionBody);
		return caption;
	}
	
	public String toString() {
		return getValue();
	}

}
