package org.xmlcml.svgplus.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.core.AbstractAnalyzer;
import org.xmlcml.svgplus.core.PageAnalyzer;

/**
 * @author pm286
 *
 */
public class TableAnalyzer extends AbstractAnalyzer {
	private static final Logger LOG = Logger.getLogger(TableAnalyzer.class);
	
	private List<Table> tableList;

	public TableAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public void analyze() {
	}

	public List<Table> findTables() {
		tableList = new ArrayList<Table>();
		// NYI
//		caption = (Caption) Chunk.createFromAndReplace((SVGG)captions.get(0), new Caption(pageAnalyzer, "bar"));
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
