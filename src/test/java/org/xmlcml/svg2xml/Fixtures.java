package org.xmlcml.svg2xml;

import java.io.File;
import java.util.List;

import nu.xom.Builder;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.analyzer.WhitespaceChunkerAnalyzerX;
import org.xmlcml.svg2xml.tools.Chunk;

public class Fixtures {

	private static final Logger LOG = Logger.getLogger(Fixtures.class);
	
	public static final String RESOURCES_DIR = "src/test/resources/";
	public static final String SVG2XML_DIR = RESOURCES_DIR+"org/xmlcml/svg2xml/";
	public static final String PDFS_DIR = RESOURCES_DIR+"pdfs/";
	public static final String COMMAND_DIR= SVG2XML_DIR+"command/";
	public static final String CORE_DIR = SVG2XML_DIR+"core/";
	public static final String ACTION_DIR = SVG2XML_DIR+"action/";
	public static final String SVG_DIR = SVG2XML_DIR+"svg/";
	public static final String SVG1_DIR = RESOURCES_DIR+"svg/";
	public static final String BMC_DIR = SVG_DIR+"bmc/";
	public static final File ACTION_SVG_DIR = new File(Fixtures.ACTION_DIR, "svg");
	public static final File ACTION_PDF_DIR = new File(Fixtures.ACTION_DIR, "pdf");
	public static final File CSIRO_DIR = new File("../pdfs/csiro/test");
	public static final File CSIRO_DIR0 = new File("../pdfs/csiro/test0");
	public static final File CSIRO_DIR1 = new File("../pdfs/csiro/test1");
	
	public static final String AJC_PAGE6_PDF = CORE_DIR+"ajc-page6.pdf";
	
	public final static File NOOP_FILE = new File(CORE_DIR+"noopTst.xml");
	public final static File BASIC_FILE = new File(CORE_DIR+"basicTst.xml");
	public static final File INCLUDE_TEST_FILE = new File(CORE_DIR+"includeTst.xml");
	public static final File INFILE_TEST = new File(CORE_DIR+"infileTst.xml");
	public static final File ASSERT_TST = new File(COMMAND_DIR+"assertTst.xml");
	public static final File NO_ASSERT_TST = new File(COMMAND_DIR+"noAssertTst.xml");
	public static final File VARIABLE_TST = new File(COMMAND_DIR+"variableTst.xml");
	public static final File WHITESPACE_CHUNKER_COMMAND = new File(Fixtures.COMMAND_DIR+"whitespaceChunkerTst.xml");
	public static final File WHITESPACE_0_TST = new File(Fixtures.COMMAND_DIR+"pageTst0.xml");
	public static final File PAGE0_SVG = new File(Fixtures.COMMAND_DIR+"test-page0.svg");
	public static final File HARTER3_SVG = new File(Fixtures.COMMAND_DIR+"harter3.svg");
	public static final File HARTER3SMALL_SVG = new File(Fixtures.COMMAND_DIR+"harter3small.svg");
	public static final File AJC6_SVG = new File(Fixtures.COMMAND_DIR+"ajc6.svg");
	public static final File POLICIES_SVG = new File(Fixtures.COMMAND_DIR+"policies.svg");
	public static final File CHUNK_ANALYZE = new File(Fixtures.ACTION_DIR+"chunkAnalyzeTst.xml");
	public static final File CHUNK_ANALYZE0 = new File(Fixtures.ACTION_DIR+"chunkAnalyzeTst0.xml");
	
	public static final File CHUNK_ANALYZE_POLICIES = new File(Fixtures.ACTION_DIR, "chunkAnalyzePolicies.xml");
	public static final File TWO_CHUNKS_SVG = new File(Fixtures.ACTION_SVG_DIR, "twoChunks.svg");
	public static final File TWO_CHUNKS1_PDF = new File(Fixtures.ACTION_PDF_DIR, "twoChunks1.pdf");
	public static final File TWO_COLUMNS_PDF = new File(Fixtures.ACTION_PDF_DIR, "twoColumns.pdf");
	public static final File BMC310_PDF = new File(Fixtures.ACTION_PDF_DIR, "bmc11-310.pdf");
	public static final File BMC313_PDF = new File(Fixtures.ACTION_PDF_DIR, "bmc11-313.pdf");
	public static final File SUSCRIPTS_PDF = new File(Fixtures.ACTION_PDF_DIR, "suscripts.pdf");
	public static final File FONT_STYLES_PDF = new File(Fixtures.ACTION_PDF_DIR, "fontStyles.pdf");
	
	public static final File SVG_AJC_DIR = new File(Fixtures.SVG_DIR, "ajc");
	public static final File SVG_AJC_PAGE6_SPLIT_SVG = new File(Fixtures.SVG_AJC_DIR, "ajc_page6_split.svg");

	public final static File TARGET = new File("target");
	public final static File TEST_PDFTOP = new File(RESOURCES_DIR+"pdfs");
	public final static File EXT_PDFTOP = new File("../pdfs");
	public final static File SVGTOP = new File(RESOURCES_DIR+"svg");
	
	public final static File BMCINDIR = new File(TEST_PDFTOP, "bmc");
	public final static File BMCOUTDIR = new File(TARGET, "bmc");
	public final static File BMCSVGDIR = new File(SVGTOP, "bmc");
	
	public final static File ELIFEINDIR = new File(TEST_PDFTOP, "elife");
	public final static File ELIFEOUTDIR = new File(TARGET, "elife");
	public final static File ELIFESVGDIR = new File(SVGTOP, "elife");

	public final static File MISCINDIR = new File(TEST_PDFTOP, "misc");
	public final static File MISCOUTDIR = new File(TARGET, "misc");
	public final static File MISCSVGDIR = new File(SVGTOP, "misc");

	public final static File PEERJINDIR = new File(TEST_PDFTOP, "peerj");
	public final static File PEERJOUTDIR = new File(TARGET, "peerj");
	public final static File PEERJSVGDIR = new File(SVGTOP, "peerj");

	public final static File ANYINDIR = new File(TEST_PDFTOP, "any");
	public final static File ANYOUTDIR = new File(TARGET, "any");
	public final static File ANYSVGDIR = new File(SVGTOP, "any");

	public static final File ANALYZER_DIR = new File(SVG2XML_DIR+"analyzer/");
	/** a 4 line chunk (paragraph) with no suscripts */
	public static final File PARA1_SVG = new File(Fixtures.ANALYZER_DIR, "1parachunk.svg");
	// 3 paragraphs
	public static final File PARA_SUSCRIPT_SVG = new File(Fixtures.ANALYZER_DIR, "parasWithSuscripts.svg");
	private static final File LINE1_SVG = new File(Fixtures.ANALYZER_DIR, "singleLine.svg");

	public static File PAGE3RESULTS_SVG = new File(Fixtures.ANALYZER_DIR, "page3results.svg");
// text stuff	
	public static final File TEXT_DIR = new File(SVG2XML_DIR+"text/");
// whole paper	
	public static final File PDFS_BMC_DIR = new File(PDFS_DIR, "bmc/");
	public static final File SVG_BMC_DIR = new File(SVG1_DIR, "bmc/");
	public static final File MULTIPLE312_DIR = new File(PDFS_BMC_DIR, "multiple-1471-2148-11-312/");
	public static final File MULTIPLE312_PDF = new File(PDFS_BMC_DIR, "multiple-1471-2148-11-312.pdf");
	public static final File SVG_MULTIPLE312_DIR = new File(SVG_BMC_DIR, "multiple-1471-2148-11-312/");
	public static final File HTML_MULTIPLE312_DIR = new File(MULTIPLE312_DIR, "html/");
	
	public static final File RAW_MULTIPLE312_SVG_PAGE1 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page1.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE2 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page2.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE3 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page3.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE4 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page4.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE5 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page5.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE6 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page6.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE7 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page7.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE8 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page8.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE9 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page9.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE10 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page10.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE11 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page11.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE12 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page12.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE13 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page13.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE14 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page14.svg");

	public static final File G2_3_3_SVG = new File(SVG_MULTIPLE312_DIR, "chunk.g.2.3.3.svg");

	public final static String MATH_ROOT = "maths-1471-2148-11-311";
	public static final File MATH311_DIR = new File(PDFS_BMC_DIR, MATH_ROOT+"/");
	public static final File MATH311_PDF = new File(PDFS_BMC_DIR, MATH_ROOT+".pdf");
	public static final File SVG_MATH311_DIR = new File(SVG_BMC_DIR, MATH_ROOT+"/");
	public static final File HTML_MATH311_DIR = new File(MATH311_DIR, "html/");
	
	public static final File RAW_MATH311_SVG_PAGE1 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page1.svg");
	public static final File RAW_MATH311_SVG_PAGE2 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page2.svg");
	public static final File RAW_MATH311_SVG_PAGE3 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page3.svg");
	public static final File RAW_MATH311_SVG_PAGE4 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page4.svg");
	public static final File RAW_MATH311_SVG_PAGE5 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page5.svg");
	public static final File RAW_MATH311_SVG_PAGE6 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page6.svg");
	public static final File RAW_MATH311_SVG_PAGE7 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page7.svg");
	public static final File RAW_MATH311_SVG_PAGE8 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page8.svg");
	public static final File RAW_MATH311_SVG_PAGE9 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page9.svg");
	public static final File RAW_MATH311_SVG_PAGE10 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page10.svg");
	public static final File RAW_MATH311_SVG_PAGE11 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page11.svg");
	public static final File RAW_MATH311_SVG_PAGE12 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page12.svg");
	public static final File RAW_MATH311_SVG_PAGE13 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page13.svg");
	public static final File RAW_MATH311_SVG_PAGE14 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page14.svg");

	public final static String GEO_ROOT = "geotable-1471-2148-11-310";
	public static final File GEO310_DIR = new File(PDFS_BMC_DIR, GEO_ROOT+"/");
	public static final File GEO310_PDF = new File(PDFS_BMC_DIR, GEO_ROOT+".pdf");
	public static final File SVG_GEO310_DIR = new File(SVG_BMC_DIR, GEO_ROOT+"/");
	public static final File HTML_GEO310_DIR = new File(GEO310_DIR, "html/");
	
	public static final File RAW_GEO310_SVG_PAGE1 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page1.svg");
	public static final File RAW_GEO310_SVG_PAGE2 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page2.svg");
	public static final File RAW_GEO310_SVG_PAGE3 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page3.svg");
	public static final File RAW_GEO310_SVG_PAGE4 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page4.svg");
	public static final File RAW_GEO310_SVG_PAGE5 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page5.svg");
	public static final File RAW_GEO310_SVG_PAGE6 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page6.svg");
	public static final File RAW_GEO310_SVG_PAGE7 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page7.svg");
	public static final File RAW_GEO310_SVG_PAGE8 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page8.svg");
	public static final File RAW_GEO310_SVG_PAGE9 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page9.svg");
	public static final File RAW_GEO310_SVG_PAGE10 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page10.svg");
	public static final File RAW_GEO310_SVG_PAGE11 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page11.svg");
	public static final File RAW_GEO310_SVG_PAGE12 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page12.svg");
	public static final File RAW_GEO310_SVG_PAGE13 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page13.svg");
	public static final File RAW_GEO310_SVG_PAGE14 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page14.svg");

	public static void drawChunkBoxes(List<Chunk> finalChunkList) {
		for (Chunk chunk : finalChunkList) {
			SVGRect bbox = chunk.createGraphicalBoundingBox();
			if (bbox != null) {
				chunk.appendChild(bbox);
			}
		}
	}
	
	public final static SVGElement createSVGElement(File file) {
		SVGElement svgElement =  null;
		try {
			svgElement = SVGElement.readAndCreateSVG(new Builder().build(file).getRootElement());
		} catch (Exception e) {
			throw new RuntimeException("Cannot create SVGElement", e);
		}
		return svgElement;
	}
	
	/** page numbered from ONE
	 * 
	 * @param file
	 * @param page
	 * @return
	 */
	public static SVGSVG getSVGPageFromPDF(File file, int page) {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir target "+file);
		SVGSVG svgPage = (page < 1 || page > converter.getPageList().size()) ? null : converter.getPageList().get(page-1);
		return svgPage;
	}
	
	public static SVGSVG createSVGPage(File svgFile) {
		SVGSVG svgPage = null;
		try {
			svgPage = (SVGSVG) SVGElement.readAndCreateSVG(new Builder().build(svgFile).getRootElement());
		} catch (Exception e){
			throw new RuntimeException("Cannot create SVG: ", e);
		}
		return svgPage;
	}

	public static SVGSVG createChunkedSVGPage(File pdfFile, int pageNum) {
		SVGSVG svgPage = Fixtures.getSVGPageFromPDF(pdfFile, pageNum);
		LOG.debug("svgPage "+svgPage.query("//*").size());
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX();
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgPage);
		return svgPage;
	}

	public static List<Chunk> createLeafChunks(File pdfFile, int pageNum) {
		SVGSVG svgPage = createChunkedSVGPage(pdfFile, pageNum);
		List<Chunk> chunkList = Chunk.extractChunks(SVGUtil.getQuerySVGElements(svgPage, ".//svg:g[@LEAF]"));
		return chunkList;
	}





}
