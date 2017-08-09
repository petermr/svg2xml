package org.xmlcml.svg2xml.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.linestuff.Path2ShapeConverter;
import org.xmlcml.svg2xml.SVG2XMLFixtures;

public class TableGridTest {

	private static final Logger LOG = Logger.getLogger(TableGridTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testReadRelative() throws FileNotFoundException {
		File cellFile = new File(SVG2XMLFixtures.TABLE_DIR, "els/table1top.svg");
		SVGSVG svg = (SVGSVG) SVGUtil.parseToSVGElement(new FileInputStream(cellFile)); 
		List<SVGShape> shapeList = SVGUtil.makeShapes(svg);
	}
}
