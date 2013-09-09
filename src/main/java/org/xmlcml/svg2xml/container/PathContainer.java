package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.figure.FigureGraphic;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.PathAnalyzer;
import org.xmlcml.svg2xml.pdf.ChunkId;
import org.xmlcml.svg2xml.pdf.PDFIndex;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

public class PathContainer extends AbstractContainer  {

	public final static Logger LOG = Logger.getLogger(PathContainer.class);

	private List<SVGPath> pathList;
	
	public PathContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public PathContainer(List<SVGPath> pathList, PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
		this.add(pathList);
	}

	/** move to PathAnalyzerX
	 * 
	 * @param pageAnalyzer
	 * @param pathAnalyzer
	 * @return
	 */
	public static PathContainer createPathContainer(PageAnalyzer pageAnalyzer, PathAnalyzer pathAnalyzer) {
		PathContainer pathContainer = new PathContainer(pageAnalyzer);
		addSVGElements(pathContainer, pathAnalyzer);
		return pathContainer;
	}
	
	private static void addSVGElements(PathContainer pathContainer, PathAnalyzer pathAnalyzer) {
		List<SVGPath> pathList = pathAnalyzer.getPathList();
		if (pathList != null && pathList.size() > 0){
			pathContainer.addPathList(pathList);
		}
	}

	private void addPathList(List<SVGPath> pathList) {
		ensurePathList();
		this.pathList.addAll(pathList);
	}

	@Override
	public HtmlElement createHtmlElement() {
		htmlElement = null;
		if (svgChunk != null) {
			ChunkId chunkId = getChunkId();
			String id = chunkId == null ? String.valueOf(System.currentTimeMillis()) : chunkId.toString();
			super.createHtmlElement();
			String imageName = pageAnalyzer.getPageIO().createImageFilename(id);
			String svgName = pageAnalyzer.getPageIO().createSvgFilename(id);
			HtmlDiv div = FigureGraphic.createHtmlImgDivElement(imageName, "20%");
			htmlElement.appendChild(div);
			FigureGraphic figureGraphic = new FigureGraphic(pageAnalyzer);
			removeAnnotatedRects(svgChunk);
			figureGraphic.setSVGContainer(svgChunk);
			figureGraphic.createAndWriteImageAndSVG(imageName, div, svgName);
		} else {
			LOG.error("Null Path Chunk");
		}
		return htmlElement;
	}

	// this is a mess
	private void removeAnnotatedRects(SVGG svgChunk) {
		Nodes nodes = svgChunk.query("//*[@title='org.xmlcml.svg2xml.page.PathAnalyzer1']");
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	public List<SVGPath> getPathList() {
		return pathList;
	}

	public void add(SVGPath path) {
		ensurePathList();
	}

	private void ensurePathList() {
		if (pathList == null) {
			this.pathList = new ArrayList<SVGPath>();
		}
	}
	
	@Override
	public SVGG createSVGGChunk() {
		SVGG g = new SVGG();
		for (SVGPath path : pathList) {
			g.appendChild(path.copy());
		}
		return g;
	}
	

	@Override
	public String summaryString() {
		StringBuilder sb = new StringBuilder(">>>PathContainer>>>"+" paths: "+pathList.size()+"\n");
		for (SVGPath path : pathList) {
			sb.append(SVG2XMLUtil.trimText(20, path.getSignature())+"\n");
		}
		sb.append("<<<PathContainer<<<");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString()+"\n");
		sb.append(outputSVGList("Paths", pathList));
		return sb.toString();
	}

	public void add(List<SVGPath> pathList) {
		ensurePathList();
		this.pathList.addAll(pathList);
	}

	public void addToIndexes(PDFIndex pdfIndex) {
		String pathString = this.toString();
		pdfIndex.addToPathIndex(pathString, this);
	}
}
