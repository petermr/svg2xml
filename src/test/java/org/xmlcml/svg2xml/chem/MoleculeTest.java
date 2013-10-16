package org.xmlcml.svg2xml.chem;

import java.io.File;
import java.util.ArrayList;
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
import org.xmlcml.graphics.svg.path.TramLine;
import org.xmlcml.graphics.svg.path.TramLineManager;
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
	private static final Angle MAX_ANGLE = new Angle(0.12, Units.RADIANS);
	private static final Double MAX_WIDTH = 2.0;

	
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
	
	@Test
	public void testWithElementPNG() {
		createLinesFromOutlines("image.g.5.11", MAX_WIDTH, MAX_ANGLE, 48);
		createLinesFromOutlines("image.g.5.12", MAX_WIDTH, MAX_ANGLE, 51);
		createLinesFromOutlines("image.g.5.13", MAX_WIDTH, MAX_ANGLE, 88);
		createLinesFromOutlines("image.g.5.14", MAX_WIDTH, MAX_ANGLE, 95);
		
	}
	
	@Test
	public void testTramLines() {
		List<SVGLine> lineList = createLinesFromOutlines("image.g.2.13", MAX_WIDTH, MAX_ANGLE, 13);
		Assert.assertEquals("linelist", 13, lineList.size());
		TramLineManager tramLineManager = new TramLineManager();
		List<TramLine> tramLineList = tramLineManager.makeTramLineList(lineList);
		Assert.assertEquals("tramLines", 3, tramLineList.size());

	}
	
	@Test
	public void testMoreTramLines() {
		tramLineTester("image.g.2.13", MAX_WIDTH, MAX_ANGLE, 13, 3);
		tramLineTester("image.g.2.11", MAX_WIDTH, MAX_ANGLE, 6, 1);
		tramLineTester("image.g.2.18", MAX_WIDTH, MAX_ANGLE, 21, 5);
		tramLineTester("image.g.2.23", MAX_WIDTH, MAX_ANGLE, 24, 4);
		tramLineTester("image.g.2.25", MAX_WIDTH, MAX_ANGLE, 24, 5);
		tramLineTester("image.g.5.11", MAX_WIDTH, MAX_ANGLE, 48, 4);
		tramLineTester("image.g.5.12", MAX_WIDTH, MAX_ANGLE, 51, 6);
		tramLineTester("image.g.5.13", MAX_WIDTH, MAX_ANGLE, 88, 11);
		tramLineTester("image.g.5.14", MAX_WIDTH, MAX_ANGLE, 95, 13);
	}

	private void tramLineTester(String root, double maxWidth, Angle angleEps, int lineCount, int tramLineCount) {
		List<SVGLine> lineList = createLinesFromOutlines(root, maxWidth, angleEps, lineCount);
		TramLineManager tramLineManager = new TramLineManager();
		List<TramLine> tramLineList = tramLineManager.makeTramLineList(lineList);
		for (TramLine tramLine : tramLineList) {
			tramLine.setFillx("red");
			tramLine.setStrokeWidthx(10.);
		}
		Assert.assertEquals("tramLines "+root, tramLineCount, tramLineList.size());
	}
	
	// ================= HELPERS ===============
	private static List<SVGLine> createLinesFromOutlines(String fileRoot, Double maxWidth, Angle angleEps, int pathCount) {
		List<SVGPath> pathList = SVGPath.extractPaths(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULE_DIR, fileRoot+".svg")));
		Assert.assertEquals("paths", pathCount, pathList.size());
		SVGG g = new SVGG();
		int i = 0;
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		for (SVGPath path : pathList) {
			LOG.trace(path.getSignature());
			SVGPath newPath = path.replaceAllUTurnsByButt(angleEps);
			if (newPath != null) {
				SVGLine line = newPath.createLineFromMLLLL(angleEps, maxWidth);
				if (line != null) {
					g.appendChild(line);
					lineList.add(line);
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
		return lineList;
	}
}
