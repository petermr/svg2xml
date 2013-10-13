package org.xmlcml.svg2xml.chem;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.path.PathPrimitiveList;
import org.xmlcml.svg2xml.Fixtures;

/** test reading molecules
 * 
 * Reads SVG and uses heuristics to create chemistry.
 * 
 * @author pm286
 *
 */
public class MoleculeTest {

	@Test
	public void testRoundedLines() {
		List<SVGPath>pathList = SVGPath.extractPaths(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULE_DIR, "image.g.2.13.svg")));
		PathPrimitiveList primList = pathList.get(0).ensurePrimitives();
	}

	@Test
	public void testnonWedgeBondsAndElements() {
//		image.g.2.13.svg
	}
	
	@Test
	public void testnonWedgeBondsAndElements1() {
//		image.g.2.16.svg
	}
	
	@Test
	public void testSubscripts() {
//		image.g.2.11.svg
		
	}
	
	@Test
	public void testWedges() {
//		image.g.2.18.svg
		
	}
	
	@Test
	public void testNoRingsOrWedges() {
//		image.g.2.23.svg
		
	}
	
	@Test
	public void testHard() {
//		image.g.2.25.svg
		
	}
}
