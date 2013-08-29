package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGTitle;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlH1;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlStyle;
import org.xmlcml.html.HtmlTitle;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.AbstractContainer.ContainerType;
import org.xmlcml.svg2xml.container.ScriptContainer;
import org.xmlcml.svg2xml.tools.Chunk;

/**
 * @author pm286
 *
 */
public class PageAnalyzer extends AbstractAnalyzer {


	private static final Logger LOG = Logger.getLogger(PageAnalyzer.class);
	
	public final static Pattern PATTERN = null;
	public final static String TITLE = "PAGE";
	final static PrintStream SYSOUT = System.out;
	
	private int aggregatedContainerCount;
	private PageIO pageIo;
	private List<AbstractContainer> abstractContainerList;
	private HtmlElement runningTextHtmlElement;
	
	private PageAnalyzer() {
		pageIo = new PageIO();
	}	

	public PageAnalyzer(SVGSVG svgPage, PDFAnalyzer pdfAnalyzer) {
		this();
		pageIo.setSvgInPage(svgPage);
		pageIo.setPDFAnalyzer(pdfAnalyzer);
	}

	public PageAnalyzer(File svgPageFile) {
		this();
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgPageFile);
		pageIo.setSvgInPage(svgPage);
	}

//	public void analyze() {
//	}
	
	@Override
	public SVGG oldAnnotateChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
	public Integer indexAndLabelChunk(String content, ChunkId id) {
		Integer serial = super.indexAndLabelChunk(content, id);
		// index...
		return serial;
	}
	
	void splitChunksAnnotateAndCreatePage() {
 		List<SVGElement> gList = createWhitespaceChunkList();
		pageIo.setSvgOutPage(pageIo.createBlankSVGOutPageWithNumberAndSize());
		pageIo.ensureWhitespaceSVGChunkList();
		for (int ichunk = 0; ichunk < gList.size(); ichunk++) {
			SVGG gOrig = (SVGG) gList.get(ichunk);
			AbstractAnalyzer analyzerX = AbstractAnalyzer.createSpecificAnalyzer(gOrig);
			List<AbstractContainer> newContainerList = analyzerX.createContainers(this);
			annotateAndOutput(newContainerList, analyzerX);
		}
		pageIo.createFinalSVGPageFromChunks();
	}

	private void annotateAndOutput (
			List<? extends AbstractContainer> newContainerList, AbstractAnalyzer analyzer) {
		ensureAggregatedContainerList();
		for (AbstractContainer newContainer : newContainerList) {
			ChunkId chunkId = new ChunkId(pageIo.getHumanPageNumber(), aggregatedContainerCount);
			newContainer.setChunkId(chunkId); 
			abstractContainerList.add(newContainer);
			SVGG gOut = newContainer.createSVGGChunk();
			gOut.setId(chunkId.toString());
			gOut = annotateChunkAndAddIdAndAttributes(gOut, chunkId, analyzer, PageEditorX.DECIMAL_PLACES);
			newContainer.setSVGChunk(gOut);
			newContainer.setChunkId(chunkId);
			LOG.trace("Chunk "+newContainer.getClass()+" "+chunkId+" "/*+gOut*/);
			pageIo.add(gOut);
			aggregatedContainerCount++;
		}
	}


	void writeFinalSVGChunks(List<? extends AbstractContainer> containerList, AbstractAnalyzer analyzer, File outputDocumentDir) {
		Character cc = 'a';
		for (AbstractContainer container : containerList) {
			container.writeFinalSVGChunk(outputDocumentDir, cc++, getHumanPageNumber(), getAggregatedCount());
		}
	}
	
	private int getAggregatedCount() {
		return pageIo.getAggregatedCount();
	}

	private int getHumanPageNumber() {
		return pageIo.getHumanPageNumber();
	}

	private void ensureAggregatedContainerList() {
		if (abstractContainerList == null) {
			abstractContainerList = new ArrayList<AbstractContainer>();
		}
	}

	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

	private SVGG annotateChunkAndAddIdAndAttributes(SVGG gOrig, ChunkId chunkId, AbstractAnalyzer analyzerX, int decimalPlaces) {
		if (analyzerX == null) {
			throw new RuntimeException("Null analyzer");
		}
		List<SVGElement> gList = SVGUtil.getQuerySVGElements(gOrig, "//svg:g");
		SVGG gOut = analyzerX.annotateChunk(gList);
		gOut.setId(chunkId.toString());
		CMLUtil.copyAttributes(gOrig, gOut);
		gOut.format(decimalPlaces);
		return gOut;
	}

	/**
	 * <title stroke="black" stroke-width="1.0">char: 981; name: null; f: Symbol; fn: PHHOAK+Symbol; e: Dictionary</title>
	 * @param svg
	 */
	private void processNonUnicodeCharactersInTitles() {
		List<SVGElement> textTitles = SVGUtil.getQuerySVGElements(pageIo.getRawSVGPage(), ".//svg:title");
		for (SVGElement t : textTitles) {
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
			SVGElement text = ((SVGElement)title.getParent());
			int cc =text.getChildCount();
			for (int i = 0; i < cc; i++) {
				text.getChild(0).detach();
			}
			char c =  (char)(int)ss;
			LOG.trace("> "+c);
			try {
				text.appendChild(""+c);
			} catch (Exception e) {
				LOG.trace("skipped problem character: "+(int)c);
			}
		}
	}
	
	private List<SVGElement> createWhitespaceChunkList() {
//		pageIo.readRawSVGPageIfNecessary();
		processNonUnicodeCharactersInTitles();
		List<Chunk> chunkList = createWhitespaceSeparatedChunks();
		WhitespaceChunkerAnalyzerX.drawBoxes(chunkList, "red", "yellow", 0.5);
		List<SVGElement> gList = SVGG.generateElementList(pageIo.getRawSVGPage(), "svg:g/svg:g/svg:g[@edge='YMIN']");
		return gList;
	}

	private List<Chunk> createWhitespaceSeparatedChunks() {
		List<Chunk> chunkList = 
				WhitespaceChunkerAnalyzerX.chunkCreateWhitespaceChunkList(pageIo.getRawSVGPage());
		return chunkList;
	}


	void annotatePage() {
		List<SVGG> gList = SVGG.extractGs(SVGUtil.getQuerySVGElements(pageIo.getRawSVGPage(), ".//svg:g[@id]"));
		for (SVGG g : gList) {
			ChunkId chunkId = new ChunkId(g.getId());
			boolean indexed = pdfIndex.getUsedIdSet().contains(chunkId);
			LOG.trace("ID written "+chunkId+" "+indexed);
			if (indexed) {
				Real2Range bbox = g.getBoundingBox();
				Real2[] corners = bbox.getCorners();
				SVGLine line = new SVGLine(corners[0], corners[1]);
				line.setOpacity(0.3);
				line.setWidth(5.0);
				line.setFill("green");
				g.appendChild(line);
			}
		}
	}

	public void add(AbstractContainer container) {
		ensureContainerList();
		abstractContainerList.add(container);
	}

	private void ensureContainerList() {
		if (abstractContainerList == null) {
			abstractContainerList = new ArrayList<AbstractContainer>();
		}
	}

	public String summaryString() {
		StringBuilder sb = new StringBuilder("Page: "+pageIo.getMachinePageNumber()+"\n");
		sb.append("Containers: "+abstractContainerList.size()+"\n");
		for (AbstractContainer container : abstractContainerList) {
			sb.append(container.summaryString()+"\n........................\n");
		}
		return sb.toString();
	}
	
	public HtmlElement createHtmlElement() {
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
		for (AbstractContainer container : abstractContainerList) {
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
		if (abstractContainerList == null) {
			sb.append("NULL containers"); 
		} else {
			sb.append("Containers: "+abstractContainerList.size()+"\n");
			for (AbstractContainer container : abstractContainerList) {
				sb.append(container.toString()+"\n");
			}
		}
		return sb.toString();
	}

	public List<AbstractContainer> getAbstractContainerList() {
		return abstractContainerList;
	}

	public void setPageAnalyzerContainerList(
			List<AbstractContainer> abstractContainerList) {
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
//		String xpath = "//*[local-name()='span' and count(text()) = 1  and count(*) = 0]";
//		replaceNodesWithChildren(div, xpath);
		String xpath = "//*[local-name()='span' and count(text()) = 1  and count(*) = 0]/text()";
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
		removeNodes(div, "//*[local-name()='span' and normalize-space(.)='']");
	}

	private static void removeNodes(HtmlElement div, String xpath) {
		Nodes nodes = div.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	private static void removeSpanSubSupBI(HtmlElement div) {
		replaceParentsWithNodes(div, "//*[local-name()='span']/*[local-name()='sub' or local-name()='sup' or local-name()='b' or local-name()='i']");
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


	public void writeRawSVGPageToFinalDirectory() {
		pageIo.writeRawSVGPageToFinalDirectory();
	}

	public int getMachinePageNumber() {
		return pageIo.getMachinePageNumber();
	}
	
	void summaryContainers() {
		int page = this.getMachinePageNumber();
		SYSOUT.println("************************PAGE***************************"+page+">>>>>> \n");
		SYSOUT.println(this.summaryString());
		SYSOUT.println("************************PAGE***************************"+page+"<<<<<< \n");
	}

	public void outputChunks() {
		List<AbstractContainer> abstractContainerList = this.getAbstractContainerList();
		SYSOUT.println(".......................");
		for (AbstractContainer abstractContainer : abstractContainerList) {
			SVGG chunk = abstractContainer.getSVGChunk();
			String chunkId = chunk.getId();
			File file = pageIo.createChunkFile(chunkId);
			PageIO.outputFile(chunk, file);
			LOG.trace(abstractContainer.getClass() + " " + chunkId);
		}
	}

	public void outputHtmlComponents() {
		List<AbstractContainer> abstractContainerList = this.getAbstractContainerList();
		LOG.debug(".......................");
		Set<ChunkId> chunkIdSet = new HashSet<ChunkId>(); 
		for (AbstractContainer abstractContainer : abstractContainerList) {
			ChunkId chunkId = abstractContainer.getChunkId();
			if (chunkId == null) {
				// probably a bug
				throw new RuntimeException("Null chunkId in "+abstractContainer.getClass()+" "+abstractContainer.getChunkId());
			}
				normalizeDuplicateChunkId(chunkIdSet, abstractContainer, chunkId);
				long time = System.currentTimeMillis();
				LOG.trace(abstractContainer.getClass()+" "+chunkId+" "+abstractContainer.getType());
				HtmlElement element = abstractContainer.createHtmlElement();
				ContainerType type = abstractContainer.getType();
				if (pageIo.isOutputFigures() && ContainerType.FIGURE.equals(type)) {
				} else if (pageIo.isOutputTables() && ContainerType.TABLE.equals(type)) {
				} else if (pageIo.isOutputHtmlChunks() && ContainerType.CHUNK.equals(type)) {
				} else if (pageIo.isOutputHeaders() && ContainerType.HEADER.equals(type)) {
				} else if (pageIo.isOutputFooters() && ContainerType.FOOTER.equals(type)) {
				}
				if (type != null) {
					LOG.debug("creating html chunk: "+type+": "+chunkId.toString()+" "+(System.currentTimeMillis()-time));
					File file = PageIO.createHtmlFile(pageIo.getFinalSVGDocumentDir(), type, chunkId.toString());
					PageIO.outputFile(element, file);
				}
				chunkIdSet.add(chunkId);
		}
		LOG.debug("finished outputHtmlComponents");
	}

	private void normalizeDuplicateChunkId(Set<ChunkId> chunkIdSet,
			AbstractContainer abstractContainer, ChunkId chunkId) {
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
	
//	void outputHtml() {
//		HtmlElement div = this.createHtml();
//		SYSOUT.println("*************************HTML**************************"+div.getId()+">>>>>> \n");
//		pageIo.outputHtmlChunk(div);
//	}
	
	public static PageAnalyzer createAndAnalyze(File rawSvgPageFile) {
		return createAndAnalyze(rawSvgPageFile, (File) null, 1);
	}

	public static PageAnalyzer createAndAnalyze(File rawSvgPageFile, File rawSVGDirectory, Integer pageCounter) {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(rawSvgPageFile);
		pageAnalyzer.setRawSVGDocumentDir(rawSVGDirectory);
		pageAnalyzer.setMachinePageNumber(pageCounter);
		pageAnalyzer.splitChunksAnnotateAndCreatePage();
		LOG.trace(pageAnalyzer.getPageIO().toString());
		return pageAnalyzer;
	}

	public void outputHtmlRunningText() {
		HtmlElement div = this.createRunningHtml();
		div.setId(String.valueOf(pageIo.getHumanPageNumber()));
		SYSOUT.println("*************************HTML**************************"+div.getId()+">>>>>> \n");
		File file = PageIO.createHtmlFile(pageIo.getFinalSVGDocumentDir(), ContainerType.TEXT, div.getId());
		PageIO.outputFile(div, file);
	}

	private HtmlElement createRunningHtml() {
		runningTextHtmlElement = new HtmlDiv();
		for (AbstractContainer abstractContainer : abstractContainerList) {
			LOG.trace("Container: "+abstractContainer.getClass());
			ContainerType type = abstractContainer.getType();
			String content = abstractContainer.getRawValue();
			if (ContainerType.HEADER.equals(abstractContainer.getType())) {
			} else if (ContainerType.FOOTER.equals(abstractContainer.getType())) {
			} else if (ContainerType.TITLE.equals(type)) {
				HtmlH1 h1 = new HtmlH1();
				h1.appendChild(((ScriptContainer)abstractContainer).createHtmlElement().copy());
				runningTextHtmlElement.appendChild(h1);
			} else if (ContainerType.FIGURE.equals(type)) {
				runningTextHtmlElement.appendChild(abstractContainer.getFigureElement().copy());
			} else if (ContainerType.LIST.equals(type)) {
				runningTextHtmlElement.appendChild(abstractContainer.getListElement().copy());
			} else if (ContainerType.TABLE.equals(type)) {
				runningTextHtmlElement.appendChild(abstractContainer.getTableElement().copy());
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

}
