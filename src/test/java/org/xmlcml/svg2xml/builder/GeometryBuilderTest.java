package org.xmlcml.svg2xml.builder;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;

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


}