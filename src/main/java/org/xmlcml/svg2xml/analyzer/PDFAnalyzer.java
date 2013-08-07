package org.xmlcml.svg2xml.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlMenuSystem;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.action.PageEditorX;
import org.xmlcml.svg2xml.action.SVGPlusConstantsX;
import org.xmlcml.svg2xml.util.NameComparator;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

import com.google.common.collect.Multimap;


public class PDFAnalyzer /*implements Annotatable */{


	private static final File TARGET_DIR = new File("target");
	private static final File OUTPUT_DIR = new File(TARGET_DIR, "output");
	private static final File SVG_DIR = new File(TARGET_DIR, "svg");
	
	private final static Logger LOG = Logger.getLogger(PDFAnalyzer.class);
	
	private final static PrintStream SYSOUT = System.out;
	private static final String HTTP = "http";
	public static final String Z_CHUNK = "z_";
	private static final String DOT_PDF = ".pdf";
	
	private File inFile;
	private String inputName;
	String fileRoot;
	private File svgTopDir = SVG_DIR;
	File svgDocumentDir;
	private File outputTopDir = OUTPUT_DIR;
	File outputDocumentDir;

	private DocumentListAnalyzer documentListAnalyzer;
	PDFIndex pdfIndex;

//	HtmlEditor htmlEditor;
	
	private List<PageAnalyzer> pageAnalyzerList;

	public PDFAnalyzer() {
	}

	public PDFAnalyzer(DocumentListAnalyzer documentListAnalyzer) {
		this.documentListAnalyzer = documentListAnalyzer;
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
	
	private void analyzePDFs(String name) {
		if (name == null) {
			throw new RuntimeException("file/s must not be null");
		} else if (name.endsWith(SVGPlusConstantsX.DOT_PDF)) {
			if (name.startsWith(HTTP)) {
				this.analyzePDFURL(name);
			} else {
				this.analyzePDFFile(new File(name));
			}
		} else {
			File file = new File(name);
			this.readFilenamesAndAnalyzePDFs(file);
		}
	}

	/** read filenames from file
	 * 
	 * @param file
	 */
	private void readFilenamesAndAnalyzePDFs(File file) {
		if (file.exists()) {
			if (!file.isDirectory()) {
				File parentFile = file.getParentFile();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					while (true) {
						String line = br.readLine();
						if (line == null) {
							break;
						}
						if (line.startsWith("#")) {
							// comment
						} else if (line.endsWith(SVGPlusConstantsX.DOT_PDF)) {
							readAndAnalyzeFile(parentFile, line);
						}
					}
				} catch (Exception e) {
					throw new RuntimeException("Cannot read listing file: "+file, e);
				}
			} else {
				File[] files = file.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(SVGPlusConstantsX.DOT_PDF);
					}
				});
				if (files != null && files.length > 0) {
					for (File pdf : files) {
						createAnalyzerAndAnalyzePDF(pdf);
					}
				}
			}
		}
	}

	private void readAndAnalyzeFile(File parentDir, String filename) {
		File inFile = new File(parentDir, filename);
		if (!inFile.exists()) {
			LOG.error("PDF file does not exist: "+inFile);
		} else {
			createAnalyzerAndAnalyzePDF(inFile);
		}
	}

	private void createAnalyzerAndAnalyzePDF(File inFile) {
		try {
			PDFAnalyzer analyzer = new PDFAnalyzer();
			analyzer.analyzePDFFile(inFile);
		} catch (Exception e) {
			LOG.error("Cannot read file: "+inFile+" ("+e+")");
		}
	}

	private void analyzePDFURL(String name) {
		try {
			inputName = name;
			fileRoot = inputName.substring(0, inputName.length() - SVGPlusConstantsX.DOT_PDF.length());
			if (fileRoot.startsWith(HTTP)) {
				fileRoot = fileRoot.substring(fileRoot.indexOf("//")+2);
				fileRoot = fileRoot.substring(fileRoot.indexOf("/")+1);
				LOG.debug("fileroot "+fileRoot);
			}
			svgDocumentDir = new File(svgTopDir, fileRoot);
			LOG.debug("svgDocument "+svgDocumentDir);
			outputDocumentDir = new File(outputTopDir, fileRoot);
			outputDocumentDir.mkdirs();
			fileRoot = "";
			LOG.debug("outputDocument "+outputDocumentDir);
			mainAnalysis();
			File htmlDir = (new File(outputTopDir, fileRoot));
//			copyOriginalPDF(inFile, htmlDir);
			createHtmlMenuSystem(htmlDir);
		} catch (Exception e) {
			throw new RuntimeException ("URL failed", e);
		}
	}

	public void analyzePDFFile(File inFile) {
		this.inFile = inFile;
		inputName = inFile.getName();
		fileRoot = inputName.substring(0, inputName.length() - SVGPlusConstantsX.DOT_PDF.length());
		svgDocumentDir = new File(svgTopDir, fileRoot);
		outputDocumentDir = new File(outputTopDir, fileRoot);
		mainAnalysis();
		File htmlDir = (new File(outputTopDir, fileRoot));
		copyOriginalPDF(inFile, htmlDir);
		createHtmlMenuSystem(htmlDir);
	}

	private void copyOriginalPDF(File inFile, File htmlDir) {
		try {
			IOUtils.copy(new FileInputStream(inFile), new FileOutputStream(new File(htmlDir, "00_"+inputName)));
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}

	public  void mainAnalysis() {
		boolean summarize = true;
		ensurePDFIndex();
		createSVGFilesfromPDF();
		File[] svgPageFiles = svgDocumentDir.listFiles();
		LOG.debug("listing Files in: "+svgDocumentDir);
		if (svgPageFiles == null) {
			throw new RuntimeException("No files in "+svgDocumentDir);
		}
		pageAnalyzerList = iteratePagesAndCreateChunkAndScriptLists(svgPageFiles);
		if (summarize) summaryContainers();
		createIndexesAndRemoveDuplicates();
		mergeTextContainers();
		
		createHtml();
		SYSOUT.println();
		writeSvgPages();
//		analyzeAndCreateHTML();  // not yet written
	}

	private void summaryContainers() {
		int page = 1;
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
			SYSOUT.println("***************************************************"+page+">>>>>> \n");
			SYSOUT.println(pageAnalyzer.summaryString());
			SYSOUT.println("***************************************************"+page+"<<<<<< \n");
			page++;
		}
	}

	private void debugContainers() {
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
			LOG.debug("\n============== "+pageAnalyzer.toString());
		}
	}

	private void mergeTextContainers() {
		LOG.debug("mergeTextContainers NYI");
	}

	private void createIndexesAndRemoveDuplicates() {
		ensurePDFIndex();
		pdfIndex.ensureElementMultimaps();
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
			pdfIndex.addToindexes(pageAnalyzer);
		}
		pdfIndex.analyzeContainers();
		pdfIndex.createIndexes();
		pdfIndex.AnalyzeDuplicates();
		LOG.trace("IDS: "+pdfIndex.getUsedIdSet());
	}

	private List<PageAnalyzer> iteratePagesAndCreateChunkAndScriptLists(File[] svgPageFiles) {
		ensurePageAnalyzerList();
		for (int pageCounter = 0; pageCounter < svgPageFiles.length; pageCounter++) {
			SYSOUT.print(pageCounter+"~");
			PageAnalyzer pageAnalyzer = new PageAnalyzer(this, pageCounter);
			SVGSVG svgPage = pageAnalyzer.splitChunksAnnotateAndCreatePage();
			SVG2XMLUtil.writeToSVGFile(this.outputDocumentDir, "page"+pageCounter, svgPage, true);
			pageAnalyzerList.add(pageAnalyzer);
		}
		return pageAnalyzerList;
	}

	private void ensurePageAnalyzerList() {
		if (pageAnalyzerList == null) {
			pageAnalyzerList = new ArrayList<PageAnalyzer>();
		}
	}

	private void analyzeForLists(SVGG gChunk) {
		// TODO Auto-generated method stub
		
	}

	private void analyzeAndCreateHTML() {
		HtmlEditor htmlEditor = new HtmlEditor(this);
		htmlEditor.getHtmlAnalyzerListSortedByChunkId();
		htmlEditor.removeDuplicates();
		htmlEditor.createLinkedElementList();
		htmlEditor.mergeCaptions();
		htmlEditor.categorizeHtml();
		htmlEditor.analyzeTables();
		htmlEditor.analyzeFigures();
		htmlEditor.outputHtmlElements();
	}

	private void writeSvgPages() {
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
			pageAnalyzer.writeSVGPage(outputDocumentDir);
		}
	}

	public void createSVGFilesfromPDF() {
		LOG.trace("createSVG");
		PDF2SVGConverter converter = new PDF2SVGConverter();
		if (inFile != null && inFile.exists()) {
			createSVGFilesfromPDF(converter, inFile.toString());
		} else if (inputName != null && inputName.startsWith(HTTP)) {
			String inputName1 = inputName.substring(inputName.lastIndexOf("/")+1);
			if (inputName.toLowerCase().endsWith(DOT_PDF)) {
				inputName1 = inputName1.substring(0, inputName1.length()-DOT_PDF.length());
			}
			LOG.debug("filename: "+inputName1);
			createSVGFilesfromPDF(converter, inputName);
		} else {
			throw new RuntimeException("no input file: "+inFile);
		}
	}

	private void createSVGFilesfromPDF(PDF2SVGConverter converter, String inputName) {
		File[] files = (svgDocumentDir == null) ? null : svgDocumentDir.listFiles();
		if (!svgDocumentDir.exists() || files == null || files.length == 0) {
			svgDocumentDir.mkdirs();
			LOG.debug("running "+inputName+" to "+svgDocumentDir.toString());
			converter.run("-outdir", svgDocumentDir.toString(), inputName );
		} else {
			LOG.debug("Skipping SVG");
		}
	}

	private void createHtml() {
		int page = 1;
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
			SYSOUT.println("***************************************************"+page+">>>>>> \n");
			HtmlElement div = pageAnalyzer.createHtml();
			SYSOUT.println("***************************************************"+page+"<<<<<< \n");
			PageAnalyzer.cleanHtml(div);
			try {
				CMLUtil.debug(div, new FileOutputStream("target/page"+page+".html"), 0);
			} catch (Exception e) {
				throw new RuntimeException("cannot write html", e);
			}
			page++;
		}
	}

	private void createHtmlOld() {
//		ensureHtmlEditor();
		LOG.error("HTMLEditor NYI");
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
//			ChunkId chunkId = ChunkId.createChunkId(gChunk);
//			AbstractAnalyzer analyzerX = AbstractAnalyzer.createSpecificAnalyzer(gChunk);
//			HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer(htmlEditor, analyzerX);
//			HtmlElement htmlElement = htmlAnalyzer.createHtml();
//			if (htmlElement != null) {
//				htmlEditor.addHtmlElement(htmlElement, chunkId);
//				htmlEditor.indexHtmlBySvgId(htmlAnalyzer, chunkId);
//			} else {
//				LOG.warn("no html from: "+analyzerX);
//				if (analyzerX instanceof TextAnalyzerX) {
//					((TextAnalyzerX)analyzerX).debug();
//				}
//			}
		}
		// split this into
		// process textLine containers
		// split them
        // create new TextContainers
        // List<TextContainer> splitTextContainerList = getResultOfSplit()...
        // for (TextContainer splitTextContainer : splitTextContainerList) {
        //     textContainerList.add(splitTextContainer);
        // }
	}
	
	private void ensurePDFIndex() {
		if (pdfIndex == null) {
			pdfIndex = new PDFIndex(this);
		}
	}

	public static List<List<String>> findDuplicates(String title, Multimap<? extends Object, String> map) {
		List<List<String>> duplicateList = new ArrayList<List<String>>();
		for (Map.Entry<? extends Object, Collection<String>> mapEntry : map.asMap().entrySet()) {
			Object key = mapEntry.getKey();
			Collection<String> ids = mapEntry.getValue();
			List<String> idList = (Arrays.asList(ids.toArray(new String[0])));
			Collections.sort(idList);
			if (idList.size() > 1) {
				LOG.trace("DUPLICATES: "+title+" >"+key+"< "+idList);
				duplicateList.add(idList);
			}
		}
		return duplicateList;
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
				SYSOUT.println("PDFAnalyzer <inputFile(s)>");
				SYSOUT.println("mvn exec:java -Dexec.mainClass=\"org.xmlcml.svg2xml.analyzer.PDFAnalyzer\" " +
						" -Dexec.args=\"src/test/resources/pdfs/bmc/1471-2180-11-174.pdf\"");
				SYSOUT.println("OR java org.xmlcml.svg2xml.analyzer.PDFAnalyzer src/test/resources/pdfs/bmc/1471-2180-11-174.pdf");
				SYSOUT.println("");
				SYSOUT.println("input can be:");
				SYSOUT.println("    (a) single PDF file as above (must end with \".pdf\")");
				SYSOUT.println("    (b) directory containing one or more *.pdf");
				SYSOUT.println("    (c) list of *.pdf files (relative to '.' or absolute)");
				SYSOUT.println("    (d) URL (must start with http:// or https://) - NYI");
				System.exit(0);
			} else {
				PDFAnalyzer analyzer = new PDFAnalyzer();
				analyzer.analyzePDFs(args[0]); 
			}
		}

	public int getDecimalPlaces() {
		return PageEditorX.DECIMAL_PLACES;
	}

	public File getExistingOutputDocumentDir() {
		outputDocumentDir.mkdirs();
		return outputDocumentDir;
	}


}
