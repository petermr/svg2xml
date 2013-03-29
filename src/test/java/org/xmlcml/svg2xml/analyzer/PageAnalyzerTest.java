package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
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
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.tools.Chunk;


public class PageAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(PageAnalyzerTest.class);
	
	public final static String BMC_GEOTABLE = "geotable-1471-2148-11-310";

	public final static String AJC1 = "CH01182";
	
	@Before
	public void createSVGFixtures() {
		createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, BMC_GEOTABLE);
	}
	
	public static void createSVG(File indir, File outtop, String fileRoot) {
		LOG.trace("createSVG");
		PDF2SVGConverter converter = new PDF2SVGConverter();
		File infile = new File(indir, fileRoot+".pdf");
		if (!infile.exists()) {
			throw new RuntimeException("no input file: "+infile);
		}
		File outdir = new File(outtop, fileRoot);
		LOG.trace("outdir "+outdir);
		if (!outdir.exists() || outdir.listFiles() == null) {
			outdir.mkdirs();
			Assert.assertTrue("outdir "+outtop, outtop.exists());
			LOG.debug("running "+infile.toString()+" to "+outdir.toString());
			converter.run("-outdir", outdir.toString(), infile.toString() );
		} else {
			LOG.trace("Skipping SVG");
		}

	}
	
	@Test
	public void testSetup() {
		
	}
	
	@Test
	public void testGeoTablePage() {
		int page = 2;
		analyzeChunkInSVGPage(Fixtures.BMCSVGDIR, BMC_GEOTABLE, page, Fixtures.BMCOUTDIR);
	}
	
	@Test
	public void testGeoTablePages() {
		File[] files = new File(Fixtures.BMCSVGDIR, BMC_GEOTABLE).listFiles();
		analyzeChunksInPagesInFiles(files, Fixtures.BMCSVGDIR, BMC_GEOTABLE, Fixtures.BMCOUTDIR);
	}

	private void analyzeChunksInPagesInFiles(File[] files, File svgDir, String fileRoot, File outdir) {
		for (int page = 0; page < files.length; page++) {
			analyzeChunkInSVGPage(svgDir, fileRoot, page+1, outdir);
		}
	}
	
	@Test
	public void testBoldResults() {
		SVGElement svgElement = SVGElement.readAndCreateSVG(new File("src/test/resources/org/xmlcml/svg2xml/svg/bmc/tree-page-2-results.svg"));
		analyzeChunkInSVGPage((SVGElement) svgElement.getChildElements().get(0), "chunk", Fixtures.BMCOUTDIR, "results");
	}
	
	//================================================================
	
	public static void analyzePDF(File pdfDir, File svgDir, String paperRoot, File outDir) {
		createSVG(pdfDir, svgDir, paperRoot);
		analyzePaper(svgDir, paperRoot, outDir);
	}
	
	public static void analyzePaper(File svgdir, String paperRoot, File outdir) {
		File paperRootDir = new File(svgdir, paperRoot);
		File[] files = paperRootDir.listFiles();
		if (files == null) {
			throw new RuntimeException("No files in "+paperRootDir);
		}
		for (int page = 0; page < files.length; page++) {
			System.out.print(page+"=");
			analyzeChunkInSVGPage(svgdir, paperRoot, page+1, outdir);
		}
		System.out.println();
	}
	
	private static void analyzeChunkInSVGPage(File svgdir, String fileRoot, int page, File outdir) {
		File svgFile = new File(new File(outdir, fileRoot), "page-"+page+".svg");
		if (svgFile.exists()) {
			LOG.trace("Skipping: "+svgFile);
			return;
		}
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(new File(new File(svgdir, fileRoot),fileRoot+"-page"+page+".svg"));
		SemanticDocumentActionX semanticDocumentAction = SemanticDocumentActionX.createSemanticDocumentActionWithSVGPage(svg);
		List<Chunk> chunkList = WhitespaceChunkerAnalyzerX.chunkCreateWhitespaceChunkList(semanticDocumentAction);
		WhitespaceChunkerAnalyzerX.drawBoxes(chunkList, "red", "yellow", 0.5);
		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
		for (int ichunk = 0; ichunk < gList.size(); ichunk++) {
			analyzeChunkInSVGPage(gList.get(ichunk), "page-"+page+"-"+ichunk, outdir, fileRoot);
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
			LOG.trace("BBB "+image.getBoundingBox());
			SVGSVG svg = new SVGSVG();
			svg.appendChild(new SVGImage(image));
			try {
				File dir = new File(outdir, fileRoot);
				dir.mkdirs();
				File file = new File(dir, page+"-image-"+ii+"-"+ichunk+".svg");
				CMLUtil.debug(svg, new FileOutputStream(file), 1);
				LOG.trace("FILE: "+file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void analyzeChunkInSVGPage(SVGElement chunkSvg, String name, File outdir, String fileRoot) {
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
		LOG.trace("lines "+textLines.size());
		for (TextLine textLine : textLines){
			LOG.trace(">> "+textLine);
		}
		Element element = textAnalyzer.createHtmlDivWithParas();
		if (element != null) {
			try {
				AbstractPageAnalyzerX.tidyStyles(element);
				File outdir0 = new File(outdir+"/"+fileRoot);
				outdir0.mkdirs();
				File outfile = new File(outdir0, name+".html");
				OutputStream os = new FileOutputStream(outfile);
				CMLUtil.debug(element, os, 1);
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
