package org.xmlcml.svg2xml.page;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.page.BoundingBoxManager.BoxEdge;
import org.xmlcml.svg2xml.paths.Chunk;

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
public class WhitespaceChunkerAnalyzerX /*extends PageChunkAnalyzer*/ {
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

	private Chunk topChunk;

	public WhitespaceChunkerAnalyzerX() {
		super();
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

	public static List<Chunk> chunkCreateWhitespaceChunkList(SVGSVG svgPage) {
		WhitespaceChunkerAnalyzerX whitespaceChunkerAnalyzer = new WhitespaceChunkerAnalyzerX();
		List<Chunk> finalChunkList = whitespaceChunkerAnalyzer.splitByWhitespace(svgPage);
		return finalChunkList;
	}

	public void setSplitterParams(SplitterParams[] spParams) {
		this.splitterParams = Arrays.asList(spParams);
	}
	
	/** the main analysis routine
	 * includes text, paths, figures, tables
	 */
	public List<Chunk> splitByWhitespaceAndLabelLeafNodes(SVGElement svgElement) {
		List<Chunk> chunkList = splitByWhitespace(svgElement);
		labelLeafNodes(chunkList);
		return chunkList;
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
//		List<SVGElement> topChunks = SVGUtil.getQuerySVGElements(to, "svg:g[@id='"+TOP_CHUNK+"']");
//		if (topChunks.size() != 1) {
//			throw new RuntimeException("Should be ONE top chunk");
//		}
		labelXYMINDescendants(topChunk, "chunk0");
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

	/** splits topChunk into subchunks (altering topChunk)
	 * 
	 * @param topChunk
	 * @return
	 */
	// FIXME add variable levels
	public List<Chunk> splitByWhitespace(SVGElement elementToBeChunked) {
		Chunk topChunk = new Chunk(elementToBeChunked);
//		topChunk.debug("PRE"+topChunk.getParent());
		Long time0 = System.currentTimeMillis();
		// I could recurse, but we only have 3 levels...
		LOG.trace("descendants0: "+topChunk.getDescendantSVGElementListWithoutDefsDescendants().size()+"/"+(System.currentTimeMillis()-time0));
		topChunk.setBoundingBoxCacheForSelfAndDescendants(true);
		LOG.trace("descendants: "+topChunk.getDescendantSVGElementListWithoutDefsDescendants().size()+"/"+(System.currentTimeMillis()-time0));
//		pageEditorX.getSVGPage().appendChild(topChunk);
		topChunk.setId(TOP_CHUNK);
		LOG.trace(String.valueOf(splitterParams.get(0).width)+"; "+String.valueOf(splitterParams.get(1).width)+"; "+String.valueOf(splitterParams.get(2).width)+"; ");
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
		removeEmptyChunks(topChunk);
//		topChunk.debug("TOP");
		removeChildren(elementToBeChunked);
		moveChildrenFromChunkToElement(elementToBeChunked, topChunk);
		return subSubSubChunkList;
	}

	private void moveChildrenFromChunkToElement(SVGElement elementToBeChunked, Chunk topChunk) {
		Elements childElements1 = topChunk.getChildElements();
		for (int i = 0; i < childElements1.size(); i++) {
			childElements1.get(i).detach();
			elementToBeChunked.appendChild(childElements1.get(i));
		}
	}

	private void removeChildren(SVGElement elementToBeChunked) {
		Elements childElements = elementToBeChunked.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			childElements.get(i).detach();
		}
	}

	private void removeEmptyChunks(Chunk topChunk) {
		List<SVGElement> emptyGList = SVGUtil.getQuerySVGElements(topChunk, "//svg:g[count(*)=0]");
		for (SVGElement g : emptyGList) {
			g.detach();
		}
	}

	public static void drawBoxes(List<Chunk> chunkList, String stroke, String fill, Double opacity) {
		if (chunkList != null) {
			for (Chunk chunk : chunkList) {
				Real2Range bbox = chunk.getBoundingBox();
				SVGRect rect = new SVGRect( bbox);
				rect.setStroke(stroke);
				rect.setFill(fill);
				rect.setStrokeWidth(0.9);
				rect.setOpacity(opacity);
				chunk.appendChild(rect);
			}
		}
	}

}
