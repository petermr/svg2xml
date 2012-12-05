package org.xmlcml.svgplus.analyzer;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.action.SemanticDocumentActionX;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.tools.SplitterParams;

/**
 * page-oriented
 * 
 * slices the page up into chunks using continuous whitespace. 
 * Can then recurse. Most obvious strategy is:
 * 1 slice Y (i.e. horizontal borders)
 * 2 slice X on results of 1
 * 3 slice Y on results of 2
 * 
 * determine types of chunk after this and apply different strategies
 * 
 * analyze(control) is the only meaningful public routine and by default will analyze
 * * text
 * * paths (transforming to rect, line, polyline, circle)
 * * figures
 * * tables
 * 
 * each of these has a special analyzer (TextAnalyzer, etc.)
 * 
 * pageChunkSplitter communicates upwards through currentPage
 * @author pm286
 *
 */
public class WhitespaceChunkerAnalyzerX extends AbstractPageAnalyzerX {
	private static final Logger LOG = Logger.getLogger(WhitespaceChunkerAnalyzerX.class);

	public static final String CLIP = "CLIP";
	public static final String WHITE = "WHITE";

	public static final String YMIN_XMIN_YMIN = ".//svg:g[@edge='YMIN']/svg:g[@edge='XMIN']/svg:g[@edge='YMIN']";

	private static final String LEAF = "LEAF";
	public static final String TOP_CHUNK = "topChunk";

	
	private List<SplitterParams> splitterParams = null;
	
	
	// empirical borderwidths
	// Y0, X0, Y1, X1...
	private static double YSEP_0 = 10.;
	private static Double YSEP_1 = 5.;
	private static Double XSEP_0 = 10.0;
	
	private List<Chunk> finalChunkList;

	public WhitespaceChunkerAnalyzerX(SemanticDocumentActionX semanticDocumentActionX) {
		super(semanticDocumentActionX);
		init();
	}
	
	private void init() {
		setSplitterParams(
				new SplitterParams[] {
				new SplitterParams(BoxEdge.YMIN, YSEP_0),
				new SplitterParams(BoxEdge.XMIN, XSEP_0),
				new SplitterParams(BoxEdge.YMIN, YSEP_1)
				}
		);
	}

	public void setSplitterParams(SplitterParams[] spParams) {
		this.splitterParams = Arrays.asList(spParams);
	}
	
	/** the main analysis routine
	 * includes text, paths, figures, tables
	 */
	public void splitByWhitespaceAndLabelLeafNodes() {
		finalChunkList = splitByWhitespace();
		labelLeafNodes(finalChunkList);
	}
	
	public void labelLeafNodes(List<Chunk> finalChunkList) {
		for (Chunk chunk : finalChunkList) {
			if (SVGUtil.getQuerySVGElements(chunk, "self::svg:g[@edge='YMIN' and parent::svg:g[@edge='XMIN' and parent::svg:g[@edge='YMIN']]]").size() >0) {
				chunk.addAttribute(new Attribute(LEAF, "3"));
			} else if (SVGUtil.getQuerySVGElements(chunk, "self::svg:g[@edge='XMIN' and parent::svg:g[@edge='YMIN']]").size() >0) {
				chunk.addAttribute(new Attribute(LEAF, "2"));
			} else if (SVGUtil.getQuerySVGElements(chunk, "self::svg:g[@edge='YMIN']").size() >0) {
				chunk.addAttribute(new Attribute(LEAF, "1"));
			}
		}
		List<SVGElement> topChunks = SVGUtil.getQuerySVGElements(pageEditorX.getSVGPage(), "svg:g[@id='"+TOP_CHUNK+"']");
		if (topChunks.size() != 1) {
			throw new RuntimeException("Should be ONE top chunk");
		}
		labelXYMINDescendants((Chunk)topChunks.get(0), "chunk0");
	}
	
	private void labelXYMINDescendants(Chunk chunk, String idRoot) {
		List<SVGElement> childChunkList = SVGUtil.getQuerySVGElements(chunk, "./svg:g[contains(@edge, 'MIN')]");
		for (int i = 0; i < childChunkList.size(); i++) {
			Chunk childChunk = (Chunk) childChunkList.get(i);
			String id = idRoot+"."+i;
			childChunk.setId(id);
			labelXYMINDescendants(childChunk, id);
		}
	}

	// FIXME add variable levels
	public List<Chunk> splitByWhitespace() {
		Long time0 = System.currentTimeMillis();
		// I could recurse, but we only have 3 levels...
		Chunk topChunk = new Chunk(pageEditorX.getSVGPage());
		LOG.debug("descendants0: "+topChunk.getDescendantSVGElementList().size()+"/"+(System.currentTimeMillis()-time0));
		topChunk.setBoundingBoxCacheForSelfAndDescendants(true);
		LOG.debug("descendants: "+topChunk.getDescendantSVGElementList().size()+"/"+(System.currentTimeMillis()-time0));
		pageEditorX.getSVGPage().appendChild(topChunk);
		topChunk.setId(TOP_CHUNK);
		List<Chunk> subChunkList = topChunk.splitIntoChunks(splitterParams.get(0).width, splitterParams.get(0).boxEdge);
		List<Chunk> subSubChunkList = new ArrayList<Chunk>();
		List<Chunk> subSubSubChunkList = null;
		for (Chunk subChunk : subChunkList) {
			List<Chunk> cc = subChunk.splitIntoChunks(splitterParams.get(1).width, splitterParams.get(1).boxEdge);
			subSubChunkList.addAll(cc);
			subSubSubChunkList = new ArrayList<Chunk>();
			for (Chunk subSubChunk : subSubChunkList) {
				cc = subSubChunk.splitIntoChunks(splitterParams.get(2).width, splitterParams.get(2).boxEdge);
				subSubSubChunkList.addAll(cc);
			}
		}
		topChunk.debug("TOP");
		removeEmptyChunks(topChunk);
		return subSubSubChunkList;
	}

	private void removeEmptyChunks(Chunk topChunk) {
		List<SVGElement> emptyGList = SVGUtil.getQuerySVGElements(topChunk, "//svg:g[count(*)=0]");
		for (SVGElement g : emptyGList) {
			g.detach();
		}
	}
	
}
