package org.xmlcml.svg2xml.page;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.table.TableTable;

import util.Path2ShapeConverter;

/**
 * @author pm286
 *
 */
public class TableAnalyzer /*extends PageChunkAnalyzer */ {
	private static final Logger LOG = Logger.getLogger(TableAnalyzer.class);

	public static final Pattern PATTERN = Pattern.compile("^[Tt][Aa][Bb][Ll]?[Ee]?\\s*\\.?\\s*(\\d+).*", Pattern.DOTALL);

	private TextAnalyzer textAnalyzer;
	private ShapeAnalyzer shapeAnalyzer;

//	private Real2Range pathBox;
	private List<SVGShape> shapeList;
	private List<SVGText> textList;
	
	public TableAnalyzer() {
		super();
	}

	public TableAnalyzer(TextAnalyzer textAnalyzer, ShapeAnalyzer shapeAnalyzer) {
		this.textAnalyzer = textAnalyzer;
		this.shapeAnalyzer = shapeAnalyzer;
	}

	public void analyze() {
		List<SVGShape> shapeList = shapeAnalyzer.getShapeList();
		shapeList = Path2ShapeConverter.removeDuplicateShapes(shapeList);
		List<SVGText> textList = textAnalyzer.getTextCharacters();
//		pathBox = SVGUtil.createBoundingBox(pathList);
//		RealRange pathBoxXRange = pathBox.getXRange();
		Real2Range textBox = SVGUtil.createBoundingBox(textList);
		RealRange textBoxYRange = textBox.getYRange();
		List<Real2Range> boxList = SVGUtil.createNonOverlappingBoundingBoxList(shapeList);
		RealRangeArray verticalBoxes = new RealRangeArray(boxList, RealRange.Direction.VERTICAL);
		verticalBoxes.addTerminatingCaps(textBoxYRange.getMin(), textBoxYRange.getMax());
		RealRangeArray yGaps = verticalBoxes.inverse();
		yGaps.debug();
		
	}
	
	public HtmlTable createTable() {
		shapeList = shapeAnalyzer == null ? null : shapeAnalyzer.getShapeList();
		textList = textAnalyzer == null ? null : textAnalyzer.getTextCharacters();
		TableTable tableTable = new TableTable(shapeList, textList);
		tableTable.analyzeVerticalTextChunks();
		HtmlTable htmlTable = tableTable.createHtmlElement();
		Integer tableNumber = tableTable.getTableNumber();
		if (tableNumber != null) {
			try {
				SVGUtil.debug(htmlTable, new FileOutputStream("target/TABLEX"+tableNumber+".html"), 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot output file", e);
			}
		}
		return htmlTable;
	}

}
