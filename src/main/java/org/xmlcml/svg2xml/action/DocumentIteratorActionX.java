package org.xmlcml.svg2xml.action;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.pdf2svg.util.MenuSystem;

public class DocumentIteratorActionX extends DocumentActionX {

	private static final String INPUT_DIR = SVGPlusConstantsX.D_DOT+SVGPlusConstantsX.ROOT_DIR;
	static final String INPUT_FILE = SVGPlusConstantsX.D_DOT+SVGPlusConstantsX.INPUT_FILE;
	private static final String OUTPUT_DIR = SVGPlusConstantsX.D_DOT+SVGPlusConstantsX.OUTPUT_DIR;

	private final static Logger LOG = Logger.getLogger(DocumentIteratorActionX.class);


	private String format;
	private String regex;
	private Integer max;

	private File infile;
	private File outfile;

	private List<String> skipList;
	private List<File> infileList;

	private File outdir;
	
	public DocumentIteratorActionX(AbstractActionX documentActionElement) {
		super(documentActionElement);
	}
	
	public final static String TAG ="documentIterator";

	private static final List<String> ATTNAMES = new ArrayList<String>();
	static {
		ATTNAMES.add(FILENAME);
		ATTNAMES.add(OUTFILE);
		ATTNAMES.add(FORMAT);
		ATTNAMES.add(MAX);
		ATTNAMES.add(REGEX);
		ATTNAMES.add(SKIP_IF_EXISTS);
	}

	/** constructor
	 */
	public DocumentIteratorActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentIteratorActionX(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
//				AbstractActionElement.FILENAME,
		});
	}
	
	@Override
	public void run() {
		LOG.debug("executing: \n"+this.getString());
		infile = getInfile();
		outfile = getOutfile();
		format = getFormat();
		regex = getRegex();
		if (infile == null) {
			LOG.debug(" *** No input file given ***");
		} else if (!infile.exists()) {
			throw new RuntimeException("*** Input file does not exist: "+infile.getAbsolutePath()+" ***");
		} else {
			createInputFileList();
			createOutputDirectory();
			processInputFileList();
		}
	}

	private void createInputFileList() {
		infileList = new ArrayList<File>();
		max = getInteger(AbstractActionX.MAX);
		File indir = null;
		if (!infile.isDirectory()) {
			String name = infile.getName();
			infileList.add(infile);
			indir = infile.getParentFile();
		} else if (infile.isDirectory()) {
			indir = infile;
			List<File> files = getFiles(infile);
			infileList.addAll(files);
		}
		semanticDocumentActionX.setVariable(INPUT_DIR, indir);
	}
	
	private void createOutputDirectory() {
		// outfile may have been set by commandline
		if (outfile == null) {
			if (infile != null) {
				outdir = (infile.isDirectory()) ? infile : infile.getParentFile();
			}
		} else {
			outfile.mkdirs();
			outdir = (outfile.isDirectory()) ? outfile : outfile.getParentFile();
		}
	}

	private void processInputFileList() {
		for (File infile : infileList) {
			LOG.debug("file "+infile);
			semanticDocumentActionX.setVariable(INPUT_FILE, infile);
			outdir = outdir != null ? outdir : infile.getParentFile();
			outdir.mkdirs();
			semanticDocumentActionX.setVariable(OUTPUT_DIR, outdir);
			runChildActionList(); // will normally include PageIterator
		}
	}

	private List<File> getFiles(File indir) {
		File[] files = indir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String path) {
				return path.endsWith(SVGPlusConstantsX.PDF); // for test
			}
		});
		return files == null ? new ArrayList<File>() : Arrays.asList(files);
	}

	private File getInfile() {
		infile = (File) semanticDocumentActionX.getVariable(SemanticDocumentActionX.S_INFILE);
		if (infile == null) {
			String filename = getFilename();
			infile = (filename == null) ? null : new File(filename);
		}
		return infile;
	}

	private File getOutfile() {
		// variable set takes precedence
		outfile = (File) semanticDocumentActionX.getVariable(SemanticDocumentActionX.S_OUTFILE);
		if (outfile == null) {
			String outfilename = this.getAndExpand(OUTFILE);
			if (outfilename != null) {
				outfile = new File(outfilename);
				semanticDocumentActionX.setVariable(SemanticDocumentActionX.S_OUTFILE, outfile);
			}
		}
		return outfile;
	}

	private List<File> filterByRegex(List<File> rawDirList) {
		if (regex != null) {
			List<File> dirList = new ArrayList<File>();
			for (File rawDir : rawDirList) {
				if (rawDir.getAbsolutePath().matches(regex)) {
					dirList.add(rawDir);
				}
			}
			rawDirList = dirList;
		}
		return rawDirList;
	}

	private List<File> filterByCount(List<File> rawDirList) {
		if (max != null) {
			List<File> dirList = new ArrayList<File>();
			int i = 0;
			for (File rawDir : rawDirList) {
				if (i++ >= max) {
					break;
				}
				dirList.add(rawDir);
			}
			rawDirList = dirList;
		}
		return rawDirList;
	}

	private void createHtmlMenuDisplay() {
//		LOG.debug("FILES "+rawDirList.size());
		MenuSystem menuSystem = new MenuSystem(infile);
		menuSystem.setRoot("/../"+SVGPlusConstantsX.OUT+CMLConstants.S_SLASH+SVGPlusConstantsX.INDEX_HTML);
		menuSystem.setLabel("../../");
		menuSystem.setRowWidth(100);
		menuSystem.setAddPdf(false);
//		menuSystem.writeDisplayFiles(rawDirList, "_dir");

	}

	private File[] listSVGFiles(File rawDir) {
		File[] svgFiles = rawDir.listFiles(new FilenameFilter() {
			public boolean accept(File rawDir, String name) {
				return name.endsWith(SVGPlusConstantsX.SVG);
			}
		});
		return svgFiles;
	}


	private void outputSVGsToRawDir(File rawDir, List<SVGSVG> pageList) {
		int page = 0;
		for (SVGSVG svgPage :pageList) {
			CMLUtil.outputQuietly(svgPage, new File(rawDir, SVGPlusConstantsX.PAGE+(++page)+SVGPlusConstantsX.SVG), 1);
		}
	}

	private List<File> createRawDirsAndGenerateSVGs(File file) {
		File[] pdfFiles = listPDFFiles(file);
		LOG.trace("PDFs "+((pdfFiles == null) ? "NULL" : Arrays.asList(pdfFiles)));
		LOG.trace("PDFs "+((pdfFiles == null) ? "NULL" : pdfFiles.length));
		List<File> rawDirList = null;
		if (pdfFiles != null) {
			rawDirList = new ArrayList<File>();
			// files of form: a/b/foo.pdf => a/b/foo/raw/*.svg
			for (File pdfFile : pdfFiles) {
//				File rawDir = generateRawDirAndGenerateSVGsElseSkip(pdfFile);
//				LOG.trace("created raw "+rawDir);
//				rawDirList.add(rawDir);
			}
		}
		return rawDirList;
	}

	private File generateRawFileDirectory(File pdfFile) {
		String name = pdfFile.getName();
		String root = name.substring(0, name.length()-SVGPlusConstantsX.PDF.length());
		File subRootDir = new File(pdfFile.getParentFile(), root);
		subRootDir.mkdir();
		File rawDir = new File(subRootDir, SVGPlusConstantsX.RAW);
		rawDir.mkdir();
		return rawDir;
	}

	private File[] listPDFFiles(File file) {
		File[] pdfFiles = file.listFiles(
			new FilenameFilter() {
				public boolean accept(File file, String name) {
					return name.endsWith(SVGPlusConstantsX.PDF);
				}
			});
		return pdfFiles;
	}
	
}
