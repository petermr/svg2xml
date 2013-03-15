package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.tools.Chunk;


public class PageAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(PageAnalyzerTest.class);
	
	public final static File PDFTOP = new File("src/test/resources/pdfs");
	public final static File PDFTOP1 = new File("../pdfs");
	public final static File SVGTOP = new File("src/test/resources/svg");
	public final static File TARGET = new File("target");

	public final static File BMCINDIR = new File(PDFTOP, "bmc");
	public final static File BMCSVGDIR = new File(SVGTOP, "bmc");
	public final static File BMCOUTDIR = new File(TARGET, "bmc");

	public final static File AJCINDIR = new File(PDFTOP1, "ajc");
	public final static File AJCSVGDIR = new File(TARGET, "ajc");
	public final static File AJCOUTDIR = new File(TARGET, "ajc");
	
	public final static String BMC_GEOTABLE = "geotable-1471-2148-11-310";
	public final static String MATHS = "maths-1471-2148-11-311";
	public final static String MULTIPLE = "multiple-1471-2148-11-312";
	public final static String TREE = "tree-1471-2148-11-313";

	public final static String AJC1 = "CH01182";
	
	@Before
	public void createSVGFixtures() {
		createSVG(BMCINDIR, BMCSVGDIR, BMC_GEOTABLE);
		createSVG(BMCINDIR, BMCSVGDIR, MATHS);
		createSVG(BMCINDIR, BMCSVGDIR, MULTIPLE);
		createSVG(BMCINDIR, BMCSVGDIR, TREE);

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
	public void testGeoTablePage() {
		int page = 2;
		analyzeChunkInSVGPage(BMCSVGDIR, BMC_GEOTABLE, page, BMCOUTDIR);
	}
	
	@Test
	public void testGeoTablePages() {
		File[] files = new File(BMCSVGDIR, BMC_GEOTABLE).listFiles();
		for (int page = 0; page < files.length; page++) {
			analyzeChunkInSVGPage(BMCSVGDIR, BMC_GEOTABLE, page+1, BMCOUTDIR);
		}
	}
	
	@Test
	public void testTreePages() {
		analyzePaper(BMCSVGDIR, TREE, BMCOUTDIR);
	}

	@Test
	@Ignore
	public void testAJC1Pages() {
		analyzePaper(AJCSVGDIR, AJC1, AJCOUTDIR);
	}

	private void analyzePaper(File svgdir, String paperRoot, File outdir) {
		File paperRootDir = new File(svgdir, paperRoot);
		File[] files = paperRootDir.listFiles();
		if (files == null) {
			throw new RuntimeException("No files in "+paperRootDir);
		}
		for (int page = 0; page < files.length; page++) {
			analyzeChunkInSVGPage(svgdir, paperRoot, page+1, outdir);
		}
	}
	
	private static void analyzeChunkInSVGPage(File svgdir, String fileRoot, int page, File outdir) {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(new File(new File(svgdir, fileRoot),fileRoot+"-page"+page+".svg"));
		SemanticDocumentActionX semanticDocumentAction = SemanticDocumentActionX.createSemanticDocumentActionWithSVGPage(svg);
		List<Chunk> chunkList = WhitespaceChunkerAnalyzerX.chunkCreateWhitespaceChunkList(semanticDocumentAction);
		WhitespaceChunkerAnalyzerX.drawBoxes(chunkList, "red", "yellow", 0.5);
		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
		for (int ichunk = 0; ichunk < gList.size(); ichunk++) {
			analyzeChunkInSVGPage(gList, ichunk, page, outdir, fileRoot);
			checkImages((SVGElement)gList.get(ichunk), outdir, fileRoot, page, ichunk);
		}
		try {
			CMLUtil.debug(svg, 
					new FileOutputStream(new File(new File(outdir, fileRoot), "page-"+page+".svg")),
					1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	private static void checkImages(SVGElement element, File outdir, String fileRoot, int page, int ichunk) {
		List<SVGImage> images = SVGImage.extractImages(SVGUtil.getQuerySVGElements(element, ".//svg:image"));
		for (int ii = 0; ii < images.size(); ii++) {
			SVGImage image = images.get(ii);
			LOG.debug("BBB "+image.getBoundingBox());
			SVGSVG svg = new SVGSVG();
			svg.appendChild(new SVGImage(image));
			try {
				File file = new File(new File(outdir, fileRoot), page+"-image-"+ii+"-"+ichunk+".svg");
				CMLUtil.debug(svg, new FileOutputStream(file), 1);
				System.out.println("FILE: "+file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void analyzeChunkInSVGPage(List<SVGElement> gList, int ichunk, int page, File outdir, String fileRoot) {
//		System.out.println(">> "+ichunk);
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
			File outdir0 = new File(outdir+"/"+fileRoot);
			outdir0.mkdirs();
			File outfile = new File(outdir0, "page-"+page+"-"+ichunk+".html");
			OutputStream os = new FileOutputStream(outfile);
			CMLUtil.debug(element, os, 1);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
