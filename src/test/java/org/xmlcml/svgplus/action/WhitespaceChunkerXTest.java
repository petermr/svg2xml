package org.xmlcml.svgplus.action;

import java.io.File;

import java.io.FileOutputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.analyzer.PageChunkSplitterAnalyzerX;
import org.xmlcml.svgplus.tools.Chunk;

public class WhitespaceChunkerXTest {

	@Test
	public void testPageSplitter0() throws Exception {
		SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		SVGSVG svgPage = semanticDocumentAction.getSVGPage();
		PageChunkSplitterAnalyzerX pageChunkSplitterAnalyzer = new PageChunkSplitterAnalyzerX(semanticDocumentAction);
		List<Chunk> finalChunkList = pageChunkSplitterAnalyzer.splitByWhitespace();
		Assert.assertNotNull(finalChunkList);
		Assert.assertEquals("chunks", 15, finalChunkList.size());
		drawChunkBoxes(semanticDocumentAction, finalChunkList);
		CMLUtil.debug(semanticDocumentAction.getSVGPage(), new FileOutputStream("target/pageSplitter0.svg"), 1);
	}

	@Test
	public void testHarter3() throws Exception {
		SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.HARTER3_SVG);
		SVGSVG svgPage = semanticDocumentAction.getSVGPage();
		PageChunkSplitterAnalyzerX pageChunkSplitterAnalyzer = new PageChunkSplitterAnalyzerX(semanticDocumentAction);
		List<Chunk> finalChunkList = pageChunkSplitterAnalyzer.splitByWhitespace();
		Assert.assertNotNull(finalChunkList);
		Assert.assertEquals("chunks", 29, finalChunkList.size());
		drawChunkBoxes(semanticDocumentAction, finalChunkList);
		CMLUtil.debug(semanticDocumentAction.getSVGPage(), new FileOutputStream("target/testHarter3.svg"), 1);
	}

	@Test
	public void testHarter3small() throws Exception {
		SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.HARTER3SMALL_SVG);
		SVGSVG svgPage = semanticDocumentAction.getSVGPage();
		PageChunkSplitterAnalyzerX pageChunkSplitterAnalyzer = new PageChunkSplitterAnalyzerX(semanticDocumentAction);
		List<Chunk> finalChunkList = pageChunkSplitterAnalyzer.splitByWhitespace();
		Assert.assertNotNull(finalChunkList);
		Assert.assertEquals("chunks", 29, finalChunkList.size());
		drawChunkBoxes(semanticDocumentAction, finalChunkList);
		CMLUtil.debug(semanticDocumentAction.getSVGPage(), new FileOutputStream("target/testHarter3small.svg"), 1);
	}

	@Test
	public void testPolicies() {
		testSplit(Fixtures.POLICIES_SVG, 7, new File("target/policies.svg"));
	}
	
	@Test
	public void testPage6() {
		testSplit(Fixtures.AJC6_SVG, 14, new File("target/page6.svg"));
	}
	
	private static void drawChunkBoxes(SemanticDocumentActionX semanticDocumentAction,
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
			SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(svgFile);
			SVGSVG svgPage = semanticDocumentAction.getSVGPage();
			PageChunkSplitterAnalyzerX pageChunkSplitterAnalyzer = new PageChunkSplitterAnalyzerX(semanticDocumentAction);
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
