package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlUl;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.text.ScriptLine;

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
	private List<ListItem> bulletedItemList;;
	
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
		LOG.debug("LEFTIND "+sc.getLeftIndentSet());
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

	private void createNumberedOrBulletedList() {
		errorMultilineList = new ArrayList<ListItem>();
		ListItem header = null;
		ListItem footer = null;
		if (multiScriptLineList != null) {
			firstInteger = null;
			Integer currentInteger = null;
			String currentBullet = null;
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
						LOG.error("cannot find leading integer or bullet: "+multiScriptLine.toString());
						errorMultilineList.add(multiScriptLine);
					}
					continue;
				}
				if (firstInteger == null && leadingInteger != null) {
					firstInteger = leadingInteger;
					numberedItemList = new ArrayList<ListItem>();
					numberedItemList.add(multiScriptLine);
				} else if (currentBullet == null && bullet != null) {
					currentBullet = bullet;
					bulletedItemList = new ArrayList<ListItem>();
					bulletedItemList.add(multiScriptLine);
				} else if (leadingInteger != null && leadingInteger - currentInteger == 1) {
					numberedItemList.add(multiScriptLine);
				} else if (currentBullet.equals(bullet)) {
					bulletedItemList.add(multiScriptLine);
				} else if (currentInteger != null){
					LOG.error("integers not in ascending sequence: "+leadingInteger+" (last was "+currentInteger+")");
					errorMultilineList.add(multiScriptLine);
				} else if (currentBullet != null) {
					LOG.error("bullets changed: "+bullet+" (last was "+currentBullet+")");
					errorMultilineList.add(multiScriptLine);
				}
				currentInteger = leadingInteger;
				currentBullet = bullet;
				line++;
			}
			lastInteger = currentInteger;
			if (header != null) {
				LOG.debug("HEADER "+header.toString());
			}
			if (currentInteger != null) {
				LOG.debug("********************************List from: "+firstInteger+" to "+lastInteger+
						" errors "+errorMultilineList.size());
			} else if (currentBullet  != null) {
				LOG.debug("********************************List of ("+ bulletedItemList.size()+") bullets: "+currentBullet+
						" errors "+errorMultilineList.size());
			}
			if (footer != null) {
				LOG.debug("FOOTER "+footer.toString());
			}
		}
	}

	private boolean hasList() {
		return hasList ;
	}

	ListContainer createNonIndentedList() {
		LOG.debug("Skipping any non-indented lists");
		// will have to look for bullets, numbers, etc. 
		return null;
	}


	private ListContainer createComplexIndentedList() {
		LOG.debug("Skipping any complex indented lists");
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
			LOG.trace(scriptLine.getTextContentWithSpaces());
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
			} else {
				LOG.debug("Left margin: "+leftMargin);
				throw new RuntimeException("bad indent: '"+scriptLine.getTextContentWithSpaces()+"'");
			}
			multiScriptLine.add(scriptLine);
		}
		return multiScriptLineList;
	}

	@Override
	public HtmlElement createHtmlElement() {
		LOG.error("FIX the SPANS");
		HtmlElement ul = null;
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
