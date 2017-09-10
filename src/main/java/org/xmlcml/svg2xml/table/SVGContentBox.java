package org.xmlcml.svg2xml.table;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGUtil;
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
	private SVGG svgElement;
	
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

	public void addContainedElements(PhraseListList phraseListList) {
		for (PhraseList phraseList : phraseListList) {
			for (int iPhrase = 0; iPhrase < phraseList.size(); iPhrase++) {
				Phrase phrase = phraseList.get(iPhrase);
				phrase.setBoundingBoxCached(true);
				//this is inefficient but it keeps the phrases in order
				if (getRect().getBoundingBox().includes(phrase.getBoundingBox())) {
					addPhrase(phrase);
					this.appendChild(phrase.copy());
				}
			}
		}
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
}
