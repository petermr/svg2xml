package org.xmlcml.svg2xml.text;

import org.xmlcml.graphics.svg.SVGElement;

/** chunks in a TextLine such as Phrases and Blanks
 * 
 * @author pm286
 *
 */
public abstract class LineChunk extends SVGElement {
	
	public LineChunk(String tag) {
		super(tag);
	}

}
