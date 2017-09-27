package org.xmlcml.svg2xml.text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.text.phrase.PhraseChunk;
import org.xmlcml.graphics.svg.text.phrase.PhraseNew;
import org.xmlcml.graphics.svg.text.phrase.SusType;
import org.xmlcml.graphics.svg.text.phrase.TextChunk;

/** merges phrases which might be related as sub or superscripts
 * 
 * @author pm286
 *
 */
public class SuscriptEditor {

	private static final Logger LOG = Logger.getLogger(SuscriptEditor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private TextChunk phraseListList;
	private double minSubFontRatio = 0.4;
	private double maxSubFontRatio = 0.8;
	private double minSubOffsetRatio = 0.2;
	private double maxSubOffsetRatio = 0.8;
	private double minSuperFontRatio = 0.4;
	private double maxSuperFontRatio = 0.8;
	private double minSuperOffsetRatio = 0.2;
	private double maxSuperOffsetRatio = 0.8;
	
//	private Metrics metrics0;
//	private Metrics metrics1;
	private double yDelta;
private boolean hasSuscripts;
	
	
	public SuscriptEditor(TextChunk phraseListList) {
		setPhraseListList(phraseListList);
		setDefaults();
	}

	private void setDefaults() {
		minSubFontRatio = 0.4;
		maxSubFontRatio = 0.8;
		minSubOffsetRatio = 0.2;
		maxSubOffsetRatio = 0.8;
		minSuperFontRatio = 0.4;
		maxSuperFontRatio = 0.8;
		minSuperOffsetRatio = 0.2;
		maxSuperOffsetRatio = 0.8;
	}

	public PhraseChunk mergeSuscripts(SusType susType, PhraseChunk phraseList0, PhraseChunk phraseList1) {
		LOG.trace("Merging? "+phraseList0+" // "+phraseList1);
		Double y0 = phraseList0.getY();
		Double y1 = phraseList1.getY();
		if (y0 == null || y1 == null) {
			LOG.trace("unexpected null y0/y1");
			return null;
		}
		yDelta = y1 - y0; // always positive
		double fontRatio01 = phraseList0.getFontSize() / phraseList1.getFontSize();
		double fontRatio10 = 1.0 / fontRatio01;
		LOG.trace(yDelta + " || "+fontRatio01);
		PhraseChunk newPhraseList = null;
		LOG.trace("metrics ==="+susType+"====> "+yDelta+" / "+phraseList0.getFontSize()+" | "+fontRatio01+" ( "+minSuperFontRatio+ " - "+maxSuperFontRatio+")");
		if (SusType.SUPER.equals(susType)) {
			if (fontRatio01 < minSuperFontRatio || fontRatio01 > maxSuperFontRatio) {
				return null;
			}
			if (yDelta > phraseList1.getFontSize()) {
				return null;
			}
		}
		if (SusType.SUB.equals(susType)) {
			if (fontRatio10 < minSubFontRatio || fontRatio10 > maxSubFontRatio) {
				return null;
			}
			if (yDelta > phraseList0.getFontSize()) {
				return null;
			}
		}
		LOG.trace(susType+" \n"+phraseList0.getStringValue() + "\n"+phraseList1.getStringValue());
		newPhraseList = mergePhraseListsByIncreasingX(susType, phraseList0, phraseList1);
		if (hasSuscripts) {
			newPhraseList = joinPhraseComponents(newPhraseList);
		}
		LOG.trace(newPhraseList.toXML());
		LOG.trace(newPhraseList.getStringValue());
		return newPhraseList;
	}

	private PhraseChunk mergePhraseListsByIncreasingX(SusType susType, PhraseChunk phraseList0, PhraseChunk phraseList1) {
		PhraseChunk newPhraseList;
		int index0 = 0;
		int index1 = 0;
		newPhraseList = new PhraseChunk();
		hasSuscripts = false;
		while (true) {
			PhraseNew phrase0 = index0 >= phraseList0.size() ? null : phraseList0.get(index0);
			Double x0 = phrase0 == null ? null : phrase0.getX();
			PhraseNew phrase1 = index1 >= phraseList1.size() ? null : phraseList1.get(index1);
			Double x1 = phrase1 == null ? null : phrase1.getX();
			if (SusType.SUPER.equals(susType) && phrase0 != null) {
				phrase0.setSuscript(susType, true);
				hasSuscripts = true;
			} else if (SusType.SUB.equals(susType) && phrase1 != null) {
				phrase1.setSuscript(susType, true);
				hasSuscripts = true;
			}
			if (x0 == null) {
				if (x1 == null) {
					break;
				}
				while (index1 < phraseList1.size()) {
					newPhraseList.add(new PhraseNew(phraseList1.get(index1++)));
				}
			} else if (x1 == null) {
				while (index0 < phraseList0.size()) {
					phrase0 = phraseList0.get(index0++);
					newPhraseList.add(new PhraseNew(phrase0));
				}
			} else if (x0 < x1) {
				newPhraseList.add(new PhraseNew(phrase0));
				index0++;
			} else {
				newPhraseList.add(new PhraseNew(phrase1));
				index1++;
			}
		}
		return newPhraseList;
	}

	private PhraseChunk joinPhraseComponents(PhraseChunk phraseList) {
		if (phraseList == null || phraseList.size() < 2) {
			return phraseList;
		}
		PhraseNew lastPhrase = null;
		PhraseChunk newPhraseList = new PhraseChunk();
		for (int i = 0; i < phraseList.size(); i++) {
			PhraseNew phrase = phraseList.get(i);
			LOG.trace("PH "+phrase+"/"+phrase.hasSubscript());
			if (lastPhrase == null) {
				// 1st phrase
				lastPhrase = phrase;
			} else if (!lastPhrase.shouldAddSpaceBefore(phrase)) {
				lastPhrase.mergePhrase(phrase);
				LOG.trace("JOIN "+lastPhrase.toXML()+" => "+phrase);
			} else {
				newPhraseList.add(new PhraseNew(lastPhrase));
				lastPhrase = phrase;
			}
		}
		if (lastPhrase != null) {
			newPhraseList.add(new PhraseNew(lastPhrase));
		}
		LOG.trace("NEW "+newPhraseList);
		return newPhraseList;
	}



	public GraphicsElement mergeAll() {
		int size = phraseListList.size();
		for (int i = 0; i < size - 1;) {
			PhraseChunk phraseList0 = phraseListList.get(i);
			PhraseChunk phraseList1 = phraseListList.get(i + 1);
			LOG.trace("======================================================================\n"
					+"SUPER "+i+"/"+size+"\n"+phraseList0+"\n"+phraseList1);
			if (mergePhraseListsVertically(SusType.SUPER, i, i+1)) {
				LOG.trace("MERGED SUPER");
				size--;
			} else {
			};
			if (i < size - 1) {
				LOG.trace("======================================================================\n"
					+ "SUB "+i+"/"+size+"\n"+phraseListList.get(i)+"\n"+phraseListList.get(i + 1));
				if (mergePhraseListsVertically(SusType.SUB, i, i + 1)) {
					LOG.trace("MERGED SUB");
					size--;
				}
			}
			i++;
		}
		LOG.trace("condensed all "+phraseListList);
		return phraseListList;
	}

	private boolean mergePhraseListsVertically(SusType susType, int line0, int line1) {
		boolean merged = false;
		PhraseChunk phraseList0 = phraseListList.get(line0);
		PhraseChunk phraseList1 = phraseListList.get(line1);
		PhraseChunk newPhraseList = mergeSuscripts(susType, phraseList0, phraseList1);
		if (newPhraseList != null) {
			PhraseChunk mainPhraseList = (SusType.SUPER.equals(susType)) ? phraseList1 : phraseList0;
			PhraseChunk minorPhraseList = (SusType.SUPER.equals(susType)) ? phraseList0 : phraseList1;
			merged = phraseListList.replace(mainPhraseList, newPhraseList);
			phraseListList.remove(minorPhraseList);
		}
		return merged;
	}

	public GraphicsElement getPhraseListList() {
		return phraseListList;
	}

	public void setPhraseListList(TextChunk phraseListList) {
		this.phraseListList = phraseListList;
	}

	public double getMinSubFontRatio() {
		return minSubFontRatio;
	}

	public void setMinSubFontRatio(double minSubFontRatio) {
		this.minSubFontRatio = minSubFontRatio;
	}

	public double getMaxSubFontRatio() {
		return maxSubFontRatio;
	}

	public void setMaxSubFontRatio(double maxSubFontRatio) {
		this.maxSubFontRatio = maxSubFontRatio;
	}

	public double getMinSubOffsetRatio() {
		return minSubOffsetRatio;
	}

	public void setMinSubOffsetRatio(double minSubOffsetRatio) {
		this.minSubOffsetRatio = minSubOffsetRatio;
	}

	public double getMaxSubOffsetRatio() {
		return maxSubOffsetRatio;
	}

	public void setMaxSubOffsetRatio(double maxSubOffsetRatio) {
		this.maxSubOffsetRatio = maxSubOffsetRatio;
	}

	public double getMinSuperFontRatio() {
		return minSuperFontRatio;
	}

	public void setMinSuperFontRatio(double minSuperFontRatio) {
		this.minSuperFontRatio = minSuperFontRatio;
	}

	public double getMaxSuperFontRatio() {
		return maxSuperFontRatio;
	}

	public void setMaxSuperFontRatio(double maxSuperFontRatio) {
		this.maxSuperFontRatio = maxSuperFontRatio;
	}

	public double getMinSuperOffsetRatio() {
		return minSuperOffsetRatio;
	}

	public void setMinSuperOffsetRatio(double minSuperOffsetRatio) {
		this.minSuperOffsetRatio = minSuperOffsetRatio;
	}

	public double getMaxSuperOffsetRatio() {
		return maxSuperOffsetRatio;
	}

	public void setMaxSuperOffsetRatio(double maxSuperOffsetRatio) {
		this.maxSuperOffsetRatio = maxSuperOffsetRatio;
	}
}
