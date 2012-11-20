package org.xmlcml.svgplus.document;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.core.SVGPlusConstants;
import org.xmlcml.svgplus.page.PageActionElement;

public class DocumentActionListAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentActionListAction.class);
	
	private DocumentActionListElement documentActionListElement;

	
	private List<AbstractActionElement> documentActionCommandElements;
	private List<DocumentAction> documentActions;
	private List<File> rawDirList;
	private String outDir;
	
	private void clearVars() {
		documentActionCommandElements = null;
		documentActions = null;
	}


	public DocumentActionListAction(AbstractActionElement documentActionCommand) {	
		super(documentActionCommand);
		this.documentActionListElement = (DocumentActionListElement) documentActionCommand;
	}


	private void ensureDocumentListElementFunctions() {
		documentActionListElement.ensurePhysicalLogicalStyleManager();
	}
	
	@Override
	public void run() {
		getDocumentAnalyzer();
		ensureDocumentListElementFunctions();
		// get documentIterator
		if (rawDirList != null) {
			for (File rawDir : rawDirList) {
				getAnalyzer().putValue(SVGPlusConstants.D_DOT+SVGPlusConstants.RAW_DIRECTORY, rawDir.getAbsolutePath());
				outDir = getOutDir();
				LOG.trace("outDir "+outDir);
				getAnalyzer().putValue(SVGPlusConstants.D_DOT+SVGPlusConstants.OUT_DIR, outDir);
				runActions(rawDir);
			}
		} else {
			runActions();
		}
	}


	private String getOutDir() {
		outDir = getAndExpand(PageActionElement.OUT_DIR, SVGPlusConstants.OUT_DIR);
		return outDir;
	}


	private void runActions(File rawDir) {
		clearVars();
		getDocumentAnalyzer();
		runActions();
	}

	private void runActions() {
		documentActionCommandElements = documentActionListElement.getDocumentActionCommandElements();
		createDocumentActions();
		LOG.trace("documentActions: "+documentActions.size());
		for (DocumentAction documentAction: documentActions) {
			documentAction.run();
		}
	}
	
	private List<DocumentAction> createDocumentActions() {
		if (documentActions == null) {
			DocumentActionFactory documentActionFactory = new DocumentActionFactory();
			documentActions = new ArrayList<DocumentAction>();
			for (AbstractActionElement command : documentActionCommandElements) {
				DocumentAction documentAction = documentActionFactory.createAction(command, documentAnalyzer);
				documentActions.add(documentAction);
			}
		}
		return documentActions;
	}

	public void setRawDirList(List<File> rawDirList) {
		this.rawDirList = rawDirList;
	}

}
