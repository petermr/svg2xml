package org.xmlcml.svg2xml.chem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.join.Joinable;
import org.xmlcml.graphics.svg.join.JoinableText;
import org.xmlcml.graphics.svg.join.JoinPointList;
import org.xmlcml.graphics.svg.join.Junction;
import org.xmlcml.graphics.svg.join.JunctionManager;
import org.xmlcml.graphics.svg.join.TramLine;
import org.xmlcml.graphics.svg.join.TramLineManager;
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
	private static final Angle MAX_ANGLE = new Angle(0.03, Units.RADIANS);
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
	@Ignore // FIXME
	public void test00100() {
		createLinesFromOutlines("02.00100.g.6.5", MAX_WIDTH, MAX_ANGLE, 34); // should be 27??
		tramLineTester("02.00100.g.6.5", MAX_WIDTH, MAX_ANGLE, 34, 6);
	}
	
	
	@Test
	public void testWithElementPNG() {
		createLinesFromOutlines("image.g.5.11", MAX_WIDTH, MAX_ANGLE, 48);
		createLinesFromOutlines("image.g.5.12", MAX_WIDTH, MAX_ANGLE, 51);
		createLinesFromOutlines("image.g.5.13", MAX_WIDTH, MAX_ANGLE, 88);
		createLinesFromOutlines("image.g.5.14", MAX_WIDTH, MAX_ANGLE, 95);
		
	}
	
	@Test
	@Ignore // FIXME
	public void testTramLinesG2_11() {
		List<SVGLine> lineList = createLinesFromOutlines("image.g.2.11", MAX_WIDTH, MAX_ANGLE, 6);
		TramLineManager tramLineManager = new TramLineManager();
//		tramLineManager.setTramLineSeparationFactor(0.35);
		List<TramLine> tramLineList = tramLineManager.makeTramLineList(lineList);
		Assert.assertEquals("tramLines", 1, tramLineList.size());
	}
	
	@Test
	@Ignore // FIXME
	public void testTramLines() {
		List<SVGLine> lineList = createLinesFromOutlines("image.g.2.13", MAX_WIDTH, MAX_ANGLE, 13);
		Assert.assertEquals("linelist", 13, lineList.size());
		TramLineManager tramLineManager = new TramLineManager();
//		tramLineManager.setTramLineSeparationFactor(0.35);
		List<TramLine> tramLineList = tramLineManager.makeTramLineList(lineList);
		Assert.assertEquals("tramLines", 3, tramLineList.size());

	}
	
	@Test
	@Ignore // FIXME
	public void testMoreTramLines() {
		tramLineTester("image.g.2.13", MAX_WIDTH, MAX_ANGLE, 13, 3);
		tramLineTester("image.g.2.11", MAX_WIDTH, MAX_ANGLE, 6, 1); 
		tramLineTester("image.g.2.18", MAX_WIDTH, MAX_ANGLE, 21, 5);
		tramLineTester("image.g.2.23", MAX_WIDTH, MAX_ANGLE, 24, 4);
		tramLineTester("image.g.2.25", MAX_WIDTH, MAX_ANGLE, 24, 5);
		tramLineTester("image.g.5.11", MAX_WIDTH, MAX_ANGLE, 48, 4);
	}
	
	@Test
	@Ignore // FIXME need reparameterization
	public void testFailingTramLines() {
		tramLineTester("image.g.5.12", MAX_WIDTH, MAX_ANGLE, 51, 6); //was 8 probably because of hatches
		tramLineTester("image.g.5.13", MAX_WIDTH, MAX_ANGLE, 88, 10); //was 12
		tramLineTester("image.g.5.14", MAX_WIDTH, MAX_ANGLE, 95, 13); //was 16
	}
	
	@Test
	public void testJunction() {
		List<SVGLine> lineList = createLinesFromOutlines("image.g.2.11", MAX_WIDTH, MAX_ANGLE, 6);
		List<Joinable> joinableList = JoinPointList.makeJoinableList(lineList);
		// remove lines which are better described as tramlines
		joinableList.remove(5);
		joinableList.remove(4);
		Assert.assertEquals("no tram", 4, joinableList.size());
		JunctionManager junctionManager = new JunctionManager();
		List<Junction> junctionList = junctionManager.makeJunctionList(joinableList);
		Assert.assertEquals("junction", 3, junctionList.size());
	}

	@Test
	@Ignore // FIXME
	public void testJunctionWithTram() {
		List<SVGLine> lineList = createLinesFromOutlines("image.g.2.11", MAX_WIDTH, MAX_ANGLE, 6);
		TramLineManager tramLineManager = new TramLineManager();
//		tramLineManager.setTramLineSeparationFactor(0.35);
		List<TramLine> tramLineList = tramLineManager.makeTramLineList(lineList);
		lineList = tramLineManager.removeUsedTramLinePrimitives(lineList);
		Assert.assertEquals("tramLines", 1, tramLineList.size());
		List<Joinable> joinableList = JoinPointList.makeJoinableList(lineList);
		Assert.assertEquals("no tram", 4, joinableList.size());
		joinableList.add(tramLineList.get(0));
		Assert.assertEquals("joinable", 5, joinableList.size());
		JunctionManager junctionManager = new JunctionManager();
		List<Junction> junctionList = junctionManager.makeJunctionList(joinableList);
		Assert.assertEquals("junction", 5, junctionList.size());
	}

	@Test
	@Ignore // FIXME
	public void testJunctionWithTramAndText() {
		String fileRoot = "image.g.2.11";
		List<SVGLine> lineList = createLinesFromOutlines(fileRoot, MAX_WIDTH, MAX_ANGLE, 6);
		TramLineManager tramLineManager = new TramLineManager();
//		tramLineManager.setTramLineSeparationFactor(0.35);
		List<TramLine> tramLineList = tramLineManager.makeTramLineList(lineList);
		lineList = tramLineManager.removeUsedTramLinePrimitives(lineList);
		List<Joinable> joinableList = JoinPointList.makeJoinableList(lineList);
		joinableList.addAll(tramLineList);
		JunctionManager junctionManager = new JunctionManager();
		List<SVGText> textList = createTextListAndAddIds(fileRoot);
		for (SVGText svgText : textList) {
			joinableList.add(new JoinableText(svgText));
		}
		Assert.assertEquals("text", 11, joinableList.size());
		List<Junction> junctionList = junctionManager.makeJunctionList(joinableList);
		for (Junction junction : junctionList) {
			LOG.debug(junction);
		}
		Assert.assertEquals("junction", 8, junctionList.size());
	}

	@Test
	@Ignore // FIXME
	public void testMergeJunctions() {
		String fileRoot = "image.g.2.11";
		List<SVGText> textList = createTextListAndAddIds(fileRoot);
		List<SVGLine> lineList = createLinesFromOutlines(fileRoot, MAX_WIDTH, MAX_ANGLE, 6);
		TramLineManager tramLineManager = new TramLineManager();
//		tramLineManager.setTramLineSeparationFactor(0.35);
		List<TramLine> tramLineList = tramLineManager.makeTramLineList(lineList);
		lineList = tramLineManager.removeUsedTramLinePrimitives(lineList);
		List<Joinable> joinableList = JoinPointList.makeJoinableList(lineList);
		joinableList.addAll(tramLineList);
		JunctionManager junctionManager = new JunctionManager();
		for (SVGText svgText : textList) {
			joinableList.add(new JoinableText(svgText));
		}
		List<Junction> junctionList = junctionManager.makeJunctionList(joinableList);
		for (Junction junction : junctionList) {
			LOG.debug(junction);
		}
		Assert.assertEquals("junction", 8, junctionList.size());
		junctionList = junctionManager.mergeJunctions();
//	FIXME
//		Assert.assertEquals("merged", 6, junctionList.size());
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
					String id = path.getId();
					if (id == null) {
						id = createId(i);
						line.setId(id);
					}
					LOG.trace("line id: "+line.getId());
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

	private static String createId(int i) {
		return "line."+i;
	}

	private void tramLineTester(String root, double maxWidth, Angle angleEps, int lineCount, int tramLineCount) {
		List<SVGLine> lineList = createLinesFromOutlines(root,maxWidth, angleEps, lineCount);
		TramLineManager tramLineManager = new TramLineManager();
//		tramLineManager.setTramLineSeparationFactor(0.35);
		List<TramLine> tramLineList = tramLineManager.makeTramLineList(lineList);
		for (TramLine tramLine : tramLineList) {
			tramLine.setFillAll("red");
			tramLine.setStrokeWidthAll(10.);
		}
		Assert.assertEquals("tramLines "+root, tramLineCount, tramLineList.size());
	}
	
	private List<SVGText> createTextListAndAddIds(String fileRoot) {
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(
				SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULE_DIR, fileRoot+".svg")));
		for (int i = 0; i < textList.size(); i++){
			SVGText text = textList.get(i);
			String id = text.getId();
			if (id == null) {
				text.setId("text."+i);
			}
		}
		return textList;
	}

}
