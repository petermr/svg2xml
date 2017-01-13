package org.xmlcml.svg2xml.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Univariate;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.svg2xml.table.TableStructurer;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.TextStructurer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** a new approach (2017) to analyzing the structure of pages.
 * uses PhraseList extents and HorizontalRulers to estimate widths
 * 
 * @author pm286
 *
 */
public class PageLayoutAnalyzer {

	private static final Logger LOG = Logger.getLogger(PageLayoutAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static Pattern TABLE_N = Pattern.compile("T[Aa][Bb][Ll][Ee]\\s+(\\d+)\\s+(\\(cont(inued)?\\.?\\))?(.{0,500})");


	protected TextStructurer textStructurer;
	protected PhraseListList phraseListList;
	protected TableStructurer tableStructurer;
	protected List<HorizontalRuler> horizontalRulerList;
	protected List<HorizontalElement> horizontalList;
	
	private Multiset<IntRange> xRangeSet = HashMultiset.create();
	private Multiset<Integer> xRangeStartSet;
	private Multiset<Integer> xRangeEndSet;
	private boolean includeRulers;
	private boolean includePhrases;
	private int xRangeRangeMin; // to exclude ticks on diagrams


	private File inputFile;

	public PageLayoutAnalyzer() {
		setDefaults();
	}

	private void setDefaults() {
		xRangeRangeMin = 50;
		ensureXRangeSets();
	}

	private void ensureXRangeSets() {
		xRangeSet = HashMultiset.create();
		xRangeStartSet = HashMultiset.create();
		xRangeEndSet = HashMultiset.create();
	}
	
	public void createContent(File inputFile) {
		this.inputFile = inputFile;
		textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		phraseListList = textStructurer.getPhraseListList();
		tableStructurer = textStructurer.createTableStructurer();
		phraseListList.format(3);
		createOrderedHorizontalList();
	}

	private List<HorizontalElement> createOrderedHorizontalList() {
		Stack<PhraseList> phraseListStack = new Stack<PhraseList>();
		for (PhraseList phraseList : phraseListList) {
			phraseListStack.push(phraseList);
		}
		Stack<HorizontalRuler> horizontalRulerListStack = new Stack<HorizontalRuler>();
		horizontalRulerList = tableStructurer.getHorizontalRulerList(true, 1.0);
		for (HorizontalRuler ruler : horizontalRulerList) {
			LOG.trace("RULER: "+ruler.getBoundingBox()+"; children "+ruler.getAllSVGLineList().size()/*+"/"+ruler.toXML()*/);
			horizontalRulerListStack.push(ruler);
		}
		addStacksToHorizontalListInYOrder(phraseListStack, horizontalRulerListStack);
		return horizontalList;
	}

	private void addStacksToHorizontalListInYOrder(Stack<PhraseList> phraseListStack, Stack<HorizontalRuler> horizontalRulerListStack) {
		horizontalList = new ArrayList<HorizontalElement>();
		PhraseList currentPhraseList = null;
		HorizontalRuler currentRuler = null;
		while (!phraseListStack.isEmpty() || !horizontalRulerListStack.isEmpty()) {
			if (!phraseListStack.isEmpty() && currentPhraseList == null) {
				currentPhraseList = phraseListStack.pop();
			}
			if (!horizontalRulerListStack.isEmpty() && currentRuler == null) {
				currentRuler = horizontalRulerListStack.pop();
			}
			if (currentRuler != null && currentPhraseList != null) {
				Double rulerY = currentRuler.getY();
				Double phraseListY = currentPhraseList.getXY().getY();
				if (rulerY < phraseListY) {
					horizontalList.add(currentPhraseList);
					currentPhraseList = null;
				} else {
					horizontalList.add((HorizontalElement)currentRuler);
					currentRuler = null;
				}
			} else if (currentPhraseList != null) {
				horizontalList.add(currentPhraseList);
				currentPhraseList = null;
			} else if (currentRuler != null) {
				horizontalList.add((HorizontalElement)currentRuler);
				currentRuler = null;
			}
		}
		Collections.reverse(horizontalList);
	}

	public List<HorizontalElement> getHorizontalList() {
		return horizontalList;
	}

//	public Map<String, File> findTableTitles(List<File> svgChunkFiles) {
//		Map<String, File> svgChunkByTitle = new HashMap<String, File>();
//		for (File svgChunkFile : svgChunkFiles) {
//			TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgChunkFile);
//			PhraseListList phraseListList = textStructurer.getPhraseListList();
//			phraseListList.format(3);
//			String value = phraseListList.getStringValue();
//			Matcher matcher = TABLE_N.matcher(value);
//			List<String> titleList = new ArrayList<String>();
//			int start = 0;
//			while (matcher.find(start)) {
//				start = matcher.end();
//				String title = matcher.group(1);
//				if (matcher.group(2) != null) {
//					title += "c";
//				}
//				if (titleList.contains(title)) {
//					LOG.warn("Duplicate title: "+title);
//					title += "*";
//				}
//				titleList.add(title);
//			}
//			for (int i = 0; i < titleList.size(); i++) {
//				String title = titleList.get(i);
//				svgChunkByTitle.put(title, svgChunkFile);
//			}
//		}
//		return svgChunkByTitle;
//	}

	public void analyzeXRangeExtents(File inputFile) {
		createContent(inputFile);
		List<HorizontalElement> horizontalElementList = getHorizontalList();
		
		for (HorizontalElement horizontalElement : horizontalElementList) {
			IntRange xRange = new IntRange(((SVGElement)horizontalElement).getBoundingBox().getXRange().format(0));
			int round = 1;
			xRangeStartSet.add(xRange.getMin() / round * round);
			xRangeEndSet.add(xRange.getMax() / round * round);
			if (includeRulers && horizontalElement instanceof HorizontalRuler ||
				includePhrases && horizontalElement instanceof PhraseList) {
//				System.out.println(((SVGElement)horizontalElement).toString());
				if (xRange.getRange() >= xRangeRangeMin) {
					xRangeSet.add(xRange);
					System.out.println(xRange);
				}
			}
		}
	}

	public List<HorizontalRuler> getHorizontalRulerList() {
		return horizontalRulerList;
	}

//	private void ensureContent() {
//		if (tableStructurer == null && inputFile != null) {
//			createContent(inputFile);
//		}
//	}

	public void setIncludeRulers(boolean b) {
		this.includeRulers = b;
	}

	public void setIncludePhrases(boolean b) {
		this.includePhrases = b;
	}

	public Multiset<IntRange> getXRangeSet() {
		return xRangeSet;
	}

	public Multiset<Integer> getXRangeEndSet() {
		return xRangeEndSet;
	}

	public Multiset<Integer> getXRangeStartSet() {
		return xRangeStartSet;
	}

	public void setXRangeRangeMin(int rangeMin) {
		this.xRangeRangeMin = rangeMin;
	}

	public RealArray getXRangeStartArray() {
		return getRealArray(xRangeStartSet);
	}
	
	public Univariate getXStartUnivariate() {
		return new Univariate(getXRangeStartArray());
	}

	public RealArray getXRangeEndArray() {
		return getRealArray(xRangeEndSet);
	}

	public Univariate getXEndUnivariate() {
		return new Univariate(getXRangeEndArray());
	}


	private RealArray getRealArray(Multiset<Integer> xSet) {
		RealArray xArray = new RealArray();
		for (Multiset.Entry<Integer> entry : xSet.entrySet()) {
			for (int i = 0; i < entry.getCount(); i++) {
				xArray.addElement((double)entry.getElement());
			}
		}
		return xArray;
	}

}
