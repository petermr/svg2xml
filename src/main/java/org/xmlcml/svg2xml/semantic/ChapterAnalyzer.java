package org.xmlcml.svg2xml.semantic;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.analyzer.AbstractAnalyzer;
import org.xmlcml.svg2xml.analyzer.ChunkId;
import org.xmlcml.svg2xml.analyzer.PDFIndex;
import org.xmlcml.svg2xml.table.TableOld;

/**
 * @author pm286
 *
 */
public class ChapterAnalyzer extends AbstractAnalyzer {
	private static final Logger LOG = Logger.getLogger(ChapterAnalyzer.class);
	public static final Pattern PATTERN = Pattern.compile("^[Cc][Hh][Aa][Pp][Tt?][Ee?][Rr?]\\s*\\.?\\s*(\\d+).*", Pattern.DOTALL);
	public final static String TITLE = "CHAPTER";
	
	public ChapterAnalyzer() {
		super();
	}

	public ChapterAnalyzer(PDFIndex pdfIndex) {
		super(pdfIndex);
	}

	public void analyze() {
	}
	
	@Override
	public SVGG oldAnnotateChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
	public Integer indexAndLabelChunk(String content, ChunkId id, Set<ChunkId> usedChunkSet) {
		Integer serial = super.indexAndLabelChunk(content, id);
		// index...
		return serial;
	}
	
	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

}
