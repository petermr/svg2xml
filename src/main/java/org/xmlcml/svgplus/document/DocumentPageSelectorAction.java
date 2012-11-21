package org.xmlcml.svgplus.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.page.PageActionElement;
import org.xmlcml.svgplus.page.tools.PageSelector;

public class DocumentPageSelectorAction extends DocumentAction {

	public final static Logger LOG = Logger.getLogger(DocumentPageSelectorAction.class);
	private static final String ALL = "0-";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(AbstractActionElement.ACTION);
		ATTNAMES.add(PageActionElement.PAGE_RANGE);
	}
	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	public DocumentPageSelectorAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
	}
	
	//  e.g. "4,5,8-9 12-"  or "first" or "notFirst" or "last" or "notLast" or "notFirstOrLast"
	@Override
	public void run() {
//		PageSelector pageSelector = documentAnalyzer.getPageSelector(); 
//		String range = getActionCommandElement().getAttributeValue(PageActionElement.PAGE_RANGE);
//		range = range == null ? ALL : range.trim();
//		if (PageSelector.FIRST.equals(range)) {
//			pageSelector.setFirst();
//		} else if (PageSelector.NOT_FIRST.equals(range)) {
//			pageSelector.setNotFirst();
//		} else if (PageSelector.LAST.equals(range)) {
//			pageSelector.setLast();
//		} else if (PageSelector.NOT_LAST.equals(range)) {
//			pageSelector.setNotLast();
//		} else if (PageSelector.NOT_FIRST_OR_LAST.equals(range)) {
//			pageSelector.setNotFirstOrLast();
//		} else {
//		pageSelector.decodeRangeAndSetSelected(range);			
//		}
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
				PageActionElement.PAGE_RANGE,
		});
	}

}
