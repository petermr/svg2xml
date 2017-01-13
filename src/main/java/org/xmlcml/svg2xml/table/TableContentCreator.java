package org.xmlcml.svg2xml.table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.IntRangeArray;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.svg2xml.page.PageLayoutAnalyzer;
import org.xmlcml.svg2xml.table.TableSection.TableSectionType;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.TextStructurer;

public class TableContentCreator extends PageLayoutAnalyzer {

	private static final Logger LOG = Logger.getLogger(TableContentCreator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static Pattern TABLE_N = Pattern.compile("(T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+(?:\\(cont(inued)?\\.?\\))?\\s*)");

	private List<HorizontalRuler> rulerList;
	private List<TableSection> tableSectionList;
	private IntRangeArray rangesArray;

	private TableTitle tableTitle;

	private boolean addIndents;

	public TableContentCreator() {
	}

	public List<TableTitle> findTableTitles(List<File> svgChunkFiles) {
		List<TableTitle> tableTitleList = new ArrayList<TableTitle>();
		for (File svgChunkFile : svgChunkFiles) {
			TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgChunkFile);
			PhraseListList phraseListList = textStructurer.getPhraseListList();
			phraseListList.format(3);
			String value = phraseListList.getStringValue();
			List<String> titleList = findChunksWithTitlePattern(value);
			for (int i = 0; i < titleList.size(); i++) {
				TableTitle tableTitle = new TableTitle(titleList.get(i), svgChunkFile.getName());
				tableTitleList.add(tableTitle);
			}
		}
		return tableTitleList;
	}

	private List<String> findChunksWithTitlePattern(String value) {
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
//				if (value.equals(title)) {
//				LOG.debug(value);
				if (value.startsWith(title)) {
					titleIndex = i;
					LOG.debug("title["+value+"]");
					break;
				}
			}
		}
		LOG.debug(title+"/"+titleIndex+"/");
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
		for (int i = startRow; i < horizontalList.size(); i++) {
			HorizontalElement horizontalElement = horizontalList.get(i);
			LOG.trace(">>"+horizontalElement);
			if (horizontalElement instanceof HorizontalRuler) {
				HorizontalRuler thisRuler = (HorizontalRuler) horizontalElement;
				LOG.trace("adding "+thisRuler);
				IntRange thisRange = new IntRange(thisRuler.getBoundingBox().getXRange());
				if (firstRuler == null) {
					firstRuler = thisRuler;
					firstRange = thisRange;
					LOG.trace("first "+firstRange);
				} else if (!thisRange.isEqualTo(firstRange)) {
					LOG.debug("skipped range: "+thisRange+" vs "+firstRange);
					continue;
				}
				followingRulerList.add(thisRuler);
			}
		}
		return followingRulerList;
	}

	public void createSectionsAndRangesArray() {
		List<HorizontalElement> horizontalList = getHorizontalList();
		int iRow = search(tableTitle.getTitle());
		if (iRow == -1) {
			LOG.error("Cannot find title: "+tableTitle);
		} else {
			List<HorizontalRuler> fullRulerList = getFullRulers(iRow);
			tableSectionList = new ArrayList<TableSection>();
			IntRange tableSpan = fullRulerList.get(0).getIntRange().getRangeExtendedBy(20, 20);
			LOG.debug("Table Span "+tableSpan);
			this.createSections(horizontalList, iRow, fullRulerList, tableSpan);
			this.createRangesArray(tableSectionList);
		}
	}
	
	private IntRangeArray createRangesArray(List<TableSection> tableSectionList) {
		rangesArray = new IntRangeArray();
		int length = 0;
		for (TableSection tableSectionX : tableSectionList) {
			int phraseListCount = tableSectionX.getPhraseListCount();
//			LOG.debug(">>"+phraseListCount);
			IntRange intRange = new IntRange(length, length + phraseListCount);
			length += phraseListCount;
			rangesArray.add(intRange);
		}
		return rangesArray;
	}

	public void createSections(List<HorizontalElement> horizontalList, int iRow, List<HorizontalRuler> fullRulerList,
			IntRange tableSpan) {
		int section = 0;
		TableSection tableSection = null;
		for (int j = iRow; j < horizontalList.size(); j++) {
			HorizontalElement element = horizontalList.get(j);
			HorizontalRuler ruler = (element instanceof HorizontalRuler) ? 
					(HorizontalRuler) element : null;
			if (tableSection == null || fullRulerList.contains(ruler)) {
				tableSection = new TableSection(TableSectionType.values()[section]);
				tableSectionList.add(tableSection);
				if (section < TableSectionType.values().length - 1) {
					section++;
				}
			} else if (element instanceof PhraseList) {
				PhraseList newPhraseList = ((PhraseList) element).extractIncludedLists(tableSpan);
				if (newPhraseList.size() > 0) {
					tableSection.add(newPhraseList);
				}
				
			} else if (element instanceof HorizontalRuler) {
				tableSection.add(element);
			}
		}
	}

	public IntRangeArray getRangesArray() {
		return rangesArray;
	}

	public IntRangeArray getRangesArrayWithPseudoHeader() {
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
		List<TableSection> sectionList = getTableSectionList();
		HtmlHtml html = TableStructurer.createHtmlWithTable(inputFile, sectionList, tableTitle);
		return html;
	}

	public void setTableTitle(TableTitle tableTitle) {
		this.tableTitle = tableTitle;
	}

	public void setAddIndents(boolean add) {
		this.addIndents = add;
	}


}
