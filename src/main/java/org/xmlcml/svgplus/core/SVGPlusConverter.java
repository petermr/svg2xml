package org.xmlcml.svgplus.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.control.SemanticDocumentAction;
import org.xmlcml.svgplus.control.SemanticDocumentElement;
import org.xmlcml.svgplus.util.PConstants;

/** not yet a JumboConverter (was called PDF2SVGConverter)
 * can be used for developing resources such as fonts
 * 
 * PDF2XMLConverter uses PDF2SVGReader to create raw SVGPages
 * it then can use DocumentAnalyzer to analyze them (e.g. using pageAnalyzer and hence pathAnalyzer, etc)
 * it can also write them out, etc.
 * It can then create specific XML from the document
 * 
 * an intermediate allows pages to be written out by PDF2SVGReader and saved as page0.svg, etc. This is mainly
 * for developers
 * 
 * @author pm286
 *
 */
public class SVGPlusConverter {


	public static final String S_INPUTFILE = "s.inputfile";
	public static final String S_OUTPUTFILE = "s.outputfile";
	public static final String S_SEMDOC = "s.semdoc";
	private static final String MISSING_COMMAND_FILE = "Must always give command file";
	private static final String COMMAND_FILE = "-c";
	private static final String DOCUMENT_PREFIX = "-d.";
	private static final String INPUT_FILE = "-i";
	private static final String INPUT_FORMAT = "-informat";
	private static final String OUTPUT_FILE = "-o";
	private static final String PAGES = "-p";
	private static final String PAGE_PREFIX = "-p.";
	private static final String PDF = "pdf";

	private final static Logger LOG = Logger.getLogger(SVGPlusConverter.class);
//	private static final File DEFAULT_OUTPUT = new File("target/");

	private List<SVGSVG> svgPageList;
	private Integer firstPageNumber;
	private Integer lastPageNumber;
	private String inputFilename;
	private String outputFilename;
    private String semanticDocumentFilename;
	private File infile;
//	private File outfile = DEFAULT_OUTPUT;
	private File outfile = null;
	private String inputFormat = PDF;
	private String startPage;
	private String endPage;
	private Integer startPageNumber;
	private Integer endPageNumber;
//	private Map<String, String> variableMap;
    
	private SemanticDocumentElement semanticDocumentElement;
	private SemanticDocumentAction semanticDocumentAction;

	private FilenameFilter pdfFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(PConstants.PDF);
		}
	};


	public List<SVGSVG> getSvgPageList() {
		return svgPageList;
	}

	public SVGPlusConverter() {
	}

	/** runs converter with args from commandline
	 * 
	 * @param args
	 */
	public SVGPlusConverter(String[] args) {
		
	}

	public String getSemanticDocumentFilename() {
		return semanticDocumentFilename;
	}

	public SemanticDocumentElement getSemanticDocumentElement() {
		return semanticDocumentElement;
	}

	public SemanticDocumentAction getSemanticDocumentAction() {
		return semanticDocumentAction;
	}

	public void readPDF(File file) throws IOException {
	}
	
	public void readPDF(InputStream is) throws IOException {
	}

	
	/** reads existing SVGPages (output by PDF2SVGReader
	 * 
	 * @param pageList
	 */
	public void readSVGPages(List<SVGSVG> pageList) {
		this.svgPageList = pageList;
	}

	/** this is the main workflow
	 * 
	 * @throws Exception
	 */
	private void readSemanticDocumentSetValuesAndRun() throws Exception {
		LOG.debug("sem doc variables "+semanticDocumentAction.getVariableMap().size());
		for (String var : semanticDocumentAction.getVariableMap().keySet()) {
			LOG.debug("key: "+var);
		}
		if (semanticDocumentAction != null) {
			semanticDocumentAction.setDocumentFilename(semanticDocumentFilename);
			getInputFileOrDirectory();
			semanticDocumentAction.setInfile(infile);
			getOutputFileOrDirectory();
			semanticDocumentAction.setOutfile(outfile);
			findPageNumbers();
			semanticDocumentAction.setPages(startPageNumber, endPageNumber);
			semanticDocumentAction.run();
		}
	}

	private void getInputFileOrDirectory() {
		if (inputFilename != null) {
			infile = new File(inputFilename);
			if (!infile.exists()) {
				throw new RuntimeException("input file does not exist: "+inputFilename);
			}
			LOG.debug("reading from: "+infile.getAbsolutePath()+"(dir = "+infile.isDirectory()+")");
			semanticDocumentAction.setVariable(S_INPUTFILE, inputFilename);
		}
	}

	private void getOutputFileOrDirectory() {
		if (outputFilename != null) {
			outfile = new File(outputFilename);
			if (outfile.isDirectory()) {
				LOG.debug("writing to: "+outfile.getAbsolutePath()+"(dir = "+infile.isDirectory()+")");
			}
			semanticDocumentAction.setVariable(S_OUTPUTFILE, outputFilename);
		}
	}

	private void findPageNumbers() {
		startPageNumber = parsePageNumber(startPage);
		endPageNumber = parsePageNumber(endPage);
	}

	private Integer parsePageNumber(String page) {
		Integer pageNumber = null;
		if (page != null) {
			try {
				pageNumber = new Integer(page);
			} catch (Exception e) {
				throw new RuntimeException("Cannot parse as page number: "+page);
			}
		}
		return pageNumber;
	}

	private void readSemanticDocumentFile() {
		if (semanticDocumentFilename == null) {
			throw new RuntimeException(MISSING_COMMAND_FILE);
		}
		File semanticDocumentFile = new File(semanticDocumentFilename);
		if (!semanticDocumentFile.exists()) {
			throw new RuntimeException("command file (semanticDocument) does not exist: "+semanticDocumentFilename+": "+new File(semanticDocumentFilename).getAbsolutePath());
		}
		semanticDocumentElement = SemanticDocumentElement.createSemanticDocument(semanticDocumentFile);
		semanticDocumentAction = semanticDocumentElement.getSemanticDocumentAction();
	}

	private void usage() {
		System.out.println("usage: org.xmlcml.svgplus.PDF2XMLConverter [args]");
		System.out.println("      -c                             // read and process commandfile (Mandatory)");
		System.out.println("      -i <input.dir or input.pdf or rawDir>    // foo.pdf, or foo directory");
		System.out.println("      -informat <input format>    // PDF or SVG (currently NYI)");
		System.out.println("      -o <output.dir >               // overrides default output dir");
		System.out.println("      -p <firstPage> <lastPage>      // lastPage can be 9999");
		System.out.println("  ");
		System.out.println("  the normal use is to have a number of PDFs in a directory (alpha.pdf, blob.pdf)");
		System.out.println("  the first phase creates a directory for each (alpha/, blob/ ...");
		System.out.println("  then raw svg is created by PDF2SVG. This is not normally written except for debug");
		System.out.println("  in which case it will be in ./raw/page1.svg ... pagen.svg");
		System.out.println("      -i foo.pdf processes a single file as above");
		System.out.println("  ");
		System.out.println("  typical usage is:");
		System.out.println("      PDF2XMLConverter -c <commandfile> -i <pdfDir> ");
		System.out.println("      or");
		System.out.println("      PDF2XMLConverter -c <commandfile> -i <rawDir> // generally only for developers");
	}

	public void run(String argString) {
		String args[] = argString.trim().length() == 0 ? new String[0] : argString.split(CMLConstants.S_WHITEREGEX);
		run(args);
	}

	public void run(String args[]) {
		if (args.length == 0) {
			usage();
		} else {
			inputFilename = null;
			outputFilename = null;
			startPage = null;
			endPage = null;
			int i = 0;
			while (i < args.length) {
				if (COMMAND_FILE.equals(args[i])) {
					semanticDocumentFilename = args[++i]; i++;
					readSemanticDocumentFile();
				} else if (INPUT_FILE.equals(args[i])) {
					inputFilename = args[++i]; i++;
				} else if (INPUT_FORMAT.equals(args[i])) {
					inputFormat = args[++i]; i++;
				} else if (OUTPUT_FILE.equals(args[i])) {
					outputFilename = args[++i]; i++;
				} else if (PAGES.equals(args[i])) {
					startPage = args[++i]; i++;
					if (i < args.length && !args[i].startsWith("-")) {
						endPage = args[i]; i++;
					}
				} else if (args[i].startsWith(PAGE_PREFIX) || args[i].startsWith(DOCUMENT_PREFIX)) {
					if (semanticDocumentAction == null) {
						throw new RuntimeException("commandfile must preceed variable setting in arguments");
					}
					// chop off minus
					String name = args[i].substring(1);
					String value = args[++i]; i++;
					semanticDocumentAction.setVariable(name, value);
				} else {
					System.err.println("unknown arg: "+args[i++]);
				}
			}
			if (semanticDocumentFilename == null) {
				throw new RuntimeException(MISSING_COMMAND_FILE);
			}
			try {
				readSemanticDocumentSetValuesAndRun();
			} catch (Exception e) {
				throw new RuntimeException("Cannot read / process input ("+inputFormat+")", e);
			}
		}
	}
	
	public static void main(String[] args) {
		SVGPlusConverter converter = new SVGPlusConverter();
		converter.run(args);
	}

}
