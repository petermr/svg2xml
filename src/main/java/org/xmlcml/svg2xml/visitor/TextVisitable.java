package org.xmlcml.svg2xml.visitor;

import org.xmlcml.svg2xml.page.ChunkAnalyzer;

import org.xmlcml.svg2xml.page.PageAnalyzer;

public class TextVisitable extends ChunkAnalyzer implements SemanticVisitable {

	protected TextVisitable(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}

	@Override
	public void accept(SemanticVisitor v) {
	}

	protected void setPageAnalyzer(PageAnalyzer pageAnalyzer) {
	}

}
