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
import org.xmlcml.svg2xml.util.GraphPlot;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** manages the table header, including trying to sort out the column spanning
 * 
 * @author pm286
 *
 */
public class TableFooterSection extends TableSection {
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
		if (boundingBox == null) {
			LOG.warn("no bounding box");
		} else {
			String title = this.getFontInfo()+" //" +this.getStringValue();
			SVGTitle svgTitle = new SVGTitle(title);
			SVGRect plotBox = GraphPlot.plotBox(boundingBox, colors[1], opacity[1]);
			plotBox.appendChild(svgTitle);
			g.appendChild(plotBox);
			TableContentCreator.shiftToOrigin(svgChunk, g);
		}
		return g;
	}

//	private String getFontInfo() {
//		List<Phrase> phrases = this.getOrCreatePhrases();
//		Multiset<Double> fontSizeSet = HashMultiset.create();
//		Multiset<String> fontFamilySet = HashMultiset.create();
//		Multiset<String> fontWeightSet = HashMultiset.create();
//		Multiset<String> fontStyleSet = HashMultiset.create();
//		for (LineChunk phrase : phrases) {
//			fontSizeSet.add(phrase.getFontSize());
//			fontFamilySet.add(String.valueOf(phrase.getFontFamily()));
//			fontWeightSet.add(String.valueOf(phrase.getFontWeight()));
//			fontStyleSet.add(String.valueOf(phrase.getFontStyle()));
//		}
//		StringBuilder sb = new StringBuilder();
//		sb.append("{"+MultisetUtil.getEntriesSortedByCount(fontFamilySet).toString()+"}");
//		sb.append("{"+MultisetUtil.getDoubleEntriesSortedByCount(fontSizeSet).toString()+"}");
//		sb.append("{"+MultisetUtil.getEntriesSortedByCount(fontWeightSet).toString()+"}");
//		sb.append("{"+MultisetUtil.getEntriesSortedByCount(fontStyleSet).toString()+"}");
//		LOG.debug(sb.toString());
//		return sb.toString();
//	}
	


}
