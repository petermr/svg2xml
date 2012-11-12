package org.xmlcml.svgplus.core;

import org.xmlcml.svgplus.control.page.PageAnalyzer;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;

public abstract class AbstractSVGAnalyzer {

	protected SVGSVG svgPage;
	protected PageAnalyzer pageAnalyzer;
	protected SVGG svgg;
	
	public AbstractSVGAnalyzer() {
	}

	public AbstractSVGAnalyzer(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
		this.svgPage = pageAnalyzer.getSVGPage();
	}

	public void setSVGPage(SVGSVG svgPage) {
		this.svgPage = svgPage;
	}

	public SVGSVG getSVGPage() {
		return svgPage;
	}

	public AbstractAnalyzer getPageAnalyzer() {
		return pageAnalyzer;
	}

}
