package org.xmlcml.svg2xml.page;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.PathContainer;
import org.xmlcml.svg2xml.dead.PageEditorDead;
import org.xmlcml.svg2xml.paths.Chunk;

/**
 * Analyzes paths either direct children of chunks or extracted from them.
 * <p>
 * PathAnalyzer contains not member variables except PathContainer
 * </p>
 * @author pm286
 *
 */
public class PathAnalyzer extends ChunkAnalyzer {

	public final static Logger LOG = Logger.getLogger(PathAnalyzer.class);

	private PathContainer pathContainer;
	
	public PathAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}

	public void addPathList(List<SVGPath> pathList) {
		if (pathList != null) {
			ensurePathContainer();
			pathContainer.addPathList(pathList); 
		}
	}

	private void ensurePathContainer() {
		if (this.pathContainer == null) {
			this.pathContainer = new PathContainer(this);
		}
	}

	public void setPathContainer(PathContainer pathContainer) {
		this.pathContainer = pathContainer;
	}
	
	/** a delegate accessing PathList in pathContainer.
	 * 
	 * @return pathContainer.getPathList() or null;
	 */
	public List<SVGPath> getPathList() {
		ensurePathContainer();
		return pathContainer.getPathList();
	}
	
	/** 
	 * 
	 * @param analyzerX
	 * @param suffix
	 * @param pageAnalyzer
	 * @return
	 */
	@Override
	public List<AbstractContainer> createContainers() {
		ensurePathContainer();
		ensureAbstractContainerList();
		abstractContainerList.add(pathContainer);
		return abstractContainerList;
	}

	public String toString() {
		ensurePathContainer();
		String s = "";
		s += "paths: "+pathContainer.getPathList().size();
		return s;
	}


}
