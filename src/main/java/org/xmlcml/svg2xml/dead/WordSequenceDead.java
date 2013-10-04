package org.xmlcml.svg2xml.dead;


/** implemented as a new <g> element with new child elements
 * normally represents a horizontal line of characters, grouped into words
 * @author pm286
 *
 */
public class WordSequenceDead{

//
//	private static final Logger LOG = Logger.getLogger(WordSequence.class);
//	
//	private static final String WORD_LIST = "wordList_";
//	private static final String WORD_SEQUENCE = "wordSequence";
//	private static final String WORD_CONCATENATOR = "~~";
//	private List<WordDead> wordList; // only for caching - the words are stored as child elements
//	private List<Real2Range> boundingBoxList;
//	private String stringValue;
//	
//	public WordSequence() {
//		init();
//	}
//	
//	public WordSequence(List<WordDead> words) {
//		init();
//		getWords();
//		if (wordList.size() == 0) {
//			String id = (words.size() > 0) ? words.get(0).getId() : null;
//			this.setId(WORD_LIST+id);
//			for (WordDead word : words) {
//				this.appendChild(new WordDead(word));
//				wordList.add(word);
//			}
//		}
//		
//	}
//	
//	protected void init() {
//		this.setClassName(WORD_SEQUENCE);
//	}
//	
//	public List<WordDead> getWords() {
//		if (wordList == null) {
//		    wordList = WordDead.extractWords(SVGUtil.getQuerySVGElements(this, "./svg:g[@class='"+WordDead.SVG_CLASS_NAME+"']"));
//		}
//		return wordList;
//	}
//
//	public int size() {
//		getWords();
//		return wordList.size();
//	}
//
//	public Iterator<WordDead> iterator() {
//		getWords();
//		return wordList.iterator();
//	}
//	
//	public WordDead get(int i) {
//		getWords();
//		return wordList.get(i);
//	}
//
//	public List<Real2Range> getWordBoundingBoxList() {
//		getWords();
//		if (boundingBoxList == null) {
//			boundingBoxList = new ArrayList<Real2Range>();
//			for (WordDead word : wordList) {
//				boundingBoxList.add(word.getBoundingBox());
//			}
//		}
//		return boundingBoxList;
//	}
//	
//	public Real2Range getBoundingBox() {
//		if (boundingBoxNeedsUpdating()) {
//			getWordBoundingBoxList();
//			boundingBox = new Real2Range();
//			for (Real2Range bbox : boundingBoxList) {
//				boundingBox = boundingBox.plusEquals(bbox);
//			}
//		}
//		return boundingBox;
//	}
//	
//	public void replaceCharactersByWords() {
//		
//	}
//	
//	public String getStringValue() {
//		getWords();
//		if (stringValue == null && wordList.size() > 0) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(wordList.get(0).getStringValue());
//			for (int i = 1; i < wordList.size(); i++) {
//				sb.append(WORD_CONCATENATOR);
//				sb.append(wordList.get(i).getStringValue());
//				LOG.trace(">> "+wordList.get(i));
//			}
//			stringValue = sb.toString();
//		}
//		return stringValue;
//	}
//
//	public CMLArray createCMLArray() {
//		getWords();
//		CMLArray array = null;
//		String[] ss = new String[ wordList.size()];
//		for (int i = 0 /** why 1 */; i < wordList.size(); i++) {
//			WordDead word = wordList.get(i);
//			ss[i] = word.getStringValue();
//			LOG.trace("WORD "+word.getId());
//		}
//		try {
//			IntArray intArray = new IntArray(ss);
//			if (intArray != null) {
//				array = new CMLArray(intArray.getArray());
//			}
//		} catch (Exception e) {/*integer parse failed*/ }
//		if (array == null) {
//			try {
//				RealArray realArray = new RealArray(ss);
//				if (realArray != null) {
//					array = new CMLArray(realArray.getArray());
//				}
//			} catch (Exception e1) {/*double parse failed*/}
//		}
//		if (array == null) {
//			try {
//				array = new CMLArray(ss);
//			} catch (Exception e2) {
//				LOG.debug("Array problem: "+ e2);
//			}
//		}
//		return array;
//	}
//
//	public WordDead getLastWord() {
//		getWords();
//		return (wordList == null || wordList.size() == 0) ? null : wordList.get(wordList.size()-1);
//	}
//
//	public static WordSequence createParagraphMarker() {
//		List<WordDead> words = new ArrayList<WordDead>();
//		words.add(WordDead.PARA);
//		WordSequence paragraphMarker = new WordSequence(words);
//		return paragraphMarker;
//	}
//	
//	public SVGText createSVGText() {
//		SVGText svgText = new SVGText(new Real2(this.getX(), this.getY()), this.getStringValue());
//		svgText.setId(this.getId());
//		return svgText;
//	}
//
//	public void addAsSibling(HasDataType hasDataType) {
//		getWords();
//		WordDead word = wordList.get(0);
//		SVGText wordText = (word == null) ? null : word.getSVGText();
//		SVGElement textParent = (SVGElement) ((wordText == null) ? null : wordText.getParent());
//		textParent.appendChild((Element) hasDataType);
//	}
}
