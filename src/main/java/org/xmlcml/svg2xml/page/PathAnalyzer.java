package org.xmlcml.svg2xml.page;

import java.util.List;

import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.PathContainer;
import org.xmlcml.svg2xml.paths.Path2SVGInterpreter;

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
	private Path2SVGInterpreter path2SVGInterpreter;
	
	public PathAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}

	/**
	 * Construct a PathAnalyzer with descendant paths from svgElement
	 * 
	 * @param svgElement
	 * @param pageAnalyzer
	 */
	public PathAnalyzer(SVGElement svgElement, PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
		addPathList(SVGPath.extractPaths(svgElement));
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

	public void convertPathsToSVG() {
		ensurePath2SVGInterpreter();
		List<SVGElement> convertedPathList = Path2SVGInterpreter.interpretPathsAsRectCirclePolylineAndReplace(getPathList());
		pathContainer.setConvertedPathList(convertedPathList);
	}

	private void ensurePath2SVGInterpreter() {
		if (path2SVGInterpreter == null) {
			this.path2SVGInterpreter = new Path2SVGInterpreter(pathContainer.getPathList());
		}
	}

	public Path2SVGInterpreter getPath2SVGInterpreter() {
		return path2SVGInterpreter;
	}

	public PathContainer getPathContainer() {
		return pathContainer;
	}

	public List<SVGElement> getConvertedPathList() {
		return pathContainer == null ? null : pathContainer.getConvertedPathList();
	}

	/** replaces old paths with new SVGElements where they have been converted
	 * 
	 */
	public void convertPaths2SVG() {
		convertPathsToSVG();
		List<SVGElement> convertedPaths = pathContainer.getConvertedPathList();
		List<SVGPath> pathList = getPathList();
		if (convertedPaths.size() != pathList.size()){
			throw new RuntimeException("converted paths ("+convertedPaths.size()+") != old paths ("+pathList.size()+")");
		}
		for (int i = 0; i < pathList.size(); i++) {
			SVGPath oldPath = pathList.get(i);
			ParentNode parent = oldPath.getParent();
			SVGElement convertedElement = convertedPaths.get(i);
			if (convertedElement instanceof SVGPath) {
				// no need to replace as no conversion done
			} else {
				parent.replaceChild(oldPath, convertedElement);
			}
		}
	}

	public SVGElement getSVGChunk() {
		return pathContainer == null ? null : pathContainer.getSVGChunk();
	}

}
