package org.xmlcml.svg2xml.table;

import org.apache.log4j.Logger;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTd;

public class TableCell extends TableChunk {

	private final static Logger LOG = Logger.getLogger(TableCell.class);
	
	public TableCell() {
		super();
	}

	/** default simple value without spaces or subscripts
	 * 
	 * @return
	 */
	public HtmlElement createHtmlElement() {
		HtmlTd td = new HtmlTd();
		HtmlElement cellBody = createHtmlThroughTextStructurer();
		if (cellBody != null) {
			td.appendChild(cellBody);
			cellBody = TableChunk.removeStyles(cellBody);
		}
		return td;
	}
	
	public String toString() {
		return getValue();
	}

}
