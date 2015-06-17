package org.xmlcml.svg2xml.page;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.pdf2svg.SVGSerializer;
import org.xmlcml.svg2xml.container.AbstractContainer.ContainerType;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;
import org.xmlcml.svg2xml.pdf.PDFAnalyzerIO;
import org.xmlcml.svg2xml.util.SVG2XMLConstantsX;
import org.xmlcml.xml.XMLUtil;

public class PageIO {

	private static final Logger LOG = Logger.getLogger(PageIO.class);
	static {LOG.setLevel(Level.DEBUG);}
	
	private static final double WIDTH = 600.0;
	private static final double HEIGHT = 800.0;
	public static final String PAGE = "page";
	public static final String CHUNK = "chunk";
	public static final String FIGURE = "figure";
	public static final String IMAGE = "image";
	public static final String TABLE = "table";
	public static final String TEXT = "text";
	public static final String DOT_HTML = ".html";
	public static final String DOT_SVG = ".svg";
	public static final String DOT_PNG = ".png";

	private List<SVGG> whitespaceSVGChunkList;
	
	private int humanPageNumber;
	private int machinePageNumber = 0;
	private File rawSVGDocumentDir;
	private File finalSVGDocumentDir;
	private SVGSVG finalSVGPage;
	private SVGSVG rawSVGPage;
	private int aggregatedCount;
	// to pick up processing options
	private PDFAnalyzer pdfAnalyzer;
	private String imageMimeType = SVGImage.IMAGE_PNG;
	public static final int DECIMAL_PLACES = 3;
	
	public PageIO() {
		
	}
	
	public int getHumanPageNumber() {
		return humanPageNumber;
	}

	public void setHumanPageNumber(int humanPageNumber) {
		this.humanPageNumber = humanPageNumber;
	}

	public File getRawSVGDocumentDir() {
		return rawSVGDocumentDir;
	}

	public void setRawSVGDocumentDir(File rawSVGDocumentDir) {
		this.rawSVGDocumentDir = rawSVGDocumentDir;
	}

	public File createDefaultFinalDocumentDir() {
		if (rawSVGDocumentDir != null) {
			finalSVGDocumentDir = createfinalSVGDocumentDirectory(rawSVGDocumentDir);
			finalSVGDocumentDir.mkdirs();
		} else {
			finalSVGDocumentDir = new File("target/");
			finalSVGDocumentDir.mkdirs();
		}
		return finalSVGDocumentDir;
	}

	public static File createfinalSVGDocumentDirectory(File dir) {
		String name = dir.getName();
		return new File(PDFAnalyzerIO.OUTPUT_DIR, name);
	}

	public File getFinalSVGDocumentDir() {
		return (finalSVGDocumentDir == null) ? createDefaultFinalDocumentDir() : finalSVGDocumentDir;
	}

	public void setFinalSVGDocumentDir(File finalSVGDocumentDir) {
		this.finalSVGDocumentDir = finalSVGDocumentDir;
	}

	public SVGSVG getFinalSVGPage() {
		return finalSVGPage;
	}

	public void setFinalSVGPage(SVGSVG finalSVGPage) {
		this.finalSVGPage = finalSVGPage;
	}

	public SVGSVG getRawSVGPage() {
		return rawSVGPage;
	}

	public void setRawSVGPage(SVGSVG rawSVGPage) {
		this.rawSVGPage = rawSVGPage;
	}

	public List<SVGG> getWhitespaceSVGChunkList() {
		return whitespaceSVGChunkList;
	}

	public int getMachinePageNumber() {
		return machinePageNumber;
	}

	public void add(SVGG gChunk) {
		ensureChunkList();
		whitespaceSVGChunkList.add(gChunk);
	}

	private void ensureChunkList() {
		if (whitespaceSVGChunkList == null) {
			whitespaceSVGChunkList = new ArrayList<SVGG>();
		}
		
	}

	SVGSVG createBlankSVGOutPageWithNumberAndSize() {
		finalSVGPage = new SVGSVG();
		finalSVGPage.setWidth(WIDTH);
		finalSVGPage.setHeight(HEIGHT);
		String pageId = "p."+humanPageNumber;
		finalSVGPage.setId(pageId);
		return finalSVGPage;
	}

	void writeRawSVGPageToRawDirectory() {
		try {
			String pageRoot = createPageRootWithHumanNumber();
			finalSVGDocumentDir.mkdirs();
			String id = rawSVGPage.getId();
			LOG.trace("ID "+id);
			SVGUtil.debug(
				rawSVGPage, new FileOutputStream(new File(rawSVGDocumentDir, pageRoot+SVG2XMLConstantsX.DOT_SVG)), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void writeFinalSVGPageToFinalDirectory() {
		try {
			String pageRoot = createPageRootWithHumanNumber();
			finalSVGDocumentDir.mkdirs();
			String id = finalSVGPage.getId();
			LOG.trace("ID "+id);
			SVGUtil.debug(
				rawSVGPage, new FileOutputStream(new File(finalSVGDocumentDir, pageRoot+SVG2XMLConstantsX.DOT_SVG)), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	String createPageRootWithHumanNumber() {
		String page = PAGE + (machinePageNumber + 1);
		LOG.trace("Page "+page);
		return page;
	}

	public void setMachinePageNumber(int pageNumber) {
		machinePageNumber = pageNumber;
		setHumanPageNumber(machinePageNumber + 1);
	}

	public void setSvgInPage(SVGSVG svgPage) {
		rawSVGPage = svgPage;
	}

	public void setSvgOutPage(SVGSVG svgPage) {
		finalSVGPage= svgPage;
	}

	public void ensureWhitespaceSVGChunkList() {
		if (whitespaceSVGChunkList == null) {
			whitespaceSVGChunkList = new ArrayList<SVGG>();
		}
	}

	public void createFinalSVGPageFromChunks() {
		if (pdfAnalyzer == null) {
			pdfAnalyzer = new PDFAnalyzer();
		}
		if (pdfAnalyzer != null && pdfAnalyzer.getPdfOptions().annotateChunks) {
			WhitespaceChunkerAnalyzerX.drawBoxes(whitespaceSVGChunkList, "none", "yellow", 0.5);
		}
		for (SVGG g : whitespaceSVGChunkList) {
			if (g.toXML().contains("yellow")) {
				LOG.trace("rect");
			}
			finalSVGPage.appendChild(g);
		}
		if (pdfAnalyzer.getPdfOptions().isOutputAnnotatedSvgPages()) {
			String pageRoot = createPageRootWithHumanNumber();
			try {
				File outputDir = (rawSVGDocumentDir == null) ? createFixmeDir() : rawSVGDocumentDir;
				outputDir.mkdirs();
				File svgFile = new File(outputDir, pageRoot+SVG2XMLConstantsX.DOT_SVG);
				LOG.debug("Path: "+svgFile.getAbsolutePath());
				SVGUtil.debug(finalSVGPage, new FileOutputStream(svgFile), 1);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private File createFixmeDir() {
		return new File("target/fixme/");
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" whitespaceChunkList "+(whitespaceSVGChunkList == null ? null : whitespaceSVGChunkList.size())+"; ");
		sb.append(" humanPageNumber "+humanPageNumber+"; ");
		sb.append(" machinePageNumber "+machinePageNumber+"; ");
		sb.append(" rawSVGDocumentDir "+getRawSVGDocumentDir()+"; ");
		sb.append(" finalSVGDocumentDir "+getFinalSVGDocumentDir()+"; ");
		sb.append(" rawSVGPage "+rawSVGPage+"; ");
		sb.append(" finalSVGPage "+finalSVGPage+"; ");
		return sb.toString();
	}

	public int getAggregatedCount() {
		return aggregatedCount;
	}

	public void setAggregatedCount(int count) {
		aggregatedCount = count;
	}

	public String createChunkFilename(String chunkId) {
		return createFilename(rawSVGDocumentDir, CHUNK, chunkId, DOT_SVG);
	}

	public String createImageFilename(String chunkId) {
		return createFilename(rawSVGDocumentDir, IMAGE, chunkId, DOT_PNG);
	}
	
	public String createSvgFilename(String chunkId) {
		File baseFile = (rawSVGDocumentDir == null) ? createFixmeDir() : rawSVGDocumentDir;
		String filename = createFilename(baseFile, IMAGE, chunkId, DOT_SVG);
		LOG.debug("generated filename "+filename);
		return filename;
	}
	
	private String createFilename(File baseFile, String root, String id, String suffix) {
		File file = new File(baseFile, root+"."+id+suffix);
		return file.getPath().replaceAll("\\\\", "/");
	}
	
	public static File createHtmlFile(File dir, ContainerType type, String chunkId) {
		if (dir != null) {
			dir.mkdirs();
			return new File(dir, type+"."+chunkId+DOT_HTML);
		}
		return null;
	}

	// must add this in somewhere: WhitespaceChunkerAnalyzerX.drawBoxes
	public static void outputFile(Element element, File file) {
		if (file != null) {
			try {
				File parentFile = file.getParentFile();
				if (parentFile != null) {
					file.getParentFile().mkdirs();
				}
				LOG.debug("writing to "+file);
				Element elementCopy = (Element) element.copy();
				SVGSerializer svgSerializer = new SVGSerializer(new FileOutputStream(file));
				svgSerializer.write(XMLUtil.ensureDocument(elementCopy));
			} catch (IOException e) {
				throw new RuntimeException("Cannot write file: "+file, e);
			}
		} else {
			throw new RuntimeException("Cannot write null file: ");
		}
	}

	public static void outputImage(SVGImage svgImage, File file, String mimeType) {
		try {
			file.getParentFile().mkdirs();
			svgImage.writeImage(file, mimeType);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file: "+file, e);
		}
	}

	public void setPDFAnalyzer(PDFAnalyzer pdfAnalyzer) {
		this.pdfAnalyzer = pdfAnalyzer;
	}

	public boolean isOutputHtmlChunks() {
		return pdfAnalyzer != null && pdfAnalyzer.getPdfOptions() != null &&
				pdfAnalyzer.getOutputHtmlChunks();
	}

	public boolean isOutputFigures() {
		return pdfAnalyzer != null && pdfAnalyzer.getPdfOptions() != null &&
				pdfAnalyzer.getOutputFigures();
	}

	public boolean isOutputFooters() {
		return pdfAnalyzer != null && pdfAnalyzer.getPdfOptions() != null &&
				pdfAnalyzer.getOutputFooters();
	}

	public boolean isOutputHeaders() {
		return pdfAnalyzer != null && pdfAnalyzer.getPdfOptions() != null &&
				pdfAnalyzer.getOutputHeaders();
	}

	public boolean isOutputTables() {
		return pdfAnalyzer != null && pdfAnalyzer.getPdfOptions() != null &&
				pdfAnalyzer.getOutputTables();
	}

	public static void copyChildElementsFromTo(HtmlElement fromElement, HtmlElement toElement) {
		for (int i = 0; i < fromElement.getChildCount(); i++) {
			Node child = fromElement.getChild(i);
			if (child instanceof Text) {
				toElement.appendChild(child.copy());
			} else {
				toElement.appendChild(HtmlElement.create((Element)child));
			}
		}
	}

	public String getImageMimeType() {
		return imageMimeType ;
	}

}
