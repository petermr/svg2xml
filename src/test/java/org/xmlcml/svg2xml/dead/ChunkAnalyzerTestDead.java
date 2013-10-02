package org.xmlcml.svg2xml.dead;

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
import org.xmlcml.svg2xml.dead.ChunkAnalyzerXDead;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.page.WhitespaceChunkerAnalyzerX;
import org.xmlcml.svg2xml.paths.Chunk;
import org.xmlcml.svg2xml.text.TextLine;

//@Ignore //uses old code
public class ChunkAnalyzerTestDead {

//	private final static Logger LOG = Logger.getLogger(ChunkAnalyzerTestDead.class);
//	
//	@Test
//	// does the same but without reading semanticDocument (commandfile)
//	public void testTwoChunksSVGElement() throws Exception {
//		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX();
//		SVGElement svgElement = Fixtures.createSVGElement(Fixtures.TWO_CHUNKS_SVG);
//		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
//		SVGSVG svgPage = (SVGSVG) svgElement;
//		Assert.assertEquals("child Elements", 3, svgPage.getChildElements().size()); // no script elements
//		SVGDefs defs = (SVGDefs) svgPage.getChildElements().get(0);
//		SVGG g1 = (SVGG) svgPage.getChildElements().get(1);
//		List<SVGElement> texts = SVGUtil.getQuerySVGElements(g1, "./svg:g/svg:g/svg:text");
//		Assert.assertEquals("text Elements 1", 7, texts.size()); // includes a space
//		SVGText text0 = (SVGText) texts.get(0);
//		Assert.assertTrue("text0 coords",  new Real2(72.024, 81.52).isEqualTo(text0.getXY(), 0.001));
//		Assert.assertEquals("text0 value",  "c", text0.getText());
//		SVGG g2 = (SVGG) svgPage.getChildElements().get(2);
//		texts = SVGUtil.getQuerySVGElements(g2, "./svg:g/svg:g/svg:text");
//		Assert.assertEquals("text Elements 1", 7, texts.size()); // includes a space
//		SVGText text5 = (SVGText) texts.get(5);
//		Assert.assertTrue("text5 coords",  new Real2(99.006, 106.98).isEqualTo(text5.getXY(), 0.001));
//		Assert.assertEquals("text5 value",  "1", text5.getText());
//	}
//
//	@Test
//	public void testTwoLargerChunks() throws Exception {
//		SVGSVG svgPage = Fixtures.createChunkedSVGPage(Fixtures.TWO_CHUNKS1_PDF, 1);
//		Assert.assertEquals("child Elements", 3, svgPage.getChildElements().size()); // no script elements
//		SVGG g1 = (SVGG) svgPage.getChildElements().get(1);
//		List<SVGElement> texts = SVGUtil.getQuerySVGElements(g1, "./svg:g/svg:g/svg:text");
//		Assert.assertEquals("text Elements 1", 168, texts.size());
//		SVGText text0 = (SVGText) texts.get(0);
//		Assert.assertTrue("text0 coords "+text0.getXY(),  new Real2(72.024, 81.4).isEqualTo(text0.getXY(), 0.001));
//		Assert.assertEquals("text0 value",  "T", text0.getText());
//		SVGG g2 = (SVGG) svgPage.getChildElements().get(2);
//		texts = SVGUtil.getQuerySVGElements(g2, "./svg:g/svg:g/svg:text");
//		Assert.assertEquals("text Elements 1", 214, texts.size()); // includes a space
//		SVGText text5 = (SVGText) texts.get(5);
//		Assert.assertTrue("text5 coords "+text5.getXY(),  new Real2(96.257, 122.34).isEqualTo(text5.getXY(), 0.001));
//		Assert.assertEquals("text5 value",  "i", text5.getText());
//	}
//	
////	@Test
////	public void testTwoLargerChunkTypes() throws Exception {
////		List<Chunk> leafChunks = Fixtures.createLeafChunks(Fixtures.TWO_CHUNKS1_PDF, 1);
////		ChunkAnalyzerXOld chunkAnalyzer = new ChunkAnalyzerXOld();
////		chunkAnalyzer.analyzeChunk(leafChunks.get(0));
////		TextAnalyzer textAnalyzerX = chunkAnalyzer.getTextAnalyzerX();
////		textAnalyzerX.getTextLineByYCoordMap();
////		List<TextLine> lineList = textAnalyzerX.getLinesInIncreasingY();
//////		SVGUtil.debug(svgPage, new FileOutputStream("target/chunkAnalyzer/twoChunks1.svg"), 1);
////	}
//		
//
////	@Test
////	public void testTwoColumns() throws Exception {
////		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.TWO_COLUMNS_PDF, 1);
////		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX();
////		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
////		SVGUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/twoColumn.svg"), 1);
////		SVGSVG svgPage = (SVGSVG) svgElement;
////		List<SVGG> leafGs = SVGG.extractGs(SVGUtil.getQuerySVGElements(svgPage, ".//svg:g[@LEAF='3']"));
////		Assert.assertEquals("leafs ", 14, leafGs.size()); // there is a near zero (single space para)
////		for (SVGG g : leafGs) {
////			SVGRect box = g.drawBox("red", "yellow", 1.0, 0.3);
////		}
////		SVGUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/twoColumnBoxes.svg"), 1);
////	}
//
////	@Test
////	public void testTwoColumnTypes() throws Exception {
////		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.TWO_COLUMNS_PDF, 1);
////		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX();
////		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
////		SVGSVG svgPage = (SVGSVG) svgElement;
////		List<SVGG> leafGs = SVGG.extractGs(SVGUtil.getQuerySVGElements(svgPage, ".//svg:g[@LEAF='3']"));
////		for (SVGG g : leafGs) {
////			Chunk chunk =(Chunk) g;
////		}
////		SVGUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/twoColumnBoxes.svg"), 1);
////	}
//
//	
//	@Test
//	@Ignore // throws GC overhead limit exceeded in maven //this is probably clunky string handling
//	public void testBMC310_1() throws Exception {
//		
//		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.BMC310_PDF, 1);
//		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX();
//		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
//		SVGUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/bmc310_1.svg"), 1);
//	}
//
////	@Test
//////	@Ignore // throws GC overhead limit exceeded in maven
////	public void testBMC313_2() throws Exception {
////		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.BMC313_PDF, 2);
////		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX();
////		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
////		SVGUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/bmc313_2.svg"), 2);
////	}
////
////	@Test
//////	@Ignore // throws GC overhead limit exceeded in maven
////	public void testBMC313_3() throws Exception {
////		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.BMC313_PDF, 3);
////		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX();
////		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
////		SVGUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/bmc313_3.svg"), 2);
////	}
//
//	@Test
//	public void testSuscripts() throws Exception {
//		SVGElement svgElement = Fixtures.getSVGPageFromPDF(Fixtures.SUSCRIPTS_PDF, 1);
//		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX();
//		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
//		SVGUtil.debug(svgElement, new FileOutputStream("target/chunkAnalyzer/suscripts.svg"), 2);
//	}
//
//
}
