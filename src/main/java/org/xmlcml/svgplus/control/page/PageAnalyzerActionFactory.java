package org.xmlcml.svgplus.control.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.core.DocumentAnalyzer;

public class PageAnalyzerActionFactory {
	private final static Logger LOG = Logger.getLogger(PageAnalyzerActionFactory.class);

	/** action values
	 * 
	 */
	public static final String RUN_PAGE_ANALYZER = "runPageAnalyzer";

	public static final List<String> ACTIONS = new ArrayList<String>();
	
	static {
		ACTIONS.add(RUN_PAGE_ANALYZER);
	}
	
	public PageAnalyzerAction createAction(PageAnalyzerElement command, DocumentAnalyzer documentAnalyzer) {

		PageAnalyzerAction pageAnalyzerAction = null;
		String action = command.getAttributeValue(AbstractActionElement.ACTION);
		LOG.trace("action: "+action);
		if (RUN_PAGE_ANALYZER.equals(action)) {
			pageAnalyzerAction = new PageAnalyzerAction(command, documentAnalyzer);
		} else {
			throw new RuntimeException("Unknown action: "+action);
		}
		return pageAnalyzerAction;
	}


}
