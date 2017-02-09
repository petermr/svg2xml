package org.xmlcml.svg2xml.text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

	private PhraseListList phraseListList;
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
	
	
	public SuscriptEditor(PhraseListList phraseListList) {
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

	public PhraseList mergeSuscripts(PhraseListList phraseListList, int row0, int row1, SusType susType) {
		LOG.debug(row0 + " ?? "+row1);
		PhraseList phraseList0 = phraseListList.get(row0);
		PhraseList phraseList1 = phraseListList.get(row1);
		return mergeSuscripts(susType, phraseList0, phraseList1);
	}

	public PhraseList mergeSuscripts(SusType susType, PhraseList phraseList0, PhraseList phraseList1) {
		LOG.debug("Merging? "+phraseList0+" // "+phraseList1);
		Double y0 = phraseList0.getY();
		Double y1 = phraseList1.getY();
		if (y0 == null || y1 == null) {
			LOG.error("unexpected null y0/y1");
			return null;
		}
		yDelta = y1 - y0; // always positive
		double fontRatio01 = phraseList0.getFontSize() / phraseList1.getFontSize();
		double fontRatio10 = 1.0 / fontRatio01;
		LOG.debug(yDelta + " || "+fontRatio01);
		PhraseList newPhraseList = null;
		LOG.debug("metrics ==="+susType+"====> "+yDelta+" / "+phraseList0.getFontSize()+" | "+fontRatio01+" ( "+minSuperFontRatio+ " - "+maxSuperFontRatio+")");
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
		LOG.debug(susType+" \n"+phraseList0.getStringValue() + "\n"+phraseList1.getStringValue());
		int index0 = 0;
		int index1 = 0;
		newPhraseList = new PhraseList();
		while (true) {
			Phrase phrase0 = index0 >= phraseList0.size() ? null : phraseList0.get(index0);
			Double x0 = phrase0 == null ? null : phrase0.getX();
			Phrase phrase1 = index1 >= phraseList1.size() ? null : phraseList1.get(index1);
			Double x1 = phrase1 == null ? null : phrase1.getX();
			if (SusType.SUPER.equals(susType) && phrase0 != null) {
				phrase0.setSuscript(susType, true);
			} else if (SusType.SUB.equals(susType) && phrase1 != null) {
				phrase1.setSuscript(susType, true);
			}
			if (x0 == null) {
				if (x1 == null) {
					break;
				}
				while (index1 < phraseList1.size()) {
					newPhraseList.add(new Phrase(phraseList1.get(index1++)));
				}
			} else if (x1 == null) {
				while (index0 < phraseList0.size()) {
					phrase0 = phraseList0.get(index0++);
					newPhraseList.add(new Phrase(phrase0));
				}
			} else if (x0 < x1) {
				newPhraseList.add(new Phrase(phrase0));
				index0++;
			} else {
				newPhraseList.add(new Phrase(phrase1));
				index1++;
			}
		}
		LOG.debug(newPhraseList.toXML());
		LOG.debug(newPhraseList.getStringValue());
		return newPhraseList;
	}


	public PhraseListList mergeAll() {
		int size = phraseListList.size();
		for (int i = 0; i < size - 1;) {
			PhraseList phraseList0 = phraseListList.get(i);
			PhraseList phraseList1 = phraseListList.get(i + 1);
			LOG.debug("======================================================================\n"
					+"SUPER "+i+"/"+size+"\n"+phraseList0+"\n"+phraseList1);
			if (phraseList0.toString().contains("entorhinal")) {
				int a = 1;
				int b = a;
			}
			if (mergePhraseLists(SusType.SUPER, i, i+1)) {
				LOG.debug("MERGED SUPER");
				size--;
			} else {
//				i++;
			};
			if (i < size - 1) {
				LOG.debug("======================================================================\n"
					+ "SUB "+i+"/"+size+"\n"+phraseListList.get(i)+"\n"+phraseListList.get(i + 1));
				if (mergePhraseLists(SusType.SUB, i, i + 1)) {
					LOG.debug("MERGED SUB");
					size--;
				}
//				i++;
			}
			i++;
		}
		return phraseListList;
	}

	private boolean mergePhraseLists(SusType susType, int line0, int line1) {
		boolean merged = false;
		PhraseList phraseList0 = phraseListList.get(line0);
		PhraseList phraseList1 = phraseListList.get(line1);
		LOG.debug(line0+"???"+line1);
		PhraseList newPhraseList = mergeSuscripts(susType, phraseList0, phraseList1);
		if (newPhraseList != null) {
			PhraseList mainPhraseList = (SusType.SUPER.equals(susType)) ? phraseList1 : phraseList0;
			PhraseList minorPhraseList = (SusType.SUPER.equals(susType)) ? phraseList0 : phraseList1;
			merged = phraseListList.replace(mainPhraseList, newPhraseList);
			phraseListList.remove(minorPhraseList);
		}
		return merged;
	}

//	private Metrics getMetrics(PhraseList phraseList) {
//		Metrics metrics = new Metrics();
//		metrics.fontSize = phraseList.getFontSize();
//		metrics.y = phraseList.getY();
//		return metrics;
//	}

	public PhraseListList getPhraseListList() {
		return phraseListList;
	}

	public void setPhraseListList(PhraseListList phraseListList) {
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
//class Metrics {
//
//	public Double y;
//	public Double fontSize;
//	
//}
