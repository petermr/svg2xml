package org.xmlcml.svg2xml.box;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGLineList;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.table.GenericRow;
import org.xmlcml.svg2xml.text.Phrase;
import org.xmlcml.svg2xml.text.PhraseList;
import org.xmlcml.svg2xml.text.PhraseListList;

/** a contentBox is (usually) a Rect which contains other material.
 * Examples are textboxes, legend boxes or author-marked areas
 * especially in tables.
 * 
 * The contents are ordered by geometry and not by order of addition. There is no check on
 * duplicate additions at present.
 * 
 * Contents can be any SVGElements but are usually PhraseLists (a line of text)
 * or lines (for legends or short rules) 
 * or small graphics objects (Shapes) for symbols
 * 
 * @author pm286
 *
 */
public class SVGContentBox extends SVGG {

	public static final String CONTENT_BOX = "contentBox";
	private static final Logger LOG = Logger.getLogger(SVGContentBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private SVGRect rect = null;
	private PhraseListList phraseListList;
	private SVGLineList lineList;
	private SVGG svgElement;
	private ArrayList<SVGLineList> lineListList;
	
	private SVGContentBox() {
		super();
		this.setClassName(CONTENT_BOX);
	}

	public SVGContentBox(SVGRect rect) {
		this();
		if (rect != null) {
			this.rect = rect;
			this.boundingBox = rect.getBoundingBox();
			this.appendChild(rect.copy());
		}
	}

	public static SVGContentBox createContentBox(SVGRect rect, PhraseListList phraseListList) {
		SVGContentBox contentBox = null;
		if (rect != null && phraseListList != null) {
			if (rect.getBoundingBox().includes(phraseListList.getBoundingBox())) {
				contentBox = new SVGContentBox(rect);
				contentBox.phraseListList = phraseListList;
			}
		}
		return contentBox;
	}

	public void addPhrase(Phrase phrase) {
		getOrCreatePhraseListList();
		PhraseList phraseList = new PhraseList();
		// maybe we should detach
		phraseList.add(new Phrase(phrase));
		phraseListList.add(phraseList);
	}

	public PhraseListList getOrCreatePhraseListList() {
		if (phraseListList == null) {
			phraseListList = new PhraseListList();
		}
		return phraseListList;
	}

	public SVGRect getRect() {
		if (rect == null) {
			List<SVGElement> rects = SVGUtil.getQuerySVGElements(this, "./*[local-name()='rect']");
			if (rects.size() == 1) {
				rect = (SVGRect) rects.get(0);
			}
				
		}
		return rect;
	}
	
	public int size() {
		int size = 0;
		size += getOrCreatePhraseListList().size();
		return size;
	}

	public void addContainedElements(GraphicsElement phraseListList) {
		LOG.error("addContainedElements NYI");
//		for (PhraseList phraseList : phraseListList) {
//			for (int iPhrase = 0; iPhrase < phraseList.size(); iPhrase++) {
//				Phrase phrase = phraseList.get(iPhrase);
//				phrase.setBoundingBoxCached(true);
//				//this is inefficient but it keeps the phrases in order
//				if (getRect().getBoundingBox().includes(phrase.getBoundingBox())) {
//					addPhrase(phrase);
//					this.appendChild(phrase.copy());
//				}
//			}
//		}
	}

	@Override
	public String toString() {
		String s = ""
			+ "rect "+rect.getBoundingBox()+""
			+ " pll "+phraseListList;
		return s;
	}
		
	public SVGElement getOrCreateSVGElement() {
		if (svgElement == null) {
			svgElement = new SVGG();
			svgElement.setClassName(CONTENT_BOX);
			if (rect != null) {
				SVGRect rectCopy = new SVGRect(rect);
				rectCopy.setCSSStyle("stroke-width:1.0;fill:yellow;opacity:0.3;");
				svgElement.appendChild(rectCopy);
			}
			svgElement.appendChild(phraseListList.copy());
		}
		return svgElement;
	}

	/** rather horrible 
	 * selects row content and adds that.
	 * may be in wrong place
	 * 
	 * @param row
	 */
	/*
	private SVGLine line;
	private RowType type;
	private Real2Range box;
	private PhraseList phraseList;
	private SVGLineList lineList;
	private SVGContentBox contentBox;
	 */
	public void add(GenericRow row) {
		boolean added = row.addLineToContentBox(this);
		if (!added) {
			added = row.addLineListToContentBox(this);
		}
		if (!added) {
			added = row.addPhraseListToContentBox(this);
		}
	}

	public boolean addLine(SVGLine line) {
		getOrCreateLineList();
		return lineList.add(line);
	}

	private SVGLineList getOrCreateLineList() {
		if (lineList == null) {
			lineList = new SVGLineList();
		}
		return lineList;
	}

	public boolean addLineList(SVGLineList lineList) {
		getOrCreateLineListList();
		return lineListList.add(lineList);
	}

	private List<SVGLineList> getOrCreateLineListList() {
		if (lineListList == null) {
			lineListList = new ArrayList<SVGLineList>();
		}
		return lineListList;
	}

	public boolean addPhraseList(PhraseList phraseList) {
		getOrCreatePhraseListList();
		phraseListList.add(phraseList);
		return phraseList != null;
	}
}
