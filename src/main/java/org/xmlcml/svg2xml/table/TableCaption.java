package org.xmlcml.svg2xml.table;

import java.util.List;

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

public class TableCaption extends GenericChunk {

	private final static Logger LOG = Logger.getLogger(TableCaption.class);
	private HtmlCaption caption;
	
	public TableCaption(List<? extends SVGElement> elementList) {
		super(elementList);
	}

	public TableCaption(GenericChunk chunk) {		
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


	/** default simple value without spaces or subscripts
	 * 
	 * @return
	 */
	public HtmlElement createHtmlTable() {
		caption = new HtmlCaption();
		HtmlElement captionBody = createHtmlThroughTextContainer();
		captionBody = GenericChunk.removeStyles(captionBody);
		caption.appendChild(captionBody);
		return caption;
	}
	
	public String toString() {
		return getValue();
	}

}
