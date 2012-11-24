package org.xmlcml.svgplus.core;


import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.command.DocumentAction;
import org.xmlcml.svgplus.command.CurrentPage;
import org.xmlcml.svgplus.command.VariableStore;
import org.xmlcml.svgplus.text.SimpleFont;

public class SemanticDocumentAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(SemanticDocumentAction.class);

	public static final String INFILE = "infile";
	public static final String S_INFILE = SVGPlusConstants.S_DOT+INFILE;
	public static final String OUTFILE = "outfile";
	public static final String S_OUTFILE = SVGPlusConstants.S_DOT+OUTFILE;
	public static final String SEMDOC = "semdoc";
	public static final String S_SEMDOC = SVGPlusConstants.S_DOT+SEMDOC;
	
	private String semanticDocumentFilename;
	private VariableStore variableStore;
	private SimpleFont simpleFont;
	private SVGPlusConverter svgPlusConverter;
	private CurrentPage pageAnalyzer;
	
	public SemanticDocumentAction(AbstractActionElement documentActionElement) {
		super(documentActionElement);
	}
	
	public SemanticDocumentAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		if (getDebug() != null && getDebug()) {
			debugSemanticDocument();
		}
		runChildActionList();
	}

	private void debugSemanticDocument() {
		LOG.debug("DEBUG: \n"+toString());
		
	}

	public void setInfile(File infile) {
		ensureVariableStore();
		this.setVariable(S_INFILE, infile);
	}
	
	public void setOutfile(File outfile) {
		ensureVariableStore();
		this.setVariable(S_OUTFILE, outfile);
	}

	public File getInfile() {
		ensureVariableStore();
		return (File) this.getVariable(S_INFILE);
	}

	public File getOutfile() {
		ensureVariableStore();
		return (File) this.getVariable(S_OUTFILE);
	}

	private void ensureVariableStore() {
		if (this.variableStore == null) {
			this.variableStore = new VariableStore();
		}
	}

	public void setDocumentFilename(String semanticDocumentFilename) {
		this.semanticDocumentFilename = semanticDocumentFilename;
		this.setVariable(S_SEMDOC, this.semanticDocumentFilename);
	}
	
	public VariableStore getVariableStore() {
		ensureVariableStore();
		return variableStore;
	}
	
	public Object getVariable(String name) {
		ensureVariableStore();
		return variableStore.getVariable(name);
	}

	public void setVariable(String name, Object value) {
		ensureVariableStore();
		if (value == null) {
			variableStore.deleteKey(name);
		} else {
			variableStore.setVariable(name, value);
		}
	}
	
	public String getDebugString() {
		ensureVariableStore();
		return variableStore.debugString("VARIABLES");
	}

	public List<String> getVariableNames() {
		ensureVariableStore();
		return variableStore.getVariableNames();
	}

	public SimpleFont getSimpleFont() {
		return this.simpleFont;
	}

	public void setSVGPlusConverter(SVGPlusConverter svgPlusConverter) {
		this.svgPlusConverter = svgPlusConverter;
	}

	public SVGPlusConverter getSVGPlusConverter() {
		return svgPlusConverter;
	}

	public SVGSVG getSVGPage() {
		ensurePageAnalyzer();
		return pageAnalyzer.getSVGPage();
	}

	private void ensurePageAnalyzer() {
		if (pageAnalyzer == null) {
			this.pageAnalyzer = new CurrentPage();
			pageAnalyzer.setSVGPage(this.getSVGPage());
		}
	}

	public CurrentPage getPageAnalyzer() {
		ensurePageAnalyzer();
		return pageAnalyzer;
	}
	
}

