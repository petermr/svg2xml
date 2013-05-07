package org.xmlcml.svg2xml.table;


import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGUtil;

/** temporary container for major chunks in a table
 * typically:
 *  - caption
 *  - header
 *  - body
 *  - footer
 * @author pm286
 *
 */
public class GenericTableChunk extends AbstractTableChunk {

	private final static Logger LOG = Logger.getLogger(GenericTableChunk.class);
	
	public GenericTableChunk() {
		
	}

}
