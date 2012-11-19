package org.xmlcml.svgplus.control;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.document.DocumentAction;
import org.xmlcml.svgplus.control.document.DocumentActionListElement;
import org.xmlcml.svgplus.control.document.DocumentIteratorAction;
import org.xmlcml.svgplus.control.document.DocumentIteratorElement;
import org.xmlcml.svgplus.core.SVGPlusConverter;

public class SemanticDocumentAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(SemanticDocumentAction.class);
	
	private static final String DEFAULT_STYLE_MANAGER = "src/main/resources/org/xmlcml/graphics/styles/bmc/styles.xml";
	private String styleManagerFile = DEFAULT_STYLE_MANAGER;
	
	private SemanticDocumentElement semanticDocumentElement;

	private String semanticDocumentFilename;
	private File infile;
	private File outfile;
	private Integer startPageNumber;
	private Integer endPageNumber;
	private Map<String, String> variableMap;
	
	public SemanticDocumentAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
		this.semanticDocumentElement = (SemanticDocumentElement) documentActionCommand;
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
		DocumentIteratorElement documentIteratorElement = semanticDocumentElement.getDocumentIteratorElement();
		if (documentIteratorElement != null) {
			documentIteratorElement.setSemanticDocumentElement(semanticDocumentElement);
			DocumentIteratorAction documentIteratorAction = documentIteratorElement.getDocumentIteratorAction();
			documentIteratorAction.run();
		} else {
			runDocumentAction();
		}
	}

	private void debugSemanticDocument() {
		LOG.debug("DEBUG: \n"+toString());
		
	}

	private void runDocumentAction() {
		DocumentActionListElement documentActionListElement = semanticDocumentElement.getDocumentActionList();
		if (documentActionListElement != null) {
			documentActionListElement.getDocumentActionListAction().run();
		}
	}
	
	public void setInfile(File infile) {
		this.infile = infile;
	}
	
	public void setOutfile(File outfile) {
		this.outfile = outfile;
	}

	public void setPages(Integer startPageNumber, Integer endPageNumber) {
		this.startPageNumber = startPageNumber;
		this.endPageNumber = endPageNumber;
	}

	public File getInfile() {
		return infile;
	}

	public File getOutfile() {
		return outfile;
	}

	public Integer getStartPageNumber() {
		return startPageNumber;
	}

	public Integer getEndPageNumber() {
		return endPageNumber;
	}
	
//	private File infile;
//	private File outfile;
//	private Integer startPageNumber;
//	private Integer endPageNumber;
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("semanticDocumentFilename: "+semanticDocumentFilename+"\n");
		sb.append("infile:                   "+infile+"\n");
		sb.append("outfile:                  "+outfile+"\n");
		sb.append("startPageNumber:          "+startPageNumber+"\n");
		sb.append("endPageNumber:            "+endPageNumber+"\n");
		sb.append(getVariablesString());
		return sb.toString();
	}

	private String getVariablesString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Variables: \n");
		if (variableMap != null) {
			for (String key : variableMap.keySet()) {
				sb.append(key+" : "+variableMap.get(key)+"\n");
			}
		}
		return sb.toString();
	}

	public void copy(Map<String, String> varMap) {
		this.variableMap = new HashMap<String, String>();
		for (String key : varMap.keySet()) {
			this.variableMap.put(key, varMap.get(key));
		}
	}

	private void ensureVariableMap() {
		if (this.variableMap == null) {
			this.variableMap = new HashMap<String, String>();
		}
	}

	public void setDocumentFilename(String semanticDocumentFilename) {
		this.semanticDocumentFilename = semanticDocumentFilename;
		this.setVariable(SVGPlusConverter.S_SEMDOC, this.semanticDocumentFilename);
	}
	
	public Map<String, String> getVariableMap() {
		ensureVariableMap();
		return variableMap;
	}
	
	public String getVariable(String name) {
		return variableMap == null ? null : variableMap.get(name);
	}

	public void setVariable(String name, String value) {
		ensureVariableMap();
		variableMap.put(name, value);
	}

	/** returns keys in sorted order
	 * 
	 */
	public List<String> getVariableNames() {
		ensureVariableMap();
		List<String> keyList = new ArrayList<String>();
		Set<String> keySet = variableMap.keySet();
		if (keySet != null && keySet.size() > 0) {
			if (!keyList.addAll(keySet)) {
				throw new RuntimeException("Cannot add keys");
			}
			Collections.sort(keyList);
		}
		return keyList;
	}
}

