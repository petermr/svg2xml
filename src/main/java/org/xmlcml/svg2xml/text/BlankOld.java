package org.xmlcml.svg2xml.text;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;

import nu.xom.Element;

@Deprecated // moved to svg
public class BlankOld extends LineChunkOld {

	private static final Logger LOG = Logger.getLogger(BlankOld.class);
	public final static String TAG = "blank";
	
	private Real2Range boundingBox;

	public BlankOld(Real2Range bbox) {
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

	protected List<? extends LineChunkOld> getChildChunks() {
		throw new RuntimeException("not applicable");
	}

}
