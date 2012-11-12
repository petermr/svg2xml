package org.xmlcml.svgplus.figure;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.core.Chunk;
import org.xmlcml.svgplus.util.PConstants;

/** FigurePanels are logical subunits of the FigureBody
 * They are usually determined heuristically by whitespace or explicit borders
 * 
 * they often contain further subdivisions ("fragments") which have irregular whitespace boundaries
 * This division is done through a PixelBoxAnnotator
 * 
 * @author pm286
 *
 */

public class FigurePanel extends Chunk {
	private static final String PANEL_TAG = "panel";

	final static Logger LOG = Logger.getLogger(FigurePanel.class);

	public final static String FIGURE_PANEL = "FIGURE_PANEL";

	private List<FigureFragment> fragmentList;
	private Element panelAnalysis;
	
	public FigurePanel() {
		super();
	}

	/** copies contents and attributes of chunk into this and detach()es it
	 * 
	 * @param chunk
	 */
	public FigurePanel(Chunk chunk) {
		this();
		this.copyAttributesAndChildrenFromSVGElement(chunk);
		this.createElementListAndCalculateBoundingBoxes();
		chunk.detach();
	}

	List<FigureFragment> groupIntoWhitespaceSeparatedFragments(Real2 deltaXY) {
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(this, "./svg:*");
		PixelBoxAnnotator pixelBoxAnnotator = new PixelBoxAnnotator(this, elements);
		fragmentList = pixelBoxAnnotator.groupIntoWhitespaceSeparatedFragments(deltaXY);
		makeAggregateAnalysis();
		return fragmentList;
	}

	public Element getPanelAnalysis() {
		return panelAnalysis;
	}

	private void makeAggregateAnalysis() {
		panelAnalysis = new Element(PANEL_TAG);
		panelAnalysis.addAttribute(new Attribute(PConstants.ID, this.getId()));
		for (FigureFragment fragment : fragmentList) {
			Element primitives = fragment.getPrimitivesElement();
			if (primitives != null) {
				panelAnalysis.appendChild(primitives.copy());
			}
		}
	}

	public List<FigureFragment> getFragmentList() {
		return fragmentList;
	}

}
