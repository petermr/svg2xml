package org.xmlcml.svg2xml.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.IntRangeArray;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGTitle;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.html.HtmlTr;
import org.xmlcml.svg2xml.page.PageLayoutAnalyzer;
import org.xmlcml.svg2xml.table.TableSection.TableSectionType;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.util.GraphPlot;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Attribute;

public class TableContentCreator extends PageLayoutAnalyzer {


	private static final Logger LOG = Logger.getLogger(TableContentCreator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static Pattern TABLE_N = Pattern.compile("(T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+(?:\\(cont(inued)?\\.?\\))?\\s*)");
	private static final String TABLE_FOOTER = "table.footer";
	private static final String TABLE_BODY = "table.body";
	private static final String TABLE_HEADER = "table.header";
	private static final String TABLE_TITLE = "table.title";
	public static final String DOT_ANNOT_SVG = ".annot.svg";
	private static final String DOT_PNG = ".png";

	private List<HorizontalRuler> rulerList;
	private List<TableSection> tableSectionList;
	private IntRangeArray rangesArray;
	private TableTitle tableTitle;
	private boolean addIndents;
	private TableTitleSection tableTitleSection;
	private TableHeaderSection tableHeader;
	private TableBodySection tableBody;
	private TableFooterSection tableFooter;
	
	public TableContentCreator() {
	}

	/** scans whole file for all tableTitles.
	 * 
	 * @param svgChunkFiles
	 * @return list of titles;
	 */
	public List<TableTitle> findTableTitles(List<File> svgChunkFiles) {
		List<TableTitle> tableTitleList = new ArrayList<TableTitle>();
		for (File svgChunkFile : svgChunkFiles) {
			findTableTitle(tableTitleList, svgChunkFile);
		}
		return tableTitleList;
	}

	private void findTableTitle(List<TableTitle> tableTitleList, File svgChunkFile) {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgChunkFile);
		PhraseListList phraseListList = textStructurer.getPhraseListList();
		phraseListList.format(3);
		String value = phraseListList.getStringValue();
		List<String> titleList = findTitlesWithPattern(value);
		for (int i = 0; i < titleList.size(); i++) {
			TableTitle tableTitle = new TableTitle(titleList.get(i), svgChunkFile.getName());
			tableTitleList.add(tableTitle);
		}
	}

	private List<String> findTitlesWithPattern(String value) {
		Matcher matcher = TABLE_N.matcher(value);
		List<String> titleList = new ArrayList<String>();
		int start = 0;
		while (matcher.find(start)) {
			start = matcher.end();
			String title = matcher.group(0);
			if (titleList.contains(title)) {
				LOG.warn("Duplicate title: "+title);
				title += "*";
			}
			titleList.add(title);
		}
		return titleList;
	}

	public int search(String title) {
		int titleIndex = -1;
		for (int i = 0; i < horizontalList.size(); i++) {
			HorizontalElement horizontalElement = horizontalList.get(i);
			if (horizontalElement instanceof PhraseList) {
				String value = ((PhraseList)horizontalElement).getStringValue().trim();
				if (value.startsWith(title)) {
					titleIndex = i;
					LOG.trace("title["+value+"]");
					break;
				}
			}
		}
		LOG.trace(title+"/"+titleIndex+"/");
		return titleIndex;
	}

	public HorizontalRuler getNextRuler(int irow) {
		HorizontalRuler nextRuler = null;
		getFullRulers(irow);
		return nextRuler;
	}

	/** assume following ruler is representative of this table and find all subsequent full rulers.
	 * 
	 * @param startRow
	 * @return
	 */
	public List<HorizontalRuler> getFullRulers(int startRow) {
		HorizontalRuler firstRuler = null;
		IntRange firstRange = null;
		List<HorizontalRuler> followingRulerList = new ArrayList<HorizontalRuler>();
		IntRange previousRange = null;
		for (int i = startRow; i < horizontalList.size(); i++) {
			HorizontalElement horizontalElement = horizontalList.get(i);
			if (horizontalElement instanceof HorizontalRuler) {
				HorizontalRuler thisRuler = (HorizontalRuler) horizontalElement;
				IntRange thisRange = new IntRange(thisRuler.getBoundingBox().getXRange());
//				LOG.debug("*****************"+thisRange+"/"+thisRuler.getSVGLine().getXY(0).getY());
				if (firstRuler == null) {
					firstRuler = thisRuler;
					firstRange = thisRange;
				} else if (!thisRange.isEqualTo(firstRange)) {
					LOG.trace("skipped range: "+thisRange+" vs "+firstRange);
					continue;
				}
				followingRulerList.add(thisRuler);
			}
		}
		return followingRulerList;
	}

	public void createSectionsAndRangesArray() {
		List<HorizontalElement> horizontalList = getHorizontalList();
		int iRow = tableTitle == null ? 0 : search(tableTitle.getTitle());
//		FIXME
//		mergeOverlappingRulersWithSameYInHorizontalElements(iRow);
		if (iRow == -1) {
			LOG.error("Cannot find title: "+tableTitle);
		} else {
			List<HorizontalRuler> fullRulerList = getFullRulers(iRow);
			tableSectionList = new ArrayList<TableSection>();
			IntRange tableSpan = fullRulerList.size() == 0 ? null : fullRulerList.get(0).getIntRange().getRangeExtendedBy(20, 20);
			LOG.debug("Table Span "+tableSpan);
			if (tableSpan != null) {
				this.createSections(horizontalList, iRow, fullRulerList, tableSpan);
				this.createPhraseRangesArray();
			}
		}
	}
	
	private IntRangeArray createPhraseRangesArray() {
		rangesArray = new IntRangeArray();
		int length = 0;
		for (TableSection tableSectionX : tableSectionList) {
			int phraseListCount = tableSectionX.getPhraseListCount();
			IntRange intRange = new IntRange(length, length + phraseListCount);
			length += phraseListCount;
			rangesArray.add(intRange);
		}
		LOG.debug("rangesArray "+rangesArray);
		return rangesArray;
	}

	private void createSections(List<HorizontalElement> horizontalList, int iRow, List<HorizontalRuler> fullRulerList,
			IntRange tableSpan) {
		TableSection tableSection = null;
		LOG.trace("start at row: "+iRow+"; "+horizontalList.get(0));
		for (int j = iRow; j < horizontalList.size(); j++) {
			HorizontalElement element = horizontalList.get(j);
			HorizontalRuler ruler = (element instanceof HorizontalRuler) ? 
					(HorizontalRuler) element : null;
			if (tableSection == null || fullRulerList.contains(ruler)) {
				tableSection = new TableSection(TableSectionType.OTHER);
				tableSectionList.add(tableSection);
			}
			if (element instanceof PhraseList) {
				PhraseList newPhraseList = (PhraseList) element;
				if (newPhraseList.size() > 0) {
					tableSection.add(newPhraseList);
				}
				
			} else if (element instanceof HorizontalRuler) {
				// dont add Ruler if first element (e.g sectioning ruler)
				if (tableSection.getHorizontalElementList().size() > 0) {
					tableSection.add(element);
				}
			}
		}
		LOG.debug("sections "+tableSectionList.size());
	}

	public IntRangeArray getRangesArray() {
		return rangesArray;
	}

	private IntRangeArray getRangesArrayWithPseudoHeader() {
		if (rangesArray.size() == 4) {
			LOG.warn("adding pseudoheader");
			IntRangeArray newArray = new IntRangeArray();
			newArray.add(rangesArray.get(0));
			newArray.add(new IntRange(rangesArray.get(0).getMax(), rangesArray.get(1).getMin()));
			for (int i = 1; i < rangesArray.size(); i++) {
				newArray.add(rangesArray.get(i));
			}
			rangesArray = newArray;
		}

		return rangesArray;
	}

	public List<TableSection> getTableSectionList() {
		return tableSectionList;
	}

	public HtmlHtml createHTMLFromSVG(File inputFile) {
		createContent(inputFile);
		createSectionsAndRangesArray();
		LOG.debug("FIXME");
		HtmlHtml html = tableStructurer.createHtmlWithTable(tableSectionList, tableTitle);
		try {
			XMLUtil.debug(html, new File("target/table/debug/sections.html"), 1);
		} catch (IOException e) {
		}
		return html;
	}

	public void setTableTitle(TableTitle tableTitle) {
		this.tableTitle = tableTitle;
	}

	public void setAddIndents(boolean add) {
		this.addIndents = add;
	}

	public SVGElement createMarkedSections(
			String[] colors,
			double[] opacity) {
		// write SVG
		SVGElement markedChunk = getTextStructurer().getSVGChunk();
		SVGG g = new SVGG();
		g.setClassName("sections");
		markedChunk.appendChild(g);
		TableStructurer tableStructurer = getTableStructurer();
		SVGRect plotBox;
		plotBox = GraphPlot.plotBox(tableStructurer.getTitleBBox(), colors[0], opacity[0]);
		plotBox.setClassName(TABLE_TITLE);
		plotBox.appendChild(new SVGTitle(TABLE_TITLE));
		g.appendChild(plotBox);
		plotBox = GraphPlot.plotBox(tableStructurer.getHeaderBBox(), colors[1], opacity[1]);
		plotBox.setClassName(TABLE_HEADER);
		plotBox.appendChild(new SVGTitle(TABLE_HEADER));
		g.appendChild(plotBox);
		plotBox = GraphPlot.plotBox(tableStructurer.getBodyBBox(), colors[2], opacity[2]);
		plotBox.setClassName(TABLE_BODY);
		plotBox.appendChild(new SVGTitle(TABLE_BODY));
		g.appendChild(plotBox);
		plotBox = GraphPlot.plotBox(tableStructurer.getFooterBBox(), colors[3], opacity[3]);
		plotBox.setClassName(TABLE_FOOTER);
		plotBox.appendChild(new SVGTitle(TABLE_FOOTER));
		g.appendChild(plotBox);
		TableContentCreator.shiftToOrigin(markedChunk, g);
		return markedChunk;
	}

	public static void shiftToOrigin(SVGElement markedChunk, SVGG g) {
		SVGG gg = null;
		SVGElement svgElement =  (SVGElement) markedChunk.getChildElements().get(0);
		if (svgElement instanceof SVGG) {
			SVGG firstG = (SVGG) markedChunk.getChildElements().get(0);
			Transform2 t2 = firstG.getTransform();
			g.setTransform(t2);
		}
	}

	public TableTitleSection getTableTitle() {
		if (tableTitleSection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() > 0) {
				tableTitleSection = new TableTitleSection(tableSectionList.get(0));
			}
		}
		return tableTitleSection;
	}

	public TableHeaderSection getTableHeader() {
		if (tableHeader == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 2) {
				tableHeader = new TableHeaderSection(tableSectionList.get(1));
			}
		}
		return tableHeader;
	}

	public TableBodySection getTableBody() {
		if (tableBody == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 3) {
				tableBody = new TableBodySection(tableSectionList.get(2));
			}
		}
		return tableBody;
	}

	public TableFooterSection getTableFooter() {
		if (tableFooter == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 4) {
				tableFooter = new TableFooterSection(tableSectionList.get(3));
			}
		}
		return tableFooter;
	}

	public SVGElement getSVGChunk() {
		return textStructurer.getSVGChunk();
	}

	public SVGElement annotateAreas(File inputFile) {
		createHTMLFromSVG(inputFile);
		SVGElement svgChunk = createMarkedSections(
				new String[] {"yellow", "red", "cyan", "blue"},
				new double[] {0.2, 0.2, 0.2, 0.2}
			);
		TableTitleSection tableTitle = getTableTitle();
		if (tableTitle == null) {
			LOG.warn("no table title");
		} else {
			svgChunk = tableTitle.createMarkedContent(
					(SVGElement) svgChunk.copy(),
					new String[] {"yellow", "yellow"}, 
					new double[] {0.2, 0.2}
					);
		}
		TableHeaderSection tableHeader = getTableHeader();
		if (tableHeader == null) {
			LOG.warn("no table header");
		} else {
			tableHeader.createHeaderRowsAndColumnGroups();
			svgChunk = tableHeader.createMarkedSections(
					(SVGElement) svgChunk.copy(),
					new String[] {"blue", "green"}, 
					new double[] {0.2, 0.2}
					);
		}
		TableBodySection tableBody = getTableBody();
		if (tableBody == null) {
			LOG.warn("no table body");
		} else {
			tableBody.createHeaderRowsAndColumnGroups();
			svgChunk = tableBody.createMarkedSections(
					(SVGElement) svgChunk.copy(),
					new String[] {"yellow", "red"}, 
					new double[] {0.2, 0.2}
					);
		}
		TableFooterSection tableFooter = getTableFooter();
		if (tableFooter != null) {
			svgChunk = tableFooter.createMarkedContent(
					(SVGElement) svgChunk.copy(),
					new String[] {"blue", "blue"}, 
					new double[] {0.2, 0.2}
					);
		}
		return svgChunk;
	}

	public void markupAndOutputTable(File inputFile, File outDir) {
		String outRoot = inputFile.getName();
		outRoot = outRoot.substring(0, outRoot.length() - DOT_PNG.length());
		File outputFile = new File(outDir, outRoot+DOT_ANNOT_SVG);
		LOG.debug("reading "+inputFile);
		SVGElement svgChunk = annotateAreas(inputFile);
		SVGSVG.wrapAndWriteAsSVG(svgChunk, outputFile);
	}

	/** create HTML from annot.svg
	 * 
	 * @param annotSvgFile
	 * @param outDir
	 * @throws IOException 
	 */
	public void createHTML(File annotSvgFile, File outDir) throws IOException {
		SVGElement svgElement = SVGUtil.parseToSVGElement(new FileInputStream(annotSvgFile));
		HtmlHtml html = new HtmlHtml();
		HtmlBody body = new HtmlBody();
		html.appendChild(body);
		HtmlTable table = new HtmlTable();
		table.addAttribute(new Attribute("style", "border: 1px solid black;"));
		body.appendChild(table);
		
		addCaption(svgElement, table);
		addHeader(svgElement, table);
		addBody(svgElement, table);
		XMLUtil.debug(html, new File(outDir, annotSvgFile.getName()+".html"), 1);
		
		
	}

	private void addHeader(SVGElement svgElement, HtmlTable table) {
		HtmlTr tr = new HtmlTr();
		table.appendChild(tr);
		SVGElement g = (SVGElement) XMLUtil.getSingleElement(svgElement, 
				".//*[local-name()='g' and @class='"+TableHeaderSection.HEADER_COLUMN_BOXES+"']");
		if (g != null) {
			List<SVGRect> rects = SVGRect.extractSelfAndDescendantRects(g);
			for (int i = 0; i < rects.size(); i++) {
				String title = rects.get(i).getValue();   // messy but has to be rewritten
				title = title.replace(" //", "");
				HtmlTh th = new HtmlTh();
				th.addAttribute(new Attribute("style", "border: 1px solid black;"));
				th.appendChild(title.substring(title.indexOf("/")+1));
				tr.appendChild(th);
			}
		}
	}

	private void addBody(SVGElement svgElement, HtmlTable table) {
		SVGElement g = (SVGElement) XMLUtil.getSingleElement(svgElement, 
				".//*[local-name()='g' and @class='"+TableBodySection.BODY_CELL_BOXES+"']");
		if (g != null) {
			List<SVGG> gs = SVGG.extractSelfAndDescendantGs(g);
			List<List<SVGRect>> rectListList = new ArrayList<List<SVGRect>>();
			for (int i = 0; i < gs.size(); i++) {
				List<SVGRect> rects = SVGRect.extractSelfAndDescendantRects(gs.get(i));
				rectListList.add(rects);
			}
			if (rectListList.size() > 0) {
				for (int irow = 0; irow < rectListList.get(0).size(); irow++) {
					HtmlTr tr = new HtmlTr();
					table.appendChild(tr);
					for (int jcol = 0; jcol < rectListList.size(); jcol++) {
						List<SVGRect> rectjList = rectListList.get(jcol);
						if (irow >= rectjList.size()) {
							LOG.warn("row index out of range "+irow);;
						} else {
							SVGRect rectij = rectjList.get(irow);
							HtmlTd td = new HtmlTd();
							td.addAttribute(new Attribute("style", "border: 1px solid black;"));
							tr.appendChild(td);
							String value = rectij.getValue();
							td.appendChild(value.substring(value.indexOf("/")+1));
						}
					}
				}
			}
		}	
	}

	private void addCaption(SVGElement svgElement, HtmlTable table) {
		HtmlCaption caption = new HtmlCaption();
		String captionS = XMLUtil.getSingleValue(svgElement, ".//*[local-name()='g' and @class='"+TableTitleSection.TITLE_TITLE+"']");
		if (captionS !=null) {
			int idx = captionS.indexOf("//");
			captionS = idx == -1 ? captionS : captionS.substring(idx + 2);
	//		caption.appendChild(captionS.substring(captionS.indexOf("//")+2));
			table.appendChild(caption);
		}
	}



}
