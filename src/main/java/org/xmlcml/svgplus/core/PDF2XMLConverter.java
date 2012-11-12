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
public class PDF2XMLConverter {


	private static final String COMMAND_FILE = "-c";
	private static final String DOCUMENT_PREFIX = "-d.";
	private static final String INPUT_FILE = "-i";
	private static final String OUTPUT_FILE = "-o";
	private static final String PAGES = "-p";
	private static final String PAGE_PREFIX = "-p.";

	private final static Logger LOG = Logger.getLogger(PDF2XMLConverter.class);

	private List<SVGSVG> svgPageList;
	private Integer firstPageNumber;
	private Integer lastPageNumber;
	private String inputFilename;
	private String outputFilename;
	private Boolean removeImageData = true;
	private File rawSvgDir;
    private String semanticDocumentFilename;
	private File infile;
	private File outfile;
	private String startPage;
	private String endPage;
	private Integer startPageNumber;
	private Integer endPageNumber;
	private Map<String, String> variableMap;
    
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

	public PDF2XMLConverter() {
	}

	/** runs converter with args from commandline
	 * 
	 * @param args
	 */
	public PDF2XMLConverter(String[] args) {
		
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
		readSemanticDocumentFile();
		if (semanticDocumentAction != null) {
			semanticDocumentAction.setDocumentFilename(semanticDocumentFilename);
			getInputFileOrDirectory();
			semanticDocumentAction.setInfile(infile);
			getOutputFileOrDirectory();
			semanticDocumentAction.setOutfile(outfile);
			findPageNumbers();
			semanticDocumentAction.setPages(startPageNumber, endPageNumber);
			if (variableMap != null) {
				semanticDocumentAction.copy(variableMap);
			}
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
		}
	}

	private void getOutputFileOrDirectory() {
		if (outputFilename != null) {
			outfile = new File(outputFilename);
			if (outfile.isDirectory()) {
				LOG.debug("writing to: "+outfile.getAbsolutePath()+"(dir = "+infile.isDirectory()+")");
			}
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
			semanticDocumentFilename = SemanticDocumentElement.getDefaultCommandFilename();
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
		System.out.println("      -a                             // analyze SVG (e.g. create figures)");
		System.out.println("      -c                             // read and process commandfile (preferred)");
		System.out.println("      -i <input.dir or input.pdf or rawDir>    // foo.pdf, or foo directory");
		System.out.println("      -o <output.dir >               // overrides default output dir");
		System.out.println("      -p <firstPage> <lastPage>      // lastPage can be 9999");
		System.out.println("  ");
		System.out.println("  the normal use is to have a number of PDFs in a directory (alpha.pdf, blob.pdf)");
		System.out.println("  the first phase creates a directory for each (alpha/, blob/ ...");
		System.out.println("  then suddirectories are created alpha/raw and alpha/final");
		System.out.println("  each directory has page0.svg ... page99.svg");
		System.out.println("  if there are figures, final/ has final/figure1.svg ...figure99.svg");
		System.out.println("  if there are tables, final/ has final/table1.svg ...table99.svg");
		System.out.println("  variants:");
		System.out.println("      -i foo.pdf processes a single file as above");
		System.out.println("  ");
		System.out.println("  typical usage is:");
		System.out.println("      PDF2XMLConverter -i <pdfDir> -a");
		System.out.println("      or");
		System.out.println("      PDF2XMLConverter -i <rawDir> -a // generally only for developers");
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
				} else if (INPUT_FILE.equals(args[i])) {
					inputFilename = args[++i]; i++;
				} else if (OUTPUT_FILE.equals(args[i])) {
					outputFilename = args[++i]; i++;
				} else if (PAGES.equals(args[i])) {
					startPage = args[++i]; i++;
					if (i < args.length && !args[i].startsWith("-")) {
						endPage = args[i]; i++;
					}
				} else if (args[i].startsWith(PAGE_PREFIX) || args[i].startsWith(DOCUMENT_PREFIX)) {
					ensureVariableMap();
					// chop off minus
					variableMap.put(args[i].substring(1), args[++i]); i++;
				} else {
					System.err.println("unknown arg: "+args[i++]);
				}
			}
			try {
				readSemanticDocumentSetValuesAndRun();
			} catch (Exception e) {
				throw new RuntimeException("Cannot read / process PDF", e);
			}
		}
	}
	
	private void ensureVariableMap() {
		if (variableMap == null) {
			variableMap = new HashMap<String, String>();
		}
	}

	public static void main(String[] args) {
		PDF2XMLConverter converter = new PDF2XMLConverter();
		converter.run(args);
	}

}
