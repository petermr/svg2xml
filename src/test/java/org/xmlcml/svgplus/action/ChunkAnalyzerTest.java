package org.xmlcml.svgplus.action;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.analyzer.WhitespaceChunkerAnalyzerX;
import org.xmlcml.svgplus.tools.Chunk;

public class ChunkAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(ChunkAnalyzerTest.class);
	
	@Test
	public void testChunkAnalyze() throws Exception {
		SemanticDocumentActionX semanticDocumentAction = SemanticDocumentActionX.createSemanticDocument(Fixtures.CHUNK_ANALYZE);
		semanticDocumentAction.run();
	}

	@Test
	public void testChunkAnalyze0() throws Exception {
		SemanticDocumentActionX semanticDocumentAction = SemanticDocumentActionX.createSemanticDocument(Fixtures.CHUNK_ANALYZE0);
		semanticDocumentAction.run();
	}

	@Test
	public void testChunkAnalyze00() throws Exception {
//		SemanticDocumentActionX semanticDocumentAction = SemanticDocumentActionX.createSemanticDocument(Fixtures.NOOP_FILE);
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzerX = new WhitespaceChunkerAnalyzerX(semanticDocumentAction);
		SVGElement svgElement = Fixtures.createSVGElement(Fixtures.TWO_CHUNKS_SVG);
		whitespaceChunkerAnalyzerX.splitByWhitespaceAndLabelLeafNodes(svgElement);
		svgElement.debug("CHUNKED");
		
	}


}
