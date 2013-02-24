package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.tools.Chunk;


/** a chunk of text 
 * normally many lines, including paragraph indents
 * 
 * @author pm286
 *
 */
public class TextChunk {

	private final static Logger LOG = Logger.getLogger(TextChunk.class);

	private static final String SUB = "sub";
	private static final String SUP = "sup";
	// allows for some variation in subsup offset in Y
	private static final Double SCRIPTFACTOR = 1.2;
	// allows for uncertainty on width of characters for spacing
	private static final double XTOLER = 0.8;
	// have we advanced a line?
	private static final double YTOLER = 1.0;  

	private List<WordSequence> wordSequenceList;
	private List<Paragraph> paragraphList;
	private Chunk chunk;
	public Chunk getChunk() {
		return chunk;
	}

	public TextChunk(Chunk chunk, List<WordSequence> wordSequenceList) {
		this.wordSequenceList = wordSequenceList;
		this.chunk = chunk;
	}

	public Integer size() {
		return wordSequenceList == null ? null : wordSequenceList.size();
	}
	
	public List<WordSequence> getWordSequenceList() {
		return wordSequenceList;
	}

	public WordSequence getLine(int i) {
		return wordSequenceList == null ? null : wordSequenceList.get(i); 
	}
	
	public List<Paragraph> createParagraphs() {
		paragraphList = new ArrayList<Paragraph>();
		Paragraph singleParagraph = new Paragraph();
		for (WordSequence wordSequence : wordSequenceList) {
			singleParagraph.addWordSequence(wordSequence);
		}
		paragraphList = singleParagraph.splitAtParagraphBreaks();
		for (Paragraph paragraph : paragraphList) {
			List<Word> wordList = paragraph.getWordList();
			if (wordList.size() > 0) {
				paragraph.setText(wordList.get(0).getXY());
			}
			chunk.appendChild(paragraph);
		}
		return paragraphList;
	}

	public void addSuperscriptsFrom(TextChunk scriptChunk) {
		this.mergeChunks(scriptChunk, SUP);
	}

	public boolean hasSuperscriptsIn(TextChunk scriptChunk) {
		WordSequence scriptWordSequence = scriptChunk.getLastWordSequence();
		WordSequence thisWordSequence = this.getFirstWordSequence();
		return thisWordSequence != null && scriptWordSequence != null && 
				liesInSubSupRange(thisWordSequence, scriptWordSequence, this.getSuperscriptY(), 1. / SCRIPTFACTOR);
	}

	private Double getSuperscriptY() {
		throw new RuntimeException("NYI");
	}

	private Double getSubscriptY() {
		throw new RuntimeException("NYI");
	}

	public void addSubscriptsFrom(TextChunk scriptChunk) {
		this.mergeChunks(scriptChunk, SUB);
	}

	public boolean hasSubscriptsIn(TextChunk scriptChunk) {
		WordSequence thisWordSequence = this.getLastWordSequence();
		WordSequence scriptWordSequence = scriptChunk.getFirstWordSequence();
		return liesInSubSupRange(thisWordSequence, scriptWordSequence, this.getSubscriptY(), SCRIPTFACTOR);
	}

	private boolean liesInSubSupRange(WordSequence thisWordSequence, WordSequence scriptWordSequence, Double script, double tolerance) {
		if (thisWordSequence == null || scriptWordSequence == null) {
			return false;
		}
		double deltaY = scriptWordSequence.getXY().getY() - thisWordSequence.getXY().getY();
		return script != null && (deltaY < tolerance * script && deltaY > script / tolerance);
	}

	public WordSequence getLastWordSequence() {
		return (wordSequenceList == null || wordSequenceList.size() == 0) ? null : 
			wordSequenceList.get(wordSequenceList.size()-1);
	}
		
	public WordSequence getFirstWordSequence() {
		return wordSequenceList == null || wordSequenceList.size() == 0 ? null : wordSequenceList.get(0);
	}
		
	public boolean isSuscript() {
		throw new RuntimeException("isSuscript used to depend on ChunkStyle: rewrite");
	}

	/** alternates between the two textChunks
	 * 
	 * @param otherChunk
	 */
	public void mergeChunks(TextChunk subSupChunk, String scriptType) {
		List<WordSequence> subSupList = subSupChunk.getWordSequenceList();
		Double subSupX = subSupChunk.getFirstWordSequence().getX();
		Double thisX = this.getFirstWordSequence().getX();
		Double thisY = this.getFirstWordSequence().getXY().getY();
		int subSupPos = 0;
		int thisPos = 0;
		List<WordSequence> theList = (subSupX < thisX) ? subSupList : this.wordSequenceList;
		while (subSupPos < subSupList.size() || thisPos < wordSequenceList.size()) {
			if (theList.equals(wordSequenceList)) {
				WordSequence wordSequence = wordSequenceList.get(thisPos++);
				// off end of line?
				if (wordSequence.getXY().getY() - thisY > YTOLER) {
					break;
				}
				// remove para markers
				if (Word.S_PARA.equals(wordSequence.getStringValue()) && thisPos > 1) {
					wordSequenceList.remove(--thisPos);
					continue; // skip para markers after start of line
				}
				theList = subSupList;
			} else {
				if (subSupPos < subSupList.size()) {
					WordSequence subSupSequence = subSupList.get(subSupPos);
					subSupList.remove(subSupPos);
					wordSequenceList.add(thisPos, subSupSequence);
					thisPos++;
					this.transferSequenceFrom(subSupChunk.getChunk(), subSupSequence, scriptType);
				}
				theList = wordSequenceList;
			}
		}
	}

	/**
<g clipPath="clipPath4" chunkStyle="suscript">
<text ...>
<rect ...>
<rect ...>
<g name="para">
<text style=" stroke : none;" x="513.471118845" y="557.34327503184" font-size="8.0">6</text>
</g>
</g>
	 * @param subSupSequence
	 */
	private void transferSequenceFrom(Chunk subSupChunk, WordSequence subSupSequence, String script) {
		List<SVGElement> subSupParas = SVGUtil.getQuerySVGElements(subSupChunk, ".//svg:g[@name='para']");
		SVGElement paraG = subSupParas.get(0);
		paraG.detach();
		
		// transfer the actual chunk
		this.chunk.appendChild(paraG);
		// and merge the texts
		String suText = paraG.getValue();
		List<SVGElement> thisParas = SVGUtil.getQuerySVGElements(chunk, ".//svg:g[@name='para']");
		if (thisParas.size() == 0) {
			throw new RuntimeException("Something went wrong on transferring");
		}
		SVGG para0 = (SVGG) thisParas.get(0);
		SVGText text0 = (SVGText) para0.getChildElements().get(0);
		text0.setText(text0.getText()+scriptify(suText, script));
		// concatenate any trailing text
		SVGG para1 = (thisParas.size() > 1) ? (SVGG) thisParas.get(1) : null;
		if (para1 != null) {
			String para1S = para1.getValue();
			text0.setText(text0.getText()+" "+para1S);
			para1.detach();
		}
	}

	private String scriptify(String suText, String script) {
		return "<"+script+">"+suText+"</"+script+">";
	}

	private void debugLists(List<WordSequence> suscriptList) {
		LOG.debug("SU>> "+suscriptList.size());
		for (int i = 0; i < suscriptList.size(); i++) {
			LOG.debug(suscriptList.get(i).getStringValue());
		}
		LOG.debug("This>> "+wordSequenceList.size());
		for (int i = 0; i < wordSequenceList.size(); i++) {
			LOG.debug(wordSequenceList.get(i).getStringValue());
		}
	}

	public String getSummaryString() {
		String summary = this.getFirstWordSequence().getStringValue();
		if (getWordSequenceList().size() > 1) {
			summary += " ... "+this.getLastWordSequence().getStringValue();
		}
		return summary;
	}

	public void detachChunk() {
//		chunk.detach();
	}

	public String getStringValue() {
		return chunk.getStringValue();
	}
	
	public void debug() {
		for (WordSequence wordSequence : wordSequenceList) {
			System.out.println("WS> "+wordSequence.toXML()+" "+wordSequence.getStringValue());
		}
		if (paragraphList != null) {
			for (Paragraph paragraph : paragraphList) {
				System.out.println("P> "+paragraph.toXML());
			}
		}
	}
}
