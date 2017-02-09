package org.xmlcml.svg2xml.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Univariate;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.table.TableGrid;
import org.xmlcml.svg2xml.table.TableStructurer;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.SuscriptEditor;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.xml.XMLUtil;

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
	private boolean rotatable = false;


	private boolean omitWhitespace = true;

	public PageLayoutAnalyzer() {
		setDefaults();
	}

	private void setDefaults() {
		xRangeRangeMin = 50;
		ensureXRangeSets();
		omitWhitespace = true;
	}

	private void ensureXRangeSets() {
		xRangeSet = HashMultiset.create();
		xRangeStartSet = HashMultiset.create();
		xRangeEndSet = HashMultiset.create();
	}
	
	public void createContent(File inputFile) {
		LOG.debug(inputFile.getAbsolutePath());
		this.inputFile = inputFile;
		textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		SVGElement chunk = textStructurer.getSVGChunk();
		cleanChunk(chunk);
		if (rotatable  && textStructurer.hasAntiClockwiseCharacters()) {
			SVGSVG.wrapAndWriteAsSVG(chunk, new File("target/debug/preRot.svg"));
			chunk = textStructurer.rotateClockwise();
			SVGSVG.wrapAndWriteAsSVG(chunk, new File("target/debug/postRot.svg"));
			TextStructurer textStructurer1 = TextStructurer.createTextStructurerWithSortedLines(chunk);
			textStructurer = textStructurer1;
		}

		phraseListList = textStructurer.getPhraseListList();
		LOG.debug("reading ... "+phraseListList.toXML());
		textStructurer.condenseSuscripts();
		phraseListList.format(3);
		tableStructurer = textStructurer.createTableStructurer();
		TableGrid tableGrid = tableStructurer.createGrid();
			
		if (tableGrid == null) {
			createOrderedHorizontalList();
		}
	}

	private void cleanChunk(SVGElement chunk) {
		if (omitWhitespace) {
			detachWhitespaceTexts(chunk);
		}
	}

	private void detachWhitespaceTexts(SVGElement chunk) {
		List<SVGText> spaceList = SVGText.extractSelfAndDescendantTexts(chunk);
		for (SVGText text : spaceList) {
			String textS = text.getText();
			if (textS == null || textS.trim().length() == 0) {
				text.detach();
				LOG.debug("Deleted whitespace");
			}
		}
	}

	private List<HorizontalElement> createOrderedHorizontalList() {
		Stack<PhraseList> phraseListStack = new Stack<PhraseList>();
		for (PhraseList phraseList : phraseListList) {
			phraseListStack.push(phraseList);
		}
		Stack<HorizontalRuler> horizontalRulerListStack = new Stack<HorizontalRuler>();
		horizontalRulerList = tableStructurer.getHorizontalRulerList(true, 1.0);
		for (HorizontalRuler ruler : horizontalRulerList) {
			horizontalRulerListStack.push(ruler);
		}
		addStacksToHorizontalListInYOrder(phraseListStack, horizontalRulerListStack);
		return horizontalList;
	}

	private void addStacksToHorizontalListInYOrder(Stack<PhraseList> phraseListStack, Stack<HorizontalRuler> horizontalRulerListStack) {
		horizontalList = new ArrayList<HorizontalElement>();
		PhraseList currentPhraseList = null;
		HorizontalRuler currentRuler = null;
		while (!phraseListStack.isEmpty() || !horizontalRulerListStack.isEmpty() ||
				currentPhraseList != null || currentRuler != null) {
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
					addPhraseList(currentPhraseList);
					currentPhraseList = null;
				} else {
					addRuler(currentRuler);
					currentRuler = null;
				}
			} else if (currentPhraseList != null) {
				addPhraseList(currentPhraseList);
				currentPhraseList = null;
			} else if (currentRuler != null) {
				addRuler(currentRuler);
				currentRuler = null;
			} else {
				LOG.trace("stacks empty");
			}
		}
		Collections.reverse(horizontalList);
	}

	private void addRuler(HorizontalRuler currentRuler) {
		horizontalList.add((HorizontalElement)currentRuler);
		LOG.trace("phrase: "+currentRuler.getStringValue()+"/"+currentRuler.getY());
	}

	private void addPhraseList(PhraseList currentPhraseList) {
		horizontalList.add(currentPhraseList);
		LOG.trace("phrase: "+currentPhraseList.getStringValue()+"/"+currentPhraseList.getY());
	}

	public List<HorizontalElement> getHorizontalList() {
		return horizontalList;
	}


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
				if (xRange.getRange() >= xRangeRangeMin) {
					xRangeSet.add(xRange);
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

	public TextStructurer getTextStructurer() {
		return textStructurer;
	}

	public TableStructurer getTableStructurer() {
		return tableStructurer;
	}
	
	public boolean isRotatable() {
		return rotatable;
	}

	public void setRotatable(boolean rotatable) {
		this.rotatable = rotatable;
	}



}
