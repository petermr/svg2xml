package org.xmlcml.svg2xml.plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGEllipse;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.flow.FlowStructurer;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.TextBox;
import org.xmlcml.svg2xml.text.TextStructurer;

public class PlotStructurerTest {
	
	public static final Logger LOG = Logger.getLogger(PlotStructurerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String OUTFILE_SVG = "plot.svg";
	private static File OUT_ROOT_TOP = new File("target/plots/");

	@Test
	public void testTEX10Paths() throws IOException {
		String outRoot = "tex10Paths";
		createPaths(
			new File(Fixtures.PLOT_DIR, "TEX.g.10.1.svg"),
			outRoot
		);
		createPlots(outRoot, 0);
	}

	@Test
	public void testTEX11Paths() throws IOException {
		String outRoot = "tex11Paths";
		createPaths(
			new File(Fixtures.PLOT_DIR, "TEX.g.11.1.svg"),
			outRoot
		);
		createPlots(outRoot, 11);
	}

	@Test
	public void testTEX12Paths() throws IOException {
		String outRoot = "tex12Paths";
		createPaths(
			new File(Fixtures.PLOT_DIR, "TEX.g.12.0.svg"),
			outRoot
		);
		createPlots(outRoot, 0);
	}

	@Test
	public void testTEX13Paths() throws IOException {
		String outRoot = "tex13Paths";
		createPaths(
			new File(Fixtures.PLOT_DIR, "TEX.g.13.1.svg"),
			outRoot
		);
		createPlots(outRoot, 0);
	}

	@Test
	public void testBLK_SAM3() throws IOException {
		String outRoot = "blkSam31";
		createPaths(
			new File(Fixtures.PLOT_DIR, "BLK_SAM.g.3.1.svg"),
			outRoot
		);
		createPlots(outRoot, 24);
	}

	@Test
	@Ignore
	public void testBLK_SAM4() throws IOException {
		String outRoot = "blkSam40";
		createPaths(
			new File(Fixtures.PLOT_DIR, "BLK_SAM.g.4.0.svg"),
			outRoot
		);
		createPlots(outRoot, 22);
	}

	@Test
	public void testBLK_SAM4Bot() throws IOException {
		String outRoot = "blkSam40Bot";
		createPaths(
			new File(Fixtures.PLOT_DIR, "BLK_SAM.g.4.0.bot.svg"),
			outRoot
		);
		createPlots(outRoot, 22);
	}

	@Test
	public void testBLK_SAM4Top() throws IOException {
		String outRoot = "blkSam40Top";
		createPaths(
			new File(Fixtures.PLOT_DIR, "BLK_SAM.g.4.0.top.svg"),
			outRoot
		);
		createPlots(outRoot, 22);
	}

	@Test
	/** checks that graphics attributes are copied to child Elements
	 *  
	 * @throws IOException
	 */
	public void testBLK_SAM4TopSmall() throws IOException {
		String outRoot = "blkSam40TopSmall";
		createPaths(
			new File(Fixtures.PLOT_DIR, "BLK_SAM.g.4.0.top.small.svg"),
			outRoot
		);
		createPlots(outRoot, 22);
	}

//	@Test
//	public void testBakker_Funnel() throws IOException {
//		String outRoot = "bakkerFunnel";
//		createPaths(
//			new File("demos", "funnel/bakker2014-page11.svg/"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}
//
//	@Test
//	/** glyph-based characters
//	 * problems with points
//	 * 
//	 * @throws IOException
//	 */
//	public void testBooth_Funnel() throws IOException {
//		String outRoot = "boothFunnel";
//		createPaths(
//			new File("demos", "funnel/booth2010-page18.svg/"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}
//
//	@Test
//	/** glyph-based characters
//	 * 
//	 * @throws IOException
//	 */
//	public void testCalvin_Funnel() throws IOException {
//		String outRoot = "calvinFunnel";
//		createPaths(
//			new File("demos", "funnel/calvin2011-page12.svg/"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}
//
//	@Test
//	/** glyph-based characters
//	 * 
//	 * @throws IOException
//	 */
//	public void testChoi_Funnel() throws IOException {
//		String outRoot = "choiFunnel";
//		createPaths(
//			new File("demos", "funnel/choi2012-page5.svg/"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}
//
//	@Test
//	/** 
//	 * 
//	 * @throws IOException
//	 */
//	public void testDong_Funnel() throws IOException {
//		String outRoot = "dongFunnel";
//		createPaths(
//			new File("demos", "funnel/dong2009-page4.svg/"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}
//
//	@Test
//	/** 
//	 * 
//	 * @throws IOException
//	 */
//	public void testKerr_Funnel() throws IOException {
//		String outRoot = "kerrFunnel";
//		createPaths(
//			new File("demos", "funnel/kerr2012-page5.svg/"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}
//
//	@Test
//	/** 
//	 * 
//	 * @throws IOException
//	 */
//	public void testNair_Funnel() throws IOException {
//		String outRoot = "nairFunnel";
//		createPaths(
//			new File("demos", "funnel/nair2014-page4.svg/"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}
//
//	@Test
//	public void testRogers1_Funnel() throws IOException {
//		String outRoot = "rogers1Funnel";
//		createPaths(
//			new File("demos", "funnel/rogers2009-page44.svg/"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}
//
//	@Test
//	public void testSAGE_Funnel() throws IOException {
//		String outRoot = "sageFunnel";
//		createPaths(
//			new File(Fixtures.PLOT_DIR, "SAGE_Sbarra_funnel.g.11.0.svg"),
//			outRoot
//		);
//		createPlots(outRoot, 0);
//	}

	// =======================
	
	private void createFlow(File inputFile, String outRoot) throws IOException {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(inputFile);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);

		PhraseListList phraseListList = textStructurer.getOrCreatePhraseListListFromWords();
		
		Real2 xMargins = new Real2(5.0, 5.0);
		Real2 yMargins = new Real2(2.0, 2.0);
		List<TextBox> textBoxList = textStructurer.createTextBoxList(phraseListList, xMargins, yMargins);
		FlowStructurer flowStructurer = textStructurer.createFlowStructurer(phraseListList);
		List<SVGShape> shapeList = flowStructurer.makeShapes();
		
		SVGG g = new SVGG();
		for (TextBox textBox : textBoxList) {
			SVGRect rect = textBox.getOrCreateBoundingRect();
			rect.setFill("black");
			rect.setOpacity(0.2);
			rect.setStrokeWidth(1.5);
			textBox.getStringValue();
			g.appendChild(new TextBox(textBox));
		}
		
		for (SVGShape shape : shapeList) {
			LOG.debug(shape);
			if (shape instanceof SVGRect) {
				shape.setFill("cyan");
				shape.setOpacity(0.2);
			} else if (shape instanceof SVGLine) {
				shape.setStroke("red");
			} else if (shape instanceof SVGPolyline) {
				SVGPolyline polyline = (SVGPolyline) shape;
				polyline.setStroke("orange");
				polyline.setFill("blue");
				polyline.setStrokeWidth(1.0);
			} else if (shape instanceof SVGPolygon) {
				SVGPolygon polygon = (SVGPolygon) shape;
				polygon.setStroke("blue");
				polygon.setFill("orange");
				polygon.setStrokeWidth(2.0);
			} 
			g.appendChild(shape.copy());
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(OUT_ROOT_TOP, outRoot+"/"+OUTFILE_SVG));
		
	}
	
	public static void createPaths(File inputFile, String outRoot) throws IOException {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(inputFile);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);

		PhraseListList phraseListList = textStructurer.getOrCreatePhraseListListFromWords();
		
		Real2 xMargins = new Real2(5.0, 5.0);
		Real2 yMargins = new Real2(2.0, 2.0);
		List<TextBox> textBoxList = textStructurer.createTextBoxList(phraseListList, xMargins, yMargins);
		FlowStructurer flowStructurer = textStructurer.createFlowStructurer(phraseListList);
		List<SVGShape> shapeList = flowStructurer.makeShapes();
		
		SVGG g = new SVGG();
		for (TextBox textBox : textBoxList) {
			SVGRect rect = textBox.getOrCreateBoundingRect();
			rect.setFill("black");
			rect.setOpacity(0.2);
			rect.setStrokeWidth(1.5);
			textBox.getStringValue();
			g.appendChild(new TextBox(textBox));
		}
		
		for (SVGShape shape : shapeList) {
			if (shape instanceof SVGRect) {
				shape.setFill("cyan");
				shape.setOpacity(0.2);
			} else if (shape instanceof SVGLine) {
				shape.setStroke("red");
			} else if (shape instanceof SVGEllipse) {
				shape.setStroke("green");
			} else if (shape instanceof SVGPolyline) {
				SVGPolyline polyline = (SVGPolyline) shape;
				polyline.setStroke("orange");
//				polyline.setFill("blue");
				polyline.setStrokeWidth(1.0);
			} else if (shape instanceof SVGPolygon) {
				SVGPolygon polygon = (SVGPolygon) shape;
				polygon.setStroke("blue");
				polygon.setFill("orange");
				polygon.setStrokeWidth(2.0);
			} 
			g.appendChild(shape.copy());
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(OUT_ROOT_TOP, outRoot+"/"+OUTFILE_SVG));
		
	}
	
	public static void createPlots(String outRoot, int arrowCount) throws FileNotFoundException {
		OUT_ROOT_TOP.mkdirs();
		File outfile = new File(OUT_ROOT_TOP, outRoot+"/plot.svg");
		SVGG g = (SVGG) SVGElement.readAndCreateSVG(outfile).getChildElements().get(0);
		File outDir = new File(OUT_ROOT_TOP, outRoot+"/");
		SVGSVG svg = new SVGSVG();
//		svg.setMarker(SVGArrow.ARROWHEAD);
		g.detach();
		svg.appendChild(g);
		SVGUtil.debug(svg, new FileOutputStream(new File(outDir, OUTFILE_SVG)), 1);
	}
	



}
