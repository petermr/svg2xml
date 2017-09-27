package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.graphics.svg.SVGLine;

public class VerticalRuler extends Ruler {

	private static final Logger LOG = Logger.getLogger(VerticalRuler.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** allowed misalignment for "same X"*/
	public static final double X_TOLERANCE = 2.0;
	
	public VerticalRuler(SVGLine line) {
		super(line);
	}
	
	/** requires sorted lines.
	 * 
	 * @param lines
	 * @return
	 */
	public static List<VerticalRuler> createSortedRulersFromSVGList(List<SVGLine> lines) {
		List<VerticalRuler> rulerList = new ArrayList<VerticalRuler>();
		for (int i = 0; i < lines.size(); i++) {
			SVGLine line = lines.get(i);
			if (line.isVertical(epsilon)) {
				VerticalRuler ruler = new VerticalRuler(line);
				rulerList.add(ruler);
			}
		}
		Collections.sort(rulerList, new VerticalRulerComparator());
		return rulerList;
	}
	
	public IntRange getIntRange() {
		return new IntRange(getBoundingBox().getYRange());
	}
	
}
class VerticalRulerComparator implements Comparator<VerticalRuler> {

	public int compare(VerticalRuler vr1, VerticalRuler vr2) {
		if (vr1 == null || vr2 == null || vr1.getIntRange() == null || vr2.getIntRange() == null) {
			return 0;
		}
		if (vr1.getX() < vr2.getX()) return -1;
		if (vr1.getX() > vr2.getX()) return 1;
		return vr1.getIntRange().getMin() - vr2.getIntRange().getMin();
	}

}
