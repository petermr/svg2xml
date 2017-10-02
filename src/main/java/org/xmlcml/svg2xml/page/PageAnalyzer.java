package org.xmlcml.svg2xml.page;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.html.HtmlBody;
import org.xmlcml.graphics.html.HtmlDiv;
import org.xmlcml.graphics.html.HtmlElement;
import org.xmlcml.graphics.html.HtmlH1;
import org.xmlcml.graphics.html.HtmlHtml;
import org.xmlcml.graphics.html.HtmlP;
import org.xmlcml.graphics.html.HtmlStyle;
import org.xmlcml.graphics.html.HtmlTitle;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGTitle;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.image.ImageConverter;
import org.xmlcml.svg2xml.container.AbstractContainerOLD;
import org.xmlcml.svg2xml.container.AbstractContainerOLD.ContainerType;
import org.xmlcml.svg2xml.container.MixedContainer;
import org.xmlcml.svg2xml.container.ScriptContainerOLD;
import org.xmlcml.svg2xml.paths.Chunk;
import org.xmlcml.svg2xml.pdf.ChunkId;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;
import org.xmlcml.svg2xml.pdf.PDFIndex;
import org.xmlcml.svg2xml.util.SVG2XMLConstantsX;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

/**
 * Processes a page.
 * <p>
 * Normally called by an iteration over pages from PDFAnalyzer.
 * <p>
 * Main routine is splitChunksAnnotateAndCreatePage(). This creates whitespace-separated chunks and binds each to a ChunkAnalyzer. The ChunkAnalyzer is specialized as (say)
 * <ul>
 *   <li>FigureAnalyzer</li>
 *   <li>ImageAnalyzer</li>
 *   <li>ShapeAnalyzer</li>
 *   <li>MixedAnalyzer</li>
 *   <li>TextAnalyzer</li>
 * </ul>
 * These analyzers may output files (especially FigureAnalyzer) but most output is reserved until later..
 * <p>
 * Optionally (but normally) components are then output with
 * <code>
 *       PDFAnalyzer 		pdfIo.outputFiles(getPdfOptions()); // move this to PageAnalyzer
 * </code>
 * <p>
 * Each ChunkAnalyzer is bound to a corresponding AbstractContainer (e.g. PathContainer) each of which has a createHtmlElement().
 *       
 * @author pm286
 */
public class PageAnalyzer /*extends PageChunkAnalyzer*/ {


	private static final Logger LOG = Logger.getLogger(PageAnalyzer.class);
	
	public final static Pattern PATTERN = null;
	public final static String TITLE = "PAGE";
	final static PrintStream SYSOUT = System.out;

	private static final long BIG_SVG_FILE = 1000000;
	
	private int aggregatedContainerCount;
	private PageIO pageIo;
	private HtmlElement runningTextHtmlElement;

	private PDFAnalyzer pdfAnalyzer;
	private PDFIndex pdfIndex;  // maybe remove later

	/** 
	 * Main routine to analyze page
	 */
	protected List<AbstractContainerOLD> abstractContainerList;

	private PageAnalyzer() {
		pageIo = new PageIO();
	}	
	
	public PageAnalyzer(SVGSVG svg) {
		this();
		pageIo.setSvgInPage(svg);
		tidySVGPage();
	}

	public PageAnalyzer(SVGSVG svgPage, PDFAnalyzer pdfAnalyzer) {
		this();
		pageIo.setSvgInPage(svgPage);
		pageIo.setPDFAnalyzer(pdfAnalyzer);
		this.pdfAnalyzer = pdfAnalyzer;
	}

	public PageAnalyzer(File svgPageFile) {
		this();
		SVGSVG svgPage = convertLongHref(svgPageFile);
		pageIo.setSvgInPage(svgPage);
		tidySVGPage();
	}

	private void tidySVGPage() {
		GraphicsElement svgPage = pageIo.getRawSVGPage();
		removeClipPathsDefs(svgPage);
		removeClipPathAttributes(svgPage);
		numberElements();
	}

	private void numberElements() {
		GraphicsElement svgPage = pageIo.getRawSVGPage();
		if (svgPage != null) {
			for (int i = 0; i < svgPage.getChildElements().size(); i++) {
				SVGUtil.setSVGXAttribute((GraphicsElement) svgPage.getChildElements().get(i), SVG2XMLConstantsX.Z, String.valueOf(i));
			}
		}
	}

	private void removeClipPathsDefs(GraphicsElement svgPage) {
		List<SVGElement> defs = SVGUtil.getQuerySVGElements(svgPage, "./svg:defs");
		for (GraphicsElement def : defs) {
			removeClipPathChildrenAndEmptyDef(def);
		}
	}

	private void removeClipPathAttributes(GraphicsElement svgPage) {
		if (svgPage != null) {
			Nodes clipPathAttributes = svgPage.query("./*/@clip-path");
			for (int i = 0; i < clipPathAttributes.size(); i++) {
				clipPathAttributes.get(i).detach();
			}
		}
	}

	private void removeClipPathChildrenAndEmptyDef(GraphicsElement def) {
		List<SVGElement> clipPaths = SVGUtil.getQuerySVGElements(def, "./svg:clipPath");
		for (GraphicsElement clipPath : clipPaths) {
			clipPath.detach();
		}
		if (def.getChildElements().size() == 0) {
			def.detach();
		}
	}

	private SVGSVG convertLongHref(File svgPageFile) {
		SVGSVG svgPage = null;
		try {
			String content = FileUtils.readFileToString(svgPageFile, "UTF-8");
			long size = FileUtils.sizeOf(svgPageFile);
			if (size > BIG_SVG_FILE) {
				ImageConverter cleaner = new ImageConverter();//TODO not used
			}
			svgPage = (SVGSVG) SVGElement.readAndCreateSVG(XMLUtil.parseXML(content));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return svgPage;
	}


	/**
	 * Main routine, which creates ChunkAnalyzers from SVGGs.
	 * <p>
	 * Splits into chunks by whitespace then normalizes all paths with Path2ShapeConverter. Does this for each
	 * chunk to avoid problems with widely distributed elements.
	 */
	public void splitChunksAndCreatePage() {
 		List<SVGElement> gList = createWhitespaceChunkList();
		pageIo.setSvgOutPage(pageIo.createBlankSVGOutPageWithNumberAndSize());
		pageIo.ensureWhitespaceSVGChunkList();
		for (int ichunk = 0; ichunk < gList.size(); ichunk++) {
			SVGG gChunk = (SVGG) gList.get(ichunk);
			//String chunkId = this.getHumanPageNumber()+"."+ichunk;
			//SVGSVG.wrapAndWriteAsSVG(gChunk, new File("target/chunk."+chunkId+".A.svg"));
			//SVGSVG.wrapAndWriteAsSVG(gChunk, new File("target/chunk."+chunkId+".C.svg"));
			ChunkAnalyzer chunkAnalyzer = createSpecificAnalyzer(gChunk);
			if (!(chunkAnalyzer instanceof TextAnalyzerOLD)) {
				LOG.trace(chunkAnalyzer);
				sortByZ(gChunk);
			}
			List<AbstractContainerOLD> newContainerList = chunkAnalyzer.createContainers();
			for (AbstractContainerOLD newContainer : newContainerList) {
				newContainer.setSVGChunk(gChunk);
			}
			createChunksAndStoreInContainer(newContainerList, chunkAnalyzer);
		}
		pageIo.createFinalSVGPageFromChunks();
	}

	private void sortByZ(SVGG gChunk) {
		Map<Double, SVGElement> elementByZMap = new HashMap<Double, SVGElement>();
		List<SVGElement> childElements = SVGUtil.getQuerySVGElements(gChunk, "./*");
		LOG.trace("child: "+childElements.size());
		List<Double> rawList = new ArrayList<Double>();
		for (SVGElement svgElement : childElements) {
			Attribute attribute = SVGUtil.getSVGXAttributeAttribute(svgElement, SVG2XMLConstantsX.Z);
			if (attribute != null) {
				Double z = Double.valueOf(attribute.getValue());
				elementByZMap.put(z, svgElement);
				rawList.add(z);
			}
		}
		//boolean isOrdered = isZListOrdered(rawList);
		//if (!isOrdered) {
		detachChildrenAndReplaceInZOrder(gChunk, elementByZMap, childElements, rawList);
		//}
	}

	private void detachChildrenAndReplaceInZOrder(SVGG gChunk, Map<Double, SVGElement> elementByZMap,
			List<SVGElement> childElements, List<Double> rawList) {
		for (GraphicsElement childElement : childElements) {
			childElement.detach();
		}
		Collections.sort(rawList);
		for (Double z : rawList) {
			//System.out.print(z+" ");
			Element element = elementByZMap.get(z);
			element.detach();
			gChunk.appendChild(element);
		}
		//System.out.println();
	}

	private boolean isZListOrdered(List<Integer> rawList) {
		boolean isOrdered = true;
		for (int i = 0; i < rawList.size(); i++) {
			if (i > 0 && rawList.get(i-1) > rawList.get(i)) {
				isOrdered = false;
				break;
			}
		}
		return isOrdered;
	}

	/** 
	 * Decides whether chunk is Text, Path, Image or Mixed
	 * 
	 * @param gChunk
	 * @return analyzer suited to type (e.g. TextAnalyzer)
	 */
	public ChunkAnalyzer createSpecificAnalyzer(SVGElement gChunk) {
		// path ytransformations have already been done
		ChunkAnalyzer analyzer = null;
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(gChunk);
		List<SVGShape> shapeList = SVGShape.extractSelfAndDescendantShapes(gChunk);
		List<SVGImage> imageList = SVGImage.extractSelfAndDescendantImages(gChunk);
		LOG.trace("text: "+textList.size()+" shapeList: "+shapeList.size()+" imageList: "+imageList.size());
		String id = gChunk.getId();
		ChunkId chunkId = (id == null ? null : new ChunkId(id));
		if (textList.size() != 0 && (shapeList.size() == 0 && imageList.size() == 0)) {
			analyzer = new TextAnalyzerOLD(textList, this);
		} else if (shapeList.size() != 0 && (textList.size() == 0 && imageList.size() == 0)) {			
			analyzer = createShapeAnalyzer(shapeList);
		} else if (imageList.size() != 0 && (textList.size() == 0 && shapeList.size() == 0)) {
			analyzer = createImageAnalyzer(imageList);
		} else {
			analyzer = new MixedAnalyzer(this);
			ChunkAnalyzer childAnalyzer = null;
			MixedAnalyzer mixedAnalyzer = (MixedAnalyzer) analyzer;
			if (imageList.size() != 0) {
				childAnalyzer = createImageAnalyzer(imageList);
				mixedAnalyzer.add(childAnalyzer);
				childAnalyzer.setChunkId(chunkId, 1);
			}
			if (textList.size() != 0) {
				childAnalyzer = new TextAnalyzerOLD(textList, this);
				mixedAnalyzer.add(childAnalyzer);
				childAnalyzer.setChunkId(chunkId, 2);
			}
			if (shapeList.size() != 0) {
				childAnalyzer = createShapeAnalyzer(shapeList);
				mixedAnalyzer.add(childAnalyzer);
				childAnalyzer.setChunkId(chunkId, 3);
			}
			LOG.trace("MIXED: "+analyzer+" "+gChunk.getChildCount());
		}
		analyzer.setSVGChunk(gChunk);
		return analyzer;
	}

	private ChunkAnalyzer createImageAnalyzer(List<SVGImage> imageList) {
		ImageAnalyzer imageAnalyzer = new ImageAnalyzer(this);
		imageAnalyzer.addImageList(imageList);
		return imageAnalyzer;
	}

	private ShapeAnalyzer createShapeAnalyzer(List<SVGShape> shapeList) {
		ShapeAnalyzer shapeAnalyzer = new ShapeAnalyzer(this);
		shapeAnalyzer.addShapeList(shapeList);
		return shapeAnalyzer;
	}

	
	protected void ensureAbstractContainerList() {
		if (abstractContainerList == null) {
			abstractContainerList = new ArrayList<AbstractContainerOLD>();
		}
	}

	public List<AbstractContainerOLD> getAbstractContainerList() {
		return abstractContainerList;
	}


	private void createChunksAndStoreInContainer (
			List<? extends AbstractContainerOLD> newContainerList, ChunkAnalyzer pageChunkAnalyzer) {
		ensureAbstractContainerList();
		for (AbstractContainerOLD newContainer : newContainerList) {
			newContainer.setChunkAnalyzer(pageChunkAnalyzer);
			ChunkId chunkId = new ChunkId(pageIo.getHumanPageNumber(), aggregatedContainerCount);
			newContainer.setChunkId(chunkId);
			abstractContainerList.add(newContainer);
			SVGG gChunk = newContainer.createSVGGChunk();
			gChunk.setId(chunkId.toString());
			gChunk = createChunksAndAddIdAndAttributes(gChunk, chunkId, pageChunkAnalyzer, PageIO.DECIMAL_PLACES);
			newContainer.setSVGChunk(gChunk);
			newContainer.setChunkId(chunkId);
			LOG.trace("Chunk "+newContainer.getClass()+" "+chunkId+" "/*+gOut*/);
			pageIo.add(gChunk);
			aggregatedContainerCount++;
		}
	}


	void writeFinalSVGChunks(List<? extends AbstractContainerOLD> containerList, ChunkAnalyzer analyzer, File outputDocumentDir) {
		Character cc = 'a';
		for (AbstractContainerOLD container : containerList) {
			container.writeFinalSVGChunk(outputDocumentDir, cc++, getHumanPageNumber(), getAggregatedCount());
		}
	}
	
	private int getAggregatedCount() {
		return pageIo.getAggregatedCount();
	}

	private int getHumanPageNumber() {
		return pageIo.getHumanPageNumber();
	}

	/** 
	 * (Constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	public String getTitle() {
		return TITLE;
	}

	private SVGG createChunksAndAddIdAndAttributes(SVGG gOrig, ChunkId chunkId, ChunkAnalyzer analyzerX, int decimalPlaces) {
		if (analyzerX == null) {
			throw new RuntimeException("Null analyzer");
		}
		List<SVGElement> gList = SVGUtil.getQuerySVGElements(gOrig, "*");//.//svg:g");
		SVGG gOut = analyzerX.createChunkFromList(gList);
		gOut.setId(chunkId.toString());
		XMLUtil.copyAttributes(gOrig, gOut);
		gOut.format(decimalPlaces);
		return gOut;
	}

	/**
	 * &lt;title stroke="black" stroke-width="1.0"&gt;char: 981; name: null; f: Symbol; fn: PHHOAK+Symbol; e: Dictionary&lt;/title&gt;
	 * @param svg
	 */
	private void processNonUnicodeCharactersInTitles() {
		List<SVGElement> textTitles = SVGUtil.getQuerySVGElements(pageIo.getRawSVGPage(), ".//svg:title");
		for (GraphicsElement t : textTitles) {
			SVGTitle title = (SVGTitle) t;
			String s = title.getValue();
			String[] chunks =s.split(";");
			Integer ss = null;
			for (String chunk : chunks) {
				String[] sss = chunk.split(":");
				if (sss[0].equals("char") && !sss[1].equals("null")) {
					ss = new Integer(sss[1].trim());
					break;
				}
				if (sss[0].equals("name") && !sss[1].equals("null")) {
					ss = 127;
					break;
				}
			}
			GraphicsElement text = ((GraphicsElement)title.getParent());
			int cc =text.getChildCount();
			for (int i = 0; i < cc; i++) {
				text.getChild(0).detach();
			}
			char c =  (char)(int)ss;
			LOG.trace("> "+c);
			try {
				text.appendChild(String.valueOf(c));
			} catch (Exception e) {
				LOG.trace("skipped problem character: "+(int)c);
			}
		}
	}
	
	private List<SVGElement> createWhitespaceChunkList() {
		//pageIo.readRawSVGPageIfNecessary();
		processNonUnicodeCharactersInTitles();
		List<Chunk> chunkList = createWhitespaceSeparatedChunks();
		// wrong place for this
		//WhitespaceChunkerAnalyzerX.drawBoxes(chunkList, "red", "yellow", 0.5);
		List<SVGElement> gList = SVGG.generateElementList(pageIo.getRawSVGPage(), "svg:g/svg:g/svg:g[@edge='YMIN']");
		return gList;
	}

	private List<Chunk> createWhitespaceSeparatedChunks() {
		List<Chunk> chunkList = 
				WhitespaceChunkerAnalyzerX.chunkCreateWhitespaceChunkList(pageIo.getRawSVGPage());
		return chunkList;
	}


	private void annotatePage() {
		List<SVGG> gList = SVGG.extractGs(SVGUtil.getQuerySVGElements(pageIo.getRawSVGPage(), ".//svg:g[@id]"));
		for (SVGG g : gList) {
			ChunkId chunkId = new ChunkId(g.getId());
			boolean indexed = pdfIndex.getUsedIdSet().contains(chunkId);
			LOG.trace("ID written "+chunkId+" "+indexed);
			if (indexed) {
				Real2Range bbox = g.getBoundingBox();
				Real2[] corners = bbox.getLLURCorners();
				SVGLine line = new SVGLine(corners[0], corners[1]);
				line.setOpacity(0.3);
				line.setWidth(5.0);
				line.setFill("green");
				g.appendChild(line);
			}
		}
	}

	public void add(AbstractContainerOLD container) {
		ensureAbstractContainerList();
		abstractContainerList.add(container);
	}

	public HtmlElement createHtmlElement() {
		ensureAbstractContainerList();
		HtmlHtml html = new HtmlHtml();
		addStyle(html);
		HtmlTitle title = new HtmlTitle("Page: "+pageIo.getHumanPageNumber());
		html.appendChild(title);
		HtmlBody body = new HtmlBody();
		html.appendChild(body);
		HtmlDiv div = new HtmlDiv();
		body.appendChild(div);
		HtmlP htmlP = new HtmlP();
		htmlP.appendChild("Containers: "+abstractContainerList.size());
		div.appendChild(htmlP);
		cleanHtml(div);
		for (AbstractContainerOLD container : abstractContainerList) {
			div.appendChild(container.createHtmlElement());
		}
		return html;
	}

	private void addStyle(HtmlHtml html) {
		HtmlStyle style = new HtmlStyle();
		style.addCss("div {border : solid 1pt; margin: 2pt; padding : 2pt}");
		html.insertChild(style, 0);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Page: "+pageIo.getMachinePageNumber()+"\n");
		ensureAbstractContainerList();
		sb.append("Containers: "+abstractContainerList.size()+"\n");
		for (AbstractContainerOLD container : abstractContainerList) {
			sb.append(container.toString()+"\n");
		}
		return sb.toString();
	}

	public void setPageAnalyzerContainerList(
			List<AbstractContainerOLD> abstractContainerList) {
		this.abstractContainerList = abstractContainerList;
	}

	/** removes empty/unnecessary spans etc.
	 *  
	 * @param div
	 */
	public static void cleanHtml(HtmlElement div) {
		removeWhitespaceSpan(div);
		removeSpanSubSupBI(div);
		replaceSpansByTextChildren(div);
	}

	private static void replaceSpansByTextChildren(HtmlElement div) {
		//String xpath = "//*[local-name()='span' and count(text()) = 1  and count(*) = 0]";
		//replaceNodesWithChildren(div, xpath);
		String xpath = ".//*[local-name()='span' and count(text()) = 1  and count(*) = 0]/text()";
		replaceParentsWithNodes(div, xpath);
	}

	private static void replaceNodesWithChildren(HtmlElement div, String xpath) {
		Nodes nodes = div.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			Node spanNode = nodes.get(i);
			ParentNode parent = spanNode.getParent();
			int index = parent.indexOf(spanNode);
			int nchild = spanNode.getChildCount();
			for (int j = nchild-1; j >=0; j--) {
				Node child = spanNode.getChild(j);
				child.detach();
				parent.insertChild(child, index);
			}
			parent.detach();
		}
	}

	private static void removeWhitespaceSpan(HtmlElement div) {
		removeNodes(div, ".//*[local-name()='span' and normalize-space(.)='']");
	}

	private static void removeNodes(HtmlElement div, String xpath) {
		Nodes nodes = div.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	private static void removeSpanSubSupBI(HtmlElement div) {
		replaceParentsWithNodes(div, ".//*[local-name()='span']/*[local-name()='sub' or local-name()='sup' or local-name()='b' or local-name()='i']");
	}

	/** not tested
	 * 
	 * @param div
	 * @param xpath
	 */
	private static void replaceParentsWithNodes(HtmlElement div, String xpath) {
		Nodes nodes = div.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			replaceParentWith(node);
		}
	}

	private static void replaceParentWith(Node node) {
		Element parent = (Element) node.getParent();
		Element grandParent = (Element) parent.getParent();
		node.detach();
		grandParent.replaceChild(parent, node);
	}


	public PageIO getPageIO() {
		return pageIo;
	}

	public void setMachinePageNumber(int pageNumber) {
		pageIo.setMachinePageNumber(pageNumber);
	}


	public void setRawSVGDocumentDir(File rawSVGDocumentDir) {
		pageIo.setRawSVGDocumentDir(rawSVGDocumentDir);
	}


	public void writeRawSVGPageToRawDirectory() {
		pageIo.writeRawSVGPageToRawDirectory();
	}

	public int getMachinePageNumber() {
		return pageIo.getMachinePageNumber();
	}
	
	public void summaryContainers() {
		int page = this.getMachinePageNumber();
		SYSOUT.println("************************PAGE***************************"+page+">>>>>> \n");
		SYSOUT.println(this.summaryString());
		SYSOUT.println("************************PAGE***************************"+page+"<<<<<< \n");
	}

	public void outputChunks() {
		ensureAbstractContainerList();
		List<AbstractContainerOLD> abstractContainerList = this.getAbstractContainerList();
		//SYSOUT.println(".......................");
		for (AbstractContainerOLD abstractContainer : abstractContainerList) {
			SVGG chunk = abstractContainer.getSVGChunk();
			String chunkId = chunk.getId();
			LOG.trace("Chunk "+chunk.toXML());
			File file = new File(pageIo.createChunkFilename(chunkId));
			//SVGSVG.wrapAndWriteAsSVG(chunk, file);
			//PageIO.outputFile(chunk, file);
			LOG.trace(abstractContainer.getClass() + " " + chunkId);
		}
	}

	public void outputImages() {
		ensureAbstractContainerList();
		List<AbstractContainerOLD> abstractContainerList = this.getAbstractContainerList();
		//SYSOUT.println(".......................");
		for (AbstractContainerOLD abstractContainer : abstractContainerList) {
			SVGG chunk = abstractContainer.getSVGChunk();
			String chunkId = chunk.getId();
			ChunkAnalyzer chunkAnalyzer = abstractContainer.getChunkAnalyzer();
			if (chunkAnalyzer instanceof ImageAnalyzer) {
				ImageAnalyzer imageAnalyzer = (ImageAnalyzer) chunkAnalyzer;
				List<SVGImage> imageList = imageAnalyzer.getImageList();
				outputImageFiles(chunkId, imageAnalyzer, imageList);
				LOG.warn("ImageAnalyzer needs writing: "+chunkId);
			} else if (chunkAnalyzer instanceof FigureAnalyzer) {
				throw new RuntimeException("FigureAnalyzer needs writing: "+chunkId);
			} else if (chunkAnalyzer instanceof MixedAnalyzer) {
				MixedAnalyzer mixedAnalyzer = (MixedAnalyzer) chunkAnalyzer;
				ImageAnalyzer imageAnalyzer = mixedAnalyzer.getImageAnalyzer();
				if (imageAnalyzer != null) {
					List<SVGImage> imageList = imageAnalyzer.getImageList();
					outputImageFiles(chunkId, imageAnalyzer, imageList);
				}
			} else {
				
			}
			LOG.trace(abstractContainer.getClass() + " " + chunkId);
		}
	}

	private void outputImageFiles(String chunkId, ImageAnalyzer imageAnalyzer,
			List<SVGImage> imageList) {
		if (imageList.size() == 1) {
			try {
				outputImageFile(chunkId, imageList.get(0));
			} catch (RuntimeException e) {
				
			}
		} else {
			// enumerate images with extra digits
			int imageCount = imageList.size();
			for (int i = 0; i < imageCount; i++) {
				SVGImage image = imageList.get(i);
				String value = image.getImageValue();
				if (value == null || value.length() < 100) {
					LOG.trace("small/zero image "+value);
				} else {
					String fileId = chunkId+"."+(i+1);
					LOG.trace("output image: "+fileId);
					try {
						outputImageFile(fileId, image);
					} catch (RuntimeException e) {
						
					}
				}
			}
		}
	}

	private void outputImageFile(String chunkId, SVGImage image) {
		File file = new File(pageIo.createImageFilename(chunkId));
		PageIO.outputImage(image, file, pageIo.getImageMimeType());
	}

	public void outputHtmlComponents() {
		LOG.trace(".......................");
		Set<ChunkId> chunkIdSet = new HashSet<ChunkId>(); 
		ensureAbstractContainerList();
		LOG.trace("abstractContainers "+abstractContainerList.size());
		for (AbstractContainerOLD abstractContainer : abstractContainerList) {
			LOG.trace(abstractContainer.getClass());
			if (abstractContainer instanceof MixedContainer) {
				LOG.trace(abstractContainer.getClass());
			}
			ChunkId chunkId = abstractContainer.getChunkId();
			if (chunkId == null) {
				// probably a bug
				throw new RuntimeException("Null chunkId in "+abstractContainer.getClass()+" "+abstractContainer.getChunkId());
			}
			normalizeDuplicateChunkId(chunkIdSet, abstractContainer, chunkId);
			long time = System.currentTimeMillis();
			LOG.trace("abstractContainer: "+abstractContainer.getClass()+" "+chunkId+" "+abstractContainer.getType()+" "+abstractContainer.hashCode());
			HtmlElement element = abstractContainer.createHtmlElement();
			ContainerType type = abstractContainer.getType();
			if (pageIo.isOutputFigures() && ContainerType.FIGURE.equals(type)) {
			} else if (pageIo.isOutputTables() && ContainerType.TABLE.equals(type)) {
			} else if (pageIo.isOutputHtmlChunks() && ContainerType.CHUNK.equals(type)) {
			} else if (pageIo.isOutputHeaders() && ContainerType.HEADER.equals(type)) {
			} else if (pageIo.isOutputFooters() && ContainerType.FOOTER.equals(type)) {
			}
			if (type != null) {
				LOG.trace("creating html chunk: "+type+": "+chunkId.toString()+" "+(System.currentTimeMillis()-time));
				File file = PageIO.createHtmlFile(pageIo.getFinalSVGDocumentDir(), type, chunkId.toString());
				LOG.trace("html "+file+" "+element.toXML());
				PageIO.outputFile(element, file);
			}
			chunkIdSet.add(chunkId);
		}
		LOG.trace("finished outputHtmlComponents");
	}

	private void normalizeDuplicateChunkId(Set<ChunkId> chunkIdSet,
			AbstractContainerOLD abstractContainer, ChunkId chunkId) {
		// sometimes some duplicates (this is a bug in parsing paragraphs and is crude and
		// should be eliminated
		if (chunkIdSet.contains(chunkId)) {
			chunkId.setSubChunkNumber(new Integer(1));
			if (chunkIdSet.contains(chunkId)) {
				LOG.trace(abstractContainer.getClass()+" "+chunkId);
			}
			abstractContainer.setChunkId(chunkId);
		}
	}
	
	public static PageAnalyzer createAndAnalyze(File rawSvgPageFile) {
		return createAndAnalyze(rawSvgPageFile, (File) null, 1);
	}

	public static PageAnalyzer createAndAnalyze(File rawSvgPageFile, File rawSVGDirectory, Integer pageCounter) {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(rawSvgPageFile);
		pageAnalyzer.setRawSVGDocumentDir(rawSVGDirectory);
		pageAnalyzer.setMachinePageNumber(pageCounter);
		pageAnalyzer.splitChunksAndCreatePage();
		LOG.trace(pageAnalyzer.getPageIO().toString());
		return pageAnalyzer;
	}

	public static PageAnalyzer createAndAnalyze(SVGSVG svg, Integer pageCounter) {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svg);
		pageAnalyzer.setMachinePageNumber(pageCounter);
		pageAnalyzer.splitChunksAndCreatePage();
		LOG.trace(pageAnalyzer.getPageIO().toString());
		return pageAnalyzer;
	}

	public static PageAnalyzer createAndAnalyze(SVGSVG svg, Integer pageCounter, File rawSvgDirectory) {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svg);
		pageAnalyzer.setRawSVGDocumentDir(rawSvgDirectory);
		pageAnalyzer.setMachinePageNumber(pageCounter);
		pageAnalyzer.splitChunksAndCreatePage();
		LOG.trace(pageAnalyzer.getPageIO().toString());
		return pageAnalyzer;
	}

	public void outputHtmlRunningText() {
		HtmlElement div = createRunningHtml();
		div.setId(String.valueOf(pageIo.getHumanPageNumber()));
		SYSOUT.print("<"+div.getId()+">");
		File file = PageIO.createHtmlFile(pageIo.getFinalSVGDocumentDir(), ContainerType.TEXT, div.getId());
		// should already have been output
		// PageIO.outputFile(div, file);
	}

	public HtmlElement createRunningHtml() {
		runningTextHtmlElement = new HtmlDiv();
		ensureAbstractContainerList();
		for (AbstractContainerOLD abstractContainer : abstractContainerList) {
			LOG.trace("Container: "+abstractContainer.getClass());
			ContainerType type = abstractContainer.getType();
			String content = abstractContainer.getRawValue();
			if (ContainerType.HEADER.equals(abstractContainer.getType())) {
			} else if (ContainerType.FOOTER.equals(abstractContainer.getType())) {
			} else if (ContainerType.TITLE.equals(type)) {
				HtmlH1 h1 = new HtmlH1();
				h1.appendChild(((ScriptContainerOLD)abstractContainer).createHtmlElement().copy());
				runningTextHtmlElement.appendChild(h1);
			} else if (ContainerType.FIGURE.equals(type)) {
				if (abstractContainer.getFigureElement() != null) {
					runningTextHtmlElement.appendChild(abstractContainer.getFigureElement().copy());
				}
			} else if (ContainerType.LIST.equals(type)) {
				runningTextHtmlElement.appendChild(abstractContainer.getListElement().copy());
			} else if (ContainerType.TABLE.equals(type)) {
				Element element = abstractContainer.getTableElement();
				if (element != null) {
					runningTextHtmlElement.appendChild(element.copy());
				}
			} else if (ContainerType.TEXT.equals(type)) {
				HtmlElement div = abstractContainer.createHtmlElement();
				PageIO.copyChildElementsFromTo(div, runningTextHtmlElement);
			} else if (ContainerType.CHUNK.equals(type)) {
				HtmlElement div = abstractContainer.createHtmlElement();
				PageIO.copyChildElementsFromTo(div, runningTextHtmlElement);
			} else {
				addSee(runningTextHtmlElement, type);
			}
		}
		return runningTextHtmlElement;
	}

	private void addSee(HtmlElement element, ContainerType type) {
		HtmlElement p = new HtmlP("see "+type);
		element.appendChild(p);
	}

	public HtmlElement getRunningHtmlElement() {
		return runningTextHtmlElement;
	}

	public String summaryString() {
		StringBuilder sb = new StringBuilder("Page: "+pageIo.getMachinePageNumber()+"\n");
		ensureAbstractContainerList();
		sb.append("Containers: "+abstractContainerList.size()+"\n");
		for (AbstractContainerOLD container : abstractContainerList) {
			sb.append(container.summaryString()+"\n........................\n");
		}
		return sb.toString();
	}

	public void writeFinalSVGPageToFinalDirectory() {
		pageIo.writeFinalSVGPageToFinalDirectory();
	}

}
