package org.xmlcml.svg2xml.figure;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.tools.BoundingBoxManager.BoxEdge;
import org.xmlcml.svg2xml.tools.Caption;
import org.xmlcml.svg2xml.tools.Chunk;
import org.xmlcml.svg2xml.util.SVGPlusConstantsX;

/** a figure consists of (possibly optional) Caption and FigureBody
 * these are directly accessible but are also child elements of Figure
 * @author pm286
 *
 */
public class Figure extends Chunk {
	private final static Logger LOG = Logger.getLogger(Figure.class);

	private static final String FIGURE_TAG = "figure";
	private static final String CAPTION_TAG = "caption";
	public final static String FIGURE = "FIGURE";
	
	private Chunk caption;
	private FigureBody figureBody;
	private FigureAnalyzer figureAnalyzerX;
	private List<FigurePanel> panelList;
	private List<FigureFragment> fragmentList;
	private Element figureAnalysis;
	
	protected String getChunkStyleName() {
		return FIGURE;
	}

	public Figure() {
		super();
	}

	public Figure(FigureAnalyzer figureAnalyzerX) {
		this();
		this.figureAnalyzerX = figureAnalyzerX;
	}

	public void addCaption(Chunk caption) {
		this.caption = caption;
		caption.detach();
		this.appendChild(caption);
	}

	public Chunk getCaption() {
		return this.caption;
	}

	public FigureBody getFigureBody() {
		return figureBody;
	}

	private FigureBody createFigureBody() {
		// assume caption is separated from rest by horizontal line. May need to check more later
		ensureFigureBody();
		List<SVGElement> oldGList = SVGUtil.getQuerySVGElements(this, "./svg:g[not(@chunkStyle='"+Caption.CAPTION+"')]");
		figureBody.transferGChildren(oldGList);
		// clean up old Gs
		this.removeEmptySVGG();
		return figureBody;
	}
	
	private List<FigurePanel> createPanelList() {
		// chunk the figureBody by horizontal whitespace and add to new container
		// (may have to do vertical space later)
		List<Chunk> panelChunkList = figureBody.splitIntoChunks(figureAnalyzerX.getPanelSeparation(), BoxEdge.XMIN);
		String id = this.getId();
		int i = 0;
		ensurePanelList();
		for (Chunk panel : panelChunkList) {
			FigurePanel figurePanel = new FigurePanel(panel);
			figurePanel.setId(id+"."+(i++));
			this.addFigurePanel(figurePanel);
			SVGElement.drawBox(figurePanel.getBoundingBox(), this, "blue", "cyan", 1.0, 0.3);
			panelList.add(figurePanel);
		}
		LOG.trace("Split figureBody into figurePanels: "+panelList.size());
		return panelList;
	}
	
	private void ensurePanelList() {
		if (panelList == null) {
			panelList = new ArrayList<FigurePanel>();
		}
	}

	public List<FigureFragment> createFragmentsInsidePanels() {
		fragmentList = new ArrayList<FigureFragment>();
		if (figureBody != null) {
			fragmentList = figureBody.createFragmentsInsidePanels();
		}
		return fragmentList;
	}

	public List<FigurePanel> splitByHorizontalWhitespace() {
		createFigureBody();
		createPanelList();
		this.annotateWithBoxes();
		return panelList;
	}

	void annotateWithBoxes() {
		Chunk caption = getCaption();
		caption.setBoundingBoxCached(false);
		SVGElement.drawBox(caption.getBoundingBox(), caption, "magenta", "#ffffaa", 3.0, 0.3);
		createElementListAndCalculateBoundingBoxes();
		SVGElement.drawBox(getBoundingBox(), this, "green", "#ffffaa", 3.0, 0.3);
	}

	private void ensureFigureBody() {
		if (figureBody == null) {
			figureBody = new FigureBody(this);
			this.addFigureBody(figureBody);
		}
	}

	private void addFigureBody(FigureBody figureBody) {
		this.figureBody = figureBody;
		figureBody.detach();
		this.appendChild(figureBody);
	}

	void addFigurePanel(FigurePanel figurePanel) {
		ensureFigureBody();
		figureBody.addFigurePanel(figurePanel);
	}

	void removeFigurePanel(FigurePanel figurePanel) {
		figureBody.remove(figurePanel);
	}

	public List<FigurePanel> getFigurePanelList() {
		return figureBody == null ? null : figureBody.getFigurePanelList();
	}

	public FigureAnalyzer getFigureAnalyzer() {
		return figureAnalyzerX;
	}

	public Element getFigureAnalysis() {
		figureAnalysis = new Element(FIGURE_TAG);
		String id = this.getId();
		if (id != null) {
			figureAnalysis.addAttribute(new Attribute(SVGPlusConstantsX.ID, id));
		}
		if (figureBody.getBodyAnalysis() != null) {
			figureAnalysis.appendChild(figureBody.getBodyAnalysis().copy());
		}
		
		if (caption != null) {
			Element captionTitle = new Element(CAPTION_TAG);
			captionTitle.appendChild(caption.query("./*/*/@title").get(0).getValue());
			figureAnalysis.appendChild(captionTitle);
		}
		return figureAnalysis;
	}

}
