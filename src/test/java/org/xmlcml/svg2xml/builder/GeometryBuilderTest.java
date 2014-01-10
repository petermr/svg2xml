package org.xmlcml.svg2xml.builder;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.svg2xml.Fixtures;

/** 
 * Test reading of molecules.
 * <p>
 * Reads SVG and uses heuristics to create chemistry.
 * 
 * @author pm286
 */

//FIXME think the raw material (rounded lines) causes problems and we should have simpler tests
public class GeometryBuilderTest {

	private final static Logger LOG = Logger.getLogger(GeometryBuilderTest.class);
	public static final Angle MAX_ANGLE = new Angle(0.12, Units.RADIANS);
	public static final Double MAX_WIDTH = 2.0;

	@Test
	@Ignore
	public void testWords() {
		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.IMAGE_2_11_SVG, "bloom-203-6-page3small.svg")));
		List<SVGPath> pathList = SVGPath.extractPaths(geometryBuilder.getSVGRoot());
		Assert.assertEquals("paths", 36, pathList.size());
	}

	@Test
	public void testPaths() {
		GeometryBuilder geometryBuilder = new GeometryBuilder(SVGElement.readAndCreateSVG(
				new File(Fixtures.BUILDER_DIR, "bloom-203-6-page3small.svg")));
		List<SVGPath> pathList = SVGPath.extractPaths(geometryBuilder.getSVGRoot());
		Assert.assertEquals("paths", 36, pathList.size());
	}
	
	@Test
	@Ignore
	public void testShape() {
		SVGElement svg = SVGElement.readAndCreateSVG(new File(Fixtures.BUILDER_DIR, "bloom-203-6-page3small.svg"));
		GeometryBuilder geometryBuilder = new GeometryBuilder(svg);
		//geometryBuilder.extractPlotComponents();
		geometryBuilder.createDerivedPrimitives();
		List<SVGPolygon> polygonList = geometryBuilder.getRawPrimitives().getPolygonList();
		List<SVGLine> lineList = geometryBuilder.getRawPrimitives().getLineList();
		List<SVGShape> shapeList = geometryBuilder.getRawPrimitives().getShapeList();
		List<SVGPolyline> polylineList = geometryBuilder.getRawPrimitives().getPolylineList();
		List<SVGPath> pathList = geometryBuilder.getRawPrimitives().getPathList();
		Assert.assertEquals("paths", 36, pathList.size());
		File file = new File("target/");
		file.mkdirs();
		SVGSVG.wrapAndWriteAsSVG(svg, new File("target/astro.svg"));
		//Assert.assertEquals("shapes", 0, shapeList.size());
		Assert.assertEquals("lines", 6, lineList.size());
		for (SVGLine line : lineList) {
			LOG.debug("line: "+line);
		}
		Assert.assertEquals("polylines", 19, polylineList.size());
		for (SVGPolyline polyline : polylineList) {
			polyline.format(2);
			LOG.debug("polyLine: "+polyline.size()+" "+polyline.getBoundingBox().getXRange().getRange()+"/"+polyline.getBoundingBox().getYRange().getRange());
		}
		System.out.println();
		Assert.assertEquals("polygons", 11, polygonList.size());
		for (SVGPolygon polygon : polygonList) {
			polygon.format(2);
			LOG.debug("polygon: "+polygon.size()+" "+polygon.getBoundingBox().getXRange().getRange()+"/"+polygon.getBoundingBox().getYRange().getRange());
		}
	}

}