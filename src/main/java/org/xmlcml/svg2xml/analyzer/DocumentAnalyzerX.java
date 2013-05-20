package org.xmlcml.svg2xml.analyzer;

import org.xmlcml.graphics.svg.SVGG;



public class DocumentAnalyzerX implements Annotatable {
	public static final String REPORTED_PAGE_COUNT = "reportedPageCount";
	
	public SVGG annotateChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
}
