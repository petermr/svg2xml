package org.xmlcml.svg2xml.action;

import java.io.FileOutputStream;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGDefs;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.analyzer.ChunkAnalyzerX;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;
import org.xmlcml.svg2xml.analyzer.WhitespaceChunkerAnalyzerX;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.tools.Chunk;

public class ChunkAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(ChunkAnalyzerTest.class);
	
	@Test
	public void testChunkAnalyze() throws Exception {
		AbstractActionX semanticDocumentAction = SemanticDocumentActionX.createSemanticDocument(Fixtures.CHUNK_ANALYZE);
		semanticDocumentAction.run();
		// just checks it runs OK
	}


	@Test
	// runs on twoChunks.pdf
	// splits into 2 simple chunks
	public void testTwoChunks0() throws Exception {
		AbstractActionX semanticDocumentAction = SemanticDocumentActionX.createSemanticDocument(Fixtures.CHUNK_ANALYZE0);
		Assert.assertNotNull("semanticDocumentAction not null", semanticDocumentAction);
		semanticDocumentAction.run();
		SVGElement svgPage = semanticDocumentAction.getSVGPage();
		Assert.assertNotNull("svgPage not null", svgPage);
		CMLUtil.debug(svgPage, new FileOutputStream("target/twoChunks0.svg"), 1);
		Assert.assertEquals("child Elements", 5, svgPage.getChildElements().size()); // we have 2 script elements - get rid ?
		SVGDefs defs = (SVGDefs) svgPage.getChildElements().get(0);
		SVGG g1 = (SVGG) svgPage.getChildElements().get(1);
		List<SVGElement> texts = SVGUtil.getQuerySVGElements(g1, "./svg:g/svg:g/svg:text");
		Assert.assertEquals("text Elements 1", 7, texts.size()); // includes a space
		SVGText text0 = (SVGText) texts.get(0);
		Assert.assertTrue("text0 coords",  new Real2(72.024, 81.52).isEqualTo(text0.getXY(), 0.001));
		Assert.assertEquals("text0 value",  "c", text0.getText());
		SVGG g2 = (SVGG) svgPage.getChildElements().get(2);
		texts = SVGUtil.getQuerySVGElements(g2, "./svg:g/svg:g/svg:text");
		Assert.assertEquals("text Elements 1", 7, texts.size()); // includes a space
		SVGText text5 = (SVGText) texts.get(5);
		Assert.assertTrue("text5 coords",  new Real2(99.006, 106.98).isEqualTo(text5.getXY(), 0.001));
		Assert.assertEquals("text5 value",  "1", text5.getText());
	}

	@Test
	// does the same but without reading semanticDocument (commandfile)
	public void testTwoChunksSVGElement() throws Exception {
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(semanticDocumentAction);
		SVGElement svgElement = Fixtures.createSVGElement(Fixtures.TWO_CHUNKS_SVG);
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
		SVGSVG svgPage = (SVGSVG) svgElement;
		Assert.assertEquals("child Elements", 3, svgPage.getChildElements().size()); // no script elements
		SVGDefs defs = (SVGDefs) svgPage.getChildElements().get(0);
		SVGG g1 = (SVGG) svgPage.getChildElements().get(1);
		List<SVGElement> texts = SVGUtil.getQuerySVGElements(g1, "./svg:g/svg:g/svg:text");
		Assert.assertEquals("text Elements 1", 7, texts.size()); // includes a space
		SVGText text0 = (SVGText) texts.get(0);
		Assert.assertTrue("text0 coords",  new Real2(72.024, 81.52).isEqualTo(text0.getXY(), 0.001));
		Assert.assertEquals("text0 value",  "c", text0.getText());
		SVGG g2 = (SVGG) svgPage.getChildElements().get(2);
		texts = SVGUtil.getQuerySVGElements(g2, "./svg:g/svg:g/svg:text");
		Assert.assertEquals("text Elements 1", 7, texts.size()); // includes a space
		SVGText text5 = (SVGText) texts.get(5);
		Assert.assertTrue("text5 coords",  new Real2(99.006, 106.98).isEqualTo(text5.getXY(), 0.001));
		Assert.assertEquals("text5 value",  "1", text5.getText());
	}

	@Test
	public void testTwoLargerChunks() throws Exception {
		SVGSVG svgPage = Fixtures.createChunkedSVGPage(Fixtures.TWO_CHUNKS1_PDF, 1);
		Assert.assertEquals("child Elements", 3, svgPage.getChildElements().size()); // no script elements
		SVGG g1 = (SVGG) svgPage.getChildElements().get(1);
		List<SVGElement> texts = SVGUtil.getQuerySVGElements(g1, "./svg:g/svg:g/svg:text");
		Assert.assertEquals("text Elements 1", 168, texts.size());
		SVGText text0 = (SVGText) texts.get(0);
		Assert.assertTrue("text0 coords "+text0.getXY(),  new Real2(72.024, 81.4).isEqualTo(text0.getXY(), 0.001));
		Assert.assertEquals("text0 value",  "T", text0.getText());
		SVGG g2 = (SVGG) svgPage.getChildElements().get(2);
		texts = SVGUtil.getQuerySVGElements(g2, "./svg:g/svg:g/svg:text");
		Assert.assertEquals("text Elements 1", 214, texts.size()); // includes a space
		SVGText text5 = (SVGText) texts.get(5);
		Assert.assertTrue("text5 coords "+text5.getXY(),  new Real2(96.257, 122.34).isEqualTo(text5.getXY(), 0.001));
		Assert.assertEquals("text5 value",  "i", text5.getText());
	}
	
	@Test
	public void testTwoLargerChunkTypes() throws Exception {
		List<Chunk> leafChunks = Fixtures.createLeafChunks(Fixtures.TWO_CHUNKS1_PDF, 1);
		ChunkAnalyzerX chunkAnalyzer = new ChunkAnalyzerX();
		chunkAnalyzer.analyzeChunk(leafChunks.get(0));
		TextAnalyzerX textAnalyzerX = chunkAnalyzer.getTextAnalyzerX();
		textAnalyzerX.getTextLineByYCoordMap();
		List<TextLine> lineList = textAnalyzerX.getLinesInIncreasingY();
//		CMLUtil.debug(svgPage, new FileOutputStream("target/chunkAnalyzer/twoChunks1.svg"), 1);
	}
		

	@Test
	public void testTwoColumns() throws Exception {
		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.TWO_COLUMNS_PDF, 1);
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(semanticDocumentAction);
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
		CMLUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/twoColumn.svg"), 1);
		SVGSVG svgPage = (SVGSVG) svgElement;
		List<SVGG> leafGs = SVGG.extractGs(SVGUtil.getQuerySVGElements(svgPage, ".//svg:g[@LEAF='3']"));
		Assert.assertEquals("leafs ", 14, leafGs.size()); // there is a near zero (single space para)
		for (SVGG g : leafGs) {
			SVGRect box = g.drawBox("red", "yellow", 1.0, 0.3);
		}
		CMLUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/twoColumnBoxes.svg"), 1);
	}

	@Test
	public void testTwoColumnTypes() throws Exception {
		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.TWO_COLUMNS_PDF, 1);
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(semanticDocumentAction);
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
		SVGSVG svgPage = (SVGSVG) svgElement;
		List<SVGG> leafGs = SVGG.extractGs(SVGUtil.getQuerySVGElements(svgPage, ".//svg:g[@LEAF='3']"));
		for (SVGG g : leafGs) {
			Chunk chunk =(Chunk) g;
		}
		CMLUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/twoColumnBoxes.svg"), 1);
	}

	
	@Test
	@Ignore // throws GC overhead limit exceeded in maven //this is probably clunky string handling
	public void testBMC310_1() throws Exception {
		
		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.BMC310_PDF, 1);
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(semanticDocumentAction);
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
		CMLUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/bmc310_1.svg"), 1);
	}

	@Test
	@Ignore // throws GC overhead limit exceeded in maven
	public void testBMC313_2() throws Exception {
		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.BMC313_PDF, 2);
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(semanticDocumentAction);
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
		CMLUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/bmc313_2.svg"), 2);
	}

	@Test
	@Ignore // throws GC overhead limit exceeded in maven
	public void testBMC313_3() throws Exception {
		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.BMC313_PDF, 3);
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(semanticDocumentAction);
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
		CMLUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/bmc313_3.svg"), 2);
	}

	@Test
	public void testSuscripts() throws Exception {
		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.SUSCRIPTS_PDF, 1);
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(semanticDocumentAction);
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
		CMLUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/suscripts.svg"), 2);
	}


}
