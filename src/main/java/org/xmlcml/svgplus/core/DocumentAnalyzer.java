package org.xmlcml.svgplus.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.control.SemanticDocumentAction;
import org.xmlcml.svgplus.control.SemanticDocumentElement;
import org.xmlcml.svgplus.control.page.PageAnalyzer;
import org.xmlcml.svgplus.control.page.PageSelector;
import org.xmlcml.svgplus.figure.Figure;
import org.xmlcml.svgplus.text.SimpleFont;
import org.xmlcml.svgplus.util.PConstants;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class DocumentAnalyzer extends AbstractAnalyzer {
	private final static Logger LOG = Logger.getLogger(DocumentAnalyzer.class);
	public static final String REPORTED_PAGE_COUNT = "reportedPageCount";
	public static final String NAME_PREFIX = PConstants.D;
	
	public enum AnalyzerType {
		CHUNK,
		CLIP,
		FONT,
	};
	
	private List<SVGSVG> svgPageList;
	private PageAnalyzer pageAnalyzer;
	private Integer firstPageNumber = null;
	private Integer lastPageNumber = null;

	private Multimap<String, PageAnalyzer> pagesByClipPath;
	private Multimap<Integer, PageAnalyzer> pagesByFontSize;
	private Map<Integer, PageAnalyzer> pageAnalyzerIndex = new HashMap<Integer, PageAnalyzer>();
	private List<Table> tableList;
	private List<Figure> figureList;
	private SemanticDocumentElement semanticDocumentElement;
	private PageSelector pageSelector;
	private File infile;

	public Multimap<String, PageAnalyzer> getPagesByClipPath() {
		return pagesByClipPath;
	}

	public SemanticDocumentElement getSemanticDocument() {
		return semanticDocumentElement;
	}

	public void setSemanticDocument(SemanticDocumentElement semanticDocument) {
		this.semanticDocumentElement = semanticDocument;
	}

	public Multimap<Integer, PageAnalyzer> getPagesByFontSize() {
		return pagesByFontSize;
	}

	public Map<Integer, PageAnalyzer> getPageInterpreterIndex() {
		return pageAnalyzerIndex;
	}

	public List<SVGSVG> getPageList() {
		return svgPageList;
	}
	
	public void setFirstPageNumber(Integer num) {
		this.firstPageNumber = (num != null) ? num : firstPageNumber;
	}

	public void setLastPageNumber(Integer num) {
		this.lastPageNumber = (num != null) ? num : lastPageNumber;
	}

	public DocumentAnalyzer() {
		LOG.trace("documentAnalyzer");
	}
	
	/**
	 * COPIES list and pages
	 * @param pageList
	 */
	public void setPageList(List<SVGSVG> pageList) {
		this.svgPageList = new ArrayList<SVGSVG>();
		for (SVGSVG page : pageList) {
			this.svgPageList.add((page == null ? null : (SVGSVG)page.copy()));
		}
		this.pageSelector = new PageSelector(svgPageList.size());
	}
	
//	private void aggregateFigures() {
//		ensureFigureList();
//		figureList.addAll(pageAnalyzer.getFigureList());
//		LOG.debug("Figures: "+figureList.size());
//	}
//
//	private void aggregateTables() {
////		ensureTableList();
////		tableList.addAll(pageAnalyzer.getTableList());
////		LOG.debug("Tables: "+tableList.size());
//	}

	private List<Figure> ensureFigureList() {
		if (figureList == null) {
			figureList = new ArrayList<Figure>();
		}
		return figureList;
	}
	
	private List<Table> ensureTableList() {
		if (tableList == null) {
			tableList = new ArrayList<Table>();
		}
		return tableList;
	}
	
	public List<Chunk> getChunkList() {
		return null;
	}


	private void writeChunks(File outputDir, List<Table> tableList, String type) {
		int i = 0;
//		for (Chunk chunk : tableList) {
//			try {
//				chunk.writeTo(outputDir, type, ++i);
//			} catch (IOException e) {
//				throw new RuntimeException("Cannot write "+type+"s");
//			}
//		}
	}

	public void setSemanticDocumentElement(SemanticDocumentElement semanticDocumentElement) {
		this.semanticDocumentElement = semanticDocumentElement;
	}

	public void runSemanticDocument() {
		semanticDocumentElement.getSemanticDocumentAction().run();
	}

	public Integer getLastPageNumber() {
		return (svgPageList == null) ? null : svgPageList.size()-1;
	}
	
	public PageSelector getPageSelector() {
		if (pageSelector == null) {
			pageSelector = new PageSelector(getLastPageNumber());
		}
		return pageSelector;
	}


	public DocumentAnalyzer getDocumentAnalyzer() {
		return this;
	}

	public String getNamePrefix() {
		return NAME_PREFIX;
	}

	public void writeDocument(File finalDir) {
		int i = 0;
		for (SVGSVG svgPage : svgPageList) {
			CMLUtil.outputQuietly(svgPage, new File(finalDir, PConstants.PAGE+(++i)+PConstants.FINAL+PConstants.SVG), 1);
		}
	}

	public SemanticDocumentAction getSemanticDocumentAction() {
		return semanticDocumentElement.getSemanticDocumentAction();
	}

	public void setInfile(File infile) {
		this.infile = infile;
	}

	public File getInfile() {
		return infile;
	}

	public SimpleFont getSimpleFont() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
