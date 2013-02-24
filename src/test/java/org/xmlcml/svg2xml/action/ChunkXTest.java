package org.xmlcml.svg2xml.action;

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
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svgplus.tools.BoundingBoxManager;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;

public class ChunkXTest {

	private final static Logger LOG = Logger.getLogger(ChunkXTest.class);
	
	private static final int _PAGE0_SIZE = 351 - 1;

	@Test
	public void testChunkFromPageEditorWithPage() {
		SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		PageEditorX pageEditor = semanticDocumentAction.getPageEditor();
		Assert.assertNotNull("page editor should not be null", pageEditor);
		Chunk chunk = new Chunk(pageEditor.getSVGPage());
		String roleValue = chunk.getAttributeValue(Chunk.ROLE, PDF2SVGUtil.SVGX_NS);
		Assert.assertEquals("role", Chunk.CHUNK, roleValue);
	}
	
	@Test
	public void testGetDescendantElementList() {
		SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		Chunk chunk = new Chunk(semanticDocumentAction.getPageEditor().getSVGPage());
		List<SVGElement> descendantList = chunk.getDescendantSVGElementListWithoutDefsDescendants();
		Assert.assertNotNull("descendantlist should not be null", descendantList);
		Assert.assertTrue("descendantlist should not be empty", descendantList.size() >0);
		Assert.assertEquals("descendantlist", _PAGE0_SIZE, descendantList.size());
	}
	
	
	@Test
	@Ignore // page count wrong
	public void testBoundingBoxManager() {
		SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
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
	public void testSplit() {
		SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.PAGE0_SVG);
		Chunk chunk = new Chunk(semanticDocumentAction.getPageEditor().getSVGPage());
		chunk.createElementListAndCalculateBoundingBoxes();
		LOG.debug("BBOXES "+chunk.getBoundingBoxManager().getBBoxList().size());
		List<Chunk> chunkList = chunk.splitIntoChunks(10.0, BoxEdge.YMIN);
		Assert.assertNotNull("split list not null", chunkList);
		Assert.assertEquals("split list ", 15, chunkList.size());
		checkChunkList(
				new Real2Range[] {
        new Real2Range(new RealRange(72.024,133.191), new RealRange(29.48,40.52)),
        new Real2Range(new RealRange(72.024,174.111), new RealRange(53.86,64.9)),
        new Real2Range(new RealRange(72.024,220.341), new RealRange(78.22,89.26)),
        new Real2Range(new RealRange(72.024,253.341), new RealRange(102.22,113.26)),
        new Real2Range(new RealRange(72.024,253.341), new RealRange(126.46,137.5)),
        new Real2Range(new RealRange(72.024,507.191), new RealRange(151.78,177.34)),
        new Real2Range(new RealRange(72.024,201.621), new RealRange(190.9,201.94)),
        new Real2Range(new RealRange(360.91,364.001), new RealRange(227.41,238.45)),
        new Real2Range(new RealRange(72.024,200.301), new RealRange(263.53,274.57)),
        new Real2Range(new RealRange(72.024,112.791), new RealRange(288.01,299.05)),
        new Real2Range(new RealRange(72.024,103.911), new RealRange(312.61,323.65)),
        new Real2Range(new RealRange(72.024,248.061), new RealRange(337.09,348.13)),
        new Real2Range(new RealRange(72.024,75.115), new RealRange(361.69,372.73)),
        new Real2Range(new RealRange(72.024,77.875), new RealRange(386.19,397.23)),
        new Real2Range(new RealRange(72.024,75.115), new RealRange(410.19,421.23)),
				},
				chunkList,
				0.001);
	}
	
	@Test
	public void testAJCPage6() {
		SemanticDocumentActionX semanticDocumentAction = Fixtures.createSemanticDocumentActionWithSVGPage(Fixtures.AJC6_SVG);
		Chunk chunk = new Chunk(semanticDocumentAction.getPageEditor().getSVGPage());
		chunk.createElementListAndCalculateBoundingBoxes();
		LOG.debug("BBOXES "+chunk.getBoundingBoxManager().getBBoxList().size());
		List<Chunk> chunkList = chunk.splitIntoChunks(1.0, BoxEdge.YMIN);
		Assert.assertNotNull("split list not null", chunkList);
		Assert.assertEquals("split list ", 25, chunkList.size());
	}
	

	private void checkChunkList(Real2Range[] refList, List<? extends SVGElement> elementList, double eps) {
		List<Real2Range> elementBoxes = BoundingBoxManager.createBBoxList(elementList);
		BoundingBoxManagerTest.checkReal2RangeList(
				refList,
				elementBoxes,
				eps);
	}

}
