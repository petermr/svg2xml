package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.analyzer.PDFIndex;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.analyzer.PathAnalyzerX;

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
	public static PathContainer createPathContainer(PageAnalyzer pageAnalyzer, PathAnalyzerX pathAnalyzer) {
		PathContainer pathContainer = new PathContainer(pageAnalyzer);
		addSVGElements(pathContainer, pathAnalyzer);
		return pathContainer;
	}
	
	private static void addSVGElements(PathContainer pathContainer, PathAnalyzerX pathAnalyzer) {
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
		HtmlDiv divElement = new HtmlDiv();
		HtmlP p = new HtmlP();
		p.appendChild("Path NYI probaly to SVG");
		divElement.appendChild(p);
		return divElement;
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
