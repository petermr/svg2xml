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
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Univariate;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.cache.ComponentCache;
import org.xmlcml.svg2xml.table.TableGrid;
import org.xmlcml.svg2xml.table.TableStructurer;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.svg2xml.text.Phrase;
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

	private static final char CHAR_P = 'P';
	private static final char CHAR_L = 'L';
	private static final String LP = "LP";
	private static final String LP_1 = LP+"{1}";
	private static final String X = "X";
	private static final String T = "T";

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
	protected SVGElement svgChunk;

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
		this.inputFile = inputFile;
		svgChunk = SVGElement.readAndCreateSVG(inputFile);
		createContent(svgChunk);
	}

	public void createContent(SVGElement svgElement) {
		ComponentCache componentCache = new ComponentCache();
		componentCache.readGraphicsComponents(svgElement);
		LOG.debug("components: "+componentCache.toString());
		List<Real2Range> rects = SVGElement.createBoundingBoxList(componentCache.getOrCreateRectCache().getOrCreateRectList());
		LOG.trace("rects: "+rects);
		textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgElement);
		SVGElement inputSVGChunk = textStructurer.getSVGChunk();
		cleanChunk(inputSVGChunk);
		if (rotatable  && textStructurer.hasAntiClockwiseCharacters()) {
			SVGSVG.wrapAndWriteAsSVG(inputSVGChunk, new File("target/debug/preRot.svg"));
			inputSVGChunk = textStructurer.rotateClockwise();
			SVGSVG.wrapAndWriteAsSVG(inputSVGChunk, new File("target/debug/postRot.svg"));
			TextStructurer textStructurer1 = TextStructurer.createTextStructurerWithSortedLines(inputSVGChunk);
			textStructurer = textStructurer1;
		}

		phraseListList = textStructurer.getPhraseListList();
		LOG.trace(">pll>"+phraseListList.size()+" ... "+phraseListList.toXML());
		textStructurer.condenseSuscripts();
		phraseListList.format(3);
		tableStructurer = textStructurer.createTableStructurer();
		TableGrid tableGrid = tableStructurer.createGrid();
			
		if (tableGrid == null) {
			createOrderedHorizontalList();
			LOG.trace("hlist: " + PageLayoutAnalyzer.createSig(horizontalList));
		}
		return;
	}

	public static String createSig(List<HorizontalElement> horizontalList) {
		StringBuilder sb;
		String sig = createLPList(horizontalList);
		LOG.trace(">>"+sig);
		String lpc = createLPCountList(sig);
		LOG.trace(">>>"+lpc);
		String lpccond = contractLP1(lpc);
		LOG.trace(">>>>"+lpccond);
		return lpccond;
	}

	private static String createLPList(List<HorizontalElement> horizontalList) {
		StringBuilder sb = new StringBuilder();
		for (HorizontalElement helem :horizontalList) {
			if (helem instanceof HorizontalRuler) {
				sb.append("L");
			} else if (helem instanceof PhraseList) {
				sb.append("P");
			} else {
				sb.append(helem.getClass().getSimpleName());
			}
		}
		String sig = sb.toString();
		return sig;
	}

	private static String createLPCountList(String sig) {
		StringBuilder sb;
		sb = new StringBuilder();
		int pcount = 0;
		for (int i = 0; i < sig.length(); i++) {
			char c = sig.charAt(i);
			if (c == CHAR_P) {
				pcount++;
			} else if (c == CHAR_L) {
				if (pcount > 0) {
					sb.append(CHAR_P+"{"+pcount+"}");
				}
				pcount = 0;
				sb.append("L");
			}
		}
		if (pcount > 0) {
			sb.append(CHAR_P+"{"+pcount+"}");
		}
		return sb.toString();
	}

	private static String contractLP1(String sig) {
		String cond = sig.replace(LP_1, X);
		StringBuilder sb = new StringBuilder();
		int xcount = 0;
		int i = 0;
		while (i < cond.length()) {
			String s = cond.substring(i, i+1);
			if (X.equals(s)) {
				if (xcount == 1) {
					sb.append(X);
				}
				xcount++;
			} else {
				if (xcount > 1) {
					sb.append(T+"{"+(xcount - 1) +"}");
					xcount = 0;
				}
				sb.append(s);
			}
			i++;
		}
		if (xcount > 0) {
			sb.append(T+"{"+xcount+"}");
		}
		String s = sb.toString();
		s = s.replace(X, LP);
		s = s.replace(T+"{1}", LP);
		return s;
	}


	private void cleanChunk(GraphicsElement chunk) {
		if (omitWhitespace) {
			detachWhitespaceTexts(chunk);
		}
	}

	private void detachWhitespaceTexts(GraphicsElement chunk) {
		List<SVGText> spaceList = SVGText.extractSelfAndDescendantTexts(chunk);
		for (SVGText text : spaceList) {
			String textS = text.getText();
			if (textS == null || textS.trim().length() == 0) {
				text.detach();
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
		for (HorizontalElement horizontalElement : horizontalList) {
			LOG.trace("============"+horizontalElement.getClass()+"\n"+horizontalElement.toString());
		}
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
