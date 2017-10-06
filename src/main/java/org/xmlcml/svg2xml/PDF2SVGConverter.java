package org.xmlcml.svg2xml;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGSVG;

/** dummy class to enable new PDF2SVG to be slotted in.
 * 
 * @author pm286
 *
 */
public class PDF2SVGConverter {
	private static final Logger LOG = Logger.getLogger(PDF2SVGConverter.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public PDF2SVGConverter() {
		LOG.warn("PDFConverter shorted out");
	}

	public void run(String ...string) {
		LOG.warn("PDFConverter.run() shorted out");
	}

	public List<SVGSVG> getPageList() {
		LOG.warn("PDF2SVGConverter skipped");
		// TODO Auto-generated method stub
		return null;
	}
}
