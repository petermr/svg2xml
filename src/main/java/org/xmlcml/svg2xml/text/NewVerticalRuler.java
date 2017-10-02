package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.svg2xml.old.RulerOld;

public class NewVerticalRuler extends RulerOld {

	public NewVerticalRuler(SVGLine line) {
		super(line);
	}

	/** requires sorted lines.
	 * 
	 * @param lines
	 * @return
	 */
	public static List<NewVerticalRuler> createFromSVGList(List<SVGLine> lines) {
		List<NewVerticalRuler> rulerList = new ArrayList<NewVerticalRuler>();
		SVGLine lastLine = null;
		NewVerticalRuler lastRuler = null;
		for (int i = 0; i < lines.size(); i++) {
			lastLine = i == 0 ? null : lines.get(i - 1);
			Double lastX = lastLine == null ? null : lastLine.getMidPoint().getX();
			SVGLine line = lines.get(i);
			NewVerticalRuler ruler = new NewVerticalRuler(line);
			double x = line.getMidPoint().getX();
//			if (lastX != null && Real.isEqual(x,  lastX, EPS)) {
//				lastRuler.add(ruler);
//			} else {
				lastRuler = ruler;
				rulerList.add(lastRuler);
//			}
		}
		return rulerList;
	}


}
