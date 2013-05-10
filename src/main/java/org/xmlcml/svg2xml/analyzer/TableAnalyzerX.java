package org.xmlcml.svg2xml.analyzer;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.table.TableTable;
import org.xmlcml.svg2xml.text.TextLineContainer;

/**
 * @author pm286
 *
 */
public class TableAnalyzerX extends AbstractPageAnalyzerX {
	private static final Logger LOG = Logger.getLogger(TableAnalyzerX.class);
	
	public static final Pattern PATTERN = Pattern.compile("^[Tt][Aa][Bb][Ll]?[Ee]?\\s*\\.?\\s*(\\d+).*", Pattern.DOTALL);
	public static final String TITLE = "TABLE";

	private TextAnalyzerX textAnalyzer;
	private PathAnalyzerX pathAnalyzer;
	private TextLineContainer textLineContainer;

	private Real2Range pathBox;
	private Real2Range textBox;

	private List<SVGPath> pathList;

	private List<SVGText> textList;
	
	public TableAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
	}

	public TableAnalyzerX(PDFIndex pdfIndex) {
		super(pdfIndex);
	}
	
	public TableAnalyzerX(TextAnalyzerX textAnalyzer, PathAnalyzerX pathAnalyzer) {
		this.textAnalyzer = textAnalyzer;
		this.pathAnalyzer = pathAnalyzer;
		this.textLineContainer = textAnalyzer.getTextLineContainer();
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
		
//		TableTable tableTable = new TableTable();
//		for (RealRange yGap : yGaps) {
//			GenericChunk tableChunk = new GenericChunk();
//			tableTable.add(tableChunk);
//			tableChunk.populateChunk(textList, pathBoxXRange, yGap);
//			System.out.println("HHHHHHHHHHHHHHHHhh"+tableChunk.getHorizontalMask());
//		}
//		tableTable.analyze();
	}
	
	public HtmlElement analyze1() {
		pathList = pathAnalyzer == null ? null : pathAnalyzer.getPathList();
		textList = textAnalyzer == null ? null : textAnalyzer.getTextCharacters();
		TableTable tableTable = new TableTable(pathList, textList);
		tableTable.analyzeVerticalTextChunks();
		HtmlElement htmlElement = tableTable.getHtml();
		return htmlElement;
	}

	@Override
	public SVGG labelChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
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
