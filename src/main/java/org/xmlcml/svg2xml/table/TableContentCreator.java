package org.xmlcml.svg2xml.table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.IntRangeArray;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRange.Direction;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGLineList;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGTitle;
import org.xmlcml.graphics.svg.cache.ComponentCache;
import org.xmlcml.graphics.svg.cache.LineCache;
import org.xmlcml.graphics.svg.cache.RectCache;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.html.HtmlTr;
import org.xmlcml.svg2xml.box.ContentBoxCache;
import org.xmlcml.svg2xml.box.SVGContentBox;
import org.xmlcml.svg2xml.page.PageLayoutAnalyzer;
import org.xmlcml.svg2xml.table.GenericRow.RowType;
import org.xmlcml.svg2xml.table.TableSection.TableSectionType;
import org.xmlcml.svg2xml.table.TableSection.TableSectionTypeOLD;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRule;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.util.GraphPlot;
import org.xmlcml.xml.XMLUtil;

public class TableContentCreator extends PageLayoutAnalyzer {

	private static final Pattern TABLE_PATTERN = Pattern.compile("T[Aa][Bb][Ll][Ee]");
	private static final String TITLE_SECT_FILL = "yellow";
	private static final String HEADER_SECT_FILL = "red";
	private static final String BODY_SECT_FILL = "cyan";
	private static final String FOOTER_SECT_FILL = "blue";

	private static final Logger LOG = Logger.getLogger(TableContentCreator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static Pattern TABLE_PATTERN_CONTINUED = Pattern.compile("(.*\\(?[Cc]ont(inued)?\\.?\\)?)");
	public final static Pattern TABLE_TITLE_PATTERN_SIMPLE = Pattern.compile("(T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+\\s*)");
	public final static Pattern TABLE_TITLE_PATTERN_CONTINUED = Pattern.compile("(T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+(\\(?[Cc]ont(inued)?\\.?\\)?)\\s*)");
	public final static Pattern TABLE_TITLE_PATTERN_ANY = Pattern.compile("(.*T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+(?:\\([Cc]ont(inued)?\\.?\\))?\\s*.*)");
	
	private static final String TABLE_FOOTER = "table.footer";
	private static final String TABLE_BODY = "table.body";
	private static final String TABLE_HEADER = "table.header";
	private static final String TABLE_TITLE = "table.title";
	public static final String DOT_ANNOT_SVG = ".annot.svg";
	private static final String DOT_PNG = ".png";
	private static final String CELL_FULL = "cell";
	private static final String CELL_EMPTY = "empty";
	private static final double LINE_BOX_WIDTH = 0.5;

	private List<TableSection> tableSectionList;
	private IntRangeArray rangesArray;
	private TableTitle tableTitle;
	private boolean continued = false;
	private TableTitleSection tableTitleSection;
	private TableHeaderSection tableHeaderSection;
	private TableBodySection tableBodySection;
	private TableFooterSection tableFooterSection;
	private GraphicsElement annotatedSvgChunk;
	private double rowDelta = 2.5; //large to manage suscripts
	private ContentBoxCache contentBoxCache;
	private int titleSectionIndex;
	private List<HorizontalRule> firstFullRuleList;
	private SVGG contentBoxGridG;
	private File contentBoxGridFile;
	private ComponentCache ownerComponentCache;
	
	public TableContentCreator() {
	}

//	/** scans whole file for all tableTitles.
//	 * 
//	 * @param svgChunkFiles
//	 * @return list of titles;
//	 */
//	// FIXME not used?
//	private List<TableTitle> findTableTitles(List<File> svgChunkFiles) {
//		List<TableTitle> tableTitleList = new ArrayList<TableTitle>();
//		for (File svgChunkFile : svgChunkFiles) {
//			findTableTitle(tableTitleList, svgChunkFile);
//		}
//		return tableTitleList;
//	}

//	private void findTableTitle(List<TableTitle> tableTitleList, File svgChunkFile) {
//		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgChunkFile);
//		PhraseListList phraseListList = textStructurer.getPhraseListList();
//		phraseListList.format(3);
//		String value = phraseListList.getStringValue();
//		List<String> titleList = TableContentCreator.findTitles(TABLE_TITLE_PATTERN_ANY, value);
//		for (int i = 0; i < titleList.size(); i++) {
//			TableTitle tableTitle = new TableTitle(titleList.get(i), svgChunkFile.getName());
//			tableTitleList.add(tableTitle);
//		}
//	}

	private static List<String> findTitles(Pattern titlePattern, String value) {
		Matcher matcher = titlePattern.matcher(value);
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

	private int search(String title) {
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

//	// FIXME not used
//	private HorizontalRule getNextRuler(int irow) {
//		HorizontalRule nextRuler = null;
//		getFullRulers(irow);
//		return nextRuler;
//	}

	/** assume following ruler is representative of this table and find all subsequent full rulers.
	 * 
	 * @param startRow
	 * @return
	 */
	private List<HorizontalRule> getFullRules(int startRow) {
		HorizontalRule firstRule = null;
		IntRange firstRange = null;
		List<HorizontalRule> followingRuleList = new ArrayList<HorizontalRule>();
		IntRange previousRange = null;
		for (int i = startRow; i < horizontalList.size(); i++) {
			HorizontalElement horizontalElement = horizontalList.get(i);
			if (horizontalElement instanceof HorizontalRule) {
				HorizontalRule thisRule = (HorizontalRule) horizontalElement;
				IntRange thisRange = new IntRange(thisRule.getBoundingBox().getXRange());
				if (firstRule == null) {
					firstRule = thisRule;
					firstRange = thisRange;
				} else if (!thisRange.isEqualTo(firstRange)) {
					LOG.trace("skipped range: "+thisRange+" vs "+firstRange);
					continue;
				}
				followingRuleList.add(thisRule);
			}
		}
		return followingRuleList;
	}

//	private void createSectionsAndRangesArray() {
//		List<HorizontalElement> horizontalList = getHorizontalList();
//		int iRow = tableTitle == null ? 0 : search(tableTitle.getTitle());
////		mergeOverlappingRulersWithSameYInHorizontalElements(iRow);
//		if (iRow == -1) {
//			LOG.error("Cannot find title: "+tableTitle +"; MAYBE CHANGE LOGIC");
//			// CHANGE LOGIC
//		} else {
//			List<HorizontalRule> fullRulerList = getFullRules(iRow);
//			tableSectionList = new ArrayList<TableSection>();
//			IntRange tableSpan = fullRulerList.size() == 0 ? null : fullRulerList.get(0).getIntRange().getRangeExtendedBy(20, 20);
//			if (tableSpan != null) {
//				this.createSections(horizontalList, iRow, fullRulerList, tableSpan);
//				this.createPhraseRangesArray();
//				analyzeRangesAndSections();
//
//			}
//		}
//	}
	
	private void createSectionsAndRangesArrayNew() {
		removeBoundingRules(horizontalList, StartEnd.START);
		removeBoundingRules(horizontalList, StartEnd.END);
		makeCaches(svgChunk);
		LineCache lineCache = ownerComponentCache.getOrCreateLineCache();
		contentBoxGridG = new SVGG();
		contentBoxGridG.appendChild(contentBoxCache.getOrCreateConvertedSVGElement());
		contentBoxGridG.appendChild(contentBoxCache.getOrCreateContentBoxGrid().getOrCreateSVGElement());
		String sig = PageLayoutAnalyzer.createSig(horizontalList);
		tableSectionList = new ArrayList<TableSection>();
		TableSection tableSection = new TableSection();
		
		// sections are always bounded by long horizontal lines or panels,
		// and never by phraselists, phraselistlists or short horizontal lines
		// forget HorizontalElements at this stage
		// this will be fairly crude.
		RowManager rowManager = new RowManager();
		
		List<SVGContentBox> contentBoxes = contentBoxCache.getOrCreateContentBoxList();
		LOG.debug("contentBox: "+contentBoxes.size());
		for (SVGContentBox contentBox : contentBoxes) {
			rowManager.addContentBox(contentBox);
		}
		List<Real2Range> contentGridPanelBoxes = contentBoxCache.getOrCreateContentBoxGrid().getBboxList();
		LOG.debug("contentGrid: "+contentGridPanelBoxes.size());
		for (Real2Range contentGridPanelBox : contentGridPanelBoxes) {
			RealRange yRange1 = contentGridPanelBox.getRealRange(Direction.VERTICAL);
			rowManager.addContentGridPanelBox(contentGridPanelBox);
		}
		SVGLineList longHorizontalRuleList = lineCache.getOrCreateLongHorizontalLineList();
		rowManager.addHorizontalRules(longHorizontalRuleList, RowType.LONG_HORIZONTAL);
		// I think the short Horizontal all get turned into siblings
		SVGLineList shortHorizontalRuleList = lineCache.getOrCreateShortHorizontalLineList();
		rowManager.addHorizontalRules(shortHorizontalRuleList, RowType.SHORT_HORIZONTAL);
		List<SVGLineList> horizontalSiblingsList = lineCache.getHorizontalSiblingsList();
		rowManager.addHorizontalSiblingsList(horizontalSiblingsList);
// phrases		
		addPhrases(rowManager);
		rowManager.sortAndAddBoxContent();
		LOG.debug("RowManager "+rowManager.toString());
		
		getTitleSectionIndex();
		// the L refers to lines (now H) // FIXME
		
		String fullRules = sig.replaceAll("[^L]", "");
		LOG.debug("sections "+tableSectionList.size()+"; title => "+titleSectionIndex+"; sig: " + sig +"; fullRules: "+ fullRules.length());
		firstFullRuleList = getFullRules(0);
		LOG.debug("fullRule "+firstFullRuleList.size()+"; ");

		IntRange tableSpan = firstFullRuleList.size() == 0 ? null : firstFullRuleList.get(0).getIntRange().getRangeExtendedBy(20, 20);
		if (tableSpan != null) {
			this.createSectionsNew(horizontalList, 0, firstFullRuleList, tableSpan);
			this.createPhraseRangesArray();
			analyzeRangesAndSections();
		}
	}

	private void addPhrases(RowManager rowManager) {
		for (HorizontalElement elem : horizontalList) {
			if (elem instanceof PhraseList) {
				PhraseList phraseList = (PhraseList) elem;
				rowManager.addPhraseList(phraseList);
			}
		}
	}

	private void addToRangeArray(RealRangeArray yRangeArray, SVGLineList ruleList, String type) {
		for (SVGLine line : ruleList) {
			Real2Range lineBox = line.getBoundingBox().
					getReal2RangeExtendedInY(LINE_BOX_WIDTH, LINE_BOX_WIDTH);
			RealRange yRange = lineBox.getYRange();
			addRange(yRangeArray, type, yRange, "");
		}
	}

	private void addRange(RealRangeArray yRangeArray, String type, RealRange yRange, String msg) {
		if (yRangeArray.includes(yRange)) {
			LOG.debug("OMITTED " + type+": "+msg);
		} else {
			yRangeArray.add(yRange);
		}
	}

	private void addContentBoxGridPanels(RealRangeArray yRangeArray) {
		List<Real2Range> contentGridPanels = contentBoxCache.getOrCreateContentBoxGrid().getBboxList();
		LOG.debug("contentGrid: "+contentGridPanels.size());
		for (Real2Range contentGridPanel : contentGridPanels) {
			RealRange yRange = contentGridPanel.getRealRange(Direction.VERTICAL);
			yRangeArray.add(yRange);
		}
	}

	/** finds section containing the string matching TABLE_PATTERN regex
	 * 
	 * @return index of section -1 if not found (THBF => 0, HBTF => 2, etc.) 
	 */
	private int getTitleSectionIndex() {
		titleSectionIndex = -1;
		for (int i = 0; i < tableSectionList.size(); i++) {
			TableSection tableSection1 = tableSectionList.get(i);
			if (tableSection1.contains(TABLE_PATTERN)) {
				titleSectionIndex = i;
			}
			LOG.trace(tableSection1.debugPhrases()+"\n===========================\n");
		}
		return titleSectionIndex;
	}

	private void makeCaches(SVGElement svgElement) {
		ownerComponentCache = new ComponentCache();
		ownerComponentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		RectCache rectCache = ownerComponentCache.getOrCreateRectCache();
		ownerComponentCache.removeBorderingRects();
		contentBoxCache = ContentBoxCache.createCache(rectCache, phraseListList);
		contentBoxCache.getOrCreateConvertedSVGElement();
		contentBoxCache.getOrCreateContentBoxGrid();
		ownerComponentCache.addCache(contentBoxCache);
	}

	private void removeBoundingRules(List<HorizontalElement> horizontalList, StartEnd startEnd) {
		while (horizontalList.size() > 0) {
			int ielem = StartEnd.START.equals(startEnd) ? 0 : horizontalList.size() - 1;
			HorizontalElement helem = horizontalList.get(ielem);
			if (helem instanceof HorizontalRule) {
				LOG.trace("removed: "+horizontalList.get(ielem));
				horizontalList.remove(ielem);
			} else {
				LOG.trace("FINISH");
				break;
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
		return rangesArray;
	}

	private void analyzeRangesAndSections() {
		
		String firstSectionString = tableSectionList.get(0).getStringValue().trim();
		firstSectionString = firstSectionString.substring(0,  Math.min(firstSectionString.length(), 50)).trim();
		String lastSectionString = tableSectionList.get(tableSectionList.size() - 1).getStringValue().trim();
		lastSectionString = lastSectionString.substring(0,  Math.min(lastSectionString.length(), 50)).trim();
		if (firstSectionString.startsWith("Table")) {
			LOG.trace("title 0 "+firstSectionString);
		} else if (lastSectionString.startsWith("Table")) {
			LOG.trace("title last "+lastSectionString);
		} else {
			LOG.info("***** NO TITLE SECTION ****\n"+firstSectionString+"\n"+lastSectionString);
		}
		if (rangesArray.size() == 4) {
			// the commonest
			if (rangesArray.get(0).getMax() > 2) {
				LOG.trace("large title: "+firstSectionString+"\n"+rangesArray);
			} else if (rangesArray.get(1).getRange() > 4) {
				LOG.trace("large header: "+rangesArray);
			} else if (rangesArray.get(2).getRange() < 4) {
				LOG.trace("small body: "+rangesArray);
			}
		} else {
			LOG.trace("Ranges: "+rangesArray.size()+"; "+rangesArray);
		}

	}

	private void createSections(List<HorizontalElement> horizontalList, int iRow, List<HorizontalRule> fullRuleList,
			IntRange tableSpan) {
		TableSection tableSection = null;
		LOG.trace("start at row: "+iRow+"; "+horizontalList.get(0));
		for (int j = iRow; j < horizontalList.size(); j++) {
			HorizontalElement element = horizontalList.get(j);
			HorizontalRule rule = (element instanceof HorizontalRule) ? 
					(HorizontalRule) element : null;
			if (tableSection == null || fullRuleList.contains(rule)) {
				tableSection = new TableSection(TableSectionTypeOLD.OTHER);
				tableSectionList.add(tableSection);
			}
			if (element instanceof PhraseList) {
				PhraseList newPhraseList = (PhraseList) element;
				if (newPhraseList.size() > 0) {
					tableSection.add(newPhraseList);
				}
				
			} else if (element instanceof HorizontalRule) {
				// dont add Rule if first element (e.g sectioning rule)
				if (tableSection.getHorizontalElementList().size() > 0) {
					tableSection.add(element);
				}
			}
		}
		LOG.debug("Created but not analyzed table sections OLD: "+tableSectionList.size());
	}

	/** used in transition between refactors */
	private void createSectionsNew(List<HorizontalElement> horizontalList, int iRow, List<HorizontalRule> fullRulerList,
			IntRange tableSpan) {
		TableSection tableSection = null;
		LOG.trace("start at row: "+iRow+"; "+horizontalList.get(0));
		for (int j = iRow; j < horizontalList.size(); j++) {
			HorizontalElement element = horizontalList.get(j);
			HorizontalRule ruler = (element instanceof HorizontalRule) ? 
					(HorizontalRule) element : null;
			if (tableSection == null || fullRulerList.contains(ruler)) {
				tableSection = new TableSection(TableSectionTypeOLD.OTHER);
				tableSectionList.add(tableSection);
			}
			if (element instanceof PhraseList) {
				PhraseList newPhraseList = (PhraseList) element;
				if (newPhraseList.size() > 0) {
					tableSection.add(newPhraseList);
				}
				
			} else if (element instanceof HorizontalRule) {
				// dont add Ruler if first element (e.g sectioning ruler)
				if (tableSection.getHorizontalElementList().size() > 0) {
					tableSection.add(element);
				}
			}
		}
		LOG.debug("Created but not analyzed table sections NEW: "+tableSectionList.size());
	}

	// GETTER
	public IntRangeArray getRangesArray() {
		return rangesArray;
	}

	// GETTER
	public List<TableSection> getTableSectionList() {
		return tableSectionList;
	}

	public HtmlHtml createHTMLFromSVG(File inputFile) {
		createContent(inputFile);
		createSectionsAndRangesArrayNew();
		analyzeSectionsNew();
		if (contentBoxGridFile != null) {
			SVGSVG.wrapAndWriteAsSVG(contentBoxGridG, contentBoxGridFile);
		}
		HtmlHtml html = tableStructurer.createHtmlWithTable(tableSectionList, tableTitle);
		try {
			XMLUtil.debug(html, new File("target/table/debug/sections.html"), 1);
		} catch (IOException e) {
		}
		return html;
	}

	private void analyzeSectionsNew() {
		LOG.debug("ANALYZE SECTIONS NEW: "+tableSectionList.size());
		findTitleSections();
		LOG.debug("Other sects: ");
		for (int isect = 0; isect < tableSectionList.size(); isect++) {
			TableSection tableSection = tableSectionList.get(isect);
			if (!tableSection.isTitleOrContinued()) {
				PhraseListList phraseListList = tableSection.getOrCreatePhraseListList();
				LOG.debug("PHRASES: "+phraseListList.size()+"; font "+phraseListList.getFontFamily()+"; "+phraseListList.getFontSize());
				
			}
		}
		
	}

	private void findTitleSections() {
		tableTitle = null;
		continued = false;
		for (int isect = 0; isect < tableSectionList.size(); isect++) {
			TableSection tableSection = tableSectionList.get(isect);
//			LOG.debug("Table section: "+tableSection);
			String title = tableSection.matchAgainstIndividualPhrases(TABLE_TITLE_PATTERN_ANY);
			if (title != null) {
				tableSection.setType(TableSectionType.TITLE);
				tableTitle = new TableTitle(title);
				LOG.debug("**TITLE: "+title);
			}
			// this allows for it to be on separate line
			continued = tableSection.matchAgainstIndividualPhrases(TABLE_PATTERN_CONTINUED) != null;
			if (continued) {
				tableSection.setType(TableSectionType.TITLE_CONT);
				LOG.debug("**CONT: "+title);
			}
			if (title != null || continued) {
				if (isect == 0) {
					// ok
				} else if (isect == tableSectionList.size() - 1) {
					LOG.debug("Table at foot");
				} else {
					LOG.warn("Table in strange place: section "+isect);
				}
				continue;
			} else {
				// skip non titles
			}
		}
	}

	private void setTableTitle(TableTitle tableTitle) {
		this.tableTitle = tableTitle;
	}

	/** FIXME.
	 * works on getTextStructurer().getSVGChunk(). needs refactoring to textStructurer
	 * 
	 * returns original file with overlaid boxes
	 * 
	 * @param colors
	 * @param opacity
	 * @return
	 */
	public GraphicsElement createMarkedSections(/*SVGElement markedChunk,*/
			String[] colors,
			double[] opacity) {
		// write SVG
		GraphicsElement markedChunk = getTextStructurer().getSVGChunk();
		SVGG g = new SVGG();
		g.setClassName("sections");
		markedChunk.appendChild(g);
		TableStructurer tableStructurer = getTableStructurer();
		SVGRect plotBox;
		plotBox = GraphPlot.createBoxWithFillOpacity(tableStructurer.getTitleBBox(), colors[0], opacity[0]);
		plotBox.setClassName(TABLE_TITLE);
		plotBox.appendChild(new SVGTitle(TABLE_TITLE));
		g.appendChild(plotBox);
		plotBox = GraphPlot.createBoxWithFillOpacity(tableStructurer.getHeaderBBox(), colors[1], opacity[1]);
		plotBox.setClassName(TABLE_HEADER);
		plotBox.appendChild(new SVGTitle(TABLE_HEADER));
		g.appendChild(plotBox);
		plotBox = GraphPlot.createBoxWithFillOpacity(tableStructurer.getBodyBBox(), colors[2], opacity[2]);
		plotBox.setClassName(TABLE_BODY);
		plotBox.appendChild(new SVGTitle(TABLE_BODY));
		g.appendChild(plotBox);
		plotBox = GraphPlot.createBoxWithFillOpacity(tableStructurer.getFooterBBox(), colors[3], opacity[3]);
		plotBox.setClassName(TABLE_FOOTER);
		plotBox.appendChild(new SVGTitle(TABLE_FOOTER));
		g.appendChild(plotBox);
		TableContentCreator.shiftToOrigin(markedChunk, g);
		return markedChunk;
	}

	public static void shiftToOrigin(GraphicsElement markedChunk, SVGG g) {
		SVGG gg = null;
		GraphicsElement svgElement =  (GraphicsElement) markedChunk.getChildElements().get(0);
		if (svgElement instanceof SVGG) {
			SVGG firstG = (SVGG) markedChunk.getChildElements().get(0);
			Transform2 t2 = firstG.getTransform();
			g.setTransform(t2);
		}
	}

	public TableTitleSection getOrCreateTableTitleSectionOLD() {
		if (tableTitleSection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 1) {
				tableTitleSection = new TableTitleSection(tableSectionList.get(0));
			}
		}
		return tableTitleSection;
	}

	public TableHeaderSection getOrCreateTableHeaderSectionOLD() {
		if (tableHeaderSection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 2) {
				tableHeaderSection = new TableHeaderSection(tableSectionList.get(1));
			}
		}
		return tableHeaderSection;
	}

	public TableBodySection getOrCreateTableBodySectionOLD() {
		if (tableBodySection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 3) {
				tableBodySection = new TableBodySection(tableSectionList.get(2));
			}
		}
		return tableBodySection;
	}

	public TableFooterSection getOrCreateTableFooterSectionOLD() {
		if (tableFooterSection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 4) {
				tableFooterSection = new TableFooterSection(tableSectionList.get(3));
			}
		}
		return tableFooterSection;
	}


	public GraphicsElement getSVGChunk() {
		return textStructurer.getSVGChunk();
	}

	public GraphicsElement annotateAreas(File inputFile) {
		createHTMLFromSVG(inputFile);
		return annotateAreasInSVGChunk();
	}

	
	public GraphicsElement annotateAreasInSVGChunk() {
		GraphicsElement svgChunk = createMarkedSections(
				new String[] {TITLE_SECT_FILL,
						HEADER_SECT_FILL,
						BODY_SECT_FILL,
						FOOTER_SECT_FILL},
				new double[] {0.2, 0.2, 0.2, 0.2}
			);
		svgChunk = annotateAreasInTitleSection(svgChunk);
		svgChunk = annotateAreasInHeaderSection(svgChunk);
		svgChunk = annotateAreasInBodySection(svgChunk);
		svgChunk = annotateAreasInFooterSection(svgChunk);
		return svgChunk;
	}

	private GraphicsElement annotateAreasInFooterSection(GraphicsElement svgChunk) {
		TableFooterSection tableFooter = getOrCreateTableFooterSectionOLD();
		if (tableFooter != null) {
			svgChunk = tableFooter.createMarkedContent(
					(GraphicsElement) svgChunk.copy(),
					new String[] {"blue", "blue"}, 
					new double[] {0.2, 0.2}
					);
		}
		return svgChunk;
	}

	private GraphicsElement annotateAreasInBodySection(GraphicsElement svgChunk) {
		TableBodySection tableBody = getOrCreateTableBodySectionOLD();
		if (tableBody == null) {
			LOG.trace("no table body");
		} else {
			tableBody.createHeaderRowsAndColumnGroups();
			svgChunk = tableBody.createMarkedSections(
					(GraphicsElement) svgChunk.copy(),
					new String[] {"yellow", "red"}, 
					new double[] {0.2, 0.2}
					);
		}
		return svgChunk;
	}

	private GraphicsElement annotateAreasInHeaderSection(GraphicsElement svgChunk) {
		TableHeaderSection tableHeader = getOrCreateTableHeaderSectionOLD();
		if (tableHeader == null) {
			LOG.warn("no table header");
		} else {
			tableHeader.createHeaderRowsAndColumnGroups();
			svgChunk = tableHeader.createMarkedSections(
					(GraphicsElement) svgChunk.copy(),
					new String[] {"blue", "green"}, 
					new double[] {0.2, 0.2}
					);
		}
		return svgChunk;
	}

	private GraphicsElement annotateAreasInTitleSection(GraphicsElement svgChunk) {
		TableTitleSection tableTitle = getOrCreateTableTitleSectionOLD();
		if (tableTitle == null) {
			LOG.warn("no table title");
		} else {
			svgChunk = tableTitle.createMarkedContent(
					(GraphicsElement) svgChunk.copy(),
					new String[] {"yellow", "yellow"}, 
					new double[] {0.2, 0.2}
					);
		}
		return svgChunk;
	}

	public void markupAndOutputTable(File inputFile, File outDir) {
		String outRoot = inputFile.getName();
		outRoot = outRoot.substring(0, outRoot.length() - DOT_PNG.length());
		LOG.trace("reading SVG "+inputFile);
		annotatedSvgChunk = annotateAreas(inputFile);
		File outputFile = new File(outDir, outRoot+DOT_ANNOT_SVG);
		LOG.trace("writing annotated SVG "+outputFile);
		SVGSVG.wrapAndWriteAsSVG(annotatedSvgChunk, outputFile);
	}

	/** create HTML from annot.svg
	 * 
	 * @param annotSvgFile
	 * @param outDir
	 * @throws IOException 
	 */
	public void createHTML(File annotSvgFile, File outDir) throws IOException {
		HtmlHtml html = createHtmlFromSVG();
		File outfile = new File(outDir, annotSvgFile.getName()+".html");
		XMLUtil.debug(html, outfile, 1);
		
		
	}

	public HtmlHtml createHtmlFromSVG() {
		HtmlHtml html = new HtmlHtml();
		HtmlBody body = new HtmlBody();
		html.appendChild(body);
		HtmlTable table = new HtmlTable();
		table.setClassAttribute("table");
		body.appendChild(table);
		
		addCaption(annotatedSvgChunk, table);
		int bodyCols = getGElements(annotatedSvgChunk).size();
		addHeader(annotatedSvgChunk, table, bodyCols);
		addBody(annotatedSvgChunk, table);
		return html;
	}

	private void addHeader(GraphicsElement svgElement, HtmlTable table, int bodyCols) {
		int cols = 0;
		HtmlTr tr = new HtmlTr();
		table.appendChild(tr);
		GraphicsElement g = svgElement == null ? null : (SVGElement) XMLUtil.getSingleElement(svgElement, 
				".//*[local-name()='g' and @class='"+TableHeaderSection.HEADER_COLUMN_BOXES+"']");
		if (g != null) {
			cols = addHeaderBoxes(tr, g, bodyCols);
		}
	}

	private int addHeaderBoxes(HtmlTr tr, GraphicsElement g, int bodyCols) {
		List<SVGRect> rects = SVGRect.extractSelfAndDescendantRects(g);
		int headerCols = rects.size();
		int bodyDelta = bodyCols - headerCols;
		LOG.trace("Header boxes: "+headerCols+"; delta: "+bodyDelta);
		for (int i = 0; i < bodyDelta; i++) {
			HtmlTh th = new HtmlTh();
			tr.appendChild(th);
		}
		for (int i = 0; i < headerCols; i++) {
			SVGRect rect = rects.get(i);   // messy but has to be rewritten
			Real2 xy = rect.getXY();
			String title = rect.getValue();   // messy but has to be rewritten
			title = title.replace(" //", "");
			HtmlTh th = new HtmlTh();
			th.setClassAttribute(CELL_FULL);
			th.appendChild(title.substring(title.indexOf("/")+1));
			tr.appendChild(th);
		}
		return headerCols;
	}

	private void addBody(GraphicsElement svgElement, HtmlTable table) {
		List<SVGG> gs = getGElements(svgElement);
		if (gs.size() == 0) {
			LOG.warn("No annotated body");
			return;
		}
		List<List<SVGRect>> columnList = new ArrayList<List<SVGRect>>();
		for (int i = 0; i < gs.size(); i++) {
			List<SVGRect> rects = SVGRect.extractSelfAndDescendantRects(gs.get(i));
			columnList.add(rects);
		}
		LOG.trace("Body columns: "+columnList.size());

		if (columnList.size() == 0) {
			return;
		}

		List<RealRange> allRanges = createRowRanges(columnList);
		for (int jcol = 0; jcol < columnList.size(); jcol++) {
			List<SVGRect> column = columnList.get(jcol);
			padColumn(column, allRanges);
		}
		
		for (int irow = 0; irow < allRanges.size(); irow++) {
			createRowsAndAddToTable(table, columnList, irow);
		}
	}

	private List<SVGG> getGElements(GraphicsElement svgElement) {
		GraphicsElement g = svgElement == null ? null : (SVGElement) XMLUtil.getSingleElement(svgElement, 
				".//*[local-name()='g' and @class='"+TableBodySection.BODY_CELL_BOXES+"']");
		List<SVGG> gs = (g == null) ? new ArrayList<SVGG>() : SVGG.extractSelfAndDescendantGs(g);
		return gs;
	}

	private void createRowsAndAddToTable(HtmlTable table, List<List<SVGRect>> columnList, int irow) {
		HtmlTr tr = new HtmlTr();
		table.appendChild(tr);
		for (int jcol = 0; jcol < columnList.size(); jcol++) {
			List<SVGRect> rectjList = columnList.get(jcol);
			if (irow >= rectjList.size()) {
				LOG.trace("row index out of range "+irow);;
			} else {
				SVGShape rectij = rectjList.get(irow);
				HtmlTd td = new HtmlTd();
				tr.appendChild(td);
				String value = rectij == null ? "/" : rectij.getValue();
				String value1 = value.substring(value.indexOf("/")+1);
				td.appendChild(value1);
				td.setClassAttribute((value1.trim().length() == 0) ? CELL_EMPTY : CELL_FULL);
			}
		}
	}

	private List<RealRange> createRowRanges(List<List<SVGRect>> columnList) {
		// populate allRanges with column0
		List<RealRange> allRanges = new ArrayList<RealRange>();
		List<SVGRect> column0 = columnList.get(0);
		if (column0.size() == 0) {
			return allRanges; // no rows
		}
		for (int irow = 0; irow < column0.size(); irow++) {
			SVGRect rowi = column0.get(irow);
			RealRange rowRange = rowi.getBoundingBox().getYRange().format(3);
			allRanges.add(rowRange);
		}
		
		// iterate over other columns, filling in holes if necessary
		for (int jcol = 0; jcol < columnList.size(); jcol++) {
			List<SVGRect> columnj = columnList.get(jcol);
			int allPtr = allRanges.size() - 1;
			int colPtr = columnj.size() - 1;
			if (colPtr > allPtr) {
				LOG.error("Column ("+jcol+"; "+(colPtr+1)+") larger than allRanges ("+(allPtr+1)+") \n"+columnj+"; \n"+allRanges);
			}
			while (colPtr >= 0) {
				SVGRect rowi = columnj.get(colPtr);
				RealRange colRange = rowi.getBoundingBox().getYRange();
				RealRange allRange = allRanges.get(allPtr);
				if (colRange.intersects(allRange)) {
					RealRange newRange = colRange.plus(allRange);
					allRanges.set(allPtr, newRange);
					LOG.trace("equal: "+allPtr+"; "+colPtr);
					allPtr--;
					colPtr--;
				} else if (colRange.getMax() < allRange.getMin()) {
					LOG.trace("less: "+allPtr+"; "+colPtr);
					allPtr--;
				} else if (colRange.getMin() > allRange.getMax()) {
					LOG.trace("more: "+allPtr+"; "+colPtr);
					allRanges.add(allPtr + 1, colRange);
					colPtr--;
				} else {
					throw new RuntimeException("cannot add to allRanges "+allRange+"; "+colRange);
				}
				if (allPtr < 0 && colPtr >= 0) {
					LOG.error("Cannot match col=>all "+colPtr+" => "+allPtr+"; "+columnj.size()+" => "+allRanges.size()+" => "+columnj+" => "+allRanges);
					break;
				}
			}
		}
		return allRanges;
	}

	private void padColumn(List<SVGRect> column, List<RealRange> allRanges) {
		int allPtr = allRanges.size() - 1;
		int colPtr = column.size() - 1;
		while (allPtr >= 0) {
			if (colPtr < 0) {
				// empty space at start of column
				column.add(0, (SVGRect) null);
				allPtr--;
			} else {
				RealRange allRange = allRanges.get(allPtr);
				RealRange colRange = column.get(colPtr).getBoundingBox().getYRange();
				if (colRange.intersects(allRange)) {
					// ranges match
					colPtr--;
					allPtr--;
				} else if (colRange.getMin() > allRange.getMax()) {
					throw new RuntimeException("IMPOSSIBLE "+allRange+"; "+colRange);
				} else if (colRange.getMax() < allRange.getMin()) {
					// empty cell in column
					column.add(colPtr + 1, (SVGRect) null);
					allPtr--;
				} else {
					throw new RuntimeException("cannot map to allRanges "+allRange+"; "+colRange);
				}
			}
		}
	}

	// FIXME empty caption
	private void addCaption(GraphicsElement svgElement, HtmlTable table) {
		HtmlCaption caption = new HtmlCaption();
		String captionS = svgElement == null ? null : XMLUtil.getSingleValue(svgElement, ".//*[local-name()='g' and @class='"+TableTitleSection.TITLE_TITLE+"']");
		if (captionS !=null) {
			int idx = captionS.indexOf("//");
			captionS = idx == -1 ? captionS : captionS.substring(idx + 2);
	//		caption.appendChild(captionS.substring(captionS.indexOf("//")+2));
			table.appendChild(caption);
		}
	}

	public GraphicsElement getAnnotatedSvgChunk() {
		return annotatedSvgChunk;
	}

	public static TableContentCreator createHTMLFrom(File tableFile) {
		TableContentCreator tableContentCreator = null;
		if (tableFile != null) {
			tableContentCreator = new TableContentCreator();
			tableContentCreator.createHTMLFromSVG(tableFile);
		}
		return tableContentCreator;
	}
	
	public void setContentBoxGridFile(File file) {
		this.contentBoxGridFile = file;
	}

	public ContentBoxCache getContentBoxCache() {
		return contentBoxCache;
	}
}
