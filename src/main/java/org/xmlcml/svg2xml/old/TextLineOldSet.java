package org.xmlcml.svg2xml.old;

import java.util.LinkedHashSet;
import java.util.List;


/** an ordered set of TextLines.
 * usually created by extracted TextLines out of a Chunk/TextAnalyzer
 * not widely used ?obsolete
 * @author pm286
 *
 */
public class TextLineOldSet extends LinkedHashSet<TextLineOld> {

	private static final long serialVersionUID = 3049279902624745797L;
	
	public TextLineOldSet() {
	}

	public TextLineOldSet(List<TextLineOld> textLineList) {
		for (TextLineOld textLine : textLineList) {
			this.add(textLine);
		}
	}

}
