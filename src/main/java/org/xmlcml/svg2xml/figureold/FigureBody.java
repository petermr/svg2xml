package org.xmlcml.svg2xml.figureold;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.paths.Chunk;
import org.xmlcml.svg2xml.util.SVG2XMLConstantsX;

/** FigureBody consists of the non-caption part of the figure
 *  it will contain one or more SubFigures
 * @author pm286
 *
 */

public class FigureBody extends Chunk {
	private static final String BODY_TAG = "figureBody";

	private final static Logger LOG = Logger.getLogger(FigureBody.class);

	public final static String FIGURE_BODY = "FIGURE_BODY";
	private List<FigurePanel> figurePanelList;
	private Figure figure;
	private Element bodyAnalysis;
	
	public FigureBody(Figure figure) {
		super();
		this.figure = figure;
	}
	
	public List<FigureFragment> createFragmentsInsidePanels() {
		List<FigureFragment> allFragmentList = new ArrayList<FigureFragment>();
		if (figurePanelList != null) {
			bodyAnalysis = new Element(BODY_TAG);
			String id = this.getId();
			if (id != null) {
				bodyAnalysis.addAttribute(new Attribute(SVG2XMLConstantsX.ID, id));
			}
//			FigureAnalyzer figureAnalyzer = figure.getFigureAnalyzer();
			Real2 margins = figure.getClusterWhitespaceBoxMargins();
			for (FigurePanel figurePanel : figurePanelList) {
				List<FigureFragment> fragmentList = 
					figurePanel.groupIntoWhitespaceSeparatedFragments(margins);
				allFragmentList.addAll(fragmentList);
				Element panelAnalysis = figurePanel.getPanelAnalysis();
				if (panelAnalysis != null) {
					bodyAnalysis.appendChild(panelAnalysis.copy());
				}
			}
		}
		return allFragmentList;
	}

	void transferGChildren(List<SVGElement> nonCaptionChunks) {
		for (SVGElement elem : nonCaptionChunks) {
			SVGG g = (SVGG) elem;
			Elements childElements = g.getChildElements();
			for (int i = 0; i < childElements.size(); i++) {
				childElements.get(i).detach();
				this.appendChild(childElements.get(i));
			}
		}
		this.createElementListAndCalculateBoundingBoxes();
	}


	private void ensureFigurePanelList() {
		if (figurePanelList == null) {
			figurePanelList = new ArrayList<FigurePanel>();
		}
	}
	
	void addFigurePanel(FigurePanel figurePanel) {
		ensureFigurePanelList();
		figurePanel.detach();
		this.appendChild(figurePanel);
		figurePanelList.add(figurePanel);
	}

	public List<FigurePanel> getFigurePanelList() {
		return figurePanelList;
	}

	void remove(FigurePanel figurePanel) {
		if (figurePanelList != null) {
			figurePanel.detach();
			figurePanelList.remove(figurePanel);
		}
	}

	public Element getBodyAnalysis() {
		return bodyAnalysis;
	}

}
