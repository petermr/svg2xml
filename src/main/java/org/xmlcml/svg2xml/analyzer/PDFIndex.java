package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Int2Range;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.action.SVGPlusConstantsX;
import org.xmlcml.svg2xml.util.TextFlattener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/** indexes a PDF
 * 
 * @author pm286
 *
 */
public class PDFIndex {


	private final static Logger LOG = Logger.getLogger(PDFIndex.class);

	private static final String BBOX = "bbox";
	public static final String CONTENT = "content";
	private static final String FIRST_INTEGER = "firstInteger";
	private static final String FLATTENED = "flattened";
	public static final String IMAGE = "image";
	public static final String PATH = "path";
	
	public static final String APPENDIX = "appendix";
	public static final String BIBREF = "bibRef";
	public static final String CHAPTER = "chapter";
	public static final String FIGURE = "figure";
	public static final String SCHEME = "scheme";
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
	private Map<ChunkId, HtmlElement> htmlElementByIdMap;

	private Multimap<String, ChunkId> svgIdByAppendixMap;
	private Multimap<String, ChunkId> svgIdByBibRefMap;
	private Multimap<String, ChunkId> svgIdByChapterMap;
	private Multimap<String, ChunkId> svgIdByFigureMap;
	private Multimap<String, ChunkId> svgIdBySchemeMap;
	private Multimap<String, ChunkId> svgIdByTableMap;


	private Multimap<Int2Range, ChunkId> bboxMap;

	private PDFAnalyzer pdfAnalyzer;

	private List<List<ChunkId>> contentIdListList;
	private List<List<ChunkId>> flattenedIdListList;
	private List<List<ChunkId>> firstIntegerIdListList;
	private List<List<ChunkId>> imageIdListList;
	private List<List<ChunkId>> pathIdListList;
	private List<List<ChunkId>> bboxIdListList;

//	private List<List<ChunkId>> appendixIdListList;
//	private List<List<ChunkId>> bibRefIdListList;
//	private List<List<ChunkId>> chapterIdListList;
//	private List<List<ChunkId>> figureIdListList;
//	private List<List<ChunkId>> schemeIdListList;
//	private List<List<ChunkId>> tableIdListList;


	private Set<ChunkId> usedIdSet;

	private AppendixAnalyzer appendixAnalyzer;
	private BibRefAnalyzer bibRefAnalyzer;
	private ChapterAnalyzer chapterAnalyzer;
	private FigureAnalyzerX figureAnalyzer;
	private SchemeAnalyzer schemeAnalyzer;
	private TableAnalyzerX tableAnalyzer;

	private Set<ChunkId> usedSet;
	
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
				LOG.debug("DUPLICATES: "+title+" >"+ keyS.substring(0, Math.min(15, keyS.length()))+" ... "+"< "+idList);
				duplicateList.add(idList);
			}
		}
		return duplicateList;
	}

	private void removeUsedIds(List<ChunkId> idList) {
		int size = idList.size();
		for (int i = size - 1; i >= 0; i--) {
			if (usedIdSet.contains(idList.get(i))) {
				idList.remove(i);
			}
		}
	}

	void ensureElementMultimaps() {
		if (svgIdByContentMap == null) {
			setHtmlElementByIdMap(new HashMap<ChunkId, HtmlElement>()); 
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
		}
	}

	public void createIndexes() {
		
		usedIdSet = new HashSet<ChunkId>();
		
		makeAppendixIndex();
		makeBibRefIndex();
		makeChapterIndex();
		makeFigureIndex();
		makeSchemeIndex();
		makeTableIndex();

		contentIdListList = findDuplicates(CONTENT, svgIdByContentMap);
		markChunksAndNoteUsed(contentIdListList, CONTENT);
		printDuplicates(CONTENT, contentIdListList);

		flattenedIdListList = findDuplicates(FLATTENED, svgIdByFlattenedMap);
		markChunksAndNoteUsed(flattenedIdListList, FLATTENED);
		analyzeIntegers(flattenedIdListList);
		printDuplicates(FLATTENED, flattenedIdListList);
		
		firstIntegerIdListList = findDuplicates(FIRST_INTEGER, svgIdByFirstIntegerMap);
		markChunksAndNoteUsed(firstIntegerIdListList, FIRST_INTEGER);
		analyzeFirstIntegers(firstIntegerIdListList);
		printDuplicates(FIRST_INTEGER, firstIntegerIdListList);
		
		imageIdListList = findDuplicates(IMAGE, svgIdByImageContentMap);
		markChunksAndNoteUsed(imageIdListList, IMAGE);
		printDuplicates(IMAGE, imageIdListList);
		
		pathIdListList = findDuplicates(PATH, svgIdByPathIdMap);
		markChunksAndNoteUsed(pathIdListList, PATH);
		printDuplicates(PATH, pathIdListList);
		
		bboxIdListList = findDuplicates(BBOX, bboxMap);
		markChunksAndNoteUsed(bboxIdListList, BBOX);
		printDuplicates(BBOX, bboxIdListList);
	}

	private List<List<ChunkId>> makeTableIndex() {
		List<List<ChunkId>> chunkList = new ArrayList<List<ChunkId>>();
		return chunkList;
	}

	private List<List<ChunkId>> makeSchemeIndex() {
		List<List<ChunkId>> chunkList = new ArrayList<List<ChunkId>>();
		return chunkList;
	}

	private List<List<ChunkId>> makeFigureIndex() {
		List<List<ChunkId>> chunkList = new ArrayList<List<ChunkId>>();
		Pattern pattern = Pattern.compile("^(Figu?r?e?)\\s*\\.?\\s*(\\d+)");
		return chunkList;
	}

	private List<List<ChunkId>> makeChapterIndex() {
		List<List<ChunkId>> chunkList = new ArrayList<List<ChunkId>>();
		return chunkList;
	}

	private List<List<ChunkId>> makeBibRefIndex() {
		List<List<ChunkId>> chunkList = new ArrayList<List<ChunkId>>();
		return chunkList;
	}

	private List<List<ChunkId>> makeAppendixIndex() {
		List<List<ChunkId>> chunkList = new ArrayList<List<ChunkId>>();
		return chunkList;
	}

	private void analyzeIntegers(List<List<ChunkId>> idListList) {
		for (List<ChunkId> idList : idListList) { 
			if (idList.size() > 0) {
				TextFlattener textFlattener = createTextFlattener(idList.get(0));
				List<List<Integer>> intListList = new ArrayList<List<Integer>>();
				for (ChunkId id : idList) {
					String htmlValue = getValueFromHtml(id);
					List<Integer> ints = textFlattener.captureIntegers(htmlValue);
					intListList.add(ints);
				}
				try {
					IntMatrix intMatrix = IntMatrix.createByRows(intListList);
					LOG.debug("IM "+intMatrix);
					for (int j = 0; j  < intMatrix.getCols(); j++) {
						IntArray column = intMatrix.extractColumnData(j);
						if (column.isArithmeticProgression(1)) {
							System.out.println("PROG "+column);
						} else if (column.getConstant() != null) {
							System.out.println("CONS "+column);
						}
					}
				} catch (Exception e) {
					LOG.error("IntMatrix bug"+intListList, e);
				}
			}
		}
	}

	private void analyzeFirstIntegers(List<List<ChunkId>> idListList) {
		for (List<ChunkId> idList : idListList) { 
			if (idList.size() > 0) {
				Pattern pattern = createFirstIntegerPattern(idList.get(0));
				IntArray intArray = new IntArray();
				for (ChunkId id : idList) {
					String htmlValue = getValueFromHtml(id);
					Integer integer = captureFirstInteger(pattern, htmlValue);
					if (integer != null) {
						intArray.addElement(integer);
					}
				}
				if (intArray.isArithmeticProgression(1)) {
					System.out.println("PROG "+intArray);
				}
			}
		}
	}

	private Integer captureFirstInteger(Pattern pattern, String htmlValue) {
		Integer integer = null;
		if (htmlValue != null && pattern != null) {
			Matcher matcher = pattern.matcher(htmlValue);
			if (matcher.matches()) {
				String ii = matcher.group(2);
				integer = new Integer(ii);
			}
		}
		return integer;
	}

	private Pattern createFirstIntegerPattern(ChunkId id0) {
		String htmlValue0 = getValueFromHtml(id0);
		Pattern pattern = TextFlattener.createFirstIntegerPattern(htmlValue0);
		return pattern;
	}

	private TextFlattener createTextFlattener(ChunkId id0) {
		TextFlattener textFlattener = new TextFlattener();
		String htmlValue0 = getValueFromHtml(id0);
		Pattern pattern = textFlattener.createIntegerPattern(htmlValue0);
		LOG.debug("P "+pattern);
		return textFlattener;
	}

	private String getValueFromHtml(ChunkId id) {
		HtmlElement htmlElement = getHtmlElementByIdMap().get(id);
		if (htmlElement == null) {
			throw new RuntimeException("no HTML for id: "+id);
		}
		String htmlValue = htmlElement.getValue();
		return htmlValue;
	}

	private void markChunksAndNoteUsed(List<List<ChunkId>> idListList, String title) {
		for (int i = 0; i < idListList.size(); i++) {
			List<ChunkId> idList = idListList.get(i);
			for (ChunkId id : idList) {
				if (usedIdSet.contains(id)) continue;
				HtmlElement element = getHtmlElementByIdMap().get(id);
				if (element != null) {
					element.setClassAttribute(title+"."+i);
					usedIdSet.add(id);
				}
			}
		}
	}

	void indexHtmlBySvgId(HtmlElement htmlElement, ChunkId chunkId) {
		ensureElementMultimaps();
		getHtmlElementByIdMap().put(chunkId, htmlElement);
	}

	private void printDuplicates(String title, List<List<ChunkId>> idListList) {
			if (idListList.size() > 0 ) {
				LOG.trace("duplicate "+title);
				for (List<ChunkId> idList : idListList) {
					if (title.equals(CONTENT)) {
	//				    output(idList, title);
						duplicateContentCount++;
					} else if (title.equals(FLATTENED)) {
						LOG.trace("flattened"+idList);
						output(idList, title);
						duplicateFlattenedCount++;
					} else if (title.equals(FIRST_INTEGER)) {
						LOG.trace(FIRST_INTEGER+idList);
						output(idList, title);
						duplicateFirstIntegerCount++;
					} else if (title.equals(BBOX)) {
	//					output(idList,  title);
						Set<String> set = new HashSet<String>();
						for (ChunkId id : idList) {
							HtmlElement element = getHtmlElementByIdMap().get(id);
							// not all chunks are HTML
							if (element != null) {
								String s = element.toXML();
								LOG.trace(s);
								set.add(s);
							}
						}
						if (set.size() == 1) {
							LOG.debug("bbox "+set.toString());
						}
						duplicateBboxCount++;
					} else if (title.equals(IMAGE)) {
						output(idList, title);
						duplicateImageCount++;
					} else if (title.equals(PATH)) {
						output(idList, title);
						duplicatePathCount++;
					}
				}
			}
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
		content.replaceAll("[\n\r\u0085\u2028\u2029]", " ");
		ChunkId id = new ChunkId(gOut.getId());
		svgElementByIdMap.put(id, gOut);
		indexByBoundingBox(gOut, id);
		indexByTextContent(content, id);
		indexByImageContent(gOut, id);
		indexByPathContent(gOut, id);
	}

	private void indexByTextContent(String content, ChunkId id) {
		if (content.trim().length() > 0) {
			svgIdByContentMap.put(content, id);
			indexByFlattenedIntegerContent(content, id);
			indexByFirstIntegerContent(content, id);
			indexByContentAnalyzers(content, id);
		}
	}

	private void indexByContentAnalyzers(String content, ChunkId id) {
		ensureContentAnalyzers();
		ensureUsedIdSet();
		if (figureAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (tableAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (bibRefAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (appendixAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (chapterAnalyzer.indexAndLabelChunk(content, id) != null) return;
		if (schemeAnalyzer.indexAndLabelChunk(content, id) != null) return;
	}

	private void ensureContentAnalyzers() {
		if (figureAnalyzer == null) {
			appendixAnalyzer = new AppendixAnalyzer(this);
			bibRefAnalyzer = new BibRefAnalyzer(this);
			chapterAnalyzer = new ChapterAnalyzer(this);
			figureAnalyzer = new FigureAnalyzerX(this);
			schemeAnalyzer = new SchemeAnalyzer(this);
			tableAnalyzer = new TableAnalyzerX(this);
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
				LOG.trace(imageValue.substring(0, Math.min(50, imageValue.length()))+" ... ");
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
	public void output(List<ChunkId> idList, String title) {
		
		try {
			for (ChunkId id : idList) {
				File dir = new File("target/"+title+"/duplicate/");
				dir.mkdirs();
				File file = new File(dir, id+SVGPlusConstantsX.SVG);
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
		List<IntListPattern> extractedIntegerList = new ArrayList<IntListPattern>();
		for (List<ChunkId> idList : flattenedIdListList) {
			List<String> valueList = new ArrayList<String>();
			for (ChunkId id : idList) {
				HtmlElement html = getHtmlElementByIdMap().get(id);
				// not all chunks are HTML
				if (html != null) {
					String value = html.getValue();
					valueList.add(value);
				}
			}
			Set<String> patternStringSet = new HashSet<String>();
			Pattern pattern = null;
			for (String value : valueList) {
				pattern = TextFlattener.createDigitStringMatchingPatternCapture(value);
				patternStringSet.add(pattern.toString());
			}
			if (patternStringSet.size() == 1 && valueList.size() > 0) {
				TextFlattener textFlattener = new TextFlattener();
				textFlattener.createIntegerPattern(valueList.get(0));
				LOG.trace("T "+textFlattener.getIntegerPattern().toString());
				for (String value : valueList) { 
					List<Integer> integerList = textFlattener.captureIntegers(value);
					LOG.trace("V "+value+" "+ integerList);
					IntListPattern extractedInteger = new IntListPattern(pattern, integerList);
					extractedIntegerList.add(extractedInteger);
				}
			}
		}
		for (IntListPattern extractedI : extractedIntegerList) {
			LOG.trace("Pattern: "+extractedI.toString());
		}
	}

	public void addHtmlElement(HtmlElement htmlElement, ChunkId chunkId) {
		ensureElementMultimaps();
		getHtmlElementByIdMap().put(chunkId, htmlElement);
	}

	public void outputHtmlElements() {
		Set<Map.Entry<ChunkId, HtmlElement>> entries= getHtmlElementByIdMap().entrySet();
		for (Map.Entry<ChunkId, HtmlElement> entry : entries) {
			HtmlElement htmlElement = entry.getValue();
			ChunkId chunkId = entry.getKey();
			pdfAnalyzer.outputElementAsHTML(htmlElement, chunkId);
		}
		// TODO Auto-generated method stub
		
	}

	public Map<ChunkId, HtmlElement> getHtmlElementByIdMap() {
		return htmlElementByIdMap;
	}

	public void setHtmlElementByIdMap(Map<ChunkId, HtmlElement> htmlElementByIdMap) {
		this.htmlElementByIdMap = htmlElementByIdMap;
	}

	public void addUsedId(ChunkId id) {
		ensureUsedIdSet();
		usedSet.add(id);
	}

	private void ensureUsedIdSet() {
		if (usedSet == null) {
			usedSet = new HashSet<ChunkId>();
		}
	}

}
