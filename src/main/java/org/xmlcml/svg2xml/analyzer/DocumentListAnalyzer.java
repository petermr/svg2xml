package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/** analyzes a collection of PDFs
 * allows us to find similarities in subcomponents, especially repeated items
 * 
 * @author pm286
 *
 */
public class DocumentListAnalyzer {

	private final static Logger LOG = Logger.getLogger(DocumentListAnalyzer.class);
	
	private static final String PDF = ".pdf";
	private File inputDir;
	private File svgTopDir = getDefaultSVGDir();
	private File outputTopDir = getDefaultOutputDir();
	
	private Multimap<String, SVGElement> contentMap;
	private Multimap<String, SVGElement> imageMap;
	private Multimap<String, SVGElement> pathMap;
	
	private int duplicateImageCount;
	private int duplicatePathCount;

	private String randomName;

	public DocumentListAnalyzer() {
		
	}
	
	private File getDefaultSVGDir() {
		randomName = getRandomName();
		return makeFile("target/svg/"+randomName);
	}
	
	public void setSVGTopDir(String svgTopDirName) {
		this.svgTopDir = new File(svgTopDirName);
	}

	private File getDefaultOutputDir() {
		return makeFile("target/output/"+randomName);
	}

	public void setOutputTopDir(String outputTopDirName) {
		this.svgTopDir = new File(outputTopDirName);
	}

	public void analyzeFiles(List<File> fileList) {
		if (fileList != null && fileList.size() > 0) {
			for (File file: fileList) {
				if (file.toString().endsWith(PDF)) {
					PDFAnalyzer analyzer = new PDFAnalyzer(this);
					setDirectoriesMapsAndCounts(analyzer);
					analyzer.analyzePDFFile(file);
				}
			}
		}
	}

	private void setDirectoriesMapsAndCounts(PDFAnalyzer analyzer) {
		analyzer.setOutputTopDir(outputTopDir);
		analyzer.setSVGTopDir(svgTopDir);
		setMaps(analyzer);
	}

	private void setMaps(PDFAnalyzer analyzer) {
		analyzer.setContentMap(contentMap);
		analyzer.setImageMap(imageMap);
		analyzer.setDuplicateImageCount(duplicateImageCount);
		analyzer.setPathMap(pathMap);
		analyzer.setDuplicatePathCount(duplicatePathCount);
	}
	
	public void analyzeDirectory(File inputDir) {
		this.inputDir = inputDir;
		ensureElementMultimaps();
		if (inputDir != null && inputDir.isDirectory()) {
			LOG.debug("SVG: "+svgTopDir+"; output: "+outputTopDir);
			File[] files = inputDir.listFiles();
			analyzeFiles(Arrays.asList(files));
		}
		findDuplicatesInIndexes();
	}

	private void ensureElementMultimaps() {
		contentMap = HashMultimap.create(); 
		imageMap = HashMultimap.create();
		duplicateImageCount = 0;
		pathMap = HashMultimap.create(); 
		duplicatePathCount = 0;
	}


	
	public void findDuplicatesInIndexes() {
		List<List<SVGElement>> elementListList;
		elementListList = PDFAnalyzer.findDuplicates(PDFAnalyzer.CONTENT, contentMap);
		printDuplicates(PDFAnalyzer.CONTENT, elementListList);
		elementListList = PDFAnalyzer.findDuplicates(PDFAnalyzer.IMAGE, imageMap);
		printDuplicates(PDFAnalyzer.IMAGE, elementListList);
		elementListList = PDFAnalyzer.findDuplicates(PDFAnalyzer.PATH, pathMap);
		printDuplicates(PDFAnalyzer.PATH, elementListList);
	}

	private void printDuplicates(String title, List<List<SVGElement>> elementListList) {
		if (elementListList.size() > 0 ) {
			LOG.debug("duplicate "+title);
			for (List<SVGElement> elementList : elementListList) {
				SVGElement firstElement = elementList.get(0);
				if (title.equals(PDFAnalyzer.CONTENT)) {
					String content = firstElement.getValue();
					LOG.debug(elementList.size()+": "+content.substring(0, Math.min(100, content.length())));
				} else if (title.equals(PDFAnalyzer.IMAGE)) {
					PDFAnalyzer.output(firstElement, duplicateImageCount, title);
					duplicateImageCount++;
				} else if (title.equals(PDFAnalyzer.PATH)) {
					PDFAnalyzer.output(firstElement, duplicatePathCount, title);
					duplicatePathCount++;
				}
			}
		}
	}

	private String getRandomName() {
		return "" + (int) (100*Math.random());
	}
	
	private File makeFile(String filename) {
		File file = new File(filename);
		file.mkdirs();
		return file;
	}

	/**
mvn exec:java -Dexec.mainClass="org.xmlcml.svg2xml.analyzer.DocumentListAnalyzer" 
    -Dexec.args="src/test/resources/pdfs/bmc"
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("DocumentListAnalyzerTest <directory>");
			System.out.println("mvn exec:java -Dexec.mainClass=\"org.xmlcml.svg2xml.analyzer.DocumentListAnalyzer\" " +
					" -Dexec.args=\"src/test/resources/pdfs/bmc\"");
			System.out.println("OR java org.xmlcml.svg2xml.analyzer.DocumentListAnalyzer src/test/resources/pdfs/bmc");
			System.exit(0);
		} else {
			DocumentListAnalyzer analyzer = new DocumentListAnalyzer();
			analyzer.analyzeDirectory(new File(args[0]));
		}
	}

}
