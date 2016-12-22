package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.linestuff.Path2ShapeConverter;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;

public class Word extends LineChunk implements Iterable<SVGText> {

	private final static Logger LOG = Logger.getLogger(Word.class);

	public final static String TAG = "word";
	public static String SPACE_SYMBOL = " ";

	List<SVGText> childTextList;
	private boolean guessWidth = true;
	
	public Word() {
		super();
		this.setClassName(TAG);
		
	}

	public Word(SVGG g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	/** 
	 * Creates Word from characters in svgText.
	 * <p>
	 * Mainly for testing purposes. Is unlikely to provide valid coordinates
	 * for characters.
	 * <p>
	 * Splits svgText into characters.
	 * 
	 * @param svgText
	 */
	public static Word createTestWord(SVGText svgText) {
		Word word = new Word();
		List<SVGText> textList = new ArrayList<SVGText>();
		String value = svgText.getValue();
		for (int i = 0; i < value.length(); i++) {
			Real2 r2 = new Real2();
			textList.add(new SVGText(r2, String.valueOf(value.charAt(i))));
		}
		word.setTextList(textList);
		return word;
	}
	
	public Iterator<SVGText> iterator() {
		getOrCreateChildTextList();
		return childTextList.iterator();
	}
	
	public void setTextList(List<SVGText> textList) {
		for (SVGText text : textList) {
			add(text);
		}
	}

	public Real2 getCentrePointOfFirstCharacter() {
		getOrCreateChildTextList();
		return childTextList.get(0).getCentrePointOfFirstCharacter();
	}
	
	private List<SVGText> getOrCreateChildTextList() {
		if (childTextList == null) {
			List<Element> textChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGText.TAG+"']");
			childTextList = new ArrayList<SVGText>();
			for (Element child : textChildren) {
				childTextList.add((SVGText)child);
			}
		}
		return childTextList;
	}

	public Real2 getCentrePointOfLastCharacter() {
		getOrCreateChildTextList();
		return childTextList.get(childTextList.size() - 1).getCentrePointOfFirstCharacter();
	}

	public Double getRadiusOfFirstCharacter() {
		getOrCreateChildTextList();
		return childTextList.get(0).getRadiusOfFirstCharacter();
	}

	public Double getRadiusOfLastCharacter() {
		getOrCreateChildTextList();
		return childTextList.get(childTextList.size() - 1).getRadiusOfFirstCharacter();
	}

	public void add(SVGText text) {
		this.appendChild(new SVGText(text));
	}

	private void ensureTextList() {
		if (childTextList == null) {
			childTextList = new ArrayList<SVGText>();
		}
	}
	
	public String toString() {
		return getStringValue();
	}

	public String getStringValue() {
		getOrCreateChildTextList();
		StringBuilder sb = new StringBuilder();
		for (SVGText text : childTextList) {
			sb.append(text.getValue());
		}
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}

	public Double getSpaceCountBetween(Word followingWord) {
		SVGText char0 = get(getCharacterCount() - 1);
		SVGText char1 = followingWord == null ? null : followingWord.get(0);
		return char1 == null || char0 == null ? null : char0.getEnSpaceCount(char1);
	}

	public Double getSeparationBetween(Word followingWord) {
		SVGText char0 = get(getCharacterCount() - 1);
		SVGText char1 = followingWord.get(0);
		return char0.getSeparation(char1);
	}

	public Integer getCharacterCount() {
		getOrCreateChildTextList();
		return childTextList.size();
	}

	public SVGText get(int index) {
		getOrCreateChildTextList();
		return index < 0 || index >= childTextList.size() ? null : childTextList.get(index);
	}

	public Double getStartX() {
		getOrCreateChildTextList();
		return childTextList.size() == 0 ? null : childTextList.get(0).getX();
	}

	/** 
	 * Gets end point of string, including width of last character.
	 * 
	 * @return
	 */
	public Double getEndX() {
		getOrCreateChildTextList();
		SVGText endText = childTextList.get(childTextList.size() - 1);
		Double x = (endText == null ? null : endText.getX());
		Double w =  (endText == null ? null : endText.getScaledWidth(guessWidth));
		return (x == null || w == null ? null : x + w);
	}

	public Double getMidX() {
		return (getStartX() + getEndX()) / 2.;
	}

	public Double translateToDouble() {
		Double d = null;
		try {
			d = Double.valueOf(toString());
		} catch (NumberFormatException e) {
			// cannot translate
		}
		return d;
	}

	public Integer translateToInteger() {
		Integer i = null;
		try {
			i = new Integer(toString());
		} catch (NumberFormatException e) {
			// cannot translate
		}
		return i;
	}

	/** 
	 * Creates a list of Words split at spaces.
	 * <p>
	 * If no spaces, returns this.
	 * 
	 * @return
	 */
	List<Word> splitAtSpaces() {
		getOrCreateChildTextList();
		List<Word> newWordList = new ArrayList<Word>();
		Word newWord = null;
		for (SVGText text : childTextList) {
			String value = text.getValue();
			LOG.trace(value);
			//is it a space?
			if (value.trim().length() == 0) {
				newWord = null;
			} else {
				if (newWord == null) {
					newWord = new Word();
					newWordList.add(newWord);
				}
				newWord.add(text);
			}
		}
		return newWordList;
	}

	/** 
	 * Creates a Phrase of Words from raw Words.
	 * 
	 * <p>Some raw Words contain explicit spaces and these can be split and recombined into a Phrase.
	 * If the word has no spaces then the Phrase contains a single Word.</p>
	 * 
	 * @return
	 */
	public Phrase createPhrase() {
		Phrase phrase = new Phrase();
		List<Word> splitWords = splitAtSpaces();
		for (SVGElement word : splitWords) {
			phrase.add(word);
		}
		return phrase;
	}

	public Real2Range getBoundingBox() {
		if (boundingBox == null){
			getOrCreateChildTextList();
			boundingBox = new Real2Range();
			RealRange xrange = getStartX() == null || getEndX() == null ? null: new RealRange(getStartX(), getEndX());
			RealRange yrange = childTextList.get(0).getBoundingBox().getYRange();
			boundingBox = xrange == null || yrange == null ? null : new Real2Range(xrange, yrange);
		}
		return boundingBox;
	}

	public RealRange getYRange() {
		getBoundingBox();
		return boundingBox.getYRange();
	}
	
	public Real2 getXY() {
		getOrCreateChildTextList();
		return (childTextList.size() == 0) ? null : childTextList.get(0).getXY();
	}

	public static List<Real2Range> createBBoxList(List<Word> wordList) {
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		for (SVGElement word : wordList) {
			bboxList.add(word.getBoundingBox());
		}
		return bboxList;
	}
	
	public Double getFontSize() {
		getOrCreateChildTextList();
		Double f = null;
		if (childTextList.size() > 0) {
			f = childTextList.get(0).getFontSize();
			for (int i = 1; i < childTextList.size(); i++) {
				Double ff = childTextList.get(i).getFontSize();
				if (ff != null) {
					f = Math.max(f,  ff);
				}
			}
		}
		return f;
	}

	public Element copyElement() {
		getOrCreateChildTextList();
		Element element = (Element) this.copy();
		for (SVGText text : childTextList) {
			element.appendChild(text.copy());
		}
		return element;
	}

	/** this requires more work as SVGText needs to access ancestors.
	 * 
	 */
	public void pullUpChildAttributes() {
		Map<String, String> attValueMap = new HashMap<String, String>();
		Set<String> liveSet = new HashSet<String>(COMMON_ATT_NAMES);

		List<Element> childList = XMLUtil.getQueryElements(this, "*");
		for (int i = 0; i < childList.size(); i++) {
			Element child = childList.get(i);
			Attribute svgxz = child.getAttribute(Path2ShapeConverter.Z_COORDINATE, SVGX_NS);
			child.removeAttribute(svgxz);
			findFullyDuplicatedAttributes(attValueMap, liveSet, child);
		}
		for (int i = 0; i < childList.size(); i++) {
			Element child = childList.get(i);
			upliftAttributes(liveSet, child);
		}

	}

	private void upliftAttributes(Set<String> liveSet, Element child) {
		for (int j = 0; j < child.getAttributeCount(); j++) {
			Attribute attribute = child.getAttribute(j);
			String name = attribute.getLocalName();
			if (liveSet.contains(name)) {
				attribute.detach();
				this.addAttribute(attribute);
			}
		}
	}

	private void findFullyDuplicatedAttributes(Map<String, String> attValueMap, Set<String> liveSet, Element child) {
		for (int j = 0; j < child.getAttributeCount(); j++) {
			Attribute att = child.getAttribute(j);
			String name = att.getLocalName();
			String value = att.getValue();
			if (liveSet.contains(name)) {
				String value1 = attValueMap.get(name);
				if (value1 == null) {
					attValueMap.put(name, value1);
				} else if (!value1.equals(value)) {
					liveSet.remove(name);
				}
			}
		}
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateChildTextList();
		for (SVGText text : childTextList) {
			Transform2 t2 = Transform2.getRotationAboutPoint(angle, centreOfRotation);
			Transform2 oldT2 = text.getTransform();
			if (oldT2 != null) {
				t2 = t2.concatenate(oldT2);
			}
			text.setTransform(t2);
			LOG.debug("T: "+text.toXML());
		}
		return;
	}
	
}
