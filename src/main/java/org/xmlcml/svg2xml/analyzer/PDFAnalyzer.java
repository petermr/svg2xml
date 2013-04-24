package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGTitle;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlLi;
import org.xmlcml.html.HtmlMenuSystem;
import org.xmlcml.html.HtmlUl;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.action.SVGPlusConstantsX;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.tools.Chunk;
import org.xmlcml.svg2xml.util.NameComparator;

import com.google.common.collect.Multimap;


public class PDFAnalyzer implements Annotatable {


	private final static Logger LOG = Logger.getLogger(PDFAnalyzer.class);

	private static final String SVG = SVGPlusConstantsX.SVG;
	private static final String PDF = SVGPlusConstantsX.PDF;

	public static final String PAGE = "page";

	private static final String BINOMIAL_REGEX_S = "[A-Z][a-z]*\\.?\\s+[a-z][a-z]+(\\s+[a-z]+)*";
	private String htmlRegexS = BINOMIAL_REGEX_S;
	private static final String ITALIC_XPATH_S = ".//*[local-name()='i']";
	private String htmlXPath = ITALIC_XPATH_S;

	private static final String HTML = SVGPlusConstantsX.HTML;

	private File inputTopDir;
	private File inFile;
	private String inputName;
	private String fileRoot;
	private File svgTopDir = new File("target/svg");
	private File svgDocumentDir;
	private File svgPageFile;
	private File outputTopDir = new File("target/output");;
	private File outputDocumentDir;
	private int pageNumber;
	private boolean skipFile;

	private DocumentListAnalyzer documentListAnalyzer;
	private PDFIndex pdfIndex;

	public PDFAnalyzer() {
	}

	public PDFAnalyzer(DocumentListAnalyzer documentListAnalyzer) {
		this.documentListAnalyzer = documentListAnalyzer;
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

		ensurePDFIndex();
		pdfIndex.ensureElementMultimaps();
		for (int page = 0; page < files.length; page++) {
			System.out.print(page+"~");
			createAndAnalyzeSVGChunks(page+1);
		}
		System.out.println();
		pdfIndex.createIndexes();
		pdfIndex.AnalyzeDuplicates();
		pdfIndex.outputHtmlElements();

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
		ensurePDFIndex();
		this.pageNumber = pageNumber;
		String pageRoot = PAGE+(pageNumber);
		String pageSvg = fileRoot+"-"+pageRoot+SVG;
		svgPageFile = new File(svgDocumentDir, pageSvg);
		if (svgPageFile.exists() && skipFile) {
			LOG.debug("Skipping: "+svgPageFile);
			return;
		}
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(svgPageFile);
//		stripNewlines(svg);
		processNonUnicodeCharacters(svg);
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
			SVGG gOrig = (SVGG) gList.get(ichunk);
			SVGG gOut = copyChunkAnalyzeMakeId(pageNumber, gOrig, ichunk);
			svgOut.appendChild(gOut);
			pdfIndex.addToindexes(gOut);
		}
		writeSVGPage(pageRoot, svgOut);
	}

	/**
	 * <title stroke="black" stroke-width="1.0">char: 981; name: null; f: Symbol; fn: PHHOAK+Symbol; e: Dictionary</title>
	 * @param svg
	 */
	private void processNonUnicodeCharacters(SVGSVG svg) {
		List<SVGElement> textTitles = SVGUtil.getQuerySVGElements(svg, ".//svg:title");
		for (SVGElement t : textTitles) {
			SVGTitle title = (SVGTitle) t;
			String s = title.getValue();
			String[] chunks =s.split(";");
			Integer ss = null;
			for (String chunk : chunks) {
				String[] sss = chunk.split(":");
				if (sss[0].equals("char") && !sss[1].equals("null")) {
					ss = new Integer(sss[1].trim());
//					System.out.println("TEXT: "+ss);
					break;
				}
				if (sss[0].equals("name") && !sss[1].equals("null")) {
//					ss = sss[1];
					ss = 127;
					break;
				}
			}
			SVGElement text = ((SVGElement)title.getParent());
			int cc =text.getChildCount();
			for (int i = 0; i < cc; i++) {
				text.getChild(0).detach();
			}
			char c =  (char)(int)ss;
			System.out.println("> "+c);
			text.appendChild(""+c);
//			text.debug("XX");
		}
	}

	private void stripNewlines(SVGSVG svg) {
		Nodes texts = svg.query("//text()");
		for (int i = 0; i < texts.size(); i++) {
			Text text = (Text) texts.get(i);
			String value = text.getValue();
			if (value.contains("\n")) {
				value = value.replaceAll("\n", "");
				text.setValue(value);
			}
		}
	}

	private void ensurePDFIndex() {
		if (pdfIndex == null) {
			pdfIndex = new PDFIndex(this);
		}
	}

	private void writeSVGPage(String pageRoot, SVGSVG svgOut) {
		try {
			outputDocumentDir.mkdirs();
			CMLUtil.debug(
				svgOut, new FileOutputStream(new File(outputDocumentDir, pageRoot+SVG)), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private SVGG copyChunkAnalyzeMakeId(int pageNumber, SVGG gOrig, int ichunk) {
		ChunkId chunkId = new ChunkId(pageNumber, ichunk);
		SVGG gOut = analyzeChunkInSVGPage(gOrig, chunkId);
		CMLUtil.copyAttributes(gOrig, gOut);
		return gOut;
	}

	public static List<List<String>> findDuplicates(String title, Multimap<? extends Object, String> map) {
		List<List<String>> duplicateList = new ArrayList<List<String>>();
		for (Map.Entry<? extends Object, Collection<String>> mapEntry : map.asMap().entrySet()) {
			Object key = mapEntry.getKey();
			Collection<String> ids = mapEntry.getValue();
			List<String> idList = (Arrays.asList(ids.toArray(new String[0])));
			Collections.sort(idList);
			if (idList.size() > 1) {
				LOG.debug("DUPLICATES: "+title+" >"+key+"< "+idList);
				duplicateList.add(idList);
			}
		}
		return duplicateList;
	}
		

	public SVGG labelChunk() {
		// might iterate through pages
		throw new RuntimeException("NYI");
	}
	
	public SVGG analyzeChunkInSVGPage(SVGElement chunkSvg, ChunkId chunkId) {
		SVGG gOut = null;
		AbstractPageAnalyzerX analyzerX = AbstractPageAnalyzerX.getAnalyzer(chunkSvg);
		TextAnalyzerX textAnalyzer = null;
		String message = null;
		HtmlElement htmlElement = new HtmlDiv();
		gOut = analyzerX.labelChunk();
		gOut.setId(chunkId.toString());
		htmlElement = analyzerX.createHTML();
		if (htmlElement != null) {
			pdfIndex.addHtmlElement(htmlElement, chunkId);
			pdfIndex.indexHtmlBySvgId(htmlElement, chunkId);
		} else {
			LOG.warn("no html from: "+analyzerX);
			if (analyzerX instanceof TextAnalyzerX) {
				((TextAnalyzerX)analyzerX).debug();
			}
		}
		return gOut;
	}

	private void createHtmlMenuSystem(File dir) {
		HtmlMenuSystem menuSystem = new HtmlMenuSystem();
		menuSystem.setOutdir(dir.toString());
		File[] filesh = dir.listFiles();
		Arrays.sort(filesh, new NameComparator());
		for (File filex : filesh) {
			menuSystem.addHRef(filex.toString());
		}
		try {
			menuSystem.outputMenuAndBottomAndIndexFrame();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void outputElementAsHTML(Element element, ChunkId chunkId) {
		String chunkFileRoot = PAGE+chunkId.getPageNumber()+"-"+chunkId.getChunkNumber();
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

	public PDFIndex getIndex() {
		ensurePDFIndex();
		return pdfIndex;
	}

	/**
	mvn exec:java -Dexec.mainClass="org.xmlcml.svg2xml.analyzer.PDFAnalyzer" 
	    -Dexec.args="src/test/resources/pdfs/bmc"
		 * @param args
		 */
		public static void main(String[] args) {
			if (args.length == 0) {
				System.out.println("PDFAnalyzer <directory>");
				System.out.println("mvn exec:java -Dexec.mainClass=\"org.xmlcml.svg2xml.analyzer.PDFAnalyzer\" " +
						" -Dexec.args=\"src/test/resources/pdfs/bmc/1471-2180-11-174.pdf\"");
				System.out.println("OR java org.xmlcml.svg2xml.analyzer.PDFAnalyzer src/test/resources/pdfs/bmc/1471-2180-11-174.pdf");
				System.exit(0);
			} else {
				PDFAnalyzer analyzer = new PDFAnalyzer();
				analyzer.analyzePDFFile(new File(args[0]));
			}
		}

}
