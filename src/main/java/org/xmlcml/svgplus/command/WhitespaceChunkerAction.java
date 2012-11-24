package org.xmlcml.svgplus.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.tools.PageChunkSplitter;

/**
	<pageAction action="createWhitespaceChunks" depth="3"/>
 * @author pm286
 *
 */
public class WhitespaceChunkerAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(WhitespaceChunkerAction.class);
	
	public WhitespaceChunkerAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		if (true) {
			LOG.error("NYI");
		} else {
			PageChunkSplitter pageChunkSplitter = getPageAnalyzer().ensurePageChunkSplitter();
			Integer depth = getDepth();
			if (depth != null) {
				LOG.trace("DEPTH cannot yet be set");
			}
			List<Chunk> finalChunkList = pageChunkSplitter.splitByWhitespace();
			pageChunkSplitter.labelLeafNodes(finalChunkList);
		}
	}

}
