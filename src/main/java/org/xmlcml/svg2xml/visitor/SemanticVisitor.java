package org.xmlcml.svg2xml.visitor;

import java.util.List;

import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.page.FigureAnalyzer;


public interface SemanticVisitor {

	void visit(TextVisitable textVisitable);
	void visit(TableVisitable tableVisitable);
	void visit(SemanticVisitable figureVisitable);
	
	void setFigureList(List<FigureAnalyzer> figureList);
	void setTableList(List<HtmlTable> tableList);
	void setTextList(List<HtmlElement> elementList);
}
