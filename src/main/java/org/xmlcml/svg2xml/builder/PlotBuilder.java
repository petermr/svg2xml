package org.xmlcml.svg2xml.builder;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.builder.SimpleBuilder;
import org.xmlcml.graphics.svg.path.Path2ShapeConverter;

public class PlotBuilder extends SimpleBuilder {

	public PlotBuilder() {
		
	}

	public PlotBuilder(SVGElement svgElement) {
		super(svgElement);
		init();
	}
	
	private void init() {
	}


	public void extractPlotComponents() {
		List<SVGPath> pathList = SVGPath.extractPaths(svgRoot);
		Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
		List<SVGShape> shapeList = path2ShapeConverter.convertPathsToShapes(pathList);
		List<SVGPolygon>  polygonList = new ArrayList<SVGPolygon>();
		List<SVGPolyline>  polylineList = new ArrayList<SVGPolyline>();
		List<SVGLine>  lineList = new ArrayList<SVGLine>();
		List<SVGShape>  unclassifiedShapeList = new ArrayList<SVGShape>();
		for (SVGShape shape : shapeList) {
			if (shape instanceof SVGPolyline) {
				polylineList.add((SVGPolyline)shape);
			} else if (shape instanceof SVGLine) {
				lineList.add((SVGLine)shape);
			} else if (shape instanceof SVGPolygon) {
				SVGPolygon polygon = (SVGPolygon)shape;
				SVGCircle circle =  path2ShapeConverter.convertToCircle(polygon);
				polygonList.add((SVGPolygon)shape);
			} else {
				unclassifiedShapeList.add(shape);
			}
		}
	}
}
