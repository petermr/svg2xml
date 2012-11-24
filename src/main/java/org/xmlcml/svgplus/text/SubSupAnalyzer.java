package org.xmlcml.svgplus.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGTSpan;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.command.AbstractPageAnalyzer;
import org.xmlcml.svgplus.tools.BoundingBoxManager;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class SubSupAnalyzer extends AbstractPageAnalyzer {

	public static final String SCRIPT_TYPE = "scriptType";

	public enum SubSup {
		SUBSCRIPT("sub", 1),
		SUPERSCRIPT("sup", -1);

		private String html;
		private int offset;
		private SubSup(String html, int offset) {
			this.html = html;
			this.offset = offset;
		}
		public String getHtml() {
			return html;
		}
	};
	
	private final static Logger LOG = Logger.getLogger(SubSupAnalyzer.class);
	private final static Double ANGLE_EPS = 0.1;
	
	private TextAnalyzer textAnalyzer;
	private List<Integer> sortedYCoords;
	private Map<Integer, List<SVGText>> textByYCoordAndXCoord;
	private int currentYCoordIndex;
	private Double superscriptSeparationMinInFonts = 0.4;
	private Double superscriptSeparationMaxInFonts = 0.8;
	private Double subscriptSeparationMinInFonts = 0.05;
	private Double subscriptSeparationMaxInFonts = 0.5;
	private Double subSupXSeparationMaxInFonts = 2.0;
	private Double maxFontRatio = 0.8;
	private Integer thisXIndex;
	private Integer otherXIndex;
	private SubSup currentSubSupType;
	private List<SVGText> texts;

	public SubSupAnalyzer(TextAnalyzer textAnalyzer) {
		this.textAnalyzer = textAnalyzer;
	}

	public void mergeTexts(List<SVGText> texts) {
		this.texts = texts;
		removeNonZeroOrientedTexts();
		createTextsIndexedByCoordinates();
		currentYCoordIndex = 0;
		for (; currentYCoordIndex < sortedYCoords.size(); currentYCoordIndex++) {
			mergePrecedingLineAsSuperScripts();
			mergeFollowingLineAsSubScripts();
		}
		for (Integer yCoord : sortedYCoords) {
			List<SVGText> thisLineTexts = textByYCoordAndXCoord.get(yCoord);
			normalizeParentageInSVG(thisLineTexts);
		}
		
		detachEmptyGs();
	}

	/** select only texts with zero rotation
	 * 
	 */
	private void removeNonZeroOrientedTexts() {
		List<SVGText> orientedTexts = new ArrayList<SVGText>();
		for (SVGText text : this.texts) {
			if (Real.isZero(text.getAngleOfRotationFromTransformInDegrees(), ANGLE_EPS)) {
				orientedTexts.add(text);
			}
		}
		this.texts = orientedTexts;
	}

	private void detachEmptyGs() {
		if (texts.size() > 0) {
			while (true) {
				List<SVGElement> emptyGs = SVGUtil.getQuerySVGElements(this.texts.get(0), "//svg:g[count(*)=0]");
				for (SVGElement emptyG : emptyGs) {
					emptyG.detach();
				}
				if (emptyGs.size() == 0) {
					break;
				}
			}
		}
	}

	private void normalizeParentageInSVG(List<SVGText> lineTexts) {
		if (lineTexts != null && lineTexts.size() > 1) {
			SVGText text0 = getFirstNonNullText(lineTexts);
			if (text0 != null) {
				ParentNode parent = text0.getParent();
				SVGText textContainer = new SVGText(text0);
				textContainer.setText(null);
				parent.appendChild(textContainer);
				for (SVGText text : lineTexts) {
					text.detach();
					SVGTSpan tSpan = new SVGTSpan();
					CMLUtil.copyAttributes(text, tSpan);
					tSpan.setText(text.getValue());
					textContainer.appendChild(tSpan);
				}
			}
		}
	}

	private void mergeFollowingLineAsSubScripts() {
		Integer nextLineCoordinate = getNextYCoord();
		if (nextLineCoordinate != null) { 
			this.currentSubSupType = SubSup.SUBSCRIPT;
			List<SVGText> thisLineTexts = textByYCoordAndXCoord.get(getThisYCoord());
			List<SVGText> nextLineTexts = textByYCoordAndXCoord.get(nextLineCoordinate);
			mergeScriptsBetweenAdjacentLines(thisLineTexts, nextLineTexts);
		}
	}

	private void mergePrecedingLineAsSuperScripts() {
		Integer previousLineCoordinate = getPreviousYCoord();
		if (previousLineCoordinate != null) { 
			this.currentSubSupType = SubSup.SUPERSCRIPT;
			List<SVGText> thisLineTexts = textByYCoordAndXCoord.get(getThisYCoord());
			List<SVGText> previousLineTexts = textByYCoordAndXCoord.get(previousLineCoordinate);
			mergeScriptsBetweenAdjacentLines(thisLineTexts, previousLineTexts);
		}
	}

	private List<SVGText> mergeScriptsBetweenAdjacentLines(List<SVGText> thisTexts, List<SVGText> otherTexts) {
		if (thisTexts == null || thisTexts.size() == 0 || 
			otherTexts == null || otherTexts.size() ==0  ) {
			return null;  // no action
		}
		Double thisYCoord = getFirstNonNullYCoord(thisTexts);
		Double otherYCoord = getFirstNonNullYCoord(otherTexts);
		Double fontSize = getFirstNonNullFontSize(thisTexts);
		Double otherSize = getFirstNonNullFontSize(otherTexts);
		if (thisYCoord == null || otherYCoord == null || fontSize == null || otherSize == null) {
			return null;
		}
		Double fontRatio = otherSize / fontSize;
		if (fontRatio > maxFontRatio) {
			return null;
		}
		Double lineSeparationInFonts = Math.abs((thisYCoord - otherYCoord) / fontSize);
		Double minSep = (SubSup.SUPERSCRIPT.equals(currentSubSupType)) ? superscriptSeparationMinInFonts : subscriptSeparationMinInFonts;
		Double maxSep = (SubSup.SUPERSCRIPT.equals(currentSubSupType)) ? superscriptSeparationMaxInFonts : subscriptSeparationMaxInFonts;
		List<SVGText> mergedList = null;
		if (lineSeparationInFonts >= minSep && lineSeparationInFonts <= maxSep && otherSize <= fontSize) {
			LOG.trace("MERGING "+currentSubSupType+getDebugString(thisTexts)+" // "+getDebugString(otherTexts));
			
			thisXIndex = 0;
			otherXIndex = 0;
			
			mergedList = mergeTextsByXCoordinate(thisTexts, otherTexts);
			// replace current line
			textByYCoordAndXCoord.put(sortedYCoords.get(currentYCoordIndex), mergedList);
		}
//		debugLineIndex();
		
		return mergedList;
	}

	private Double getFirstNonNullYCoord(List<SVGText> texts) {
		SVGText text = getFirstNonNullText(texts);
		return (text == null) ? null : text.getXY().getY();
	}

	private Double getFirstNonNullFontSize(List<SVGText> texts) {
		SVGText text = getFirstNonNullText(texts);
		return (text == null) ? null : text.getFontSize();
	}

	private SVGText getFirstNonNullText(List<SVGText> texts) {
		SVGText text = null;
		if (texts != null) {
			for (SVGText txt : texts) {
				if (txt != null) {
					text = txt;
					break;
				}
			}
		}
		return text;
	}

	private void debugLineIndex() {
		for (int coord : sortedYCoords) {
			System.out.println("Coord "+coord+": "+getDebugString(textByYCoordAndXCoord.get(coord)));
		}
	}

	private List<SVGText> mergeTextsByXCoordinate(List<SVGText> thisTexts, List<SVGText> otherTexts) {
		List<SVGText> mergedList;
		mergedList = new ArrayList<SVGText>();
		while (true) {
			if (thisXIndex == null && otherXIndex == null) {
				break;
			} else if (thisXIndex == null) {
				for (; otherXIndex < otherTexts.size(); otherXIndex++) {
					mergedList.add(markSubSup(currentSubSupType, otherTexts.get(otherXIndex)));
					otherTexts.set(otherXIndex, null);
				}
				break;
			} else if (otherXIndex == null) {
				for (; thisXIndex < thisTexts.size(); thisXIndex++) {
					SVGText thisText = thisTexts.get(thisXIndex);
					mergedList.add(markSubSup(null, thisText));
				}
				break;
			} else {
				SVGText thisText = thisTexts.get(thisXIndex);
				Double thisStartX = thisText.getXY().getX();
				Double thisEndX = thisText.getBoundingBox().getXRange().getMax();
				SVGText otherText = otherTexts.get(otherXIndex);
				Double otherStartX = otherText.getXY().getX();
				Double otherEndX = otherText.getBoundingBox().getXRange().getMax();
				if (thisStartX < otherStartX) {
					mergedList.add(markSubSup(null, thisTexts.get(thisXIndex)));
					thisXIndex = (thisXIndex < thisTexts.size()-1) ? thisXIndex+1 : null;
				} else {
					Double interTextSeparationRatio = (thisStartX - otherEndX) / otherText.getFontSize();
					mergedList.add(markSubSup(currentSubSupType, otherTexts.get(otherXIndex)));
					// remove from otherText
					otherTexts.set(otherXIndex, null); 
					otherXIndex = (otherXIndex < otherTexts.size()-1) ? otherXIndex+1 : null;
				}
			}
			
		}
		return mergedList;
	}

	public static SVGText markSubSup(SubSup subSup, SVGText svgText) {
		if (subSup != null) {
			svgText.addAttribute(new Attribute(SCRIPT_TYPE, ""+subSup));
		}
		return svgText;
	}

	private String getDebugString(List<SVGText> texts) {
		String s = null;
		if (texts != null) {
			s = "";
			for (SVGText text : texts) {
				s += " "+((text == null) ? null : text.getValue()+" || ");
			}
		}
		return s;
	}

	/** creates textByYCoordAndXCoord = new HashMap<Integer, List<SVGText>>()
	 * and returns sorted list of Y coords
	 * 
	 * @param texts
	 * @return
	 */
	private List<Integer> createTextsIndexedByCoordinates() {
		ListMultimap<Integer, SVGText> textByYCoord = ArrayListMultimap.create();
		LOG.trace("texts "+this.texts.size());
		for (SVGText text : this.texts) {
			Integer yCoord = (int) Math.round(text.getXY().getY());
			textByYCoord.put(yCoord, text);
		}
		Integer[] yCoords = textByYCoord.keySet().toArray(new Integer[0]);
		
		Arrays.sort(yCoords);
		sortedYCoords = Arrays.asList(yCoords);
		LOG.trace("sortedCoords "+sortedYCoords.size());
		textByYCoordAndXCoord = new HashMap<Integer, List<SVGText>>();
		for (Integer yCoord : yCoords) {
			List<SVGText> textsInLine = (List<SVGText>) textByYCoord.get(yCoord);
			List<SVGText> textsInLine1 = SVGText.extractTexts(BoundingBoxManager.getElementsSortedByEdge(textsInLine, BoxEdge.XMIN));
			textByYCoordAndXCoord.put(yCoord, textsInLine1);
		}
		return sortedYCoords;
	}

	private Integer getPreviousYCoord() {
		return currentYCoordIndex <= 0 || currentYCoordIndex >= sortedYCoords.size()? null : sortedYCoords.get(currentYCoordIndex-1);
	}

	private Integer getThisYCoord() {
		return currentYCoordIndex < 0 || currentYCoordIndex >= sortedYCoords.size() ? null : sortedYCoords.get(currentYCoordIndex);
	}

	private Integer getNextYCoord() {
		return (currentYCoordIndex >= sortedYCoords.size()-1 || currentYCoordIndex < -1) ? null : sortedYCoords.get(currentYCoordIndex+1);
	}
}
