package org.xmlcml.svgplus.document;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.pdf2svg.util.MenuSystem;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.core.SVGPlusConstants;
import org.xmlcml.svgplus.core.SemanticDocumentAction;

public class DocumentIteratorAction extends DocumentAction {

	private static final String INPUT_FILE = SVGPlusConstants.D_DOT+SVGPlusConstants.INPUT_FILE;
	private static final String OUTPUT_DIR = SVGPlusConstants.D_DOT+SVGPlusConstants.OUTPUT_DIR;

	private final static Logger LOG = Logger.getLogger(DocumentIteratorAction.class);

	public static final String PAGE_COUNT = SVGPlusConstants.D_DOT+"pageCount";

	private String format;
	private String regex;
	private Integer max;

	private File infile;
	private File outfile;

	private List<String> skipList;
	private List<File> infileList;

	private File outdir;
	
	public DocumentIteratorAction(AbstractActionElement documentActionElement) {
		super(documentActionElement);
	}
	
	@Override
	public void run() {
		LOG.trace("executing: \n"+getActionElement().getString());
		infile = getInfile();
		format = getFormat();
		regex = getRegex();
		if (infile == null) {
			LOG.trace("No input file given");
		} else if (!infile.exists()) {
			LOG.trace("Input file does not exist: "+infile.getAbsolutePath());
		} else {
			createInputFileList();
			if (infileList.size() > 0) {
				processInputFileList();
			}
		}
	}

	private void processInputFileList() {
		PageIteratorAction pageIteratorAction = getChildPageIteratorAction();
		if (pageIteratorAction != null) {
			for (File infile : infileList) {
				LOG.debug("file "+infile);
				semanticDocumentAction.setVariable(INPUT_FILE, infile);
				outdir = infile.getParentFile();
				outdir.mkdirs();
				semanticDocumentAction.setVariable(OUTPUT_DIR, outdir);
				pageIteratorAction.convertFileToSVGList(infile);
				runChildActionList();
			}
		}
	}

	private PageIteratorAction getChildPageIteratorAction() {
		Nodes nodes = this.actionElement.query("./*[local-name()='"+PageIteratorElement.TAG+"']");
		PageIteratorElement pageIteratorElement =  nodes.size() == 1 ? (PageIteratorElement) nodes.get(0) : null;
		return pageIteratorElement == null ? null : (PageIteratorAction) pageIteratorElement.getAction();
	}

	private void createInputFileList() {
		infileList = new ArrayList<File>();
		max = getInteger(DocumentIteratorElement.MAX);
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
		semanticDocumentAction.setVariable(SVGPlusConstants.D_DOT+SVGPlusConstants.ROOT_DIR, indir);
	}
	
	private List<File> getFiles(File indir) {
		File[] files = indir.listFiles(new FilenameFilter() {
			public boolean accept(File file, String s) {
				return file.getPath().endsWith(SVGPlusConstants.PDF); // for test
			}
		});
		return files == null ? new ArrayList<File>() : Arrays.asList(files);
	}

	private File getInfile() {
		infile = (File) semanticDocumentAction.getVariable(SemanticDocumentAction.S_INFILE);
		if (infile == null) {
			String filename = getFilename();
			infile = (filename == null) ? null : new File(filename);
		}
		return infile;
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
		menuSystem.setRoot("/../"+SVGPlusConstants.OUT+CMLConstants.S_SLASH+SVGPlusConstants.INDEX_HTML);
		menuSystem.setLabel("../../");
		menuSystem.setRowWidth(100);
		menuSystem.setAddPdf(false);
//		menuSystem.writeDisplayFiles(rawDirList, "_dir");

	}

	private File[] listSVGFiles(File rawDir) {
		File[] svgFiles = rawDir.listFiles(new FilenameFilter() {
			public boolean accept(File rawDir, String name) {
				return name.endsWith(SVGPlusConstants.SVG);
			}
		});
		return svgFiles;
	}


	private void outputSVGsToRawDir(File rawDir, List<SVGSVG> pageList) {
		int page = 0;
		for (SVGSVG svgPage :pageList) {
			CMLUtil.outputQuietly(svgPage, new File(rawDir, SVGPlusConstants.PAGE+(++page)+SVGPlusConstants.SVG), 1);
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
		String root = name.substring(0, name.length()-SVGPlusConstants.PDF.length());
		File subRootDir = new File(pdfFile.getParentFile(), root);
		subRootDir.mkdir();
		File rawDir = new File(subRootDir, SVGPlusConstants.RAW);
		rawDir.mkdir();
		return rawDir;
	}

	private File[] listPDFFiles(File file) {
		File[] pdfFiles = file.listFiles(
			new FilenameFilter() {
				public boolean accept(File file, String name) {
					return name.endsWith(SVGPlusConstants.PDF);
				}
			});
		return pdfFiles;
	}
	
}
