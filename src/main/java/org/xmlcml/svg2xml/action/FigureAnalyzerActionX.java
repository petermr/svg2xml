package org.xmlcml.svg2xml.action;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.analyzer.FigureAnalyzerX;
import org.xmlcml.svgplus.analyzer.TextAnalyzerX;
import org.xmlcml.svgplus.figure.Figure;
import org.xmlcml.svgplus.figure.FigureFragment;
import org.xmlcml.svgplus.figure.FigurePanel;
import org.xmlcml.svgplus.tools.Chunk;
import org.xmlcml.svgplus.util.GraphUtil;

public class FigureAnalyzerActionX extends PageActionX {


	private final static Logger LOG = Logger.getLogger(FigureAnalyzerActionX.class);
	private String filename;

	public FigureAnalyzerActionX(AbstractActionX actionElement) {
		super(actionElement);
	}

	public final static String TAG ="figureAnalyzer";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static final String ANALYZE_FRAGMENTS = "analyzeFragments";
	static final String CREATE_FRAGMENTS = "createFragments";
	final static String CREATE_WORDS_LINES = "createWordsLines";
	static final String FRAGMENT_COLOURS = "clusterColours";
	final static String FRAGMENT_MARGINS = "clusterMargins";
	final static String LOCATION_STRATEGY = "locationStrategy";
	final static String PANEL_SEPARATION = "panelSeparation";

	static {
		ATTNAMES.add(ANALYZE_FRAGMENTS);
		ATTNAMES.add(CREATE_FRAGMENTS);
		ATTNAMES.add(CREATE_WORDS_LINES);
		ATTNAMES.add(FILENAME);
		ATTNAMES.add(FRAGMENT_COLOURS);
		ATTNAMES.add(FRAGMENT_MARGINS);
		ATTNAMES.add(LOCATION_STRATEGY);
		ATTNAMES.add(PANEL_SEPARATION);
	}

	/** constructor
	 */
	public FigureAnalyzerActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new FigureAnalyzerActionX(this);
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
				LOCATION_STRATEGY,
		});
	}

	@Override
	public void run() {
		FigureAnalyzerX figureAnalyzer = getPageEditor().ensureFigureAnalyzer();
		figureAnalyzer.setLocationStrategy(getAndExpand(FigureAnalyzerActionX.LOCATION_STRATEGY));
		List<Figure> figureList = figureAnalyzer.createFigures();
		List<FigurePanel> panelList = figureAnalyzer.createPanelsUsingWhitespace();
		filename = getFilename();
		if (isTrue(FigureAnalyzerActionX.CREATE_WORDS_LINES)) {
			TextAnalyzerX textAnalyzer = getPageEditor().ensureTextAnalyzer();
			for (FigurePanel figurePanel : panelList) {
				List<SVGElement> elements = SVGUtil.getQuerySVGElements(figurePanel, ".");
				textAnalyzer.analyzeSingleWordsOrLines(elements);
			}
		}
		if (isTrue(FigureAnalyzerActionX.ANALYZE_FRAGMENTS)) {
			figureAnalyzer.createFragmentsInsidePanelsForAllFigures();
			analyzeFigures(figureList);
			for (Figure figure : figureList) {
				CMLUtil.outputQuietly(figure.getFigureAnalysis(), new File(filename+"."+figure.getId()+SVGPlusConstantsX.XML), 1);
			}
		}
		for (Chunk figure : figureList) {
			GraphUtil.writeFileAsSVGSVGWithMouse(filename+"."+figure.getId()+SVGPlusConstantsX.SVG, figure);
		}
	}

	private void analyzeFigures(List<Figure> figureList) {
		LOG.trace("Figures: "+figureList.size());
		for (int i = 0; i < figureList.size(); i++) {
			Figure figure = figureList.get(i);
			List<FigurePanel> panelList = figure.getFigurePanelList();
			if (panelList != null) {
				LOG.trace("   Panels: "+panelList.size());
				for (int j = 0; j < panelList.size(); j++) {
					FigurePanel panel = panelList.get(j);
					List<FigureFragment> fragmentList = panel.getFragmentList();
					LOG.trace("      Fragments: "+fragmentList.size());
					for (int k = 0; k < fragmentList.size(); k++) {
						FigureFragment fragment = fragmentList.get(k);
						fragment.analyzePrimitives();
					}
				}
			}
			figure.getFigureAnalysis();
		}
		
	}

}
