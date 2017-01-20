package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.Real;
import org.xmlcml.graphics.svg.SVGLine;

public class HorizontalRuler extends Ruler {

	private static final Logger LOG = Logger.getLogger(HorizontalRuler.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** allowed misalignment for "same Y"*/
	public static final double Y_TOLERANCE = 2.0;
	
	private List<Word> wordList;

	public HorizontalRuler(SVGLine line) {
		super(line);
	}
	/** requires sorted lines.
	 * 
	 * @param lines
	 * @return
	 */
	public static List<HorizontalRuler> createFromSVGList(List<SVGLine> lines) {
		List<HorizontalRuler> rulerList = new ArrayList<HorizontalRuler>();
		SVGLine lastLine = null;
		HorizontalRuler lastRuler = null;
		for (int i = 0; i < lines.size(); i++) {
			lastLine = i == 0 ? null : lines.get(i - 1);
			Double lastY = lastLine == null ? null : lastLine.getMidPoint().getY();
			SVGLine line = lines.get(i);
			LOG.trace("-----> "+line.toXML());
			HorizontalRuler ruler = new HorizontalRuler(line);
			lastRuler = ruler;
			rulerList.add(lastRuler);
		}
		return rulerList;
	}
	public IntRange getIntRange() {
		return new IntRange(getBoundingBox().getXRange());
	}
	
}
