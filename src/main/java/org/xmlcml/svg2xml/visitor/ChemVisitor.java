package org.xmlcml.svg2xml.visitor;



public class ChemVisitor extends AbstractVisitor {

	// ===================called on Visitables===================

	@Override
	public void visit(TextVisitable textVisitable) {
		// extract chemistry from text
	}

	@Override
	public void visit(TableVisitable tableVisitable) {
		// extract chemistry from table captions and cells
	}

	@Override
	public void visit(SemanticVisitable figureVisitable) {
		// extract chemistry from schemes
	}

	// =======================called by visitables===============
	
	
}