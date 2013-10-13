package org.xmlcml.svg2xml.chem;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svg2xml.Fixtures;

/** test reading molecules
 * 
 * Reads SVG and uses heuristics to create chemistry.
 * 
 * @author pm286
 *
 */
public class MoleculeTest {

	private final static Logger LOG = Logger.getLogger(MoleculeTest.class);
	private static final Angle MAX_ANGLE = new Angle(0.02, Units.RADIANS);
	private static final Double MAX_WIDTH = 1.0;

	
	@Test
	public void testCreateLines() {
		List<SVGPath> pathList = SVGPath.extractPaths(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULE_DIR, "image.g.2.13"+".svg")));
		Assert.assertEquals("paths", 13, pathList.size());
		SVGG g = new SVGG();
		int i = 0;
		for (SVGPath path : pathList) {
			LOG.trace(path.getSignature());
			SVGPath newPath = path.replaceAllUTurnsByButt(MAX_ANGLE);
			if (newPath != null) {
				SVGLine line = newPath.createLineFromMLLLL(MAX_ANGLE, MAX_WIDTH);
				if (line != null) {
					g.appendChild(line);
				} else {
					LOG.debug("Failed line"+i);
					newPath.setFill("red");
					g.appendChild(newPath.copy());
				}
			} else {
				LOG.debug("Failed Caps"+i);
				path.setFill("blue");
				g.appendChild(path.copy());
			}
			i++;
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/moleculeLines"+"image.g.2.13"+".svg"));
	}

	@Test
	public void testnonWedgeBondsAndElements() {
		createLinesFromOutlines("image.g.2.13", MAX_WIDTH, MAX_ANGLE, 13);
	}

	
	@Test
	public void testnonWedgeBondsAndElements1() {
		createLinesFromOutlines("image.g.2.16", MAX_WIDTH, MAX_ANGLE, 20);
	}
	
	@Test
	public void testSubscripts() {
		createLinesFromOutlines("image.g.2.11", MAX_WIDTH, MAX_ANGLE, 6);
		
	}
	
	@Test
	public void testWedges() {
		createLinesFromOutlines("image.g.2.18", MAX_WIDTH, MAX_ANGLE, 21);
		
	}
	
	@Test
	public void testNoRingsOrWedges() {
		createLinesFromOutlines("image.g.2.23", MAX_WIDTH, MAX_ANGLE, 24);
		
	}
	
	@Test
	public void testHard() {
		createLinesFromOutlines("image.g.2.25", MAX_WIDTH, MAX_ANGLE, 24);
		
	}
	
	// ================= HELPERS ===============
	private static void createLinesFromOutlines(String fileRoot, Double maxWidth, Angle angleEps, int pathCount) {
		List<SVGPath> pathList = SVGPath.extractPaths(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULE_DIR, fileRoot+".svg")));
		Assert.assertEquals("paths", pathCount, pathList.size());
		SVGG g = new SVGG();
		int i = 0;
		for (SVGPath path : pathList) {
			LOG.trace(path.getSignature());
			SVGPath newPath = path.replaceAllUTurnsByButt(angleEps);
			if (newPath != null) {
				SVGLine line = newPath.createLineFromMLLLL(angleEps, maxWidth);
				if (line != null) {
					g.appendChild(line);
				} else {
					LOG.debug("Failed line"+i);
					newPath.setFill("red");
					g.appendChild(newPath.copy());
				}
			} else {
				LOG.debug("Failed Caps"+i);
				path.setFill("blue");
				g.appendChild(path.copy());
			}
			i++;
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/moleculeLines"+fileRoot+".svg"));
	}
}
