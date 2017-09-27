package org.xmlcml.svg2xml.text;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.rule.horizontal.LineChunk;
import org.xmlcml.graphics.svg.text.phrase.PhraseChunk;
import org.xmlcml.graphics.svg.text.phrase.PhraseNew;
import org.xmlcml.graphics.svg.text.phrase.TextChunk;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

/** contains one or more Phrases into a box.
 * The box may be explicit (lines) or implicit (whitespace)
 * 
 * @author pm286
 *
 */
public class TextBox extends SVGG {

	private static final Logger LOG = Logger.getLogger(TextBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String TAG = "textBox";

	private TextChunk phraseListListElement;
	private SVGRect boundingRect;
	
	public TextBox() {
		super();
		this.setClassName(TAG);
	}

	public TextBox(PhraseChunk phraseList) {
		this();
		add(phraseList);
	}

	public TextBox(LineChunk phrase) {
		this();
		add(phrase);
		SVGSVG.wrapAndWriteAsSVG(this, new File("target/flow/textBox1.svg"));

	}

	public TextBox(TextBox textBox) {
		super(textBox);
		this.setClassName(TAG);
	}

	public GraphicsElement getOrCreatePhraseListList() {
		if (phraseListListElement == null) {
			List<Element> phraseListChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+PhraseChunk.TAG+"']");//			List<Element> phraseListListChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"']");
			phraseListListElement = new TextChunk();
			for (Element phraseListList : phraseListChildren) {
				phraseListListElement.add(new PhraseChunk((PhraseChunk)phraseListList));
			}
		}
		return phraseListListElement;
	}


	public void add(LineChunk phrase) {
		PhraseChunk phraseList = new PhraseChunk();
		phraseList.add(new PhraseNew(phrase));
		add(phraseList);
		boundingRect = null;
	}

	public void add(PhraseChunk phraseList) {
		ensurePhraseListList();
		PhraseChunk newPhraseList = new PhraseChunk(phraseList);
		phraseListListElement.appendChild(newPhraseList);
		getOrCreatePhraseListList();
	}

	private void ensurePhraseListList() {
		if (phraseListListElement == null) {
			phraseListListElement = new TextChunk();
			this.appendChild(phraseListListElement);
		}
	}

	public Real2Range getBoundingBox() {
		Real2Range bboxRect = (boundingBox == null) ? null : boundingRect.getBoundingBox();
		Real2Range bboxChild = (phraseListListElement == null) ? null : phraseListListElement.getBoundingBox();
		Real2Range bbox = bboxRect;
		if (bboxChild != null) {
			bbox = bboxChild.plus(bbox);
		}
		
		return bbox;
	}

	public SVGShape getOrCreateBoundingRect() {
		if (boundingRect == null) {
			Real2Range bbox = this.getBoundingBox();
			boundingRect = SVGRect.createFromReal2Range(bbox);
			if (boundingRect != null) {
				this.appendChild(boundingRect);
			}
		}
		return boundingRect;
	}

	public String getStringValue() {
		String s = phraseListListElement == null ? null : phraseListListElement.getStringValue();
		if (s != null) this.setStringValueAttribute(s);
		return s;
	}

}
