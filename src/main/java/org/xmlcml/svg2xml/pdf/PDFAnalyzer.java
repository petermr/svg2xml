package org.xmlcml.svg2xml.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.collection.DocumentListAnalyzer;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.PageIO;
import org.xmlcml.svg2xml.util.SVG2XMLConstantsX;

import com.google.common.collect.Multimap;

/** process a complete document.
 * 
 * may be called standalone or in an iteration from DocumentListAnalyzer
 * 
 * uses PDFAnalyzerIO as helper to manage the IO variables.
 * uses PDFAnalyzerOptions as helper to manage the processing options.
 * 
 * creates List<PageAnalyzer> as a result of processing all pages
 * 
 * intermediate results may be stored in directory created for each document.
 * collects conversion to HTML as  runningTextElement;
 * 
 * @author pm286
 *
 */

public class PDFAnalyzer {

	private final static Logger LOG = Logger.getLogger(PDFAnalyzer.class);
	
	final static PrintStream SYSOUT = System.out;
	public static final String Z_CHUNK = "z_";
	
	private PDFAnalyzerIO pdfIo;
	private DocumentListAnalyzer documentListAnalyzer;
	PDFIndex pdfIndex;
	// created by analyzing pages
	private List<PageAnalyzer> pageAnalyzerList;
	private PDFAnalyzerOptions pdfOptions;

	private HtmlElement runningTextElement;

	public PDFAnalyzer() {
		pdfIo = new PDFAnalyzerIO(this);
		setPdfOptions(new PDFAnalyzerOptions(this));
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
	
	/** a main entry routine
	 * 
	 * if name ends with ".pdf" then treat as single file else directory
	 * if name starts with "http://" treat as URL of single PDF file
	 * 
	 * first creates SVG, then analyzes them
	 * 
	 * @param name file or directory
	 */
	public void analyzePDFs(String name) {
		if (name == null) {
			throw new RuntimeException("file/s must not be null");
		} else if (name.endsWith(SVG2XMLConstantsX.DOT_PDF)) {
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
						} else if (line.endsWith(SVG2XMLConstantsX.DOT_PDF)) {
							readAndAnalyzeFile(parentFile, line);
						}
					}
				} catch (Exception e) {
					throw new RuntimeException("Cannot read listing file: "+file, e);
				}
			} else {
				File[] files = file.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(SVG2XMLConstantsX.DOT_PDF);
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
		if (!pdfIo.skipOutput(pdfOptions)) {
			analyzeRawSVGPagesWithPageAnalyzers();
		} else {
			LOG.debug("Skipped Output: "+pdfIo.outputDocumentDir);
		}
	}


	public void analyzeRawSVGPagesWithPageAnalyzers() {
		// this does not output anything 
		pageAnalyzerList = createAndFillPageAnalyzers();
		// this outputs files
		pdfIo.outputFiles(getPdfOptions());
		createIndexesAndRemoveDuplicates();
	}

	private void debugContainers() {
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
			LOG.debug("\n============== "+pageAnalyzer.toString());
		}
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
		List<File> rawSvgPageFiles = pdfIo.collectRawSVGFiles();
		ensurePageAnalyzerList();
		LOG.debug(rawSVGDirectory+" files: "+rawSvgPageFiles.size());
		for (int pageCounter = 0; pageCounter < rawSvgPageFiles.size(); pageCounter++) {
			SYSOUT.print(pageCounter+"~");
			PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(rawSvgPageFiles.get(pageCounter), rawSVGDirectory, pageCounter);
			pageAnalyzerList.add(pageAnalyzer);
		}
		return pageAnalyzerList;
	}
	
	public void createRunningHtml() {
		runningTextElement = new HtmlDiv();
		for (PageAnalyzer pageAnalyzer : pageAnalyzerList) {
			PageIO.copyChildElementsFromTo(pageAnalyzer.getRunningHtmlElement(), runningTextElement);
		}
	}

	private void ensurePageAnalyzerList() {
		if (pageAnalyzerList == null) {
			pageAnalyzerList = new ArrayList<PageAnalyzer>();
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
		return PageIO.DECIMAL_PLACES;
	}
	
	public PDFAnalyzerIO getPDFIO() {
		return pdfIo;
	}

	public void setRawSvgDirectory(File rawSvgDirectory) {
		pdfIo.setRawSvgDirectory(rawSvgDirectory);
	}

	public List<PageAnalyzer> getPageAnalyzerList() {
		return pageAnalyzerList;
	}

	public Element getRunningTextHtml() {
		return runningTextElement;
	}

	public PDFAnalyzerOptions getPdfOptions() {
		return pdfOptions;
	}

	public void setPdfOptions(PDFAnalyzerOptions pdfOptions) {
		this.pdfOptions = pdfOptions;
	}

	public boolean getOutputHtmlChunks() {
		return pdfOptions.outputHtmlChunks;
	}

	public boolean getOutputFigures() {
		return pdfOptions.outputRawFigureHtml;
	}

	public boolean getOutputFooters() {
		return pdfOptions.outputFooters;
	}

	public boolean getOutputHeaders() {
		return pdfOptions.outputHeaders;
	}

	public boolean getOutputTables() {
		return pdfOptions.outputRawTableHtml;
	}

	public void setSkipOutput(boolean b) {
		pdfOptions.skipOutput = b;
	}

}
