package org.xmlcml.svgplus.action;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGSVG;
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


}
