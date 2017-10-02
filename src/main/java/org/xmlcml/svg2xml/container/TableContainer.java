package org.xmlcml.svg2xml.container;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.html.HtmlCaption;
import org.xmlcml.graphics.html.HtmlElement;
import org.xmlcml.graphics.html.HtmlTable;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.page.PageAnalyzer;

/** not sure whether this should be built during page creation
 * 
 * @author pm286
 *
 */
public class TableContainer extends AbstractContainerOLD {

	
	public final static Logger LOG = Logger.getLogger(TableContainer.class);

	public TableContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}

	@Override
	public HtmlElement createHtmlElement() {
		htmlElement = new HtmlTable();
		HtmlCaption caption = new HtmlCaption();
		caption.appendChild("Table NYI");
		htmlElement.appendChild(caption);
		return htmlElement;
	}

	
	@Override
	public SVGG createSVGGChunk() {
		SVGG g = new SVGG();
		g.setTitle("Table NYI");
		return g;
	}

}
