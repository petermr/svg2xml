package org.xmlcml.svg2xml.page;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.page.TextAnalyzerOLD.TextOrientation;
import org.xmlcml.svg2xml.text.TextStructurerOLD;

/** Analyzer for a graphic object.
 * <p>
 * The object is most likely the graphic part of a figure (the other is the caption).
 * The object may have text, Shapes and Images. Some may be null, but there are normally 
 * Shapes and/or Images.
 * <p>
 * Normally reads a chunk (svgElement) and creates analyzers for the components. Can be recursive
 * (i.e. a graphicAnalyzer object can be split unto smaller objects each with a graphics analyzer).
 * 
 * @author pm286
 */
public class GraphicAnalyzer extends ChunkAnalyzer {

	static final Logger LOG = Logger.getLogger(FigureAnalyzer.class);

	private static final Double YEPS = 2.0;
	
	private ImageAnalyzer imageAnalyzer;
	private ShapeAnalyzer shapeAnalyzer;
	private TextAnalyzerOLD textAnalyzer;

	public GraphicAnalyzer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public GraphicAnalyzer(PageAnalyzer pageAnalyzer, SVGElement svgChunk) {
		super(pageAnalyzer);
		setSVGChunk(svgChunk);
		createAnalyzers();
	}

	/** 
	 * Creates GraphicAnalyzer.
	 * 
	 * PageAnalyzer is dummy.
	 * 
	 * @param svgChunk
	 */
	public GraphicAnalyzer(SVGElement svgChunk) {
		this(new PageAnalyzer((SVGSVG) null), svgChunk);
	}

	private void createAnalyzers() {
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgChunk);
		if (textList != null && textList.size() > 0) {
			textAnalyzer = new TextAnalyzerOLD(textList, pageAnalyzer);
		}
		List<SVGShape> shapeList = SVGShape.extractSelfAndDescendantShapes(svgChunk);
		if (shapeList != null && shapeList.size() > 0) {
			shapeAnalyzer = new ShapeAnalyzer(shapeList, pageAnalyzer);
		}
		List<SVGImage> imageList = SVGImage.extractSelfAndDescendantImages(svgChunk);
		if (imageList != null && imageList.size() > 0) {
			imageAnalyzer = new ImageAnalyzer(imageList, pageAnalyzer);
		}
	}

	/** 
	 * Convenience method, mainly for tests.
	 * <p>
	 * Finds a chunk (using SVGG.createSVGG(file, xPath) and then creates 
	 * GraphicAnalyzer to analyze it. TODO something.
	 * 
	 * @param svgFile
	 * @param xPath to search for chunk (normally &lt;g&gt; containing the graphic)
	 * @return null first in list; if no chunk found
	 */
	public static GraphicAnalyzer createGraphicAnalyzer(File svgFile, String xPath)  {
		return GraphicAnalyzer.createGraphicAnalyzer(svgFile, xPath, 0);
	}

	/** 
	 * Convenience method, mainly for tests.
	 * <p>
	 * Finds a chunk (using SVGG.createSVGG(file, xPath)) and then creates 
	 * GraphicAnalyzer to analyze it. TODO something.
	 * 
	 * @param svgFile
	 * @param xPath to search for chunk (normally &lt;g&gt; containing the graphic)
	 * @param index result in list (counts from zero)
	 * @return null if no chunk found
	 */
	public static GraphicAnalyzer createGraphicAnalyzer(File svgFile, String xPath, int index)  {
		SVGG chunk = SVGG.createSVGGChunk(svgFile, xPath, index);
		GraphicAnalyzer graphicAnalyzer = null;
		if (chunk != null) {
			PageAnalyzer pageAnalyzer = new PageAnalyzer(svgFile);
			graphicAnalyzer = new GraphicAnalyzer(pageAnalyzer, chunk);
		}
		return graphicAnalyzer;
	}
	
	/** 
	 * Create TextAnalyzer for given orientation.
	 * <p>
	 * Extracts the characters with given orientation and prepares for TextStructurer.
	 * 
	 * @param textOrientation
	 * @return
	 */
	public TextAnalyzerOLD createTextAnalyzer(TextOrientation textOrientation) {
		TextAnalyzerOLD textAnalyzer = null;
		if (TextOrientation.ROT_0.equals(textOrientation)) {
			textAnalyzer = getRot0TextAnalyzer();
		} else if (TextOrientation.ROT_PI2.equals(textOrientation)) {
			textAnalyzer = getRotPi2TextAnalyzer();
		} else if (TextOrientation.ROT_PI.equals(textOrientation)) {
			textAnalyzer = getRotPiTextAnalyzer();
		} else if (TextOrientation.ROT_3PI2.equals(textOrientation)) {
			textAnalyzer = getRot3Pi2TextAnalyzer();
		} else {
			
		}
		return textAnalyzer;
	}

	/** 
	 * Create TextStructurer for given orientation.
	 * 
	 * Create a textAnalyzer and then the TextStructurer primed with the 
	 * characters for that orientation.
	 * 
	 * @param graphicAnalyzer
	 * @param textOrientation
	 * @return null if unknown orientation
	 */
	public  TextStructurerOLD createTextStructurer(TextOrientation textOrientation) {
		TextAnalyzerOLD textAnalyzer = createTextAnalyzer(textOrientation);
		return (textAnalyzer == null ? null : new TextStructurerOLD(textAnalyzer));
	}

	public ImageAnalyzer getImageAnalyzer() {
		return imageAnalyzer;
	}

	public ShapeAnalyzer getShapeAnalyzer() {
		return shapeAnalyzer;
	}

	public TextAnalyzerOLD getTextAnalyzer() {
		return textAnalyzer;
	}

	public TextAnalyzerOLD getRot0TextAnalyzer() {
		return (textAnalyzer == null ? null : textAnalyzer.getRot0TextAnalyzer());
	}

	public TextAnalyzerOLD getRotPi2TextAnalyzer() {
		return (textAnalyzer == null ? null : textAnalyzer.getRotPi2TextAnalyzer());
	}

	public TextAnalyzerOLD getRotPiTextAnalyzer() {
		return (textAnalyzer == null ? null : textAnalyzer.getRotPiTextAnalyzer());
	}

	public TextAnalyzerOLD getRot3Pi2TextAnalyzer() {
		return textAnalyzer == null ? null : textAnalyzer.getRot3Pi2TextAnalyzer();
	}

	public TextAnalyzerOLD getRotIrregularTextAnalyzer() {
		return textAnalyzer == null ? null : textAnalyzer.getRotIrregularTextAnalyzer();
	}

	public List<SVGText> getAllTextCharacters() {
		return (textAnalyzer == null) ? new ArrayList<SVGText>() : textAnalyzer.getTextCharacters();
	}

	public List<SVGText> getRot0TextCharacters() {
		return (textAnalyzer == null) ? new ArrayList<SVGText>() : textAnalyzer.getRot0TextCharacters();
	}

	public List<SVGText> getRotPi2TextCharacters() {
		return (textAnalyzer == null) ? new ArrayList<SVGText>() : textAnalyzer.getRotPi2TextCharacters();
	}

	public List<SVGText> getRotPiTextCharacters() {
		return (textAnalyzer == null) ? new ArrayList<SVGText>() : textAnalyzer.getRotPiTextCharacters();
	}

	public List<SVGText> getRot3Pi2TextCharacters() {
		return (textAnalyzer == null) ? new ArrayList<SVGText>() : textAnalyzer.getRot3Pi2TextCharacters();
	}

}