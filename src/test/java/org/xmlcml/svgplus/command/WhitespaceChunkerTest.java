package org.xmlcml.svgplus.command;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.core.SVGPlusConverter;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.tools.PageChunkSplitterAnalyzer;

public class WhitespaceChunkerTest {

	@Test
	public void testWhitespaceChunker() {
		SVGPlusConverter converter = new SVGPlusConverter();
		converter.run("-c "+Fixtures.WHITESPACE_CHUNKER_TST);
	}

	@Test
	public void testPageSplitter0() throws Exception {
		SemanticDocumentAction semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		PageChunkSplitterAnalyzer pageChunkSplitterAnalyzer = new PageChunkSplitterAnalyzer(semanticDocumentAction);
		List<Chunk> finalChunkList = pageChunkSplitterAnalyzer.splitByWhitespace();
		Assert.assertNotNull(finalChunkList);
	}

}
