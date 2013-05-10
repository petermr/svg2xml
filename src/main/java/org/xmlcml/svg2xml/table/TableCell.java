package org.xmlcml.svg2xml.table;

import org.apache.log4j.Logger;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTd;

public class TableCell extends GenericChunk {

	private final static Logger LOG = Logger.getLogger(TableCell.class);
	
	public TableCell() {
		super();
	}

	/** default simple value without spaces or subscripts
	 * 
	 * @return
	 */
	public HtmlElement getHtml() {
		HtmlTd td = new HtmlTd();
		td.appendChild(getValue());
		return td;
	}
	
	public String toString() {
		return getValue();
	}

}
