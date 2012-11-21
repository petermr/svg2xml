package org.xmlcml.svgplus.page;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.paths.PathAnalyzer;

public class BoxProcessorAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(BoxProcessorAction.class);
	
	private PathAnalyzer pathAnalyzer;
	
	public BoxProcessorAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	@Override
	public void run() {
		pathAnalyzer = pageAnalyzer.ensurePathAnalyzer();
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(getSVGPage(), getXPath());
		processRoundedBox(elementList);
	}
	
	private void processRoundedBox(List<SVGElement> elementList) {
		for (SVGElement element : elementList) {
			LOG.trace("roundedBox "+element.getId());
			Chunk chunk = null;
			if (element instanceof Chunk) {
				chunk = (Chunk) element;
			}
			List<SVGElement> outerElements = 
					pathAnalyzer.getBoxOutsideMargins(chunk, this.getMarginX(), this.getMarginY());
			if (outerElements.size() != 0) {
				Integer boxCount = this.getBoxCount();
				if (false && outerElements.size() < boxCount) { // because title page has strange boxes
//					chunk.debug("BAD BOX");
					LOG.debug("elements at edge of box ("+outerElements.size()+") != boxCount: "+boxCount); 
					throw new RuntimeException("box");
				} else {
					for (SVGElement outerElement : outerElements) {
						outerElement.detach();
					}
//					chunk.setChunkStyleValue(ChunkStyle.OUTLINED_BOX);
				}
			}
			LOG.trace("end processRoundedBox");
		}
	}

}
