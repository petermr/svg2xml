package org.xmlcml.svg2xml.analyzer;

/** checks result of running analyzer
 * 
 * @author pm286
 *
 */
public class CheckAnalyzer {

	Class<? extends AbstractPageAnalyzerX> clazz;
	private Integer count = null;
	private Integer imageCount;
	private Integer pathCount;
	private Integer textCount;
	private AbstractPageAnalyzerX imageAnalyzer;
	private AbstractPageAnalyzerX pathAnalyzer;
	private AbstractPageAnalyzerX textAnalyzer;
	
	public static CheckAnalyzer createCheckAnalyzer(AbstractPageAnalyzerX analyzer) {
		CheckAnalyzer  checkAnalyzer = null;
		if (analyzer instanceof TextAnalyzerX) {
			checkAnalyzer = new CheckAnalyzer((TextAnalyzerX) analyzer);
		} else if (analyzer instanceof PathAnalyzerX) {
			checkAnalyzer = new CheckAnalyzer((PathAnalyzerX) analyzer);
		} else if (analyzer instanceof MixedAnalyzer) {
			checkAnalyzer = new CheckAnalyzer((MixedAnalyzer) analyzer);
		} else if (analyzer instanceof ImageAnalyzerX) {
			checkAnalyzer = new CheckAnalyzer((ImageAnalyzerX) analyzer);
		} 
		return checkAnalyzer;
	}
	
	public CheckAnalyzer(ImageAnalyzerX imageAnalyzer) {
		this(ImageAnalyzerX.class, imageAnalyzer.getImageList().size());
	}
	
	public CheckAnalyzer(PathAnalyzerX pathAnalyzer) {
		this(PathAnalyzerX.class, pathAnalyzer.getPathList().size());
	}
	
	public CheckAnalyzer(MixedAnalyzer mixedAnalyzer) {
		this(MixedAnalyzer.class, 
				mixedAnalyzer.getImageAnalyzer(),
				mixedAnalyzer.getPathAnalyzer(),
				mixedAnalyzer.getTextAnalyzer());
	}
	
	public CheckAnalyzer(TextAnalyzerX textAnalyzer) {
		this(TextAnalyzerX.class, textAnalyzer.getTextCharacters().size());
	}
	
	public CheckAnalyzer(Class<? extends AbstractPageAnalyzerX> clazz, int count) {
		this.clazz = clazz;
		this.count = count;
	}
	
	public CheckAnalyzer(Class<? extends MixedAnalyzer> clazz, 
			ImageAnalyzerX imageAnalyzer, PathAnalyzerX pathAnalyzer, TextAnalyzerX textAnalyzer) {
		this.clazz = clazz;
		this.imageCount = imageAnalyzer == null ? 0 : imageAnalyzer.getImageList().size();
		this.pathCount = pathAnalyzer == null ? 0 : pathAnalyzer.getPathList().size();
		this.textCount = textAnalyzer == null ? 0 : textAnalyzer.getTextCharacters().size();
	}
	
	public CheckAnalyzer(Class<? extends MixedAnalyzer> clazz, 
			int imageCount, int pathCount, int textCount) {
		this.clazz = clazz;
		this.imageCount = imageCount;
		this.pathCount = pathCount;
		this.textCount = textCount;
	}
	
	@Override
	public boolean equals(Object analy) {
		boolean equals = false;
		if (analy instanceof CheckAnalyzer) {
			CheckAnalyzer analyzer = (CheckAnalyzer) analy;
			if (this.clazz.equals(MixedAnalyzer.class) && analyzer.clazz.equals(MixedAnalyzer.class)) {
				equals = (this.imageCount.equals(analyzer.imageCount)) && 
			         (this.pathCount.equals(analyzer.pathCount)) && 
			         (this.textCount.equals(analyzer.textCount)); 
			} else if (this.clazz.equals(analyzer.clazz)) {
				equals = (this.count.equals(analyzer.count));
			}
		}
		return equals;
	}
	
	public String toString() {
		return "image: "+imageCount+"; path "+pathCount+"; text "+textCount;
	}
}
