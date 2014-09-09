package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.RealArray;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

/** collections of maps, sets, summarising stats on potential columns in text.
 * 
 * @author pm286
 *
 */
public class ColumnMaps {

	private final static Logger LOG = Logger.getLogger(ColumnMaps.class);
	
	private static final Double COUNT_CUTOFF = 0.33;
	
	private TextStructurer textStructurer;
	private Multiset<Integer> startXIntSet;
	private Multiset<Integer> midXIntSet;
	private Multiset<Integer> endXIntSet;
	private List<TextLine> textLineList;

	private List<Multiset.Entry<Integer>> startXSortedByCount;
	private List<Multiset.Entry<Integer>> startXSortedByCoordinate;
	private List<Multiset.Entry<Integer>> midXSortedByCount;
	private List<Multiset.Entry<Integer>> midXSortedByCoordinate;
	private List<Multiset.Entry<Integer>> endXSortedByCount;
	private List<Multiset.Entry<Integer>> endXSortedByCoordinate;

	private List<Entry<Integer>> startTabWithHighestCountList;
	private List<Entry<Integer>> midTabWithHighestCountList;
	private List<Entry<Integer>> endTabWithHighestCountList;

	private Integer startTabCountFilter;
	private Integer endTabCountFilter;

	private List<Entry<Integer>> startTabWithHighestCountCoordList;
	private List<Entry<Integer>> midTabWithHighestCountCoordList;
	private List<Entry<Integer>> endTabWithHighestCountCoordList;

	private ColumnMaps() {
		
	}
	
	public ColumnMaps(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
		textLineList = textStructurer.getTextLineList();
		generateMaps();
	}

	public ColumnMaps(List<TextLine> textLineList) {
		this.textLineList = textLineList;
		generateMaps();
	}

	private void generateMaps() {
		textStructurer.getLinesInIncreasingY();
		startXIntSet = HashMultiset.create();
		midXIntSet = HashMultiset.create();
		endXIntSet = HashMultiset.create();
		for (TextLine textLine : textLineList) {
			RawWords rawWords = textLine.getRawWords();
			addToSet(rawWords.getStartXArray(), startXIntSet);
			addToSet(rawWords.getMidXArray(), midXIntSet);
			addToSet(rawWords.getEndXArray(), endXIntSet);
		}
		startXSortedByCount = makeList(getSortedByCount(startXIntSet));
		startXSortedByCoordinate = makeList(getSortedByValue(startXIntSet));
		midXSortedByCount = makeList(getSortedByCount(midXIntSet));
		midXSortedByCoordinate = makeList(getSortedByValue(midXIntSet));
		endXSortedByCount = makeList(getSortedByCount(endXIntSet));
		endXSortedByCoordinate = makeList(getSortedByValue(endXIntSet));
	}
	
	private List<Entry<Integer>> makeList(Iterable<Entry<Integer>> iterable) {
		List<Entry<Integer>> sortedList = new ArrayList<Entry<Integer>>();
		for (Entry<Integer> entry : iterable) {
			sortedList.add(entry);
		}
		return sortedList;
	}
	
	public List<Entry<Integer>> getStartXSortedByCount() {
		return startXSortedByCount;
	}
	public List<Entry<Integer>> getStartXSortedByValue() {
		return startXSortedByCoordinate;
	}
	public List<Entry<Integer>> getMidXSortedByCount() {
		return midXSortedByCount;
	}
	public List<Entry<Integer>> getMidXSortedByValue() {
		return midXSortedByCoordinate;
	}
	public List<Entry<Integer>> getEndXSortedByCount() {
		return endXSortedByCount;
	}
	public List<Entry<Integer>> getEndXSortedByValue() {
		return endXSortedByCoordinate;
	}
	
	private void addToSet(RealArray realArray, Multiset<Integer> intSet) {
		for (int i = 0; i < realArray.size(); i++) {
			intSet.add((int) realArray.get(i));
		}
	}

	private Iterable<Multiset.Entry<Integer>> getSortedByCount(Multiset<Integer> set) {
		Iterable<Multiset.Entry<Integer>> entriesSortedByCount = 
		   Multisets.copyHighestCountFirst(set).entrySet();
		return entriesSortedByCount;
	}
	
	private Iterable<Multiset.Entry<Integer>> getSortedByValue(Multiset<Integer> set) {
		Iterable<Multiset.Entry<Integer>> entriesSortedByValue =
		   ImmutableSortedMultiset.copyOf(set).entrySet();	
		return entriesSortedByValue;
	}
	public void getTabs() {
		startTabCountFilter = getTabFilter(startXSortedByCount);
		startTabWithHighestCountList = getTabsWithHighestCount(startTabCountFilter, startXSortedByCount);
		debug("start", startTabWithHighestCountList);
		Integer midTabCountFilter = getTabFilter(midXSortedByCount);
		midTabWithHighestCountList = getTabsWithHighestCount(midTabCountFilter, midXSortedByCount);
		debug("mid", midTabWithHighestCountList);
		endTabCountFilter = getTabFilter(endXSortedByCount);
		endTabWithHighestCountList = getTabsWithHighestCount(endTabCountFilter, endXSortedByCount);
		debug("end", endTabWithHighestCountList);
		startTabWithHighestCountCoordList = getTabsWithHighestCount(startTabCountFilter, startXSortedByCoordinate);
		debug("start", startTabWithHighestCountCoordList);
		midTabWithHighestCountCoordList = getTabsWithHighestCount(midTabCountFilter, midXSortedByCoordinate);
		debug("mid", midTabWithHighestCountCoordList);
		endTabWithHighestCountCoordList = getTabsWithHighestCount(endTabCountFilter, endXSortedByCoordinate);
		debug("end", endTabWithHighestCountCoordList);
	}
	
	public List<Tab> createSingleTabList() {
		List<Tab> tabList = new ArrayList<Tab>();
		List<Entry<Integer>> startXList = startTabWithHighestCountCoordList;
		List<Entry<Integer>> midXList = midTabWithHighestCountCoordList;
		List<Entry<Integer>> endXList = endTabWithHighestCountCoordList;
		
		while (true) {
			Tab tab = null;
			Entry<Integer> startEntry = startXList.isEmpty() ? null : startXList.get(0);
			Entry<Integer> midEntry = midXList.isEmpty() ? null : midXList.get(0);
			Entry<Integer> endEntry = endXList.isEmpty() ? null : endXList.get(0);
			Integer startX = startEntry == null ? null : startEntry.getElement();
			Integer midX = midEntry == null ? null : midEntry.getElement();
			Integer endX = endEntry == null ? null : endEntry.getElement();
			LOG.trace(startX+" "+midX+" "+endX);
			if (lessThanEqual(startX, midX) && lessThanEqual(startX, endX)) {
				tab = new Tab("S", startEntry);
				//startXSortedByCoordinate.remove(0);
				startXList.remove(0);
			} else if (lessThanEqual(midX, startX) && lessThanEqual(midX, endX)) {
				tab = new Tab("M", midEntry);
				//midXSortedByCoordinate.remove(0);
				midXList.remove(0);
			} else if (lessThanEqual(endX, startX) && lessThanEqual(endX, midX)) {
				tab = new Tab("E", endEntry);
				//endXSortedByCoordinate.remove(0);
				endXList.remove(0);
			} else {
				LOG.debug("FINISHED");
				break;
			}
			if (tab != null) {
				tabList.add(tab);
			}
		}
		return tabList;
	}
	
	private boolean lessThanEqual(Integer startX, Integer midX) {
		if (startX == null) {
			return false;
		} else if (midX == null) {
			return true;
		} else {
			return startX <= midX;
		}
	}
	private Integer getTabFilter(List<Entry<Integer>> sortedByCountList) {
		return sortedByCountList.size() == 0 ? null : sortedByCountList.get(0).getCount(); 
	}
	private void debug(String msg,
			List<Entry<Integer>> tabWithHighestCountList) {
		System.out.println(msg);
		for (Entry<Integer> entry : tabWithHighestCountList) {
			System.out.print(entry.getElement()+"("+entry.getCount()+") ");
		}
		System.out.println();
	}
	private List<Entry<Integer>> getTabsWithHighestCount(Integer maxTab, List<Entry<Integer>> sortedByCountList) {
		List<Entry<Integer>> highestCountTabList = new ArrayList<Entry<Integer>>();
		if (sortedByCountList.size() > 0) { 
			for (Entry<Integer> entry : sortedByCountList) {
				if (entry.getCount() >= COUNT_CUTOFF * maxTab) {
					highestCountTabList.add(entry);
				}
			}
		}
		return highestCountTabList;
	}
	
}
