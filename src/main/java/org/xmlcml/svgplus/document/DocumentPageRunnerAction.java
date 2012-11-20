package org.xmlcml.svgplus.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.core.PageAnalyzer;
import org.xmlcml.svgplus.core.SVGPlusConstants;
import org.xmlcml.svgplus.page.PageAction;
import org.xmlcml.svgplus.page.PageAnalyzerAction;
import org.xmlcml.svgplus.page.PageAnalyzerElement;
import org.xmlcml.svgplus.page.PageSelector;

public class DocumentPageRunnerAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentPageRunnerAction.class);
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(AbstractActionElement.ACTION);
		ATTNAMES.add(AbstractActionElement.FILENAME);
	}
	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	
	private AbstractActionElement documentActionCommand;
	private PageAnalyzerElement pageAnalyzerCommandElement;
	private List<PageAction> pageActions;
	
	public DocumentPageRunnerAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
		this.documentActionCommand = documentActionCommand;
		createPageActionListCommand();
	}
	
	private void createPageActionListCommand() {
		Nodes nodes0 = documentActionCommand.query("*");
		Nodes nodes = documentActionCommand.query(PageAnalyzerElement.TAG);
		if (nodes0.size() != 1 || nodes.size() != 1) {
			throw new RuntimeException(
					"pageRunner must have exactly one child of type <"+PageAnalyzerElement.TAG+">," +
							" found "+nodes0.size()+" children");
		}
		pageAnalyzerCommandElement = (PageAnalyzerElement) nodes.get(0);
//		pageActions = pageActionsCommand.getPageActions();
	}

	@Override
	public void run() {
//		List<SVGSVG> pageList = documentAnalyzer.getPageList();
//		for (int i = 0; i < pageList.size(); i++) {
//			SVGSVG svgPage = pageList.get(i);
//			System.out.println("=========== page "+i+" =============");
//			PageSelector pageSelector = documentAnalyzer.getPageSelector();
//			if (!pageSelector.isSet(i)) {
//				LOG.debug("skipping page");
//			} else {
//				PageAnalyzerAction pageAnalyzerAction = new PageAnalyzerAction(pageAnalyzerCommandElement, documentAnalyzer);
//				pageAnalyzerAction.getPageAnalyzer().putValue(PageAnalyzer.NAME_PREFIX+CMLConstants.S_PERIOD+PConstants.PAGE, ""+i);
//				pageAnalyzerAction.setSVGPage(svgPage);
//				pageAnalyzerAction.setZeroBasedPageNumber(i);
//				try {
//					pageAnalyzerAction.run();
//				} catch (Exception e) {
//					LOG.error("*******************Failed to parse page "+i+ "("+e+")");
//				}
//			}
//		}
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}

}
