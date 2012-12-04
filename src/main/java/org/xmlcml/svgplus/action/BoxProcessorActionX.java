package org.xmlcml.svgplus.action;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.tools.Chunk;

public class BoxProcessorActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(BoxProcessorActionX.class);
	
	private PathAnalyzerX pathAnalyzerX;

	public final static String TAG ="boxProcessor";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionX.ACTION);
		ATTNAMES.add(PageActionX.BOX_COUNT);
		ATTNAMES.add(PageActionX.MARGIN_X);
		ATTNAMES.add(PageActionX.MARGIN_Y);
		ATTNAMES.add(PageActionX.TITLE);
		ATTNAMES.add(PageActionX.XPATH);
	}

	/** constructor
	 */
	public BoxProcessorActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new BoxProcessorActionX(this);
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
			AbstractActionX.XPATH,
			PageActionX.BOX_COUNT,
			PageActionX.MARGIN_X,
			PageActionX.MARGIN_Y,
		});
	}

	public BoxProcessorActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		pathAnalyzerX = getPageEditor().ensurePathAnalyzer();
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
					pathAnalyzerX.getBoxOutsideMargins(chunk, this.getMarginX(), this.getMarginY());
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
