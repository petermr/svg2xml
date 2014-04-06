package org.xmlcml.svg2xml.text;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;

public class Blank extends LineChunk {

	private static final Logger LOG = Logger.getLogger(Blank.class);
	
	private Real2Range boundingBox;

	public Blank(Real2Range bbox) {
		this.boundingBox = bbox;
	}

	@Override
	public String toString() {
		return "Blank: "+boundingBox.toString();
	}

}
