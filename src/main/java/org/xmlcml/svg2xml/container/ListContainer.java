package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.text.ScriptLine;

public class ListContainer extends AbstractContainer {

	public final static Logger LOG = Logger.getLogger(ListContainer.class);

	private static final double INDENT_EPS = 0.1;

	private List<MultiScriptLine> multiScriptLineList = null;
	private ScriptContainer scriptContainer;
	private boolean hasList = true;;
	
	public ListContainer(PageAnalyzer pageAnalyzer, List<MultiScriptLine> multiScriptLineList) {
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
			scriptContainerDebug();
		}
	}

	private void scriptContainerDebug() {
		LOG.debug("scriptContainerDebug NYI");
	}

	private void multiLineDebug() {
		LOG.debug("MSLL "+(multiScriptLineList.size()));
		for (MultiScriptLine multiScriptLine1 : multiScriptLineList) {
			LOG.debug("MSL: ..."+multiScriptLine1.size());
			LOG.debug("LEAD INT: "+multiScriptLine1.getLeadingInteger());
			LOG.debug("BULLET: "+multiScriptLine1.getBullet());
			for (int i = 0; i < multiScriptLine1.size(); i++) {
				LOG.debug("......"+multiScriptLine1.get(i));
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
		}
		return listContainer;
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

	private ListContainer createSimpleIndentedList() {
		if (scriptContainer == null) return null;
		scriptContainer.createLeftIndent01();  
		Double leftIndent0 = scriptContainer.getLeftIndent0();
		Double leftIndent1 = scriptContainer.getLeftIndent1();
		LOG.trace("leftIndents: " + leftIndent0 + " / "+leftIndent1);
		List<MultiScriptLine> multiScriptLineList = new ArrayList<MultiScriptLine>();
		MultiScriptLine multiScriptLine = null;
		Double lastIndent = null;
		for (ScriptLine scriptLine : scriptContainer) {
			Double leftMargin = scriptLine.getLeftMargin();
			if (Real.isEqual(leftMargin, leftIndent0, INDENT_EPS)) {
				multiScriptLine = new MultiScriptLine();
				multiScriptLineList.add(multiScriptLine);
				multiScriptLine.setIndented(false);
			} else if (multiScriptLine == null) { // first line
				multiScriptLine = new MultiScriptLine();
				multiScriptLineList.add(multiScriptLine);
				multiScriptLine.setIndented(true);
			} else if (Real.isEqual(leftMargin, leftIndent1, INDENT_EPS)) {
				// OK
			} else {
				throw new RuntimeException("bad indent: '"+scriptLine+"'");
			}
			multiScriptLine.add(scriptLine);
		}
		ListContainer listContainer = (multiScriptLine == null) ? 
				null : new ListContainer(this.pageAnalyzer, multiScriptLineList);
		if (listContainer != null) {
			listContainer.hasList = true;
//			listContainer.debug();
		}
		return listContainer;
	}


	@Override
	public SVGG createSVGGChunk() {
		// TODO Auto-generated method stub
		return null;
	}

}
