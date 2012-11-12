package org.xmlcml.svgplus.figure;


import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.control.page.PageAction;
import org.xmlcml.svgplus.core.Chunk;
import org.xmlcml.svgplus.text.TextAnalyzer;
import org.xmlcml.svgplus.util.GraphUtil;
import org.xmlcml.svgplus.util.PConstants;

public class FigureAnalyzerAction extends PageAction {


	private final static Logger LOG = Logger.getLogger(FigureAnalyzerAction.class);
	private String filename;

	public FigureAnalyzerAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	@Override
	public void run() {
		FigureAnalyzer figureAnalyzer = pageAnalyzer.ensureFigureAnalyzer();
		figureAnalyzer.setLocationStrategy(getAndExpand(FigureAnalyzerElement.LOCATION_STRATEGY));
		List<Figure> figureList = figureAnalyzer.createFigures();
		List<FigurePanel> panelList = figureAnalyzer.createPanelsUsingWhitespace();
		filename = getFilename();
		if (isTrue(FigureAnalyzerElement.CREATE_WORDS_LINES)) {
			TextAnalyzer textAnalyzer = pageAnalyzer.ensureTextAnalyzer();
			for (FigurePanel figurePanel : panelList) {
				List<SVGElement> elements = SVGUtil.getQuerySVGElements(figurePanel, ".");
				textAnalyzer.analyzeSingleWordsOrLines(elements);
			}
		}
		if (isTrue(FigureAnalyzerElement.ANALYZE_FRAGMENTS)) {
			figureAnalyzer.createFragmentsInsidePanelsForAllFigures();
			analyzeFigures(figureList);
			for (Figure figure : figureList) {
				CMLUtil.outputQuietly(figure.getFigureAnalysis(), new File(filename+"."+figure.getId()+PConstants.XML), 1);
			}
		}
		for (Chunk figure : figureList) {
			GraphUtil.writeFileAsSVGSVGWithMouse(filename+"."+figure.getId()+PConstants.SVG, figure);
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
