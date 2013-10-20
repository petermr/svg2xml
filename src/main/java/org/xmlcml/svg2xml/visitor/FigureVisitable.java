package org.xmlcml.svg2xml.visitor;

import java.util.List;

import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.page.PageAnalyzer;

public class FigureVisitable extends AbstractVisitable implements SemanticVisitable {
	
	protected FigureVisitable(PageAnalyzer pageAnalyzer) {
		this.setPageAnalyzer(pageAnalyzer); 
	}

	@Override
	public void accept(SemanticVisitor visitor) {
		this.semanticVisitor = visitor;
		List<FigureAnalyzer> figureList = createFigureList(pageAnalyzer);
		visitor.setFigureList(figureList);
	}

	private List<FigureAnalyzer> createFigureList(PageAnalyzer pageAnalyzer) {
		throw new RuntimeException("FigureList NYI");
	}

}
