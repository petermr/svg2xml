package org.xmlcml.svg2xml.figure;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.Ignore;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.page.GraphicAnalyzer;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer.TextOrientation;
import org.xmlcml.svg2xml.text.TextStructurer;

public class GraphicAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(GraphicAnalyzerTest.class);
	
	@Test
	public void failingTestForFigureMargins() {
		FigureGraphic f = new FigureGraphic(new FigureAnalyzer(new PageAnalyzer(Fixtures.FIGURE_PAGE_3_SVG)));
		f.createImageFromComponents("target/margins.png");
	}
	
	@Test
	public void testGraphic() {
		PageAnalyzer pageAnalyzer = new PageAnalyzer((SVGSVG) null);
		SVGElement svgElement = SVGElement.readAndCreateSVG(Fixtures.FIGURE_PAGE_3_SVG);
		SVGG graphic = (SVGG) SVGG.generateElementList(svgElement, "svg:g/svg:g/svg:g[@edge='YMIN']").get(2);
		GraphicAnalyzer graphicAnalyzer = new GraphicAnalyzer(pageAnalyzer, graphic);
		Assert.assertNull("image", graphicAnalyzer.getImageAnalyzer());
		Assert.assertNotNull("shape", graphicAnalyzer.getShapeAnalyzer());
		Assert.assertNotNull("text", graphicAnalyzer.getTextAnalyzer());
		Assert.assertEquals("shapeList", 183, graphicAnalyzer.getShapeAnalyzer().getShapeList().size());
		Assert.assertEquals("textList", 2168, graphicAnalyzer.getTextAnalyzer().getTextCharacters().size());
	}
	
	@Test
	public void testTextOrientation() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.FIGURE_PAGE_3_SVG,  "svg:g/svg:g/svg:g[@edge='YMIN']", 2);
		Assert.assertEquals("textList", 2168, graphicAnalyzer.getAllTextCharacters().size());
		Assert.assertEquals("textList0", 2089, graphicAnalyzer.getTextAnalyzer().getRot0TextCharacters().size());
		Assert.assertEquals("textListPi2", 79, graphicAnalyzer.getTextAnalyzer().getRotPi2TextCharacters().size());
		Assert.assertEquals("textListPi", 0, graphicAnalyzer.getTextAnalyzer().getRotPiTextCharacters().size());
		Assert.assertEquals("textList3Pi2", 0, graphicAnalyzer.getTextAnalyzer().getRot3Pi2TextCharacters().size());
		Assert.assertEquals("textList3Pi2", 0, graphicAnalyzer.getTextAnalyzer().getRotIrregularTextCharacters().size());
	}
	
	@Test
	public void testTextOrientationAnalyzers() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.FIGURE_PAGE_3_SVG,  "svg:g/svg:g/svg:g[@edge='YMIN']", 2);
		Assert.assertEquals("textList", 2168, graphicAnalyzer.getAllTextCharacters().size());
		Assert.assertNotNull("textList0", graphicAnalyzer.getRot0TextAnalyzer());
		Assert.assertEquals("textList0", 2089, graphicAnalyzer.getRot0TextAnalyzer().getTextCharacters().size());
		Assert.assertNotNull("textListPi2", graphicAnalyzer.getRotPi2TextAnalyzer());
		Assert.assertEquals("textListPi2", 79, graphicAnalyzer.getRotPi2TextAnalyzer().getTextCharacters().size());
		Assert.assertNotNull("textListPi", graphicAnalyzer.getRotPiTextAnalyzer());
		Assert.assertEquals("textListPi", 0, graphicAnalyzer.getTextAnalyzer().getRotPiTextCharacters().size());
		Assert.assertNotNull("textList3Pi2", graphicAnalyzer.getRot3Pi2TextAnalyzer());
		Assert.assertEquals("textList3Pi2", 0, graphicAnalyzer.getRot3Pi2TextAnalyzer().getTextCharacters().size());
		Assert.assertNotNull("textListPi2", graphicAnalyzer.getRotIrregularTextAnalyzer());
		Assert.assertEquals("textListIrreg", 0, graphicAnalyzer.getRotIrregularTextAnalyzer().getTextCharacters().size());
	}
	
	/** 
	 * Extracts all horizontal text.
	 * <p>
	 * Test probably fragile.
	 */
	@Test
	public void testRot0Analyzer() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.FIGURE_PAGE_3_SVG,  "svg:g/svg:g/svg:g[@edge='YMIN']", 2);
		TextAnalyzer textAnalyzer = graphicAnalyzer.getRot0TextAnalyzer();
		TextStructurer textStructurer = new TextStructurer(textAnalyzer);
		HtmlElement htmlElement = textStructurer.createHtmlElement();
		SVGUtil.debug(htmlElement, "target/rot0.html", 1);
		Assert.assertTrue("horizontal", 
				htmlElement.toXML().startsWith(
						"<div xmlns=\"http://www.w3.org/1999/xhtml\"><i>Luscinia  </i>" +
						" (Muscicapidae) * <sub><i>Ficedula</i></sub>"));

	}
	
	/** 
	 * Extracts all vertical text.
	 * <p>
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testRotPi2Analyzer() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.FIGURE_PAGE_3_SVG,  "svg:g/svg:g/svg:g", 2);
		testSVGandHTML(graphicAnalyzer, TextOrientation.ROT_PI2, "target/page3pi2.svg", "target/page3pi2.html", "page3pi2",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">" +
				"<p /><b>Suboscines Oscines</b>" +
				" 'core Corvoidea'  Passer- Sylvi- Muscicap-oidea oidea oidea Passerida </div>"	);
	}
	
	/** 
	 * Extracts all horizontal text.
	 * <p>
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testTreeGraphics() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.TREE_G_8_2_SVG,  "./svg:g");
		testSVGandHTML(graphicAnalyzer, TextOrientation.ROT_0, "target/tree.svg", "target/tree.html", "tree",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">" +
				" Hpi L A  Ssy L 89  Hla L 99  Hag L 95  Nle L  Human L  Macaque L  Hpi M  Ssy M 92  Hag M 99" +
				"  Hla M 95  Nle M  Human M  Macaque M  Mouse M 0.01  Hag L B  Hla L  Hpi L 81  Hag M" +
				"  Hla M  Hpi M 100  Ssy L 99  Ssy M  Nle L 95  Nle M  Human L  Human M 0.005 </div>");
	}
	
	/** 
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testMathsGraphics72() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.MATHS_G_7_2_SVG,  "./svg:g");
		testSVGandHTML(graphicAnalyzer, TextOrientation.ROT_0, "target/g72.svg", "target/g72.html", "html",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">" +
				"1) 0.4 A 1) 2) 3) 2) 0.3 0.2 0.3 0.4 0 0.1 0.2 0.3 0.4 0 0.1 0.2 3) 0.1 0 0 2 4 6 8 10 12 14 B 0 2 4 6 8 10 12 14 Time (Myr) <p />" +
				"<b>Figure 3 </b> <b>Rates-through-time plot</b> . Diversification rates through time resulting from the analysis of 100 phylogenies simulated under a fivefold increase in diversification rates. The upper plot (A) shows the marginal rates for 1 Myr time categories (line) and the 95% highest posterior density (error bars). The x-axis represents time (Myr), and the y-axis is the average per-lineage diversification rate (spp/Myr). The insert displays three examples of marginal distributions of the diversification rate for three points along the phylogenies (indicated by arrows on the rates through time plot): 1) close to the tips (2 Mya), 2) at the point of rate shift (5 Mya), and 3) towards to root of the trees (10 Mya). Note the bimodal distribution of rates when a rat-shift is found (both the lower and higher rates are sampled). In the lower plot (B), the frequencies of a rate shift are proportional to the probability of a rate shift in that time frame. </div>"
				);
		testSVGandHTML(graphicAnalyzer, TextOrientation.ROT_PI2, "target/g72Pi2.svg", "target/g72Pi2.html", "html",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">Sampling  Diversif cation rate (r)  frequency </div>");
	}
	
	/** 
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testMathsGraphicsPi2() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.MATHS_G_7_2_SVG,  "./svg:g");
		testSVGandHTML(graphicAnalyzer, TextOrientation.ROT_PI2, "target/g72pi2.svg", "target/g72pi2.html", "g72pi2",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">Sampling  Diversif cation rate (r)  frequency </div>");
	}
	
	/** 
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testHistogram() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.HISTOGRAM_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "histogram", 
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">0 2 4 6 8 10 12 14 Time (Myr) </div>");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_PI2, "histogram", 
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">Sampling  frequency </div>");
	}
	
	private void testExtractionOfTextWithTextStructurer(GraphicAnalyzer graphicAnalyzer,
			TextOrientation textOrientation, String root, String refHtml) {
		String root1 = root + "_"+textOrientation.toString().toLowerCase();
		testSVGandHTML(graphicAnalyzer, textOrientation, "target/"+root1+".svg",  "target/"+root1+".html", root1, refHtml);
	}


	/** 
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testXAxis() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.XAXIS_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "xaxis",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">0 2 4 6 8 10 12 14 Time (Myr) </div>");
	}
	
	/** 
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testMultiple72() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.MULTIPLE_G_7_2_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "multiple72",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\"><i>H. agilis</i> <p /><b>**</b> <b>**</b> 0.4  0.2  0.0  <i>H. lar</i> <p /><b>** *</b> 0.2  0.1null <p /><b>NE</b> 0.0  <i>H. pileatus</i> 0.2  <p /><b>** **</b> 0.1  0.0  <sub><i>N. leucogenys</i></sub> <p /><b>**</b> <b>**</b> 0.4  0.2  0.0  <i>S. syndactylus</i> 0.3  0.2  <p /><b>**</b> 0.1  0.0  <p /><b>Ex Int Ex Int G</b> <sub><b>A </b></sub> <b>G</b> <sub><b>X</b></sub> <b>L M Kim et al</b> </div>");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_PI2, "multiple72",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\"><p /><b>Nucleotide diversity (</b> S <b>)  of L and M opsin genes (x 10</b> <sup><b>-2</b></sup> <b>)</b> </div>");
	}
	
	/** 
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testMaths66() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.MATHS_G_6_6_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "maths66",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">AB </div>");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_PI2, "maths66",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">Speciation rate ( λ ) 0.51.01.52.0 </div>");
	}
	
	/** 
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testMaths68() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.MATHS_G_6_8_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "maths68",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">255075100 Taxon sampling (%) </div>");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_PI2, "maths68",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">Speciation rate ( λ ) 0.40.60.81.01.21.4 0.40.60.81.01.21.4 Extinction rate ( μ ) </div>");
		}
	
	/** 
	 * Test probably fragile as HTML method may develop.
	 */
	@Test
	public void testMultiple92() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.MULTIPLE_G_9_2_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "multiple92",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\"><p /><b>Exon 3 Intron 3 Exon 4 Intron 4 Exon 5</b> <b>Hag</b> <b>Hla</b> <b>Hpi</b> <b>Nle</b> <b>Ssy</b> </div>");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_PI2, "multiple92", 
				"<div xmlns=\"http://www.w3.org/1999/xhtml\" />");
	}
	
	/** 
	 * Single Double axis plot
	 */
	@Test
	public void testScatterplotRed() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.SCATTERPLOTRED_7_2_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "scatterplotred",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">  0.04   <sup>0.03  </sup> <sup>0.02  </sup> 0.01   0.00   0.00   0.01   0.02   0.03   0.04   0.05   <p />" +
				"<b>dN of </b> <b><i>EF-1 </i></b> <i>α</i> </div>");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_PI2, "scatterplotred", 
				"<div xmlns=\"http://www.w3.org/1999/xhtml\"><p /><b>dN of </b> <i>β</i> <b><i>-tub </i></b> </div>");
	}
	
	
	/** 
	 * Single Double axis plot with more types of point
	 * <p>
	 * This has corrupted Greek letters
	 */
	@Test
	public void testMulticolourScatterplot() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.SCATTERPLOT_7_2_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "scatterplot",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">  0.04   <sup>0.03  </sup> <sup>0.02  </sup> 0.01   0.00   0.00   0.01   0.02   0.03   0.04   0.05   <p /><b>dN of </b> <b><i>EF-1 </i></b> null </div>");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_PI2, "scatterplot", 
				"<div xmlns=\"http://www.w3.org/1999/xhtml\"><p /><b>dN of </b> Ε <b><i>-tub </i></b> </div>");
	}
	
	/** 
	 * Five Double axis plot with more types of point
	 * <p>
	 * The results are not intuitive as they are sorted by Y so read across the page
	 * <p>
	 * This has corrupted Greek letters
	 */
        @Ignore
	@Test
	public void test5Scatterplots() {
		GraphicAnalyzer graphicAnalyzer = GraphicAnalyzer.createGraphicAnalyzer(Fixtures.SCATTERPLOT_FIVE_7_2_SVG,  "./svg:g");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_0, "scatterplot5",
				"<div xmlns=\"http://www.w3.org/1999/xhtml\">0.14    B  C A  0.50   0.04   <sup>0.12  </sup> 0.40   <sup>0.03   0.10  </sup> <sup>0.30  </sup>" +
				" 0.08   <sup>0.02  </sup> <sup>0.06  </sup>" +
				" 0.20   0.04   0.01   0.10   0.02   0.00   0.00  0.00   0.00   0.01   0.02   0.03   0.04   0.05   0.00   0.01   0.02   0.03   0.04   0.050.00   0.01   0.02   0.03   0.04   0.05   <p />" +
				"<b>dN of </b> <b><i>EF-1 </i></b> null  <b>dN of  </b> <b><i>-tub </i></b> <b>dN of  </b> <b><i>-tub </i></b> D   <sub>0.60   </sub>" +
				" E  0.40   <sup>0.50  </sup> 0.30   <sup>0.40  </sup> 0.30   <sup>0.20  </sup>" +
				" 0.20   0.10   0.10   0.00   0.00   0.00   0.01   0.02   0.03   0.04   0.05   0.00   0.01   0.02   0.03   0.04   0.05   <p />" +
				"<b>dN of  </b> <b><i>-tub  </i></b> <b>dN of  </b> <b><i>-tub </i></b> </div>");
		testExtractionOfTextWithTextStructurer(graphicAnalyzer, TextOrientation.ROT_PI2, "scatterplot5", 
				"<div xmlns=\"http://www.w3.org/1999/xhtml\"><p /><b>dS of </b> <b><i>EF-1</i></b> Δ null <p /><b>dN of </b>" +
				" Ε <b><i>-tub </i></b> <b>p-distance of rDNA cluster </b>" +
				" <b>p-distance of intron within </b> <b><i>EF-1</i></b> Δ <sup>null</sup> <p /><b>dS of </b> Ε <b><i>-tub </i></b> </div>");
	}
	
	//=============================================
	
	private static void testSVGandHTML(GraphicAnalyzer graphicAnalyzer, TextOrientation textOrientation, 
			String svgFilename, String htmlFilename, String assertMsg, String refHtml) {
		Assert.assertNotNull("non-null graphicsAnalyzer", graphicAnalyzer);
		TextStructurer textStructurer = graphicAnalyzer.createTextStructurer(textOrientation);
		if (textStructurer != null) {
			SVGUtil.debug(textStructurer.getDebugSVG(), svgFilename, 1);
			HtmlElement htmlElement = textStructurer.createHtmlElement();
			SVGUtil.debug(htmlElement, htmlFilename, 1);
			Assert.assertEquals(assertMsg, refHtml, htmlElement.toXML());
		}
	}

}
