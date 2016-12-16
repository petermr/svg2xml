package org.xmlcml.svg2xml.text;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;

import nu.xom.Element;

public class Blank extends LineChunk {

	private static final Logger LOG = Logger.getLogger(Blank.class);
	public final static String TAG = "blank";
	
	private Real2Range boundingBox;

	public Blank(Real2Range bbox) {
		super();
		this.setClassName(TAG);
		this.boundingBox = bbox;
	}

	@Override
	public String toString() {
		return "Blank: "+boundingBox.toString();
	}

	public Element copyElement() {
		return (Element) this.copy();
	}
	
}
