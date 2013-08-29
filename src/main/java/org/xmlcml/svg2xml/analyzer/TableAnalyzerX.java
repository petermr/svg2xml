package org.xmlcml.svg2xml.analyzer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.table.TableTable;

/**
 * @author pm286
 *
 */
public class TableAnalyzerX extends AbstractAnalyzer {
	private static final Logger LOG = Logger.getLogger(TableAnalyzerX.class);
	
	public static final Pattern PATTERN = Pattern.compile("^[Tt][Aa][Bb][Ll]?[Ee]?\\s*\\.?\\s*(\\d+).*", Pattern.DOTALL);
	public static final String TITLE = "TABLE";

	private TextAnalyzerX textAnalyzer;
	private PathAnalyzerX pathAnalyzer;

	private Real2Range pathBox;
//	private Real2Range textBox;

	private List<SVGPath> pathList;

	private List<SVGText> textList;
	
	public TableAnalyzerX() {
		super();
	}

	public TableAnalyzerX(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	public TableAnalyzerX(TextAnalyzerX textAnalyzer, PathAnalyzerX pathAnalyzer) {
		this.textAnalyzer = textAnalyzer;
		this.pathAnalyzer = pathAnalyzer;
	}

	public void analyze() {
		pathAnalyzer.forceRemoveDuplicatePaths();
		List<SVGPath> pathList = pathAnalyzer.getPathList();
		List<SVGText> textList = textAnalyzer.getTextCharacters();
		pathBox = SVGUtil.createBoundingBox(pathList);
		RealRange pathBoxXRange = pathBox.getXRange();
		Real2Range textBox = SVGUtil.createBoundingBox(textList);
		RealRange textBoxYRange = textBox.getYRange();
		List<Real2Range> boxList = SVGUtil.createNonOverlappingBoundingBoxList(pathList);
		RealRangeArray verticalBoxes = new RealRangeArray(boxList, RealRange.Direction.VERTICAL);
		verticalBoxes.addTerminatingCaps(textBoxYRange.getMin(), textBoxYRange.getMax());
		RealRangeArray yGaps = verticalBoxes.inverse();
		yGaps.debug();
		
	}
	
	public HtmlTable createTable() {
		pathList = pathAnalyzer == null ? null : pathAnalyzer.getPathList();
		textList = textAnalyzer == null ? null : textAnalyzer.getTextCharacters();
		TableTable tableTable = new TableTable(pathList, textList);
		tableTable.analyzeVerticalTextChunks();
		HtmlTable htmlTable = tableTable.createHtmlElement();
		Integer tableNumber = tableTable.getTableNumber();
		if (tableNumber != null) {
			try {
				CMLUtil.debug(htmlTable, new FileOutputStream("target/TABLEX"+tableNumber+".html"), 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot output file", e);
			}
		}
		return htmlTable;
	}

//	@Override
//	public SVGG oldAnnotateChunk() {
//		throw new RuntimeException("annotate NYI");
//	}
	
	public Integer indexAndLabelChunk(String content, ChunkId id) {
		Integer serial = super.indexAndLabelChunk(content, id);
		// index...
		return serial;
	}
	
	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

}
