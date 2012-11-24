package org.xmlcml.svgplus.command;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;

public abstract class AbstractPageAnalyzer {
	
	private final static Logger LOG = Logger.getLogger(AbstractPageAnalyzer.class);

	protected CurrentPage currentPage;
	protected SVGG svgg; // current svg:gelement
	
	protected AbstractPageAnalyzer() {
	}

	protected AbstractPageAnalyzer(CurrentPage currentPage) {
		this();
		this.currentPage = currentPage;
	}
	
	public CurrentPage getCurrentPage() {
		return currentPage;
	}
	
	public SVGSVG getSVGPage() {
		return currentPage.getSVGPage();
	}

}
