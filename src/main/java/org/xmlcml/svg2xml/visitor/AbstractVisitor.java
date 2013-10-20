package org.xmlcml.svg2xml.visitor;

import java.util.List;

import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.page.FigureAnalyzer;

public abstract class AbstractVisitor implements SemanticVisitor {

	protected List<FigureAnalyzer> figureList;
	protected List<HtmlTable> tableList;
	protected List<HtmlElement> textList;

	@Override
	public void setFigureList(List<FigureAnalyzer> figureList) {
		this.figureList = figureList;
	}

	@Override
	public void setTableList(List<HtmlTable> tableList) {
		this.tableList = tableList;
	}

	@Override
	public void setTextList(List<HtmlElement> textList) {
		this.textList = textList;
	}
		

}
