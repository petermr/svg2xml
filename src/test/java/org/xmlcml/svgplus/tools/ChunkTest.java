package org.xmlcml.svgplus.tools;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.pdf2svg.util.PDF2SVGUtil;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.command.PageEditor;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;

public class ChunkTest {

	private final static Logger LOG = Logger.getLogger(ChunkTest.class);
	
	private static final int _PAGE0_SIZE = 351 - 1;

	@Test
	public void testChunkFromPageEditorWithPage() {
		SemanticDocumentAction semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		PageEditor pageEditor = semanticDocumentAction.getPageEditor();
		Assert.assertNotNull("page editor should not be null", pageEditor);
		Chunk chunk = new Chunk(pageEditor.getSVGPage());
		String roleValue = chunk.getAttributeValue(Chunk.ROLE, PDF2SVGUtil.SVGX_NS);
		Assert.assertEquals("role", Chunk.CHUNK, roleValue);
	}
	
	@Test
	public void testGetDescendantElementList() {
		SemanticDocumentAction semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		Chunk chunk = new Chunk(semanticDocumentAction.getPageEditor().getSVGPage());
		List<SVGElement> descendantList = chunk.getDescendantSVGElementList();
		Assert.assertNotNull("descendantlist should not be null", descendantList);
		Assert.assertTrue("descendantlist should not be empty", descendantList.size() >0);
		Assert.assertEquals("descendantlist", _PAGE0_SIZE, descendantList.size());
	}
	
	
	@Test
	@Ignore // page count wrong
	public void testBoundingBoxManager() {
		SemanticDocumentAction semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		Chunk chunk = new Chunk(semanticDocumentAction.getPageEditor().getSVGPage());
		chunk.createElementListAndCalculateBoundingBoxes();
		BoundingBoxManager boundingBoxManager = chunk.getBoundingBoxManager();
		Assert.assertNotNull("bbox manager not null", boundingBoxManager);
		List<Real2Range> bboxList = boundingBoxManager.getBBoxList();
		Assert.assertNotNull("bbox list not null", bboxList);
		Assert.assertEquals("bbox list ", _PAGE0_SIZE, bboxList.size());
		
	}
	
	@Test
	public void testSplitNonOverlappingBoxes() {
		SVGSVG svgPage = new SVGSVG();
		svgPage.appendChild(new SVGRect(new Real2Range(new RealRange(0., 100.), new RealRange(0., 100.))));
		svgPage.appendChild(new SVGRect(new Real2Range(new RealRange(200., 300.), new RealRange(300., 400.))));
		svgPage.appendChild(new SVGRect(new Real2Range(new RealRange(400., 500.), new RealRange(550., 600.))));
		Chunk chunk = new Chunk(svgPage);
		List<Chunk> chunkxList = chunk.splitIntoChunks(10.0, BoxEdge.XMIN);
		checkChunkList(
				new Real2Range[] {
						new Real2Range(new RealRange(0., 100.), new RealRange(0., 100.)),
						new Real2Range(new RealRange(200., 300.), new RealRange(300., 400.)),
						new Real2Range(new RealRange(400., 500.), new RealRange(550., 600.)),
				},
				chunkxList,
				0.001);
		chunk = new Chunk(svgPage);
		List<Chunk> chunkyList = chunk.splitIntoChunks(10.0, BoxEdge.YMIN);
		checkChunkList(
				new Real2Range[] {
						new Real2Range(new RealRange(0., 100.), new RealRange(0., 100.)),
						new Real2Range(new RealRange(200., 300.), new RealRange(300., 400.)),
						new Real2Range(new RealRange(400., 500.), new RealRange(550., 600.)),
				},
				chunkyList,
				0.001);
	}
	
	@Test
	public void testSplitOverlappingBoxes() {
		SVGSVG svgPage = new SVGSVG();
		svgPage.appendChild(new SVGRect(new Real2Range(new RealRange(0., 300.), new RealRange(0., 100.))));
		svgPage.appendChild(new SVGRect(new Real2Range(new RealRange(400., 550.), new RealRange(400., 600.))));
		svgPage.appendChild(new SVGRect(new Real2Range(new RealRange(250., 350.), new RealRange(200., 500.))));
		Chunk chunk = new Chunk(svgPage);
		List<Chunk> chunkxList = chunk.splitIntoChunks(10.0, BoxEdge.XMIN);
		checkChunkList(
				new Real2Range[] {
						new Real2Range(new RealRange(0., 350.), new RealRange(0., 500.)),
						new Real2Range(new RealRange(400., 550.), new RealRange(400., 600.)),
				},
				chunkxList,
				0.001);
		chunk = new Chunk(svgPage);
		List<Chunk> chunkyList = chunk.splitIntoChunks(10.0, BoxEdge.YMIN);
		checkChunkList(
				new Real2Range[] {
						new Real2Range(new RealRange(0., 300.), new RealRange(0., 100.)),
						new Real2Range(new RealRange(250., 550.), new RealRange(200., 600.)),
				},
				chunkyList,
				0.001);
	}
	
	@Test
	@Ignore // no idea what is wrong - should get text bouonding boxes
	public void testSplit() {
		SemanticDocumentAction semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		Chunk chunk = new Chunk(semanticDocumentAction.getPageEditor().getSVGPage());
		LOG.debug("BBOXES "+chunk.getBoundingBoxManager().getBBoxList().size());
		List<Chunk> chunkList = chunk.splitIntoChunks(10.0, BoxEdge.YMIN);
		Assert.assertNotNull("split list not null", chunkList);
		Assert.assertEquals("split list ", 10, chunkList.size());
	}

	private void checkChunkList(Real2Range[] refList, List<? extends SVGElement> elementList, double eps) {
		List<Real2Range> elementBoxes = BoundingBoxManager.createBBoxList(elementList);
		BoundingBoxManagerTest.checkReal2RangeList(
				refList,
				elementBoxes,
				eps);
	}

}
