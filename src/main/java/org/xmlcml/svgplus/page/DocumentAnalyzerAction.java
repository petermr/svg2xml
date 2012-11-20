package org.xmlcml.svgplus.page;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.core.AbstractAction;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.core.SVGPlusConstants;
import org.xmlcml.svgplus.document.DocumentAction;

public class DocumentAnalyzerAction extends AbstractAction {

	private final static Logger LOG = Logger.getLogger(DocumentAnalyzerAction.class);
	
	public static final String PAGE_COUNT = SVGPlusConstants.D_DOT+"pageCount";
	private List<DocumentAction> documentActions;
	private DocumentAnalyzerElement documentAnalyzerActionCommand;
	
	public DocumentAnalyzerAction() {
//		super(pageAnalyzerActionCommand);
//		this.setDocumentAnalyzer(documentAnalyzer);
//		this.ensurePageAnalyzer(documentAnalyzer);
//		this.documentAnalyzerActionCommand = pageAnalyzerActionCommand;
	}
	
	public DocumentAnalyzerAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	

	
	public String getActionValue() {
		return getActionElement().getAttributeValue(AbstractActionElement.ACTION);
	}

	private List<DocumentAction> createDocumentActions() {
//		if (documentActions == null) {
//			DocumentActionFactory factory = new DocumentActionFactory();
//			documentActions = new ArrayList<DocumentAction>();
//			for (AbstractActionElement command : documentActionCommandElements) {
//				DocumentAction documentAction = factory.createAction(command);
//				documentAction.setDocumentAnalyzer(documentAnalyzer);
//				documentActions.add(documentAction);
//			}
//		}
		return documentActions;
	}


	public void run() {
//		documentActionCommandElements = documentAnalyzerActionCommand.getDocumentActionCommandElements();
	}



	void runActions() {
		for (AbstractAction documentAction: documentActions) {
//			PageSelector pageSelector = documentAction.getDocumentSelector();
//			if (pageSelector == null || pageSelector.isSet(zeroBasedPageNumber)) {
//				try {
//					documentAction.run();
//				} catch (RuntimeException e) {
//					throw new RuntimeException("failed on instruction "+documentAction.getActionCommandElement().toXML(), e);
//				} catch (Exception e) {
//					throw new RuntimeException("problem on instruction "+documentAction.getActionCommandElement().toXML(), e);
//				}
//			}
		}
	}
	
}
