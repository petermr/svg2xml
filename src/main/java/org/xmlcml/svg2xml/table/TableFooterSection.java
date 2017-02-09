package org.xmlcml.svg2xml.table;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.util.MultisetUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGTitle;
import org.xmlcml.svg2xml.text.LineChunk;
import org.xmlcml.svg2xml.text.Phrase;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.util.GraphPlot;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** manages the table header, including trying to sort out the column spanning
 * 
 * @author pm286
 *
 */
public class TableFooterSection extends TableSection {
	private static final String FOOTER_TITLE = "footer.title";
	static final Logger LOG = Logger.getLogger(TableFooterSection.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public TableFooterSection() {
		super(TableSectionType.FOOTER);
	}
	
	public TableFooterSection(TableSection tableSection) {
		super(tableSection);
	}
	
	public SVGElement createMarkedContent(
			SVGElement svgChunk,
			String[] colors,
			double[] opacity) {
			SVGG g = createColumnBoxesAndShiftToOrigin(svgChunk, colors, opacity);
			svgChunk.appendChild(g);
			return svgChunk;
	}
	
	private SVGG createColumnBoxesAndShiftToOrigin(SVGElement svgChunk, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setClassName(FOOTER_TITLE);
		if (boundingBox == null) {
			LOG.warn("no bounding box");
		} else {
			String title = FOOTER_TITLE+": "+this.getFontInfo()+" //" +this.getStringValue();
			SVGTitle svgTitle = new SVGTitle("footer: "+title);
			SVGRect plotBox = GraphPlot.plotBox(boundingBox, colors[1], opacity[1]);
			plotBox.appendChild(svgTitle);
			g.appendChild(plotBox);
			TableContentCreator.shiftToOrigin(svgChunk, g);
		}
		return g;
	}

	


}
