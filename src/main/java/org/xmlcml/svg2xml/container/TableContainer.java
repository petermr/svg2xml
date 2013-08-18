package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;

/** not sure whether this should be built during page creation
 * 
 * @author pm286
 *
 */
public class TableContainer extends AbstractContainer {

	
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
