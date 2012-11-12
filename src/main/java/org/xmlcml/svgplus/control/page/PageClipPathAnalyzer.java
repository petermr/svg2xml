package org.xmlcml.svgplus.control.page;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.xmlcml.svgplus.core.AbstractAnalyzer;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
<svg fill-opacity="1" xmlns:xlink="http://www.w3.org/1999/xlink" color-rendering="auto" color-interpolation="auto" stroke="black" text-rendering="auto" stroke-linecap="square" stroke-miterlimit="10" stroke-opacity="1" shape-rendering="auto" fill="black" stroke-dasharray="none" font-weight="normal" stroke-width="1" xmlns="http://www.w3.org/2000/svg" font-family="&apos;Dialog&apos;" font-style="normal" stroke-linejoin="miter" font-size="12" stroke-dashoffset="0" image-rendering="auto">
  <!--Generated by the Batik Graphics2D SVG Generator-->
  <defs id="genericDefs" />
  <g>
    <defs id="defs1">
      <clipPath clipPathUnits="userSpaceOnUse" id="clipPath1">
        <path d="M0 0 L60.9419 0 L60.9419 81.2217 L0 81.2217 L0 0 Z" />
      </clipPath>
...

      <text xml:space="preserve" x="5.8067" y="56.7175" clip-path="url(#clipPath1)" stroke="none">t</text>

 * @author pm286
 *
 */
public class PageClipPathAnalyzer {

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
	
	private SVGSVG svgPage;
	private AbstractAnalyzer pageAnalyzer;
	private List<SVGElement> svgClipPathList;
	private List<SVGElement> clipPathRefsList;
	private Multimap<String, SVGElement> elementsByClip;
	private Multimap<String, SVGPath> pathsByClip;
	private Multimap<String, SVGText> textsByClip;

	public PageClipPathAnalyzer(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
		this.svgPage = pageAnalyzer.getSVGPage();
	}
	
	public void analyze() {
		
		elementsByClip = ArrayListMultimap.create();
		pathsByClip = ArrayListMultimap.create();
		textsByClip = ArrayListMultimap.create();
		clipPathRefsList = SVGUtil.getQuerySVGElements(svgPage, "//svg:*[@clip-path]");
		svgClipPathList = SVGUtil.getQuerySVGElements(svgPage, "/svg:svg/svg:g/svg:defs/svg:clipPath[svg:path]");
		
		createMapsByClipPath();
		
		Set<String> clipSet = elementsByClip.keySet();
		if (clipSet.size() > fillColors.length) {
			System.err.println("Have to wrap colours for clipPaths: "+clipSet.size());
		}

		annotateClipPathsWithColour(clipSet);
	}

	public List<SVGElement> getSvgClipPathList() {
		return svgClipPathList;
	}

	public List<SVGElement> getClipPathRefsList() {
		return clipPathRefsList;
	}

	public Multimap<String, SVGElement> getElementsByClip() {
		return elementsByClip;
	}

	public Multimap<String, SVGPath> getPathsByClip() {
		return pathsByClip;
	}

	public Multimap<String, SVGText> getTextsByClip() {
		return textsByClip;
	}

	private void annotateClipPathsWithColour(Set<String> clipSet) {
		int i = 0;
		for (String clipPath : clipSet) {
			Collection<SVGElement> svgElementListByClip = elementsByClip.get(clipPath);
			for (SVGElement svgElement : svgElementListByClip) {
				annotateColour(svgElement, fillColors[i%fillColors.length]);
			}
			i++;
		}
	}

	private void createMapsByClipPath() {
		for (SVGElement svgElement : clipPathRefsList) {
			String clipPath = svgElement.getClipPath();
			if (svgElement instanceof SVGPath) {
				pathsByClip.put(clipPath, (SVGPath) svgElement);
			} else if (svgElement instanceof SVGText) {
				textsByClip.put(clipPath, (SVGText) svgElement);
			} else {
				elementsByClip.put(clipPath, svgElement);
			}
		}
	}
	
	public List<SVGElement> getClipPathList() {
		return svgClipPathList;
	}

	private void annotateColour(SVGElement svgElement, String fill) {
		SVGRect  bb = svgElement.createGraphicalBoundingBox();
		bb.setFill(fill);
		bb.setOpacity(0.5);
		svgPage.appendChild(bb);
	}
}
