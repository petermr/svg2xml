package org.xmlcml.svg2xml.table;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.IntRangeArray;
import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.linestuff.Path2ShapeConverter;
import org.xmlcml.html.HtmlBr;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlHead;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlStyle;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTbody;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.html.HtmlTfoot;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.html.HtmlThead;
import org.xmlcml.html.HtmlTr;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.LineChunk;
import org.xmlcml.svg2xml.text.Phrase;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.Ruler;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.text.VerticalRulerNew;

import nu.xom.Attribute;
import nu.xom.Node;

public class TableStructurer {
	private static final Logger LOG = Logger.getLogger(TableStructurer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String EMPTY_CHILD = "";
	private static final String WIDE = "w";
	private static final String LONG = "b";
	private static final String FONT = "f";
	private static final Double LINE_MAX_THICK = 10.0; // thick line
	// the fontSize-independent with of an indented space 
	public static final double SPACE_WIDTH = 1.0;
	public static final String LEADING_SPACE = "~";
	

	private PhraseListList phraseListList;
	private int maxColumns;
	private ArrayList<ColumnManager> columnManagerList;
	private String title;
	private HtmlTable htmlTable;
	private HtmlHtml html;
	private IntRange titleRange;
	private IntRange preHeadRange;
	private IntRange headerRange;
	private IntRange bodyRange;
	private IntRange footerRange;
	private HtmlTbody tableBody;
	private TextStructurer textStructurer;
	private List<HorizontalRuler> horizontalRulerList;
	private List<SVGElement> horizontalElementList;
	private List<VerticalRulerNew> verticalRulerListNew;
	private Real2Range bboxRuler;
	private Map<String, SVGElement> horizontalElementByCode;
	private String rowCodes;
	private double minRulerSpacingY = 5.; // below this counts as wide line
	private List<TableSection> tableSectionList;
	private TableTitle tableTitle;

	
	public TableStructurer(PhraseListList phraseListList) {
		this.phraseListList = phraseListList;
		maxColumns = phraseListList.getMaxColumns();
	}

	public void setRanges(IntRange[] ranges) {
		setRanges(
				ranges[0],
				ranges[1],
				ranges[2],
				ranges[3],
				ranges[4]
				);
	}
	public void setRanges(
			IntRange titleRange,
			IntRange preHeadRange,
			IntRange headerRange,
			IntRange bodyRange,
			IntRange footerRange
			) {
		setTitleRange(titleRange);
		setPreHeadRange(preHeadRange);
		setHeaderRange(headerRange);
		setBodyRange(bodyRange);
		setFooterRange(footerRange);
	}

	public void setTitleRange(IntRange range) {titleRange = range;}
	public void setPreHeadRange(IntRange range) {preHeadRange = range;}
	public void setHeaderRange(IntRange range) {headerRange = range;}
	public IntRange setBodyRange(IntRange range) {return bodyRange = range;}
	public void setFooterRange(IntRange range) {footerRange = range;}

	public String createTitle() {
		StringBuilder titleSB = new StringBuilder();
		if (tableTitle != null) {
			titleSB.append(tableTitle.getTitle()+" ");
		}
		if (titleRange != null) {
			for (int i = titleRange.getMin(); i < titleRange.getMax(); i++) {
				titleSB.append(phraseListList.get(i));
			}
		} else if (tableSectionList != null) {
//			TableSection titleSection =tableSectionList.get(TableSection.TableSectionType.TITLE);
			TableSection titleSection =tableSectionList.get(0);
			List<HorizontalElement> horizontalList = titleSection.getHorizontalElementList();
			titleSB.append(tableTitle.getTitle());
			for (int i = 0; i < horizontalList.size(); i++) {
				titleSB.append((PhraseList)horizontalList.get(i));
			}
		}
		title = titleSB.toString();
		return title;
	}

	private void ensureColumnManagers() {
		if (columnManagerList == null) {
			this.columnManagerList = new ArrayList<ColumnManager>();
			for (int i = 0; i < maxColumns; i++) {
				ColumnManager columnManager = new ColumnManager();
				columnManagerList.add(columnManager);
			}
		}
	}
	
	private ColumnManager getColumnManager(int iCol) {
		ensureColumnManagers();
		return columnManagerList.get(iCol);
	}

	public HtmlTable getHtmlTable() {
		return htmlTable;
	}

	public HtmlHtml getHtmlWithTable() {
		html = new HtmlHtml();
		createHtmlHead();
		createHtmlTable();
		html.appendChild(htmlTable);
		html.appendChild(createFirefoxWarning());		
		return html;
	}

	private void createHtmlHead() {
		HtmlHead head = new HtmlHead();
		html.appendChild(head);
		addStyle(head);
	}

	private void addStyle(HtmlHead head) {
		HtmlStyle style = new HtmlStyle();
		style.addCss("table {border : solid 1pt;}");
		style.addCss("caption {background : #bbffff;}");
		style.addCss("th {background : #ffdddd; border : solid blue 2pt;}");
		style.addCss("tr {border : solid 1pt;}");
		style.addCss("td {border : solid 1pt;}");
		style.addCss("tfoot {border : solid 2pt; background : #ffddff;}");
		style.addCss(".firefox {font-size : 6pt; font-style : italic;}");
		head.appendChild(style);
	}

	public HtmlTable createHtmlTable() {
		htmlTable = new HtmlTable();
		addTitle();
		addHead();
		addFooter();
		createBody();
		html.appendChild(createFirefoxWarning());
		
		return htmlTable;
	}

	private HtmlDiv createFirefoxWarning() {
		HtmlDiv div = new HtmlDiv();
		HtmlP p = new HtmlP("Note: If strange characters appear in Firefox, configure it for UTF-8 or use another browser");
		div.appendChild(p);
		div.addAttribute(new Attribute("class", "firefox"));
		return div;
	}

	private void createBody() {
		tableBody = new HtmlTbody();
		addPreHeader(tableBody);
		addAndCompressHeader(tableBody);
		addBody(tableBody);
	}

	private void addPreHeader(HtmlTbody tableBody) {
		if (preHeadRange != null) {
			List<HtmlTr> rows0 = this.createBodyTableRows(preHeadRange.getMin(), preHeadRange.getMax(), HtmlTh.class);
			for (HtmlTr row : rows0) {
				tableBody.appendChild(row);
			}
		}
	}

	private void addAndCompressHeader(HtmlTbody tableBody) {
		List<HtmlTr> rows1 = null;
		if (tableSectionList != null) {
			TableSection bodySection = tableSectionList.get(1);
			PhraseListList phraseListList= new PhraseListList();
			for (HorizontalElement element : bodySection.getHorizontalElementList()) {
				if (element instanceof PhraseList) {
					phraseListList.add((PhraseList) element);
				} else {
					LOG.warn("HORIZONTAL RULER NOT CODED");
					phraseListList.add(new PhraseList(PhraseList.NULL));
				}
			}
			maxColumns = phraseListList.getMaxColumns();
			rows1 = createBodyTableRows(0, phraseListList.size() - 1, HtmlTd.class);
		} else if (headerRange != null) {
			rows1 = createBodyTableRows( 
					headerRange.getMin(), headerRange.getMax(), HtmlTh.class);
			// project cells into StringBuilder array
		}
		List<StringBuilder> sbList = new ArrayList<StringBuilder>();
		for (int i = 0; i < maxColumns; i++) {
			sbList.add(new StringBuilder());
		}
		if (rows1 == null) {
			LOG.error("no rows");
		} else {
			for (HtmlTr rowt : rows1) {
				for (int i = 0; i < maxColumns; i++) {
					Node child = (i >= rowt.getChildCount()) ? null : rowt.getChild(i);
					String value = (child == null) ? EMPTY_CHILD : child.getValue();
					sbList.get(i).append(value == null ? "" : value);
				}
			}
		}
		HtmlTr rowx = new HtmlTr();
		for (int i = 0; i < maxColumns; i++) {
			HtmlTh th = new HtmlTh();
			th.appendChild(sbList.get(i).toString());
			rowx.appendChild(th);
		}
		tableBody.appendChild(rowx);
	}

	private void addBody(HtmlTbody tableBody) {
		List<HtmlTr> rows = null;
		if (bodyRange != null) {
			rows = createBodyTableRows(bodyRange.getMin(), bodyRange.getMax(), HtmlTd.class);
		} else {
			if (tableSectionList == null) {
				LOG.error("no body");
			} else {
				if (tableSectionList == null || tableSectionList.size() < 3) {
					LOG.error("ERROR: no Body section");
				} else {
					TableSection bodySection = tableSectionList.get(2);
					PhraseListList phraseListList= new PhraseListList();
					for (HorizontalElement element : bodySection.getHorizontalElementList()) {
						if (element instanceof PhraseList) {
							phraseListList.add((PhraseList) element);
						} else {
							LOG.error("Omitted ruler: "+element);
						}
					}
				}
			}
			rows = createBodyTableRows(0, phraseListList.size() - 1, HtmlTd.class);
		}
		for (HtmlTr row : rows) {
			tableBody.appendChild(row);
		}
		htmlTable.appendChild(tableBody);

	}

	private void addHead() {
		HtmlThead thead = new HtmlThead();
		htmlTable.appendChild(thead);
	}

	private void addTitle() {
		createTitle();
		HtmlCaption caption = new HtmlCaption();
		caption.appendChild(title);
		htmlTable.appendChild(caption);
	}

	private void addFooter() {
		HtmlTfoot foot = new HtmlTfoot();
		htmlTable.appendChild(foot);
		
		HtmlTr footRow = new HtmlTr();
		foot.appendChild(footRow);
		HtmlTd footTd = new HtmlTd();
		footRow.appendChild(footTd);
		footTd.addAttribute(new Attribute("colspan", String.valueOf(maxColumns)));
		if (footerRange != null) {
			for (int i = footerRange.getMin(); i < footerRange.getMax(); i++) {
				PhraseList phraseList = phraseListList.get(i);
				footTd.appendChild(phraseList.toString());
				footTd.appendChild(new HtmlBr());
			}
		} else {
			if (tableSectionList != null && tableSectionList.size() > 3) {
				TableSection footerSection =tableSectionList.get(3);
				for (HorizontalElement element : footerSection.getHorizontalElementList()) {
					if (element instanceof HorizontalRuler) {
						LOG.debug("HRULER in footer");
					} else {
						PhraseList phraseList = (PhraseList) element;
						footTd.appendChild(phraseList.toString());
						footTd.appendChild(new HtmlBr());
					}
				}
			}
		}
	}
	
	public void createRulerList() {
		createHorizontalRectsList();
		createHorizontalRulerList();
		createVerticalRulerList();
	}
	
	public List<SVGRect> createHorizontalRectsList() {
		List<SVGShape> shapeList = makeShapes();
		
		List<SVGRect> rectList = extractRects(shapeList);
		
		return rectList;
	}

	public List<HorizontalRuler> createVerticalRulerList() {
		List<SVGShape> shapeList = makeShapes();
		
		List<SVGLine> lineList = extractLines(shapeList, Line2.YAXIS);
		lineList = removeShortLines(lineList, 1.0);
		verticalRulerListNew = VerticalRulerNew.createFromSVGList(lineList);
		verticalRulerListNew.sort(new Comparator<Ruler>() {
			public int compare(Ruler r1, Ruler r2) {
				Double x1 = r1 == null ? null :  r1.getX();
				Double x2 = r2 == null ? null :  r2.getX();
				return (x1 == null || x2 == null) ? -1 : (int)(x1 - x2); // compare X coords
			}
		});
		Ruler.formatStrokeWidth(horizontalRulerList, 1);
		return horizontalRulerList;
	}

	public List<HorizontalRuler> createHorizontalRulerList() {
		List<SVGShape> shapeList = makeShapes();
		
		List<SVGLine> lineList = extractLines(shapeList, Line2.XAXIS);
		LOG.trace("lines: "+lineList.size());
		lineList = removeShortLines(lineList, 1.0);
		horizontalRulerList = HorizontalRuler.createFromSVGList(lineList);
		horizontalRulerList.sort(new Comparator<Ruler>() {
			public int compare(Ruler r1, Ruler r2) {
				Double y1 = r1 == null ? null :  r1.getY();
				Double y2 = r2 == null ? null :  r2.getY();
				return (y1 == null || y2 == null) ? -1 : (int)(y1 - y2); // compare Y coords
			}
		});
		Ruler.formatStrokeWidth(horizontalRulerList, 1);
		return horizontalRulerList;
	}

	private List<SVGShape> makeShapes() {
		SVGElement svgChunk = textStructurer.getSVGChunk();
		List<SVGPath> pathList = SVGPath.extractSelfAndDescendantPaths(svgChunk);
		LOG.trace("paths "+pathList.size());
		Path2ShapeConverter converter = new Path2ShapeConverter(pathList);
		List<SVGShape> shapeList = converter.convertPathsToShapes(pathList);
		return shapeList;
	}
	
	public static List<SVGRect> extractRects(List<SVGShape> shapeList) {
		List<SVGRect> rectList = new ArrayList<SVGRect>();
		for (SVGShape shape : shapeList) {
			if (shape instanceof SVGRect) {
				SVGRect rect = (SVGRect) shape;
				rectList.add(rect);
			}
		}
		return rectList;
	}
	
	public static List<SVGLine> extractLines(List<SVGShape> shapeList, Line2 axis) {
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		for (SVGShape shape : shapeList) {
			if (shape instanceof SVGLine) {
				SVGLine line = (SVGLine) shape;
				addAxiallyAlignedLineToList(axis, lineList, line);
			} else if (shape instanceof SVGPolyline) {
				SVGPolyline polyline = (SVGPolyline) shape;
				List<SVGLine> lineList1 = polyline.createLineList();
				for (SVGLine line1 : lineList1) {
					// we have a bug in zigzag polylines MLMLML and this is a temporary fix
					addAxiallyAlignedLineToList(axis, lineList, line1);
				}
			}
		}
		return lineList;
	}

	public static List<SVGRect> extractRects(List<SVGShape> shapeList, Line2 axis) {
		List<SVGRect> rectList = new ArrayList<SVGRect>();
		for (SVGShape shape : shapeList) {
			if (shape instanceof SVGRect) {
				SVGRect rect = (SVGRect) shape;
				rectList.add(rect);
			}
		}
		return rectList;
	}

	private static void addAxiallyAlignedLineToList(Line2 axis, List<SVGLine> lineList, SVGLine line) {
		if (axis == null || 
				(line.isHorizontal(SVGLine.EPS) && axis.equals(Line2.XAXIS)) ||
				(line.isVertical(SVGLine.EPS) && axis.equals(Line2.YAXIS))) {
			lineList.add(line);
		}
	}

	public static List<SVGLine> removeShortLines(List<SVGLine> lineList, double length) {
		List<SVGLine> newLines = new ArrayList<SVGLine>();
		for (SVGLine line : lineList) {
			Double rLength = line.getLength();
			if (rLength != null && rLength > length) {
				newLines.add(line);
			}
		}
		return newLines;
	}

	public void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	public List<HorizontalRuler> getHorizontalRulerList() {
		return getHorizontalRulerList(false, 0.0);
	}

	/**
	 * assumes lines sorted in increasing Y
	 * 
	 * @param merge touching lines at same y-value
	 * @param create tramline from 
	 * 
	 * @return
	 */
	public List<HorizontalRuler> getHorizontalRulerList(boolean merge, double eps) {
		if (horizontalRulerList != null && merge) {
			List<HorizontalRuler> newRulerList = new ArrayList<HorizontalRuler>();
			for (int i = 0; i < horizontalRulerList.size(); i++) {
				HorizontalRuler horizontalRuler = horizontalRulerList.get(i);
				if (horizontalRuler.getSVGLine() != null) {
					LOG.trace(horizontalRuler.toXML());
					addRuler(newRulerList, horizontalRuler);
				} else {
					List<SVGLine> lineList = horizontalRuler.getSVGLineList();
					if (lineList != null && lineList.size() > 0) {
						for (SVGLine line : lineList) {
//							LOG.trace(line.toXML());
						}
						List<SVGLine> lineList1 = SVGLine.mergeParallelLines(lineList, eps);
						if (lineList1.size() != lineList.size()) {
							horizontalRuler.setSVGLineList(lineList1);
						}
						addRuler(newRulerList, horizontalRuler);
					}
				}
			}
			horizontalRulerList = newRulerList;
		}
		return horizontalRulerList;
	}

	/** if ruler is far from previous one, add it to list.
	 * if ruler is close to last one, integrate it into last ruler as a "tramline"
	 * @param newRulerList
	 * @param horizontalRuler
	 * @return
	 */
	private void addRuler(List<HorizontalRuler> newRulerList, HorizontalRuler horizontalRuler) {
		boolean multipleRuler = true;
		if (newRulerList.size() > 0) {
			HorizontalRuler lastRuler = newRulerList.get(newRulerList.size() -1);
			IntRange thisXRange = new IntRange(horizontalRuler.getBoundingBox().getXRange());
			IntRange lastXRange = new IntRange(lastRuler.getBoundingBox().getXRange());
			double deltaY = horizontalRuler.getY() - lastRuler.getY();
			// if tramlines, record width and skip addition
			if (deltaY < minRulerSpacingY // close together
					&& lastXRange.compareTo(thisXRange) == 0) { // equal spans
				lastRuler.setWidth(deltaY);
				multipleRuler = false;
			} 
		}
		if (multipleRuler) {
			newRulerList.add(horizontalRuler);
		}
	}

	public void mergeRulersAndTextIntoShapeList() {
		phraseListList = textStructurer.getPhraseListList();
		int iPhrase = 0; 
		int iRuler = 0;
		horizontalElementList = new ArrayList<SVGElement>();
		while (true) {
			if (iPhrase < phraseListList.size() && iRuler < horizontalRulerList.size()) {
				PhraseList phraseList = phraseListList.get(iPhrase);
				Ruler ruler = horizontalRulerList.get(iRuler);
				double yPhrase = phraseList.getBoundingBox().getYMin();
				double yRuler = ruler.getBoundingBox().getYMin();
				if (yPhrase <= yRuler) {
					horizontalElementList.add(phraseListList.get(iPhrase++));
				} else {
					horizontalElementList.add(horizontalRulerList.get(iRuler++));
				}
			} else if (iPhrase < phraseListList.size()) {
				horizontalElementList.add(phraseListList.get(iPhrase++));
			} else if (iRuler < horizontalRulerList.size()) {
				horizontalElementList.add(horizontalRulerList.get(iRuler++));
			} else {
				break;
			}
		}
		addIndexes();
	}

	private void addIndexes() {
		Real2Range bboxPhrase = phraseListList.getBoundingBox();
		bboxRuler = SVGUtil.createBoundingBox(horizontalRulerList);
		horizontalElementByCode = new HashMap<String, SVGElement>();
		// there may be no lines
		Integer maxLength = (bboxRuler == null) ? null : (int) bboxRuler.getXRange().getRange();
		double maxFont = getMaxFont(phraseListList);
		int iPhrase = 0;
		int iRuler = 0;
		StringBuilder total = new StringBuilder();
		for (int i = 0; i < horizontalElementList.size(); i++) {
			SVGElement horizontalElement = horizontalElementList.get(i);
			String index = "";
			if (horizontalElement instanceof LineChunk) {
				index = indexLineChunk(maxFont, iPhrase, horizontalElement);
				iPhrase++;
			} else if (horizontalElement instanceof SVGLine) {
				index = indexSVGLine(maxLength, iRuler, horizontalElement);
				iRuler++;
			}
			total.append(index);
			horizontalElementByCode.put(index, horizontalElement);
			horizontalElement.addAttribute(new Attribute("code", String.valueOf(index)));
		}
		rowCodes = total.toString().trim();
	}

	private String indexSVGLine(int maxLength, int iRuler, SVGElement horizontalElement) {
		String index;
		index = " L"+iRuler+"";
		SVGLine line = (SVGLine) horizontalElement;
		double width = line.getStrokeWidth();
		String w = "";
		if (width > 0.6) {
			w += WIDE;
		}
		if (width > 1.5) {
			w += WIDE;
		}
		if (width > 4) {
			w += WIDE;
		}
		index += w+"";
		double l = line.getLength();
		String s = "";
		if (l / (double) maxLength > 0.1) {
			s += LONG;
		}
		if (l / (double) maxLength > 0.2) {
			s += LONG;
		}
		if (l / (double) maxLength > 0.4) {
			s += LONG;
		}
		if (l / (double) maxLength > 0.8) {
			s += LONG;
		}
		if (l / (double) maxLength > 0.99) {
			s += LONG;
		}
		index += s+"";
		return index;
	}

	private String indexLineChunk(double maxFont, int iPhrase, SVGElement horizontalElement) {
		String index;
		index = " P"+iPhrase;
		LineChunk lineChunk = (LineChunk) horizontalElement;
		String f = "";
		Double ff = lineChunk.getFontSize();
		if (ff != null) {
			int fs = (int) (double) lineChunk.getFontSize();
			if (fs / maxFont > 0.6) {
				f += FONT;
			}
			if (fs / maxFont > 0.8) {
				f += FONT;
			}
			if (fs / maxFont > 0.99) {
				f += FONT;
			}
			index += f;
		}
		return index;
	}

	private Double getMaxFont(PhraseListList phraseListList2) {
		Double maxFont = null;
		if (phraseListList.size() > 0) {
			maxFont = phraseListList.get(0).getFontSize();
			for (int i = 1; i < phraseListList.size(); i++) {
				Double f = phraseListList.get(i).getFontSize();
				maxFont =  Math.max(maxFont,  f);
			}
		}
		return maxFont;
	}

	public String getRowCodes() {
		return rowCodes;
	}

	public List<SVGElement> getHorizontalElementList() {
		return horizontalElementList;
	}

	public static HtmlHtml createHtmlWithTable(File inputFile, IntRangeArray rangeArray) {
		IntRange[] intRanges = new IntRange[5];
		if (rangeArray == null) {
			throw new RuntimeException("null RangeArray");
		}
		if (rangeArray.size() != 5) {
			LOG.debug("table too complex: "+rangeArray.size());
			return null;
		}
		for (int i = 0; i < rangeArray.size(); i++) {
			intRanges[i] = rangeArray.get(i);
		}
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		
		List<ScriptLine> scriptedLineList = textStructurer.getScriptedLineListForCommonestFont();
		for (ScriptLine scriptLine : scriptedLineList) {
			TextLine textLine0 = scriptLine.getTextLineList().get(0);
		}
		
		TableStructurer tableStructurer = textStructurer.createTableStructurer();
		tableStructurer.setRanges(intRanges);
		HtmlHtml html = tableStructurer.getHtmlWithTable();
		return html;
	}
	
	public static HtmlHtml createHtmlWithTable(File inputFile, List<TableSection> tableSectionList, TableTitle tableTitle) {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		
		List<ScriptLine> scriptedLineList = textStructurer.getScriptedLineListForCommonestFont();
		for (ScriptLine scriptLine : scriptedLineList) {
			TextLine textLine0 = scriptLine.getTextLineList().get(0);
		}
		
		TableStructurer tableStructurer = textStructurer.createTableStructurer();
		tableStructurer.setTableTitle(tableTitle);
		tableStructurer.setSections(tableSectionList);
		HtmlHtml html = tableStructurer.getHtmlWithTable();
		return html;
	}

	public void setTableTitle(TableTitle tableTitle) {
		this.tableTitle = tableTitle;
	}

	private void setSections(List<TableSection> tableSectionList) {
		this.tableSectionList = tableSectionList;
		LOG.warn("table section NYI");
	}

	private void analyzeTableRow(PhraseList phraseList, int iRow) {
		phraseListList.getOrCreateChildPhraseList();
		ensureColumnManagers();
		List<IntRange> bestWhitespaces = phraseListList.getBestWhitespaceList();
		if (bestWhitespaces.size() != maxColumns) {
			LOG.warn("maxWhitespace ("+bestWhitespaces.size()+") != maxColumns ("+maxColumns+")");
		}
		int iPhrase = 0;
		LOG.trace("-----------------------");
		for (int icol = 0; icol < maxColumns; icol++) {
			Phrase phrase = phraseList.get(iPhrase);
			IntRange enclosingRange = bestWhitespaces.get(icol);
			IntRange phraseRange = phrase.getIntRange();
			ColumnManager columnManager = getColumnManager(icol);
			columnManager.setEnclosingRange(enclosingRange);
			if (!enclosingRange.includes(phraseRange.getMin())) {
				columnManager.addPhrase(null);
				continue;
			}
			columnManager.addPhrase(phrase);
			columnManager.setStartX(phrase.getStartX());
			columnManager.setEndX(phrase.getEndX());
			double delta = phrase.getStartX() - enclosingRange.getMin();
			if (iPhrase < phraseList.size() - 1) {
				iPhrase++;
			};
		}
	}

	public HtmlTr createTableRow(PhraseList phraseList, int iRow, Class clazz) {
		phraseListList.getOrCreateChildPhraseList();
		ensureColumnManagers();
		HtmlTr row = new HtmlTr();
		for (int icol = 0; icol < maxColumns; icol++) {
			HtmlElement cell = (clazz.equals(HtmlTh.class)) ? new HtmlTh() : new HtmlTd();
			row.appendChild(cell);
			Phrase phrase = columnManagerList.get(icol).getPhrase(iRow);
//			String value = phrase == null ? "" : phrase.getStringValue();
//			cell.appendChild(value);
			HtmlElement span = phrase == null ? new HtmlBr() : phrase.getSpanValue();
			cell.appendChild(span);
		}
		return row;
	}

	public List<HtmlTr> createBodyTableRows(int startRow, int endRow, Class clazz) {
		phraseListList.getOrCreateChildPhraseList();
		List<HtmlTr> rows = new ArrayList<HtmlTr>();
		// find margins
//		List<IntRange> bestWhitespaces = phraseListList.getBestWhitespaceList();
		for (int iRow = startRow; iRow <= endRow; iRow++) {
			PhraseList phraseList = phraseListList.get(iRow);
			analyzeTableRow(phraseList, iRow);
		}
		for (int iRow = startRow; iRow <= endRow; iRow++) {
			PhraseList phraseList = phraseListList.get(iRow);
			HtmlTr row = createTableRow(phraseList, iRow, clazz);
			rows.add(row);
		}
		return rows;
	}

	private void debugColumnManagers() {
		ensureColumnManagers();
		for (int i = 0; i < columnManagerList.size(); i++) {
			columnManagerList.get(i).debug();
		}
	}
	


}
