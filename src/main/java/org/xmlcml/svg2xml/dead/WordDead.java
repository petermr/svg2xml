package org.xmlcml.svg2xml.dead;

import org.xmlcml.graphics.svg.SVGG;

/** a contiguous set of characters with no intervening spaces
 * managaed as a <g> element
 * @author pm286
 *
 */
public class WordDead extends SVGG {
	
//	private static final String SVG_CLASS = "word_";
//
//	private final static Logger LOG = Logger.getLogger(Word.class);
//	
//	public static final String SVG_CLASS_NAME = "word";
//	public static final String COORDS = "coords";
//	public static final String S_PARA = "<P>";
//	public static final Word PARA = new Word(new SVGText(new Real2(0., 0.), S_PARA));
//
//	//these are NOT childElements as they are original, so these are references
//	private List<SVGText> characters;
//	// this IS a (new) child Element
//	private SVGText svgText;
//	private String value;
//
//	/** used for constants
//	 * 
//	 * @param text
//	 */
//	private Word(SVGText text) {
//		init();
//		this.svgText = text;
//		this.setAndGetId();
//	}
//	
//	public Word(List<SVGText> chars) {
//		init();
//		characters = chars;
//		getStringValue();
//		SVGText char0 = chars.get(0);
//		this.svgText = new SVGText(char0.getXY(), value);
//		char0.getParent().appendChild(this);
//		this.svgText.setId(SVG_CLASS+char0.getId());
//		this.setAndGetId();
//		checkCoordsAndCopyAttributes(char0);
//		addCoordReal2Array();
//		replaceCharactersByText();
//	}
//
//	private void replaceCharactersByText() {
//		svgText.setFill("red");
//		this.appendChild(svgText);
//		addCoordsAttributeToText();
//		detachCharacters();
//	}
//	
//	public Word(Word word) {
//		super(word);
//		this.characters = word.characters;
//		this.svgText = word.svgText;
//		this.value = word.value;
//	}
//	
//	protected void init() {
//		this.setClassName(SVG_CLASS_NAME);
//	}
//	
//	public String setAndGetId() {
//		String id = "w_"+this.svgText.getId();
//		this.setId(id);
//		return id;
//	}
//
//	private void addCoordReal2Array() {
//		Real2Array r2a = new Real2Array();
//		for (SVGText character : characters) {
//			Real2 coord = character.getXY();
//			r2a.add(coord);
//		}
//		this.addAttribute(new Attribute(COORDS, r2a.toString()));
////		this.debug("COORDS");
//	}
//
//	private void checkCoordsAndCopyAttributes(SVGText t) {
//		if (t.getXY() == null) {
//			throw new RuntimeException("no coords");
//		}
//		XMLUtil.copyAttributes(t, this.svgText);
//		svgText.setId("w_"+t.getId());
////		svgText.debug("SVGT");
//	}
//	
//	public String getStringValue() {
//		if (value == null && characters != null) {
//			StringBuilder sb = new StringBuilder();
//			for (SVGText character : characters) {
//				String ss = character.getValue();
//				sb.append(ss);
//			}
//			value = sb.toString();
//		}
//		return value;
//	}
//
//	public Real2Range getBoundingBox() {
//		if (boundingBoxNeedsUpdating()) {
//			if (characters.size() > 0) {
//				Real2Range r2ra = characters.get(0).getBoundingBox();
//				Real2Range r2rb = characters.get(characters.size()-1).getBoundingBox();
//				boundingBox = new Real2Range(r2ra.getCorners()[0], r2rb.getCorners()[1]);
//			}
//		}
//		return boundingBox;
//	}
//	public boolean endsWith(String suffix) {
//		return (value == null || value.length() == 0) ? false : value.endsWith(suffix);  
//	}
//	public Character getCharAt(int i) {
//		return value == null || value.length() <= i ? null : value.charAt(i);
//	}
//	
//	public void setStringValue(String string) {
//		svgText.setText(string);
//		this.value = string;
//	}
//	
//	public boolean isParagraphMarker() {
//		return PARA.getStringValue().equals(getStringValue());
//	}
//	
//	public Real2 getXY() {
//		return svgText == null ? null : svgText.getXY();
//	}
//
//	public void addToParentAndReplaceCharacters(SVGElement parent) {
//		svgText.detach();
//		parent.appendChild(svgText);
//		LOG.trace(parent.getId()+" "+parent.getLocalName());
//		LOG.trace("adding SVG "+svgText.getValue()+" "+characters);
//		addCoordsAttributeToText();
//		detachCharacters();
//	}
//
//	private void addCoordsAttributeToText() {
//		Attribute coordsAtt = this.getAttribute(COORDS);
//		if (coordsAtt != null) {
//			LOG.trace("coords "+coordsAtt);
//			svgText.addAttribute(new Attribute(coordsAtt));
//		}
//	}
//
//	public void detachCharacters() {
//		if (characters != null) {
//			for (SVGText character : characters) {
//				LOG.trace("DETACH "+character.getValue());
//				character.detach();
//			}
//		}
////		this.debug("DETACHED");
//	}
//	
//	public Number makeNumber() {
//		String value = svgText.getValue();
//		Number number = null;
//		try {
//			number = new Integer(value);
//		} catch (NumberFormatException nfe) {
//			//
//		}
//		if (number == null) {
//			try {
//				number = Real.parseDouble(value);
//			} catch (NumberFormatException nfe) {
//				//
//			}
//		}
//		return number;
//	}
//
//	public SVGText getSVGText() {
//		return svgText;
//	}
//	
//	/** makes a new list composed of the words in the list
//	 * 
//	 * @param elements
//	 * @return
//	 */
//	public static List<Word> extractWords(List<SVGElement> elements) {
//		List<Word> wordList = new ArrayList<Word>();
//		for (SVGElement element : elements) {
//			if (element instanceof Word) {
//				wordList.add((Word) element);
//			}
//		}
//		return wordList;
//	}
}
