package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.graphics.svg.SVGLine;

public class HorizontalRuler extends Ruler {

	private static final Logger LOG = Logger.getLogger(HorizontalRuler.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

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
			HorizontalRuler ruler = new HorizontalRuler(line);
			double y = line.getMidPoint().getY();
			if (lastY != null && Real.isEqual(y,  lastY, EPS)) {
				lastRuler.add(ruler);
			} else {
				lastRuler = ruler;
				rulerList.add(lastRuler);
			}
		}
		return rulerList;
	}


}
