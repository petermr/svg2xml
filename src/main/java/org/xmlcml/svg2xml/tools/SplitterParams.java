package org.xmlcml.svg2xml.tools;

import org.xmlcml.svg2xml.tools.BoundingBoxManager.BoxEdge;

public class SplitterParams {

	public Double width;
	public BoxEdge boxEdge;
	
	public SplitterParams(BoxEdge boxEdge, Double width) {
		this.boxEdge = boxEdge;
		this.width = width;
	}
}
