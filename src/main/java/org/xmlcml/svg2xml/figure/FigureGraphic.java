package org.xmlcml.svg2xml.figure;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.HiddenGraphics;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.html.HtmlA;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlImg;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.page.ImageAnalyzer;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.PathAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

public class FigureGraphic extends FigureComponent {

	private final static Logger LOG = Logger.getLogger(FigureGraphic.class);

	private static final Double OFFSET_X = 0.;
	private static final Double OFFSET_Y = 0.;
	private static final double DIMENSION_MARGIN = 1.05;
	
	private BufferedImage bufferedImage;

	public FigureGraphic(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}

	public FigureGraphic(FigureAnalyzer figureAnalyzer) {
		super(figureAnalyzer);
	}

	public FigureGraphic(TextAnalyzer textAnalyzer,
			PathAnalyzer pathAnalyzer, ImageAnalyzer imageAnalyzer) {
		super(textAnalyzer, pathAnalyzer, imageAnalyzer);
	}

	public void createImageFromComponents(String pngName) {
		if (svgContainer != null) {
			Real2 translateToOrigin = new Real2(-boundingBox.getXMin() + OFFSET_X, -boundingBox.getYMin() + OFFSET_Y);
			svgContainer.setTransform(new Transform2(new Vector2(translateToOrigin)));
			SVG2XMLUtil.tidy(svgContainer);
			
			HiddenGraphics hg = createHiddenGraphics();
			try {
				hg.write(SVGImage.IMAGE_PNG, new File(pngName));
			} catch (IOException e) {
				throw new RuntimeException("Cannot write image", e);
			}
		}
	}

	private HiddenGraphics createHiddenGraphics() {
		HiddenGraphics hg = new HiddenGraphics();
		Dimension dimension = svgContainer.getDimension();
		hg.setDimension(dimension);
		hg.setDimension(new Dimension((int)(dimension.width*DIMENSION_MARGIN), (int)(dimension.height*DIMENSION_MARGIN)));
		bufferedImage = hg.createImageTranslatedToOrigin(svgContainer);
		return hg;
	}

	public void saveAsSVG(String svgName) {
		if (svgContainer != null) {
			SVGSVG.wrapAndWriteAsSVG(svgContainer, new File(svgName));
		}
	}

	public void createAndWriteImageAndSVG(String imageName, HtmlDiv div, String svgName) {
		this.createImageFromComponents(imageName);
		FigureGraphic.addSvgToDiv(div, svgName);
		this.saveAsSVG(svgName);
	}


	public static HtmlDiv createHtmlImgDivElement(String pngName, String width) {
		HtmlDiv div = new HtmlDiv();
		HtmlImg img = new HtmlImg();
		img.setSrc("../../../"+pngName);
		img.addAttribute(new Attribute("width", width));
		div.appendChild(img);
		return div;
	}
	
	public static void addSvgToDiv(HtmlDiv div, String svgName) {
		HtmlA a = new HtmlA();
		a.setHref("../../../"+svgName);
		a.setValue("SVG");
		a.setTarget(HtmlElement.Target.separate);
		div.appendChild(a);
	}


}
