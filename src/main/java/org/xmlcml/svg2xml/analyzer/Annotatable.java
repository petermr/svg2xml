package org.xmlcml.svg2xml.analyzer;

import org.xmlcml.graphics.svg.SVGG;

/** returns ab annotated SVGG of the object
 * 
 * @author pm286
 *
 */
public interface Annotatable {
	
	SVGG annotateChunk();

}
