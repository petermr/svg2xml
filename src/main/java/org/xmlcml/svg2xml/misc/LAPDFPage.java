package org.xmlcml.svg2xml.misc;

import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;



public class LAPDFPage extends LAPDFElement {

	private static final String PAGE_NUMBER = "pageNumber";
	private static final String CHUNK_COUNT = "chunkCount";
	private static final String WORD_COUNT = "wordCount";
	
	public final static String TAG = "Page";
	private Integer chunkCount;
	private Integer pageNumber;
	private Integer wordCount;
	
	public LAPDFPage() {
		super(TAG);
		processAttributes();
	}
	
	@Override
	protected void processAttribute(String name, String value)  {
		if (CHUNK_COUNT.equals(name)) {
			this.setChunkCount(value);
		} else if (PAGE_NUMBER.equals(name)) {
			this.setPageNumber(value);
		} else if (WORD_COUNT.equals(name)) {
			this.setWordCount(value);
		} else {
			LOG.debug("Unprocessed attribute: "+name+" in "+this.getClass());
		}
	}

	private void setChunkCount(String value) {
		this.chunkCount = new Integer(value);
	}

	private void setPageNumber(String value) {
		this.pageNumber = new Integer(value);
	}

	private void setWordCount(String value) {
		this.wordCount = new Integer(value);
	}

	protected SVGElement createSVGElement() {
		SVGElement svg = new SVGSVG();
		return svg;
	}

}
