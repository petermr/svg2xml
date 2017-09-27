package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.rule.horizontal.HorizontalRuleNew;
import org.xmlcml.graphics.svg.text.phrase.PhraseNew;

/** supports a column group within the TableHeader.
 * Usually denoted by a horizontal ruler with text above
 * Maybe will map onto W3C <colgroup>
 * 
 * @author pm286
 *
 */
public class ColumnGroup {

	private static final Logger LOG = Logger.getLogger(ColumnGroup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<PhraseNew> phrases;
	private HorizontalRuleNew ruler;
	private Real2Range boundingBox;
	
	public void add(PhraseNew phrase) {
		getOrCreatePhrases();
		phrases.add(phrase);
		Real2Range bbox = phrase.getBoundingBox();
		boundingBox = boundingBox == null ? bbox : boundingBox.plus(bbox);
	}

	private List<PhraseNew> getOrCreatePhrases() {
		if (phrases == null) {
			phrases = new ArrayList<PhraseNew>();
		}
		return phrases;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (PhraseNew phrase : phrases) {
			sb.append(">p> "+String.valueOf(phrase)+"\n");
		}
		sb.append("==="+String.valueOf(ruler)+"===");
		return sb.toString();
	}

	public List<PhraseNew> getPhrases() {
		return phrases;
	}

	public HorizontalRuleNew getRuler() {
		return ruler;
	}

	public void add(HorizontalRuleNew ruler) {
		if (this.ruler != null) {
//			LOG.warn("Existing ruler will be overwritten");
		}
		this.ruler = ruler;
		Real2Range bbox = ((SVGElement)ruler).getBoundingBox();
		boundingBox = boundingBox == null ? bbox : boundingBox.plus(bbox);
	}

	public Real2Range getBoundingBox() {
		return boundingBox;
	}

}
