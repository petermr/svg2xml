package org.xmlcml.svg2xml.text;

import org.xmlcml.graphics.svg.SVGG;

/** chunks in a TextLine such as Phrases and Blanks
 * 
 * @author pm286
 *
 */
public abstract class LineChunk extends SVGG implements HorizontalElement {
	
	public LineChunk() {
		super();
	}
	
	public LineChunk(SVGG e) {
		super(e);
	}

}
