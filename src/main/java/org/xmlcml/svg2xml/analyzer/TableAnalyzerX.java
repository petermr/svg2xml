package org.xmlcml.svg2xml.analyzer;

import java.util.ArrayList;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.action.SemanticDocumentActionX;
import org.xmlcml.svgplus.table.Table;

/**
 * @author pm286
 *
 */
public class TableAnalyzerX extends AbstractPageAnalyzerX {
	private static final Logger LOG = Logger.getLogger(TableAnalyzerX.class);
	
	private List<Table> tableList;

	public TableAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}
	
	public void analyze() {
	}

	public List<Table> findTables() {
		tableList = new ArrayList<Table>();
		// NYI
//		caption = (Caption) Chunk.createFromAndReplace((SVGG)captions.get(0), new Caption(pgeAnalyzer, "bar"));
//
//		List<SVGElement> tables = SVGUtil.getQuerySVGElements(
//				svgPage, "//svg:g[svg:g/svg:g[@name='para']/svg:text[starts-with(., 'Table ')]]");
//		for (SVGElement elem : tables) {
//			createCaptionAndReplace(elem, "svg:g/svg:g[@name='para' and svg:text[starts-with(., 'Table ')]]", Table.TABLE);
//			Table table = Table.createFromAndReplace((SVGG) elem);
//			table.removeOriginalText();
//			tableList.add(table);
//		}
		return tableList;
	}
	
	
}
