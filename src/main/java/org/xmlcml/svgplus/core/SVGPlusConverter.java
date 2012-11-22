package org.xmlcml.svgplus.core;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;

/**
 * Converts raw SVG to structured SVG
 * 
 * @author pm286
 *
 */
public class SVGPlusConverter {

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
	
	private String inputFilename;
	private String outputFilename;
    private String semanticDocumentFilename;
	private File infile;
	private File outfile = null;
	private String inputFormat = PDF;
    
	private SemanticDocumentElement semanticDocumentElement;
	private SemanticDocumentAction semanticDocumentAction;

	private FilenameFilter pdfFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(SVGPlusConstants.PDF);
		}
	};


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

	/** this is the main workflow
	 * 
	 * @throws Exception
	 */
	private void readSemanticDocumentSetValuesAndRun() throws Exception {
		LOG.trace("sem doc variables "+semanticDocumentAction.getVariableStore().size());
		for (String var : semanticDocumentAction.getVariableStore().keySet()) {
			LOG.trace("key: "+var);
		}
		semanticDocumentAction.setDocumentFilename(semanticDocumentFilename);
		createInputFileOrDirectoryName();
		createOutputFileOrDirectoryName();
		checkFiles();
		semanticDocumentAction.run();
	}

	private void checkFiles() {
		if (semanticDocumentFilename == null) {
			throw new RuntimeException("Must give commandFile");
		}
	}

	private void createInputFileOrDirectoryName() {
		if (inputFilename != null) {
			infile = new File(inputFilename);
			if (!infile.exists()) {
				throw new RuntimeException("input file does not exist: "+inputFilename);
			}
			LOG.debug("reading from: "+infile.getAbsolutePath()+"(dir = "+infile.isDirectory()+")");
			semanticDocumentAction.setInfile(infile);
		}
	}

	private void createOutputFileOrDirectoryName() {
		if (outputFilename != null) {
			outfile = new File(outputFilename);
			if (outfile.isDirectory()) {
				LOG.debug("writing to: "+outfile.getAbsolutePath()+"(dir = "+outfile.isDirectory()+")");
			}
			semanticDocumentAction.setOutfile(outfile);
		}
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
		semanticDocumentAction = (SemanticDocumentAction) semanticDocumentElement.getAction();
	}

	private void usage() {
		System.out.println("usage: org.xmlcml.svgplus.PDF2XMLConverter [args]");
		System.out.println("      -c                             // read and process commandfile (Mandatory)");
		System.out.println("      -i <input.dir or input.pdf or rawDir>    // foo.pdf, or foo directory");
		System.out.println("      -informat <input format>    // PDF or SVG (currently NYI)");
		System.out.println("      -o <output.dir >               // overrides default output dir");
		System.out.println("      -p <firstPage> <lastPage>      // lastPage can be 9999");
		System.out.println("      -<x>.<name>  <value>    // x is s,d,p for sem/doc/page, name alphanum]");
		System.out.println("         [e.g. -s.foo bar            // set $s.foo to value");
		System.out.println("  ");
		System.out.println("  the normal use is to have a number of PDFs in a directory .../foo (alpha.pdf, blob.pdf)");
		System.out.println("  the first phase creates a directory for each (.../foo/alpha/, .../foo/blob/ ...");
		System.out.println("  then raw svg is created by PDF2SVG. This is not normally written except for debug");
		System.out.println("  created by writePage or writeDocument");
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
			int i = 0;
			while (i < args.length) {
				if (args[i].trim().length() == 0) {
					i++;
					// skip any blanks
				} else if (COMMAND_FILE.equals(args[i])) {
					semanticDocumentFilename = args[++i]; i++;
					readSemanticDocumentFile();
				} else if (INPUT_FILE.equals(args[i])) {
					inputFilename = args[++i]; i++;
				} else if (INPUT_FORMAT.equals(args[i])) {
					inputFormat = args[++i]; i++;
				} else if (OUTPUT_FILE.equals(args[i])) {
					outputFilename = args[++i]; i++;
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
