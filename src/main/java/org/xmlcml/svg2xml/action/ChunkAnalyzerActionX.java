package org.xmlcml.svg2xml.action;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.analyzer.ChunkAnalyzerX;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;
import org.xmlcml.svg2xml.tools.Chunk;

public class ChunkAnalyzerActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(ChunkAnalyzerActionX.class);
	
	private boolean subSup;
	private boolean removeNumericTSpans;
	private boolean splitAtSpaces;

	public final static String TAG ="chunkAnalyzer";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	public static final String SUBSUP = "subSup";
	public static final String REMOVE_NUMERIC_TSPANS = "removeNumericTSpans";
	public static final String SPLIT_AT_SPACES = "splitAtSpaces";
	
	/** attribute names
	 * 
	 */

	static {
		ATTNAMES.add(AbstractActionX.XPATH);
		ATTNAMES.add(SUBSUP);
		ATTNAMES.add(REMOVE_NUMERIC_TSPANS);
	}

	/** constructor
	 */
	public ChunkAnalyzerActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new ChunkAnalyzerActionX(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
//				AbstractActionX.XPATH,    // optional now
		});
	}

	public ChunkAnalyzerActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		String xpath = getXPath();
		if (xpath != null) {
			analyzeSpecificChunks(xpath);
		} else {
			analyzeLeafChunks();
		}
	}

	private void analyzeSpecificChunks(String xpath) {
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), xpath);
		LOG.trace("XPATHS "+elements.size());
		this.subSup = isTrue(ChunkAnalyzerActionX.SUBSUP);
		this.splitAtSpaces = isTrue(ChunkAnalyzerActionX.SPLIT_AT_SPACES);
		this.removeNumericTSpans = isTrue(ChunkAnalyzerActionX.REMOVE_NUMERIC_TSPANS);

		for (SVGElement element : elements) {
			if (!(element instanceof SVGG)) {
				throw new RuntimeException("Must operate on <g> elements");
			}
			LOG.trace("*********************ELEMENT "+element.getId());
			analyzeChunk(new Chunk((SVGG)element));
		}
		debugFile("target/chunkAnalyzer1Axes.svg");
	}
	
	/** svgx:role="chunk" */
	private void analyzeLeafChunks() {
//		String xpath = ".//svg:g[@*[local-name()='role' and .='chunk'] and not(svg:g)]'";
		String xpath = ".//svg:g[@*[local-name()='role' and .='chunk'] ]'";
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), xpath);
		LOG.trace("analysing leaf chunks: "+elements.size());
	}

	private void analyzeChunk(Chunk chunk) {
		ChunkAnalyzerX chunkAnalyzer = new ChunkAnalyzerX(semanticDocumentActionX);
		createTextAnalyzer(chunkAnalyzer);
		chunkAnalyzer.analyzeChunk(chunk);
	}

	private void createTextAnalyzer(ChunkAnalyzerX chunkAnalyzer) {
		TextAnalyzerX textAnalyzerX = chunkAnalyzer.getTextAnalyzerX();
		textAnalyzerX.setSubSup(subSup);
		textAnalyzerX.setRemoveNumericTSpans(removeNumericTSpans);
	}
	
}
