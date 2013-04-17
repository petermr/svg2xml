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
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Int2Range;
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
	private static final String FLATTENED = "flattened";
	public static final String IMAGE = "image";
	public static final String PATH = "path";
	
	private int duplicateBboxCount;
	private int duplicateContentCount;
	private int duplicateFlattenedCount;
	private int duplicateImageCount;
	private int duplicatePathCount;
	
	private Map<String, SVGElement> svgElementByIdMap;
	private Multimap<String, String> svgIdByContentMap;
	private Multimap<String, String> svgIdByFlattenedMap;
	private Multimap<String, String> svgIdByImageContentMap;
	private Multimap<String, String> svgIdByPathIdMap;
	private Map<String, HtmlElement> htmlElementByIdMap;

	private Multimap<Int2Range, String> bboxMap;

	private PDFAnalyzer pdfAnalyzer;

	private List<List<String>> contentIdListList;
	private List<List<String>> flattenedIdListList;
	private List<List<String>> imageIdListList;
	private List<List<String>> pathIdListList;
	private List<List<String>> bboxIdListList;

	private List<Set<String>> duplicateContentSet;
	private List<Set<String>> duplicateFlattenedSet;
	private List<Set<String>> duplicateImageSet;
	private List<Set<String>> duplicatePathSet;
	private List<Set<String>> duplicateBboxSet;
	
	public PDFIndex(PDFAnalyzer analyzer) {
		this.pdfAnalyzer = analyzer;
	}

	public static <T extends Object> List<List<String>> findDuplicates(String title, Multimap<T, String> map) {
		List<List<String>> duplicateList = new ArrayList<List<String>>();
		for (T key : map.keySet()) {
			Collection<String> ids = map.get(key);
			List<String> idList = (Arrays.asList(ids.toArray(new String[0])));
			Collections.sort(idList);
			if (idList.size() > 1) {
				LOG.debug("DUPLICATE: "+title+" >"+key+"< "+idList);
				duplicateList.add(idList);
			}
		}
		return duplicateList;
	}

	void ensureElementMultimaps() {
		if (svgIdByContentMap == null) {
			htmlElementByIdMap = new HashMap<String, HtmlElement>(); 
			svgElementByIdMap = new HashMap<String, SVGElement>(); 
			svgIdByContentMap = HashMultimap.create(); 
			svgIdByFlattenedMap = HashMultimap.create(); 
			svgIdByImageContentMap = HashMultimap.create();
			duplicateImageCount = 0;
			svgIdByPathIdMap = HashMultimap.create(); 
			duplicatePathCount = 0;
			bboxMap = HashMultimap.create(); 
			duplicateBboxCount = 0;
		}
	}

	public void findDuplicatesInIndexes() {
			contentIdListList = findDuplicates(CONTENT, svgIdByContentMap);
			duplicateContentSet = makeSet(contentIdListList);
			printDuplicates(CONTENT, contentIdListList);
			flattenedIdListList = findDuplicates(FLATTENED, svgIdByFlattenedMap);
			duplicateFlattenedSet = makeSet(flattenedIdListList);
			printDuplicates(CONTENT, flattenedIdListList);
			imageIdListList = findDuplicates(IMAGE, svgIdByImageContentMap);
			duplicateImageSet = makeSet(imageIdListList);
			printDuplicates(IMAGE, imageIdListList);
			pathIdListList = findDuplicates(PATH, svgIdByPathIdMap);
			duplicatePathSet = makeSet(pathIdListList);
			printDuplicates(PATH, pathIdListList);
			bboxIdListList = findDuplicates(BBOX, bboxMap);
			duplicateBboxSet = makeSet(bboxIdListList);
			printDuplicates(BBOX, bboxIdListList);
		}

	private List<Set<String>> makeSet(List<List<String>> idListList) {
		List<Set<String>> setList = new ArrayList<Set<String>>();
		for (List<String> idList : idListList) {
			Set<String> set = new HashSet<String>(idList);
			LOG.debug("set "+set.size());
			setList.add(set);
		}
		return setList;
	}

	void indexHtmlBySvgId(HtmlElement htmlElement, String chunkId) {
		ensureElementMultimaps();
		htmlElementByIdMap.put(chunkId, htmlElement);
	}

	private void printDuplicates(String title, List<List<String>> idListList) {
			if (idListList.size() > 0 ) {
				LOG.trace("duplicate "+title);
				for (List<String> idList : idListList) {
					if (title.equals(CONTENT)) {
	//				    output(idList, title);
						duplicateContentCount++;
					} else if (title.equals(FLATTENED)) {
						LOG.debug("flattened"+idList);
						output(idList, title);
						duplicateFlattenedCount++;
					} else if (title.equals(BBOX)) {
	//					output(idList,  title);
						Set<String> set = new HashSet<String>();
						for (String id : idList) {
							String s = htmlElementByIdMap.get(id).toXML();
							LOG.trace(s);
							set.add(s);
						}
						if (set.size() == 1) {
							LOG.debug("bbox "+set.toString());
						}
	//					LOG.debug("=====");
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
	public void setContentMap(Multimap<String, String> contentMap) {
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
	public void setImageMap(Multimap<String, String> imageMap) {
		this.svgIdByImageContentMap = imageMap;
	}

	/** preload a pathMap which can be used for several PDFAnalyzers
	 * Warning - may require a lot of memory
	 * @param pathMap
	 */
	public void setPathMap(Multimap<String, String> pathMap) {
		this.svgIdByPathIdMap = pathMap;
	}

	/** these are messy but canonicalising is far too slow
	 * 
	 * @param gOut
	 */
	void addToindexes(SVGG gOut) {
		String content = gOut.getValue();
		String id = gOut.getId();
		if (id == null) {
			System.out.println("missing ID");
			return;
		}
		svgElementByIdMap.put(id, gOut);
		Real2Range bbox = gOut.getBoundingBox();
		Int2Range i2r = new Int2Range(bbox);
		boolean added = bboxMap.put(i2r, id);
		if (content.trim().length() > 0) {
			svgIdByContentMap.put(content, id);
			String flattened = TextFlattener.flattenDigitStrings(content);
			LOG.trace(id+"> "+flattened);
			svgIdByFlattenedMap.put(flattened, id);
			LOG.trace(">> "+svgIdByFlattenedMap);
		} else {
			List<SVGImage> imageList = SVGImage.extractImages(SVGUtil.getQuerySVGElements(gOut, ".//svg:image"));
			if (imageList.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (SVGImage image : imageList) {
					sb.append(image.getImageValue());
				}
				svgIdByImageContentMap.put(sb.toString(), id);
			} else {
				List<SVGPath> pathList = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(gOut, ".//svg:path"));
				if (pathList.size() > 0) {
					StringBuilder sb = new StringBuilder();
					for (SVGPath path : pathList) {
						sb.append(path.getDString());
					}
					svgIdByPathIdMap.put(sb.toString(), id);
				}
			}
		}
	}

	/** may be obsolete
	 * 
	 * @param id
	 * @param serial
	 * @param title
	 */
	public void output(List<String> idList, String title) {
		
		try {
			for (String id : idList) {
				File dir = new File("target/"+title+"/duplicate/");
				dir.mkdirs();
				File file = new File(dir, id+SVGPlusConstantsX.SVG);
				SVGElement element = svgElementByIdMap.get(id);
				CMLUtil.debug(element, new FileOutputStream(file), 1);
				LOG.debug("wrote: "+file.getAbsolutePath());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** preload a bboxMap which can be used for several PDFAnalyzers
	 * @param bboxMap
	 */
	public void setBoundingBoxMap(Multimap<Int2Range, String> bboxMap) {
		this.bboxMap = bboxMap;
	}

	public void AnalyzeDuplicates() {
		List<IntListPattern> extractedIntegerList = new ArrayList<IntListPattern>();
		for (List<String> idList : flattenedIdListList) {
			List<String> valueList = new ArrayList<String>();
			for (String id : idList) {
				HtmlElement html = htmlElementByIdMap.get(id);
				String value = html.getValue();
				valueList.add(value);
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
				System.out.println("T "+textFlattener.getIntegerPattern().toString());
				for (String value : valueList) { 
					List<Integer> integerList = textFlattener.captureIntegers(value);
					System.out.println("V "+value+" "+ integerList);
					IntListPattern extractedInteger = new IntListPattern(pattern, integerList);
					extractedIntegerList.add(extractedInteger);
				}
			}
		}
		for (IntListPattern extractedI : extractedIntegerList) {
			LOG.debug("Pattern: "+extractedI.toString());
		}
	}

}
