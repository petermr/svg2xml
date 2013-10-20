package org.xmlcml.svg2xml.visitor;

import java.util.List;

import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.page.FigureAnalyzer;


public class MathVisitor extends AbstractVisitor {

	// ===================called on Visitables===================

	@Override
	public void visit(TextVisitable textVisitable) {
		// extract equations :-)
	}

	@Override
	public void visit(TableVisitable tableVisitable) {
		// no-op - don't do math on tables
	}

	@Override
	public void visit(SemanticVisitable figureVisitable) {
		// no-op - don't do math on figures
	}

// =======================called by visitables===============
	
	@Override
	public void setTableList(List<HtmlTable> tableList) {
		// no-op - generally no math in tables
	}

	@Override
	public void setTextList(List<HtmlElement> textList) {
		// TODO Auto-generated method stub
		
	}
	
}
