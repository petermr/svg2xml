package org.xmlcml.svgplus.command;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.core.SVGPlusConverter;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.tools.PageChunkSplitterAnalyzer;

public class WhitespaceChunkerTest {

	@Test
	public void testWhitespaceChunkerRuns() {
		SVGPlusConverter converter = new SVGPlusConverter();
		converter.run("-c "+Fixtures.WHITESPACE_CHUNKER_COMMAND);
	}

	@Test
	public void testPageSplitter0() throws Exception {
		SemanticDocumentAction semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		SVGSVG svgPage = semanticDocumentAction.getSVGPage();
		PageChunkSplitterAnalyzer pageChunkSplitterAnalyzer = new PageChunkSplitterAnalyzer(semanticDocumentAction);
		List<Chunk> finalChunkList = pageChunkSplitterAnalyzer.splitByWhitespace();
		Assert.assertNotNull(finalChunkList);
		Assert.assertEquals("chunks", 15, finalChunkList.size());
		drawChunkBoxes(semanticDocumentAction, finalChunkList);
		CMLUtil.debug(semanticDocumentAction.getSVGPage(), new FileOutputStream("target/pageSplitter0.svg"), 1);
	}

	@Test
	public void testHarter3() throws Exception {
		SemanticDocumentAction semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.HARTER3_SVG);
		SVGSVG svgPage = semanticDocumentAction.getSVGPage();
		PageChunkSplitterAnalyzer pageChunkSplitterAnalyzer = new PageChunkSplitterAnalyzer(semanticDocumentAction);
		List<Chunk> finalChunkList = pageChunkSplitterAnalyzer.splitByWhitespace();
		Assert.assertNotNull(finalChunkList);
		Assert.assertEquals("chunks", 28, finalChunkList.size());
		drawChunkBoxes(semanticDocumentAction, finalChunkList);
		CMLUtil.debug(semanticDocumentAction.getSVGPage(), new FileOutputStream("target/testHarter3.svg"), 1);
	}

	@Test
	public void testPolicies() {
		testSplit(Fixtures.POLICIES_SVG, 8, new File("target/policies.svg"));
	}
	
	@Test
	public void testPage6() {
		testSplit(Fixtures.AJC6_SVG, 14, new File("target/page6.svg"));
	}
	
	private static void drawChunkBoxes(SemanticDocumentAction semanticDocumentAction,
			List<Chunk> finalChunkList) {
		for (Chunk chunk : finalChunkList) {
			SVGRect bbox = chunk.createGraphicalBoundingBox();
			if (bbox != null) {
				chunk.appendChild(bbox);
			}
		}
	}

	private static void testSplit(File svgFile, int chunkCount, File outputFile) {
		try {
			SemanticDocumentAction semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(svgFile);
			SVGSVG svgPage = semanticDocumentAction.getSVGPage();
			PageChunkSplitterAnalyzer pageChunkSplitterAnalyzer = new PageChunkSplitterAnalyzer(semanticDocumentAction);
			List<Chunk> finalChunkList = pageChunkSplitterAnalyzer.splitByWhitespace();
			Assert.assertNotNull(finalChunkList);
			Assert.assertEquals("chunks", chunkCount, finalChunkList.size());
			drawChunkBoxes(semanticDocumentAction, finalChunkList);
			CMLUtil.debug(semanticDocumentAction.getSVGPage(), new FileOutputStream(outputFile), 1);
		} catch (Exception e){
			throw new RuntimeException("Cannot test", e);
		}
	}


}
