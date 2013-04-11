package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlLi;
import org.xmlcml.html.HtmlMenuSystem;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlUl;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.action.SVGPlusConstantsX;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.text.TextLine;
import org.xmlcml.svg2xml.tools.Chunk;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PDFAnalyzer implements Annotatable {


	private final static Logger LOG = Logger.getLogger(PDFAnalyzer.class);

	public static final String IMAGE = "image";
	public static final String CONTENT = "content";
	public static final String PATH = "path";
	public static final String PAGE = "page";

	private static final String SVG = SVGPlusConstantsX.SVG;
	private static final String HTML = SVGPlusConstantsX.HTML;
	private static final String PDF = SVGPlusConstantsX.PDF;

	private static final String BINOMIAL_REGEX_S = "[A-Z][a-z]*\\.?\\s+[a-z][a-z]+(\\s+[a-z]+)*";
	private String htmlRegexS = BINOMIAL_REGEX_S;
	private static final String ITALIC_XPATH_S = ".//*[local-name()='i']";
	private String htmlXPath = ITALIC_XPATH_S;
	
	private File inputTopDir;
	private File inFile;
	private String inputName;
	private String fileRoot;
	private File svgTopDir;
	private File svgDocumentDir;
	private File svgPageFile;
	private File outputTopDir;
	private File outputDocumentDir;
	private int pageNumber;
	private boolean skipFile;

	private String chunkFileRoot;

	private Set<String> canonicalXMLSet;

	private Multimap<String, SVGElement> contentMap;
	private Multimap<String, SVGElement> imageMap;
	private Multimap<String, SVGElement> pathMap;
	private int duplicateImageCount;
	private int duplicatePathCount;
	
	public PDFAnalyzer() {
		
	}

	public void setInputTopDir(File inDir) {
		this.inputTopDir = inDir;
	}
	
	public void setSVGTopDir(File svgDir) {
		this.svgTopDir = svgDir;
	}
	
	public void setOutputTopDir(File outDir) {
		this.outputTopDir = outDir;
	}
	
	public void setFileRoot(String fileRoot) {
		this.fileRoot = fileRoot;
	}
	
	public void setSkipFile(boolean skipFile) {
		this.skipFile = skipFile;
	}
	
	public void setHtmlRegex(String htmlRegexS) {
		this.htmlRegexS = htmlRegexS;
	}
	
	public void setHtmlXPath(String htmlXPath) {
		this.htmlXPath = htmlXPath;
	}
	
	public void analyzePDFFile(File inFile) {
		this.inFile = inFile;
		inputName = inFile.getName();
		fileRoot = inputName.substring(0, inputName.length() - PDF.length());
		svgDocumentDir = new File(svgTopDir, fileRoot);
		outputDocumentDir = new File(outputTopDir, fileRoot);
		analyzePDF();
		File htmlDir = (new File(outputTopDir, fileRoot));
		try {
			IOUtils.copy(new FileInputStream(inFile), new FileOutputStream(new File(htmlDir, "00_"+inputName)));
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
		List<File> htmlFiles = analyzeHtml(htmlDir);
		HtmlElement speciesList = searchHtml(htmlFiles, htmlXPath, htmlRegexS);
		try {
			CMLUtil.debug(speciesList, new FileOutputStream(new File(outputDocumentDir, "species.html")), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		createHtmlMenuSystem(htmlDir);
	}

	private HtmlElement searchHtml(List<File> htmlFiles, String xpath, String regex) {
		HtmlUl ul = new HtmlUl();
		Set<String> binomialSet = new HashSet<String>();
		Pattern htmlPattern = Pattern.compile(htmlRegexS);
		for (File file : htmlFiles) {
			Element html = null;
			try {
				html = new Builder().build(file).getRootElement();
			} catch (Exception e) {
				LOG.error("Failed on html File: "+file);
			}
			if (html != null) {
				Nodes nodes = html.query(xpath);
				for (int i = 0; i < nodes.size(); i++) {
					String value = nodes.get(i).getValue();
					if (htmlPattern.matcher(value).matches()) {	
						if (!binomialSet.contains(value)) {
							LOG.trace(value);
							HtmlLi li = new HtmlLi();
							ul.appendChild(li);
							li.setValue(value);
							binomialSet.add(value);
						}
					}
				}
			}
		}
		return ul;
	}

	private List<File> analyzeHtml(File htmlDir) {
		List<File> htmlFileList = new ArrayList<File>();
		File[] files = htmlDir.listFiles();
		for (File file : files) {
			if (file.toString().endsWith(".html")) {
				htmlFileList.add(file);
			}
		}
		return htmlFileList;
	}

	public  void analyzePDF() {
		createSVGfromPDF();
		File[] files = svgDocumentDir.listFiles();
		LOG.debug("listing Files in: "+svgDocumentDir);
		if (files == null) {
			throw new RuntimeException("No files in "+svgDocumentDir);
		}

		ensureElementMultimaps();
		for (int page = 0; page < files.length; page++) {
			System.out.print(page+"=");
			createAndAnalyzeSVGChunks(page+1);
		}
		System.out.println();
		findDuplicatesInIndexes();
	}

	private void ensureElementMultimaps() {
		if (contentMap == null) {
			contentMap = HashMultimap.create(); 
			imageMap = HashMultimap.create();
			duplicateImageCount = 0;
			pathMap = HashMultimap.create(); 
			duplicatePathCount = 0;
		}
	}

	public void createSVGfromPDF() {
		LOG.trace("createSVG");
		PDF2SVGConverter converter = new PDF2SVGConverter();
		if (!inFile.exists()) {
			throw new RuntimeException("no input file: "+inFile);
		}
//		File svgDocumentDir = new File(svgTopDir, fileRoot);
		boolean exists = svgDocumentDir.exists();
		File[] files = (svgDocumentDir == null) ? null : svgDocumentDir.listFiles();
		if (!exists || files == null || files.length == 0) {
			svgDocumentDir.mkdirs();
			LOG.debug("running "+inFile.toString()+" to "+svgDocumentDir.toString());
			converter.run("-outdir", svgDocumentDir.toString(), inFile.toString() );
		} else {
			LOG.debug("Skipping SVG");
		}
	}

	private void createAndAnalyzeSVGChunks(int pageNumber) {
		this.pageNumber = pageNumber;
		String pageRoot = PAGE+(pageNumber);
		String pageSvg = fileRoot+"-"+pageRoot+SVG;
		svgPageFile = new File(svgDocumentDir, pageSvg);
		if (svgPageFile.exists() && skipFile) {
			LOG.debug("Skipping: "+svgPageFile);
			return;
		}
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(svgPageFile);
		SemanticDocumentActionX semanticDocumentAction = 
				SemanticDocumentActionX.createSemanticDocumentActionWithSVGPage(svg);
		List<Chunk> chunkList = 
				WhitespaceChunkerAnalyzerX.chunkCreateWhitespaceChunkList(semanticDocumentAction);
		WhitespaceChunkerAnalyzerX.drawBoxes(chunkList, "red", "yellow", 0.5);
		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
		SVGSVG svgOut = new SVGSVG();
		svgOut.setWidth(600.0);
		svgOut.setHeight(800.0);
		String pageId = "p."+pageNumber;
		svgOut.setId(pageId);
		for (int ichunk = 0; ichunk < gList.size(); ichunk++) {
			String chunkId = "g."+pageNumber+"."+ichunk;
			chunkFileRoot = PAGE+pageNumber+"-"+ichunk;
			SVGG gOut = analyzeChunkInSVGPage(gList.get(ichunk));
			gOut.setId(chunkId);
			addToindexes(gOut);
			svgOut.appendChild(gOut);
		}
		try {
			CMLUtil.debug(
				svgOut, new FileOutputStream(new File(outputDocumentDir, pageRoot+SVG)), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** these are messy but canonicalising is far too slow
	 * 
	 * @param gOut
	 */
	private void addToindexes(SVGG gOut) {
		String content = gOut.getValue();
		if (content.trim().length() > 0) {
			contentMap.put(content, gOut);
		} else {
			List<SVGImage> imageList = SVGImage.extractImages(SVGUtil.getQuerySVGElements(gOut, ".//svg:image"));
			if (imageList.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (SVGImage image : imageList) {
					sb.append(image.getImageValue());
				}
				imageMap.put(sb.toString(), gOut);
			} else {
				List<SVGPath> pathList = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(gOut, ".//svg:path"));
				if (pathList.size() > 0) {
					StringBuilder sb = new StringBuilder();
					for (SVGPath path : pathList) {
						sb.append(path.getDString());
					}
					pathMap.put(sb.toString(), gOut);
				}
			}
		}
	}
	
	public void findDuplicatesInIndexes() {
		List<List<SVGElement>> elementListList = findDuplicates(CONTENT, contentMap);
		printDuplicates(CONTENT, elementListList);
		elementListList = findDuplicates(IMAGE, imageMap);
		printDuplicates(IMAGE, elementListList);
		elementListList = findDuplicates(PATH, pathMap);
		printDuplicates(PATH, elementListList);
	}

	private void printDuplicates(String title, List<List<SVGElement>> elementListList) {
		if (elementListList.size() > 0 ) {
			LOG.trace("duplicate "+title);
			for (List<SVGElement> elementList : elementListList) {
				SVGElement firstElement = elementList.get(0);
				if (title.equals(CONTENT)) {
					String content = firstElement.getValue();
					LOG.trace(elementList.size()+": "+content.substring(0, Math.min(100, content.length())));
				} else if (title.equals(IMAGE)) {
					output(firstElement, duplicateImageCount, title);
					duplicateImageCount++;
				} else if (title.equals(PATH)) {
					output(firstElement, duplicatePathCount, title);
					duplicatePathCount++;
				}
			}
		}
	}

	public static void output(SVGElement element, int serial, String title) {
		try {
			String filename = "target/"+title+"/duplicate"+serial+SVGPlusConstantsX.SVG;
			CMLUtil.debug(element, new FileOutputStream(filename), 1);
			LOG.debug("wrote: "+filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<List<SVGElement>> findDuplicates(String title, Multimap<String, SVGElement> map) {
		Set<String> keySet = map.keySet();
		List<List<SVGElement>> duplicateList = new ArrayList<List<SVGElement>>();
		for (String key : keySet) {
			Collection<SVGElement> svgElements = map.get(key);
			List<SVGElement> svgElementList = (Arrays.asList(svgElements.toArray(new SVGElement[0])));		
			if (svgElementList.size() > 1) {
				LOG.trace("DUPLICATE: "+title);
				duplicateList.add(svgElementList);
			}
		}
		return duplicateList;
	}

	public SVGG annotate() {
		// might iterate through pages
		throw new RuntimeException("NYI");
	}
	
	public SVGG analyzeChunkInSVGPage(SVGElement chunkSvg) {
		SVGG gOut = null;
		AbstractPageAnalyzerX analyzerX = AbstractPageAnalyzerX.getAnalyzer(chunkSvg);
		TextAnalyzerX textAnalyzer = null;
		String message = null;
		if (analyzerX instanceof ImageAnalyzerX) {
			AbstractPageAnalyzerX imageAnalyzer = (AbstractPageAnalyzerX) analyzerX;
			gOut = imageAnalyzer.annotate();
		} else if (analyzerX instanceof MixedAnalyzer) {
			MixedAnalyzer mixedAnalyzer = (MixedAnalyzer) analyzerX;
			gOut = mixedAnalyzer.annotate();
		} else if (analyzerX instanceof PathAnalyzerX) {
			AbstractPageAnalyzerX pathAnalyzer = (AbstractPageAnalyzerX) analyzerX;
			gOut = pathAnalyzer.annotate();
		} else if (analyzerX instanceof TextAnalyzerX) {
			textAnalyzer = (TextAnalyzerX) analyzerX;
			gOut = textAnalyzer.annotate();
		} else {
			throw new RuntimeException("Unknown analyzer "+analyzerX);
		}
		Element element = null;
		if (textAnalyzer != null) {
			element = this.createHTMLParasAndDivs(inputName, textAnalyzer);
		} else {
			element = createHTMLMessage(gOut);
		}
		outputElementAsHTML(element);
		return gOut;
	}


//	private SVGG createSVGText(String message, Real2Range bbox) {
//		return AbstractPageAnalyzerX.createAnnotationDetails("brown", 0.2, bbox, message, 10.0);
//	}

	private Element createHTMLMessage(SVGG gOut) {
		String message = gOut.getValue(); // crude
		HtmlDiv div = new HtmlDiv();
		HtmlP p = new HtmlP();
		div.appendChild(p);
		p.appendChild(message);
		return div;
	}

	private void createHtmlMenuSystem(File dir) {
		HtmlMenuSystem menuSystem = new HtmlMenuSystem();
		menuSystem.setOutdir(dir.toString());
		File[] filesh = dir.listFiles();
		for (File filex : filesh) {
			menuSystem.addHRef(filex.toString());
		}
		try {
			menuSystem.outputMenuAndBottomAndIndexFrame();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Element createHTMLParasAndDivs(String name, TextAnalyzerX textAnalyzer) {
		LOG.trace("createHTMLParasAndDivs");
		List<TextLine> textLines = textAnalyzer.getLinesInIncreasingY();
		LOG.trace("lines "+textLines.size());
		for (TextLine textLine : textLines){
			LOG.trace(">> "+textLine);
		}
		Element element = textAnalyzer.createHtmlDivWithParas();
		if (element != null) {
			AbstractPageAnalyzerX.tidyStyles(element);
		}
		return element;
	}

	private void outputElementAsHTML(Element element) {
		try {
			outputDocumentDir.mkdirs();
			File outfile = new File(outputDocumentDir, chunkFileRoot+HTML);
			LOG.trace("writing "+outfile);
			OutputStream os = new FileOutputStream(outfile);
			CMLUtil.debug(element, os, 1);
			os.close();
		} catch (Exception e) {
			throw new RuntimeException("cannot write HTML: "+e);
		}
	}

	public void setContentMap(Multimap<String, SVGElement> contentMap) {
		this.contentMap = contentMap;
	}

	public void setImageMap(Multimap<String, SVGElement> imageMap) {
		this.imageMap = imageMap;
	}

	public void setPathMap(Multimap<String, SVGElement> pathMap) {
		this.pathMap = pathMap;
	}

	public void setDuplicateImageCount(int duplicateImageCount) {
		this.duplicateImageCount = duplicateImageCount;
	}

	public void setDuplicatePathCount(int duplicatePathCount) {
		this.duplicatePathCount = duplicatePathCount;
	}

}
