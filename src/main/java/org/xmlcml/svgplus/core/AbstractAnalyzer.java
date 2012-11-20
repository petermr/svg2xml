package org.xmlcml.svgplus.core;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;

public abstract class AbstractAnalyzer {
	private final static Logger LOG = Logger.getLogger(AbstractAnalyzer.class);

	protected SemanticDocumentAction semanticDocumentAction;
	protected PageAnalyzer pageAnalyzer;  // because almost all analyzers work on pages
	protected SVGSVG svgPage;             // because almost all analyzers work on <svg:svg>s
	protected SVGG svgg;                  // because almost all analyzers work on <svg:g>s

	protected AbstractAnalyzer() {
	}

	protected AbstractAnalyzer(PageAnalyzer pageAnalyzer) {
		this();
		this.pageAnalyzer = pageAnalyzer;
	}
	
	public SVGSVG getSVGPage() {
		return svgPage;
	}

	public void setSVGPage(SVGSVG svgPage) {
		this.svgPage = svgPage;
	}

//	public List<String> getNames() {
//		List<String> names = new ArrayList<String>();
//		List<String> pageNames = Arrays.asList(ensureValueMap().keySet().toArray(new String[0]));
//		names.addAll(pageNames);
//		List<String> docNames = Arrays.asList(semanticDocumentAction.getVariableMap().keySet().toArray(new String[0]));
//		names.addAll(docNames);
//		return names;
//	}
	
	public void putValue(String name, Object value) {
//		String[] names = getNameParts(name);
//		if (PageAnalyzer.NAME_PREFIX.equals(names[0]) && this instanceof PageAnalyzer) {
//			semanticDocumentAction.setVariable(names[1], value);
//		} else if (DocumentAnalyzer.NAME_PREFIX.equals(names[0])) {
//			semanticDocumentAction.setVariable(names[1], value);
//		} else {
//			throw new RuntimeException("Bad regex name / or cannot access lookup "+name);
//		}
	}

	protected SemanticDocumentAction getSemanticDocumentAction() {
		return semanticDocumentAction;
	}

	
}
