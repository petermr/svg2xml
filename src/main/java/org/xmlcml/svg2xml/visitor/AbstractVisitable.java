package org.xmlcml.svg2xml.visitor;

import org.xmlcml.svg2xml.page.PageAnalyzer;
/** superclasses of SVG2XML visitables.
 * 
 * 
 * @author pm286
 *
 */
public abstract class AbstractVisitable implements SemanticVisitable {

	protected PageAnalyzer pageAnalyzer;
	protected SemanticVisitor semanticVisitor;

	protected SemanticVisitor getSemanticVisitor() {
		return semanticVisitor;
	}

	protected void setSemanticVisitor(SemanticVisitor semanticVisitor) {
		this.semanticVisitor = semanticVisitor;
	}

	protected PageAnalyzer getPageAnalyzer() {
		return pageAnalyzer;
	}

	protected void setPageAnalyzer(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
	}

	/** default is for immediate visitation.
	 * 
	 * @param visitor
	 */
	public void accept(AbstractVisitor visitor) {
		visitor.visit(this);
	}
}
