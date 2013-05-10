package org.xmlcml.svg2xml.table;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlElement;

public class TableCaption extends GenericChunk {

	private final static Logger LOG = Logger.getLogger(TableCaption.class);
	
	public TableCaption(List<? extends SVGElement> elementList) {
		super(elementList);
	}

	public TableCaption(GenericChunk chunk) {		
		this(chunk.getElementList());
	}

	/** default simple value without spaces or subscripts
	 * 
	 * @return
	 */
	public HtmlElement getHtml() {
		HtmlCaption caption = new HtmlCaption();
		caption.appendChild(getValue());
		return caption;
	}
	
	public String toString() {
		return getValue();
	}

}
