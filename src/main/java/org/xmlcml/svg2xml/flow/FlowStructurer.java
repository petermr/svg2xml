package org.xmlcml.svg2xml.flow;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.linestuff.Path2ShapeConverter;
import org.xmlcml.svg2xml.text.TextStructurer;

public class FlowStructurer {

	private TextStructurer textStructurer;
	private GraphicsElement phraseListList;

	public FlowStructurer(GraphicsElement phraseListList) {
		this.phraseListList = phraseListList;
	}

	public void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	public List<SVGShape> makeShapes() {
		GraphicsElement svgChunk = textStructurer.getSVGChunk();
		List<SVGPath> pathList = SVGPath.extractSelfAndDescendantPaths(svgChunk);
		Path2ShapeConverter converter = new Path2ShapeConverter(pathList);
		converter.setSplitPolyLines(true);
		List<SVGShape> shapeList = converter.convertPathsToShapes(pathList);
		
		return shapeList;
	}
	
	public static List<SVGRect> extractRects(List<SVGShape> shapeList) {
		List<SVGRect> rectList = new ArrayList<SVGRect>();
		for (SVGShape shape : shapeList) {
			if (shape instanceof SVGRect) {
				SVGRect rect = (SVGRect) shape;
				rectList.add(rect);
			}
		}
		return rectList;
	}
	

}
