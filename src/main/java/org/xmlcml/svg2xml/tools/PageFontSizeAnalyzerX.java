package org.xmlcml.svg2xml.tools;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.page.PageChunkAnalyzer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**

 * @author pm286
 *
 */
public class PageFontSizeAnalyzerX extends PageChunkAnalyzer {

	private static final Logger LOG = Logger.getLogger(PageFontSizeAnalyzerX.class);

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

	public PageFontSizeAnalyzerX() {
		super();
	}
	
	public void analyze() {
		elementsByFontSize = ArrayListMultimap.create();
		LOG.trace("getting font sizes");
		textList = SVGUtil.getQuerySVGElements(pageEditorX.getSVGPage(), "//svg:text[@font-size]");
		LOG.trace("creating maps");
		createMapsForElementsByFontSize();
		LOG.trace("created maps");
	}

	public Multimap<Integer, SVGElement> createMapsForElementsByFontSize() {
		for (SVGElement svgElement : textList) {
			Integer fontSize100 = (int) (100.*svgElement.getFontSize());
			elementsByFontSize.put(fontSize100, svgElement);
		}
		return elementsByFontSize;
	}
	
}
