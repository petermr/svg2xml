package org.xmlcml.svgplus.tools;

import java.util.ArrayList;


import java.util.List;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.command.AbstractAnalyzer;
import org.xmlcml.svgplus.command.PageAnalyzer;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;

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
 * pageChunkSplitter communicates upwards through pageAnalyzer
 * @author pm286
 *
 */
public class PageChunkSplitter extends AbstractAnalyzer {
	private static final Logger LOG = Logger.getLogger(PageChunkSplitter.class);

	public static final String CLIP = "CLIP";
	public static final String WHITE = "WHITE";

	public static final String YMIN_XMIN_YMIN = ".//svg:g[@edge='YMIN']/svg:g[@edge='XMIN']/svg:g[@edge='YMIN']";

	private static final String LEAF = "LEAF";
	public static final String TOP_CHUNK = "topChunk";

	// empirical borderwidths
	private Double YSEP_0 = 10.;
	private Double YSEP_1 = 5.;
	private Double XSEP_0 = 10.0;
	
	private List<Chunk> finalChunkList;

	public PageChunkSplitter(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
	}

//	/** the main analysis routine
//	 * includes text, paths, figures, tables
//	 */
//	@Deprecated
//	public void splitByWhitespaceThenSplitByClipPath() {
//		finalChunkList = splitByWhitespace();
//		labelLeafNodes(finalChunkList);
//		splitByClipPath(finalChunkList);
//	}
	
	/** the main analysis routine
	 * includes text, paths, figures, tables
	 */
	public void splitByWhitespaceThenSplitByPhysicalStyle() {
		finalChunkList = splitByWhitespace();
		labelLeafNodes(finalChunkList);
		splitByPhysicalStyle(finalChunkList);
//		splitByClipPath(finalChunkList);
	}
	
	private void splitByPhysicalStyle(List<Chunk> finalChunkList2) {
		// TODO Auto-generated method stub
		
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
		List<SVGElement> topChunks = SVGUtil.getQuerySVGElements(svgPage, "svg:g[@id='"+TOP_CHUNK+"']");
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
	
//	private void debugPage(String filename) {
//		try {
//			CMLUtil.debug(svgPage, new FileOutputStream(filename), 1);
//		} catch (IOException e) {
//			throw new RuntimeException("file error: "+filename, e);
//		}
//	}

	
	public List<Chunk> splitByWhitespace() {
		Long time0 = System.currentTimeMillis();
		// I could recurse, but we only have 3 levels...
		Chunk topChunk = new Chunk(this, svgPage);
		LOG.trace("descendants0: "+topChunk.getElementList().size()+"/"+(System.currentTimeMillis()-time0));
		topChunk.setBoundingBoxCacheForSelfAndDescendants(true);
		LOG.trace("descendants: "+topChunk.getElementList().size()+"/"+(System.currentTimeMillis()-time0));
		svgPage.appendChild(topChunk);
		topChunk.setId(TOP_CHUNK);
		List<Chunk> subChunkList = topChunk.splitIntoChunks(YSEP_0, BoxEdge.YMIN);
		List<Chunk> subSubChunkList = new ArrayList<Chunk>();
		List<Chunk> subSubSubChunkList = null;
		for (Chunk subChunk : subChunkList) {
			List<Chunk> cc = subChunk.splitIntoChunks(XSEP_0, BoxEdge.XMIN);
			subSubChunkList.addAll(cc);
			subSubSubChunkList = new ArrayList<Chunk>();
			for (Chunk subSubChunk : subSubChunkList) {
				cc = subSubChunk.splitIntoChunks(YSEP_1, BoxEdge.YMIN);
				subSubSubChunkList.addAll(cc);
			}
		}
		removeEmptyChunks(topChunk);
		if (false) removeAttributes( new String[] {
//				"clip-path",
				"text-rendering",
				"shape-rendering",
				"space",
				"fill-rule",
		});
		Long time = System.currentTimeMillis();
		LOG.trace("pageChunkSplitter: "+(time-time0));
		return subSubSubChunkList;
	}

	private void removeAttributes(String[] localNames) {
		LOG.debug("removing attributes");
		for (String localName : localNames) {
			Nodes nodes = svgPage.query("//@*[local-name()='"+localName+"']");
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		}
		LOG.debug("removed attributes");
	}
	
	private void removeEmptyChunks(Chunk topChunk) {
		List<SVGElement> emptyGList = SVGUtil.getQuerySVGElements(topChunk, "//svg:g[count(*)=0]");
		for (SVGElement g : emptyGList) {
			g.detach();
		}
	}
	
	public AbstractAnalyzer getPathInterpreter() {
		return pageAnalyzer;
	}

}
