package org.xmlcml.svgplus.control.page;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.core.Chunk;

/**
	<pageAction action="createWhitespaceChunks" depth="3"/>
 * @author pm286
 *
 */
public class WhitespaceChunkerAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(WhitespaceChunkerAction.class);
	
	public WhitespaceChunkerAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	@Override
	public void run() {
		PageChunkSplitter pageChunkSplitter = pageAnalyzer.ensurePageChunkSplitter();
		Integer depth = getDepth();
		if (depth != null) {
			LOG.trace("DEPTH cannot yet be set");
		}
		List<Chunk> finalChunkList = pageChunkSplitter.splitByWhitespace();
		pageChunkSplitter.labelLeafNodes(finalChunkList);
		
	}

}
