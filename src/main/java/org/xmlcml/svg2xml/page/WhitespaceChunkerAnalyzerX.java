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
import org.xmlcml.graphics.svg.linestuff.BoundingBoxManager.BoxEdge;
import org.xmlcml.svg2xml.paths.Chunk;

/**
 * Page-oriented.
 * <p>
 * Slices the page up into chunks using continuous whitespace. 
 * Can then recurse. Most obvious strategy is:
 * <ul>
 * <li>1 slice Y (i.e. horizontal borders)</li>
 * <li>2 slice X on results of 1</li>
 * <li>3 slice Y on results of 2</li>
 * </ul>
 * Determine types of chunk after this and apply different strategies.
 * <p>
 * Analyze(control) is the only meaningful public routine and by default will analyze:
 * <ul>
 * <li>Text</li>
 * <li>Paths (transforming to rect, line, polyline, circle)</li>
 * <li>Figures</li>
 * <li>Tables</li>
 * </ul>
 * Each of these has a special analyzer (TextAnalyzer, etc.).
 * <p>
 * PageChunkSplitter communicates upwards through currentPage.
 * 
 * @author pm286
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
	private static double YSEP_0 = 10.0;
	private static Double YSEP_1 = 6.0;
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
		splitterParams = Arrays.asList(spParams);
	}
	
	/** 
	 * The main analysis routine.
	 * Includes text, paths, figures and tables.
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
		//List<SVGElement> topChunks = SVGUtil.getQuerySVGElements(to, "svg:g[@id='"+TOP_CHUNK+"']");
		//if (topChunks.size() != 1) {
		//	throw new RuntimeException("Should be ONE top chunk");
		//}
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

	/** 
	 * Splits topChunk into subchunks (altering topChunk)
	 * 
	 * @param topChunk
	 * @return
	 */
	// FIXME add variable levels
	public List<Chunk> splitByWhitespace(SVGElement elementToBeChunked) {
		Chunk topChunk = new Chunk(elementToBeChunked);
		//topChunk.debug("PRE"+topChunk.getParent());
		Long time0 = System.currentTimeMillis();
		// I could recurse, but we only have 3 levels...
		LOG.trace("descendants0: "+topChunk.getDescendantSVGElementListWithoutDefsDescendants().size()+"/"+(System.currentTimeMillis() - time0));
		topChunk.setBoundingBoxCacheForSelfAndDescendants(true);
		LOG.trace("descendants: "+topChunk.getDescendantSVGElementListWithoutDefsDescendants().size()+"/"+(System.currentTimeMillis() - time0));
		//pageEditorX.getSVGPage().appendChild(topChunk);
		topChunk.setId(TOP_CHUNK);
		LOG.trace(String.valueOf(splitterParams.get(0).width)+"; "+String.valueOf(splitterParams.get(1).width)+"; "+String.valueOf(splitterParams.get(2).width)+"; ");
		List<Chunk> subChunkList = topChunk.splitIntoChunks(splitterParams.get(0).width, splitterParams.get(0).boxEdge);
		List<Chunk> finalSubChunks = mergeAdjacentDiagramChunks(subChunkList);
		List<Chunk> subSubChunkList = new ArrayList<Chunk>();
		//List<Chunk> subSubSubChunkList = null;
		List<Chunk> subSubSubChunkList = new ArrayList<Chunk>();
		for (Chunk subChunk : finalSubChunks) {
			List<Chunk> subSubChunks = subChunk.splitIntoChunks(splitterParams.get(1).width, splitterParams.get(1).boxEdge);
			/*int chunksWithOnlyText = 0;
			for (Chunk c : cc) {
				if (c.isTextChunk()) {
					chunksWithOnlyText++;
				}
			}
			if (cc.size() - chunksWithOnlyText > 1) {
				subSubChunkList.addAll(subChunk.splitIntoChunks(Double.MAX_VALUE, splitterParams.get(1).boxEdge));
			} else {*/
			List<Chunk> finalSubSubChunks = mergeAdjacentDiagramChunks(subSubChunks);
			subSubChunkList.addAll(finalSubSubChunks);
			//}
		}
		for (Chunk subSubChunk : subSubChunkList) {
			//List<Chunk> cc = subSubChunk.splitIntoChunks(subSubChunk.isTextChunk() ? splitterParams.get(2).width : Double.MAX_VALUE, splitterParams.get(2).boxEdge);
			List<Chunk> subSubSubChunks = subSubChunk.splitIntoChunks(splitterParams.get(2).width, splitterParams.get(2).boxEdge);
			List<Chunk> finalSubSubSubChunks = mergeAdjacentDiagramChunks(subSubSubChunks);
			subSubSubChunkList.addAll(finalSubSubSubChunks);
		}
		removeEmptyChunks(topChunk);
		//topChunk.debug("TOP");
		removeChildren(elementToBeChunked);
		moveChildrenFromChunkToElement(elementToBeChunked, topChunk);
		return subSubSubChunkList;
	}

	private List<Chunk> mergeAdjacentDiagramChunks(List<Chunk> chunks) {
		List<Chunk> finalChunks = new ArrayList<Chunk>();
		Chunk currentChunk = null;
		for (Chunk c : chunks) {
			if (currentChunk == null) {
				currentChunk = new Chunk(c);
				c.getParent().appendChild(currentChunk);
				currentChunk.createElementListAndCalculateBoundingBoxes();
			} else {
				if (c.isTextChunk()) {
					finalChunks.add(currentChunk);
					currentChunk = new Chunk(c);
					c.getParent().appendChild(currentChunk);
					currentChunk.createElementListAndCalculateBoundingBoxes();
				} else {
					if (currentChunk.isTextChunk()) {
						finalChunks.add(currentChunk);
						currentChunk = new Chunk(c);
						c.getParent().appendChild(currentChunk);
						currentChunk.createElementListAndCalculateBoundingBoxes();
					} else {
						currentChunk.copyAttributesAndChildrenFromSVGElement(c);
						currentChunk.createElementListAndCalculateBoundingBoxes();
					}
				}
			}
			c.detach();
		}
		finalChunks.add(currentChunk);
		return finalChunks;
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
		List<SVGElement> emptyGList = SVGUtil.getQuerySVGElements(topChunk, ".//svg:g[count(*)=0]");
		for (SVGElement g : emptyGList) {
			g.detach();
		}
	}

	public static void drawBoxes(List<SVGG> chunkList, String stroke, String fill, Double opacity) {
		if (chunkList != null) {
			for (SVGG chunk : chunkList) {
				Real2Range bbox = chunk.getBoundingBox();
				if (bbox != null) {
					SVGRect rect = new SVGRect(bbox);
					rect.setStroke(stroke);
					rect.setFill(fill);
					rect.setStrokeWidth(0.9);
					rect.setOpacity(opacity);
					chunk.appendChild(rect);
				}
			}
		}
	}

}
