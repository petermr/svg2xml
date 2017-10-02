package org.xmlcml.svg2xml.text;

import java.util.LinkedHashSet;
import java.util.List;


/** an ordered set of TextLines.
 * usually created by extracted TextLines out of a Chunk/TextAnalyzer
 * not widely used ?obsolete
 * @author pm286
 *
 */
@Deprecated // moved to SVG
public class TextLineSetOLD extends LinkedHashSet<TextLineOLD> {

	private static final long serialVersionUID = 3049279902624745797L;
	
	public TextLineSetOLD() {
	}

	public TextLineSetOLD(List<TextLineOLD> textLineList) {
		for (TextLineOLD textLine : textLineList) {
			this.add(textLine);
		}
	}

}
