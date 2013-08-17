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
import org.xmlcml.svg2xml.action.SVGPlusConstantsX;
import org.xmlcml.svg2xml.util.NameComparator;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

import com.google.common.collect.Multimap;


public class PDFAnalyzer /*implements Annotatable */{

	private final static Logger LOG = Logger.getLogger(PDFAnalyzer.class);
	
	final static PrintStream SYSOUT = System.out;
	public static final String Z_CHUNK = "z_";
	
	private PDFAnalyzerIO pdfIo;
	private DocumentListAnalyzer documentListAnalyzer;
	PDFIndex pdfIndex;
	// created by analyzing pages
	private List<PageAnalyzer> pageAnalyzerList;
	private PDFAnalyzerOptions pdfOptions;

	public PDFAnalyzer() {
		pdfIo = new PDFAnalyzerIO(this);
		pdfOptions = new PDFAnalyzerOptions(this);
	}

	public PDFAnalyzer(DocumentListAnalyzer documentListAnalyzer) {
		this();
		this.documentListAnalyzer = documentListAnalyzer;
	}
	
	public void setSVGTopDir(File svgDir) {
		pdfIo.setSvgTopDir(svgDir);
	}
	
	public void setOutputTopDir(File outDir) {
		pdfIo.setOutputTopDir(outDir);
	}
	
	public void setFileRoot(String fileRoot) {
		pdfIo.setFileRoot(fileRoot);
	}
	
	private void analyzePDFs(String name) {
		if (name == null) {
			throw new RuntimeException("file/s must not be null");
		} else if (name.endsWith(SVGPlusConstantsX.DOT_PDF)) {
			if (name.startsWith(PDFAnalyzerIO.HTTP)) {
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
		pdfIo.setPDFURL(name);
		analyzePDF();
	}

	public void analyzePDFFile(File inFile) {
		pdfIo.setUpPDF(inFile);
		analyzePDF();
	}

	private void analyzePDF() {
		ensurePDFIndex();
		createSVGFilesfromPDF();
		analyzeRawSVGPagesWithPageAnalyzers();
		pdfIo.outputFiles(pdfOptions);
	}


	public void analyzeRawSVGPagesWithPageAnalyzers() {
		pageAnalyzerList = createAndFillPageAnalyzers();
		pdfIo.outputFiles(pdfOptions);
		createIndexesAndRemoveDuplicates();
		mergeTextContainers();
		createHtml();
		SYSOUT.println();
		writeSvgPages();
//		analyzeAndCreateHTML();  // not yet written
	}

	private void createHtml() {
		SYSOUT.println("createHtml not used...");
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

	private List<PageAnalyzer> createAndFillPageAnalyzers() {
		File rawSVGDirectory = pdfIo.getRawSVGPageDirectory();
		File[] rawSvgPageFiles =pdfIo.collectRawSVGFiles();
		ensurePageAnalyzerList();
		LOG.debug(rawSVGDirectory+" files: "+rawSvgPageFiles.length);
		for (int pageCounter = 0; pageCounter < rawSvgPageFiles.length; pageCounter++) {
			SYSOUT.print(pageCounter+"~");
			PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(rawSvgPageFiles[pageCounter], rawSVGDirectory, pageCounter);
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
			pageAnalyzer.writeRawSVGPageToFinalDirectory();
		}
	}

	public void createSVGFilesfromPDF() {
		LOG.trace("createSVG");
		PDF2SVGConverter converter = new PDF2SVGConverter();
		File inFile = pdfIo.getInFile();
		String inputName = pdfIo.getInputName();
		if (inFile != null && inFile.exists()) {
			createSVGFilesfromPDF(converter, inFile.toString());
		} else if (inputName != null && inputName.startsWith(PDFAnalyzerIO.HTTP)) {
			pdfIo.createHttpInputName(inputName);
			createSVGFilesfromPDF(converter, inputName);
		} else {
			throw new RuntimeException("no input file: "+inFile);
		}
	}

	private void createSVGFilesfromPDF(PDF2SVGConverter converter, String inputName) {
		File svgDocumentDir = pdfIo.getRawSVGDirectory();
		File[] files = (svgDocumentDir == null) ? null : svgDocumentDir.listFiles();
		if (!svgDocumentDir.exists() || files == null || files.length == 0) {
			svgDocumentDir.mkdirs();
			LOG.debug("running "+inputName+" to "+svgDocumentDir.toString());
			converter.run("-outdir", svgDocumentDir.toString(), inputName );
		} else {
			LOG.debug("Skipping SVG");
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
	
	PDFAnalyzerIO getPDFIO() {
		return pdfIo;
	}

	public void setRawSvgDirectory(File rawSvgDirectory) {
		pdfIo.setRawSvgDirectory(rawSvgDirectory);
	}

	public List<PageAnalyzer> getPageAnalyzerList() {
		return pageAnalyzerList;
	}
	
}
