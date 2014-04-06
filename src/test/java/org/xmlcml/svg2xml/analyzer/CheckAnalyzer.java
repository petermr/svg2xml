package org.xmlcml.svg2xml.analyzer;

import org.xmlcml.svg2xml.page.ChunkAnalyzer;
import org.xmlcml.svg2xml.page.ImageAnalyzer;
import org.xmlcml.svg2xml.page.MixedAnalyzer;
import org.xmlcml.svg2xml.page.ShapeAnalyzer;
import org.xmlcml.svg2xml.page.TextAnalyzer;

/** checks result of running analyzer
 * 
 * @author pm286
 *
 */
public class CheckAnalyzer {

	Class<? extends ChunkAnalyzer> clazz;
	private Integer count = null;
	private Integer imageCount;
	private Integer shapeCount;
	private Integer textCount;
//	private ChunkAnalyzer imageAnalyzer;
//	private ChunkAnalyzer shapeAnalyzer;
//	private ChunkAnalyzer textAnalyzer;
	
	public static CheckAnalyzer createCheckAnalyzer(ChunkAnalyzer analyzer) {
		CheckAnalyzer  checkAnalyzer = null;
		if (analyzer instanceof TextAnalyzer) {
			checkAnalyzer = new CheckAnalyzer((TextAnalyzer) analyzer);
		} else if (analyzer instanceof ShapeAnalyzer) {
			checkAnalyzer = new CheckAnalyzer((ShapeAnalyzer) analyzer);
		} else if (analyzer instanceof MixedAnalyzer) {
			checkAnalyzer = new CheckAnalyzer((MixedAnalyzer) analyzer);
		} else if (analyzer instanceof ImageAnalyzer) {
			checkAnalyzer = new CheckAnalyzer((ImageAnalyzer) analyzer);
		} 
		return checkAnalyzer;
	}
	
	public CheckAnalyzer(ImageAnalyzer imageAnalyzer) {
		this(ImageAnalyzer.class, imageAnalyzer.getImageList().size());
	}
	
	public CheckAnalyzer(ShapeAnalyzer shapeAnalyzer) {
		this(ShapeAnalyzer.class, shapeAnalyzer.getShapeList().size());
	}
	
	public CheckAnalyzer(MixedAnalyzer mixedAnalyzer) {
		this(MixedAnalyzer.class, 
				mixedAnalyzer.getImageAnalyzer(),
				mixedAnalyzer.getShapeAnalyzer(),
				mixedAnalyzer.getTextAnalyzer());
	}
	
	public CheckAnalyzer(TextAnalyzer textAnalyzer) {
		this(TextAnalyzer.class, textAnalyzer.getTextCharacters().size());
	}
	
	public CheckAnalyzer(Class<? extends ChunkAnalyzer> clazz, int count) {
		this.clazz = clazz;
		this.count = count;
	}
	
	public CheckAnalyzer(Class<? extends MixedAnalyzer> clazz, 
			ImageAnalyzer imageAnalyzer, ShapeAnalyzer shapeAnalyzer, TextAnalyzer textAnalyzer) {
		this.clazz = clazz;
		this.imageCount = imageAnalyzer == null ? 0 : imageAnalyzer.getImageList().size();
		this.shapeCount = shapeAnalyzer == null ? 0 : shapeAnalyzer.getShapeList().size();
		this.textCount = textAnalyzer == null ? 0 : textAnalyzer.getTextCharacters().size();
	}
	
	public CheckAnalyzer(Class<? extends MixedAnalyzer> clazz, 
			int imageCount, int shapeCount, int textCount) {
		this.clazz = clazz;
		this.imageCount = imageCount;
		this.shapeCount = shapeCount;
		this.textCount = textCount;
	}
	
	@Override
	public boolean equals(Object analy) {
		boolean equals = false;
		if (analy instanceof CheckAnalyzer) {
			CheckAnalyzer analyzer = (CheckAnalyzer) analy;
			if (this.clazz.equals(MixedAnalyzer.class) && analyzer.clazz.equals(MixedAnalyzer.class)) {
				equals = (this.imageCount.equals(analyzer.imageCount)) && 
			         (this.shapeCount.equals(analyzer.shapeCount)) && 
			         (this.textCount.equals(analyzer.textCount)); 
			} else if (this.clazz.equals(analyzer.clazz)) {
				equals = (this.count.equals(analyzer.count));
			}
		}
		return equals;
	}
	
	public String toString() {
		return "image: "+imageCount+"; path "+shapeCount+"; text "+textCount;
	}
}
