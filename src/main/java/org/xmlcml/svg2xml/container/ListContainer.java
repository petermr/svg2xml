package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlUl;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.TextStructurer;

public class ListContainer extends AbstractContainer {

	public final static Logger LOG = Logger.getLogger(ListContainer.class);

	private static final double INDENT_EPS = 1.0; // lines are sometimes slightly wiggly

	private List<ListItem> multiScriptLineList = null;
	private ScriptContainer scriptContainer;
	private boolean hasList = true;

	private Integer firstInteger;
	private Integer lastInteger;
	private List<ListItem> numberedItemList;
	private List<ListItem> errorMultilineList;
	private List<ListItem> bulletedItemList;

	private String currentBullet;

	private boolean validList;

	private List<ListItem> currentList;;
	
	public ListContainer(PageAnalyzer pageAnalyzer, List<ListItem> multiScriptLineList) {
		super(pageAnalyzer);
		this.multiScriptLineList = multiScriptLineList;
//		debug();
	}

	public ListContainer(ScriptContainer sc) {
		super(sc.pageAnalyzer);
		this.scriptContainer = sc;
	}

	public void debug() {
		if (multiScriptLineList != null) {
			multiLineDebug();
		}
		if (scriptContainer != null) {
			scriptContainer.debug();
		}
	}

	private void multiLineDebug() {
		LOG.debug("MSLL "+(multiScriptLineList.size()));
		for (ListItem multiScriptLine1 : multiScriptLineList) {
			LOG.debug("MSL: ..."+multiScriptLine1.size());
			LOG.debug("LEAD INT: "+multiScriptLine1.getLeadingInteger());
			LOG.debug("BULLET: "+multiScriptLine1.getBullet());
			for (int i = 0; i < multiScriptLine1.size(); i++) {
				LOG.debug("......"+multiScriptLine1.get(i).getTextContentWithSpaces());
			}
		}
	}
	
	public static ListContainer createList(ScriptContainer sc) {
		sc.createLeftIndentSet(0);
		LOG.trace("LEFTIND "+sc.getLeftIndentSet());
		int size = sc.leftIndentSet.entrySet().size();
		ListContainer listContainer = new ListContainer(sc);
		if (size == 1) {
			listContainer.createNonIndentedList();
		} else if (size == 2) {
			listContainer.createSimpleIndentedList();
		} else {
			listContainer.createComplexIndentedList();
		}
		if (!listContainer.hasList()) {
			listContainer = null;
		} else {
			listContainer.createNumberedOrBulletedList();
		}
		return listContainer;
	}

	private List<ListItem> createNumberedOrBulletedList() {
		if (currentList == null) {
			errorMultilineList = new ArrayList<ListItem>();
			ListItem header = null;
			ListItem footer = null;
			if (multiScriptLineList != null) {
				firstInteger = null;
				Integer currentInteger = null;
				currentBullet = null;
				validList = true;
				int line = 0;
				for (ListItem multiScriptLine : multiScriptLineList) {
					Integer leadingInteger = multiScriptLine.getLeadingInteger();
					String bullet = multiScriptLine.getBullet();
					if (leadingInteger == null && bullet == null) {
						if (header == null) {
							header = multiScriptLine;
						} else if (line == multiScriptLineList.size() - 1) {
							footer = multiScriptLine;
						} else {
							LOG.trace("cannot find leading integer or bullet: "+multiScriptLine.toString());
							errorMultilineList.add(multiScriptLine);
						}
						continue;
					}
					if (firstInteger == null && leadingInteger != null) {
						addFirstNumericItem(multiScriptLine, leadingInteger);
					} else if (currentBullet == null && bullet != null) {
						addFirstBulletedItem(multiScriptLine, bullet);
					} else if (leadingInteger != null) {
						int delta = leadingInteger - currentInteger;
					    if (delta == 1) {
					    	numberedItemList.add(multiScriptLine);
					    } else {
					    	LOG.trace("List not in arithmetic progression"+leadingInteger+" (last was "+currentInteger+")");
					    	validList = false;
					    	break;
					    }
					} else if (bullet != null && bullet.equals(currentBullet)) {
						bulletedItemList.add(multiScriptLine);
					} else if (currentBullet != null) {
						LOG.trace("bullets changed: "+bullet+" (last was "+currentBullet+")");
						errorMultilineList.add(multiScriptLine);
				    	validList = false;
				    	break;
					}
					currentInteger = leadingInteger;
					currentBullet = bullet;
					line++;
				}
				createCurrentList(header, footer, currentInteger);
			}
		}
		return currentList;
	}

	private void createCurrentList(ListItem header, ListItem footer,
			Integer currentInteger) {
		if (!validList || errorMultilineList.size() > 0) {
			bulletedItemList = null;
			numberedItemList = null;
		} else {
			lastInteger = currentInteger;
			if (header != null) {
				LOG.trace("HEADER "+header.toString());
			}
			if (currentInteger != null) {
				LOG.trace("********************************List from: "+firstInteger+" to "+lastInteger);
				currentList = numberedItemList;
			} else if (currentBullet  != null) {
				LOG.trace("********************************List of ("+ bulletedItemList.size()+") bullets: "+currentBullet);
				currentList = bulletedItemList;
			}
			if (footer != null) {
				LOG.trace("FOOTER "+footer.toString());
			}
		}
	}

	private void addFirstBulletedItem(ListItem multiScriptLine, String bullet) {
		currentBullet = bullet;
		bulletedItemList = new ArrayList<ListItem>();
		bulletedItemList.add(multiScriptLine);
	}

	private void addFirstNumericItem(ListItem multiScriptLine, Integer leadingInteger) {
		firstInteger = leadingInteger;
		numberedItemList = new ArrayList<ListItem>();
		numberedItemList.add(multiScriptLine);
	}

	private boolean hasList() {
		return hasList ;
	}

	ListContainer createNonIndentedList() {
		LOG.trace("Skipping any non-indented lists");
		// will have to look for bullets, numbers, etc. 
		return null;
	}


	private ListContainer createComplexIndentedList() {
		LOG.trace("Skipping any complex indented lists");
		return null;
	}

	private List<ListItem> createSimpleIndentedList() {
		if (scriptContainer == null) return null;
		scriptContainer.createLeftIndent01();  
		Double leftIndent0 = scriptContainer.getLeftIndent0();
		Double leftIndent1 = scriptContainer.getLeftIndent1();
		LOG.trace("leftIndents: " + leftIndent0 + " / "+leftIndent1);
		multiScriptLineList = new ArrayList<ListItem>();
		ListItem multiScriptLine = null;
		Double lastIndent = null;
		for (ScriptLine scriptLine : scriptContainer) {
//			LOG.trace(scriptLine.getTextContentWithSpaces());
			Double leftMargin = scriptLine.getLeftMargin();
			if (Real.isEqual(leftMargin, leftIndent0, INDENT_EPS)) {
				multiScriptLine = new ListItem();
				multiScriptLineList.add(multiScriptLine);
				multiScriptLine.setIndented(false);
			} else if (multiScriptLine == null) { // first line
				multiScriptLine = new ListItem();
				multiScriptLineList.add(multiScriptLine);
				multiScriptLine.setIndented(true);
			} else if (Real.isEqual(leftMargin, leftIndent1, INDENT_EPS)) {
				// OK
			} else if (leftMargin == null) {
				// should this ever happen?
				LOG.error("Null Left margin: "+scriptLine.getTextContentWithSpaces()+"'");
			}
			multiScriptLine.add(scriptLine);
		}
		return multiScriptLineList;
	}

	@Override
	public HtmlUl createHtmlElement() {
		HtmlUl ul = null;
		List<ListItem> itemList = (numberedItemList != null) ? numberedItemList : null;
		itemList = (itemList != null) ? itemList : bulletedItemList;
		if (itemList != null) {
			ul = new HtmlUl();
			for (ListItem item : itemList) {
				// this should be a HtmlLi
				HtmlElement element = item.createHtmlElement();
				ul.appendChild(element);
			}
		}
		return ul;
	}
	

	@Override
	public SVGG createSVGGChunk() {
		// TODO Auto-generated method stub
		return null;
	}

}
