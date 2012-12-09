package org.xmlcml.svgplus;

import java.io.File;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svgplus.action.AbstractActionX;
import org.xmlcml.svgplus.action.SemanticDocumentActionX;
import org.xmlcml.svgplus.analyzer.WhitespaceChunkerAnalyzerX;
import org.xmlcml.svgplus.tools.Chunk;

public class Fixtures {

	public static final String SVGPLUS_DIR = "src/test/resources/org/xmlcml/svgplus/";
	public static final String COMMAND_DIR= SVGPLUS_DIR+"command/";
	public static final String CORE_DIR = SVGPLUS_DIR+"core/";
	public static final String ACTION_DIR = SVGPLUS_DIR+"action/";
	public static final File ACTION_SVG_DIR = new File(Fixtures.ACTION_DIR, "svg");
	public static final File ACTION_PDF_DIR = new File(Fixtures.ACTION_DIR, "pdf");
	
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
	
	public static AbstractActionX getSemanticDocumentAction(File commandFile) {
		AbstractActionX semanticDocumentAction = null;
		try {
			Element element = new Builder().build(commandFile).getRootElement();
			semanticDocumentAction = SemanticDocumentActionX.createSemanticDocument(element);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create semanticDocumentAction ", e);
		}
		return semanticDocumentAction;
	}
	
	public static SemanticDocumentActionX createSemanticDocumentActionWithSVGPage(File svgPageFile) {
		SemanticDocumentActionX semanticDocumentAction = null;
		try {
			Element element = new Builder().build(svgPageFile).getRootElement();
			SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(element);
			semanticDocumentAction = new SemanticDocumentActionX();
			semanticDocumentAction.getPageEditor().setSVGPage(svgPage);
		} catch (Exception e) {
			throw new RuntimeException("cannot create page: ", e);
		}
		return semanticDocumentAction;
	}

	public static void drawChunkBoxes(AbstractActionX semanticDocumentAction,
			List<Chunk> finalChunkList) {
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

	public static SVGSVG createChunkedSVGPage(File pdfFile, int pageNum) {
		SVGSVG svgPage = Fixtures.getSVGPageFromPDF(pdfFile, pageNum);
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(new SemanticDocumentActionX());
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgPage);
		return svgPage;
	}

	public static List<Chunk> createLeafChunks(File pdfFile, int pageNum) {
		SVGSVG svgPage = createChunkedSVGPage(pdfFile, pageNum);
		List<Chunk> chunkList = Chunk.extractChunks(SVGUtil.getQuerySVGElements(svgPage, ".//svg:g[@LEAF]"));
		return chunkList;
	}

}
