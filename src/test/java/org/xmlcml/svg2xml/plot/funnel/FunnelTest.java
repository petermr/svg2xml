package org.xmlcml.svg2xml.plot.funnel;

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
import org.xmlcml.svg2xml.plot.PlotStructurerTest;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.TextBox;
import org.xmlcml.svg2xml.text.TextStructurer;

public class FunnelTest {
	
	public static final Logger LOG = Logger.getLogger(FunnelTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private File OUT_ROOT_TOP = new File("target/plots/funnel/");
	private static final File DEMOS = new File(Fixtures.SVG2XML_DIR);
	private static final String OUTFILE_SVG = "plot.svg";

	@Test
	public void testBakker_Funnel() throws IOException {
		String outRoot = "bakkerFunnel";
		PlotStructurerTest.createPaths(
			new File(DEMOS, "funnel/bakker2014-page11.svg/"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}

	@Test
	/** glyph-based characters
	 * problems with points
	 * 
	 * @throws IOException
	 */
	public void testBooth_Funnel() throws IOException {
		String outRoot = "boothFunnel";
		PlotStructurerTest.createPaths(
			new File(DEMOS, "funnel/booth2010-page18.svg/"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}

	@Test
	/** glyph-based characters
	 * 
	 * @throws IOException
	 */
	public void testCalvin_Funnel() throws IOException {
		String outRoot = "calvinFunnel";
		PlotStructurerTest.createPaths(
			new File(DEMOS, "funnel/calvin2011-page12.svg/"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}

	@Test
	/** glyph-based characters
	 * 
	 * @throws IOException
	 */
	public void testChoi_Funnel() throws IOException {
		String outRoot = "choiFunnel";
		PlotStructurerTest.createPaths(
			new File(DEMOS, "funnel/choi2012-page5.svg/"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}

	@Test
	/** 
	 * 
	 * @throws IOException
	 */
	public void testDong_Funnel() throws IOException {
		String outRoot = "dongFunnel";
		PlotStructurerTest.createPaths(
			new File(DEMOS, "funnel/dong2009-page4.svg/"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}

	@Test
	/** 
	 * 
	 * @throws IOException
	 */
	public void testKerr_Funnel() throws IOException {
		String outRoot = "kerrFunnel";
		PlotStructurerTest.createPaths(
			new File(DEMOS, "funnel/kerr2012-page5.svg/"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}

	@Test
	/** 
	 * 
	 * @throws IOException
	 */
	public void testNair_Funnel() throws IOException {
		String outRoot = "nairFunnel";
		PlotStructurerTest.createPaths(
			new File(DEMOS, "funnel/nair2014-page4.svg/"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}

	@Test
	public void testRogers1_Funnel() throws IOException {
		String outRoot = "rogers1Funnel";
		PlotStructurerTest.createPaths(
			new File(DEMOS, "funnel/rogers2009-page44.svg/"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}

	@Test
	public void testSAGE_Funnel() throws IOException {
		String outRoot = "sageFunnel";
		PlotStructurerTest.createPaths(
			new File(Fixtures.PLOT_DIR, "SAGE_Sbarra_funnel.g.11.0.svg"),
			outRoot
		);
		PlotStructurerTest.createPlots(outRoot, 0);
	}
	
	// =======================
	



}
