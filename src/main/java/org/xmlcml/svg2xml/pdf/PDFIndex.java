package org.xmlcml.svg2xml.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Int2Range;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.ImageContainer;
import org.xmlcml.svg2xml.container.PathContainer;
import org.xmlcml.svg2xml.container.ScriptContainer;
import org.xmlcml.svg2xml.dead.HtmlAnalyzerDead;
import org.xmlcml.svg2xml.indexer.AbstractIndexer;
import org.xmlcml.svg2xml.indexer.AppendixIndexer;
import org.xmlcml.svg2xml.indexer.BibRefIndexer;
import org.xmlcml.svg2xml.indexer.ChapterIndexer;
import org.xmlcml.svg2xml.indexer.DOIIndexer;
import org.xmlcml.svg2xml.indexer.FigureIndexer;
import org.xmlcml.svg2xml.indexer.LicenceIndexer;
import org.xmlcml.svg2xml.indexer.MiscellaneousIndexer;
import org.xmlcml.svg2xml.indexer.SchemeIndexer;
import org.xmlcml.svg2xml.indexer.SummaryIndexer;
import org.xmlcml.svg2xml.indexer.TableIndexer;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.text.IntListPattern;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.ScriptWord;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;
import org.xmlcml.svg2xml.util.SVG2XMLConstantsX;
import org.xmlcml.svg2xml.util.TextFlattener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/** indexes a PDF
 * 
 * @author pm286
 *
 */
public class PDFIndex {

	private static final String WHITESPACE = "[\n\r\u0085\u2028\u2029]";
	private static final String DUPLICATE = "duplicate";
	public final static Logger LOG = Logger.getLogger(PDFIndex.class);

	private static final String BBOX = "bbox";
	public static final String CONTENT = "content";
	private static final String FIRST_INTEGER = "firstInteger";
	private static final String FLATTENED = "flattened";
	public static final String IMAGE = "image";
	public static final String PATH = "path";
	private final static PrintStream SYSOUT = System.out;

	public static final String CHUNK_TYPE = "chunkType";

	public static final String ABSTRACT = "abstract";
	public static final String APPENDIX = "appendix";
	public static final String BIBREF = "bibRef";
	public static final String CHAPTER = "chapter";
	public static final String DOI_CITE = "doiCite";
	public static final String FIGURE = "figure";
	public static final String LICENCE = "licence";
	public static final String SCHEME = "scheme";
	public static final String SNIPPET = "snippet";
	public static final String TABLE = "table";
	
	private int duplicateBboxCount;
	private int duplicateContentCount;
	private int duplicateFlattenedCount;
	private int duplicateFirstIntegerCount;
	private int duplicateImageCount;
	private int duplicatePathCount;
	
	private Map<ChunkId, SVGElement> svgElementByIdMap;
	private Multimap<String, ChunkId> svgIdByContentMap;
	private Multimap<String, ChunkId> svgIdByFlattenedMap;
	private Multimap<String, ChunkId> svgIdByFirstIntegerMap;
	private Multimap<String, ChunkId> svgIdByImageContentMap;
	private Multimap<String, ChunkId> svgIdByPathIdMap;
	private Multimap<String, ChunkId> svgIdByAppendixMap;
	private Multimap<String, ChunkId> svgIdByBibRefMap;
	private Multimap<String, ChunkId> svgIdByChapterMap;
	private Multimap<String, ChunkId> svgIdByFigureMap;
	private Multimap<String, ChunkId> svgIdBySchemeMap;
	private Multimap<String, ChunkId> svgIdByTableMap;

	private Multimap<Double, AbstractContainer> scriptContainerByBoldFontSize;
	private Multimap<Double, AbstractContainer> pathContainerByPathString;
	private Multimap<Double, AbstractContainer> imageContainerByImageString;

	private Multimap<Int2Range, ChunkId> bboxMap;

	PDFAnalyzer pdfAnalyzer;

	private List<List<ChunkId>> contentIdListList;
	private List<List<ChunkId>> flattenedIdListList;
	private List<List<ChunkId>> firstIntegerIdListList;
	private List<List<ChunkId>> imageIdListList;
	private List<List<ChunkId>> pathIdListList;
	private List<List<ChunkId>> bboxIdListList;

	Set<ChunkId> usedIdSet;
	
	private AppendixIndexer appendixAnalyzer;
	private BibRefIndexer   bibRefAnalyzer;
	private ChapterIndexer  chapterAnalyzer;
	private DOIIndexer      doiAnalyzer;
	        FigureIndexer  figureAnalyzer;
	private LicenceIndexer  licenceAnalyzer;
	private SchemeIndexer   schemeAnalyzer;
	private MiscellaneousIndexer  snippetAnalyzer;
	private SummaryIndexer  summaryAnalyzer;
	private TableIndexer   tableAnalyzer;

	private List<AbstractIndexer> analyzerList;

	public PDFIndex(PDFAnalyzer analyzer) {
		this.pdfAnalyzer = analyzer;
	}

	public <T extends Object> List<List<ChunkId>> findDuplicates(String title, Multimap<T, ChunkId> map) {
		List<List<ChunkId>> duplicateList = new ArrayList<List<ChunkId>>();
		for (T key : map.keySet()) {
			Collection<ChunkId> ids = map.get(key);
			List<ChunkId> idList = new ArrayList<ChunkId>(Arrays.asList(ids.toArray(new ChunkId[0])));
			removeUsedIds(idList);
			Collections.sort(idList);
			if (idList.size() > 1) {
				String keyS = key.toString();
				LOG.trace("DUPLICATES: "+title+" >"+ SVG2XMLUtil.trim(keyS, 15)+" ... "+"< "+idList);
				duplicateList.add(idList);
				addUsedIdList(idList);
			}
		}
		LOG.trace("USED: "+usedIdSet);
		return duplicateList;
	}

	private void addUsedIdList(List<ChunkId> idList) {
		usedIdSet.addAll(idList);
	}

	private void removeUsedIds(List<ChunkId> idList) {
		int size = idList.size();
		for (int i = size - 1; i >= 0; i--) {
			if (getUsedIdSet().contains(idList.get(i))) {
				idList.remove(i);
			}
		}
	}

	void ensureElementMultimaps() {
		if (svgIdByContentMap == null) {
			svgElementByIdMap = new HashMap<ChunkId, SVGElement>(); 
			svgIdByContentMap = HashMultimap.create(); 
			svgIdByFlattenedMap = HashMultimap.create(); 
			svgIdByFirstIntegerMap = HashMultimap.create(); 
			svgIdByImageContentMap = HashMultimap.create();
			duplicateImageCount = 0;
			svgIdByPathIdMap = HashMultimap.create(); 
			duplicatePathCount = 0;
			bboxMap = HashMultimap.create(); 
			duplicateBboxCount = 0;
			scriptContainerByBoldFontSize = HashMultimap.create();
		}
	}

	private void ensureContainerMaps() {
		if (scriptContainerByBoldFontSize == null){
			scriptContainerByBoldFontSize = HashMultimap.create();
			pathContainerByPathString = HashMultimap.create();
			imageContainerByImageString = HashMultimap.create();
		}
	}

	public void createIndexes() {
		
		contentIdListList = findDuplicates(CONTENT, svgIdByContentMap);
		markChunksAndNoteUsed(contentIdListList, CONTENT);
		printDuplicates(CONTENT, contentIdListList);

		flattenedIdListList = findDuplicates(FLATTENED, svgIdByFlattenedMap);
		markChunksAndNoteUsed(flattenedIdListList, FLATTENED);
		analyzeIntegers(flattenedIdListList);
		printDuplicates(FLATTENED, flattenedIdListList);
		
		imageIdListList = findDuplicates(IMAGE, svgIdByImageContentMap);
		markChunksAndNoteUsed(imageIdListList, IMAGE);
		printDuplicates(IMAGE, imageIdListList);
		
		pathIdListList = findDuplicates(PATH, svgIdByPathIdMap);
		markChunksAndNoteUsed(pathIdListList, PATH);
		printDuplicates(PATH, pathIdListList);
		
	}

	private void analyzeIntegers(List<List<ChunkId>> idListList) {
		for (List<ChunkId> idList : idListList) { 
			if (idList.size() > 0) {
				TextFlattener textFlattener = createTextFlattener(idList.get(0));
				List<List<Integer>> intListList = new ArrayList<List<Integer>>();
				for (ChunkId id : idList) {
					LOG.trace("NYI");
//					String htmlValue = getValue(id);
//					List<Integer> ints = textFlattener.captureIntegers(htmlValue);
//					intListList.add(ints);
				}
				try {
					IntMatrix intMatrix = IntMatrix.createByRows(intListList);
					LOG.trace("IM "+intMatrix);
					if (intMatrix != null) {
						for (int j = 0; j  < intMatrix.getCols(); j++) {
							IntArray column = intMatrix.extractColumnData(j);
							if (column.isArithmeticProgression(1)) {
								LOG.trace("PROG "+column);
							} else if (column.getConstant() != null) {
								LOG.trace("CONS "+column);
							}
						}
					}
				} catch (Exception e) {
					LOG.error("IntMatrix bug"+intListList, e);
				}
			}
		}
	}

	private TextFlattener createTextFlattener(ChunkId id0) {
		TextFlattener textFlattener = new TextFlattener();
//		String htmlValue0 = getValueFromHtml(id0);
		String htmlValue0 = "NYI";
		Pattern pattern = textFlattener.createIntegerPattern(htmlValue0);
		LOG.trace("Flattening pattern "+pattern);
		return textFlattener;
	}

	private void printDuplicates(String title, List<List<ChunkId>> idListList) {
//			if (idListList.size() > 0 ) {
//				LOG.trace("duplicate "+title);
//				for (List<ChunkId> idList : idListList) {
//					if (title.equals(CONTENT)) {
//	//				    output(idList, title);
//						duplicateContentCount++;
//					} else if (title.equals(FLATTENED)) {
//						LOG.trace("flattened"+idList);
//						outputDuplicates(idList, title);
//						duplicateFlattenedCount++;
//					} else if (title.equals(FIRST_INTEGER)) {
//						LOG.trace(FIRST_INTEGER+idList);
//						outputDuplicates(idList, title);
//						duplicateFirstIntegerCount++;
//					} else if (title.equals(BBOX)) {
//	//					output(idList,  title);
//						Set<String> set = new HashSet<String>();
//						for (ChunkId id : idList) {
//							HtmlAnalyzerOld htmlAnalyzer = getHtmlAnalyzerById(id);
//							// not all chunks are HTML
//							if (htmlAnalyzer != null) {
//								String s = htmlAnalyzer.toXML();
//								LOG.trace(s);
//								set.add(s);
//							}
//						}
//						if (set.size() == 1) {
//							LOG.trace("bbox "+set.toString());
//						}
//						duplicateBboxCount++;
//					} else if (title.equals(IMAGE)) {
//						outputDuplicates(idList, title);
//						duplicateImageCount++;
//					} else if (title.equals(PATH)) {
//						outputDuplicates(idList, title);
//						duplicatePathCount++;
//					}
//				}
//			}
		}

	private HtmlAnalyzerDead getHtmlAnalyzerById(ChunkId id) {
//		return pdfAnalyzer.htmlEditor.getHtmlAnalyzerByIdMap().get(id);
		return null;
	}

	/** preload a contentMap which can be used for several PDFAnalyzers
	 * Warning - may require a lot of memory
	 * @param contentMap
	 */
	public void setContentMap(Multimap<String, ChunkId> contentMap) {
		this.svgIdByContentMap = contentMap;
	}

	public void setDuplicateImageCount(int duplicateImageCount) {
		this.duplicateImageCount = duplicateImageCount;
	}

	public void setDuplicatePathCount(int duplicatePathCount) {
		this.duplicatePathCount = duplicatePathCount;
	}

	/** preload a imageMap which can be used for several PDFAnalyzers
	 * Warning - may require a lot of memory
	 * @param imageMap
	 */
	public void setImageMap(Multimap<String, ChunkId> imageMap) {
		this.svgIdByImageContentMap = imageMap;
	}

	/** preload a pathMap which can be used for several PDFAnalyzers
	 * Warning - may require a lot of memory
	 * @param pathMap
	 */
	public void setPathMap(Multimap<String, ChunkId> pathMap) {
		this.svgIdByPathIdMap = pathMap;
	}

	/** these are messy but canonicalising is far too slow
	 * 
	 * @param gOut
	 */
	void addToindexes(SVGG gOut) {
		String content = gOut.getValue();
		content.replaceAll(WHITESPACE, " ");
		ChunkId id = new ChunkId(gOut.getId());
		svgElementByIdMap.put(id, gOut);
		indexByBoundingBox(gOut, id);
		indexByTextContent(content, id);
		indexByImageContent(gOut, id);
		indexByPathContent(gOut, id);
	}

	public void indexByTextContent(String content, ChunkId id) {
		if (content.trim().length() > 0) {
			svgIdByContentMap.put(content, id);
			indexByFlattenedIntegerContent(content, id);
			indexByFirstIntegerContent(content, id);
			indexByContentAnalyzers(content, id);
		}
	}

	private void indexByContentAnalyzers(String content, ChunkId id) {
		ensureSemanticAnalyzers();
		ensureUsedIdSet();
		// skip after successful index; order is roughly most likely to hit
//		if (figureAnalyzer.indexAndLabelChunk(content, id) != null) return;
//		if (tableAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (bibRefAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (appendixAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (chapterAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (schemeAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (summaryAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (licenceAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (doiAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (snippetAnalyzer.indexAndLabelChunk(content, id) != null) return;
	}

	private void ensureSemanticAnalyzers() {
		if (figureAnalyzer == null) {
			analyzerList = new ArrayList<AbstractIndexer>();
			appendixAnalyzer = new AppendixIndexer(this);
			analyzerList.add(appendixAnalyzer);
			bibRefAnalyzer = new BibRefIndexer(this);
			analyzerList.add(bibRefAnalyzer);
			doiAnalyzer = new DOIIndexer(this);
			analyzerList.add(doiAnalyzer);
			chapterAnalyzer = new ChapterIndexer(this);
			analyzerList.add(chapterAnalyzer);
			figureAnalyzer = new FigureIndexer(this);
			analyzerList.add(figureAnalyzer);
			licenceAnalyzer = new LicenceIndexer(this);
			analyzerList.add(licenceAnalyzer);
			schemeAnalyzer = new SchemeIndexer(this);
			analyzerList.add(schemeAnalyzer);
			snippetAnalyzer = new MiscellaneousIndexer(this);
			analyzerList.add(snippetAnalyzer);
			summaryAnalyzer = new SummaryIndexer(this);
			analyzerList.add(summaryAnalyzer);
			tableAnalyzer = new TableIndexer(this);
			analyzerList.add(tableAnalyzer);
		}
	}

	private void indexByBoundingBox(SVGG gOut, ChunkId id) {
		Real2Range bbox = gOut.getBoundingBox();
		Int2Range i2r = new Int2Range(bbox);
		boolean added = bboxMap.put(i2r, id);
	}

	private void indexByPathContent(SVGG gOut, ChunkId id) {
		List<SVGPath> pathList = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(gOut, ".//svg:path"));
		if (pathList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (SVGPath path : pathList) {
				sb.append(path.getDString());
			}
			svgIdByPathIdMap.put(sb.toString(), id);
		}
	}

	private void indexByImageContent(SVGG gOut, ChunkId id) {
		List<SVGImage> imageList = SVGImage.extractImages(SVGUtil.getQuerySVGElements(gOut, ".//svg:image"));
		if (imageList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (SVGImage image : imageList) {
				String imageValue = image.getImageValue();
				LOG.trace(SVG2XMLUtil.trim(imageValue, 50));
				sb.append(imageValue);
			}
			String imageContent = sb.toString();
			svgIdByImageContentMap.put(imageContent, id);
		}
	}

	private String indexByFlattenedIntegerContent(String content, ChunkId id) {
		String flattened = TextFlattener.flattenDigitStrings(content);
		LOG.trace(id+"> "+flattened);
		svgIdByFlattenedMap.put(flattened, id);
		LOG.trace(">> "+svgIdByFlattenedMap);
		return flattened;
	}

	private String indexByFirstIntegerContent(String content, ChunkId id) {
		List<Object> tokens = TextFlattener.splitAtIntegers(content);
		String firstInteger = null;
		if (tokens.size() >= 2 
				&& tokens.get(0) instanceof String
				&& tokens.get(1) instanceof Number
				) {
			firstInteger = tokens.get(0)+"0";
			svgIdByFirstIntegerMap.put(firstInteger, id);
		}
		return firstInteger;
	}

	/** may be obsolete
	 * 
	 * @param id
	 * @param serial
	 * @param title
	 */
	public void outputDuplicates(List<ChunkId> idList, String title) {
		
		try {
			for (ChunkId id : idList) {
				File dir = new File("target/"+title+"/duplicate/");
				dir.mkdirs();
				File file = new File(dir, id+SVG2XMLConstantsX.DOT_SVG);
				SVGElement element = svgElementByIdMap.get(id);
				CMLUtil.debug(element, new FileOutputStream(file), 1);
				LOG.trace("wrote: "+file.getAbsolutePath());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** preload a bboxMap which can be used for several PDFAnalyzers
	 * @param bboxMap
	 */
	public void setBoundingBoxMap(Multimap<Int2Range, ChunkId> bboxMap) {
		this.bboxMap = bboxMap;
	}

	public void AnalyzeDuplicates() {
//		List<IntListPattern> extractedIntegerList = new ArrayList<IntListPattern>();
//		for (List<ChunkId> idList : flattenedIdListList) {
//			List<String> valueList = new ArrayList<String>();
//			for (ChunkId id : idList) {
//				HtmlAnalyzerOld htmlAnalyzer = getHtmlAnalyzerById(id);
//				// not all chunks are HTML
//				if (htmlAnalyzer != null) {
//					String value = htmlAnalyzer.getValue();
//					valueList.add(value);
//				}
//			}
//			Set<String> patternStringSet = new HashSet<String>();
//			Pattern pattern = null;
//			for (String value : valueList) {
//				pattern = TextFlattener.createDigitStringMatchingPatternCapture(value);
//				patternStringSet.add(pattern.toString());
//			}
//			if (patternStringSet.size() == 1 && valueList.size() > 0) {
//				TextFlattener textFlattener = new TextFlattener();
//				textFlattener.createIntegerPattern(valueList.get(0));
//				LOG.trace("T "+textFlattener.getIntegerPattern().toString());
//				for (String value : valueList) { 
//					List<Integer> integerList = textFlattener.captureIntegers(value);
//					LOG.trace("V "+value+" "+ integerList);
//					IntListPattern extractedInteger = new IntListPattern(pattern, integerList);
//					extractedIntegerList.add(extractedInteger);
//				}
//			}
//		}
//		for (IntListPattern extractedI : extractedIntegerList) {
//			LOG.trace("Pattern: "+extractedI.toString());
//		}
	}

	public void addUsedId(ChunkId id) {
		ensureUsedIdSet();
		getUsedIdSet().add(id);
	}

	private void ensureUsedIdSet() {
		if (getUsedIdSet() == null) {
			setUsedIdSet(new HashSet<ChunkId>());
		}
	}

	public Set<ChunkId> getUsedIdSet() {
		return usedIdSet;
	}

	public void setUsedIdSet(Set<ChunkId> usedIdSet) {
		this.usedIdSet = usedIdSet;
	}

	public TableIndexer getTableAnalyzer() {
		ensureSemanticAnalyzers();
		return tableAnalyzer;
	}

	public FigureIndexer getFigureAnalyzer() {
		ensureSemanticAnalyzers();
		return figureAnalyzer;
	}

	public List<AbstractIndexer> getAnalyzerList() {
		ensureSemanticAnalyzers();
		return analyzerList;
	}

	private void markChunksAndNoteUsed(List<List<ChunkId>> idListList, String title) {
		for (int i = 0; i < idListList.size(); i++) {
			List<ChunkId> idList = idListList.get(i);
			for (ChunkId id : idList) {
//				if (getUsedIdSet().contains(id)) continue;
//				HtmlAnalyzerOld analyzer = getHtmlAnalyzerById(id);
//				if (analyzer != null) {
//					analyzer.setClassAttribute(title+"."+i);
//					getUsedIdSet().add(id);
//				}
			}
		}
	}

	public void addToindexes(PageAnalyzer pageAnalyzer) {
		for (AbstractContainer container : pageAnalyzer.getAbstractContainerList()) {
			if (container instanceof ScriptContainer) {
				((ScriptContainer)container).addToIndexes(this);
			} else if (container instanceof PathContainer) {
				((PathContainer)container).addToIndexes(this);
			} else if (container instanceof ImageContainer) {
				((ImageContainer)container).addToIndexes(this);
			} else {
				LOG.trace("Cannot index " + container.getClass());
			}
		}
//		LOG.debug("PageAnalyzer NYI");
	}

	public void addToBoldIndex(Double fontSize, ScriptContainer scriptContainer) {
		ensureContainerMaps();
		LOG.trace("Adding: "+fontSize+" "+scriptContainer);
		scriptContainerByBoldFontSize.put(fontSize, scriptContainer);
	}

	public void addToPathIndex(String pathString, PathContainer pathContainer) {
		LOG.trace("NYI Adding: "+pathString+" "+pathContainer);
	}

	public void addToImageIndex(String imageString, ImageContainer imageContainer) {
		LOG.trace("NYI Adding: "+imageString+" "+imageContainer);
	}

	public void analyzeContainers() {
		analyzeScriptContainerIndexes();
	}

	private void analyzeScriptContainerIndexes() {
		Set<Double> fontSet = scriptContainerByBoldFontSize.keySet();
		fontSet.remove((Double) null);
		Double[] fontSizes = fontSet.toArray(new Double[0]);
		if (fontSizes != null) {
			Arrays.sort(fontSizes);
			for (Double fontSize : fontSizes) {
				LOG.trace("************* "+fontSize);
				List<AbstractContainer> containers = getListByKey(fontSize);
				for (AbstractContainer container : containers) {
					if (container instanceof ScriptContainer) {
						ScriptContainer scriptContainer = (ScriptContainer) container;
						for (ScriptLine script : scriptContainer) {
							RealRangeArray wordArray = script.getWordRangeArray();
							wordArray.sortAndRemoveOverlapping();
							wordArray.format(pdfAnalyzer.getDecimalPlaces());
							LOG.trace("wordArray >>>>>>>> "+wordArray);
							for (SVGText character : script.getSVGTextCharacters()) {
								LOG.trace(character.getValue()+"_"+character.getX()+" ");
							}
//							SYSOUT.println();
							List<ScriptWord> words = script.getWords();
							for (ScriptWord word : words) {
								LOG.trace(" ~  "+word.getRawValue());
							}
//							System.out.println();
						}
					}
					LOG.trace("------"+container.getRawValue());
				}
			}
		}
	}

	private List<AbstractContainer> getListByKey(Double fontSize) {
		Collection<AbstractContainer> containers = scriptContainerByBoldFontSize.get(fontSize);
		List<AbstractContainer> containerList = new ArrayList<AbstractContainer>();
		for (Iterator<AbstractContainer> iter = containers.iterator() ; iter.hasNext(); ) {containerList.add(iter.next());}
		return containerList;
	}
}
