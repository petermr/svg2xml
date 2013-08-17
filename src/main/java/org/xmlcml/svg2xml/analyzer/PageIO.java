package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.action.SVGPlusConstantsX;

public class PageIO {

	private static final Logger LOG = Logger.getLogger(PageIO.class);
	private static final double WIDTH = 600.0;
	private static final double HEIGHT = 800.0;
	public static final String PAGE = "page";
	public static final String CHUNK = "chunk";
	public static final String DOT_HTML = ".html";
	public static final String DOT_SVG = ".svg";

	private List<SVGG> whitespaceSVGChunkList;
	
	private int humanPageNumber;
	private int machinePageNumber = 0;
	private File rawSVGDocumentDir;
	private File finalSVGDocumentDir;
	private SVGSVG finalSVGPage;
	private SVGSVG rawSVGPage;
	private int aggregatedCount;
	
	public PageIO() {
		
	}
	
	public int getHumanPageNumber() {
		return humanPageNumber;
	}

	public void setHumanPageNumber(int humanPageNumber) {
		this.humanPageNumber = humanPageNumber;
	}

	public File getRawSVGDocumentDir() {
		return rawSVGDocumentDir;
	}

	public void setRawSVGDocumentDir(File rawSVGDocumentDir) {
		this.rawSVGDocumentDir = rawSVGDocumentDir;
	}

	private File createDefaultFinalDocumentDir() {
		if (rawSVGDocumentDir != null) {
			String name = rawSVGDocumentDir.getName();
			setFinalSVGDocumentDir(new File(PDFAnalyzerIO.OUTPUT_DIR, name));
			finalSVGDocumentDir.mkdirs();
		}
		return finalSVGDocumentDir;
	}

	public File getFinalSVGDocumentDir() {
		return (finalSVGDocumentDir == null) ? createDefaultFinalDocumentDir() : finalSVGDocumentDir;
	}

	public void setFinalSVGDocumentDir(File finalSVGDocumentDir) {
		this.finalSVGDocumentDir = finalSVGDocumentDir;
	}

	public SVGSVG getFinalSVGPage() {
		return finalSVGPage;
	}

	public void setFinalSVGPage(SVGSVG finalSVGPage) {
		this.finalSVGPage = finalSVGPage;
	}

	public SVGSVG getRawSVGPage() {
		return rawSVGPage;
	}

	public void setRawSVGPage(SVGSVG rawSVGPage) {
		this.rawSVGPage = rawSVGPage;
	}

	public List<SVGG> getWhitespaceSVGChunkList() {
		return whitespaceSVGChunkList;
	}

	public int getMachinePageNumber() {
		return machinePageNumber;
	}

	public void add(SVGG gOut) {
		ensureGoutList();
		whitespaceSVGChunkList.add(gOut);
	}

	private void ensureGoutList() {
		if (whitespaceSVGChunkList == null) {
			whitespaceSVGChunkList = new ArrayList<SVGG>();
		}
		
	}

	SVGSVG createBlankSVGOutPageWithNumberAndSize() {
		finalSVGPage = new SVGSVG();
		finalSVGPage.setWidth(WIDTH);
		finalSVGPage.setHeight(HEIGHT);
		String pageId = "p."+humanPageNumber;
		finalSVGPage.setId(pageId);
		return finalSVGPage;
	}

	void writeRawSVGPageToFinalDirectory() {
		try {
			String pageRoot = createPageRootWithHumanNumber();
			finalSVGDocumentDir.mkdirs();
			String id = rawSVGPage.getId();
			LOG.trace("ID "+id);
			CMLUtil.debug(
				rawSVGPage, new FileOutputStream(new File(finalSVGDocumentDir, pageRoot+SVGPlusConstantsX.DOT_SVG)), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	String createPageRootWithHumanNumber() {
		return PAGE+(machinePageNumber+1);
	}

	public void setMachinePageNumber(int pageNumber) {
		this.machinePageNumber = pageNumber;
		this.setHumanPageNumber(machinePageNumber+1);
	}

	public void setSvgInPage(SVGSVG svgPage) {
		this.rawSVGPage = svgPage;
	}

	public void setSvgOutPage(SVGSVG svgPage) {
		this.finalSVGPage= svgPage;
	}

	public void ensureWhitespaceSVGChunkList() {
		if (whitespaceSVGChunkList == null) {
			whitespaceSVGChunkList = new ArrayList<SVGG>();
		}
	}

	public void createFinalSVGPageFromChunks() {
		for (SVGG g : whitespaceSVGChunkList) {
			finalSVGPage.appendChild(g);
		}
	}
	
	/*
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" whitespaceChunkList "+(whitespaceSVGChunkList == null ? null : whitespaceSVGChunkList.size())+"; ");
		sb.append(" humanPageNumber "+humanPageNumber+"; ");
		sb.append(" machinePageNumber "+machinePageNumber+"; ");
		sb.append(" rawSVGDocumentDir "+getRawSVGDocumentDir()+"; ");
		sb.append(" finalSVGDocumentDir "+getFinalSVGDocumentDir()+"; ");
		sb.append(" rawSVGPage "+rawSVGPage+"; ");
		sb.append(" finalSVGPage "+finalSVGPage+"; ");
		return sb.toString();
	}

	public int getAggregatedCount() {
		return aggregatedCount;
	}

	public void setAggregatedCount(int count) {
		aggregatedCount = count;
	}

	public File createChunkFile(String chunkId) {
		File file = new File(finalSVGDocumentDir, CHUNK+"."+chunkId+DOT_SVG);
		return file;
	}

	public File createHtmlChunkFile(String chunkId) {
		File file = new File(finalSVGDocumentDir, CHUNK+"."+chunkId+DOT_HTML);
		return file;
	}

	public void outputHtmlChunk(HtmlElement div) {
		String id = div.getId();
		File htmlFile = createHtmlChunkFile(id);
		outputFile( div, htmlFile);
	}

	public static void outputFile(Element element, File file) {
		try {
			CMLUtil.debug(element, new FileOutputStream(file), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file: "+file, e);
		}
	}


}
