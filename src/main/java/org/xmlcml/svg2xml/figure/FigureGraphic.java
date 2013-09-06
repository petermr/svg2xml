package org.xmlcml.svg2xml.figure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.HiddenGraphics;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.svg2xml.page.FigureAnalyzer;
import org.xmlcml.svg2xml.page.ImageAnalyzer;
import org.xmlcml.svg2xml.page.PathAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

public class FigureGraphic extends FigureComponent {

	private final static Logger LOG = Logger.getLogger(FigureGraphic.class);

	private static final Double OFFSET_X = 0.;
	private static final Double OFFSET_Y = 0.;
	private BufferedImage bufferedImage;

	public FigureGraphic(FigureAnalyzer figureAnalyzer) {
		super(figureAnalyzer);
	}

	public FigureGraphic(TextAnalyzer textAnalyzer,
			PathAnalyzer pathAnalyzer, ImageAnalyzer imageAnalyzer) {
		super(textAnalyzer, pathAnalyzer, imageAnalyzer);
	}

	public void createImageFromComponents(String pngName) {
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

	private HiddenGraphics createHiddenGraphics() {
		HiddenGraphics hg = new HiddenGraphics();
		hg.setDimension(svgContainer.getBoundingBox().getDimension());
		bufferedImage = hg.createImage(svgContainer);
		return hg;
	}

}
