package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.text.TextLine;


public class PageAnalyzerTest {

	public final static File PDFTOP = new File("src/test/resources/pdfs");
	public final static File PDFTOP1 = new File("../pdfs");
	public final static File SVGTOP = new File("src/test/resources/svg");
	public final static File TARGET = new File("target");

	public final static File BMCINDIR = new File(PDFTOP, "bmc");
	public final static File BMCOUTDIR = new File(SVGTOP, "bmc");

	public final static File AJCINDIR = new File(PDFTOP1, "ajc");
	public final static File AJCOUTDIR = new File(TARGET, "ajc");
	
	public final static String BMC_GEOTABLE = "geotable-1471-2148-11-310";
	public final static String MATHS = "maths-1471-2148-11-311";
	public final static String MULTIPLE = "multiple-1471-2148-11-312";
	public final static String TREE = "tree-1471-2148-11-313";

	public final static String AJC1 = "CH01182";
	
	@Before
	public void createSVGFixtures() {
		createSVG(BMCINDIR, BMCOUTDIR, BMC_GEOTABLE);
		createSVG(BMCINDIR, BMCOUTDIR, MATHS);
		createSVG(BMCINDIR, BMCOUTDIR, MULTIPLE);
		createSVG(BMCINDIR, BMCOUTDIR, TREE);

//		createSVG(AJCINDIR, AJCOUTDIR, AJC1);    // uncomment for AJC

	}
	
	private static void createSVG(File indir, File outtop, String fileRoot) {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		File infile = new File(indir, fileRoot+".pdf");
		if (!infile.exists()) {
			throw new RuntimeException("no inpiut file: "+infile);
		}
		File outdir = new File(outtop, fileRoot);
		if (!outdir.exists() || outdir.listFiles() == null) {
			outdir.mkdirs();
			Assert.assertTrue("outdir "+outtop, outtop.exists());
			converter.run("-outdir", outdir.toString(), infile.toString() );
		}

	}
	
	@Test
	public void testSetup() {
		
	}
	
	@Test
	public void testPage() {
		int page = 2;
		analyzeChunkInSVGPage(BMCOUTDIR, BMC_GEOTABLE, page);
	}
	
	@Test
	public void testGeoTablePages() {
		File[] files = new File(BMCOUTDIR, BMC_GEOTABLE).listFiles();
		for (int page = 0; page < files.length; page++) {
			analyzeChunkInSVGPage(BMCOUTDIR, BMC_GEOTABLE, page+1);
		}
	}
	
	@Test
	public void testTreePages() {
		analyzePaper(BMCOUTDIR, TREE);
	}

	@Test
	@Ignore
	public void testAJC1Pages() {
		analyzePaper(AJCOUTDIR, AJC1);
	}

	private void analyzePaper(File outdir, String paperRoot) {
		File[] files = new File(outdir, paperRoot).listFiles();
		for (int page = 0; page < files.length; page++) {
			analyzeChunkInSVGPage(outdir, paperRoot, page+1);
		}
	}
	
	private static void analyzeChunkInSVGPage(File outdir, String fileRoot, int page) {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(new File(new File(outdir, fileRoot),fileRoot+"-page"+page+".svg"));
		SemanticDocumentActionX semanticDocumentAction = SemanticDocumentActionX.createSemanticDocumentActionWithSVGPage(svg);
		/*List<Chunk> finalChunkList = */WhitespaceChunkerAnalyzerX.chunkCreateWhitespaceChunkList(semanticDocumentAction);
		System.out.println("SVG "+svg);
		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
		for (int i = 0; i < gList.size(); i++) {
			analyzeChunkInSVGPage(gList, i, page);
		}
	}
		
	private static void analyzeChunkInSVGPage(List<SVGElement> gList, int ichunk, int page) {
		System.out.println(">> "+ichunk);
		SVGElement chunkSvg = gList.get(ichunk);
		AbstractPageAnalyzerX analyzerX = AbstractPageAnalyzerX.getAnalyzer(chunkSvg);
		TextAnalyzerX textAnalyzer = null;
		if (analyzerX instanceof TextAnalyzerX) {
			textAnalyzer = (TextAnalyzerX) analyzerX;
		} else if (analyzerX instanceof MixedAnalyzer) {
			textAnalyzer = new TextAnalyzerX();
			textAnalyzer.analyzeTexts(((MixedAnalyzer) analyzerX).getTextList());
		} else {
			return;
		}
		List<TextLine> textLines = textAnalyzer.getLinesInIncreasingY();
		Element element = textAnalyzer.createHtmlDivWithParas();
		try {
			AbstractPageAnalyzerX.tidyStyles(element);
			CMLUtil.debug(element, new FileOutputStream("target/page-"+page+"-"+ichunk+".html"), 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
