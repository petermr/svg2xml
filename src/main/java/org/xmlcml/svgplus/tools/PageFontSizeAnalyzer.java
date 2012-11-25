package org.xmlcml.svgplus.tools;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractPageAnalyzer;
import org.xmlcml.svgplus.command.PageEditor;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**

 * @author pm286
 *
 */
public class PageFontSizeAnalyzer extends AbstractPageAnalyzer {

	private static final Logger LOG = Logger.getLogger(PageFontSizeAnalyzer.class);

	public static String[] fillColors = {
		"red",
		"green",
		"blue",
		"magenta",
		"cyan",
		"yellow",
		"magenta",
		"grey",
		"pink",
		"lime",
	};
	
	private List<SVGElement> textList;
	private Multimap<Integer, SVGElement> elementsByFontSize;

	public Multimap<Integer, SVGElement> getElementsByFontSize() {
		return elementsByFontSize;
	}

	public PageFontSizeAnalyzer(SemanticDocumentAction semanticDocumentAction) {
		super(semanticDocumentAction);
	}
	
	public void analyze() {
		elementsByFontSize = ArrayListMultimap.create();
		LOG.debug("getting font sizes");
		textList = SVGUtil.getQuerySVGElements(pageEditor.getSVGPage(), "//svg:text[@font-size]");
		LOG.debug("creating maps");
		createMapsForElementsByFontSize();
		LOG.debug("created maps");
	}

	public Multimap<Integer, SVGElement> createMapsForElementsByFontSize() {
		for (SVGElement svgElement : textList) {
			Integer fontSize100 = (int) (100.*svgElement.getFontSize());
			elementsByFontSize.put(fontSize100, svgElement);
		}
		return elementsByFontSize;
	}
	
}
